/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.bounties;

import ec.util.MersenneTwisterFast;
import java.util.Arrays;
import sim.app.bounties.jumpship.Jumpship;
import sim.engine.SimState;
import sim.engine.Steppable;
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
    private Jumpship jumpPolicy;
    private double penaltyFactor[]; // each robot has a penalty factor what percentage of current bounty do they get
    
    public Bondsman(){
    }

    Bondsman(int numGoals, int numTasks, Jumpship js) {
        this.numGoals = numGoals;
        this.numTasks = numTasks;
        jumpPolicy = js;
    }
    
    
    public void setWorld(Bounties bounties) {
        this.bounties = bounties;
        whosDoingWhatTaskID = new int[this.bounties.numRobots];
        penaltyFactor = new double[this.bounties.numRobots];
        Arrays.fill(penaltyFactor, 1);
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
            Task t = new Task(this.bounties.numRobots);
            t.setID(i);
            //t.setCurrentReward(1);// this isn't used.
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
    public void setTasks(Bag tasks) {
        this.tasks = tasks;
        numTasks = tasks.size();
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
    public int getTotalNumRobots() {
        return bounties.numRobots;
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
        //((Bounties)state).getRobotTabsCols();
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
    
    void finishTask(Task curTask, int robotID, long timestamp) {
        curTask.setLastFinished(robotID, timestamp);
        curTask.setAvailable(false); // whenever an agent finishes a task then make it unavailable
        curTask.setDone(true);
        curTask.resetReward(); // start it back at 0
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
    /**
     * So if you were doing a task and you are switching
     * you must tell the bondsman.  
     * @param robot 
     * @param newTaskID 
     * @return true if changed.
     */
    public boolean changeTask(IRobot robot, Task oldTask, Task newTask, SimState state) {
        if (jumpPolicy.jumpship(robot,oldTask, newTask, state)) {
            whosDoingWhatTaskID[robot.getId()] = newTask.getID();
            return true;
        }
        return false;
    }
    
    

    public Bag whoseDoingTask(Task b) {
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
    
    public Bag whoseDoingTaskByID(Task b) {
        Bag robots = new Bag();
        // only jumpship robots use this.
        IRobot[] allRobots = (IRobot[]) bounties.getRobots();
        for (int i = 0; i < bounties.numRobots; i++) {
            if (whosDoingWhatTaskID[i] == b.getID()){
                robots.add(allRobots[i].getId());
            }
        }
        return robots;
    }
    
    public int getBondsmanAdjustedBounty(Task task, IRobot bot) {
        return (int) (task.getCurrentReward(bot) * penaltyFactor[bot.getId()]);
    }
    
    public void setPenaltyFactor(IRobot bot, double rate) {
        penaltyFactor[bot.getId()] = rate;
    }
    
    public double getPenaltyFactor(IRobot bot) {
        return penaltyFactor[bot.getId()];
    }
    
    
}
