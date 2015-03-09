/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.agent.valuator;

import ec.util.MersenneTwisterFast;
import sim.app.bounties.Bondsman;
import sim.app.bounties.Task;
import sim.util.Bag;

/**
 *
 * @author drew
 */
public class BadValuator extends DefaultValuator{

    Bondsman bondsman;
    public BadValuator(MersenneTwisterFast random, int agentID, Bondsman bondsman) {
        super(random, 0, agentID);
        this.bondsman = bondsman;
    }

    @Override
    Task pickTask(Task[] availableTasks) {
        Task curTask = null;
        
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
