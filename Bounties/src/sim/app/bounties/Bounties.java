/*
 Copyright 2009 by Sean Luke and George Mason University
 Licensed under the Academic Free License version 3.0
 See the file "LICENSE" for more information
 */
package sim.app.bounties;

import sim.engine.*;
import static sim.engine.SimState.doLoop;
import sim.field.grid.*;
import sim.util.*;

public class Bounties extends SimState {

    private static final long serialVersionUID = 1;

    public static final int GRID_HEIGHT = 100;
    public static final int GRID_WIDTH = 100;

    
    
        
    public Bondsman bondsman = new Bondsman();// robots will publish their task to the bondsman. robots can then grab tasks form the bondsman
    public int numRobots = 2;
        

    
    // some properties
    public int getNumRobots() {
        return numRobots;
    }

    public void setNumRobots(int val) {
        if (val > 0) {
            numRobots = val;
        }
    }

    public SparseGrid2D goalsGrid = new SparseGrid2D(GRID_WIDTH, GRID_HEIGHT);
    public SparseGrid2D tasksGrid = new SparseGrid2D(GRID_WIDTH, GRID_HEIGHT);
    public SparseGrid2D robotgrid = new SparseGrid2D(GRID_WIDTH, GRID_HEIGHT);

    
    
    
    
    
    public Bounties(long seed) {
        super(seed);
    }

    public void start() {
        super.start();  // clear out the schedule

        bondsman = new Bondsman();
        
        // make new grids
        goalsGrid = new SparseGrid2D(GRID_WIDTH, GRID_HEIGHT);
        
        // get the goal locations from the bondsman.
        Bag goalLocs = bondsman.initGoals(new Int2D(tasksGrid.getWidth(), tasksGrid.getHeight()),
                this.random);
        for (int i = 0; i < goalLocs.numObjs; i++) {
            tasksGrid.setObjectLocation(goalLocs.objs[i], ((Goal)goalLocs.objs[i]).getLocation());
        }
        
        tasksGrid = new SparseGrid2D(GRID_WIDTH, GRID_HEIGHT);
        Bag tasksLocs = bondsman.initTasks(new Int2D(tasksGrid.getWidth(), tasksGrid.getHeight()),
                this.random);
        
        for (int i = 0; i < tasksLocs.numObjs; i++) {
            tasksGrid.setObjectLocation(tasksLocs.objs[i], ((Task)tasksLocs.objs[i]).getLoc());
        }
        
        
        robotgrid = new SparseGrid2D(GRID_WIDTH, GRID_HEIGHT);
        for (int x = 0; x < numRobots; x++) {
            Robot bot = new Robot();
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
