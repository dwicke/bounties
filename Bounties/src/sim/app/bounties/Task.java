package sim.app.bounties;

import sim.app.bounties.agent.IAgent;
import ec.util.MersenneTwisterFast;
import java.awt.Color;
import java.util.Arrays;
import sim.app.bounties.util.LogNormalDist;
import sim.app.bounties.util.Real;
import sim.portrayal.Fixed2D;
import sim.util.Bag;
import sim.util.Double2D;
import sim.util.Int2D;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author dfreelan
 */
public class Task implements Real, Fixed2D{
        private static final long serialVersionUID = 1;

    private int currentReward = 0; // controlled by bondsman to increase
    private boolean done = false; // true when at the goal false otherwise
    private boolean available = true;// true when a robot is not carrying and not at a goal it is false if not at the
    public Int2D realLocation;// location
    public Int2D initialLocation; 
    private int id = 0;
    private int defaultReward = 100;
    private Color availableColor = Color.RED;// may want to change color if we have different types of tasks
    private Color notAvailableColor = Color.WHITE;
    private Bag presentRobots = new Bag();
    private int lastFinishedRobotID = -1;//hopefully this wont cause a runtime error... who last finished the task by default set to -1;
    private long finishedTime = -1;
    private Bag lastAgentsWorkingOnTask; // these are the agents working on the task when someone finished it
    private int timeUntilRespawn = 0;
    private MersenneTwisterFast rand = null; 
    public int badForWho = -1;
 
    Bounties bountyState = null; //this is so we can hack in the graphics
    private Task() {}
    public Task(int numAgents, MersenneTwisterFast rand, Bounties hack) {

        
        currentReward = defaultReward;
        this.rand = rand;
        bountyState = hack;
        lastAgentsWorkingOnTask = new Bag();
    }
    
    public void setRandom(MersenneTwisterFast rand){
        this.rand = rand;
    }
    
    public boolean isDone(){
        return done;
    }
    public void setDone(boolean val){
        if(val == true && done == false){
            setBadForWho();
            generateRealTaskLocation();
        }
        if(val==false && done == true){
            makeRespawnTime();
        }
        done = val;
    }
    public void generateRealTaskLocation(){
        
        int newX = initialLocation.x + (int)Math.round(((rand.nextGaussian()) * 5));
        int newY = initialLocation.y + (int)Math.round(((rand.nextGaussian()) * 5));

        realLocation = new Int2D(newX,newY);
        this.bountyState.tasksGrid.setObjectLocation(this, realLocation);
    }
    
    public void setBadForWho() {
        if(rand.nextInt(10)==0){
            badForWho = rand.nextInt(bountyState.numAgents);
        }else{
            badForWho = -1;
        }
    }
    
    public boolean isTaskReady(){//check to see if this is finally time to spawn.
        
        timeUntilRespawn--;
        return timeUntilRespawn<=0;//less than or equal to since timeUntilRespawn could be -1 if timeUntilRespawn was chosen to be 0
    }
    
    public void setLastFinished(int robotid, long timestamp){
        lastFinishedRobotID =  robotid;
        finishedTime = timestamp;
    }
    /**
     * Call this when you finish a task.
     * @param robotid your id
     * @param timestamp the timestep that you completed the task
     * @param lastAgentIDsWorkingOnTask the list of ids of the agents that were working on it when you finished it
     */
    public void setLastFinished(int robotid, long timestamp, Bag lastAgentIDsWorkingOnTask){
        lastFinishedRobotID =  robotid;
        finishedTime = timestamp;
        lastAgentsWorkingOnTask = lastAgentIDsWorkingOnTask;
    }
    

    
    public Bag getLastAgentsWorkingOnTask() {
        return lastAgentsWorkingOnTask;
    }
    public int getLastFinishedRobotID(){
        return lastFinishedRobotID;
    }
    public long getLastFinishedTime() {
        return finishedTime;
    }
    public void makeRespawnTime(){
        timeUntilRespawn = rand.nextInt(20); // use uniform since we want them to come back within a reasonable time... //10 + (int)(Math.round(rand.nextGaussian())*10-5);
    }
    public void setAvailable(boolean available) {
        if(available == true){
            makeRespawnTime();
        }
        this.available = available;
    }
    public boolean getIsAvailable() {
        return available;
    }
    
    public void setCurrentReward(int reward){
        currentReward = reward;
    }
    public void incrementCurrentReward(){
        if (currentReward < 10000000 && isDone() == false)// don't increment while the task is done 
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
    
    @Override
    public Int2D getLocation() {
        return realLocation;
    }
    public void setInitialLocation(Int2D loc) {
        this.initialLocation = loc;
    }
    
    public Int2D getInitialPosition() {
        return this.initialLocation;
    }

    
    public int getNumRobotsDoingTask() {
        return presentRobots.numObjs;
    }

    public void resetReward() {
        currentReward = defaultReward;
        
    }
    
    public void resetReward(int reward) {
        currentReward = reward;
    }

    public Double2D getRealTargetLocation()
    {
        return new Double2D((realLocation.x - 30) * 0.1, (realLocation.y - 20) * 0.1);
    }
   
    public double getOrientation() { return 0; }

    @Override
    public boolean maySetLocation(Object field, Object newObjectLocation) {
        
        initialLocation = (Int2D) newObjectLocation;
        System.err.println("Set new location to: " + initialLocation.toCoordinates());
        realLocation = initialLocation;
        //((SparseGrid2D)field).setObjectLocation(this, initialLocation);// move myself
        return true;
    }

    public Color getNotAvailableColor() {
        return notAvailableColor;
    }

    public Color getAvailableColor() {
        return availableColor;
    }

   
    

}
