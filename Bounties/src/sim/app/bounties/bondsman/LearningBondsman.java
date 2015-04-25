/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.bondsman;

import sim.app.bounties.Bounties;
import sim.app.bounties.environment.Task;

/**
 *
 * @author drew
 */
public class LearningBondsman extends Bondsman {
    private static final long serialVersionUID = 1;

    public LearningBondsman(Bounties bounties, int exclusiveType) {
        super(bounties, exclusiveType);
    }
    

    /*
    So what would you want to learn?
    
    we want to minimize the expected bounty for the task
    We have the success rates of the agents
    We have the current average bounty on 
    we have the past bounty paid
    want to make it so that it also works for rotating robots
    
    */
    
    @Override
    public void decideExclusivity(Task task) {
        
        if (isExclusive[task.getID()]) {
            
            
        } else {
           
        }
    }
}
