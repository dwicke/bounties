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
 *
 * @author drew
 */
public class JointTaskQRobot implements Steppable, IRobot {

    private static final long serialVersionUID = 1;
    boolean hasTaskItem = false;
    Task curTask;
    Task prevTask;
    Goal curGoal;
    double reward = 0;// what i will get by completing current task
    double totalReward = 0;
    int id;
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

    public void setHasTaskItem(boolean val) {
        hasTaskItem = val;
    }

    public Bondsman getBondsman() {
        return bondsman;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getGoalID() {
        return (curGoal != null) ? curGoal.id : -1;
    }

    public int getTaskID() {
        return (curTask != null) ? curTask.getID() : -1;
    }

    @Override
    public int getCurrentTaskID() {
        if (curTask == null) {
            return -1;
        }
        return curTask.getID();

    }

//TODO: initialize Q-table
//update reward when task is done/failed
//consult the qtable for a decision
    public JointTaskQRobot() {

    }

    public boolean gotoPosition(final SimState state, Int2D position) { // exeucute task we're on if we have one
        final Bounties af = (Bounties) state;

        Int2D location = af.robotgrid.getObjectLocation(this);
        int x = location.x;
        int y = location.y;

        //System.err.println("X loc " + x + " y loc:" + y + " goal x and y: " + position.toCoordinates());
        // really simple first get inline with the x
        if (position.x != x) {
            int unit = (position.x - x) / Math.abs(position.x - x);
            af.robotgrid.setObjectLocation(this, new Int2D(x + unit, y));
            int newX = x + unit;
            return (position.x == newX) && y == position.y;
        }
        // then in y
        if (position.y != y) {
            int unit = (y - position.y) / Math.abs(y - position.y);
            af.robotgrid.setObjectLocation(this, new Int2D(x, y - unit));
            int newY = y - unit;
            return (position.x == x) && (newY == position.y);
        }
        return true;// we are there already
    }

    public boolean gotoGoalPosition(final SimState state, Real position) {
        return gotoPosition(state, position.getLocation());
    }

    public boolean gotoTaskPosition(final SimState state, Real position) {
        return gotoPosition(state, position.getLocation());
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
                myQtable = new QTable(bondsman.getTotalNumTasks(), 1, .7, .1);// focus on current reward
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

        } else if (atTask == false && !curTask.isEnoughRobots()) {
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

        } else if (atTask == true && curTask.isEnoughRobots()) {
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

        /*
         if (hasTaskItem) {// if I have it goto the goal
         System.err.println("has task item is true, enough?: " + curTask.isEnoughRobots());
            
         if (curTask.isEnoughRobots() && gotoGoalPosition(state, curGoal)) {
         // then we should tell the bondsman that we have done that task
         System.err.println("taking myself off the list");
         System.exit(0);
         bondsman.finishTask(curTask);
         hasTaskItem = false;
         System.err.println(id  + ": set task to false in done going to position");
         curTask.subtractRobot(this);
         prevTask = curTask; // set previous task to the one I finished
         curTask = null; // set to null since not doing anytihng

         } else {
         if(decideTask()){
         hasTaskItem = false;
         System.err.println(id  + ":set task to false in else");
         curTask.subtractRobot(this);
         }
         }
         } else if (curTask != null) {
         System.err.println(id  + ":hasTask is false");
         if (!curTask.getIsAvailable()) {
         prevTask = curTask;
         curTask = null;
         reward *= 0; // bad don't go after this 
         } else if (gotoTaskPosition(state, curTask)) {
         curTask.addRobot(this);
         hasTaskItem = true;
         System.err.println(id  + ":set task to true");
         if (curTask.isEnoughRobots()) {
         curTask.setAvailable(false);// WE am taking it!
         }
         }

         } else {
         if (myQtable == null) {

         // pick one randomly no. do the closest one.
         if (bondsman.getAvailableTasks().numObjs > 0) {
         myQtable = new QTable(bondsman.getTotalNumTasks(), 1, .7, .1);// focus on current reward
         curTask = (Task) bondsman.getAvailableTasks().objs[state.random.nextInt(bondsman.getAvailableTasks().numObjs)];
         curGoal = curTask.getGoal();
         reward = curTask.getCurrentReward();
         }
         return;
         }
         if (bondsman.getAvailableTasks().numObjs > 0) {
         decideTask();// don't pick a task if none available.
         qUpdate();//reward on qtable updated
         }
         }*/
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
        System.err.println(.1 + myQtable.getNormalQValue(((Task) availTasks.objs[bestTaskIndex]).getID(), 0));
        double max = (.1 + myQtable.getNormalQValue(((Task) availTasks.objs[bestTaskIndex]).getID(), 0))
                * (((Task) availTasks.objs[bestTaskIndex]).getCurrentReward());

        for (int i = 1; i < availTasks.numObjs; i++) {

            double cur = (.1 + myQtable.getNormalQValue(((Task) availTasks.objs[i]).getID(), 0))
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
