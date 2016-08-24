/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.bondsman.valuator;

import sim.app.bounties.environment.Task;

/**
 *
 * @author drew
 */
public interface BondsmanValuator {
    public void setInitialBounty(Task t);
    public double getBountyIncrement(Task t);
    
    /**
     * This will update the initial bounty of the task and
     * will also update the bounty rate for the task.
     * @param t the task to do the update for
     */
    public void updateBounty(Task t);
}
