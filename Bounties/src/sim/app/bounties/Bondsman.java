/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.bounties;

import ec.util.MersenneTwisterFast;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.portrayal.simple.MovablePortrayal2D;
import sim.util.Bag;
import sim.util.Int2D;

/**
 * Makes the tasks and goals
 * @author drew
 */
public class Bondsman implements Steppable {
        private static final long serialVersionUID = 1;

    private Bag tasks = new Bag();
    private Bag goals = new Bag();
    private int whosDoingWhatTaskID[];
    private int numTasks = 50;
    private int numGoals = 1;
    private Bounties bounties;
    
    public Bondsman(){
    }

    Bondsman(int numGoals, int numTasks) {
        this.numGoals = numGoals;
        this.numTasks = numTasks;
    }
    
    
    public void setWorld(Bounties bounties) {
        this.bounties = bounties;
        whosDoingWhatTaskID = new int[this.bounties.numRobots];
        // set everyone to do task -1 since not doing anytask
        for (int i = 0; i < whosDoingWhatTaskID.length; i++) {
            whosDoingWhatTaskID[i] = -1;
        }
    }
    
    
    /**
     * gets the initial tasks
     * @param field the field in where the tasks locations can be
     * @return the tasks
     */
    public Bag initTasks(Int2D field, MersenneTwisterFast rand) {
        tasks.clear();
        for (int i = 0; i < numTasks; i++) {
            Task t = new Task();
            t.setID(i);
            t.setCurrentReward(1);
            t.setLoc(new Int2D(rand.nextInt(field.x), rand.nextInt(field.y)));
            t.setGoal((Goal)goals.objs[rand.nextInt(goals.numObjs)]);
            t.setRequiredRobots(rand.nextInt(1)+1);
            tasks.add(t);
            
        }
        
        
        return tasks;
    }
    
    public Bag initGoals(Int2D field, MersenneTwisterFast rand) {
        goals.clear();
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

    public int getTotalNumTasks() {
        return tasks.size();
    }
    
    public Bag getAvailableTasks() {
        Bag avail = new Bag();
        for (int i = 0; i < tasks.size(); i++) {
            if (((Task) tasks.objs[i]).getIsAvailable()) {
                avail.add(tasks.objs[i]);
            }
        }
        return avail;
    }
    
    public void makeAvailable() {
        for (int i = 0; i < tasks.size(); i++) {
            if (((Task) tasks.objs[i]).isDone()) {
                ((Task) tasks.objs[i]).setAvailable(true);
                ((Task) tasks.objs[i]).setDone(false);
            }
        }
    }
    
    @Override
    public void step(SimState state) {
        
        
        // reopen finished tasks (to be more realistic need a time a number of tics before add back in)
        makeAvailable();
        incrementBounty();// increment the bounties
        
    }

    public void setNumTasks(int numTasks) {
        this.numTasks = numTasks;
    }

    public void setNumGoals(int numGoals) {
        this.numGoals = numGoals;
    }

    public int getNumGoals() {
        return numGoals;
    }

    public int getNumTasks() {
        return numTasks;
    }

    void finishTask(Task curTask) {
        curTask.setDone(true);
        curTask.resetReward(); // start it back at 0
    }
    
    public void doingTask(int robotID, int taskID) {
        whosDoingWhatTaskID[robotID] = taskID;
    }

    Bag whoseDoingTask(Task b) {
        Bag robots = new Bag();
        // only jumpship robots use this.
        IRobot[] allRobots = (IRobot[]) bounties.getRobots();
        for (int i = 0; i < bounties.numRobots; i++) {
            if (whosDoingWhatTaskID[i] == b.getID()){
                robots.add(allRobots[i]);
            }
        }
        return robots;
    }
    
    
    
    
    
}
