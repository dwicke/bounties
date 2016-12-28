/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.ra.auctioneer;

import sim.app.bounties.Bounties;
import sim.app.bounties.bondsman.Bondsman;
import sim.engine.SimState;
import sim.engine.Steppable;

/**
 *
 * @author drew
 */
public class Auction implements Steppable {
    private static final long serialVersionUID = 1;

    private Bounties bountyState;
    private Auctioneer auctioneer;

    public Auction(SimState bounties, Auctioneer auctioneer) {
        bountyState = (Bounties) bounties;
        this.auctioneer = auctioneer;
    }
    
    
    @Override
    public void step(SimState state) {
        //System.err.println("I am a auction!!!");
        auctioneer.collectBids();
        auctioneer.clearAuction();
    }
    
}
