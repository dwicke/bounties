/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.bounties;

import sim.engine.SimState;
import sim.engine.Steppable;

/**
 *
 * @author drew
 */
public class TableRobot extends AbstractRobot implements Steppable {

    Task curTask, prevTask; // the curent and previous tasks I was doing
    int timeOnTask; // the number of timesteps i have worked on the task
    long lastSeenFinished; // the timestep the current task was sa
    
    
    
    @Override
    public void step(SimState state) {
        
        // check if someone (including me) finished the task if I am at the task then I will finish it
            // if finished current task then learn
        // pick task
        // goto task
        
        Bounties bountyState = (Bounties) state;
        
        if (finishedTask(bountyState)) {
            learn(bountyState);
        }
        
        pickTask(bountyState);
        
        gotoTask(bountyState);
        
    }
    
    public boolean finishedTask(Bounties state) {
        
        
        return false;
    }
    
    public void learn(Bounties state) {
        
    }
    
    public void pickTask(Bounties state) {
        
    }
    
    
    public void gotoTask(Bounties state) {
        
    }
    
    

}
