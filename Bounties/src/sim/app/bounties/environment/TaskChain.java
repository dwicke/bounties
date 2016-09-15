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

    public TaskChain() {
        this.taskchain = new Bag();
        this.curBlock = 0;
    }
    
    
   
    
    public void push(TaskBlock tb) {
        taskchain.push(tb);
        curBlock++;
    }
    
    public TaskBlock nextTaskBlock() {
        curBlock--;
        TaskBlock nextBlock = (TaskBlock) taskchain.get(curBlock);
        return nextBlock;
    }
    
    public TaskBlock peekNextTaskBlock() {
        
        TaskBlock nextBlock = (TaskBlock) taskchain.get(curBlock - 1);
        return nextBlock;
    }
    
    public Boolean finishedTaskChain() {
        return curBlock == -1;
    }

    
    public TaskBlock reset() {
        curBlock = taskchain.numObjs - 2;
        return (TaskBlock) taskchain.top();
    }
}
