/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.bounties.bondsman;

import sim.app.bounties.agent.IAgent;
import java.util.Arrays;
import sim.app.bounties.Bounties;
import sim.app.bounties.Task;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;
import sim.util.Int2D;

/**
 * Makes the tasks
 * @author drew
 */
public class Bondsman implements Steppable {
    private static final long serialVersionUID = 1;

    protected Bag tasks = new Bag();
    private int whosDoingWhatTaskID[];
    Bounties bounties;
    private boolean isExclusive;
    
    
    public Bondsman(){
    }

    public Bondsman(Bounties bounties, boolean isExclusive) {
        this.isExclusive = isExclusive;
        this.bounties = bounties;
        whosDoingWhatTaskID = new int[this.bounties.numAgents];
        // set everyone to do task -1 since not doing anytask
        Arrays.fill(whosDoingWhatTaskID, -1);
    }
    
    @Override
    public void step(SimState state) {
        makeAvailable();
        incrementBounty();// increment the bounties
    }
    
    public void incrementBounty(){
        for(int i = 0; i< tasks.size(); i++){
            ((Task)tasks.objs[i]).incrementCurrentReward();
        }
    }
    public void makeAvailable() {
        for (int i = 0; i < tasks.size(); i++) {
            if (((Task) tasks.objs[i]).isDone()) {
                if(((Task) tasks.objs[i]).isTaskReady()){
                    
                    ((Task) tasks.objs[i]).setAvailable(true);
                    ((Task) tasks.objs[i]).setDone(false);
                }
            }
        }
    }
    
    
    /**
     * gets the initial tasks
     * @param field the field in where the tasks locations can be
     * @return the tasks
     */
    public Bag initTasks(Int2D field) {
        tasks.clear();
        for (int i = 0; i < bounties.numTasks; i++) {
            Task t = new Task(this.bounties.numAgents, bounties.random, this.bounties);
            t.setID(i);
            t.setInitialLocation(new Int2D(bounties.random.nextInt(field.x), bounties.random.nextInt(field.y)));
            t.generateRealTaskLocation();
            tasks.add(t);
        }
        return tasks;
    }
    
    
    
    public Bag getTasks(){
        return tasks;
    }
    
   
    public Bag getAvailableTasks() {
        Bag avail = new Bag();
        for (int i = 0; i < tasks.size(); i++) {
            if (((Task) tasks.objs[i]).getIsAvailable() && (isExclusive == false || whoseDoingTaskByID((Task) tasks.objs[i]).isEmpty())) {
                avail.add(tasks.objs[i]);
            }
        }
        return avail;
    }
    
    public void setIsExclusive(boolean isExlucsive) {
        this.isExclusive = isExlucsive;
    }
    
    
    public void finishTask(Task curTask, int robotID, long timestamp) {
        curTask.setLastFinished(robotID, timestamp);
        curTask.setAvailable(false); // whenever an agent finishes a task then make it unavailable
        curTask.setDone(true);
        curTask.resetReward();
        whosDoingWhatTaskID[robotID] = -1;
    }
    /**
     * use this when you finish a task and are committing to a new task
     * @param robotID who you are 
     * @param taskID what task you are doing
     */
    public void doingTask(int robotID, int taskID) {
        whosDoingWhatTaskID[robotID] = taskID;
    }
    
    
    public Bag whoseDoingTaskByID(Task b) {
        Bag robots = new Bag();
        // only jumpship robots use this.
        IAgent[] allRobots = (IAgent[]) bounties.getAgents();
        for (int i = 0; i < bounties.numAgents; i++) {
            if (whosDoingWhatTaskID[i] == b.getID()){
                robots.add(allRobots[i].getId());
            }
        }
        return robots;
    }
    
    
}
