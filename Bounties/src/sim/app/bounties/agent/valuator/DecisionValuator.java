/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.agent.valuator;

import sim.app.bounties.environment.Task;
import sim.util.Bag;
import sim.util.Int2D;

/**
 * This is where the decision are weighed and decided...
 * @author drew
 */
public interface DecisionValuator {
    public Task decideNextTask(Task availableTasks[]);
    public void learn(Task curTask, double reward, Bag agentsWorking, int numTimeSteps);
    public void setIsDead(boolean isDead);
    public void setNumTimeSteps(int numTimeSteps);
    public void setHome(Int2D home);// used only for the optimal valuators.
    public void setCurrentPos(Int2D curLoc);
    public void setPreTask(Task task);
    public void setJumped(boolean jumped);
    public void learnIncrementRate(Task[] tasks);
}
