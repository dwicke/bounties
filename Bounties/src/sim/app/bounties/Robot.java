/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties;

import java.awt.Color;
import java.awt.Graphics2D;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.simple.OvalPortrayal2D;
import sim.util.Bag;
import sim.util.Int2D;

/**
 *
 * @author drew
 */
public class Robot extends OvalPortrayal2D implements Steppable {

    private static final long serialVersionUID = 1;
    boolean hasTaskItem = false;
    Task curTask;
    Task prevTask;
    Goal curGoal;
    double reward = 0;// what i will get by completing current task
    double totalReward = 0;
    double threshold = 0;
    int id;
    
    // make a q-table for each task? and the states are values of the bounty
    // we would use the dual q-learning again where we are learning the thresholds
    // for the decision maker and
    
    // I guess just make it 5 states and one action take it
    QTable myQtable;
    int x;
    int y;

    Bondsman bondsman;
    
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
        
        System.err.println("X loc " + x + " y loc:" + y + " goal x and y: " + position.toCoordinates());
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

    public void step(final SimState state) {
        final Bounties af = (Bounties) state;
        bondsman = af.bondsman; // set the bondsman
        
        
        if(hasTaskItem){// if I have it goto the goal
            if(gotoPosition(state, curGoal.getLocation())) {
                // then we should tell the bondsman that we have done that task
                bondsman.finishTask(curTask);
                hasTaskItem = false;
                prevTask = curTask; // set previous task to the one I finished
                curTask = null; // set to null since not doing anytihng
                
            }
        }else if (curTask != null) {
            if (gotoPosition(state, curTask.getLocation())) {
                hasTaskItem = true;
                curTask.setAvailable(false);// i am taking it!
            }
            
        }else{
            if (myQtable == null) {
                 myQtable = new QTable(bondsman.getTotalNumTasks(), 1, .7, .2);// focus on current reward
                 // pick one randomly
                 if (bondsman.getAvailableTasks().numObjs > 0) {
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
        //myQTable.getBestAction(0);
        double max = 0;
        Bag availTasks = bondsman.getAvailableTasks();
        int bestTaskIndex = 0;
        for (int i = 0; i < availTasks.numObjs; i++) {
            
            double cur = myQtable.getBestAction(((Task)availTasks.objs[i]).getID());
            if (cur > max) {
                bestTaskIndex = i;
                max = cur;
            }
        }
        
        System.err.println("Robot id " + id + " max Q:" + max + " val " + max * ( (Task) availTasks.objs[bestTaskIndex]).getCurrentReward());
        // must set the goal and task if above threshold
        if (max * ( (Task) availTasks.objs[bestTaskIndex]).getCurrentReward() >= threshold) {
            // update the q-table now that we are transitioning
            
            curTask = (Task) availTasks.objs[bestTaskIndex];
            curGoal = curTask.getGoal();
            myQtable.update(prevTask.getID(), 0, reward, curTask.getID());
            reward = curTask.getCurrentReward();
            threshold += (threshold < 3) ? max : 0;// maybe?
        }
        
    }
    // a few tweaks by Sean
    private Color noTaskColor = Color.black;
    private Color hasTaskColor = Color.red;

    public final void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
        if (hasTaskItem) {
            graphics.setColor(hasTaskColor);
        } else {
            graphics.setColor(noTaskColor);
        }

        // this code was stolen from OvalPortrayal2D
        int x = (int) (info.draw.x - info.draw.width / 2.0);
        int y = (int) (info.draw.y - info.draw.height / 2.0);
        int width = (int) (info.draw.width);
        int height = (int) (info.draw.height);
        graphics.fillOval(x, y, width, height);

    }
}
