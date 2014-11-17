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
import sim.util.distribution.Poisson;
import sim.app.bounties.LogNormalDist.LogNormalDist;
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
    Bounties bounties;
    private Jumpship jumpPolicy;
    private LogNormalDist logNormalDist;
    private double penaltyFactor[]; // each robot has a penalty factor what percentage of current bounty do they get
    private int clearingTimes[];
    private int taskBeingWorkedOn[];
    private int clearTime;
    
    
    public Bondsman(){
    }

    Bondsman(int numGoals, int numTasks, Jumpship js, int clearTime) {
        this.numGoals = numGoals;
        this.numTasks = numTasks;
        this.clearTime = clearTime;
        clearingTimes = new int[numTasks];
        Arrays.fill(clearingTimes, clearTime);
        taskBeingWorkedOn = new int[numTasks];
        jumpPolicy = js;
    }
    
    public void resetClearTime(int taskID) {
        clearingTimes[taskID] = clearTime;
    }
    public int getClearTime(int taskID) {
        return clearingTimes[taskID];
    } 
    
    public void updateClearingTimes() {
        for (int i = 0; i < clearingTimes.length; i++) {
            if(clearingTimes[i] == 0 || taskBeingWorkedOn[i] == 1) { // we have to reset or someone is working on it 
                clearingTimes[i] = clearTime;
            }
            else {
                clearingTimes[i] = clearingTimes[i] - 1;
            }
        }
    }
    
    public void setWorld(Bounties bounties) {
        this.bounties = bounties;
        logNormalDist = new LogNormalDist(8,1,bounties.random);
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
            Task t = new Task(this.bounties.numRobots, rand, this.bounties);
            t.setID(i);
            //t.setCurrentReward(1);// this isn't used.
            t.setLoc(new Int2D(rand.nextInt(field.x), rand.nextInt(field.y)));
            t.setGoal((Goal)goals.objs[rand.nextInt(goals.numObjs)]);
            t.setRequiredRobots(1);
            tasks.add(t);
            t.setFailureRate(10+rand.nextInt(100));
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
                if(((Task) tasks.objs[i]).isTaskReady()){
                    ((Task) tasks.objs[i]).setAvailable(true);
                    ((Task) tasks.objs[i]).setDone(false);
                    taskBeingWorkedOn[((Task) tasks.objs[i]).getID()] = 0;
                }
            }
        }
    }
    
    @Override
    public void step(SimState state) {
        
        
        // reopen finished tasks (to be more realistic need a time a number of tics before add back in)
        //((Bounties)state).getRobotTabsCols();
        makeAvailable();
        incrementBounty();// increment the bounties
        updateClearingTimes();
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
        
        //curTask.resetReward((int)logNormalDist.sample());
        //curTask.resetReward((int)Math.abs(bounties.random.nextGaussian())*5000 + 1000); // this made a differnce a big one even more so when a bad robot is in the mix i think it does better than the 100 (works for simple and complex)
        //curTask.resetReward((int)Math.abs(bounties.random.nextGaussian())*5000 + 100); // this accentuates it even more especially if one of the robots is a BadRobot
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
        if (taskID != -1)
            taskBeingWorkedOn[taskID] = 1;
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
