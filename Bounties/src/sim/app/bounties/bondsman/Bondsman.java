/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.bounties.bondsman;

import sim.app.bounties.agent.IAgent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import sim.app.bounties.Bounties;
import sim.app.bounties.environment.Task;
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
    
    public Bondsman(){
    }

    /**
     * 
     * @param bounties the state
     * @param exclusiveType 0 if not exclusive, 1 if exclusive, 2 if bondsman decided
     */
    public Bondsman(Bounties bounties, int exclusiveType) {
        this.exclusiveType = exclusiveType;
        this.bounties = bounties;
        whosDoingWhatTaskID = new int[this.bounties.numAgents];
        // set everyone to do task -1 since not doing anytask
        Arrays.fill(whosDoingWhatTaskID, -1);
    }
    
    @Override
    public void step(SimState state) {
        makeAvailable();
        incrementBounty(); // increment the bounties
        incrementExistence();
        calculateGiniIndex();
    }
    
    private void calculateGiniIndex() {
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
	}

	public void incrementBounty(){
        for(int i = 0; i< tasks.size(); i++){
           // if (((Task) tasks.objs[i]).getIsAvailable()) // only increment the 
                ((Task)tasks.objs[i]).incrementCurrentReward();
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
                if(((Task) tasks.objs[i]).isTaskReady()) {
                    // need to decide whether to make it exclusive or not
                    if (exclusiveType == 2)
                        decideExclusivity(((Task) tasks.objs[i]));
                    // need to reset the task and make it available again
                    ((Task) tasks.objs[i]).setAvailable(true);
                    ((Task) tasks.objs[i]).setDone(false);
                    ((Task) tasks.objs[i]).makeRespawnTime(bounties.random);
                        
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
        for (int i = 0; i < bounties.numTasks; i++) {
            Task t = new Task();
            t.setID(i);
            t.setInitialLocation(new Int2D(bounties.random.nextInt(field.x), bounties.random.nextInt(field.y)));
            t.generateRealTaskLocation(bounties.random);
            bounties.tasksGrid.setObjectLocation(t, t.realLocation);
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
    
    
    
    /**
     * Finishes the task
     * @param curTask what task was finished
     * @param robotID who finished it
     * @param timestamp when did you finish it
     */
    public void finishTask(Task curTask, int robotID, long timestamp, int numTimeSteps) {
        curTask.setLastFinished(robotID, timestamp);
        curTask.setAvailable(false); // whenever an agent finishes a task then make it unavailable
        curTask.setDone(true);
        curTask.resetTimeNotFinished();
        // You stay bad at it until someone else becomes bad at it.
        // this should have been more clearly stated in the paper.
        if(bounties.random.nextInt(badOdds)==0){
            curTask.setBadForWho(bounties.random.nextInt(bounties.numAgents));
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
    
}
