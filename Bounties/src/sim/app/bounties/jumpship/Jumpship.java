/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.bounties.jumpship;

import sim.app.bounties.agent.IAgent;
import sim.app.bounties.Task;
import sim.engine.SimState;

/**
 *
 * @author drew
 */
public interface Jumpship {
    
    /**
     * 
     * @param robot the robot that is jumping ship
     * @param curTask the task it is currently working on
     * @param newTask the task it wants to work on
     * @param state the state of the world
     * @return whether the robot can successfully jumpship
     */
    public boolean jumpship(IAgent robot, Task curTask, Task newTask, SimState state);
}
