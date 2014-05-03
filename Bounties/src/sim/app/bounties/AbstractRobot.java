/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties;

import java.awt.Color;
import sim.app.bounties.robot.darwin.agent.Real;
import sim.engine.SimState;
import sim.util.Int2D;

/**
 *
 * @author drew
 */
public class AbstractRobot implements IRobot {

    boolean realRobot = false;
    boolean hasTaskItem = false;
    int id;
    IController control;
    IController realControl;
    Task curTask;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    private Color noTaskColor = Color.black;
    private Color hasTaskColor = Color.red;

    public Color getHasTaskColor() {
        return hasTaskColor;
    }

    public Color getNoTaskColor() {
        return noTaskColor;
    }

    /**
     * Is true if the robot is at the location of the task and there are enough
     * robots to perform the task.
     *
     * @return
     */
    public boolean getHasTaskItem() {
        return hasTaskItem;
    }


    public boolean gotoGoalPosition(final SimState state, Real position) {
        if (control == null) {
            // make the new controller
            if (realRobot) {
                control = new DarwinController(id);
            }
            else {
                control = new VirtualController();
            }
            control.setMyRobot(this);
        }
        
        return control.gotoGoalPosition(state, position);
    }

    public boolean gotoTaskPosition(final SimState state, Real position) {
        if (control == null) {
            // make the new controller
            if (realRobot) {
                control = new DarwinController(id);
            }
            else {
                control = new VirtualController();
            }
            
            control.setMyRobot(this);
        }
        //System.err.println("I'm going to task: " + position.getLocation().toCoordinates());
        
        return control.gotoTaskPosition(state, position);
    }

    public void setHasTaskItem(boolean val) {
        hasTaskItem = val;
    }

    @Override
    public int getCurrentTaskID() {
        if (curTask == null) {
            return -1;
        }
        return curTask.getID();

    }

    @Override
    public boolean getIsRealRobot() {
        return realRobot;
    }

    @Override
    public void setIsRealRobot(boolean isReal) {
        if (isReal != realRobot) {
            if (isReal && realControl != null) {
                control = realControl;// don't reinit it.
            } else if (!isReal) {
                realControl = control;
                control = new VirtualController();
            } else {// isReal = true and we have never been real and 
                realRobot = isReal;
                control = new DarwinController(id);
                realControl = control;
            }
            control.setMyRobot(this);
        }
    }

}
