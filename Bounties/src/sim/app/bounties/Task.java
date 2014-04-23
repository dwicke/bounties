package sim.app.bounties;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author dfreelan
 */
public class Task {
    
    private int currentReward = 0;
    private boolean done = false;
    
    public boolean isDone(){
        return done;
    }
    public void setDone(boolean val){
        done = val;
    } 
}
