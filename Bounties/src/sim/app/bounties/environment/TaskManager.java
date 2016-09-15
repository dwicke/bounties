/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.environment;

import sim.util.Bag;

/**
 * So this maintains the tasks so as to make only the leaf tasks visible
 * based on the task chains.
 * @author drew
 */
public class TaskManager extends Bag{
    
    Bag taskchains = new Bag();

    @Override
    public boolean add(Object obj) {
        
        if(obj != null && obj instanceof TaskChain) {
            TaskChain tc = (TaskChain) obj;
            super.addAll(tc.peekNextTaskBlock().tasks);
            taskchains.add(obj);
            return true;
        }
        return false;
    }
    
    public boolean finished(Task t) {
        TaskBlock tb = t.getTaskBlock().finished(t);
        this.remove(t);
        if (tb != null && tb != t.getTaskBlock()) {
            // then i should add the tasks from the next block
            this.addAll(tb.tasks);
            return false; // not finished the task chain
        }
        this.addAll(tb.getTaskChain().reset().tasks);
        
        return true;
    }
    
    
}
