/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.agent.valuator;

import ec.util.MersenneTwisterFast;
import sim.app.bounties.environment.Task;
import sim.util.Bag;
import sim.util.Int2D;

/**
 *
 * @author drew
 */
public class OptimalValuator extends DefaultValuator{
    private static final long serialVersionUID = 1;
    Int2D home;
    public OptimalValuator(MersenneTwisterFast random, int agentID, Int2D home) {
        super(random, 0, agentID);
        this.home = home;
    }

    @Override
    public void setHome(Int2D home) {
        this.home = home;
    }
    
    @Override
    Task pickTask(Task[] availableTasks) {
        double max = -1; 
        Task curTask = null;
        for (Task availableTask : availableTasks) {
            // distance from home to task (since we are at home when we choose to take a task)
            double dist = 1.0 / ((double) (availableTask.realLocation).manhattanDistance(home));
            // need epsilon so will try something.
            double rewardPerDist = dist * availableTask.getCurrentReward();
            if (rewardPerDist > max) {
                curTask = availableTask;
                max = rewardPerDist;
            }
        }
        return curTask;
    }

    @Override
    public void learn(Task curTask, double reward, Bag agentsWorking, int numTimeSteps) {
        // don't ... i'm omniscent 
    }
    
}
