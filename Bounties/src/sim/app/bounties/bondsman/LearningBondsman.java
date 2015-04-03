/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.bondsman;

import sim.app.bounties.Bounties;
import sim.app.bounties.Task;

/**
 *
 * @author drew
 */
public class LearningBondsman extends Bondsman {
    private static final long serialVersionUID = 1;

    public LearningBondsman(Bounties bounties, int exclusiveType) {
        super(bounties, exclusiveType);
    }
    
       
    
    @Override
    public void decideExclusivity(Task task) {
        
        if (isExclusive[task.getID()]) {
            
            
        } else {
           
        }
    }
}
