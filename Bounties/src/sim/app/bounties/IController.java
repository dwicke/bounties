/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.bounties;

import sim.app.bounties.agent.IRobot;
import com.gmu.robot.darwin.agent.Real;
import sim.engine.SimState;
import sim.util.Int2D;

/**
 *
 * @author drew
 */
public interface IController {
    
    public boolean gotoPosition(final SimState state, Int2D position);
    public boolean gotoGoalPosition(final SimState state, Real position);
    public boolean gotoTaskPosition(final SimState state, Real position);
    public void setMyRobot(IRobot robot);
}
