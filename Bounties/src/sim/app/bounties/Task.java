package sim.app.bounties;

import ec.util.MersenneTwisterFast;
import java.awt.Color;
import sim.app.bounties.util.Real;
import sim.portrayal.Fixed2D;
import sim.util.Bag;
import sim.util.Double2D;
import sim.util.Int2D;

/**
 *
 * @author dfreelan
 */
public class Task implements Real, Fixed2D{
    private static final long serialVersionUID = 1;

    private int currentReward = 0; // controlled by bondsman to increase
    private int lastRewardPaid = 0;
    private boolean done = false; // true when at the goal false otherwise
    private boolean available = true;// true when a robot is not carrying and not at a goal it is false if not at the
    public Int2D realLocation;// location
    public Int2D initialLocation;
    
    private int id = 0;
    private int defaultReward = 100;
    private Color availableColor = Color.RED;// may want to change color if we have different types of tasks
    private Color notAvailableColor = Color.WHITE; // make it disappear
    
    private int lastFinishedRobotID = -1;// who last finished the task by default set to -1;
    private long finishedTime = -1;
    private Bag lastAgentsWorkingOnTask; // these are the agents working on the task when someone finished it
    private int timeUntilRespawn = 0;
    
    public int badForWho = -1;
    public int maxRespawnTime = 20;
    
    public final double taskStdDev = 5.0;
    
    public Task() {
        currentReward = defaultReward;
        lastAgentsWorkingOnTask = new Bag();
    }
    
    
    public boolean isDone(){
        return done;
    }
    public void setDone(boolean val){
        done = val;
    }
    
    public void generateRealTaskLocation(MersenneTwisterFast rand){
        int newX = initialLocation.x + (int)Math.round(((rand.nextGaussian()) * taskStdDev));
        int newY = initialLocation.y + (int)Math.round(((rand.nextGaussian()) * taskStdDev));
        realLocation = new Int2D(newX,newY);
    }
    
    /**
     * id of agent the task will be harder for or -1 if not harder for anyone.
     * @param agentID 
     */
    public void setBadForWho(int agentID) {
        badForWho = agentID;
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
    public void makeRespawnTime(MersenneTwisterFast rand){
        timeUntilRespawn = rand.nextInt(maxRespawnTime); // use uniform since we want them to come back within a reasonable time... 
    }
    public int getTimeUntilRespawn() {
        return timeUntilRespawn;
    }
    public void setAvailable(boolean available) {
        this.available = available;
    }
    public boolean getIsAvailable() {
        return available;
    }
    
    public void setCurrentReward(int reward) {
        currentReward = reward;
    }
    public void incrementCurrentReward() {
        if (currentReward < 10000000 && isDone() == false)// don't increment while the task is done 
            currentReward+=1;
    }
    
    public int getDefaultReward() {
        return defaultReward;
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

    
    public int getLastReward() {
        return lastRewardPaid;
    }

    public void resetReward() {
        lastRewardPaid = currentReward;
        currentReward = defaultReward;
    }
    
    public void resetReward(int reward) {
        currentReward = reward;
    }

    @Override
    public Double2D getRealTargetLocation()
    {
        return new Double2D((realLocation.x - Bounties.GRID_WIDTH/2) * 0.1, (realLocation.y - Bounties.GRID_HEIGHT/2) * 0.1);
    }
   
    @Override
    public double getOrientation() { return 0; }

    @Override
    public boolean maySetLocation(Object field, Object newObjectLocation) {
        
        initialLocation = (Int2D) newObjectLocation;
        System.err.println("Set new location to: " + initialLocation.toCoordinates());
        realLocation = initialLocation;
        return true;
    }

    public Color getNotAvailableColor() {
        return notAvailableColor;
    }

    public Color getAvailableColor() {
        return availableColor;
    }
}
