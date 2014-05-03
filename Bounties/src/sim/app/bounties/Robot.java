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
public class Robot implements Steppable, IRobot {

    private static final long serialVersionUID = 1;
    boolean hasTaskItem = false;
    Task curTask;
    Task prevTask;
    Goal curGoal;
    double reward = 0;// what i will get by completing current task
    double totalReward = 0;
    int id;
    
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
     public int getCurrentTaskID(){
        if( curTask==null)
            return -1;
        return curTask.getID();
           
    }
  
//TODO: initialize Q-table
//update reward when task is done/failed
//consult the qtable for a decision
    public Robot() {
       
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
    
    public void step(final SimState state) {
        final Bounties af = (Bounties) state;
        bondsman = af.bondsman; // set the bondsman
        
        
        if(hasTaskItem){// if I have it goto the goal
            
            if(gotoGoalPosition(state, curGoal)) {
                // then we should tell the bondsman that we have done that task
                bondsman.finishTask(curTask);
                hasTaskItem = false;
                prevTask = curTask; // set previous task to the one I finished
                curTask = null; // set to null since not doing anytihng
                
            }
            
        }else if (curTask != null) {
            if (!curTask.getIsAvailable()) {
                prevTask = curTask;
                curTask = null;
                reward *= 0; // bad don't go after this 
            } else if (gotoTaskPosition(state, curTask)) {
                hasTaskItem = true;
                curTask.setAvailable(false);// i am taking it!
            } 
            
        }else{
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
            if (bondsman.getAvailableTasks().numObjs > 0)
                decideTask();// don't pick a task if none available.
        }
        
    }
    public void decideTask(){
        //consult q table
        
        Bag availTasks = bondsman.getAvailableTasks();
        int bestTaskIndex = 0;

        double max = (.1+ myQtable.getNormalQValue(((Task)availTasks.objs[bestTaskIndex]).getID(),0))*
                ( ((Task) availTasks.objs[bestTaskIndex]).getCurrentReward() );
                  
        for (int i = 1; i < availTasks.numObjs; i++) {
            
            double cur =(.1+ myQtable.getNormalQValue(((Task)availTasks.objs[i]).getID(),0))*
                ( ((Task) availTasks.objs[i]).getCurrentReward() );
            //System.err.println("agent id " + id+ " Cur q-val:  " + cur);
            if (cur > max) {
                bestTaskIndex = i;
                max = cur;
            }
        }
        
        
        curTask = (Task) availTasks.objs[bestTaskIndex];
        curGoal = curTask.getGoal();
        if(reward>0)
            reward = 1;
        myQtable.update(prevTask.getID(), 0, reward, curTask.getID());
        reward = 1;
        
    }
    
    

    
}
