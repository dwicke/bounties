/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.bondsman.valuator;

import java.util.Arrays;
import sim.app.bounties.Bounties;
import sim.app.bounties.environment.Task;

/**
 *
 * @author drew
 */
public class DefaultBondsmanValuator implements BondsmanValuator{

    protected final double incrementAmount[];
    protected final double defaultAmount = 1.0;
    protected final Bounties bounties;
    
    public DefaultBondsmanValuator(Bounties bounties) {
        this.bounties = bounties;
        incrementAmount = new double[this.bounties.numTasks];
        Arrays.fill(incrementAmount, defaultAmount);
    }
    
    @Override
    public void setInitialBounty(Task t) {
        t.setDefaultReward(bounties.defaultReward);
    }

    @Override
    public double getBountyIncrement(Task t) {
        return incrementAmount[t.getID()];
    }

    @Override
    public void updateBounty(Task t, int numTimeSteps) {
        // do nothing.
    }

    
}
