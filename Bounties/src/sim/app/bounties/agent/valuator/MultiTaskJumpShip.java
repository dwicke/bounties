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
public class MultiTaskJumpShip extends DefaultValuator implements DecisionValuator {
    private static final long serialVersionUID = 1;

    QTable confidenceTable; // probablility that I am successful at a task while on another
    private Task curTask; // 
    private Task previousTask; // 
    private double confLearningRate = .2; // set to .2 oritinally
    private double confBeta = .1; // not used...
    private double initValue = 1;
    private int numTasks; // this is the id of the do nothing task
    private double oneUpdateGamma = .001;

    public MultiTaskJumpShip(MersenneTwisterFast random, double epsilonChooseRandomTask, int agentID, int numTasks) {
        super(random, epsilonChooseRandomTask, agentID);
        // we also have the do nothing task
        this.numTasks = numTasks;
        confidenceTable = new QTable(numTasks + 1, numTasks + 1, confLearningRate, confBeta, initValue);
        curTask = null;
        previousTask = null;
    }

    
    
   
    
    @Override
    public void learn(Task curTask, double reward, Bag agentsWorking, int numTimeSteps) {
        // decay the epsilon (random exploration)
        epsilonChooseRandomTask *= (1.0 - (1.0 / (double)this.numTasks));
        
        if (previousTask == null) {
               confidenceTable.update(numTasks, curTask.getID(), reward);
        } else {
            confidenceTable.update(previousTask.getID(), curTask.getID(), reward);
        }
        //confidenceTable.oneUpdate(oneUpdateGamma);
    }

    @Override
    Task pickTask(Task[] availableTasks) {
        
        double maxBounty = 0.0; // this is for doing nothing
        Task chosenTask = null; // null is the do nothing task
        int curTaskID = numTasks;
        if (curTask != null) {
            curTaskID = curTask.getID();
        }
        for (Task availTask : availableTasks) {
            double confidence = confidenceTable.getQValue(curTaskID, availTask.getID());
            double value =  confidence * (availTask.getCurrentReward() + 1.0);

            if (value >= maxBounty) { // I'll do a task and not get anything for it
                maxBounty = value;
                chosenTask = availTask;
            }
        }
        
        previousTask = curTask;
        curTask = chosenTask;
        
        return chosenTask;
        
    }
}
