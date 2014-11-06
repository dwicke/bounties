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
    int adjHistLength = 3; // the number of past adjustments to keep (3 was recomended in paper)
    double historyWeights[];
    double weightSum;
    
    public LearningAuctionAgent(int numTaskClasses) {
        taskClasses = new Bag[numTaskClasses];// all are inited to 0 which is what an unknown task bid is.
        historyWeights = new double[] {.2,.3,.5};// there must be as many weights as history length.
        weightSum = 1.0;
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
    }
    
    public double getCost(Task t) {
        Int2D taskLoc =  t.getInitialPosition();// the estimated location for this task class
        return taskLoc.manhattanDistance(home);// where I will always start from
    }
    
    public double getAdjustment(Task t) {
        //return taskClasses[t.getID()];
        
            if (taskClasses[t.getID()] == null) {
                taskClasses[t.getID()] = new Bag();
                taskClasses[t.getID()].push(0);
                return 0;
            }
        
        
        double adj = 0;
        for(int i = 0; i < historySize; i++){
            adj += (double)taskClasses[curTask.getID()].objs[i] * historyWeights[i];
        }
        return adj/weightSum;
    }
    
}
