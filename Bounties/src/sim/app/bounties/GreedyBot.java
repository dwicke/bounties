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
import java.util.Comparator;
/**
 * This robot has the ability to jump ship after some timestep t of trying to do
 * a task.
 * @author drew
 */
public class GreedyBot implements Steppable, IRobot {

    Bounties world;
    double expectedTimeOfOthersToComplete[][];// [#agent][#bounties]
    Task myCurTask;
    boolean hasTaskItem = false;
    private Color noTaskColor = Color.black;
    private Color hasTaskColor = Color.red;
    int id;
    double epsilon = .01;
    int count = 0;
    boolean decided = false;
    int maxCount = 20;
    Bondsman bondsman;
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
    public int getCurrentTaskID(){
        if( myCurTask==null)
            return -1;
        return myCurTask.getID();
           
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
        final Bounties af = (Bounties) state;
        bondsman = af.bondsman; // set the bondsman
        world = (Bounties) state;// set the state of the world
     
        
        if((myCurTask==null || !decided || (myCurTask!=null && myCurTask.getIsAvailable() == false)) && !hasTaskItem){
            decideTask();
        }

                
        if (hasTaskItem == false && myCurTask!=null) {
            System.err.println("has task item is false");
            hasTaskItem = gotoTaskPosition(myCurTask);
            if(hasTaskItem){// if i'm here, add myself to list of robots waiting
                myCurTask.addRobot(this);
                if(myCurTask.isEnoughRobots()){ //if everybody is here task is no longer available, and we're coming home
                    myCurTask.setAvailable(false);
                }
            }
            
        } else if (hasTaskItem == true && myCurTask!=null) {
             System.err.println("has task item is true");
            if(myCurTask.isEnoughRobots() && gotoGoalPostion(myCurTask)) {
                // if I reached the goal then I will set my current task to null
                // and notify the bondsman
                world.bondsman.doingTask(id, -1);
                world.bondsman.finishTask(myCurTask);
                hasTaskItem = false;
                decided = false;
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
     public void decideTask(){
        //sort the tasks, 
        //find the rank of task which matches my id
        Bag temp = bondsman.getAvailableTasks();
        temp.sort(new Comparator() 
                           {
                            public int compare(Object o1, Object o2) 
                            {
                                if(((Task)o1).getCurrentReward() < ((Task)o2).getCurrentReward())
                                    return 1;
                                else if(((Task)o1).getCurrentReward() > ((Task)o2).getCurrentReward())
                                    return -1;
                                
                                 return 0;
                            }
                           }    
                    );
        if(id<temp.objs.length)
            myCurTask = (Task)temp.objs[id];
        
        
    }
}
