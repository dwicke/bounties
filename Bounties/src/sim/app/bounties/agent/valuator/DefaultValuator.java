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
    Int2D curLoc;
    Task preTask;
    boolean jumped;
    int timeSinceCompletion = 0;
    int timeOnTask = 0;
    
    @Override
    public void setTimeOnTask(int timeOnTask) {
        this.timeOnTask = timeOnTask;
    }

    
    @Override
    public void incrementTimeSinceLastCompletion() {
        timeSinceCompletion++;
    }
    
    @Override
    public void resetTimeSinceLastCompletion() {
        timeSinceCompletion = 0;
    }
    
    //does nothing
    @Override
    public void learnIncrementRate(Task[] tasks) {    }
    
    @Override
    public void setJumped(boolean jumped) {
        this.jumped = jumped;
    }
    
    @Override
    public void setPreTask(Task task) {
        preTask = task;
    }
    
    @Override
    public void setCurrentPos(Int2D curLoc) {
        this.curLoc = curLoc;
    }
    @Override
    public void setHome(Int2D home) {}// does nothing here... used for the optimal
    
    
    public DefaultValuator(MersenneTwisterFast random, double epsilonChooseRandomTask, int agentID) {
        this.random = random;
        this.epsilonChooseRandomTask = epsilonChooseRandomTask;
        this.agentID = agentID;
        isDead = false;
    }
    
    @Override
    public Task decideNextTask(Task availableTasks[], Task unavailableTasks[]) {
        if(epsilonChooseRandomTask > random.nextDouble()){
            return (Task)availableTasks[random.nextInt(availableTasks.length)];
            
        }else{
            return pickTask(availableTasks, unavailableTasks);
        }
    }
    
    @Override
    public void setIsDead(boolean isDead) {
        this.isDead = isDead;
    }
    
    Task pickTask(Task availableTasks[], Task unavailableTasks[]) {
        return pickTask(availableTasks);
    }
    abstract Task pickTask(Task availableTasks[]);
}
