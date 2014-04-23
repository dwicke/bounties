/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.bounties;

import ec.util.MersenneTwisterFast;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;
import sim.util.Int2D;

/**
 *
 * @author dfreelan
 * bondsman is in charge of making tasks.... goodluck?
 */
public class Bondsman implements Steppable {
    
    private Bag tasks = new Bag();
    private Bag goals = new Bag();
    private int numTasks = 5;
    private int numGoals = 1;
    
    
    public Bondsman(){
    }
    
    /**
     * gets the initial tasks
     * @param field the field in where the tasks locations can be
     * @return the tasks
     */
    public Bag initTasks(Int2D field, MersenneTwisterFast rand) {
        
        for (int i = 0; i < numTasks; i++) {
            Task t = new Task();
            t.setID(i);
            t.setLoc(new Int2D(rand.nextInt(field.x), rand.nextInt(field.y)));
            
            tasks.add(t);
        }
        
        
        return tasks;
    }
    
    public Bag initGoals(Int2D field, MersenneTwisterFast rand) {
        for (int i = 0; i < numGoals; i++) {
            Goal t = new Goal();
            t.setLocation(new Int2D(rand.nextInt(field.x), rand.nextInt(field.y)));
            t.setId(i);
            goals.add(t);
        }
        
        
        return goals;
    }
    
    public Bag getTasks(){
        return tasks;
    }
    public void addTask(Task a){
        tasks.add(a);
    }
    public void incrementBounty(){
        for(int i = 0; i< tasks.size(); i++){
            ((Task)tasks.objs[i]).incrementCurrentReward();
        }
    }

    
    
    @Override
    public void step(SimState state) {
        
        incrementBounty();// increment the bounties
        
        
        
    }
    
    
    
    
    
}
