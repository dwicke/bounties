/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.agent;

import java.awt.Color;
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
    boolean hasTaskItem = false;
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
    long lastSeenFinished; // the timestep the current task was at
    Bounties bountyState;
    Bondsman bondsman;
    boolean decideTaskFailed = false;
    Bag whoWasDoingWhenIDecided = new Bag();
    int deadCount = 0;
    int deadLength = 20000;
    int dieEveryN = 30000;
    int twoDieEveryN = 60000;
    DecisionValuator decider;
    
    public void setDecisionValuator(DecisionValuator dv) {
        decider = dv;
    }
    
        /**
     * Call this before scheduling the robots.
     * @param state the bounties state
     */
    public void init(SimState state) {
        bountyState = ((Bounties)state);
        bondsman = bountyState.bondsman;
        decider.decideNextTask((Task[]) bondsman.getAvailableTasks().toArray());
        numTimeSteps = 0;
        bondsman.doingTask(id, curTask.getID());
    }
    
    
    @Override
    public void step(SimState state) {
        // check if someone else finished the task I was working on
            // if finished current task then learn
        // pick task
        // goto task
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
                return;
            }else {
                decider.setIsDead(false);// i'm no longer dead so can make decisions
            }
        }
        
        
        if (decideTaskFailed) {
            if(!bondsman.getAvailableTasks().isEmpty()) {
                // get the next task
                curTask = decider.decideNextTask((Task[])bondsman.getAvailableTasks().toArray());
                decideTaskFailed = (curTask == null);
                if(decideTaskFailed == false) {
                    // then we picked a task so do the book keeping
                    numTimeSteps = 0;
                    updateStatistics(false,curTask.getID());
                    bondsman.doingTask(id, curTask.getID());
                    lastSeenFinished = curTask.getLastFinishedTime(); 
                }
            }
            //decideTaskFailed = decideNextTask();
        } else {
            numTimeSteps++;
            if (finishedTask()) {
                decider.learn(curTask, 0.0, curTask.getLastAgentsWorkingOnTask(),numTimeSteps); // then learn from it
                jumpHome(); // someone else finished the task so start again
                curTask = null;
                numTimeSteps = 0;
                decideTaskFailed = true;
                return; // can't start it in the same timestep that i chose it since doesn't happen if I was the one who completed it
            }
            if(curTask!=null && curTask.badForWho == this.id && this.hasTraps == true){
                numTimeSteps++;
                if(bountyState.schedule.getSteps() % 10 != 0)
                    return;
            }
            if (gotoTask()) { // if i made it to the task then finish it and learn
                jumpHome();
                curTask.setLastFinished(id, bountyState.schedule.getSteps(), bondsman.whoseDoingTaskByID(curTask));
                bondsman.finishTask(curTask, id, bountyState.schedule.getSteps());
                decider.learn(curTask, 1.0, curTask.getLastAgentsWorkingOnTask(), numTimeSteps);
                curTask = null;
                numTimeSteps = 0;
                decideTaskFailed = true;
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
        if(bountyState == null || curTask == null){
            System.err.println("one was null " + bountyState + "  " + curTask);
        }
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
    
    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    private Color noTaskColor = Color.black;
    private Color hasTaskColor = Color.red;

    public Color getHasTaskColor() {
        return hasTaskColor;
    }

    public Color getNoTaskColor() {
        return noTaskColor;
    }

    /**
     * Is true if the robot is at the location of the task and there are enough
     * robots to perform the task.
     *
     * @return
     */
    public boolean getHasTaskItem() {
        return hasTaskItem;
    }
    // there will be one history array for decisions, if the task is negitive it means you jumped ship, positive means you completed
    public void updateStatistics(boolean jumpedShip, int taskID){
       int multiplier = 1;
       if(jumpedShip)
           multiplier = -1;
      // System.out.println("updated decisionsMade at " + rollingHistoryCounter + " with 10");
       decisionsMade[rollingHistoryCounter] = taskID*multiplier;
      // timeOnTask[rollingHistoryCounter] = totalTimeOnTask;
       rollingHistoryCounter = (rollingHistoryCounter +1 )%historySize;
    }
    public int getLastDecision(){
        return decisionsMade[rollingHistoryCounter];
    }

    public boolean gotoGoalPosition(final SimState state, Real position) {
        
        return control.gotoGoalPosition(state, position);
    }

    public boolean gotoTaskPosition(final SimState state, Real position) {
        
        return control.gotoTaskPosition(state, position);
    }

    public void setHasTaskItem(boolean val) {
        hasTaskItem = val;
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
    
    
    public void debug(String message) {
        //System.err.println(message);
    }

    @Override
    public void setCanDie(boolean canDie) {
       this.canDie = canDie;
    }
}
