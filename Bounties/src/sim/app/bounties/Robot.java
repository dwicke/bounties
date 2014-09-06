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
public class Robot extends AbstractRobot implements Steppable {

    private static final long serialVersionUID = 1;
    
    Task prevTask;
    Goal curGoal;
    double reward = 0;// what i will get by completing current task
    double totalReward = 0;

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
    
    public int getTaskID() {
        return (curTask != null) ? curTask.getID() : -1;
    }
   
//TODO: initialize Q-table
//update reward when task is done/failed
//consult the qtable for a decision
    public Robot() {
       
    }

    public void step(final SimState state) {
        final Bounties af = (Bounties) state;
        bondsman = af.bondsman; // set the bondsman
        
        
        if(hasTaskItem){// if I have it goto the goal
            
            if(gotoGoalPosition(state, curGoal)) {
                // then we should tell the bondsman that we have done that task
                bondsman.finishTask(curTask,id);
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
                    myQtable = new QTable(bondsman.getTotalNumTasks(), 1, .7, .1, state.random);// focus on current reward
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
    public void decideTask(){ // this should be implemented per algorithm
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
        
        //update because we changed task q-table update or whatever
        curTask = (Task) availTasks.objs[bestTaskIndex];
        curGoal = curTask.getGoal();
        if(reward>0)
            reward = 1;
        myQtable.update(prevTask.getID(), 0, reward, curTask.getID());
        reward = 1;
        
        //method for gathering statistics about decisions. Jump ship vs complete, and what task i did.
        
        
    }
    
    

    
}
