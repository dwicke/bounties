/*
 Copyright 2009 by Sean Luke and George Mason University
 Licensed under the Academic Free License version 3.0
 See the file "LICENSE" for more information
 */
package sim.app.bounties;

import sim.engine.*;
import static sim.engine.SimState.doLoop;
import sim.field.grid.*;
import sim.portrayal.Portrayal2D;
import sim.portrayal.simple.MovablePortrayal2D;
import sim.util.*;

public class Bounties extends SimState {

    private static final long serialVersionUID = 1;

    public static final int GRID_HEIGHT = 40;
    public static final int GRID_WIDTH = 60;

    
    
   
    public Bondsman bondsman;
    public int numRobots = 8;
    public IRobot robots[];// index into this array corresponds to its id
    
    int numTasks = 20;
    int numGoals = 1;    

    public IRobot[] getRobots() {
        return robots;
    }
    // some properties
    public int getNumRobots() {
        return numRobots;
    }

    public void setNumRobots(int val) {
        if (val > 0) {
            numRobots = val;
        }
    }

    public void setNumGoals(int numGoals) {
        if (numGoals > 0)
            this.numGoals = numGoals;
    }

    public void setNumTasks(int numTasks) {
        if (numTasks > 0)
            this.numTasks = numTasks;
    }

    public int getNumGoals() {
        return numGoals;
    }

    public int getNumTasks() {
        return numTasks;
    }
    
    
    
    

    public SparseGrid2D goalsGrid = new SparseGrid2D(GRID_WIDTH, GRID_HEIGHT);
    public SparseGrid2D tasksGrid = new SparseGrid2D(GRID_WIDTH, GRID_HEIGHT);
    public SparseGrid2D robotgrid = new SparseGrid2D(GRID_WIDTH, GRID_HEIGHT);

    
    public Bounties(long seed) {
        super(seed);
    }

    public void start() {
        super.start();  // clear out the schedule
        bondsman = new Bondsman(numGoals, numTasks);
        bondsman.setWorld(this);
        
        // make new grids
        goalsGrid = new SparseGrid2D(GRID_WIDTH, GRID_HEIGHT);
        
        // get the goal locations from the bondsman.
        Bag goalLocs = bondsman.initGoals(new Int2D(tasksGrid.getWidth(), tasksGrid.getHeight()),
                this.random);
        for (int i = 0; i < goalLocs.numObjs; i++) {
            Goal curGoal = ((Goal)(goalLocs.objs[i]));
            
            
            goalsGrid.setObjectLocation(goalLocs.objs[i], curGoal.getLocation());
        }
        
        tasksGrid = new SparseGrid2D(GRID_WIDTH, GRID_HEIGHT);
        Bag tasksLocs = bondsman.initTasks(new Int2D(tasksGrid.getWidth(), tasksGrid.getHeight()),
                this.random);
        
        for (int i = 0; i < tasksLocs.numObjs; i++) {
            Task curTask = ((Task)(tasksLocs.objs[i]));
            tasksGrid.setObjectLocation(tasksLocs.objs[i], curTask.getLocation());
        }
        
        robots = new JumpshipRobot[numRobots];
        robotgrid = new SparseGrid2D(GRID_WIDTH, GRID_HEIGHT);
        for (int x = 0; x < numRobots; x++) {
            //Robot bot = new Robot();
            JumpshipRobot bot = new JumpshipRobot();
            robots[x] = bot;
            bot.setId(x);
            int xloc = random.nextInt(GRID_WIDTH);
            int yloc = random.nextInt(GRID_HEIGHT);
            robotgrid.setObjectLocation(bot, xloc, yloc);
            schedule.scheduleRepeating(Schedule.EPOCH + x, 0, bot, 1);
        }

        // now schedule the bondsman so that it can add more tasks as needed.
        schedule.scheduleRepeating(Schedule.EPOCH + numRobots, 0, bondsman, 1);
    }

    public static void main(String[] args) {
        doLoop(Bounties.class, args);
        System.exit(0);
    }
}
