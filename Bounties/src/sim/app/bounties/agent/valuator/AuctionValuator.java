/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.agent.valuator;

import ec.util.MersenneTwisterFast;
import java.util.Collections;
import sim.app.bounties.environment.Task;
import sim.util.Bag;

/**
 *
 * @author drew
 */
public class AuctionValuator extends LearningValuator implements DecisionValuator{
    AuctionValuator[] auctionValuators;
    
    
    public AuctionValuator(MersenneTwisterFast random, double epsilonChooseRandomTask, int agentID, boolean hasOneUp, int numTasks, int numRobots) {
        super(random, epsilonChooseRandomTask, agentID, hasOneUp, numTasks, numRobots);
    }
    
    
    public void setAuctionCompetitors(AuctionValuator[] bots) {
        this.auctionValuators = bots;
    }    
    
    /**
     * What is my "bid" for each of the available tasks in the system
     * @param availTasks
     * @return 
     */
    public double[] getEvaluations(Task[] availTasks){
       
        //System.err.println("Agent id" + agentID);
        double[] evaluations = new double[availTasks.length];
        for (int i = 0; i < availTasks.length; i++) { // over all tasks
            double tval = timeTable.getQValue(((Task)availTasks[i]).getID(), 0);
            double pval = pTable.getQValue(((Task)availTasks[i]).getID(), 0);
            double value = 1.0/tval * pval *((Task)availTasks[i]).getCurrentReward();
            
            if  (isDead == true){
                    value*=-1;
            }
            evaluations[i] = value;
            //System.err.print(value + ", ");
        }
        
        //System.err.println("\n-----------");
        
        return evaluations;
    }
    
    
    @Override
    public Task pickTask(Task[] availableTasks){
        
        // basically 
        // merge the valuations from all agents
        // find max
        // if index of max / numTasks == agentID then we have found the task we want
        // then its just a matter of index of max % numTasks
        
        // if it is not this agent's id then note the agent and the task id
        // remove that agent's valuations by setting all of its valuations to -Max
        // then set all of the agent valuations for that task to -MAX 
        
        double[][] valuations = new double[auctionValuators.length][availableTasks.length];
        //System.err.println("Num avail tasks = " + availableTasks.length);
        // for each agent get their valuation
        for (int i = 0; i < auctionValuators.length; i++) {
            valuations[i] = auctionValuators[i].getEvaluations(availableTasks);// agent id corresponds to agent's index.
        }
        
        
        for (int i = 0; i < availableTasks.length; i++) {
            AgentTaskPair max = getAssignment(valuations);
            if (max.agentID == this.agentID) {
                return availableTasks[max.taskID];
            }
            valuations = getNewValuations(valuations, max);
        }
        System.err.println("ERRRR  no task found for agent " + agentID);
        return null;// this should never happen and will result in the agent not going after a task!
    }
    
    /**
     * find the agent that has the highest valuation for a task
     * if duplicates pick randomly.
     * @param valuations
     * @return 
     */
    AgentTaskPair getAssignment(double[][] valuations) {
        
        
        AgentTaskPair[] duplicates = new AgentTaskPair[valuations.length];
        for (int i = 0; i < duplicates.length; i++) {
            duplicates[i] = new AgentTaskPair();
        }
        int curDup = 0;
        for (int i = 0; i < valuations.length; i++) { // loop over the agents
            for (int j = 0; j < valuations[i].length; j++) { // loop over the tasks
                if (valuations[i][j] > duplicates[0].valuation) {
                    // clear the duplicates
                    for (int k = 0; k < duplicates.length; k++) {
                        duplicates[k].reset();
                    }
                    curDup = 0;
                    duplicates[curDup].agentID = i;
                    duplicates[curDup].taskID = j;
                    duplicates[curDup].valuation = valuations[i][j];
                    curDup = 1;
                } else if (valuations[i][j] == duplicates[curDup].valuation && valuations[i][j] > 0) {
                    // then we have a duplicate!
                    duplicates[curDup].agentID = i;
                    duplicates[curDup].taskID = j;
                    duplicates[curDup].valuation = valuations[i][j];
                    curDup++;
                }
            }
        }
        
        // now randomly pick from the duplicate set
        return duplicates[random.nextInt(curDup)];
    }
    
    
    void printValuations(double[][] valuations) {
        for (int i = 0; i < valuations.length; i++) {
            System.err.println("Agent id = " + i);
            for (int j = 0; j < valuations[i].length; j++) {
                System.err.print(valuations[i][j] + ", ");
            }
            System.err.println("\n-----------");
        }
        
    }
    
    /**
     * given current valuations return new valuations that have the agent removed
     * and the task that was set to negative max
     * @param valuations
     * @param max
     * @return 
     */
    double[][] getNewValuations(double[][] valuations, AgentTaskPair max) {
        
        for (int i = 0; i < valuations.length; i++) { // for each agent
            // only cancel out the task that has been selected
            valuations[i][max.taskID] = Double.NEGATIVE_INFINITY;
        }
        
        // then cancel out the agent that has won
        for (int j = 0; j < valuations[max.agentID].length; j++) {
            valuations[max.agentID][j] = Double.NEGATIVE_INFINITY;
        }
        return valuations;
    }
    
    class AgentTaskPair {
        int agentID, taskID;
        double valuation;

        public AgentTaskPair() {
            agentID = -1;
            taskID = -1;
            valuation = -1;
        }

        public AgentTaskPair(int agentID, int taskID, double valuation) {
            this.agentID = agentID;
            this.taskID = taskID;
            this.valuation = valuation;
        }
        
        public void reset() {
            agentID = -1;
            taskID = -1;
            valuation = -1;
        }
    }
    
    
    @Override
    public void learn(Task curTask, double reward, Bag agentsWorking, int numTimeSteps) {
         if(reward == 1.0) {
            timeTable.update(curTask.getID(), 0, numTimeSteps);
            pTable.update(curTask.getID(), 0, reward);
        }else{
            pTable.update(curTask.getID(), 0, reward);
        }
        if (this.hasOneUp)
                pTable.oneUpdate(oneUpdateGamma);
        
    }

    @Override
    double getPValue(Task availTask) {
        throw new UnsupportedOperationException("Not supported");
    }
    
    
    
    
    
}
