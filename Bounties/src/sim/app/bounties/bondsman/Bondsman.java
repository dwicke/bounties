/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.bounties.bondsman;

import sim.app.bounties.bondsman.valuator.BondsmanValuator;
import sim.app.bounties.agent.IAgent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import sim.app.bounties.Bounties;
import sim.app.bounties.environment.Task;
import sim.app.bounties.util.SlidingWindowAverage;
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
    public int whosDoingWhatTaskID[];
    Bounties bounties;
    boolean isExclusive[];
    int exclusiveType;
    private final int badOdds = 10;
    public int clumpcnt = 0;
    public double currentAvgNumAgents = 0.0;
    
    BondsmanValuator valuator;
    private int numTasksFinished = 0;
    Bag currentlyCompletedTasks = new Bag();
    
    double totalTraveled = 0.0;
    double totalTimeNotFinished = 0.0;
    
    SlidingWindowAverage swaSpeed = new SlidingWindowAverage(1000);
    
    
    public Bondsman(){
    }

    /**
     * 
     * @param bounties the state
     * @param exclusiveType 0 if not exclusive, 1 if exclusive, 2 if bondsman decided
     * @param valuator mechanism for evaluating how to set the bounty rate
     */
    public Bondsman(Bounties bounties, int exclusiveType, BondsmanValuator valuator) {
        this.exclusiveType = exclusiveType;
        this.bounties = bounties;
        whosDoingWhatTaskID = new int[this.bounties.numAgents];
        // set everyone to do task -1 since not doing anytask
        Arrays.fill(whosDoingWhatTaskID, -1);
        this.valuator = valuator;
    }
    
    @Override
    public void step(SimState state) {
        makeAvailable();
        incrementBounty(); // increment the bounties
        incrementExistence();
        getCalculateGiniIndex();
        getGeneralEntropyIndex(1.0);
    }
    
    public int getNumTasksFinished() {
        return numTasksFinished;
    }
    
    public double getTheilIndex() {
        return getGeneralEntropyIndex(1.0);
    }
    public double getGeneralEntropyIndex(double alpha) {
        
        double entropy = 0.0;
        
        ArrayList<Task> taskList = new ArrayList<Task>();
	taskList.addAll(tasks);
        
        double N = tasks.numObjs;
        double mean = 0.0;
        for (int i =0; i < N; i++) {
            mean += taskList.get(i).getCompleteCounter();
        }
        mean /= N;
        
        if (alpha == 0) {
            for (int i =0; i < N; i++) {
                double comCountOverMean = ((double)taskList.get(i).getCompleteCounter()) / mean;
                entropy +=  Math.log(comCountOverMean);
            }
            entropy *= (-1/N);
        } else if (alpha == 1) {
            for (int i =0; i < N; i++) {
                double comCountOverMean = ((double)taskList.get(i).getCompleteCounter()) / mean;
                entropy += comCountOverMean * Math.log(comCountOverMean);
            }
            entropy *= (1/N);
        } else {
            for (int i =0; i < N; i++) {
                double comCountOverMean = ((double)taskList.get(i).getCompleteCounter()) / mean;
                entropy += Math.pow(comCountOverMean, alpha) - 1;
            }
            entropy *= (1/(N*alpha*(alpha-1)));
        }
        return entropy;
    }
    
    
    public double getCalculateGiniIndex() {
		ArrayList<Task> taskList = new ArrayList<Task>();
		taskList.addAll(tasks);
		
		// first sort the list ascendingly
		Collections.sort(taskList, new Comparator<Task>() {

			@Override
			public int compare(Task o1, Task o2) {
				int counter1 = o1.getCompleteCounter();
				int counter2 = o2.getCompleteCounter();
				
				if(counter1 < counter2) {
					return -1;
				}
				else if(counter1 > counter2){
					return 1;
				}
				else {
					return 0;
				}
			}
		});
		
		double n = taskList.size();
		double sum = 0;
		double denominator = 0;
		for(int i = 0;i<n;++i) {
			double value = (n+1-(i+1))*(double)taskList.get(i).getCompleteCounter();
			sum += value;
			denominator += (double)taskList.get(i).getCompleteCounter();
		}
		
		double g = (n+1-2*sum/denominator)/n;
                return g;
	}
    
    
    
    public void incrementBounty(){
        for(int i = 0; i< tasks.size(); i++){
           // if (((Task) tasks.objs[i]).getIsAvailable()) // only increment the 
           ((Task)tasks.objs[i]).incrementCurrentReward(valuator.getBountyIncrement((Task)tasks.objs[i]));
        }
    }
    public void incrementExistence() {
        for(int i = 0; i< tasks.size(); i++){
            if (((Task) tasks.objs[i]).getIsAvailable())
                ((Task)tasks.objs[i]).incrementTimeNotFinished();
        }
    }
    public void makeAvailable() {
        for (int i = 0; i < tasks.size(); i++) {
            
            if (((Task) tasks.objs[i]).isDone()) {
                ((Task) tasks.objs[i]).decrementReady();
                if(((Task) tasks.objs[i]).isTaskReady()) {
                    
                    
                    // need to decide whether to make it exclusive or not
                    if (exclusiveType == 2)
                        decideExclusivity(((Task) tasks.objs[i]));
                    
                    // need to reset the task and make it available again
                    ((Task) tasks.objs[i]).setAvailable(true);
                    ((Task) tasks.objs[i]).setDone(false);
                    
                    
                    if (i < (bounties.numTasks - bounties.numSpikeTasks)) {
                        ((Task) tasks.objs[i]).makeRespawnTime(bounties.random);
                    }
                    else {
                        ((Task) tasks.objs[i]).setRespawnTime(bounties.spikeRegenRate);
                    }
                    //incrementAmount[((Task)tasks.objs[i]).getID()] = 1;
                        
                }
            }
        }
    }
    public Bag whoseDoingTask(Task b) {
        Bag robots = new Bag();
        // only jumpship robots use this.
        IAgent[] allRobots = (IAgent[]) bounties.getAgents();
        for (int i = 0; i < bounties.numAgents; i++) {
            if (whosDoingWhatTaskID[i] == b.getID()){
                robots.add(allRobots[i]);
            }
        }
        return robots;
    }
    public void decideExclusivity(Task task) {
        if (isExclusive[task.getID()]) {
            // decide if we should change to non-exclusive
            // i think that this might be a
        } else {
            // decide if we should change to exclusive
            
        }
    }
    
  

    
    /**
     * gets the initial tasks
     * @param field the field in where the tasks locations can be
     * @return the tasks
     */
    public Bag initTasks(Int2D field) {
        tasks.clear();
        isExclusive = new boolean[bounties.numTasks];
        for (int i = 0; i < bounties.numTasks - bounties.numSpikeTasks; i++) {
            Task t = new Task();
            t.setID(i);
            t.setInitialLocation(new Int2D(bounties.random.nextInt(field.x), bounties.random.nextInt(field.y)));
            t.generateRealTaskLocation(bounties.random);
            bounties.tasksGrid.setObjectLocation(t, t.realLocation);
            //incrementAmount[i] = bounties.random.nextDouble(true, true) * maxIncrementAmount + minIncrementAmount;
            //incrementAmount[i] = bounties.random.nextInt(maxIncrementAmount) + 1;
            //t.setDefaultReward(bounties.defaultReward);
            valuator.setInitialBounty(t);
            tasks.add(t);
            if (exclusiveType == 2) {
                isExclusive[i] = false;//bounties.random.nextBoolean(); // randomly choose initial state
            } else {
                isExclusive[i] = (exclusiveType == 1);// 1 == exclusive 0 == not exclusive
            }         
        }
        
        // now generate the spike tasks
        for (int i = bounties.numTasks - bounties.numSpikeTasks; i < bounties.numTasks; i++) {
            Task t = new Task();
            t.setID(i);
            t.setInitialLocation(new Int2D(bounties.random.nextInt(field.x), bounties.random.nextInt(field.y)));
            t.generateRealTaskLocation(bounties.random);
            bounties.tasksGrid.setObjectLocation(t, t.realLocation);
            //incrementAmount[i] = bounties.random.nextDouble(true, true) * maxIncrementAmount + minIncrementAmount;
            //incrementAmount[i] = bounties.random.nextInt(maxIncrementAmount) + 1;
            t.setDefaultReward(bounties.spikeBountyValue);
            t.setRespawnTime(bounties.spikeRegenRate);
            t.setAvailable(false);
            t.setDone(true);
            tasks.add(t);
            if (exclusiveType == 2) {
                isExclusive[i] = false;//bounties.random.nextBoolean(); // randomly choose initial state
            } else {
                isExclusive[i] = (exclusiveType == 1);// 1 == exclusive 0 == not exclusive
            }
        }
        
        
        return tasks;
    }
    
    public Bag resetTasks(Int2D field) {

        for (int i = 0; i < bounties.numTasks; i++) {
            Task t = (Task) tasks.objs[i];
            t.setInitialLocation(new Int2D(bounties.random.nextInt(field.x), bounties.random.nextInt(field.y)));
            t.generateRealTaskLocation(bounties.random);
            bounties.tasksGrid.setObjectLocation(t, t.realLocation);
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
            if (((Task) tasks.objs[i]).getIsAvailable() && (isExclusive[i] == false || whoseDoingTaskByID((Task) tasks.objs[i]).isEmpty())) {
                avail[curAv] = (Task) tasks.objs[i];
                curAv++;
            }
        }
        
        Task[] tempAvail = new Task[curAv];
        System.arraycopy(avail, 0, tempAvail, 0, curAv);
        return tempAvail;
    }
    
    public Task[] getUnAvailableTasks() {
        Task[] unavail = new Task[tasks.size()];
        int uncurAv = 0;
        for (int i = 0; i < tasks.size(); i++) {
            if (!(((Task) tasks.objs[i]).getIsAvailable() && (isExclusive[i] == false || whoseDoingTaskByID((Task) tasks.objs[i]).isEmpty()))) {
                unavail[uncurAv] = (Task) tasks.objs[i];
                uncurAv++;
            }
        }
        
        Task[] tempAvail = new Task[uncurAv];
        System.arraycopy(unavail, 0, tempAvail, 0, uncurAv);
        return tempAvail;
    }
    
    
    
    /**
     * Finishes the task
     * @param curTask what task was finished
     * @param robotID who finished it
     * @param timestamp when did you finish it
     */
    public void finishTask(Task curTask, int robotID, long timestamp, int numTimeSteps) {
        numTasksFinished++;
        if (numTimeSteps != curTask.realLocation.manhattanDistance(bounties.agents[robotID].getRobotHome())) {
            // if the task is right on top of the agent the distance is zero but the timesteps is equal to one.
            //System.err.println("NOOOOOOOOOO number of timesteps = " + numTimeSteps + " manhattanDistance = " + curTask.realLocation.manhattanDistance(bounties.agents[robotID].getRobotHome()));
        }
        //double speed = curTask.realLocation.manhattanDistance(bounties.agents[robotID].getRobotHome()) / curTask.timeNotFinished;
        //System.err.println("Agent id = " + robotID + "  Speed = " + speed + " distance = " + curTask.realLocation.manhattanDistance(bounties.agents[robotID].getRobotHome()) + "  time not finished = " + curTask.timeNotFinished);
        
       // if (curTask.getID() == 0) {
            swaSpeed.addValue(curTask.realLocation.manhattanDistance(bounties.agents[robotID].getRobotHome()),curTask.timeNotFinished);
            totalTraveled += curTask.realLocation.manhattanDistance(bounties.agents[robotID].getRobotHome());
            totalTimeNotFinished += curTask.timeNotFinished;
        //}
        
        curTask.setLastFinished(robotID, timestamp);
        curTask.setAvailable(false); // whenever an agent finishes a task then make it unavailable
        curTask.setDone(true);
        curTask.setCurNumResourcesNeeded(bounties.random.nextInt((int) curTask.getNumResourcesNeeded() + 1));
        curTask.resetTimeNotFinished();
        curTask.removeAllAgentAtTask();
        // You stay bad at it until someone else becomes bad at it.
        // this should have been more clearly stated in the paper.
        // really the task should be reset back to being ok for you after
        curTask.setBadForWho(-1); // see how this does.
        if(bounties.random.nextInt(badOdds)==0){
            curTask.setBadForWho(bounties.random.nextInt(bounties.numAgents));
        }
        
        curTask.generateRealTaskLocation(bounties.random);
        bounties.tasksGrid.setObjectLocation(curTask, curTask.realLocation);
        valuator.updateBounty(curTask, numTimeSteps);
        // the reset of new base bounty is very simple and could be tighter... but good for now.
        //curTask.resetReward(curTask.getDefaultReward() * (1 + curTask.getCurNumResourcesNeeded()));
        curTask.resetReward(curTask.getDefaultReward());
        valuator.setInitialBounty(curTask);
        whosDoingWhatTaskID[robotID] = -1;
    }
    
    /**
     * use this when you finish a task and are committing to a new task
     * @param robotID who you are 
     * @param taskID what task you are doing
     */
    public void doingTask(int robotID, int taskID) {
        whosDoingWhatTaskID[robotID] = taskID;
        if (taskID != -1 && exclusiveType == 2) {
            isExclusive((Task) tasks.objs[taskID], robotID);
        }
    }
    
    public void isExclusive(Task t, int a) {
        //// don't do anything.
    }
    
    
    public Bag whoseDoingTaskByID(Task b) {
        Bag robots = new Bag();
        IAgent[] allRobots = (IAgent[]) bounties.getAgents();
        for (int i = 0; i < bounties.numAgents; i++) {
            if (whosDoingWhatTaskID[i] == b.getID()){
                robots.add(allRobots[i].getId());
            }
        }
        return robots;
    }
    
    public void atTask(IAgent agent, Task t) {
        t.addAgentAtTask(agent);
    }
    
    public boolean leaveTask(IAgent agent, Task t) {
        return t.removeAgentAtTask(agent);
    }
    
    public Bag getAgentsAtTask(Task t) {
        return t.getAgentsAtTask();
    }
    
    public boolean areAllPresent(Task t) {
        return t.getAreAllPresent();
    }
    
    public boolean isExclusive(Task task) {
        return isExclusive[task.getID()];
    }
    
    public boolean[] getExclusivity() {
        return isExclusive;
    }
    
    public int[] getWhosDoingWhatTaskID() {
        return whosDoingWhatTaskID;
    }
    
    public int getRunningTotalNumberRedundantAgents() {
        
        int[] nums = whosDoingWhatTaskID.clone();
        Arrays.sort(nums);
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] == nums[i - 1] && nums[i] != -1) {
                clumpcnt++;
                
            }
        }
        
        return clumpcnt;
    }
    
    public double getAvgNumAgentsPerTask() {
        // #tasksServicing / #maxAvail
        
       int[] nums = whosDoingWhatTaskID.clone();
       int unique = 1;
        Arrays.sort(nums);
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] != nums[i - 1] && nums[i] != -1) {
                unique++;
                
            }
        }
        currentAvgNumAgents = (double)unique;
        return currentAvgNumAgents;
    }

    public double getAverageCostPerTimestep() {
        double totalPaid = 0.0;
        double totalWait = 0.0;
        for (int i = 0; i < tasks.numObjs; i++) {
            totalPaid += ((Task) tasks.get(i)).getTotalRewardPaidOut();
            totalWait += ((Task) tasks.get(i)).getTotalTimeWaiting();
        }
        return totalPaid / totalWait; // this is sort of like a $/hr over all of the tasks 
    }
    
    
    public double getAveragePaid() {
        double totalPaid = 0.0;
        double totalCompleted = 0.0;
        for (int i = 0; i < tasks.numObjs; i++) {
            totalPaid += ((Task) tasks.get(i)).getTotalRewardPaidOut();
            totalCompleted += ((Task) tasks.get(i)).getCompleteCounter();
        }
        return totalPaid / totalCompleted;
    }

    public double getAverageCompletionTime() {
        double totalWait = 0.0;
        double totalCompleted = 0.0;
        for (int i = 0; i < tasks.numObjs; i++) {
            totalWait += ((Task) tasks.get(i)).getTotalTimeWaiting();
            totalCompleted += ((Task) tasks.get(i)).getCompleteCounter();
        }
        return totalWait / totalCompleted;
    }
    
    public double getOutstandingWaitTime() {
        // how long in total have the available tasks been waiting to be completed?
        double totalWait = 0.0;
        for (int i = 0; i < tasks.numObjs; i++) {
            if (((Task) tasks.objs[i]).getIsAvailable())
            {
                totalWait += ((Task) tasks.get(i)).getTimeNotFinished();
            }
        }
        return totalWait;
    }
    
    public double getAverageOutstandingWaitTime() {
        // how long in total have the available tasks been waiting to be completed?
        double totalWait = 0.0;
        double numAvailTasks = 0.0;
        for (int i = 0; i < tasks.numObjs; i++) {
            if (((Task) tasks.objs[i]).getIsAvailable())
            {
                totalWait += ((Task) tasks.get(i)).getTimeNotFinished();
                numAvailTasks++;
            }
        }
        return totalWait / tasks.numObjs;
    }

    /*
    // first look at speed
    // speed = d/t  
    // however it is a bit different 
    // distance is the distance from home base to the task of the agent which completed the task
    // time is how long the task has been available.
    // So, basically say it takes 15 units to get to the task but it has been
    // available for 30 time steps and assume all agents move at a unit per timestep
    // so the speed to complete the task was .5
    // 
    // we want to maximize speed.  we want to have speed as close to 1 as possible
    */
    public double getCompletionSpeed() {
        if (totalTimeNotFinished != 0.0) {
            return swaSpeed.getRollingAverage();
            //return totalTraveled / totalTimeNotFinished;
        }
        return 1.0;
    }

    
    
}
