/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.environment;

import sim.app.bounties.Bounties;
import sim.app.bounties.bondsman.valuator.BondsmanValuator;
import sim.util.Int2D;

/**
 *
 * @author drew
 */
public class TaskBuilder {
    
    int curID;
    
    Bounties bounties;
    BondsmanValuator valuator;
    Int2D field;

    public TaskBuilder(Bounties bounties, Int2D field, BondsmanValuator valuator) {
        this.curID = 0;
        this.bounties = bounties;
        this.field = field;
        this.valuator = valuator;
    }
    
    
    
    Task buildTask(TaskBlock block) {
        Task t = new Task();
        t.setTaskBlock(block);
        t.setID(curID);
        curID++;
        t.setInitialLocation(new Int2D(bounties.random.nextInt(field.x), bounties.random.nextInt(field.y)));
        t.generateRealTaskLocation(bounties.random);
        bounties.tasksGrid.setObjectLocation(t, t.realLocation);
        valuator.setInitialBounty(t);
        
        return t;
    }
}
