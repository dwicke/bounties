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
 *
 * @author drew
 */
public class JumpshipSimpleCValuator extends LearningValuator implements DecisionValuator {
    private static final long serialVersionUID = 1;
    
    private int numTasks;
    QTable bTable, tIncTable; // learn the 
    
    public JumpshipSimpleCValuator(MersenneTwisterFast random, double epsilonChooseRandomTask, int agentID, boolean hasOneUp, int numTasks, int numRobots) {
        super(random, epsilonChooseRandomTask, agentID, hasOneUp, numTasks, numRobots);
        this.numTasks = numTasks;
        
        tIncTable = new QTable(numTasks, 1, tTableLearningRate, tTableDiscountBeta, initValue); 
        
        
    }
    @Override
    Task pickTask(Task availableTasks[]) {
        double max = -1;
        Task curTask = null;
        for (Task availTask : availableTasks) {
            // over all tasks
            double tval = timeTable.getQValue(availTask.getID(), 0);
            double pval = getPValue(availTask);
            double tIncVal = tIncTable.getQValue(availTask.getID(), 0);
            
            double value = 0;
                        
            if(this.preTask!=null&&availTask.getID()==this.preTask.getID()) {
            	if(tval > numTimeSteps){
            		double told = tval;
                	tval = tval-numTimeSteps;
                	value = 1.0 / tval * pval * (availTask.getCurrentReward() + tval);
                }
                else{
                	value = Double.POSITIVE_INFINITY;
                }
            }
            else{
            	value = 1.0 / tval * pval * (availTask.getCurrentReward() + tval);
            }
            	
            	
            
//            if(tval >numTimeSteps){
//            	tval = tval-numTimeSteps;
//            }
//            value = 1.0 / tval * pval * (availTask.getCurrentReward() + tval);
//           
            	
            
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
        if(reward == 1.0) { // task complete
            timeTable.update(curTask.getID(), 0, numTimeSteps);
            pTable.update(curTask.getID(), 0, reward);
        }else{
            pTable.update(curTask.getID(), 0, reward);
            tIncTable.update(curTask.getID(), 0, numTimeSteps);
        }
        if (this.hasOneUp)
        	pTable.oneUpdate(oneUpdateGamma);
    }

    @Override
    public void learn(Task curTask, double reward, Bag agentsWorking, int numTimeSteps) {
        double oldT = timeTable.getQValue(curTask.getID(), 0);
        learn(curTask, reward, numTimeSteps);// I don't use agentsWorking.
        //updateEpsilon(oldT, timeTable.getQValue(curTask.getID(), 0));
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
