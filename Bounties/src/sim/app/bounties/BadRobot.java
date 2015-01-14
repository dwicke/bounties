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
 *
 * @author drew
 */
public class BadRobot extends AbstractRobot implements Steppable {

    QTable timeTable; // time to do task
    QTable pTable; // probablility that I am successful at a task
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
    int deadCount = 0;
    int deadLength = 20000;
    int dieEveryN = 30000;
    int twoDieEveryN = 60000;
    /**
     * Call this before scheduling the robots.
     * @param state the bounties state
     */
    public void init(SimState state) {
        bountyState = ((Bounties)state);
        bondsman = bountyState.bondsman;
        timeTable = new QTable(bondsman.getTotalNumTasks(), 1, .1, .1, 1); //only model me
        pTable = new QTable(bondsman.getTotalNumTasks(), 1, .2, .1, 1); //only model me
        debug("In init for id: " + id);
        debug("Qtable(row = task_id  col = robot_id) for id: " + id + " \n" + pTable.getQTableAsString());
        debug("Qtable(row = task_id  col = robot_id) for id: " + id + " \n" + timeTable.getQTableAsString());
        pickTask();
        numTimeSteps = 0;
    }
    
    @Override
    public void step(SimState state) {
        // check if someone else finished the task I was working on
            // if finished current task then learn
        // pick task
        // goto task
       /* if(state.schedule.getSteps()!=0 && state.schedule.getSteps()%twoDieEveryN == 0){
            if(id==0 || id == 1){
                deadCount = deadLength;
                bondsman.doingTask(id, -1);// don't do any task
                jumpHome();
                curTask = null;
                decideTaskFailed = true;
            }
            
        }else if(state.schedule.getSteps()!=0 && state.schedule.getSteps()%dieEveryN == 0){
            if(id==0){
                deadCount = deadLength;
                bondsman.doingTask(id, -1);// don't do any task
                jumpHome();
                curTask = null;
                decideTaskFailed = true;
            }
            
        }
        if(deadCount>0){
            deadCount--;
            return;
        }*/
        if (decideTaskFailed) {
            decideTaskFailed = decideNextTask();
        } else {
            numTimeSteps++;
            if (finishedTask()) {
                learn(0.0, curTask.getLastAgentsWorkingOnTask()); // then learn from it
                jumpHome(); // someone else finished the task so start again
                curTask = null;
                numTimeSteps = 0;
                decideTaskFailed = decideNextTask();
                return; // can't start it in the same timestep that i chose it since doesn't happen if I was the one who completed it
            }
            //System.err.println("Stepping " + numTimeSteps + " my id " + id + " curTask = " + curTask);
            // make half as fast
            if(numTimeSteps % 10 == 0) {
              //  System.err.println("Stepping " + numTimeSteps + " my id " + id);
                if (gotoTask()) { // if i made it to the task then finish it and learn
                    jumpHome();
                    iFinished = true;
                    curTask.setLastFinished(id, bountyState.schedule.getSteps(), bondsman.whoseDoingTaskByID(curTask));
                    bondsman.finishTask(curTask, id, bountyState.schedule.getSteps());
                    learn(1.0, curTask.getLastAgentsWorkingOnTask());
                    curTask = null;
                    numTimeSteps = 0;
                    decideTaskFailed = decideNextTask();
                }
            }
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
        if(epsilonChooseRandomTask > bountyState.random.nextDouble()&& false){// && false ){// && false){//&& false){ // ){
            
            pickRandomTask();
        }else{
            pickTask();
        }
        return false;// then there was a task i could choose from
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
    public void learn(double reward, Bag agentsWorking) {
        // so update my p and time tables
        
        if(reward == 1.0) {
            //System.err.println("numSteps = " + numTimeSteps);
           // System.err.println("t: r=1, id = " + id);
            timeTable.update(curTask.getID(), 0, numTimeSteps);
            //System.err.println("p: r=1, id = " + id);
            pTable.update(curTask.getID(), 0, reward);
        }else{
           // System.err.println("t: r=0, id = " + id);
            //timeTable.printTable();
           // System.err.println("p: r=0, id = " + id);
            pTable.update(curTask.getID(), 0, reward);
        }
        pTable.oneUpdate(.001);
        
       // timeTable.meanUpdate(.0005);
       // pTable.meanUpdate(.025);   
// System.err.println("Agent id = " + id + " qtable = " + pTable.getQTableAsString());
        //System.err.println("Agent id = " + id + " qtable = " + timeTable.getQTableAsString());
        
    }
    
    /**
     * Pick the current task to do.
     */
    public void pickTask() {
         
        Bag availTasks = bondsman.getAvailableTasks();
        curTask = null;
        double max = 0;
        
        while(curTask == null) {
            
            int ind = bountyState.random.nextInt(availTasks.numObjs);
            if  (bondsman.whoseDoingTask(((Task)availTasks.objs[ind])).numObjs == 0){
                curTask = (Task)availTasks.objs[ind];
            }
            
           
        }
        if(curTask==null) {
            bondsman.doingTask(id, -1);
            return;
        }
        //System.err.println("Task id = " + curTask.getID());
        
       
        updateStatistics(false,curTask.getID(),numTimeSteps);
        bondsman.doingTask(id, curTask.getID());
        //curTask.addRobot(this);
        // always set the lastSeenFinished
        lastSeenFinished = curTask.getLastFinishedTime();
       
    }
    
    /**
     * do necessary things to jumpship
     * @param newTask the task i am jumping to from curTask
     */
    public void jumpship(Task newTask) {
        
        if (bondsman.changeTask(this, curTask, newTask, bountyState) == true) {
                // then I successfully jumped ship! so learn
              //  learn(0, bondsman.whoseDoingTaskByID(curTask));
                curTask = newTask;
                updateStatistics(true,curTask.getID(),numTimeSteps);
            } else {
                updateStatistics(false,curTask.getID(),numTimeSteps);
            }
    }
    
    public void pickRandomTask() {
        // pick randomly
        
        curTask = (Task)bondsman.getAvailableTasks().objs[bountyState.random.nextInt(bondsman.getAvailableTasks().size())];
        bondsman.doingTask(id, curTask.getID());
        lastSeenFinished = curTask.getLastFinishedTime();
        updateStatistics(false,curTask.getID(),numTimeSteps);
    }
    
    
    
    /**
     * Move toward the curTask
     * @return true if i made it to the task
     */
    public boolean gotoTask() {
        if(bountyState == null || curTask == null){
          //  System.err.println("one was null " + bountyState + "  " + curTask);
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
