/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.agent.valuator;

import ec.util.MersenneTwisterFast;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import sim.app.bounties.environment.Task;
import sim.app.bounties.util.QTable;
import sim.util.Bag;

/**
 * Jumpship Simple with bounty reward estimation (JSR)
 * with Resource valuation as well.  Basically incorporating the cost of the resources
 * needed for completing the task as well.
 * 
 * I need to add in the time since I've last completed a task
 * 
 * every timestep I call pick task so I can just have a counter there and
 * then everytime I learn and I am the winner I will reset!
 * @author drew
 */
public class JSRResourceValuator extends LearningValuator implements DecisionValuator {
    private static final long serialVersionUID = 1;
    
    private int numTasks;
    
    
    public JSRResourceValuator(MersenneTwisterFast random, double epsilonChooseRandomTask, int agentID, boolean hasOneUp, int numTasks, int numRobots) {
        super(random, epsilonChooseRandomTask, agentID, hasOneUp, numTasks, numRobots);
        this.numTasks = numTasks;
        incrementRateTable = new QTable(numTasks, 1, tTableLearningRate, tTableDiscountBeta, initValue); 
    }
    @Override
    Task pickTask(Task availableTasks[]) {
        double max = -1;
        Task curTask = null;
        //List<Task> availTs = Arrays.asList(availableTasks);
        //Collections.shuffle(availTs);
        
        for (Task availTask : availableTasks) {
            // over all tasks
            double tval = timeTable.getQValue(availTask.getID(), 0);
            double incRate = incrementRateTable.getQValue(availTask.getID(), 0);
            /*
            if (preTask != null && preTask.getID() == availTask.getID()) {
                // then we are deciding whether to jumpship and we are considering continuing the current task
                // so we have to change the tval
                tval -= timeOnTask;
                
            }
            */
            
            double curReward = availTask.getCurrentReward();
            
            
            double pval = getPValue(availTask);
            double value = pval * ((curReward + tval*incRate - availTask.getNumResourcesNeeded()*availTask.getResource().getReservePrice()) / (tval + timeSinceCompletion));//1.0 / tval * pval * (curReward + tval*incRate);
            
            
            if (value > 0 && value > max) {
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
            updateLearningRate(timeTable.getQValue(curTask.getID(), 0), numTimeSteps);
            timeTable.update(curTask.getID(), 0, numTimeSteps);
            pTable.update(curTask.getID(), 0, reward);
            timeSinceCompletion = 0;// reset as I've finished the task.
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
    
    public void updateLearningRate(double oldT, double newT) {
        double f = Math.abs(newT - oldT) / oldT;
        timeTable.setAlpha(tTableLearningRate*f);
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
