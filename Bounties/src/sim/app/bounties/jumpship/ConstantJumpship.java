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
 * If you jumpship from a task and you come back to it before it is completed
 * then the bounty that you originally committed to is the amount you get.
 * @author drew
 */
public class ConstantJumpship implements Jumpship {
    
    
    private Jumpship parent;
    
    public ConstantJumpship() {
        this.parent = new DefaultJumpship();
    }
    
    public ConstantJumpship(Jumpship parent) {
        this.parent = parent;
    }

    @Override
    public boolean jumpship(IRobot robot, Task curTask, Task newTask, SimState state) {
        boolean succ = this.parent.jumpship(robot, curTask, newTask, state);
        if (succ == true) {
            
            // tell the curTask what my current reward is so it can keep track of it.
            curTask.setCurrentReward(robot.getRewardCurrentTask());
        }
        return succ;
    }
    
}
