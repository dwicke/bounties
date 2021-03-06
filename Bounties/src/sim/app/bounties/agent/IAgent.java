/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.bounties.agent;

import java.awt.Color;
import sim.app.bounties.agent.valuator.DecisionValuator;
import sim.app.bounties.control.IController;
import sim.app.bounties.jumpship.Jumpship;
import sim.app.bounties.ra.resource.Resource;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Int2D;

/**
 *
 * @author drew
 */
public interface IAgent extends Steppable {
    public void init(SimState state);
    public void setId(int id);
    public int getId();
    public int getCurrentTaskID();
    public boolean getIsRealRobot();
    public void setRobotHome(Int2D home); // the base 
    public Int2D getRobotHome();
    public void setRobotController(IController controller);
    public void setCanDie(boolean canDie);
    public void setHasTraps(boolean hasTraps);
    public void setIsBad(boolean isBad);
    public void setDecisionValuator(DecisionValuator dv);
    public void setJumpship(Jumpship jumpship);
    public void setCanJumpship(boolean canJumpship);
    public int[] getTried();
    public int[] getCompleted();
    public void setTrapStep(int trapStep);
    public double getResourceBid(Resource resource);
    public void receiveResource(Resource resource);
}
