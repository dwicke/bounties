/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.bounties;

import sim.engine.SimState;
import sim.engine.Steppable;

/**
 * there are multiple states that exist in the foraging world maybe make these
 * abstract methods and do the logic to determine whether they are in those states
 * here in the step? Then easier to subclass?
 * 1. At the task
 * 2. At the goal
 * 3. Attempting task
 * 4. Switch task
 * @author drew
 */
public class AbstractSteppableRobot extends AbstractRobot implements Steppable{

    
    
    
    
    @Override
    public void step(SimState state) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
