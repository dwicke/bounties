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
 *Considers exclusivity as well
 * @author drew
 */
public class ExpandedComplexValuator extends LearningValuator implements DecisionValuator {
    private static final long serialVersionUID = 1;
    
        QTable kTable; // probablility that the task is non-exclusive given agent
        double kTableLearningRate = .2;
        double kTableDiscountBeta = .1;

    
    
    public ExpandedComplexValuator(MersenneTwisterFast random, double epsilonChooseRandomTask, int agentID, boolean hasOneUp, int numTasks, int numRobots) {
        super(random, epsilonChooseRandomTask, agentID, hasOneUp, numTasks, numRobots);
        kTable = new QTable(numTasks, numRobots, kTableLearningRate, kTableDiscountBeta, initValue);        
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
            double pval = getPValue(availTask);
            double kval = getKValue(availTask);
            double value = 1.0 / tval * pval * kval * availTask.getCurrentReward();
            if (value > max) {
                max = value;
                curTask = availTask;
            }
        }
        return curTask;
    }
    
    
    @Override
    public double getPValue(Task taski) {
        
        if (taski.getLastAgentsWorkingOnTask().isEmpty()) {
            return 1.0;
        }
        
        double pmul = 1.0;
        
        for(int i = 0; i < taski.getLastAgentsWorkingOnTask().size(); i++) {
            if ((int)(taski.getLastAgentsWorkingOnTask().objs[i]) != agentID)
                pmul *= pTable.getQValue(taski.getID(), (int)(taski.getLastAgentsWorkingOnTask().objs[i]));
        }
        return pmul;
        
    }
    
    public double getKValue(Task taski) {
        
        if (taski.getLastAgentsWorkingOnTask().isEmpty()) {
            return 1.0;
        }
        
        double kmul = 1.0;
        
        for(int i = 0; i < taski.getLastAgentsWorkingOnTask().size(); i++) {
            if ((int)(taski.getLastAgentsWorkingOnTask().objs[i]) != agentID)
                kmul *= kTable.getQValue(taski.getID(), (int)(taski.getLastAgentsWorkingOnTask().objs[i]));
        }
        return kmul;
        
    }

    @Override
    public void learn(Task curTask, double reward, Bag agentsWorking, int numTimeSteps) {
        if(reward == 1.0) {
            timeTable.update(curTask.getID(), 0, numTimeSteps);
            for (int i = 0; i < curTask.getLastAgentsWorkingOnTask().numObjs; i++) {
                pTable.update(curTask.getID(), ((int)curTask.getLastAgentsWorkingOnTask().objs[i]), reward);
                kTable.update(curTask.getID(), ((int)curTask.getLastAgentsWorkingOnTask().objs[i]), curTask.getIsNonExclusive());

            }
            
            
            if (this.hasOneUp)
                pTable.oneUpdate(oneUpdateGamma);
            return;
        }
        pTable.update(curTask.getID(), curTask.getLastFinishedRobotID(), reward);
        kTable.update(curTask.getID(), curTask.getLastFinishedRobotID(), curTask.getIsNonExclusive());
        
       if (this.hasOneUp)
                pTable.oneUpdate(oneUpdateGamma);
       
       
       
       
       
       
    }
    
}
