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
 * We need to minimize our resource usage per task while maximizing the bounty acquired per task
 * This is the greedy approach.  This has to be an online approach as new tasks may appear.
 * 
 * @author drew
 */
public class SimpleCostJumpship extends LearningValuator implements DecisionValuator {
    private static final long serialVersionUID = 1;

    
    private int numTasks;

    public SimpleCostJumpship(MersenneTwisterFast random, double epsilonChooseRandomTask, int agentID, int numTasks, int numRobots) {
        super(random, epsilonChooseRandomTask, agentID, true, numTasks, numRobots);
        // i don't want all of the t-table starting with the same time estimate
        timeTable = new QTable(numTasks, 1, tTableLearningRate, tTableDiscountBeta, random, 100, 50); 
        pTable = new QTable(numTasks, 1, pTableLearningRate, pTableDiscountBeta, random, .5, 1.5); 
        this.numTasks = numTasks;
    }

    
    
   @Override
    public void learnIncrementRate(Task[] tasks) {
        for (Task task : tasks) {
            //System.err.println("Defaul reward" + task.getDefaultReward() + "Difference = " + (task.getCurrentReward() - task.getLastReward()) + " current: " + task.getCurrentReward() + " last: " + task.getLastReward());
            
            if ((task.getCurrentReward() - task.getLastReward()) != 0) {
                incrementRateTable.update(task.getID(), 0, task.getCurrentReward() - task.getLastReward());
            }
        }
    }
    
    @Override
    public void learn(Task curTask, double reward, Bag agentsWorking, int numTimeSteps) {
        // decay the epsilon (random exploration)
        epsilonChooseRandomTask *= (1.0 - (1.0 / (double)this.numTasks));
        if(reward == 1.0) 
            timeTable.update(curTask.getID(), 0, numTimeSteps);
        
        pTable.update(curTask.getID(), 0, reward);
        pTable.oneUpdate(oneUpdateGamma);
        
    }

    @Override
    Task pickTask(Task availableTasks[], Task unavailableTasks[], Task curChosenTask, double timeOnTask) {
        
        double maxBounty = 0.0; 
        Task chosenTask = null;
        
        for (Task availTask : availableTasks) {
            double confidence = getPValue(availTask);
            
            
            
            // pval*(reward - prospectiveCosts) - (totalOperatingCostsSoFar + prospectiveOperatingCosts)
            // basically we have the confidence of success times the amount we will earn minus the amount we would spend to succeed
            // then we subtract the operating costs basically the cost to travel to the task
            double timeOnCurTask = 0.0;
            if (curChosenTask != null && availTask.getID() == curChosenTask.getID()) {
                timeOnCurTask = timeOnTask;
            }
            double value = confidence * (getPotentialReward(availTask, timeOnCurTask) - getProspectiveCosts(availTask) - getProspectiveOperatingCosts(availTask, timeOnCurTask))
                    - getTotalOperatingCostsSinceLastPayment();

            if (value >= maxBounty) { // I'll do a task and not get anything for it
                maxBounty = value;
                chosenTask = availTask;
            }
        }
        
        if (curChosenTask != null && chosenTask != null && curChosenTask.getID() != chosenTask.getID() && agentID == 0) {
            double val = (getPValue(curChosenTask) * 
                    (getPotentialReward(curChosenTask, timeOnTask) - getProspectiveCosts(curChosenTask) - getProspectiveOperatingCosts(curChosenTask, timeOnTask)) 
                    - getTotalOperatingCostsSinceLastPayment());
            double chosenval = (getPValue(chosenTask) * (getPotentialReward(chosenTask, 0.0) - getProspectiveCosts(chosenTask) - getProspectiveOperatingCosts(chosenTask, 0)) - getTotalOperatingCostsSinceLastPayment());
            
            
        }
        
        if (curChosenTask != null && chosenTask != null && curChosenTask.getID() != chosenTask.getID()) {
            double val = (getPValue(curChosenTask) * 
                    (getPotentialReward(curChosenTask, timeOnTask) - getProspectiveCosts(curChosenTask) - getProspectiveOperatingCosts(curChosenTask, timeOnTask)) 
                    - getTotalOperatingCostsSinceLastPayment());
            double chosenval = (getPValue(chosenTask) * (getPotentialReward(chosenTask, 0.0) - getProspectiveCosts(chosenTask) - getProspectiveOperatingCosts(chosenTask, 0)) - getTotalOperatingCostsSinceLastPayment());
          if (Math.abs(val - chosenval) < 15.0) {
              chosenTask = curChosenTask;
          } else {
            System.err.println("I'm agent = " + agentID + " and i'm jumping ship");
            System.err.println("I'm jumping ship from " + curChosenTask.getID() + " with value = " + val + " to the task " + chosenTask.getID() + " with value " + chosenval);
            System.err.println("I'm jumping ship from " + curChosenTask.getID() + " with bounty = " + getPotentialReward(curChosenTask, timeOnTask)+ " to the task " + chosenTask.getID() + " with value " + getPotentialReward(chosenTask, 0.0));
            System.err.println("tval = " + timeTable.getQValue(curChosenTask.getID(), 0) + " tval2 = " + timeTable.getQValue(chosenTask.getID(), 0));
            System.err.println("timeleft = " + (timeTable.getQValue(curChosenTask.getID(), 0) - timeOnTask));
            System.err.println("Time since last completetion = " + getTimeSinceCompletion());
          }
        }
        
        
        return chosenTask;
        
    }

    double getPotentialReward(Task task, double timeOnCurTask) {
        double timeLeft = timeTable.getQValue(task.getID(), 0) - timeOnCurTask;
        if (timeLeft <= 0) {
            timeLeft = 1.0; // i think i'll be done the next timestep.
        }
        return task.getCurrentReward() + incrementRateTable.getQValue(task.getID(), 0) * timeLeft;
    }
    
    double getProspectiveCosts(Task task) {
        return 0.0;
    }
    
    double getTotalOperatingCostsSinceLastPayment() {
        return getPerTimestepCost() * (double)getTimeSinceCompletion();
    }
    
    double getProspectiveOperatingCosts(Task task, double timeOnCurTask) {
        double timeLeft = timeTable.getQValue(task.getID(), 0) - timeOnCurTask;
        if (timeLeft <= 0) {
            timeLeft = 1.0; // i think i'll be done the next timestep.
        }
        return getPerTimestepCost() * timeLeft;
    }
    
    
    @Override
    double getPValue(Task availTask) {
        return pTable.getQValue(availTask.getID(), 0);
    }

    private double getPerTimestepCost() {
        return 1.0;// easiest way...
    }
}
