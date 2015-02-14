/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.bounties.jumpship;

import sim.app.bounties.Bounties;
import sim.app.bounties.agent.IRobot;
import sim.app.bounties.Task;
import sim.engine.SimState;

/**
 *
 * @author drew
 */
public class ResetJumpship implements Jumpship{

    private Jumpship parent;
    
    public ResetJumpship() {
        this.parent = new DefaultJumpship();
    }
    
    public ResetJumpship(Jumpship parent) {
        this.parent = parent;
    }

    @Override
    public boolean jumpship(IRobot robot, Task curTask, Task newTask, SimState state) {
        boolean succ = this.parent.jumpship(robot, curTask, newTask, state);
        if (succ == true) {
            final Bounties af = (Bounties) state;
            af.robotgrid.setObjectLocation(robot,robot.getRobotHome());// teleport home
        }
        return succ;
    }
    
}
