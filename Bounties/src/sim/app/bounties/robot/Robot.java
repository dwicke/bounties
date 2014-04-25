/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.bounties.robot;

import sim.app.bounties.robot.behaviors.CommandMotions;
import sim.app.bounties.robot.comm.Parse;

/**
 * An interface for physical robots.  Standard methods that will be used.
 * @author drew
 */
public interface Robot {
    /**
     * Sends a command to the physical robot.
     * @param cms the command with a 
     */
    public void sendCommand(CommandMotions cms);
    /**
     * Gets the data parser for this robot.
     * @return the parser for the data.
     */
    public Parse getParser();
}
