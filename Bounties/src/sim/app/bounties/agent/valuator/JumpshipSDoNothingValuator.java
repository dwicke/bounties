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
 * Basically I must learn when to do nothing by learning the respawn rate of the tasks
 * that I want to do.
 * @author drew
 */
public class JumpshipSDoNothingValuator extends LearningValuator implements DecisionValuator {
    private static final long serialVersionUID = 1;
    
    private int numTasks;
    
    public JumpshipSDoNothingValuator(MersenneTwisterFast random, double epsilonChooseRandomTask, int agentID, boolean hasOneUp, int numTasks, int numRobots) {
        super(random, epsilonChooseRandomTask, agentID, hasOneUp, numTasks, numRobots);
        this.numTasks = numTasks;
    }
    
    @Override
    Task pickTask(Task availableTasks[], Task unavailableTasks[]) {
        double max = -1;
        Task curTask = null;
        double curTVal = 1000.0;
        for (Task availTask : availableTasks) {
            // over all tasks
            double tval = timeTable.getQValue(availTask.getID(), 0);
           
            double pval = getPValue(availTask);
            double value = 1.0 / tval * pval * (availTask.getCurrentReward() + tval);
            if (value > max) {
                max = value;
                curTVal = tval;
                curTask = availTask;
            }
        }
        // need to relate time to respawn to the time to get the curTask and the value
        // if time to respawn is longer than the time to get to curTask then
        // don't do nothing
        Task curUnTask = null;
        for (Task unavailTask : unavailableTasks) {
            // over all tasks
            double tval = timeTable.getQValue(unavailTask.getID(), 0);
           
            double pval = getPValue(unavailTask);
            double value = 1.0 / tval * pval * (unavailTask.getCurrentReward() + tval);
            if (value > max && curTVal > 5.0) {
                max = value;
                curUnTask = unavailTask;
            }
        }
        if (curUnTask != null) {
            System.err.println("I want to wait for an unavailable task");
            return null;
        }
        return curTask;
    }
    @Override
    double getPValue(Task availTask) {
        return pTable.getQValue(availTask.getID(), 0);
    }
    
    private void learn(Task curTask, double reward, int numTimeSteps) {
        if(reward == 1.0) {
            //updateLearningRate(timeTable.getQValue(curTask.getID(), 0), numTimeSteps); // really bad
            
            timeTable.update(curTask.getID(), 0, numTimeSteps);
            pTable.update(curTask.getID(), 0, reward);
        }else{
            pTable.update(curTask.getID(), 0, reward);
        }
        if (this.hasOneUp)
                pTable.oneUpdate(oneUpdateGamma);
    }
    public void updateLearningRate(double oldT, double newT) {
        double f = Math.abs(newT - oldT) / oldT;
        timeTable.setAlpha(tTableLearningRate*f);
        pTable.setAlpha(pTableLearningRate*f);
        
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
