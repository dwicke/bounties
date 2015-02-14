/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.bounties.jumpship;

import sim.app.bounties.agent.IRobot;
import sim.app.bounties.Task;
import sim.engine.SimState;

/**
 * Does nothing and lets you jump ship.
 * @author drew
 */
public class DefaultJumpship implements Jumpship{

    @Override
    public boolean jumpship(IRobot robot, Task curTask, Task newTask, SimState state) {
        return true;
    }
    
}
