/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.agent.valuator;

import ec.util.MersenneTwisterFast;
import sim.app.bounties.bondsman.Bondsman;
import sim.app.bounties.environment.Task;
import sim.util.Bag;

/**
 *
 * @author drew
 */
public class BadValuator extends DefaultValuator{
    private static final long serialVersionUID = 1;

    Bondsman bondsman;
    public BadValuator(MersenneTwisterFast random, int agentID, Bondsman bondsman) {
        super(random, 0, agentID);
        this.bondsman = bondsman;
    }

    @Override
    Task pickTask(Task[] availableTasks) {
        Task curTask = null;
        
        // this is not a good way to do this since if there are more agent's than
        // tasks this could result in an infinite loop!
        while(curTask == null) {
            
            int ind = random.nextInt(availableTasks.length);
            if  (bondsman.whoseDoingTaskByID(availableTasks[ind]).numObjs == 0){
                curTask = (Task)availableTasks[ind];
            }
            
           
        }
        return curTask;
    }

    @Override
    public void learn(Task curTask, double reward, Bag agentsWorking, int numTimeSteps) {
        // nop
    }
    
}
