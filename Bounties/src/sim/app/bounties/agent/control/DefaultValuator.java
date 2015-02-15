/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.agent.control;

import ec.util.MersenneTwisterFast;
import sim.app.bounties.Task;

/**
 *
 * @author drew
 */
public abstract class DefaultValuator implements DecisionValuator {
    
    MersenneTwisterFast random;
    double epsilonChooseRandomTask;
    int agentID;
    boolean isDead;
    
    public DefaultValuator(MersenneTwisterFast random, double epsilonChooseRandomTask, int agentID) {
        this.random = random;
        this.epsilonChooseRandomTask = epsilonChooseRandomTask;
        this.agentID = agentID;
        isDead = false;
    }
    
    @Override
    public Task decideNextTask(Task availableTasks[]) {
        if(epsilonChooseRandomTask > random.nextDouble()){
            return (Task)availableTasks[random.nextInt(availableTasks.length)];
            
        }else{
            return pickTask(availableTasks);
        }
    }
    
    @Override
    public void setIsDead(boolean isDead) {
        this.isDead = isDead;
    }
    
    abstract Task pickTask(Task availableTasks[]);
}
