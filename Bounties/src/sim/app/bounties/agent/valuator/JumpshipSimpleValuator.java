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
    Task pickTask(Task availableTasks[], Task unavailableTasks[]) { 
        return pickTask(availableTasks);
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
            /*
            double curReward = availTask.getCurrentReward();
            if (this.preTask != null && this.preTask.getID() != availTask.getID()) {
                curReward = availTask.getCurrentReward() - this.preTask.getCurrentReward();
                if (curReward < 0) {
                    curReward = 0;
                }
            }
            */
            
//            if (this.preTask != null && this.preTask.getID() == availTask.getID() && tval > 2) {
//                // so if this is my task then the time to complete should be changed
//                tval = Math.abs(tval - timeOnTask);
//            }
            //tval = 1.0;
            double pval = getPValue(availTask);
            //double value = 1.0 / tval * pval * (curReward + tval);
            //System.err.println("Tval = " + tval + " and the real distance = " + home.manhattanDistance(availTask.realLocation));
            //tval = home.manhattanDistance(availTask.realLocation);
            //tval = 1.0;
            //if (this.agentID == 0)
            //    System.err.println(" task Id = " + availTask.getID() + "  Tval = " + tval + " real distance = " + home.manhattanDistance(availTask.realLocation));
            
            //double value = 1.0 / tval * pval * (availTask.getCurrentReward() + tval);
            double value =  pval * (availTask.getCurrentReward() + 1.0);
            
            
            if (value > max) {
                max = value;
                curTask = availTask;
            }
            
            /// so the goal is to maximize the speed
            // speed is the time that the agent works on the task over how long it has been available
            //
            
            
            
            
            
            
            
        }
        
        return curTask;
    }
    @Override
    double getPValue(Task availTask) {
        return pTable.getQValue(availTask.getID(), 0);
    }
    
    private void learn(Task curTask, double reward, int numTimeSteps) {
        if(reward == 1.0) {
            
            
            //updateLearningRate(timeTable.getQValue(curTask.getID(), 0), numTimeSteps);
            
            timeTable.update(curTask.getID(), 0, numTimeSteps);
            if (this.agentID == 0)
                System.err.println("Task id = " + curTask.getID() + " numTimesteps = " + numTimeSteps + " new tval = " + timeTable.getQValue(curTask.getID(), 0) + " real distance = " + home.manhattanDistance(curTask.realLocation));
            
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
    }

    @Override
    public void learn(Task curTask, double reward, Bag agentsWorking, int numTimeSteps) {
        double oldT = timeTable.getQValue(curTask.getID(), 0);
        learn(curTask, reward, numTimeSteps);// I don't use agentsWorking.
//        if (reward == 1.0 && oldT != 1.0)
//            updateEpsilon(oldT, timeTable.getQValue(curTask.getID(), 0));
        updateEpsilon(1.0, 1.0);
    }
    
     public void updateEpsilon(double oldT, double newT) {
         double delta = (1.0 / (double)this.numTasks);
         double boltz = boltzman(oldT,newT, .85);
         
        epsilonChooseRandomTask = (1.0 - delta)*epsilonChooseRandomTask +
                                    delta*(boltz);
        if (this.agentID == 0) {
            System.err.println("boltz = " + boltz + " epsrnd = " + epsilonChooseRandomTask + " oldT " + oldT + " newT " + newT );
        }
    }
     
     public double boltzman(double oldT, double newT, double sigma) {
         return ( 1 - Math.exp(-Math.abs(newT - oldT) / sigma)) /
                 (1 + Math.exp(-Math.abs(newT - oldT) / sigma));
     }

}
