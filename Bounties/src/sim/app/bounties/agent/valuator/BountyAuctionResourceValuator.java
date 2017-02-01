/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.agent.valuator;

import ec.util.MersenneTwisterFast;
import java.util.Collections;
import sim.app.bounties.environment.Task;
import sim.util.Bag;

/**
 *
 * @author drew
 */
public class BountyAuctionResourceValuator extends BountyAuctionValuator {
    
    
    public BountyAuctionResourceValuator(MersenneTwisterFast random, double epsilonChooseRandomTask, int agentID, boolean hasOneUp, int numTasks, int numRobots) {
        super(random, epsilonChooseRandomTask, agentID, hasOneUp, numTasks, numRobots);
        
    }
    
    
    public void setAuctionCompetitors(BountyRAuctionValuator[] bots) {
        this.auctionValuators = bots;
    }    
    
    /**
     * What is my "bid" for each of the available tasks in the system
     * @param availTasks
     * @param curChosenTask
     * @param timeOnTask
     * @return 
     */
    @Override
    public double[] getEvaluations(Task[] availTasks, Task curChosenTask, double timeOnTask){
       
        //System.err.println("Agent id" + agentID);
        double[] evaluations = new double[availTasks.length];
        
        int i = 0;
        for (Task availTask : availTasks) {
            double confidence = pTable.getQValue(((Task)availTasks[i]).getID(), 0);
            
            
            
            // pval*(reward - prospectiveCosts) - (totalOperatingCostsSoFar + prospectiveOperatingCosts)
            // basically we have the confidence of success times the amount we will earn minus the amount we would spend to succeed
            // then we subtract the operating costs basically the cost to travel to the task
            double timeOnCurTask = 0.0;
            if (curChosenTask != null && availTask.getID() == curChosenTask.getID()) {
                timeOnCurTask = timeOnTask;
            }
            double value = confidence * (getPotentialReward(availTask, timeOnCurTask) - getProspectiveCosts(availTask) - getProspectiveOperatingCosts(availTask, timeOnCurTask))
                    - getTotalOperatingCostsSinceLastPayment();
            if  (isDead == true){
                    value*=-1;
            }
            evaluations[i] = value;
            i++;
        }
        
       

        return evaluations;
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
    

    private double getPerTimestepCost() {
        return 1.0;// easiest way...
    }
    
    @Override
    public void learn(Task curTask, double reward, Bag agentsWorking, int numTimeSteps) {
        if(reward == 1.0) {
            timeTable.update(curTask.getID(), 0, numTimeSteps);
            pTable.update(curTask.getID(), 0, reward);
        }else{
            pTable.update(curTask.getID(), 0, reward);
        }
        if (this.hasOneUp)
                pTable.oneUpdate(oneUpdateGamma);
    }

    
    
}
