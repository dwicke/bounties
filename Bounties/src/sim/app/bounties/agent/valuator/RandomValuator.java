/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.agent.valuator;

import ec.util.MersenneTwisterFast;
import sim.app.bounties.environment.Task;
import sim.util.Bag;

/**
 *
 * @author drew
 */
public class RandomValuator extends DefaultValuator{
    private static final long serialVersionUID = 1;
    public RandomValuator(MersenneTwisterFast random, int agentID) {
        super(random, 1, agentID);
    }

    @Override
    Task pickTask(Task[] availableTasks) {
        throw new IllegalStateException("a RandomValuator should never call pickTask");
    }

    @Override
    public void learn(Task curTask, double reward, Bag agentsWorking, int numTimeSteps) {
        // don't learn...
    }
    
}
