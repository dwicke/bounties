/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.bounties.fsm;

import java.util.HashMap;
import sim.util.Bag;


/**
 * Simple finite state machine.
 * @author drew
 */
public class FSM {
    
    
    //HashMap<State, HashMap<String, State> > fsm = new HashMap<>();
    State curState;
    String transition;
    
    
    /**
     * Start must have already been added to the set.  Can be any state.
     * @param start 
     * @return 
     */
    public void start(State start) {
        
        start.enter();
        transition = start.execute(null);
        curState = start.getNextState();
        if (curState != start)
            start.exit();
    }
    /**
     * Goes to the next state
     * @return true if terminated 
     */
    public boolean step() {
        if (!curState.isEntered())
            curState.enter();
        transition = curState.execute(transition);
        
        if (curState != curState.getNextState()) {
            curState.exit();
        }
        else {
            // it equals itself so check if it 
            return curState.isTerminal();
        }
        curState = curState.getNextState();
        // can't have reached terminal if the state transitioned to a new one.
        return false;
    }
    
    public State getCurrentState() {
        return curState;
    }
    
}
