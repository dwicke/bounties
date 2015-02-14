/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.bounties.old;

import sim.app.bounties.AbstractRobot;
import sim.app.bounties.Bondsman;
import sim.app.bounties.Bounties;
import sim.app.bounties.QTable;
import sim.app.bounties.Task;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;

/**
 * 
 * 
 * 
 * @author drew
 */
public class NewSimpleRobotWithQueue extends AbstractRobot implements Steppable {
    
    Bag taskList; // the list of tasks that I will do
    int maxTaskLength = 1;// the number of tasks that I can commit to.
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
    int deadLength = 20;
    int dieEveryN = 30000;
    int twoDieEveryN = 60000;
    double toeStepping, totalTasks = 0;
    /**
     * Call this before scheduling the robots.
     * @param state the bounties state
     */
    public void init(SimState state) {
        taskList = new Bag();
        bountyState = ((Bounties)state);
        bondsman = bountyState.bondsman;
        timeTable = new QTable(bondsman.getTotalNumTasks(), 1, .1, .1, 1); //only model me
        pTable = new QTable(bondsman.getTotalNumTasks(), 1, .1, .1, 1); //only model me
        debug("In init for id: " + id);
        debug("Qtable(row = task_id  col = robot_id) for id: " + id + " \n" + pTable.getQTableAsString());
        debug("Qtable(row = task_id  col = robot_id) for id: " + id + " \n" + timeTable.getQTableAsString());
        pickTask();
        //timeTable.printTable();
        if(curTask==null){
            curTask = null;
            bondsman.doingTask(id, -1);
            jumpHome();
            numTimeSteps = 0;
            decideTaskFailed = true;
        }
    
        numTimeSteps = 0;
    }
    
    @Override
    public void step(SimState state) {
        // check if someone else finished the task I was working on
            // if finished current task then learn
        // pick task
        // goto task
        /*(state.schedule.getSteps()!=0 && state.schedule.getSteps()%twoDieEveryN == 0){
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
        }*//*
        if(0==state.random.nextInt(100) && deadCount ==0){
            deadCount = deadLength;
            curTask = null;
            bondsman.doingTask(id, -1);
            jumpHome();
            numTimeSteps = 0;
            decideTaskFailed = true;
        }
         if(deadCount>0){
            deadCount--;
            return;
        }*/
        if(state.schedule.getSteps() == 200000){
            System.err.println("real q-table");
            printQTable();
            System.err.println("expectd q-table");
            printExpectedQTable();
        }
        if (decideTaskFailed) {
            decideTaskFailed = decideNextTask();
        } else {
            numTimeSteps++;
            if (finishedTask()) {
                learn(0.0, curTask.getLastAgentsWorkingOnTask()); // then learn from it
                jumpHome(); // someone else finished the task so start again
                taskList.remove(curTask);
                curTask = null;
                bondsman.doingTask(id, -1);
                numTimeSteps = 0;
                decideTaskFailed = true;
                return; // can't start it in the same timestep that i chose it since doesn't happen if I was the one who completed it
            }

            if (gotoTask()) { // if i made it to the task then finish it and learn
                
                jumpHome();
                iFinished = true;
                curTask.setLastFinished(id, bountyState.schedule.getSteps(), bondsman.whoseDoingTaskByID(curTask));
                bondsman.finishTask(curTask, id, bountyState.schedule.getSteps());
                learn(1.0, curTask.getLastAgentsWorkingOnTask());
                taskList.remove(curTask);
                curTask = null;
                bondsman.doingTask(id, -1);
                numTimeSteps = 0;
                decideTaskFailed = true;//decideNextTask();
                
            }
        }
        
    }
    public void printQTable(){
        timeTable.printTable();
    }
    public void printExpectedQTable(){
        Bag tasks = bondsman.getTasks();
        for (int i = 0; i < tasks.size(); i++) {
            Task q = (Task)tasks.objs[i];
            double reward = (double)Math.abs(((q.initialLocation.x-2.5)-this.getRobotHome().x) + Math.abs((q.initialLocation.y-2.5)-this.getRobotHome().y));
            StringBuilder build = new StringBuilder();
            build.append("state ").append(i).append(" vals: ");
            build.append(reward).append(" ");
            
            //System.err.println(build.toString());
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
        if(epsilonChooseRandomTask > bountyState.random.nextDouble() && false){// && false ){// && false){//&& false){ // ){
            
            pickRandomTask();
            
        }else{
            pickTask();
            //curTask.addRobot(this);
        }
        if(curTask==null) return true;
        
        // now set the curTask to be the one with the highest priority
        
        
        return false;// then there was a task i could choose from
    }
    
    /**
     * Returns whether the task was finished by someone else
     * @return true if finished false otherwise
     */
    public boolean finishedTask() {
        //curTask.subtractRobot(this);
       // if(curTask)
        
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
            Task q = curTask;
            
            timeTable.update(curTask.getID(), 0, numTimeSteps);
            //System.err.println("p: r=1, id = " + id);
            pTable.update(curTask.getID(), 0, reward);

        }else{
           // System.err.println("t: r=0, id = " + id);
            //timeTable.printTable();
           // System.err.println("p: r=0, id = " + id);
            pTable.update(curTask.getID(), 0, reward);
           
        }
        
        //pTable.oneUpdate(.001);
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
        for (int i = 0; i < availTasks.numObjs; i++) { // over all tasks

            double tval = timeTable.getQValue(((Task)availTasks.objs[i]).getID(), 0);
            double pval = pTable.getQValue(((Task)availTasks.objs[i]).getID(), 0);
            double value = 1.0/tval * pval*((Task)availTasks.objs[i]).getCurrentReward(this);
            //if (bountyState.schedule.getSteps() > 50000){
            if  (bondsman.whoseDoingTask(((Task)availTasks.objs[i])).size() > 0){
               // value*=-1;
            }
            //}
           // System.err.println("1/t =  " + (1.0/tval) );
           // System.err.println("agentid = " + id + " tval = " + tval + " pval = " + pval + " value = " + value + " max = " + max);
            if(value > max)
            {
                max = value;
                curTask = ((Task)availTasks.objs[i]);       
            }
        }
        if(curTask==null) {
            bondsman.doingTask(id, -1);
            return;
        }
        //System.err.println("Task id = " + curTask.getID());
        if(bountyState.schedule.getSteps() > 50000){
            totalTasks++;
            /*
            System.err.println("robot #" + this.id);
            pTable.printTable();
            System.err.println("and i chose task #" + curTask.getID());
            System.err.println("and i had " + availTasks.numObjs+ " to choose from ");
            timeTable.printTable();
            */
        if  (bondsman.whoseDoingTask(curTask).size() > 0){
                toeStepping++;
                System.err.println("Fraction stepped on :" + toeStepping/totalTasks + " toeStepping:" + toeStepping + "  total: " + totalTasks + " step =" + bountyState.schedule.getSteps() + " id =" + id);
        }
        
        }
        
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
        curTask.addRobot(this);
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
