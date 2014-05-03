/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties;

import sim.app.bounties.robot.darwin.agent.Real;
import sim.engine.SimState;
import sim.util.Int2D;

/**
 *
 * @author drew
 */
public class VirtualController implements IController {

    IRobot me;
    
    public void setMyRobot(IRobot robot) {
        me = robot;
    }
    
    @Override
    public boolean gotoPosition(final SimState state, Int2D position) { // exeucute task we're on if we have one
        final Bounties af = (Bounties) state;

        Int2D location = af.robotgrid.getObjectLocation(me);
        int x = location.x;
        int y = location.y;

        //System.err.println("X loc " + x + " y loc:" + y + " goal x and y: " + position.toCoordinates());
        // really simple first get inline with the x
        if (position.x != x) {
            int unit = (position.x - x) / Math.abs(position.x - x);
            af.robotgrid.setObjectLocation(me, new Int2D(x + unit, y));
            int newX = x + unit;
            return (position.x == newX) && y == position.y;
        }
        // then in y
        if (position.y != y) {
            int unit = (y - position.y) / Math.abs(y - position.y);
            af.robotgrid.setObjectLocation(me, new Int2D(x, y - unit));
            int newY = y - unit;
            return (position.x == x) && (newY == position.y);
        }
        return true;// we are there already
    }

    @Override
    public boolean gotoGoalPosition(final SimState state, Real position) {
        return gotoPosition(state, position.getLocation());
    }

    @Override
    public boolean gotoTaskPosition(final SimState state, Real position) {
        return gotoPosition(state, position.getLocation());
    }
}
