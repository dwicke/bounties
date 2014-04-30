/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.bounties;

import sim.app.bounties.robot.darwin.agent.Darwin;
import sim.app.bounties.robot.darwin.agent.Darwins;
import sim.app.bounties.robot.darwin.agent.Real;
import sim.app.bounties.robot.darwin.behaviors.Motions;
import sim.app.bounties.robot.darwin.comm.DarwinParser;
import sim.engine.SimState;
import sim.util.Double2D;
import sim.util.Int2D;

/**
 *
 * @author drew
 */
public class DarwinRobot extends Robot{
    private static final long serialVersionUID = 1;

    static Darwins[] available = new Darwins[8];
    {
        available[0] = Darwins.FIFTYTWO;
        available[1] = Darwins.FIFTYTHREE;
    }
    
    private Darwin robot;

    public DarwinRobot() {
        super();
        robot = available[id].build();
    }
    
    
    boolean goingToPose = false;
    public boolean gotoPosition(SimState state, Real position) {
        
        final Bounties af = (Bounties) state;
        
        // tell the robot to go to the pose
        if (goingToPose == false) {
            robot.sendCommand(Motions.getGotoPose(position.getRealTargetLocation().x, position.getRealTargetLocation().y, position.getOrientation()));
            goingToPose = true;
        }
        // move on the screen
        DarwinParser dp = ((DarwinParser)robot.getParser());
        int x = (int) (dp.getPoseX() * 10);
        int y = (int) (dp.getPoseY() * 10);
        //System.err.println("X, y theta " + x + "  " + y + " " + theta + " id = " + myID);
        // translate from C to A
        x += 30;
        y += 20;
        Int2D loc = new Int2D(x,y);
        af.robotgrid.setObjectLocation(this, loc);
        
        // check if there yet.
        return (dp.getReady() == 1);
        
    }

    @Override
    public boolean gotoGoalPosition(SimState state, Real position) {
        return gotoPosition(state, position);
    }

    @Override
    public boolean gotoTaskPosition(SimState state, Real position) {
        return gotoPosition(state, position);
    }
    
}
