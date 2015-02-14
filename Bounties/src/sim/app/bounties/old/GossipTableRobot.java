/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.old;

import sim.app.bounties.agent.AbstractRobot;
import sim.app.bounties.Bondsman;
import sim.app.bounties.Bounties;
import sim.app.bounties.Goal;
import sim.app.bounties.agent.IRobot;
import sim.app.bounties.QTable;
import sim.app.bounties.Task;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;

/**
 * This robot can change tasks anytime other than when it is taking a task to the
 * goal location.  Add back in the commented out if statement to force it to make it
 * to the task location before being able to jumpship.  This robot can also do 
 * joint tasks.
 * @author drew
 */
public class GossipTableRobot extends AbstractRobot implements Steppable  {

    private static final long serialVersionUID = 1;
    
    Task prevTask;
    Goal curGoal;
    double reward = 0;// what i will get by completing current task
    double totalReward = 0;
    double epsilon = 1/400;
    double epsilonExplorationFactor = 1; // problem is that they can immediatly jumpship
    boolean atTask = false;
    boolean enoughBots = false;
    boolean needNewTask = false;
    boolean iPickedTaskRandomly = false;
    long lastSeenFinish = -1; // the time that we last saw the finish.
    Bag peopleWhoWereWorkingOnTask = new Bag();
    // make a q-table for each task? and the states are values of the bounty
    // we would use the dual q-learning again where we are learning the thresholds
    // for the decision maker and
    // I guess just make it 5 states and one action take it
    QTable myQtable;
    int x;
    int y;

    Bondsman bondsman;
    private Task prevprevTask;
    

    public Bondsman getBondsman() {
        return bondsman;
    }

    

    public int getGoalID() {
        return (curGoal != null) ? curGoal.id : -1;
    }

    

//TODO: initialize Q-table
//update reward when task is done/failed
//consult the qtable for a decision
    public GossipTableRobot() {
    }

 

    /**
     * SO the idea with the joint task robot is that it will be able to do tasks
     * that require multiple robots to perform. So, each task says how many
     * robots it takes to perform it. SO, once all the required robots are
     * waiting at the task location they all can proceed jointly to the goal
     * location. However, if their are not enough robots at the task location
     * the robot can choose to change the task they are working on. This allows
     * the system to avoid deadlock.
     *
     * @param state the current Bounty simstate
     */
    int timeOnTask = 0;
    public void step(final SimState state) {
        final Bounties af = (Bounties) state;
        bondsman = af.bondsman; // set the bondsman
        timeOnTask++;
        // init the q-table
        if (myQtable == null) {

            // pick one randomly
            if (bondsman.getAvailableTasks().numObjs > 0) {
                myQtable = new QTable(bondsman.getTotalNumTasks(), bondsman.getTotalNumRobots(), .1, .1, state.random, 0.0, 0.0);// focus on current reward
                decideTask(state);
                
                reward = 1;//assume we complete, later this will be divided by time spent.
            }
            return;
        }
        
        
        boolean someoneFinishedMyTask = (curTask != null && curTask.getLastFinishedTime() != lastSeenFinish);
        
        if (someoneFinishedMyTask && !iPickedTaskRandomly) {
            if (bondsman.getAvailableTasks().numObjs > 0) {
                
                System.err.println("Last robot finished id = " + curTask.getLastFinishedRobotID());
                
                // so i need to say back up what my prev and cur tasks are
                
                
                
                // then pick a new task without jumping ship
                prevprevTask = prevTask;
                prevTask = curTask;
                curTask = null;
                
                decideTask(state);
                
                qUpdate(curTask.getLastFinishedRobotID());
                timeOnTask = 0;
                needNewTask = false;
                workOnTask(state);
                return;
            } else {
                return;// no new tasks so don't update yet.
            }
        }
        
        else if (needNewTask) {
            if (bondsman.getAvailableTasks().numObjs > 0) {//NEED new task
                
                if (state.random.nextDouble() < epsilonExplorationFactor) { //only randomly pick a task if we NEED a new task (condition above)
                    // randomly pick a task
                    Bag availTasks = bondsman.getAvailableTasks();
                    prevprevTask = prevTask;
                    prevTask = curTask;
                    curTask = (Task)availTasks.objs[state.random.nextInt(availTasks.size())];
                    
                    System.err.println("ID " + id + " picks: " + curTask.getID());
                    
                    needNewTask = false;
                    bondsman.doingTask(id,  curTask.getID()); // so I'm not jumping ship
                    curGoal = curTask.getGoal();
                    lastSeenFinish = curTask.getLastFinishedTime();
                    Bag peopleWorkingOnTaski = bondsman.whoseDoingTask(curTask);
                    peopleWhoWereWorkingOnTask = peopleWorkingOnTaski;
                    reward = 1;
                    qUpdate(this.id);
                    timeOnTask = 0;
                    System.err.println("picked task randomly");
                    workOnTask(state);
                    iPickedTaskRandomly = true;
                    return;
                }
                iPickedTaskRandomly = false;
                prevprevTask = prevTask;
                prevTask = curTask;
                curTask = null;
                
                bondsman.doingTask(id, -1);// not doing anything don't want me mixed in
                decideTask(state);
                needNewTask = false;
                reward = 1;
                qUpdate(this.id);
                timeOnTask = 0;
                System.err.println("Got a new Task");
                workOnTask(state);
                return;
            } else {
                return;// no new tasks so don't update yet.
            }
        }

        if (getHasTaskItem()) {
            // since we have the item and we have enough robots to do it then go
            // to the goal.
            atTask = false;
            if (gotoGoalPosition(state, curGoal)) {
                // if I made it to the goal location
                setHasTaskItem(false);
                curTask.subtractRobot(this);

                if (curTask.getNumRobotsDoingTask() == 0) {
                    //i'm the last one to make it to the goal
                    bondsman.finishTask(curTask,id, state.schedule.getSteps());
                    lastSeenFinish = state.schedule.getSteps(); // i finished it
                    System.err.println("Made it to done!");
                }
                needNewTask = true;
                

            }

        } else if (atTask == true && curTask.isEnoughRobots()) {
            // we are at the task and there are enough robots
            setHasTaskItem(true);
            curTask.setAvailable(false);
            enoughBots = true;
        } else if (atTask == false || (atTask == true && !curTask.isEnoughRobots())) {
            
            //System.err.println("Num Robots: " + curTask.isEnoughRobots() + " atTask="+ atTask);
            if (bondsman.getAvailableTasks().numObjs > 0 ) {
                if(!iPickedTaskRandomly && curTask!=null) // don't allow us to jump ship
                if (decideTask(state)) {// we have changed if true
                    prevTask.subtractRobot(this);
                    
                    // AAAHHHHHH this is realllllly bad
                    // quickest way to get the tasks back to the way they were
                    // so I can update the reward correctly without changing 
                    // decide task too much.
                    
                    Task prevTemp = prevTask;
                    prevTask = prevprevTask;
                    Task curTemp = curTask;
                    curTask = prevTemp;
                    reward = 0;
                   // try{Thread.sleep(1000);}catch(Exception e){}
                    
                    // Don't think we should be updating when we change tasks
                    // we do need to update q when the other robot finishes before
                    // we do.  
                    if(prevTask!=null)
                        qUpdate(prevTask.getLastFinishedRobotID());
                    timeOnTask = 0;
                    reward = 1;
                    prevTask = prevTemp;
                    curTask = curTemp;
                    atTask = false;
                }
            }
            
            
            workOnTask(state);
            
            
            
        }

    }
    
    
    public void workOnTask(SimState state) {
        if (gotoTaskPosition(state, curTask)) {
                // we made it to the task position
                atTask = true;
                if(!curTask.isEnoughRobots())
                    curTask.addRobot(this);
                
                if (curTask.isEnoughRobots()) {
                    enoughBots = true;
                    curTask.setAvailable(false);// make sure no one else can decide to take this task
                }
            }
    }

    /**
     * decides task that we will take at a given tick
     *
     * @return only if we changed the task do we return true
     */
    public boolean decideTask(SimState state) {
        //consult q table
        //myQTable.getBestAction(0);
        
        Bag availTasks = bondsman.getAvailableTasks();
        int bestTaskIndex = 0;
     
        double max = -1; // i would take the first part of the loop out, but it's too complex now
        Bag peopleWorkingOnTaski = null;
        for (int i = 0; i < availTasks.numObjs; i++) { // over all tasks

               //need to figure out what "state" im in (who is already working on task + me)
            peopleWorkingOnTaski = bondsman.whoseDoingTask((Task)availTasks.objs[i]);
            peopleWorkingOnTaski.add(this);
            
            // might be interesting to take the mean rather than the min.
            //  this is the glass half empty agent.  Assumes the worst so it 
            // limits loss... So, it might be less willing to explore if it has a
            // bad experience.
            double qValue = minQTableCalculation(peopleWorkingOnTaski,i);
            // need epsilon so will try something.
            double cur = (epsilon + qValue)* (((Task) availTasks.objs[i]).getCurrentReward(this));
           // System.err.println("agent id " + id+ " Cur q-val:  " + cur);
            if (cur > max) {
                bestTaskIndex = i;
                max = cur;
            }
        }

        //System.err.println("Robot id " + id + " max Q:" + max + " val " + ((Task) availTasks.objs[bestTaskIndex]).getCurrentReward());
        //report if we actually decidedto jumpship or not.
        if(curTask!=null)
            if (curTask.getID() == ((Task)(availTasks.objs[bestTaskIndex])).getID()) {
                // sticking to my task. so don't change the lastSeenFinish
                updateStatistics(true,200,80);
                return false;
            }
        
        //System.err.println("NEW BEST TASK: " + k + " bounty " + ((Task) availTasks.objs[bestTaskIndex]).getCurrentReward() );
        
        
        if (curTask == null) {
            // then this is the first task i am choosing
            bondsman.doingTask(id,  ((Task) availTasks.objs[bestTaskIndex]).getID()); // so I'm not jumping ship
            // only update curTask since it was null that means that I'm choosing a new
            // task because it was finished or this is my first time
            curTask = (Task) availTasks.objs[bestTaskIndex];
            curGoal = curTask.getGoal();
            lastSeenFinish = curTask.getLastFinishedTime();
            peopleWhoWereWorkingOnTask = peopleWorkingOnTaski;
        } else {
            // I'm jumping ship!
            if (bondsman.changeTask(this, curTask, (Task) availTasks.objs[bestTaskIndex], state) == true) {
                // then I successfully jumped ship! so update my info
                prevprevTask = prevTask;
                prevTask = curTask;
                curTask = (Task) availTasks.objs[bestTaskIndex];
                curGoal = curTask.getGoal();
                lastSeenFinish = curTask.getLastFinishedTime();
            } else {
                return false;// well I'm sticking to it even though i wanted to change.
            }
        }
        
        
        
       // bondsman.doingTask(id, curTask.getID());
        
        
        
      //  System.err.println("prev " + prevTask + " curTask " + curTask);
      //  System.err.println("REWARD: " + reward);
        updateStatistics(false,200,80); //random crap... should be real
        return true;

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
    public void qUpdate(int whoWon) {
       
        if (reward > 0) {//completeness goal....
           reward = 1;
        }
        
        if(whoWon!=this.id)
            reward = 0;
        else
            reward = 1/((double)timeOnTask);
        if(whoWon==-1){
            whoWon = this.id;
            reward = 0;
        }
        System.err.println("Who Won: " + whoWon + " my id = " + id) ;
        myQtable.update(prevTask.getID(), whoWon, (double)reward, curTask.getID());
        /*if(prevTask!=null && curTask!=null) {
            myQtable.updateQ(prevTask.getID(), whoWon, (double)reward, curTask.getID());
            if (whoWon != id)
                myQtable.updateQ(prevTask.getID(), id, (double)reward, curTask.getID());
        }*/
        
        for(int i = 0; i< peopleWhoWereWorkingOnTask.size(); i++){
            if(((IRobot)peopleWhoWereWorkingOnTask.objs[i]).getId() != whoWon){
                myQtable.update(prevTask.getID(), ((IRobot)peopleWhoWereWorkingOnTask.objs[i]).getId(), (double)reward, curTask.getID());
            }
        }
        reward = 1;//curTask.getCurrentReward();//truReward
    
    }

}
