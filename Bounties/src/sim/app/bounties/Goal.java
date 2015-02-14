/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.bounties;

import java.awt.Color;
import sim.app.bounties.util.Real;
import sim.field.grid.SparseGrid2D;
import sim.portrayal.Fixed2D;
import sim.util.Double2D;
import sim.util.Int2D;

/**
 * represents a goal.  Holds info such as location, and in the future maybe
 * overall counts of reaching this particular goal...
 * @author drew
 */
public class Goal implements Real, Fixed2D, sim.portrayal.Orientable2D { 
        private static final long serialVersionUID = 1;

    Int2D location;
    int id;
    private Color goalColor = Color.GREEN;

    public Goal() {}
    
    public void setLocation(Int2D location) {
        this.location = location;
    }

    public Int2D getLocation() {
        return location;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    
       
    
    public Double2D getRealTargetLocation()
    {
        return new Double2D((location.x - 30) * 0.1, (location.y - 20) * 0.1);
    }
   
    public double getOrientation() { return 0; }

    @Override
    public boolean maySetLocation(Object field, Object newObjectLocation) {
        location = (Int2D) newObjectLocation;
        ((SparseGrid2D) field).setObjectLocation(this, location);
        System.err.println("Goal loc: " + location.toCoordinates());
        return true;
    }

    @Override
    public void setOrientation2D(double val) {
        
    }

    @Override
    public double orientation2D() {
        return 0;
    }

    Color getGoalColor() {
        return goalColor;
    }
    
    
    
}
