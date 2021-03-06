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
    
    public OptimalValuator(MersenneTwisterFast random, int agentID, Int2D home) {
        super(random, 0, agentID);
        this.home = home;
    }


    @Override
    Task pickTask(Task availableTasks[], Task unavailableTasks[], Task curChosenTask, double timeOnTask) {
        Task curAvailTask = pickTask(availableTasks);
        double max = curAvailTask.getCurrentReward() / ((double) (curAvailTask.realLocation).manhattanDistance(home));
        Task curTask = null;
        for (Task availableTask : unavailableTasks) {
            // distance from home to task (since we are at home when we choose to take a task)
            double dist = 1.0 / ((double) (availableTask.realLocation).manhattanDistance(home));
            // need epsilon so will try something.
            double rewardPerDist = (dist + availableTask.getTimeUntilRespawn()) * availableTask.getCurrentReward();
            if (rewardPerDist > max) {
                curTask = availableTask;
                max = rewardPerDist;
            }
        }
        if (curTask != null) {
            // then go after a task that give you the most reward that takes less time than the unavailable task
            // to make up for the time
            double desiredTaskDist = ((double) (curTask.realLocation).manhattanDistance(home));
            Task waitTask = null;
            double rewardMax = -1;
            for (Task availableTask : availableTasks) {
                double dist = ((double) (availableTask.realLocation).manhattanDistance(home));
                double rewardPerDist = 1.0 / dist * availableTask.getCurrentReward() + 1;
                if (dist < desiredTaskDist && rewardPerDist > rewardMax) {
                    waitTask = availableTask;
                    rewardMax = rewardPerDist;
                }
            }
            return waitTask;
        }
        return curAvailTask;
    }
    
    
    
    
    @Override
    Task pickTask(Task[] availableTasks) {
        double max = -1; 
        Task curTask = null;
        for (Task availableTask : availableTasks) {
            // distance from home to task (since we are at home when we choose to take a task)
            double dist = 1.0 / ((double) (availableTask.realLocation).manhattanDistance(home));
            // need epsilon so will try something.
            double rewardPerDist = dist * availableTask.getCurrentReward() + 1;
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
