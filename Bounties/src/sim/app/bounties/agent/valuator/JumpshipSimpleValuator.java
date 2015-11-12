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
public class JumpshipSimpleValuator extends LearningValuator implements DecisionValuator {
    private static final long serialVersionUID = 1;
    
    private int numTasks;
    
    public JumpshipSimpleValuator(MersenneTwisterFast random, double epsilonChooseRandomTask, int agentID, boolean hasOneUp, int numTasks, int numRobots) {
        super(random, epsilonChooseRandomTask, agentID, hasOneUp, numTasks, numRobots);
        this.numTasks = numTasks;
    }
    @Override
    Task pickTask(Task availableTasks[]) {
        double max = -1;
        Task curTask = null;
        for (Task availTask : availableTasks) {
            // over all tasks
            double tval = timeTable.getQValue(availTask.getID(), 0);
           /* if (tval > numTimeSteps) {
                tval -= numTimeSteps;
            }*/
            double curReward = availTask.getCurrentReward();
            if (this.preTask != null && this.preTask.getID() != availTask.getID()) {
                curReward = availTask.getCurrentReward() - this.preTask.getCurrentReward();
                if (curReward < 0) {
                    curReward = 0;
                }
            }
            double pval = getPValue(availTask);
            double value = 1.0 / tval * pval * (curReward + tval);
            if (value > max) {
                max = value;
                curTask = availTask;
            }
        }
        return curTask;
    }
    @Override
    double getPValue(Task availTask) {
        return pTable.getQValue(availTask.getID(), 0);
    }
    
    private void learn(Task curTask, double reward, int numTimeSteps) {
        if(reward == 1.0) {
            timeTable.update(curTask.getID(), 0, numTimeSteps);
            pTable.update(curTask.getID(), 0, reward);
        }else{
            pTable.update(curTask.getID(), 0, reward);
        }
        if (this.hasOneUp)
                pTable.oneUpdate(oneUpdateGamma);
    }

    @Override
    public void learn(Task curTask, double reward, Bag agentsWorking, int numTimeSteps) {
        double oldT = timeTable.getQValue(curTask.getID(), 0);
        learn(curTask, reward, numTimeSteps);// I don't use agentsWorking.
        updateEpsilon(oldT, timeTable.getQValue(curTask.getID(), 0));
    }
    
     public void updateEpsilon(double oldT, double newT) {
         double delta = (1.0 / (double)this.numTasks);
        epsilonChooseRandomTask = (1.0 - delta)*epsilonChooseRandomTask +
                                    delta*(boltzman(oldT,newT, .85));
    }
     
     public double boltzman(double oldT, double newT, double sigma) {
         return ( 1 - Math.exp(-Math.abs(newT - oldT) / sigma)) /
                 (1 + Math.exp(-Math.abs(newT - oldT) / sigma));
     }

}
