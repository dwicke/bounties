/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.bondsman.valuator;

import sim.app.bounties.Bounties;
import sim.app.bounties.environment.Task;

/**
 * Does not work to solve the mis-coordination problem
 * @author drew
 */
public class RandomInitBountyValuator extends DefaultBondsmanValuator{

    public RandomInitBountyValuator(Bounties bounties) {
        super(bounties);
    }
    
    @Override
    public void setInitialBounty(Task t) {
        t.setDefaultReward(bounties.random.nextInt((int) bounties.defaultReward));
    }
    
    @Override
    public double getBountyIncrement(Task t) {
        
        if (bounties.random.nextInt(100) == 50) {
            t.setCurrentReward(bounties.defaultReward);
        }
        
        return bounties.random.nextInt(5);
        
    }
}
