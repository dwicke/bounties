/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.bounties.fsm;

/**
 *
 * @author drew
 */
public interface State {
    
    /**
     * Gets the next state called after execute
     * @return the next state
     */
    public State getNextState();
    
    /**
     * Called on entry to the state the first time
     */
    public void enter();
    /**
     * Will be called after enter and 
     * @param trans what transistion string was used to get here
     * @return the transition string to use to determine the next state
     */
    public String execute(String trans);
    /**
     * Called when leaving this state to a different state
     * @return 
     */
    public void exit();
    /**
     * 
     * @return if the node is a terminal node return true else false
     */
    public boolean isTerminal();
    /**
     * 
     * @return true if the node has already entered and we are recursing on the
     * same state.  False if you haven't entered yet
     */
    public boolean isEntered();
    
}
