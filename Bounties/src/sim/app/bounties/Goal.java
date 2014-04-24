/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.bounties;

import java.awt.Color;
import java.awt.Graphics2D;
import sim.app.horde.scenarios.robot.darwin.agent.Real;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.simple.OvalPortrayal2D;
import sim.util.Double2D;
import sim.util.Int2D;

/**
 * represents a goal.  Holds info such as location, and in the future maybe
 * overall counts of reaching this particular goal...
 * @author drew
 */
public class Goal extends OvalPortrayal2D implements Real{ 
    
    Int2D location;
    int id;

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
    
       private Color goalColor = Color.GREEN;
    
    @Override
    public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
        super.draw(object, graphics, info);
        
        graphics.setColor(goalColor);
        
        // this code was stolen from OvalPortrayal2D
        int x = (int) (info.draw.x - info.draw.width / 2.0);
        int y = (int) (info.draw.y - info.draw.height / 2.0);
        int width = (int) (info.draw.width);
        int height = (int) (info.draw.height);
        graphics.fillOval(x, y, width, height);
    }

    public Double2D getRealTargetLocation()
    {
        return new Double2D((location.x - 30) * 0.1, (location.y - 20) * 0.1);
    }
   
    public double getOrientation() { return 0; }
    
    
    
}
