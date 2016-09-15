/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.environment;

import ec.util.MersenneTwisterFast;
import sim.util.Bag;

/**
 * Given a set of task classes multiple TaskChains can be created for the bondsman
 * to allocate.
 * @author drew
 */
public class TaskChainBuilder {

    int maxBlockSize, maxChainLength, minBlockSize, minChainLength;
    Bag taskClasses;
    TaskBuilder taskbuilder;
    
    public TaskChainBuilder(TaskBuilder taskbuilder, int maxBlockSize, int maxChainLength, 
            int minBlockSize, int minChainLength) {
        this.maxBlockSize = maxBlockSize;
        this.maxChainLength = maxChainLength;
        this.minBlockSize = minBlockSize;
        this.minChainLength = minChainLength;
        this.taskbuilder = taskbuilder;
    }
    
    Bag buildTaskChains(int numChains, MersenneTwisterFast random) {
        Bag taskChains = new Bag(numChains);
        
        for(int i = 0; i < numChains; i++) {
            TaskChain tc = new TaskChain();
            int chainLength = random.nextInt(this.maxChainLength - this.minChainLength + 1) + this.minChainLength;
            
            // now build the chain
            for (int j = 0; j < chainLength; j++) {
                // for each link in the chain i have a block
                TaskBlock tb = new TaskBlock();
                int numTasks = random.nextInt(this.maxBlockSize - this.minBlockSize + 1) + this.minBlockSize;
                for (int k = 0; k < numTasks; k++) {
                    Task newTaskClass = taskbuilder.buildTask();
                    taskClasses.add(newTaskClass);
                    tb.addTask(newTaskClass);
                }
            }
        }
        
        return taskChains;
    }
    
    
    
}
