package sim.app.bounties.environment;

import ec.util.MersenneTwisterFast;
import java.awt.Color;
import sim.app.bounties.Bounties;
import sim.app.bounties.agent.IAgent;
import sim.app.bounties.ra.resource.Resource;
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

    private double lastReward = 0;
    private double currentReward = 0; // controlled by bondsman to increase
    private double lastRewardPaid = 0;
    private double totalRewardPaidOut = 0.0; // total reward paid out by this task
    private boolean done = false; // true when at the goal false otherwise
    private boolean available = true;// true when a robot is not carrying and not at a goal it is false if not at the
    public Int2D realLocation;// location
    public Int2D initialLocation;
    
    private int id = 0;
    private double defaultReward = 100;
    private Color availableColor = Color.RED;// may want to change color if we have different types of tasks
    private Color notAvailableColor = Color.WHITE; // make it disappear
    
    private int lastFinishedRobotID = -1;// who last finished the task by default set to -1;
    private long finishedTime = -1;
    private Bag lastAgentsWorkingOnTask; // these are the agents working on the task when someone finished it
    private Bag currentAgentsWorkingOnTask; // these are the agents working on the task now
    private Bag agentsAtTask; // the agents who are at the task
    private int numAgentsNeeded = 1; // the number of agents needed at the task to complete it
    private int timeUntilRespawn = 0;
    private int completeCounter = 0;
    private int totalTimeWaiting = 0; // this is the total time spent available and not completed over all time
    
    public int badForWho = -1;
    public int maxRespawnTime = 20;
    
    public final double taskStdDev = 5.0;
    
    public double timeNotFinished = 0;// the amount of time this task has been waiting to be finished.
    
    private double curNumResourcesNeeded = 0; // this is the number of resources currently needed to complete the task
    private double numResourcesNeeded = 0; // number of resources needed to finish this task (incremented up to max)
    private double maxNumResourcesNeeded = 0; // the max number of resources will ever need
    private int incrementResourceAt = 20; // every x times the task is completed the numResourcesNeeded is incremented
    private Resource resource;
    
    
    public boolean isNonExclusive = true;

    public TaskBlock myblock;
    
    
    public Task() {
        currentReward = defaultReward;
        lastReward = defaultReward;
        lastAgentsWorkingOnTask = new Bag();
        currentAgentsWorkingOnTask = new Bag();
        agentsAtTask = new Bag();
    }

    
    public TaskBlock getTaskBlock() {
        return myblock;
    }
    
    public void setTaskBlock(TaskBlock block) {
        myblock = block;
    }
    
    public double getLastReward() {
        return lastReward;
    }

    public void setLastRewardPaid(double lastRewardPaid) {
        this.lastRewardPaid = lastRewardPaid;
    }
    
    public void setCurNumResourcesNeeded(double needed) {
        curNumResourcesNeeded = needed;
    }
    
    public double getNumResourcesNeeded() {
        return numResourcesNeeded;
    }
    
    public double getCurNumResourcesNeeded() {
        return curNumResourcesNeeded;
    }
    
    public void setResource(Resource r) {
        resource = r;
    }
    
    public Resource getResource() {
        return resource;
    }
    
    public void setCompleteCounter(int val) {
    	this.completeCounter = val;
        if (this.completeCounter % incrementResourceAt == 0 && numResourcesNeeded < maxNumResourcesNeeded) {
            numResourcesNeeded++;
        }
    }
    
    public int getCompleteCounter() {
    	return this.completeCounter;
    }
    
    public void setDefaultReward(double defaultReward) {
        this.defaultReward = defaultReward;
    }
    
    /**
     * How many agents are needed to be at the task for it to be marked complete
     * @param numNeeded 
     */
    public void setNumAgentsNeeded(int numNeeded) {
        this.numAgentsNeeded = numNeeded;
    }
    public void setCurrentAgentsOnTask(Bag currentAgentsWorkingOnTask) {
        this.currentAgentsWorkingOnTask = currentAgentsWorkingOnTask;
    }
    public Bag getCurrentAgentsOnTask() {
        return this.currentAgentsWorkingOnTask;
    }
    public Bag getAgentsAtTask() {
        return this.agentsAtTask;
    }    
    
    public void addAgentAtTask(IAgent agent) {
        if (!this.agentsAtTask.contains(agent)) {
            this.agentsAtTask.add(agent);
            //this.setCurrentReward(this.currentReward + this.defaultReward);
        }
    }
    
    public boolean agentAtTask(IAgent agent) {
        return this.agentsAtTask.contains(agent);
    }
    
    public boolean removeAgentAtTask(IAgent agent) {
        return this.agentsAtTask.removeNondestructively(agent);
    }
    
     public void removeAllAgentAtTask() {
        this.agentsAtTask.clear();
    }
    
    public boolean getAreAllPresent() {
        return this.agentsAtTask.numObjs == getnumAgentsNeeded();
    }
    
    public int getNumAgentsAtTask() {
        return this.agentsAtTask.numObjs;
    }
    
    public int getnumAgentsNeeded() {
        return numAgentsNeeded;
    }
    
    public void setIsNonExclusive(boolean isNonExcl) {
        this.isNonExclusive = isNonExcl;
    }
    
    public int getIsNonExclusive() {
        return this.isNonExclusive == true ? 1 : 0;
    }
    public void incrementTimeNotFinished() {
        timeNotFinished++;
    }
    public double getTimeNotFinished() {
        return timeNotFinished;
    }
    public void resetTimeNotFinished() {
        totalTimeWaiting += timeNotFinished;
        timeNotFinished = 0;
    }
    
    public double getAverageCompletionTime() {
        return ((double)totalTimeWaiting) / ((double)completeCounter);
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
    
    public void decrementReady() {
        timeUntilRespawn--;
    }
    
    public boolean isTaskReady(){//check to see if this is finally time to spawn.
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
    public void setRespawnTime(int spawnTime) {
        timeUntilRespawn = spawnTime;
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
    
    public void setCurrentReward(double reward) {
        lastReward = currentReward;
        currentReward = reward;
    }
    public void incrementCurrentReward() {
        if (currentReward < 10000000 && isDone() == false)// don't increment while the task is done 
        {
            lastReward = currentReward;
            currentReward+=1.0;
        }
    }
    public void incrementCurrentReward(double incrementAmount) {
        if (currentReward < 10000000 && isDone() == false)// don't increment while the task is done 
        {
            lastReward = currentReward;
            currentReward+=incrementAmount;
            //System.err.println("current reward = " + currentReward + " last reward = " + lastReward + " incrAmt = " + incrementAmount);
        }
    }
    public double getDefaultReward() {
        return defaultReward;
    }
    
    public double getCurrentReward(){
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

    
    public double getLastRewardPaid() {
        return lastRewardPaid;
    }
    
    public double getTotalRewardPaidOut() {
        return totalRewardPaidOut;
    }
    
    public double getAverageRewardPaidOut() {
        return totalRewardPaidOut / ((double) completeCounter);
    }

    public void resetReward() {
        totalRewardPaidOut += currentReward;
        lastRewardPaid = currentReward;
        currentReward = defaultReward;
        lastReward = defaultReward;
    }
    
    public void resetReward(double reward) {
        totalRewardPaidOut += currentReward;
        lastRewardPaid = currentReward;
        currentReward = reward;
        lastReward = reward;
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
