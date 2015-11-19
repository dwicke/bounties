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
public class BountyRAuctionValuator extends BountyAuctionValuator {
    
    
    public BountyRAuctionValuator(MersenneTwisterFast random, double epsilonChooseRandomTask, int agentID, boolean hasOneUp, int numTasks, int numRobots) {
        super(random, epsilonChooseRandomTask, agentID, hasOneUp, numTasks, numRobots);
        
    }
    
    
    public void setAuctionCompetitors(BountyRAuctionValuator[] bots) {
        this.auctionValuators = bots;
    }    
    
    /**
     * What is my "bid" for each of the available tasks in the system
     * @param availTasks
     * @return 
     */
    @Override
    public double[] getEvaluations(Task[] availTasks){
       
        //System.err.println("Agent id" + agentID);
        double[] evaluations = new double[availTasks.length];
        for (int i = 0; i < availTasks.length; i++) { // over all tasks
            double tval = timeTable.getQValue(availTasks[i].getID(), 0);
            double incRate = incrementRateTable.getQValue(availTasks[i].getID(), 0);
            
            //System.err.println("Increment rate = " + incRate);
           /* if (tval > numTimeSteps) {
                tval -= numTimeSteps;
            }*/
            double curReward = availTasks[i].getCurrentReward();
            if (this.preTask != null && this.preTask.getID() != availTasks[i].getID()) {
                curReward = availTasks[i].getCurrentReward() - this.preTask.getCurrentReward();
                if (curReward < 0) {
                    curReward = 0;
                }
            }
            double pval = pTable.getQValue(((Task)availTasks[i]).getID(), 0);;
            double value = 1.0 / tval * pval * (curReward + tval*incRate);
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

    
    
}
