/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties;

import sim.util.Bag;
import sim.util.Int2D;

/**
 * This implements the learning algorithm from that paper.
 * @author drew
 */
public class LearningAuctionAgent extends AuctionAgent {

    Bag taskClasses[];
    double bid;
    int adjHistLength = 4; // the number of past adjustments to keep (3 was recomended in paper)
    double historyWeights[];
    
    public LearningAuctionAgent(int numTaskClasses) {
        taskClasses = new Bag[numTaskClasses];// all are inited to 0 which is what an unknown task bid is.
        historyWeights = new double[] {.2,.3,.5,.5};// there must be as many weights as history length.
       
    }
    
    
    
    @Override
    public double getBid(Task t) {
        
        double estimatedCost = getCost(t);// estimated cost based on known task class.
        double adjustment = getAdjustment(t);// the learned bid adjustment
        bid = estimatedCost + adjustment;
        return bid;
    }

    @Override
    public void learn() {
        double adjust = getNumStepsWorkedOnCurTask() - bid;
        if(taskClasses[curTask.getID()].numObjs == adjHistLength) {
            //then pop off the top
            taskClasses[curTask.getID()].pop();
        }
        taskClasses[curTask.getID()].push(adjust);
        assert taskClasses[curTask.getID()].numObjs <= historySize;
    }
    
    public double getCost(Task t) {
        Int2D taskLoc =  t.getInitialPosition();// the estimated location for this task class
        return taskLoc.manhattanDistance(home);// where I will always start from
    }
    
    public double getAdjustment(Task t) {
        

        if (taskClasses[t.getID()] == null) {
            taskClasses[t.getID()] = new Bag();
            taskClasses[t.getID()].push((double)0.0);
            return 0.0;
        }
        assert taskClasses[curTask.getID()].numObjs <= historySize;
        double adj = 0;
        double den = 0;// need it for the first couple
        for(int i = 0; i < taskClasses[t.getID()].numObjs; i++){
            adj += ( (double) (taskClasses[t.getID()].objs[i])) * historyWeights[i];
            den += historyWeights[i];
        }

        
        
        return adj/den;
    }
    
}
