package sim.app.bounties;

import java.awt.Color;
import java.awt.Graphics2D;
import sim.app.bounties.robot.darwin.agent.Real;
import sim.field.grid.SparseGrid2D;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.Fixed2D;
import sim.portrayal.simple.OvalPortrayal2D;
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
    

    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    public Goal getGoal() {
        return goal;
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
        currentReward+=1;
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

    
    
    

    void resetReward() {
        currentReward = 0;
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
