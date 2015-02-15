/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.agent.valuator;

import sim.app.bounties.Task;
import sim.util.Bag;

/**
 * This is where the decision are weighed and decided...
 * @author drew
 */
public interface DecisionValuator {
    public Task decideNextTask(Task availableTasks[]);
    public void learn(Task curTask, double reward, Bag agentsWorking, int numTimeSteps);
    public void setIsDead(boolean isDead);
}
