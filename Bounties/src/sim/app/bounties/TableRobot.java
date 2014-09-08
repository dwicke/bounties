/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.bounties;

import sim.engine.SimState;
import sim.engine.Steppable;

/**
 * Main things:
 * 
 * You will teleport home when:
 * 1. you finish the task
 * 2. someone else finishes the task
 * 
 * At the end of each step you will have at least made progress toward your current task
 * 
 * Before and after the current step you will not have a null curTask
 * 
 * 
 * 
 * @author drew
 */
public class TableRobot extends AbstractRobot implements Steppable {
    
    QTable myQtable;
    int numTimeSteps; // the number of timesteps since someone completed a task
    long lastSeenFinished; // the timestep the current task was at
    boolean iFinished = false; // true if I finish the cur task
    Bounties bountyState;
    Bondsman bondsman;
        
    /**
     * Call this before scheduling the robots.
     * @param state the bounties state
     */
    public void init(SimState state) {
        bountyState = ((Bounties)state);
        bondsman = bountyState.bondsman;
        myQtable = new QTable(bondsman.getTotalNumTasks(), bondsman.getTotalNumRobots(), .1, .1);// focus on current reward
        debug("In init for id: " + id);
        debug("Qtable(row = task_id  col = robot_id) for id: " + id + " \n" + myQtable.getQTableAsString());
        pickTask();
        numTimeSteps = 0;
    }
    
    @Override
    public void step(SimState state) {
        
        // check if someone else finished the task I was working on
            // if finished current task then learn
        // pick task
        // goto task
        numTimeSteps++;
        if (finishedTask()) {
            learn(0.0); // then learn from it
            jumpHome(); // someone else finished the task so start again
            curTask = null;
            numTimeSteps = 1; 
        }  
        
        pickTask();
        
        if (gotoTask()) { // if i made it to the task then finish it and learn
            jumpHome();
            iFinished = true;
            curTask.setLastFinished(id, bountyState.schedule.getSteps(), bondsman.whoseDoingTaskByID(curTask));
            learn(1.0 / (double)numTimeSteps);
            curTask = null;
            numTimeSteps = 0;
            pickTask();
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
     * Learn given the reward and the current task
     * @param reward the reward 
     */
    public void learn(double reward) { 
        for(int i = 0; i < curTask.getLastAgentsWorkingOnTask().size(); i++){
            int aID = (int) curTask.getLastAgentsWorkingOnTask().objs[i];
            myQtable.update(curTask.getID(), aID, (double)reward);
        }
    }
    
    /**
     * Pick the current task to do.
     */
    public void pickTask() {
        if (curTask == null) {
            
        }
    }
    
    public void pickRandomTask() {
        // pick randomly
        curTask = (Task)bondsman.getAvailableTasks().objs[bountyState.random.nextInt(bondsman.getTotalNumTasks())];
        bondsman.doingTask(id, curTask.getID());
        lastSeenFinished = curTask.getLastFinishedTime();
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

}
