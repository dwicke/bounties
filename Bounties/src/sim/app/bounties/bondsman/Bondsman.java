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
            if (((Task) tasks.objs[i]).isDone() && ((Task) tasks.objs[i]).isTaskReady()) {
                // need to reset the task and 
                
                ((Task) tasks.objs[i]).setAvailable(true);
                ((Task) tasks.objs[i]).setDone(false);
                ((Task) tasks.objs[i]).makeRespawnTime(bounties.random);
                
                
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
            Task t = new Task();
            t.setID(i);
            t.setInitialLocation(new Int2D(bounties.random.nextInt(field.x), bounties.random.nextInt(field.y)));
            t.generateRealTaskLocation(bounties.random);
            bounties.tasksGrid.setObjectLocation(t, t.realLocation);
            tasks.add(t);
        }
        return tasks;
    }
    
    
    
    public Bag getTasks(){
        return tasks;
    }
    
   
    public Task[] getAvailableTasks() {
        Task[] avail = new Task[tasks.size()];
        int curAv = 0;
        for (int i = 0; i < tasks.size(); i++) {
            if (((Task) tasks.objs[i]).getIsAvailable() && (isExclusive == false || whoseDoingTaskByID((Task) tasks.objs[i]).isEmpty())) {
                avail[curAv] = (Task) tasks.objs[i];
                curAv++;
            }
        }
        Task[] tempAvail = new Task[curAv];
        for (int i = 0; i < curAv; i++) {
            tempAvail[i] = avail[i];
        }
        return tempAvail;
    }
    
    public void setIsExclusive(boolean isExlucsive) {
        this.isExclusive = isExlucsive;
    }
    
    
    public void finishTask(Task curTask, int robotID, long timestamp) {
        curTask.setLastFinished(robotID, timestamp);
        curTask.setAvailable(false); // whenever an agent finishes a task then make it unavailable
        curTask.setDone(true);
        
        if(bounties.random.nextInt(10)==0){
            curTask.setBadForWho(bounties.random.nextInt(bounties.numAgents));
        }else{
            curTask.setBadForWho(-1);
        }
        
        curTask.generateRealTaskLocation(bounties.random);
        bounties.tasksGrid.setObjectLocation(curTask, curTask.realLocation);
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
