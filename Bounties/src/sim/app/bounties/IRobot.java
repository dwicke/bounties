/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.bounties;

import java.awt.Color;

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
    public void setIsRealRobot(boolean isReal);
}
