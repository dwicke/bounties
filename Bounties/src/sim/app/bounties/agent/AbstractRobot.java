/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.agent;

import java.awt.Color;
import sim.app.bounties.DarwinController;
import sim.app.bounties.IController;
import sim.app.bounties.Task;
import sim.app.bounties.VirtualController;
import com.gmu.robot.darwin.agent.Real;
import sim.engine.SimState;
import sim.util.Bag;
import sim.util.Int2D;

/**
 *
 * @author drew
 */
public class AbstractRobot implements IRobot {
    int historySize = 100;
    boolean realRobot = false;
    boolean hasTaskItem = false;
    int id;
    IController control;
    IController realControl;
    Task curTask;
    Int2D home;
    public int[] decisionsMade = new int[historySize];
    int[] timeOnTask = new int[historySize];
    int rollingHistoryCounter = 0; // pointer to current spot in the list for history purposes
    int rewardCurrentTask; // the reward for the current task
    boolean canDie;
    public boolean hasTraps = false;
    public void init(SimState state) {
        // do nothing...
    }
    public void setHasTraps(boolean hasTraps) {
        this.hasTraps = hasTraps;
    }
    public int getRewardCurrentTask() {
        return rewardCurrentTask;
    }
    /**
     * Must set this whenever I switch to a task or take a task
     * @param reward the bounty at time of commitment for me
     */
    public void setRewardCurrentTask(int reward) {
        this.rewardCurrentTask = reward;
    }
    
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
    // there will be one history array for decisions, if the task is negitive it means you jumped ship, positive means you completed
    public void updateStatistics(boolean jumpedShip, int taskID, int totalTimeOnTask){
       int multiplier = 1;
       if(jumpedShip)
           multiplier = -1;
      // System.out.println("updated decisionsMade at " + rollingHistoryCounter + " with 10");
       decisionsMade[rollingHistoryCounter] = taskID*multiplier;
       timeOnTask[rollingHistoryCounter] = totalTimeOnTask;
       rollingHistoryCounter = (rollingHistoryCounter +1 )%historySize;
    }
    public int getLastDecision(){
        return decisionsMade[rollingHistoryCounter];
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
/*
    @Override// seems like dead code
    public void setIsRealRobot(boolean isReal) { 
        if (isReal != realRobot) {
            if (isReal && realControl != null) {
                control = realControl;// don't reinit it.
            } else if (!isReal) {
                realControl = control;
                control = new VirtualController();
            } else {// isReal = true and we have never been real and 
                control = new DarwinController(id);
                realControl = control;
            }
            realRobot = isReal;
            control.setMyRobot(this);
        }
    }
*/
    @Override
    public void setRobotHome(Int2D home) {
        this.home = home;
    }

    @Override
    public Int2D getRobotHome() {
        return home;
    }

    @Override
    public void setRobotController(IController controller) {
        this.control = controller;
    }

    
    
    public void debug(String message) {
        //System.err.println(message);
    }

    @Override
    public void setCanDie(boolean canDie) {
       this.canDie = canDie;
    }
}
