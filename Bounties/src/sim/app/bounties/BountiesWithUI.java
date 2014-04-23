/*
  Copyright 2009 by Sean Luke and George Mason University
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

package sim.app.bounties;

import sim.engine.*;
import sim.display.*;
import sim.portrayal.grid.*;
import java.awt.*;
import javax.swing.*;

public class BountiesWithUI extends GUIState
    {
    public Display2D display;
    public JFrame displayFrame;

    
    // the goals grid displays the locations where balls can be dropped off at
    // each type of ball has a particular goal location.  So, ball type 1 has goal location 1
    // ball type 2 has goal location 2.  There can be multiple balls of type 1 but they
    // may be of different tasks but they each must be brought to goal location 1.
    FastValueGridPortrayal2D goalsPortrayal = new FastValueGridPortrayal2D("Goals Grid", true);  // immutable
    
    // the grid that displays the balls.
    FastValueGridPortrayal2D ballGridPortrayal = new FastValueGridPortrayal2D("Ball Grid", false); 
    SparseGridPortrayal2D robotPortrayal = new SparseGridPortrayal2D();
                
    public static void main(String[] args)
        {
        new BountiesWithUI().createController();
        }
    
    public BountiesWithUI() { super(new Bounties(System.currentTimeMillis())); }
    public BountiesWithUI(SimState state) { super(state); }
    
    // allow the user to inspect the model
    public Object getSimulationInspectedObject() { return state; }  // non-volatile

    public static String getName() { return "Bounties"; }
    
    public void setupPortrayals()
        {
        Bounties bounties = (Bounties)state;

        // tell the portrayals what to portray and how to portray them
        
        goalsPortrayal.setField(bounties.goals);
        goalsPortrayal.setMap(new sim.util.gui.SimpleColorMap(
                0,// default so make it black
                1,
                new Color(0,0,0,0),
                new Color(255,0,0,255) ));
        
        ballGridPortrayal.setField(bounties.tasks);
        
        ballGridPortrayal.setMap(new sim.util.gui.SimpleColorMap(
                0,// this is the default value so make it black
                1,
                new Color(0,0,0,0),
                new Color(128,64,64,255) ));
        
        
        robotPortrayal.setField(bounties.robotgrid);
            
        // reschedule the displayer
        display.reset();

        // redraw the display
        display.repaint();
        }
    
    public void start()
        {
        super.start();  // set up everything but replacing the display
        // set up our portrayals
        setupPortrayals();
        }
            
    public void load(SimState state)
        {
        super.load(state);
        // we now have new grids.  Set up the portrayals to reflect that
        setupPortrayals();
        }

    public void init(Controller c)
        {
        super.init(c);
        
        // Make the Display2D.  We'll have it display stuff later.
        display = new Display2D(400,400,this); // at 400x400, we've got 4x4 per array position
        displayFrame = display.createFrame();
        c.registerFrame(displayFrame);   // register the frame so it appears in the "Display" list
        displayFrame.setVisible(true);

        // attach the portrayals from bottom to top
        
        display.attach(goalsPortrayal,"Goals");
        display.attach(ballGridPortrayal,"Tasks");
        display.attach(robotPortrayal,"Agents");
        
        // specify the backdrop color  -- what gets painted behind the displays
        display.setBackdrop(Color.white);
        }
        
    public void quit()
        {
        super.quit();
        
        // disposing the displayFrame automatically calls quit() on the display,
        // so we don't need to do so ourselves here.
        if (displayFrame!=null) displayFrame.dispose();
        displayFrame = null;  // let gc
        display = null;       // let gc
        }
        
    }
    
    
    
    
