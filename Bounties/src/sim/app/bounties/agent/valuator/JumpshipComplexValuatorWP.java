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
 * This also predicts who is going to be on the task.
 * @author drew
 */
public class JumpshipComplexValuatorWP extends LearningValuator implements DecisionValuator {
    private static final long serialVersionUID = 1;
    
    double RTable[/*i*/][/*a*/]; // number of times i have commited to task i and seen agent a
    double NCount[];// number of times i have committed to doing task i
    public JumpshipComplexValuatorWP(MersenneTwisterFast random, double epsilonChooseRandomTask, int agentID, boolean hasOneUp, int numTasks, int numRobots) {
        super(random, epsilonChooseRandomTask, agentID, hasOneUp, numTasks, numRobots);
        RTable = new double[numTasks][numRobots];
        NCount = new double[numTasks];
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
            double rval = getRValue(availTask);
            double value = 1.0 / tval * pval * (availTask.getCurrentReward() + tval);
            if (value > max) {
                max = value;
                curTask = availTask;
            }
        }
        return curTask;
    }
    
    public double getRValue(Task taski) {
        
        
        double pmul = 1.0;
        
        for(int i = 0; i < taski.getCurrentAgentsOnTask().size(); i++) {
            if ((int)(taski.getCurrentAgentsOnTask().objs[i]) != agentID)
                pmul *= pTable.getQValue(taski.getID(), (int)(taski.getCurrentAgentsOnTask().objs[i]));
        }
        return pmul;
    }
    
    @Override
    public double getPValue(Task taski) {
        
        if (taski.getCurrentAgentsOnTask().isEmpty()) {
            return 1.0;
        }
        
        double pmul = 1.0;
        
        for(int i = 0; i < taski.getCurrentAgentsOnTask().size(); i++) {
            if ((int)(taski.getCurrentAgentsOnTask().objs[i]) != agentID)
                pmul *= pTable.getQValue(taski.getID(), (int)(taski.getCurrentAgentsOnTask().objs[i]));
        }
        return pmul;
        
    }

    @Override
    public void learn(Task curTask, double reward, Bag agentsWorking, int numTimeSteps) {
        if(reward == 1.0) {
            timeTable.update(curTask.getID(), 0, numTimeSteps);
            for (int i = 0; i < agentsWorking.numObjs; i++) {
                pTable.update(curTask.getID(),(int) (agentsWorking.objs[i]), reward);
            }
            if (this.hasOneUp)
                pTable.oneUpdate(oneUpdateGamma);
            return;
        }
        for (int i = 0; i < agentsWorking.numObjs; i++) {
                pTable.update(curTask.getID(), ((int)agentsWorking.objs[i]), reward);
        }
       if (this.hasOneUp)
                pTable.oneUpdate(oneUpdateGamma);
    }
    
}
