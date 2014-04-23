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


public class Bounties extends SimState
    {
    private static final long serialVersionUID = 1;

    public static final int GRID_HEIGHT = 100;
    public static final int GRID_WIDTH = 100;


    public static final int TASK_IDS[] = {1}; //
    public static final int GOAL_IDS[] = {1}; // for now only have one type of goal and ball    
        
    public int numRobots = 2;
        
        
    // some properties
    public int getNumAnts() { return numRobots; }
    public void setNumAnts(int val) {if (val > 0) numRobots = val; }

    
    public IntGrid2D goals = new IntGrid2D(GRID_WIDTH, GRID_HEIGHT,0);
    public IntGrid2D tasks = new IntGrid2D(GRID_WIDTH, GRID_HEIGHT,0);
    public SparseGrid2D robotgrid = new SparseGrid2D(GRID_WIDTH, GRID_HEIGHT);
    
    public Bounties(long seed)
        { 
        super(seed);
        }
        
    public void start()
        {
        super.start();  // clear out the schedule

        // make new grids
        goals = new IntGrid2D(GRID_WIDTH, GRID_HEIGHT,0);
        robotgrid = new SparseGrid2D(GRID_WIDTH, GRID_HEIGHT);

        
        for(int x=0; x < numRobots; x++)
            {
            Robot ant = new Robot();
            robotgrid.setObjectLocation(ant,  ,  );
            schedule.scheduleRepeating(Schedule.EPOCH + x, 0, ant, 1);
            }

        // Schedule evaporation to happen after the ants move and update
        schedule.scheduleRepeating(Schedule.EPOCH,1, new Steppable()
            {
            public void step(SimState state) { toFoodGrid.multiply(evaporationConstant); toHomeGrid.multiply(evaporationConstant); }
            }, 1);

        }

    public static void main(String[] args)
        {
        doLoop(Bounties.class, args);
        System.exit(0);
        }    
    }
    
    
    
    
    
