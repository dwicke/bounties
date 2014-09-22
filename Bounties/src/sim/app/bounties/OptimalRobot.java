/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.bounties;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;

/**
 * Main things:
 * 
 * picks task by max money/time
 * 
 * @author drew
 */
public class OptimalRobot extends AbstractRobot implements Steppable {
    
    QTable myQtable;
    int numTimeSteps; // the number of timesteps since someone completed a task
    long lastSeenFinished; // the timestep the current task was at
    boolean iFinished = false; // true if I finish the cur task
    Bounties bountyState;
    Bondsman bondsman;
    double epsilon = .0025;
    boolean randomChosen = false;
    double epsilonChooseRandomTask = .1;
    boolean decideTaskFailed = false;
    Bag whoWasDoingWhenIDecided = new Bag();
    double gamma = .05;
    double deadEpsilon = .0001;
    int deadCount = 0;
    int deadLength = 2000;
    int dieEveryN = 20000;
    int twoDieEveryN = 40000;
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
        decideNextTask();
        numTimeSteps = 0;
    }
    
    @Override
    public void step(SimState state) {
       
       /*if(state.schedule.getSteps()!=0 && state.schedule.getSteps()%twoDieEveryN == 0){
            if(id==0 || id == 1){
                deadCount = deadLength;
                bondsman.doingTask(id, -1);// don't do any task
                jumpHome();
                if(curTask!=null)
                    curTask.setAvailable(true);
                curTask = null;
                decideTaskFailed = true;
            }
            
        }else if(state.schedule.getSteps()!=0 && state.schedule.getSteps()%dieEveryN == 0){
            if(id==0){
                deadCount = deadLength;
                bondsman.doingTask(id, -1);// don't do any task
                jumpHome();
                if(curTask!=null)
                    curTask.setAvailable(true);
                curTask = null;
                decideTaskFailed = true;
            }
            
        }
        if(deadCount>0){
            deadCount--;
            return;
        }*/
            
            if(curTask == null) {
                if(decideNextTask()) {
                    return; // failed to pick a task.
                }
            }
            numTimeSteps++;
            if (gotoTask()) { // if i made it to the task then finish it and learn
                jumpHome();
                iFinished = true;
                curTask.setLastFinished(id, bountyState.schedule.getSteps(), bondsman.whoseDoingTaskByID(curTask));
                bondsman.finishTask(curTask, id, bountyState.schedule.getSteps());
                curTask = null;
                numTimeSteps = 0;
            }
        
        
    }
    
    /**
     * Can be either random or based on q-value
     * @return returns true if task was not picked
     * true if picked
     */
    public boolean decideNextTask() {
        
        if(bondsman.getAvailableTasks().isEmpty()) {
            return true; // wasn't succesful
        }
        randomChosen = false;
        pickTask();
        return false;// then there was a task i could choose from
    }
    
    
   
    
    /**
     * Pick the current task to do.
     */
    public void pickTask() {
        
        Bag availTasks = bondsman.getAvailableTasks();
      
     
        double max = -1; 

        for (int i = 0; i < availTasks.numObjs; i++) { // over all tasks

            //need to figure out what "state" im in (who is already working on task + me)

            
            // distance from home to task (since we are at home when we choose to take a task)
            double dist = 1.0 / ((double) (bountyState.tasksGrid.getObjectLocation((Task)availTasks.objs[i])).manhattanDistance(this.home));
            
            // need epsilon so will try something.
            double rewardPerDist = dist * (((Task) availTasks.objs[i]).getCurrentReward(this));
           
            if (rewardPerDist > max) {
                curTask = ((Task)availTasks.objs[i]);
                max = rewardPerDist;
            }
            
        }
        bondsman.doingTask(this.id, curTask.getID());
        curTask.setAvailable(false);
        
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

}
