/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.bounties.old;

import sim.app.bounties.agent.AbstractRobot;
import sim.app.bounties.Bounties;
import sim.app.bounties.Task;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;
import sim.util.Int2D;

/**
 * This robot has the ability to jump ship after some timestep t of trying to do
 * a task.
 * @author drew
 */
public class JumpshipRobot extends AbstractRobot implements Steppable {

    Bounties world;
    double expectedTimeOfOthersToComplete[][];// [#agent][#bounties]
    double epsilon = .01;
    int count = 0;
    int maxCount = 20;
    
    
    public double expectedTimeToComplete(Task b) {
        
        // calculate the manhattan distance from the current location
        // to the task location and then to the goal location
        Int2D myLoc = world.robotgrid.getObjectLocation(this);
        Int2D taskLoc = b.getLocation();
        Int2D goalLoc = b.getGoal().getLocation();
        return myLoc.manhattanDistance(taskLoc);//+  taskLoc.manhattanDistance(goalLoc);
    }
    
    
    public double probabilityDoTask(Task b) {
        // look a the expectedTimeToComplete of the other currently pusuing the
        // task.
        double prob = 1.0;
        double myExpectedTime = expectedTimeToComplete(b);
        // get the robots that are doing the task that I am interested in doing
        Bag otherRobots = world.bondsman.whoseDoingTask(b);
        for (int i = 0; i < otherRobots.numObjs; i++) {
            double othersTime = ((JumpshipRobot) otherRobots.objs[i]).expectedTimeToComplete(b);
            // if my time is greater to do it than others then I don't want to do it so prob = 0
            // if same time then multiply  prob 1/2
            // if my time is less than their's then make prob *=1
            prob *= (othersTime < myExpectedTime) ? 0 : ((othersTime > myExpectedTime) ? 1 : 0.5);
        }
        
        return prob;
    }
    
    public double valueOfBounty(Task b) {
        return b.getCurrentReward();
    }
    
      
    
    @Override
    public void step(SimState state) {
        
        world = (Bounties) state;// set the state of the world
        Bag tasks = world.bondsman.getAvailableTasks();
        
        if (hasTaskItem == true) {
            
            //System.err.println(curTask);
            //if(curTask!=null)
            if(gotoGoalPosition(world, curTask.getGoal())) {
                // if I reached the goal then I will set my current task to null
                // and notify the bondsman
                count = 0;
                System.err.println("FINISHED LOL");
                hasTaskItem = false;
                world.bondsman.doingTask(id, -1);
                world.bondsman.finishTask(curTask,id,state.schedule.getSteps());
                curTask = null;
            }
            
            
        }else if (hasTaskItem == false) {
            if(tasks.numObjs==0) return; // don't bother caculating task stuff if there are no available tasks
            // make a probability of moving toward a random task
            /*if (state.random.nextDouble() < epsilon) {
                
                Task randTask = (Task)tasks.objs[state.random.nextInt(tasks.numObjs)];
                hasTaskItem = gotoTaskPosition(world, randTask);
                if (hasTaskItem) {
                    curTask = randTask;
                    curTask.setAvailable(false);

                }

            } else*/ if (count < maxCount) {
                int bestTaskIndex = 0;
                int bestTaskIndexWithTime = 0;
                double maxR = 0;       
                double maxRWithTime = 0;
                for(int i = 0; i < tasks.numObjs; i++) {
                    Task iTask = (Task) tasks.objs[i];
                    // note that the expectedTimeToComplete is based off of the current
                    // time so don't need to subtract current time
                    // as was the case in the original formulation.
                    System.err.println("Robot " + id + " task id " + iTask.getID() + " curReward " + iTask.getCurrentReward() + " probDoTask = " + probabilityDoTask(iTask) + " expected time to compelted=" + expectedTimeToComplete(iTask));
                    double curRi = ((valueOfBounty(iTask)) * probabilityDoTask(iTask)); /// (expectedTimeToComplete(iTask));
                    double curRiWithTime = curRi / (expectedTimeToComplete(iTask));
                    if(iTask!= curTask && curTask!=null){
                        curRi *=.2;// decrease the rate of things not my current task
                        curRiWithTime *=.2;
                    }
                    if (curRi > maxR) {
                        maxR = curRi;
                        bestTaskIndex = i;
                    }
                   if (curRiWithTime > maxRWithTime) {
                        maxRWithTime = curRiWithTime;
                        bestTaskIndexWithTime = i;
                    }
                }
                if(bestTaskIndex != bestTaskIndexWithTime){
                    System.err.println("robot ID: " + this.id);
                    System.err.println("without time chose: " +  bestTaskIndex);
                    System.err.println("with time : " +  bestTaskIndexWithTime);   
                }
                //notify the bondsman of my task choice if changed.
                Task newTask = (Task) tasks.objs[bestTaskIndex];
                System.err.println(newTask);
                if (newTask !=null && curTask != null && newTask.getID() == curTask.getID()) {
                    count++;
                   
                }else {
                    count = 0;
                     if(id==0){
                         System.err.println("i still changed once");
                     }
                }
                
                curTask = newTask;
                world.bondsman.doingTask(id, curTask.getID());
                hasTaskItem = gotoTaskPosition(world, curTask);
                if (hasTaskItem) {
                    curTask.setAvailable(false);
                }
            }
            else {
                hasTaskItem = gotoTaskPosition(world,curTask);
                if (hasTaskItem) {
                    curTask.setAvailable(false);
                    //curTask = null;
                }
            }
        } 
        
    }

    
}
