/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.agent;

import sim.app.bounties.bondsman.Bondsman;
import sim.app.bounties.Bounties;
import sim.app.bounties.Task;
import sim.app.bounties.agent.valuator.DecisionValuator;
import sim.app.bounties.control.IController;
import sim.app.bounties.util.Real;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;
import sim.util.Int2D;

/**
 *
 * @author drew
 */
public class Agent implements IAgent, Steppable {
    
    
    int historySize = 100;
    int id;
    IController control;
    Task curTask;
    Int2D home;
    public int[] decisionsMade = new int[historySize];
    int[] timeOnTask = new int[historySize];
    int rollingHistoryCounter = 0; // pointer to current spot in the list for history purposes
    int rewardCurrentTask; // the reward for the current task
    boolean canDie;
    public boolean hasTraps = false;
    int numTimeSteps; // the number of timesteps since someone completed a task
    long lastSeenFinished = -1; // the timestep the current task was at when last finished
    Bounties bountyState;
    Bondsman bondsman;
    boolean decideTaskFailed = false;
    Bag whoWasDoingWhenIDecided = new Bag();
    int deadCount = 0;
    int deadLength = 20000;
    int dieEveryN = 30000;
    int twoDieEveryN = 60000;
    DecisionValuator decider;
    
    @Override
    public void setDecisionValuator(DecisionValuator dv) {
        decider = dv;
    }
    
    /**
     * Call this before scheduling the robots.
     * @param state the bounties state
     */
    @Override
    public void init(SimState state) {
        bountyState = ((Bounties)state);
        bondsman = bountyState.bondsman;
        curTask = decider.decideNextTask(bondsman.getAvailableTasks());
        numTimeSteps = 0;
        bondsman.doingTask(id, curTask.getID());
    }
    /**
     * Determines whether the agent should continue deciding what task to do.
     * @param state the bounty state
     * @return true if should not continue to decide false if should decide
     */
    boolean beforeDecide(SimState state) {
        if(this.canDie) {
            if(state.schedule.getSteps()!=0 && state.schedule.getSteps()%twoDieEveryN == 0){
                if(id==0 || id == 1){
                    deadCount = deadLength;
                    bondsman.doingTask(id, -1);// don't do any task
                    jumpHome();
                    curTask = null;
                    decideTaskFailed = true;
                    decider.setIsDead(true);
                }

            }else if(state.schedule.getSteps()!=0 && state.schedule.getSteps()%dieEveryN == 0){
                if(id==0){
                    deadCount = deadLength;
                    bondsman.doingTask(id, -1);// don't do any task
                    jumpHome();
                    curTask = null;
                    decideTaskFailed = true;
                    decider.setIsDead(true);
                }

            }
            if(deadCount>0){
                deadCount--;
                return true;
            }else {
                decider.setIsDead(false);// i'm no longer dead so can make decisions
            }
        }
        return false;
    }
    
    void decideTask(SimState state) {
        if(bondsman.getAvailableTasks().length > 0) {
            // get the next task
            curTask = decider.decideNextTask(bondsman.getAvailableTasks());
            decideTaskFailed = (curTask == null);
            if(decideTaskFailed == false) {
                // then we picked a task so do the book keeping
                numTimeSteps = 0;
                updateStatistics(false,curTask.getID());
                bondsman.doingTask(id, curTask.getID());
                lastSeenFinished = curTask.getLastFinishedTime(); 
            }
        }
    }
    
    /**
     * Called after either i won (got the bounty) or someone else won
     * @param reward ie 1.0 if I won and 0.0 if i lost (or something else)
     * @param won true if I won false if I lost (someone else finished the task before me.
     */
    void cleanup(double reward, boolean won) {
        if (won) {// only if I won do I get to say it is finished
            curTask.setLastFinished(id, bountyState.schedule.getSteps(), bondsman.whoseDoingTaskByID(curTask));
            bondsman.finishTask(curTask, id, bountyState.schedule.getSteps());
        }
        decider.learn(curTask, reward, curTask.getLastAgentsWorkingOnTask(), numTimeSteps);
        jumpHome();
        curTask = null;
        numTimeSteps = 0;
        decideTaskFailed = true;
    }
    
    boolean preGotoTask() {
        if(curTask!=null && curTask.badForWho == this.id && this.hasTraps == true) {
            if(bountyState.schedule.getSteps() % 10 != 0)
                return true;
        }
        return false;
    }
    
    @Override
    public void step(SimState state) {
        // the preamble before deciding or going toward a task check if I'm in a state that allows me to
        if (beforeDecide(state))
            return;
        
        if (decideTaskFailed) {
            decideTask(state);// so try and decide on a task
        } 
        else {
            numTimeSteps++;
            if (finishedTask()) {
                cleanup(0.0, false); // someone else finished the task so start again
                return; // can't start it in the same timestep that i chose it since doesn't happen if I was the one who completed it
            }
            
            if(preGotoTask())// do stuff before going toward the task
                return;
            
            if (gotoTask()) // if i made it to the task then finish it and learn
                cleanup(1.0, true);
        }
        
    }
    /**
     * Returns whether the task was finished by someone else
     * @return true if finished false otherwise
     */
    public boolean finishedTask() {
        return curTask.getLastFinishedTime() != lastSeenFinished;
    }
    /**
     * Move toward the curTask
     * @return true if i made it to the task
     */
    public boolean gotoTask() {
        return gotoTaskPosition(bountyState, curTask);
    }
    
    /**
     * Transport robot to home location
     */
    public void jumpHome() {
        bountyState.robotgrid.setObjectLocation(this,this.getRobotHome());// teleport home
    }
    
    
    public void setHasTraps(boolean hasTraps) {
        this.hasTraps = hasTraps;
    }
    public int getRewardCurrentTask() {
        return rewardCurrentTask;
    }
    /**
     * Must set this whenever I switch to a task or take a task
     * @param reward the bounty at time of commitment for me
     */
    public void setRewardCurrentTask(int reward) {
        this.rewardCurrentTask = reward;
    }
    
    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }
    
    // there will be one history array for decisions, if the task is negitive it means you jumped ship, positive means you completed
    public void updateStatistics(boolean jumpedShip, int taskID){
       int multiplier = 1;
       if(jumpedShip)
           multiplier = -1;
       decisionsMade[rollingHistoryCounter] = taskID*multiplier;
       rollingHistoryCounter = (rollingHistoryCounter +1 )%historySize;
    }
    public int getLastDecision(){
        return decisionsMade[rollingHistoryCounter];
    }


    public boolean gotoTaskPosition(final SimState state, Real position) {
        return control.gotoTaskPosition(state, position);
    }


    @Override
    public int getCurrentTaskID() {
        if (curTask == null) {
            return -1;
        }
        return curTask.getID();

    }

    @Override
    public void setRobotHome(Int2D home) {
        this.home = home;
    }

    @Override
    public Int2D getRobotHome() {
        return home;
    }

    @Override
    public void setRobotController(IController controller) {
        this.control = controller;
    }
    @Override
    public boolean getIsRealRobot() {
        return false;
    }
    
    
    @Override
    public void setCanDie(boolean canDie) {
       this.canDie = canDie;
    }
}
