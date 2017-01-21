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
public class HierarchicalEvaluator extends DefaultValuator implements DecisionValuator{

    public HierarchicalEvaluator(MersenneTwisterFast random, double epsilonChooseRandomTask, int agentID) {
        super(random, epsilonChooseRandomTask, agentID);
    }

    @Override
    Task pickTask(Task availableTasks[], Task unavailableTasks[]) { 
        return pickTask(availableTasks);
    }
    @Override
    Task pickTask(Task[] availableTasks) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void learn(Task curTask, double reward, Bag agentsWorking, int numTimeSteps) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
