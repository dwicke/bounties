/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.bounties;

import java.awt.Color;
import sim.util.Int2D;

/**
 *
 * @author drew
 */
public interface IRobot {
    public boolean getHasTaskItem();
    public Color getHasTaskColor();
    public Color getNoTaskColor();
    public void setId(int id);
    public int getId();
    public int getCurrentTaskID();
    public boolean getIsRealRobot();
    //public void setIsRealRobot(boolean isReal);
    public void setRobotHome(Int2D home); // the base 
    public Int2D getRobotHome();
    public void setRobotController(IController controller);
}
