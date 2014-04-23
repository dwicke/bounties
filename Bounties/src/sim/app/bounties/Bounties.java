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

    public static final int HOME_XMIN = 75;
    public static final int HOME_XMAX = 75;
    public static final int HOME_YMIN = 75;
    public static final int HOME_YMAX = 75;

    public static final int FOOD_XMIN = 25;
    public static final int FOOD_XMAX = 25;
    public static final int FOOD_YMIN = 25;
    public static final int FOOD_YMAX = 25;

    public static final int ROBOT_HOME_IDS = 1;
    public static final int TASK_IDS[] = {1, 2, 3, 4, 5};
    public static final int GOAL_IDS[] = {1, 2, 3, 4, 5};    
        
    public int numRobots = 2;
        
        
    // some properties
    public int getNumAnts() { return numRobots; }
    public void setNumAnts(int val) {if (val > 0) numRobots = val; }

    
    public IntGrid2D sites = new IntGrid2D(GRID_WIDTH, GRID_HEIGHT,0);
    
    public SparseGrid2D robotgrid = new SparseGrid2D(GRID_WIDTH, GRID_HEIGHT);
    
    public Bounties(long seed)
        { 
        super(seed);
        }
        
    public void start()
        {
        super.start();  // clear out the schedule

        // make new grids
        sites = new IntGrid2D(GRID_WIDTH, GRID_HEIGHT,0);
        robotgrid = new SparseGrid2D(GRID_WIDTH, GRID_HEIGHT);

        

        // initialize the grid with the home and food sites
        for( int x = HOME_XMIN ; x <= HOME_XMAX ; x++ )
            for( int y = HOME_YMIN ; y <= HOME_YMAX ; y++ )
                sites.field[x][y] = HOME;
        for( int x = FOOD_XMIN ; x <= FOOD_XMAX ; x++ )
            for( int y = FOOD_YMIN ; y <= FOOD_YMAX ; y++ )
                sites.field[x][y] = FOOD;

        for(int x=0; x < numRobots; x++)
            {
            Robot ant = new Robot(reward);
            robotgrid.setObjectLocation(ant,(HOME_XMAX+HOME_XMIN)/2,(HOME_YMAX+HOME_YMIN)/2);
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
    
    
    
    
    
