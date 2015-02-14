/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.auctionstuff;

import sim.app.bounties.Task;
import sim.app.bounties.auctionstuff.AuctionAgent;
import sim.util.Bag;
import sim.util.Int2D;

/**
 * This bids the time to get to the expected location of the task based off of its given mean location
 * @author drew
 */
public class NaiiveAuctionAgent extends AuctionAgent {

    public NaiiveAuctionAgent(int numTaskClasses) {
        
    }
    @Override
    public double getBid(Task t) {
        
        return getCost(t);
    }

    @Override
    public void learn() {
        
    }
    
    public double getCost(Task t) {
        Int2D taskLoc =  t.getInitialPosition();// the estimated location for this task class
        return taskLoc.manhattanDistance(home);// where I will always start from
    }
    
    
}
