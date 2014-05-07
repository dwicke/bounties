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
public class FullQLearnerRobot extends AbstractRobot implements Steppable {

    private static final long serialVersionUID = 1;
    
    Task prevTask;
    Goal curGoal;
    double reward = 0;// what i will get by completing current task
    double totalReward = 0;
    double epsilon  = 5;
    boolean hadToSwitch = false;
    boolean finishedTask = false;
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
    public FullQLearnerRobot() {
       
    }

    public void step(final SimState state) {
        final Bounties af = (Bounties) state;
        bondsman = af.bondsman; // set the bondsman
        //System.err.println("Robot id=" + id + " curTask = " + ((curTask != null) ? curTask.getID() : "null"));
        
        if(hasTaskItem){// if I have it goto the goal
            
            if(gotoGoalPosition(state, curGoal)) {
                // then we should tell the bondsman that we have done that task
                bondsman.finishTask(curTask);
                hasTaskItem = false;
                finishedTask = true;

                //prevTask = curTask; // set previous task to the one I finished
                //curTask = null; // set to null since not doing anytihng
                System.err.println("Made it to the goal!");
            }
            
        }else if (curTask != null && hadToSwitch == false && finishedTask == false) {
            if (!curTask.getIsAvailable()) {
                //prevTask = curTask;
                //curTask = null;
                hadToSwitch = true;
                reward *= 0; // bad don't go after this 
                
                /*
                Bag tasks = bondsman.getTasks();
                for (int i = 0; i < bondsman.getNumTasks(); i++) {
                    System.err.println("Robot" + id + " Task " + ((Task)tasks.objs[i]).getID() + " "+ myQtable.getQValue(curTask.getID(), i));
                }
                */
            } else if (gotoTaskPosition(state, curTask)) {
                hasTaskItem = true;
                curTask.setAvailable(false);// i am taking it!
            } 
            
        }else{ // so I just finished a task, I have to switch tasks or I have never done a task
            
            if (myQtable == null) {// never had a task
                 
                 
                 if (bondsman.getAvailableTasks().numObjs > 0) {
                    myQtable = new QTable(bondsman.getTotalNumTasks(), bondsman.getTotalNumTasks(), .7, .1, state.random);// focus on current reward
                    // random since initially qtable is all zero
                    curTask = (Task) bondsman.getAvailableTasks().objs[state.random.nextInt(bondsman.getAvailableTasks().numObjs)];
                    System.err.println("curTask in building q-table = " + curTask);
                    curGoal = curTask.getGoal();
                    reward = curTask.getCurrentReward();
                 } 
                 return;
            }
            // so I have to a new task
            if (bondsman.getAvailableTasks().numObjs > 0)
                decideTask();// don't pick a task if none available.
        }
        
    }
    public void decideTask(){
        //consult q table
        
        Bag availTasks = bondsman.getAvailableTasks();
        int bestTaskIndex = 0;

        //System.err.println("Curtask = " + curTask);
        double max;
        // if I had to switch tasks mid way getting to a task because someone else
        // got to it before I did I want to 
        // the
        System.err.println("Robot " + id + " had to switch = " + hadToSwitch + " finished? = " + finishedTask);
        myQtable.printTable();
        
        // if prevTask is null then I can't update the qtable because I haven't
        // changed states yet.  Our initial state is chose and we only perform
        // an action when we change from our initial state to a new one
        if (prevTask == null) {
            // select a new task set prevTask to curTask and set curTask to the new task
            max = (epsilon + myQtable.getQValue(curTask.getID(), ((Task)availTasks.objs[0]).getID())) *
                ((double) ((Task) availTasks.objs[bestTaskIndex]).getCurrentReward() );
            System.err.println("robot " + id + " index " + ((Task)availTasks.objs[0]).getID() + " cur " + max);
            
            for (int i = 1; i < availTasks.numObjs; i++) {
            
                double cur =(epsilon + myQtable.getQValue(curTask.getID(),((Task)availTasks.objs[i]).getID())) *
                    ( ((Task) availTasks.objs[i]).getCurrentReward() );
                //System.err.println("agent id " + id+ " Cur q-val:  " + cur);
                if (cur > max) {
                    bestTaskIndex = i;
                    max = cur;
                }
            }
            
        } else {
            
            if(reward>0)
                reward = 1;
            // since we have performed an action we can now update the q-table
            // so we were doing prevTask and then we chose to do the action that brought us to the new state
            // of curTask and we got the reward "reward" for doing curTask action.
            
            myQtable.update(prevTask.getID(), curTask.getID(), reward, curTask.getID());
            
            reward = 1;
            
            max = (epsilon + myQtable.getQValue(curTask.getID(), ((Task)availTasks.objs[0]).getID())) *
                ((double) ((Task) availTasks.objs[bestTaskIndex]).getCurrentReward() );
            
            System.err.println("robot " + id + " index " + ((Task)availTasks.objs[0]).getID() + " cur " + max);
            for (int i = 1; i < availTasks.numObjs; i++) {
            
                double cur =(epsilon + myQtable.getQValue(curTask.getID(),((Task)availTasks.objs[i]).getID())) *
                    ( ((Task) availTasks.objs[i]).getCurrentReward() );
                System.err.println("robot " + id + " index " + i + " cur " + cur);
                //System.err.println("agent id " + id+ " Cur q-val:  " + cur);
                if (cur > max) {
                    bestTaskIndex = i;
                    max = cur;
                }
            }
            
            
        }
        
            
        // now we want to update our curent task
        prevTask = curTask;
        curTask = (Task) availTasks.objs[bestTaskIndex];
        curGoal = curTask.getGoal();
        
        
        
        finishedTask = false;
        hadToSwitch = false;
        
    }
    
    

    
}
