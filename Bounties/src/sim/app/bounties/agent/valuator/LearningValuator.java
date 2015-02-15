/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.agent.valuator;

import ec.util.MersenneTwisterFast;
import sim.app.bounties.Task;
import sim.app.bounties.util.QTable;

/**
 *
 * @author drew
 */
public abstract class LearningValuator extends DefaultValuator implements DecisionValuator {
    
    QTable timeTable; // time to do task
    QTable pTable; // probablility that I am successful at a task
    double oneUpdateGamma = .001;
    double tTableLearningRate = .1;
    double tTableDiscountBeta = .1;
    double pTableLearningRate = .2;
    double pTableDiscountBeta = .1;
    double initValue = 1;
    boolean hasOneUp;
    
    public LearningValuator(MersenneTwisterFast random, double epsilonChooseRandomTask, 
            int agentID, boolean hasOneUp, int numTasks, int numRobots){
        super(random, epsilonChooseRandomTask, agentID);
        this.hasOneUp = hasOneUp;
        timeTable = new QTable(numTasks, 1, tTableLearningRate, tTableDiscountBeta, initValue); 
        pTable = new QTable(numTasks, numRobots, pTableLearningRate, pTableDiscountBeta, initValue);        
    }
    
    public void setOneUpdateGamma(double oneUpdateGamma) {
        this.oneUpdateGamma = oneUpdateGamma;
    }
    
    @Override
    Task pickTask(Task availableTasks[]) {
        double max = -1;
        Task curTask = null;
        for (Task availTask : availableTasks) {
            // over all tasks
            double tval = timeTable.getQValue(availTask.getID(), 0);
            double pval = getPValue(availTask);
            double value = 1.0 / tval * pval * availTask.getCurrentReward();
            if (value > max) {
                max = value;
                curTask = availTask;
            }
        }
        return curTask;
    }
    
    abstract double getPValue(Task availTask);
}
