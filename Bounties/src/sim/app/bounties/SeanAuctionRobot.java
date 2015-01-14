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
 * 
 * 
 * @author drew
 */
public class SeanAuctionRobot extends AbstractRobot implements Steppable {
    
    
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
    int deadLength = 20000;
    int dieEveryN = 30000;
    int twoDieEveryN = 60000;
    double totalTasksChosen = 0;
    double tasksNotTrusted = 0;
    boolean hasOneUpdate = false;
    boolean hasRandom = false;
    boolean isExclusive = false;
    boolean amDead = false;
    /**
     * Call this before scheduling the robots.
     * @param state the bounties state
     */
    public void init(SimState state) {
        taskList = new Bag();
        bountyState = ((Bounties)state);
        bondsman = bountyState.bondsman;
        timeTable = new QTable(bondsman.getTotalNumTasks(), 1, .1, .1, 1); //only model me
        pTable = new QTable(bondsman.getTotalNumTasks(), 1, .2, .1, 1); //only model me
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
    
    /**
     * 
     * @param updateIt true if should update false if not
     */
    public void setHasOneUpdate(boolean updateIt)
    {
        hasOneUpdate = updateIt;
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
        /*if(curTask!=null)
        if(0==state.random.nextInt(curTask.failureRate) && deadCount ==0){
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
        /*if(state.schedule.getSteps() == 200000){
            System.err.println("real q-table");
            printQTable();
            System.err.println("expectd q-table");
            printExpectedQTable();
            
            System.err.println(tasksNotTrusted/totalTasksChosen);
        }*/
        
        if(this.canDie) {
            if(state.schedule.getSteps()!=0 && state.schedule.getSteps()%twoDieEveryN == 0){
                if(id==0 || id == 1){
                    deadCount = deadLength;
                    bondsman.doingTask(id, -1);// don't do any task
                    jumpHome();
                    curTask = null;
                    decideTaskFailed = true;
                    amDead = true;
                }

            }else if(state.schedule.getSteps()!=0 && state.schedule.getSteps()%dieEveryN == 0){
                if(id==0){
                    deadCount = deadLength;
                    bondsman.doingTask(id, -1);// don't do any task
                    jumpHome();
                    curTask = null;
                    decideTaskFailed = true;
                    amDead = true;
                }

            }
            if(deadCount>0){
                deadCount--;
                return;
            }
        }
        amDead = false;
        
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
                decideTaskFailed = true; // can't choose a task in the same timestep that I find out that I 
                return; // can't start it in the same timestep
            }
             // this is the test for if you become bad for this task
            if(curTask!=null && curTask.badForWho == this.id && hasTraps == true){
                numTimeSteps++;
                if(bountyState.schedule.getSteps() % 10 != 0)
                    return;
            }
            if (gotoTask()){ // if i made it to the task then finish it and learn


                jumpHome();
                iFinished = true;
                curTask.setLastFinished(id, bountyState.schedule.getSteps(), bondsman.whoseDoingTaskByID(curTask));
                bondsman.finishTask(curTask, id, bountyState.schedule.getSteps());
                learn(1.0, curTask.getLastAgentsWorkingOnTask());
                taskList.remove(curTask);
                curTask = null;
                bondsman.doingTask(id, -1);
                numTimeSteps = 0;
                decideTaskFailed = true;
                
            }
        }
        
    }
    public void printQTable(){
        System.err.println("time table");
        timeTable.printTable();
        System.err.println("trust table");
        pTable.printTable();
    }
    public void printExpectedQTable(){
        Bag tasks = bondsman.getTasks();
        for (int i = 0; i < tasks.size(); i++) {
            Task q = (Task)tasks.objs[i];
            double reward = (double)Math.abs(((q.initialLocation.x)-this.getRobotHome().x)) + Math.abs((q.initialLocation.y)-this.getRobotHome().y);
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
        if(epsilonChooseRandomTask > bountyState.random.nextDouble() && getHasRandom()){// && false ){// && false){//&& false){ // ){
            
            pickRandomTask();
            
        }else{
            pickTask();
            //curTask.addRobot(this);
        }
        numTimeSteps = 0;
        if(curTask==null) return true;
        return false;// then there was a task i could choose from
    }
    
    /**
     * Returns whether the task was finished by someone else
     * @return true if finished false otherwise
     */
    public boolean finishedTask() {
        //curTask.subtractRobot(this);
       // if(curTask)
       //if(curTask==null) return false;
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
        if(hasOneUpdate == true)
            pTable.oneUpdate(.001);
        
        //pTable.oneUpdate(.001);
       // timeTable.meanUpdate(.0005);
       // pTable.meanUpdate(.025);   
// System.err.println("Agent id = " + id + " qtable = " + pTable.getQTableAsString());
        //System.err.println("Agent id = " + id + " qtable = " + timeTable.getQTableAsString());
        
    }
    public double[] getEvaluations(){
        Bag availTasks = bondsman.getAvailableTasks();
        
        //System.err.println("Num Avail Tasks == " + availTasks.numObjs);
        //curTask = null;
        //for (int j = 0; j < maxTaskLength - taskList.numObjs; j++) {
        double[] evaluations = new double[availTasks.numObjs];
        for (int i = 0; i < availTasks.numObjs; i++) { // over all tasks
            /*
            if (bondsman.getClearTime(((Task)availTasks.objs[i]).getID()) > 0) {
                continue; // don't bother
            }
            */
            double tval = timeTable.getQValue(((Task)availTasks.objs[i]).getID(), 0);
            double pval = pTable.getQValue(((Task)availTasks.objs[i]).getID(), 0);
            double value = 1.0/tval * pval*((Task)availTasks.objs[i]).getCurrentReward(this);
            //if (bountyState.schedule.getSteps() > 50000){
            if  (bondsman.whoseDoingTask(((Task)availTasks.objs[i])).size() > 0 || amDead == true){
                //if(isExclusive == true)
                    value*=-1;
            }
            evaluations[i] = value;
            //}
           // System.err.println("1/t =  " + (1.0/tval) );
           // System.err.println("agentid = " + id + " tval = " + tval + " pval = " + pval + " value = " + value + " max = " + max);

        }
        
        return evaluations;
        
        
     
    }
    /**
     * Pick the current task to do.
     */
    public void pickTask() {
        try{
        Bag availTasks = (Bag)bondsman.getAvailableTasks().clone();
        
        //System.err.println("Num Avail Tasks == " + availTasks.numObjs);
        curTask = null;
        double max = 0; 
        int robotWinner = -1;
        int robotIndex = -1;
        //for (int j = 0; j < maxTaskLength - taskList.numObjs; j++) {
        IRobot[] bots = bountyState.getRobots().clone();
        
        int countGood = 0;
        for (int i = 0; i < bots.length; i++) {
            if(bots[i] instanceof SeanAuctionRobot) {
                countGood++;
            }else {
                bots[i] = null;
            }
        }
        IRobot [] goodBots = new IRobot[countGood];
        int countAll = 0;
        for (int i = 0; i < bots.length; i++) {
            if(bots[i] instanceof SeanAuctionRobot) {
                goodBots[countAll] = bots[i];
                countAll++;
            }else {
                countGood++;
            }
        }
        bots = goodBots;
        
        
        int loopCount = 0;
        double[][] evaluations = new double[bots.length][];
        for(int a = 0; a<bots.length; a++){
            if(bots[a]!=null && bots[a] instanceof SeanAuctionRobot)
                evaluations[a] = ((SeanAuctionRobot)bots[a]).getEvaluations();
        }
        if(bots.length!=4)
        System.err.println("bots length " + bots.length);
        while(robotWinner != this.id && loopCount<bots.length){
            max  = 0;
            robotWinner = -1;
            Task taskWon = null;
            int taskIndex = -1;
            robotIndex = -1;
          //  System.err.println("inf loop pl0x");
            //System.err.println("START");
            for(int a = 0; a<bots.length; a++){
                for (int i = 0; i < availTasks.numObjs; i++) { // over all tasks
                  if(availTasks.objs[i] != null && bots[a]!=null && bots[a] instanceof SeanAuctionRobot){
                     double temp = evaluations[a][i];
                     //System.err.println("evaluation " + temp);
                     if(temp > max){
                         robotWinner = bots[a].getId();
                         max = temp;
                         robotIndex = a;
                         taskWon = (Task)availTasks.objs[i];
                         taskIndex = i;
                     }
                  }
                }
            }
          if(robotWinner == this.id && max > 0){
             // System.err.println(" got a cur task at index " + taskIndex);
              curTask = taskWon;
              break;
          }else{
              if(robotIndex != -1 ){

                bots[robotIndex] = null;
                availTasks.objs[taskIndex] = null;
              }else{
                 // System.err.println("okay NOW you can shit your pants " + availTasks.numObjs);
              }
          }
          loopCount++;
        }
       // System.err.println("out of inf loop ty");
        
        
        if(curTask==null) {
             //System.err.println("okay NOW you can shit your pants1 " + availTasks.numObjs);
            bondsman.doingTask(id, -1);
            return;
        }
        //System.err.println("Task id = " + curTask.getID());
        totalTasksChosen++;
        if  (bondsman.whoseDoingTask(((Task)curTask)).size() > 0){
            tasksNotTrusted++;
            if(bountyState.schedule.getSteps() >= 200000){
               // System.err.println("expected: " + curTask.initialLocation.toString() + " actual: " + curTask.realLocation.toString());
            }
        }
        updateStatistics(false,curTask.getID(),numTimeSteps);
        bondsman.doingTask(id, curTask.getID());
        //curTask.addRobot(this);
        // always set the lastSeenFinished
        lastSeenFinished = curTask.getLastFinishedTime(); } 
        catch (Exception e){
            e.printStackTrace();
            System.exit(0);
        }
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

    public void setHasRandom(boolean randomExpl) {
        hasRandom = randomExpl;
    }

    public boolean getHasRandom() {
        return hasRandom;
    }

}
