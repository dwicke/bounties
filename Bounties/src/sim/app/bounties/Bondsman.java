/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.bounties;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;

/**
 *
 * @author dfreelan
 * bondsman is in charge of making tasks.... goodluck?
 */
public class Bondsman implements Steppable {
    
    Bag tasks = new Bag();
    Bag goalLocs = new Bag();
    
    public Bondsman() {
        // set up the goal Locations
    
    }
   
    
    
    
    
    
    
    @Override
    public void step(SimState state) {
        // 
    }
    
}
