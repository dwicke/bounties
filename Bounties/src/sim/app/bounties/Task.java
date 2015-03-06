package sim.app.bounties;

import sim.app.bounties.agent.IAgent;
import ec.util.MersenneTwisterFast;
import java.awt.Color;
import java.util.Arrays;
import sim.app.bounties.LogNormalDist.LogNormalDist;
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
    private Goal goal;
    private Color availableColor = Color.RED;// may want to change color if we have different types of tasks
    private Color notAvailableColor = Color.WHITE;
    private int requiredRobots = 1;
    private Bag presentRobots = new Bag();
    private int lastFinishedRobotID = -1;//hopefully this wont cause a runtime error... who last finished the task by default set to -1;
    private int perAgentReward[]; // the reward that is individualized for each agent like if they jumpship this might not be 0
    private long finishedTime = -1;
    private Bag lastAgentsWorkingOnTask; // these are the agents working on the task when someone finished it
    private int timeUntilRespawn = 0;
    private MersenneTwisterFast rand = null; 
    public int badForWho = -1;
    public LogNormalDist incr ;
    Bounties hackItIn = null; //this is so we can hack in the graphics
    private Task() {}
    public Task(int numAgents, MersenneTwisterFast rand, Bounties hack) {
        incr= new LogNormalDist(0,2,rand);
        perAgentReward = new int[numAgents];
        Arrays.fill(perAgentReward, defaultReward);
        currentReward = defaultReward;
        this.rand = rand;
        hackItIn = hack;
        lastAgentsWorkingOnTask = new Bag();
    }
    
    public void setRandom(MersenneTwisterFast rand){
        this.rand = rand;
    }
    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    public Goal getGoal() {
        return goal;
    }
    public int getRequiredRobots(){
        return requiredRobots;
    }
    public void setRequiredRobots(int required){
        requiredRobots = required;
    }
    /**
     * kind of inefficient..... order n in number of required robots. if larger
     * sizes then could use hash... or change how all irobots claim they are present
     * 
     * Check to see if robot has already claimed he is at the task.
     * @param a  is a robot
     * 
     * 
     */
    public void addRobot(IAgent a){
        if(presentRobots.contains((Object)a))
            return;
        presentRobots.add(a);
    }
    public void subtractRobot(IAgent a){
        presentRobots.remove((Object)a);
    }
    public boolean isEnoughRobots(){
        return presentRobots.numObjs == requiredRobots;
    }
    public boolean isDone(){
        return done;
    }
    public void setDone(boolean val){
        if(val == true && done == false){
            changeTaskLocation();
        }
        if(val==false && done == true){
            makeRespawnTime();
        }
        done = val;
    }
    public void changeTaskLocation(){
        if(rand.nextInt(10)==0){
            badForWho = rand.nextInt(hackItIn.numAgents);
        }//else{
        //    badForWho = -1;
        //}
        int newX = initialLocation.x + (int)Math.round(((rand.nextGaussian()) * 5));
        int newY = initialLocation.y + (int)Math.round(((rand.nextGaussian()) * 5));
        //System.err.println(initialLocation.x);
        realLocation = new Int2D(newX,newY);
        this.hackItIn.tasksGrid.setObjectLocation(this, realLocation);
    }
    public boolean isTaskReady(){//check to see if this is finally time to spawn.
        //if(isDone()==true) return true; // this makes it so that it spawns immediatly since done is true whenever the task is finished
   
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
            currentReward+=requiredRobots;
    }
    
    /**
     * Use this not the getCurrentReward if reward may be dependent on robot
     * @param robot
     * @return 
     */
    public int getCurrentReward(IAgent robot) {
        //return defaultReward;
       
        if (perAgentReward[robot.getId()] > defaultReward)
        {
            return perAgentReward[robot.getId()];
        }
        return getCurrentReward();
    }
    
    public void setCurrentReward(IAgent robot, int reward) {
        perAgentReward[robot.getId()] = reward;
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
    public Int2D getLocation() {
        return realLocation;
    }
    public void setLoc(Int2D loc) {
        this.initialLocation = loc;
        changeTaskLocation();
    }
    
    public Int2D getInitialPosition() {
        return this.initialLocation;
    }

    
    public int getNumRobotsDoingTask() {
        return presentRobots.numObjs;
    }

    void resetReward() {
        currentReward = defaultReward;
        Arrays.fill(perAgentReward, defaultReward);//reset everyone's agent specific reward
    }
    
    void resetReward(int reward) {
        currentReward = reward;
        Arrays.fill(perAgentReward, defaultReward);//reset everyone's agent specific reward
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
