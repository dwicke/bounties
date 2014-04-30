/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.bounties;

import java.awt.Color;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;
import sim.util.Int2D;

/**
 * This robot has the ability to jump ship after some timestep t of trying to do
 * a task.
 * @author drew
 */
public class JumpshipRobot implements Steppable, IRobot {

    Bounties world;
    double expectedTimeOfOthersToComplete[][];// [#agent][#bounties]
    Task myCurTask;
    boolean hasTaskItem = false;
    private Color noTaskColor = Color.black;
    private Color hasTaskColor = Color.red;
    int id;
    double epsilon = 0.01;
    int count = 0;
    int maxCount = 2;
    
    public void setId(int id) {
        this.id = id;
    }
    
    public double expectedTimeToComplete(Task b) {
        
        // calculate the manhattan distance from the current location
        // to the task location and then to the goal location
        Int2D myLoc = world.robotgrid.getObjectLocation(this);
        Int2D taskLoc = b.getLocation();
        Int2D goalLoc = b.getGoal().getLocation();
        return myLoc.manhattanDistance(taskLoc) + taskLoc.manhattanDistance(goalLoc);
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
            // if same time then set to prob 1/2
            // if my time is less than their's then make prob *=1
            prob *= (othersTime < myExpectedTime) ? 0 : ((othersTime > myExpectedTime) ? 1 : 0.5);
        }
        
        return prob;
    }
    
    public double valueOfBounty(Task b) {
        return b.getCurrentReward();
    }
    
    public boolean gotoTaskPosition(Task b) {
        return gotoPosition(b.getLocation());
    }   
    
    public boolean gotoGoalPostion(Task b) {
        return gotoPosition(b.getGoal().getLocation());
    }
    
    public boolean gotoPosition(Int2D position)
    {
        
        Int2D location = world.robotgrid.getObjectLocation(this);
        int x = location.x;
        int y = location.y;
        
        //System.err.println("X loc " + x + " y loc:" + y + " goal x and y: " + position.toCoordinates());
        // really simple first get inline with the x
        if (position.x != x) {
            int unit = (position.x - x) / Math.abs(position.x - x);
            world.robotgrid.setObjectLocation(this, new Int2D(x + unit, y));
            int newX = x + unit;
            return (position.x == newX) && y == position.y;
        }
        // then in y
        if (position.y != y) {
            int unit = (y - position.y) / Math.abs(y - position.y);
            world.robotgrid.setObjectLocation(this, new Int2D(x, y - unit));
            int newY = y - unit;
            return (position.x == x) && (newY == position.y);
        }
        return true;// we are there already
    }    
    
    @Override
    public void step(SimState state) {
        
        
        world = (Bounties) state;// set the state of the world
        if (hasTaskItem == false) {
            // make a probability of moving toward a random task
            if (state.random.nextDouble() < epsilon) {
                Bag tasks = world.bondsman.getAvailableTasks();
                Task randTask = (Task)tasks.objs[state.random.nextInt(tasks.numObjs)];
                hasTaskItem = gotoTaskPosition(randTask);
                if (hasTaskItem) {
                    myCurTask = randTask;
                    myCurTask.setAvailable(false);

                }

            } else if (count < maxCount) {
                int bestTaskIndex = 0;
                double maxR = 0;

                Bag tasks = world.bondsman.getAvailableTasks();

                for(int i = 0; i < tasks.numObjs; i++) {
                    Task curTask = (Task) tasks.objs[i];
                    // note that the expectedTimeToComplete is based off of the current
                    // time so don't need to subtract current time
                    // as was the case in the original formulation.
                    System.err.println(curTask.getCurrentReward());
                    double curRi = (valueOfBounty(curTask) * probabilityDoTask(curTask)) / (expectedTimeToComplete(curTask));
                    if (curRi > maxR) {
                        maxR = curRi;
                        bestTaskIndex = i;
                    }

                }

                //notify the bondsman of my task choice if changed.
                Task newTask = (Task) tasks.objs[bestTaskIndex];
                if (myCurTask != null && newTask.getID() == myCurTask.getID()) {
                    count++;
                }else {
                    count = 0;
                }
                
                myCurTask = newTask;
                world.bondsman.doingTask(id, myCurTask.getID());
                hasTaskItem = gotoTaskPosition(myCurTask);
                if (hasTaskItem) {
                    myCurTask.setAvailable(false);
                }
            }
            else {
                hasTaskItem = gotoTaskPosition(myCurTask);
                if (hasTaskItem) {
                    myCurTask.setAvailable(false);
                }
            }
        } else if (hasTaskItem == true) {
            
            if(gotoGoalPostion(myCurTask)) {
                // if I reached the goal then I will set my current task to null
                // and notify the bondsman
                count = 0;
                hasTaskItem = false;
                world.bondsman.doingTask(id, -1);
                world.bondsman.finishTask(myCurTask);
            }
            
            
        }
        
    }

    public Color getHasTaskColor() {
        return hasTaskColor;
    }

    public Color getNoTaskColor() {
        return noTaskColor;
    }
    
    public boolean getHasTaskItem() {
        return hasTaskItem;
    }
    
}
