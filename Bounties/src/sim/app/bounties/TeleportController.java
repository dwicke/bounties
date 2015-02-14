/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.bounties;

import sim.app.bounties.util.Real;
import sim.engine.SimState;


/**
 *
 * @author drew
 */
public class TeleportController extends VirtualController {

    @Override
    public boolean gotoGoalPosition(SimState state, Real position) {
        final Bounties af = (Bounties) state;
        af.robotgrid.setObjectLocation(me,me.getRobotHome());// teleport home
        return true;// we are at the goal position ("home base") since we teleported.
    }
    
}
