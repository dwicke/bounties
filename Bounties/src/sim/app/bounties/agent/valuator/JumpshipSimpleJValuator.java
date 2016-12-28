/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.agent.valuator;

import ec.util.MersenneTwisterFast;
import sim.app.bounties.environment.Task;
import sim.app.bounties.util.QTable;
import sim.util.Bag;

/**
 * has two p tables.  One for probability of success (as normal or maybe try only when not jumpship)
 * and another p table for the probability of success from jumping ship from this task
 * basically if I jumpship from task i to task j and I suceed in task j then
 * i update P2[i] = P2[i]*(1- alpha) + alpha else if i fail in j then update
 * @author drew
 */
public class JumpshipSimpleJValuator extends LearningValuator implements DecisionValuator {
    private static final long serialVersionUID = 1;
    
    private int numTasks;
    QTable pJumpTable;
    
    public JumpshipSimpleJValuator(MersenneTwisterFast random, double epsilonChooseRandomTask, int agentID, boolean hasOneUp, int numTasks, int numRobots) {
        super(random, epsilonChooseRandomTask, agentID, hasOneUp, numTasks, numRobots);
        this.numTasks = numTasks;
        pJumpTable = new QTable(numTasks, numRobots, pTableLearningRate, pTableDiscountBeta, initValue);        
    }
    @Override
    Task pickTask(Task availableTasks[]) {
        double max = -1;
        Task curTask = null;
        for (Task availTask : availableTasks) {
            // over all tasks
            double tval = timeTable.getQValue(availTask.getID(), 0);
           
            double pval = /*getPValue(availTask);*/  (getPValue(availTask) + getPJumpValue());
            double value = 1.0 / (tval + timeSinceCompletion) * pval * (availTask.getCurrentReward() + tval);
            
            if (value > max) {
                max = value;
                curTask = availTask;
            }
        }
        return curTask;
    }
    
    double getPJumpValue() {
        if (preTask == null) {
            System.err.println("pretask is null!");
                    
        }
        return (preTask == null) ? 0.0 : pJumpTable.getQValue(preTask.getID(), 0);//not sure ...
        //return pJumpTable.getQValue(preTask.getID(), 0);
        //return (preTask == null) ? 1.0 : pTable.getQValue(preTask.getID(), 0);
    }
    
    @Override
    double getPValue(Task availTask) {
        return pTable.getQValue(availTask.getID(), 0);
    }
    
    private void learn(Task curTask, double reward, int numTimeSteps) {
        if(reward == 1.0) {
            timeTable.update(curTask.getID(), 0, numTimeSteps);
            pTable.update(curTask.getID(), 0, reward);
            
            // success if I've jumped from another task to this task which i've suceeded at
            if (jumped)
                pJumpTable.update(preTask.getID(), 0, reward);
            
        }else{
            pTable.update(curTask.getID(), 0, reward);
            
            // fail when i jumpship from another task or
            // i've jumpship from a jumpship
            if (jumped)
                pJumpTable.update(preTask.getID(), 0, reward);
            
        }
        if (this.hasOneUp) {
                pTable.oneUpdate(oneUpdateGamma);
                pJumpTable.oneUpdate(oneUpdateGamma);
        }
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
