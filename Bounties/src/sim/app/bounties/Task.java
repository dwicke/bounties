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
    
    private int currentReward = 0; // controlled by bondsman to increase
    private boolean done = false;
    //location variable please
    private int id = 0;
    public boolean isDone(){
        return done;
    }
    public void setDone(boolean val){
        done = val;
    }
    public void setCurrentReward(int reward){
        currentReward = reward;
    }
    public void incrementCurrentReward(){
        currentReward+=1;
    }
    public int getCurrentReward(){
        return currentReward;
    }
    public int getID(){
        return id;
    }
    public void setID(int id){
        this.id = id; 
    }
    
}
