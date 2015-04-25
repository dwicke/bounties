/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.agent.valuator;

import ec.util.MersenneTwisterFast;
import sim.app.bounties.environment.Task;
import sim.util.Bag;

/**
 *
 * @author drew
 */
public class SeanAuctionValuator extends LearningValuator implements DecisionValuator{
    private static final long serialVersionUID = 1;
    SeanAuctionValuator[] auctionValuators;

    public SeanAuctionValuator(MersenneTwisterFast random, double epsilonChooseRandomTask, int agentID, boolean hasOneUp, int numTasks, int numRobots) {
        super(random, epsilonChooseRandomTask, agentID, hasOneUp, numTasks, numRobots);
    }
    
    
    public void setAuctionCompetitors(SeanAuctionValuator[] bots) {
        this.auctionValuators = bots;
    }
    
    
    
    public double[] getEvaluations(Task[] availTasks){
       
        
        double[] evaluations = new double[availTasks.length];
        for (int i = 0; i < availTasks.length; i++) { // over all tasks
            double tval = timeTable.getQValue(((Task)availTasks[i]).getID(), 0);
            double pval = pTable.getQValue(((Task)availTasks[i]).getID(), 0);
            double value = 1.0/tval * pval*((Task)availTasks[i]).getCurrentReward();
            if  (isDead == true){
                    value*=-1;
            }
            evaluations[i] = value;
            
        }
        
        return evaluations;
    }
    
    

    @Override
    public Task pickTask(Task[] availableTasks){
        
        
        Task curTask = null;
        double max = 0; 
        int robotWinner = -1;
        int robotIndex;
        
        
        int loopCount = 0;
        double[][] evaluations = new double[auctionValuators.length][];
        for(int a = 0; a<auctionValuators.length; a++){
            
                evaluations[a] = auctionValuators[a].getEvaluations(availableTasks);
        }
        
        while(robotWinner != this.agentID && loopCount<auctionValuators.length){
            max  = 0;
            robotWinner = -1;
            Task taskWon = null;
            int taskIndex = -1;
            robotIndex = -1;
            
            for(int a = 0; a<auctionValuators.length; a++){
                for (int i = 0; i < availableTasks.length; i++) { // over all tasks
                     double temp = evaluations[a][i];
                     if(temp > max){
                         robotWinner = auctionValuators[a].agentID;
                         max = temp;
                         robotIndex = a;
                         taskWon = (Task)availableTasks[i];
                         taskIndex = i;
                     
                  }
                }
            }
          if(robotWinner == this.agentID && max > 0){
             // System.err.println(" got a cur task at index " + taskIndex);
              curTask = taskWon;
              break;
          }else{
              if(robotIndex != -1 ){

                auctionValuators[robotIndex] = null;
                availableTasks[taskIndex] = null;
              }else{
                 // System.err.println("okay NOW you can shit your pants " + availTasks.numObjs);
              }
          }
          loopCount++;
        }
        
        return curTask;
        
        
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
