/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.environment;

import sim.util.Bag;


/**
 * 
 * A task chain defines the order particular tasks should be accomplished
 * It is defined as a stack of task blocks.
 * @author drew
 */
public class TaskChain {

    Bag taskchain;
    int curBlock;
    
    
    public void push(TaskBlock tb) {
        taskchain.push(tb);
        curBlock++;
    }
    
    public TaskBlock nextTaskBlock() {
        
        TaskBlock nextBlock = (TaskBlock) taskchain.get(curBlock);
        curBlock--;
        return nextBlock;
    }
    
    public TaskBlock peekNextTaskBlock() {
        
        TaskBlock nextBlock = (TaskBlock) taskchain.get(curBlock);
        return nextBlock;
    }
    
    public Boolean finishedTaskChain() {
        return curBlock == -1;
    }

}
