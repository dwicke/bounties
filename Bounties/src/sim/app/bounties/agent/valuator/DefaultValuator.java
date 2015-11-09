/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.agent.valuator;

import ec.util.MersenneTwisterFast;
import sim.app.bounties.environment.Task;
import sim.util.Int2D;

/**
 *
 * @author drew
 */
public abstract class DefaultValuator implements DecisionValuator {
    private static final long serialVersionUID = 1;
    MersenneTwisterFast random;
    double epsilonChooseRandomTask;
    int agentID;
    boolean isDead;
    int numTimeSteps;
    Int2D curLoc;
    
    public void setCurrentPos(Int2D curLoc) {
        this.curLoc = curLoc;
    }
    public void setHome(Int2D home) {}// does nothing here... used for the optimal
    @Override
    public void setNumTimeSteps(int numTimeSteps) {
        this.numTimeSteps = numTimeSteps;
    }
    
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
