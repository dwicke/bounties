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
public class RealAuctionValuator extends BountyAuctionValuator{
    
    
    public RealAuctionValuator(MersenneTwisterFast random, double epsilonChooseRandomTask, int agentID, boolean hasOneUp, int numTasks, int numRobots) {
        super(random, epsilonChooseRandomTask, agentID, hasOneUp, numTasks, numRobots);
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
           // double pval = pTable.getQValue(((Task)availTasks[i]).getID(), 0);
            //double value = 1.0/tval * pval *((Task)availTasks[i]).getCurrentReward();
            double value = tval > 0.0 ? 1/tval : 0.0;

            if  (isDead == true){
                    value*=-1;
            }
            evaluations[i] = value;
            //System.err.print(value + ", ");
        }
        
        //System.err.println("\n-----------");
        
        return evaluations;
    }
    
   
    
    
}
