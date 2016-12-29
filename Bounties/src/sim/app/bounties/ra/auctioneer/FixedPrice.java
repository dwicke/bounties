/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.ra.auctioneer;

import sim.app.bounties.Bounties;
import sim.app.bounties.agent.IAgent;
import sim.app.bounties.ra.resource.Resource;
import sim.app.bounties.ra.resource.ResourceType;
import sim.app.bounties.ra.resource.TaskResource;
import sim.engine.SimState;

/**
 * This is the most simple auction.  The price is the reserved price and that is
 * the amount the bouty hunter must pay in order to obtain the resource.
 * 
 * There is an unlimited supply of the resource.
 * @author drew
 */
public class FixedPrice implements Auctioneer{
    private final Bounties bountyState;

    private double bids [];
    private Resource res;
    private double profit = 0.0;
    public FixedPrice(SimState bounties, Resource res) {
        bountyState = (Bounties) bounties;
        bids = new double[bountyState.numAgents];
        this.res = res;
    }
    
    @Override
    public void collectBids() {
       // System.err.println("I am collecting bids yooo");
        
        // loop over the bounty hunters and ask them for there bid
        for (IAgent a: bountyState.getAgents()) {
            bids[a.getId()] = a.getResourceBid(res);
        }
        
        
    }

    @Override
    public void clearAuction() {
        for (int i = 0; i < bids.length; i++) {
            if (bids[i] > 0) {
                bountyState.getAgents()[i].receiveResource(res);
                profit += bids[i] * res.getReservePrice();
                bids[i] = 0; // reset there bid!!!
            }
        }
    }

    @Override
    public double getProfit() {
        return profit;
    }
    
    
    
    
}
