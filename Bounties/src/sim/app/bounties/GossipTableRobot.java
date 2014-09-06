/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties;

import java.awt.Color;
import sim.app.bounties.robot.darwin.agent.Real;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;
import sim.util.Int2D;

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
    double epsilon = .1;
    boolean atTask = false;
    boolean enoughBots = false;
    boolean needNewTask = false;

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
                myQtable = new QTable(bondsman.getTotalNumTasks(), bondsman.getTotalNumRobots(), .1, .1, state.random);// focus on current reward
                decideTask();
                //curTask = (Task) bondsman.getAvailableTasks().objs[state.random.nextInt(bondsman.getAvailableTasks().numObjs)];
                //curGoal = curTask.getGoal();
                reward = 1;//assume we complete, later this will be divided by time spent.
            }
            return;
        }
        
        if (needNewTask) {
            if (bondsman.getAvailableTasks().numObjs > 0) {
                if (!decideTask()) {
                    prevTask = curTask;
                    System.err.println("Same Task");
                }
                needNewTask = false;
                reward = 1;
                qUpdate(this.id);
                System.err.println("Got a new Task");
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
                    bondsman.finishTask(curTask,id);
                    System.err.println("Made it to done!");
                }
                needNewTask = true;
                

            }

        } /*else if (atTask == false && !curTask.isEnoughRobots()) {
            // we don't have a task with enough people yet

            // anytime up until the point at which we are included in the list
            // of robots that will move the task 
            if (gotoTaskPosition(state, curTask)) {
                // we made it to the task position
                atTask = true;
                curTask.addRobot(this);
                if (curTask.isEnoughRobots()) {
                    enoughBots = true;
                }
            }

        } */else if (atTask == true && curTask.isEnoughRobots()) {
            // we are at the task and there are enough robots
            setHasTaskItem(true);
            curTask.setAvailable(false);
            enoughBots = true;
        } else if (atTask == false || (atTask == true && !curTask.isEnoughRobots())) {
            
            System.err.println("Num Robots: " + curTask.isEnoughRobots() + " atTask="+ atTask);
            if (bondsman.getAvailableTasks().numObjs > 0) {
                if (decideTask()) {// we have changed if true
                    prevTask.subtractRobot(this);
                    
                    // AAAHHHHHH this is realllllly bad
                    // quickest way to get the tasks back to the way they were
                    // so I can update the reward correctly without changing 
                    // decide task too much.
                    System.err.println("my id is: " + id);
                    Task prevTemp = prevTask;
                   // prevTask = prevprevTask;
                    Task curTemp = curTask;
                    //curTask = prevTemp;
                    reward = 0;
                   // try{Thread.sleep(1000);}catch(Exception e){}
                    qUpdate(prevTask.getLastFinished());//okay should do it now
                    timeOnTask = 0;
                    reward = 1;
                    prevTask = prevTemp;
                    curTask = curTemp;
                    atTask = false;
                }
            }
            
            if (gotoTaskPosition(state, curTask)) {
                // we made it to the task position
                atTask = true;
                if(!curTask.isEnoughRobots())
                    curTask.addRobot(this);
                
                if (curTask.isEnoughRobots()) {
                    enoughBots = true;
                }
            }
            
        }

    }

    /**
     * decides task that we will take at a given tick
     *
     * @return only if we changed the task do we return true
     */
    public boolean decideTask() {
        //consult q table
        //myQTable.getBestAction(0);

        Bag availTasks = bondsman.getAvailableTasks();
        int bestTaskIndex = 0;
     
        double max = -1; // i would take the first part of the loop out, but it's too complex now

        for (int i = 0; i < availTasks.numObjs; i++) { // over all tasks

               //need to figure out what "state" im in (who is already working on task + me)
            Bag peopleWorkingOnTaski = bondsman.whoseDoingTask((Task)availTasks.objs[i]);
            if(curTask!=null)
            if(curTask.getID()!=this.id)//add myself if i'm not already on the list
                peopleWorkingOnTaski.add(this.id);//i'm looking to start working on it
            if(peopleWorkingOnTaski.size()<1){
                peopleWorkingOnTaski.add(this.id);
            }
            double qValue = minQTableCalculation(peopleWorkingOnTaski,i);
            
            double cur = (epsilon + qValue)* (((Task) availTasks.objs[i]).getCurrentReward());
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
            updateStatistics(true,200,80);
            return false;
        }
        
                 //System.err.println("NEW BEST TASK: " + k + " bounty " + ((Task) availTasks.objs[bestTaskIndex]).getCurrentReward() );
        prevprevTask = prevTask;
        prevTask = curTask;
        curTask = (Task) availTasks.objs[bestTaskIndex];
        curGoal = curTask.getGoal();
      //  System.err.println("prev " + prevTask + " curTask " + curTask);
      //  System.err.println("REWARD: " + reward);
        updateStatistics(false,200,80); //random crap... should be real
        return true;

    }
    double minQTableCalculation(Bag peopleOnTask, int taskID){
        System.out.println(peopleOnTask.objs);
        System.out.println(peopleOnTask.objs[0]);
        double max =  myQtable.getQValue(taskID, (Integer)peopleOnTask.objs[0]);
        for(int i = 1; i<peopleOnTask.size(); i++){
            double foo = myQtable.getQValue(taskID, (Integer)peopleOnTask.objs[i]);
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
        if(prevTask!=null && curTask!=null)
        myQtable.update(prevTask.getID(), 0, (double)reward, curTask.getID());
        reward = 1;//curTask.getCurrentReward();//truReward
    
    }

}
