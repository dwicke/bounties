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
 * You will teleport home when:
 * 1. you finish the task
 * 2. someone else finishes the task
 * 
 * At the end of each step you will have at least made progress toward your current task
 * 
 * Before and after the current step you will not have a null curTask
 * unless there are not enough available tasks and then you will each
 * timestep check and call decideTask().
 * 
 * 
 * @author drew
 */
public class TableRobotWithDeath extends AbstractRobot implements Steppable {
    
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
    int deadCount = 0;
    int deadLength = 2000;
    int dieEveryN = 5000;
    int twoDieEveryN = 10000;
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
        pickRandomTask();
        numTimeSteps = 0;
    }
    
    @Override
    public void step(SimState state) {
        // check if someone else finished the task I was working on
            // if finished current task then learn
        // pick task
        // goto task
        if(state.schedule.getSteps()!=0 && state.schedule.getSteps()%twoDieEveryN == 0){
            if(id==0 || id == 1){
                deadCount = deadLength;
            }
            
        }else if(state.schedule.getSteps()!=0 && state.schedule.getSteps()%dieEveryN == 0){
            if(id==0){
                deadCount = deadLength;
            }
            
        }
        if(deadCount>0){
            deadCount--;
            if(bondsman !=null)
               bondsman.doingTask(id,-1);
            return;
        }
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
            } else if (!randomChosen) {
                pickTask(); // There will always be a task to choose from if i am here.
            }

            if (gotoTask()) { // if i made it to the task then finish it and learn
                jumpHome();
                iFinished = true;
                curTask.setLastFinished(id, bountyState.schedule.getSteps(), bondsman.whoseDoingTaskByID(curTask));
                bondsman.finishTask(curTask, id, bountyState.schedule.getSteps());
                learn(1.0 / (double)numTimeSteps, curTask.getLastAgentsWorkingOnTask());
                curTask = null;
                numTimeSteps = 0;
                decideTaskFailed = decideNextTask();
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
        
        if (bountyState.random.nextDouble() < epsilonChooseRandomTask) {
            randomChosen = true;
            pickRandomTask();
        } else {
            randomChosen = false;
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
        
         if(agentsWorking.size() == 1)
             myQtable.update(curTask.getID(), this.id, (double)reward);
        else{
            for(int i = 0; i < agentsWorking.size(); i++){
                int aID = (int) agentsWorking.objs[i];
                if(aID != this.id)
                myQtable.update(curTask.getID(), aID, (double)reward);
            }
            // myQtable.update(curTask.getID(), this.id, (double)reward);
            myQtable.update(curTask.getID(), this.id, (double)reward);
        }
        
    }
    
    /**
     * Pick the current task to do.
     */
    public void pickTask() {
        
        Bag availTasks = bondsman.getAvailableTasks();
        int bestTaskIndex = 0;
     
        double max = -1; 
        Bag peopleWorkingOnTaski = null;
        for (int i = 0; i < availTasks.numObjs; i++) { // over all tasks

            //need to figure out what "state" im in (who is already working on task + me)
            peopleWorkingOnTaski = bondsman.whoseDoingTask((Task)availTasks.objs[i]);
            peopleWorkingOnTaski.add(this);
            
            
            double qValue = minQTableCalculation(peopleWorkingOnTaski,i);
            // need epsilon so will try something.
            double cur = (epsilon + qValue) * (((Task) availTasks.objs[i]).getCurrentReward(this));
           debug("Cur = " + cur + " taskID = " + ((Task) availTasks.objs[i]).getID() + " curent reward = " + (((Task) availTasks.objs[i]).getCurrentReward(this)) + " q-value = " + qValue);
            if (cur > max) {
                whoWasDoingWhenIDecided = peopleWorkingOnTaski;
                bestTaskIndex = i;
                max = cur;
            }
        }
        
        
        Task newTask = ((Task)(availTasks.objs[bestTaskIndex]));
        
        if (curTask == null || curTask.getID() == newTask.getID()) {
            // then i am not jumping ship and i need a new task
            curTask = newTask;
            updateStatistics(false,curTask.getID(),numTimeSteps);
        } else {
            // then I am jumping ship
            jumpship(newTask);
        }
        bondsman.doingTask(id, curTask.getID());
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
    
    double minQTableCalculation(Bag peopleOnTask, int taskID){
        //System.out.println(peopleOnTask.objs);
        //System.out.println(peopleOnTask.objs[0]);
        double max =  myQtable.getQValue(taskID, ((IRobot)peopleOnTask.objs[0]).getId());
        for(int i = 1; i<peopleOnTask.size(); i++){
            double foo = myQtable.getQValue(taskID, ((IRobot)peopleOnTask.objs[i]).getId());
            if(foo<max){
                max = foo;
            }
        }
        return max;
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
