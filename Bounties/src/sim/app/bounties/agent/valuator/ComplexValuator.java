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
public class ComplexValuator extends LearningValuator implements DecisionValuator {
    private static final long serialVersionUID = 1;
    public ComplexValuator(MersenneTwisterFast random, double epsilonChooseRandomTask, int agentID, boolean hasOneUp, int numTasks, int numRobots) {
        super(random, epsilonChooseRandomTask, agentID, hasOneUp, numTasks, numRobots);
    }

    
    @Override
    public double getPValue(Task taski) {
        
        if (taski.getCurrentAgentsOnTask().isEmpty()) {
            return 1.0;
        }
        
        double pmul = 1.0;
        
        for(int i = 0; i < taski.getCurrentAgentsOnTask().size(); i++) {
            if ((Integer)(taski.getCurrentAgentsOnTask().objs[i]) != agentID)
                pmul *= pTable.getQValue(taski.getID(), (Integer)(taski.getCurrentAgentsOnTask().objs[i]));
        }
        return pmul;
        
    }

    public void learn(Task curTask, double reward, Bag agentsWorking, int numTimeSteps) {
        if(reward == 1.0) {
            timeTable.update(curTask.getID(), 0, numTimeSteps);
            for (int i = 0; i < curTask.getLastAgentsWorkingOnTask().numObjs; i++) {
                pTable.update(curTask.getID(), ((Integer)curTask.getLastAgentsWorkingOnTask().objs[i]), reward);
            }
            if (this.hasOneUp)
                pTable.oneUpdate(oneUpdateGamma);
            return;
        }
        pTable.update(curTask.getID(), curTask.getLastFinishedRobotID(), reward);
       if (this.hasOneUp)
                pTable.oneUpdate(oneUpdateGamma);
    }
    
}
