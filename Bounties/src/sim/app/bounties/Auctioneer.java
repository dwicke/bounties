/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties;

import sim.app.bounties.jumpship.Jumpship;
import sim.engine.SimState;
import sim.util.Bag;

/**
 *
 * @author drew
 */
public class Auctioneer extends Bondsman {

    Auctioneer(int numGoals, int numTasks, Jumpship js) {
        super(numGoals,numTasks,js);
    }
    
    
    @Override
    public void step(SimState state) {
        // for each of the tasks from the available task in a random order
        // ask for bids from each of the agents 
        // allocate the task to the agent with the smallest bid (pick randomly if tied for best)
        this.makeAvailable();// reset the tasks when done and ready
        Bag availTasks = getAvailableTasks();
        availTasks.shuffle(state.random);
        Bag availAgents = getAvailableAgents();// only have to do this once and then remove the winner
        for (int i = 0; i < availTasks.numObjs; i++) {
            Bag topBidders = new Bag();
            Task curTaskAuction = (Task) availTasks.objs[i];
            double smallestBid = Double.MAX_VALUE;
            if(availAgents.numObjs == 0) {
                System.err.println("NO AGENTS");
                return;
            }
            
            for(int j = 0; j <  availAgents.numObjs; j++) {
                AuctionAgent ag = (AuctionAgent) availAgents.objs[j];
                double bid = ag.getBid(curTaskAuction);
                System.err.println("Agent " + ag.getId() + " bid " + bid);
                if (bid < smallestBid){
                    topBidders.clear();
                    topBidders.add(ag);
                    smallestBid = bid;
                }
                else if (bid == smallestBid) {
                    topBidders.add(ag);
                }
            }
            System.out.println("availTasks:" + availAgents.numObjs + " avail tasks:" + availTasks.numObjs);
            //Console.WriteLine("");
            // pick the winner randomly if ties
            int winner = state.random.nextInt(topBidders.numObjs);
            ((AuctionAgent)(topBidders.objs[winner])).setTask(curTaskAuction);
            curTaskAuction.setAvailable(false);// make sure it is set to false
            availAgents.remove(topBidders.objs[winner]);// remove the winner from avail agents
        }
        
        
    }
    
    public Bag getAvailableAgents() {
        Bag availAgents = new Bag();
        for (int i = 0; i < this.bounties.robots.length; i++) {
            AuctionAgent ag = (AuctionAgent) this.bounties.robots[i];
            if(ag.isAvailable()) {
                availAgents.add(ag);
            }
        }
        return availAgents;
    }
    
    
    
}
