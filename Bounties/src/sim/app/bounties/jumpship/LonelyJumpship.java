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
 * Can't jumpship if you are the only one working on the task
 * @author drew
 */
public class LonelyJumpship implements Jumpship {

    private Jumpship parent;
    
    public LonelyJumpship() {
        this.parent = new DefaultJumpship();
    }
    
    public LonelyJumpship(Jumpship parent) {
        this.parent = parent;
    }
    
    @Override
    public boolean jumpship(IRobot robot, Task curTask, Task newTask, SimState state) {
        boolean succ = this.parent.jumpship(robot, curTask, newTask, state);
        if (succ == true) {
            final Bounties af = (Bounties) state;
            if (af.bondsman.whoseDoingTask(curTask).size() > 1) {
                // I'm not the only agent workin on the task so I do
                return succ;
            } else {
                return false;
            }
        }
        return succ;
    }
    
}
