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
public class FullJumpShipQLearnerRobot extends AbstractRobot implements Steppable  {

    private static final long serialVersionUID = 1;
    
    Task prevTask;
    Goal curGoal;
    double reward = 0;// what i will get by completing current task
    double totalReward = 0;
    
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
    

    public Bondsman getBondsman() {
        return bondsman;
    }

    

    public int getGoalID() {
        return (curGoal != null) ? curGoal.id : -1;
    }

    

//TODO: initialize Q-table
//update reward when task is done/failed
//consult the qtable for a decision
    public FullJumpShipQLearnerRobot() {

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
    public void step(final SimState state) {
        final Bounties af = (Bounties) state;
        bondsman = af.bondsman; // set the bondsman

        // init the q-table
        if (myQtable == null) {

            // pick one randomly
            if (bondsman.getAvailableTasks().numObjs > 0) {
                myQtable = new QTable(bondsman.getTotalNumTasks(), bondsman.getTotalNumTasks(), .7, .1, state.random);// focus on current reward
                curTask = (Task) bondsman.getAvailableTasks().objs[state.random.nextInt(bondsman.getAvailableTasks().numObjs)];
                curGoal = curTask.getGoal();
                reward = curTask.getCurrentReward();
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
                qUpdate();
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
                    bondsman.finishTask(curTask);
                    System.err.println("Made it to done!");
                }
                needNewTask = true;

            }

        }/* else if (atTask == false && !curTask.isEnoughRobots()) {
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

        }*/ else if (atTask == true && curTask.isEnoughRobots()) {
            // we are at the task and there are enough robots
            setHasTaskItem(true);
            curTask.setAvailable(false);
            enoughBots = true;
        } else if (atTask == false || (atTask == true && !curTask.isEnoughRobots())) {
            
            System.err.println("Num Robots: " + curTask.isEnoughRobots() + " atTask="+ atTask);
            if (bondsman.getAvailableTasks().numObjs > 0) {
                if (decideTask()) {// we have changed if true
                    prevTask.subtractRobot(this);
                    atTask = false;
                }
            }
            
            if (gotoTaskPosition(state, curTask)) {
                // we made it to the task position
                atTask = true;
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
        System.err.println("avail: " + availTasks);
        System.err.println("qtable: " + myQtable);
        System.err.println(  myQtable.getQValue(((Task) availTasks.objs[bestTaskIndex]).getID(), 0));
        double max = ( myQtable.getQValue(((Task) availTasks.objs[bestTaskIndex]).getID(), 0))
                * (((Task) availTasks.objs[bestTaskIndex]).getCurrentReward());

        for (int i = 1; i < availTasks.numObjs; i++) {

            double cur = ( myQtable.getQValue(((Task) availTasks.objs[i]).getID(), 0))
                    * (((Task) availTasks.objs[i]).getCurrentReward());
            //System.err.println("agent id " + id+ " Cur q-val:  " + cur);
            if (cur > max) {
                bestTaskIndex = i;
                max = cur;
            }
        }

        System.err.println("Robot id " + id + " max Q:" + max + " val " + ((Task) availTasks.objs[bestTaskIndex]).getCurrentReward());

        if (curTask == (Task) availTasks.objs[bestTaskIndex]) {
            return false;
        }

        prevTask = curTask;
        curTask = (Task) availTasks.objs[bestTaskIndex];
        curGoal = curTask.getGoal();
        System.err.println("prev " + prevTask + " curTask " + curTask);
        System.err.println("REWARD: " + reward);
        return true;

    }

    public void qUpdate() {
        if (reward > 0) {//completeness goal....
            reward = 1;
        }

        myQtable.update(prevTask.getID(), 0, reward, curTask.getID());
        reward = 1;//curTask.getCurrentReward();//truReward
    }

}
