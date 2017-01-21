/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.agent.valuator;

import java.util.PriorityQueue;

import ec.util.MersenneTwisterFast;
import sim.app.bounties.environment.Task;
import sim.app.bounties.util.QTable;

/**
 *
 * @author drew
 */
public abstract class LearningValuator extends DefaultValuator implements DecisionValuator {
    private static final long serialVersionUID = 1;    
    QTable timeTable; // time to do task
    QTable pTable; // probablility that I am successful at a task
    double oneUpdateGamma = .001;
    double tTableLearningRate = .1; // set to .1 originally
    double tTableDiscountBeta = .1; // not used...
    double pTableLearningRate = .2; // set to .2 oritinally
    double pTableDiscountBeta = .1; // not used...
    double initValue = 1;
    boolean hasOneUp;
//    int numTimeSteps;
    QTable incrementRateTable;
    
    public LearningValuator(MersenneTwisterFast random, double epsilonChooseRandomTask, 
            int agentID, boolean hasOneUp, int numTasks, int numRobots){
        super(random, epsilonChooseRandomTask, agentID);
        this.hasOneUp = hasOneUp;
        timeTable = new QTable(numTasks, 1, tTableLearningRate, tTableDiscountBeta, initValue); 
        pTable = new QTable(numTasks, numRobots, pTableLearningRate, pTableDiscountBeta, initValue);        
        incrementRateTable = new QTable(numTasks, 1, tTableLearningRate, tTableDiscountBeta, initValue); 
    }
    
 
    /**
     * 
     * @param tasks 
     */
    @Override
    public void learnIncrementRate(Task[] tasks) {
        for (Task task : tasks) {
            //System.err.println("Defaul reward" + task.getDefaultReward() + "Difference = " + (task.getCurrentReward() - task.getLastReward()) + " current: " + task.getCurrentReward() + " last: " + task.getLastReward());
            
            if ((task.getCurrentReward() - task.getLastReward()) != 0) {
                incrementRateTable.update(task.getID(), 0, task.getCurrentReward() - task.getLastReward());
            }
        }
    }
    
    
    public void setOneUpdateGamma(double oneUpdateGamma) {
        this.oneUpdateGamma = oneUpdateGamma;
    }
//    public void setNumTimeSteps(int numTimeSteps) {
//        this.numTimeSteps = numTimeSteps;
//    }
    
    class TaskValuePair implements Comparable<TaskValuePair>{
    	double value;
    	Task task;
    	
    	public TaskValuePair(double value, Task t) {
			this.value = value;
			this.task = t;
		}

		@Override
		public int compareTo(TaskValuePair o) {
			if(this.value <  o.value)
				return 1;
			else if(this.value > o.value)
				return -1;
			return 0;
		}
    }
    @Override
    Task pickTask(Task availableTasks[], Task unavailableTasks[]) {
        double max = -1;
        Task curTask = null;
        PriorityQueue<TaskValuePair> queue = new PriorityQueue<TaskValuePair>();
        double curTVal = 10000.0;
        for (Task availTask : availableTasks) {
            // over all tasks
            double tval = timeTable.getQValue(availTask.getID(), 0);
            double pval = getPValue(availTask);
            double value = 1.0 / tval * pval * availTask.getCurrentReward();
            if (value > max) {
                max = value;
                curTask = availTask;
                curTVal = tval;
            }
            
        }
        /*
        Task curUnTask = null;
        for (Task unavailTask : unavailableTasks) {
            // over all tasks
            double tval = unavailTask.getLocation().manhattanDistance(curLoc);//timeTable.getQValue(unavailTask.getID(), 0);
           
            double pval = getPValue(unavailTask);
            double value = 1.0 / tval * pval * (unavailTask.getCurrentReward() + tval);
            //if (value > max && 10.0 < curTVal -curTask.getTimeNotFinished()) {
            if (value > max && 30.0 < curTVal ) {
                max = value;
                curUnTask = unavailTask;
            }
        }
        if (curUnTask != null) {
            System.err.println("I want to wait for an unavailable task");
            return null;
        }
                */
        return curTask;
    }
    @Override
    Task pickTask(Task availableTasks[]) {
        double max = -1;
        Task curTask = null;
        PriorityQueue<TaskValuePair> queue = new PriorityQueue<TaskValuePair>();

        for (Task availTask : availableTasks) {
            // over all tasks
            double tval = timeTable.getQValue(availTask.getID(), 0);
            double pval = getPValue(availTask);
            double value = 1.0 / tval * pval * availTask.getCurrentReward();
            if (value > max) {
                max = value;
                curTask = availTask;
            }
            
        }
        
        return curTask;
//		for (Task availTask : availableTasks) {
//			// over all tasks
//			double tval = timeTable.getQValue(availTask.getID(), 0);
//			double pval = getPValue(availTask);
//			double value = 1.0 / tval * pval * availTask.getCurrentReward();
//			queue.add(new TaskValuePair(value, availTask));
//		}
//		ArrayList<Task> candidateList = new ArrayList<Task>();
//		for (int i = 0; i < 3; ++i) {
//			candidateList.add(queue.poll().task);
//		}
//
//		int index = random.nextInt(candidateList.size());
//		return candidateList.get(index);
        
    }
    
    abstract double getPValue(Task availTask);
}
