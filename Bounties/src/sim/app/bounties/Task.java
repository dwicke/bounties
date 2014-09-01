package sim.app.bounties;

import java.awt.Color;
import java.awt.Graphics2D;
import sim.app.bounties.robot.darwin.agent.Real;
import sim.field.grid.SparseGrid2D;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.Fixed2D;
import sim.portrayal.simple.OvalPortrayal2D;
import sim.util.Bag;
import sim.util.Double2D;
import sim.util.Int2D;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author dfreelan
 */
public class Task implements Real, Fixed2D{
        private static final long serialVersionUID = 1;

    private int currentReward = 0; // controlled by bondsman to increase
    private boolean done = false; // true when at the goal false otherwise
    private boolean available = true;// true when a robot is not carrying and not at a goal it is false if not at the
    private Int2D initialLocation;// location
    private int id = 0;
    private Goal goal;
    private Color availableColor = Color.RED;// may want to change color if we have different types of tasks
    private Color notAvailableColor = Color.WHITE;
    private int requiredRobots = 1;
    private Bag presentRobots = new Bag();
    
    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    public Goal getGoal() {
        return goal;
    }
    public int getRequiredRobots(){
        return requiredRobots;
    }
    public void setRequiredRobots(int required){
        requiredRobots = required;
    }
    /**
     * kind of inefficient..... order n in number of required robots. if larger
     * sizes then could use hash... or change how all irobots claim they are present
     * 
     * Check to see if robot has already claimed he is at the task.
     * @param a  is a robot
     * 
     * 
     */
    public void addRobot(IRobot a){
        if(presentRobots.contains((Object)a))
            return;
        presentRobots.add(a);
    }
    public void subtractRobot(IRobot a){
        presentRobots.remove((Object)a);
    }
    public boolean isEnoughRobots(){
        return presentRobots.numObjs == requiredRobots;
    }
    public boolean isDone(){
        return done;
    }
    public void setDone(boolean val){
        done = val;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
    public boolean getIsAvailable() {
        return available;
    }
    
    public void setCurrentReward(int reward){
        currentReward = reward;
    }
    public void incrementCurrentReward(){
        currentReward+=requiredRobots;
    }
    public int getCurrentReward(){
        return currentReward;
    }
    public int getID(){
        return id;
    }
    public void setID(int id){
        this.id = id; 
    }
    public Int2D getLocation() {
        return initialLocation;
    }
    public void setLoc(Int2D loc) {
        this.initialLocation = loc;
    }

    
    
    public int getNumRobotsDoingTask() {
        return presentRobots.numObjs;
    }

    void resetReward() {
        currentReward = 200;
    }

    public Double2D getRealTargetLocation()
    {
        return new Double2D((initialLocation.x - 30) * 0.1, (initialLocation.y - 20) * 0.1);
    }
   
    public double getOrientation() { return 0; }

    @Override
    public boolean maySetLocation(Object field, Object newObjectLocation) {
        
        initialLocation = (Int2D) newObjectLocation;
        System.err.println("Set new location to: " + initialLocation.toCoordinates());
        //((SparseGrid2D)field).setObjectLocation(this, initialLocation);// move myself
        return true;
    }

    Color getNotAvailableColor() {
        return notAvailableColor;
    }

    Color getAvailableColor() {
        return availableColor;
    }

   
    

}
