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
public class SemiOptimalValuator extends DefaultValuator{
    private static final long serialVersionUID = 1;
    Int2D home;
    public SemiOptimalValuator(MersenneTwisterFast random, double epsilonChooseRandomTask, int agentID, Int2D home) {
        super(random, epsilonChooseRandomTask, agentID);
        this.home = home;
    }

    @Override
    Task pickTask(Task[] availableTasks) {
        double max = -1; 
        Task curTask = null;
        for (int i = 0; i < availableTasks.length; i++) { // over all tasks

            //need to figure out what "state" im in (who is already working on task + me)

            double dist = 0; // it is possible that the task's init pos is my home.
            // distance from home to task (since we are at home when we choose to take a task)
            if (((double) availableTasks[i].getInitialPosition().manhattanDistance(this.home)) != 0)
            {
                dist = 1.0 / ((double) availableTasks[i].getInitialPosition().manhattanDistance(this.home));
            }
            
            // need epsilon so will try something.
            double rewardPerDist = (dist+ 0.0025) * availableTasks[i].getCurrentReward();
           
            if (rewardPerDist > max) {
                curTask = availableTasks[i];
                max = rewardPerDist;
            }
            
        }
        return curTask;
    }

    @Override
    public void learn(Task curTask, double reward, Bag agentsWorking, int numTimeSteps) {
        // not
    }
    
}
