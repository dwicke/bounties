/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.bounties.agent;

import java.awt.Color;
import sim.app.bounties.control.IController;
import sim.engine.SimState;
import sim.util.Int2D;

/**
 *
 * @author drew
 */
public interface IRobot {
    public void init(SimState state);
    public boolean getHasTaskItem();
    public Color getHasTaskColor();
    public Color getNoTaskColor();
    public void setId(int id);
    public int getId();
    public int getCurrentTaskID();
    public int getRewardCurrentTask();// the reward for the current task i am working on
    public boolean getIsRealRobot();
    //public void setIsRealRobot(boolean isReal);
    public void setRobotHome(Int2D home); // the base 
    public Int2D getRobotHome();
    public void setRobotController(IController controller);
    public void setCanDie(boolean canDie);
}
