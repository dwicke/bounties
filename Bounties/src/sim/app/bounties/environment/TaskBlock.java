/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.environment;

import sim.util.Bag;

/**
 * A set of tasks that need to be completed together in order task to be completed.
 * A task block can have 1 or more Task objects
 * @author drew
 */
public class TaskBlock {
    
    Bag tasks;

    public TaskBlock() {
        this.tasks = new Bag();
    }
    
    public void addTask(Task t) {
        this.tasks.add(t);
    }
    
    
}
