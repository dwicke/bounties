/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.ra.auctioneer;

import sim.app.bounties.Bounties;
import sim.app.bounties.agent.IAgent;
import sim.app.bounties.ra.resource.Resource;
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
    public FixedPrice(SimState bounties) {
        bountyState = (Bounties) bounties;
        bids = new double[bountyState.numAgents];
    }
    
    @Override
    public void collectBids() {
        System.err.println("I am collecting bids yooo");
        
        // loop over the bounty hunters and ask them for there bid
        for (IAgent a: bountyState.getAgents()) {
            bids[a.getId()] = a.getResourceBid(res);
        }
        
        
    }

    @Override
    public void clearAuction() {
        
    }
    
    
    
    
}
