/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.agent;

import sim.app.bounties.bondsman.Bondsman;
import sim.app.bounties.Bounties;
import sim.app.bounties.environment.Task;
import sim.app.bounties.agent.valuator.DecisionValuator;
import sim.app.bounties.control.IController;
import sim.app.bounties.jumpship.Jumpship;
import sim.app.bounties.util.Real;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Int2D;

/**
 *
 * @author drew
 */
public class Agent implements IAgent {
    
    
    int historySize = 100;
    int id;
    IController control;
    Task curTask;
    Int2D home;
    public int[] decisionsMade = new int[historySize];
    int rollingHistoryCounter = 0; // pointer to current spot in the list for history purposes
    boolean canDie = false;
    boolean isBad = false;
    public boolean hasTraps = false;
    int numTimeSteps; // the number of timesteps since someone completed a task
    long lastSeenFinished = -1; // the timestep the current task was at when last finished
    Bounties bountyState;
    Bondsman bondsman;
    boolean decideTaskFailed = false;
    int deadCount = 0;
    int deadLength = 20000;
    int dieEveryN = 30000;
    int twoDieEveryN = 60000;
    boolean canJumpship = false;
    Jumpship jumpship;
    DecisionValuator decider;
    double numSteps;
    double numJumpships;
    int tried[];
    int completed[];
    int currentTaskId = -1;
    
    public int[] getTried() {
        return tried;
    }
    public int[] getCompleted() {
        return completed;
    }
    
    @Override
    public void setDecisionValuator(DecisionValuator dv) {
        decider = dv;
    }

    @Override
    public void setJumpship(Jumpship jumpship) {
        this.jumpship = jumpship;
    }
    public void setCanJumpship(boolean canJumpship) {
        this.canJumpship = canJumpship;
    }
    
    public void jumpshipStat(boolean hasJumpship) {
        numSteps++;
        numJumpships = (hasJumpship == true) ? numJumpships + 1 : numJumpships;
    }
    public double getRateJumpship() {
        return numJumpships / numSteps;
    }
    
    /**
     * Call this before scheduling the robots.
     * @param state the bounties state
     */
    @Override
    public void init(SimState state) {
        bountyState = ((Bounties)state);
        bondsman = bountyState.bondsman;
        tried = new int[bountyState.numTasks];
        completed = new int[bountyState.numTasks];
        //decideTask(state);
        decideTaskFailed = true;
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

            decider.setCurrentPos(control.getCurrentLocation(state));

            // get the next task
            curTask = decider.decideNextTask(bondsman.getAvailableTasks());
            
            if (currentTaskId != curTask.getID() && currentTaskId != -1) {
            	tried[currentTaskId]++;
            }
            currentTaskId = curTask.getID();
            decideTaskFailed = (curTask == null);
           
            if(decideTaskFailed == false) {
                // then we picked a task so do the book keeping
                numTimeSteps = 0;
                updateStatistics(false,curTask.getID());
                bondsman.doingTask(id, curTask.getID());
                curTask.setCurrentAgentsOnTask(bondsman.whoseDoingTaskByID(curTask));
                lastSeenFinished = curTask.getLastFinishedTime(); 
                //tried[curTask.getID()]++;
            }
            else {
                
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
            bondsman.finishTask(curTask, id, bountyState.schedule.getSteps(), numTimeSteps);
        }
        
        decider.learn(curTask, reward, curTask.getLastAgentsWorkingOnTask(), numTimeSteps);
        jumpHome();
        curTask = null;
        numTimeSteps = 0;
        decideTaskFailed = true;
        bondsman.doingTask(id, -1);// not doing anytask.
    }
    
    boolean preGotoTask() {
        if(curTask!=null && ((curTask.badForWho == this.id && this.hasTraps == true) ||
                this.isBad == true)) {
            if(bountyState.schedule.getSteps() % 10 != 0 && this.hasTraps)
                return true;
            if(bountyState.schedule.getSteps() % 20 != 0 && this.isBad)
                return true;
        }
        return false;
    }
    
    public void decideJumpship(SimState state) {
        Task oldTask = curTask;
        decider.setPreTask(oldTask);
        decideTask(state);// so decide a task.
        if (oldTask.getID() != curTask.getID()) 
        {
            
            jumpshipStat(true);
            // consider telling the learner that i've jumped ship so that it can
            // pick what table to update.
            decider.setJumped(true);
            // learn who was going after the task when I jumpship
            decider.learn(oldTask, 0.3, bondsman.whoseDoingTaskByID(oldTask), numTimeSteps);
            jumpship.jumpship(this, oldTask, curTask, state);// take the penalty... (reset?)
        } else
        {
            jumpshipStat(false);
        }
    }
    
    
    @Override
    public void step(SimState state) {
        // the preamble before deciding or going toward a task check if I'm in a state that allows me to
        if (beforeDecide(state))
            return;
        
        if(canJumpship && curTask != null) {
            decider.setPreTask(curTask);// this is the previous task now for when i decide a new task
            decideJumpship(state);
        }
        
        if (decideTaskFailed) {

        	decider.setPreTask(null);
            // didn't jumpship so tell the learner...
            decider.setJumped(false);

            decideTask(state);// so try and decide on a task
        } 
        else {
            numTimeSteps++;
            if (finishedTask()) {
                // look into adjusting this from 0.0 to something nicer....
                //completed[curTask.getID()]--;
            	curTask.setCompleteCounter(curTask.getCompleteCounter()+1);
                cleanup(0.0, false); // someone else finished the task so start again
                return; // can't start it in the same timestep that i chose it since doesn't happen if I was the one who completed it
            }
            
            if(preGotoTask())// do stuff before going toward the task
                return;
            
            if (gotoTask()) { // if i made it to the task then finish it and learn
                    completed[curTask.getID()]++;
                    cleanup(1.0, true);
            }
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
    
    /*public boolean gotoGoal() {
        return gotoTaskGoalPosition(bountyState, curTask.);
    }*/
    
    /**
     * Transport robot to home location
     */
    public void jumpHome() {
        if (bountyState.getShouldTeleport()) {
            bountyState.robotgrid.setObjectLocation(this,this.getRobotHome());// teleport home
        } else {
            decider.setHome(control.getCurrentLocation(bountyState));// set current location as home so optimal works.
        }
    }
    
    
    
    @Override
    public void setHasTraps(boolean hasTraps) {
        this.hasTraps = hasTraps;
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
        this.decider.setHome(home);
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
    
    @Override
    public void setIsBad(boolean isBad) {
        this.isBad = isBad;
    }
}
