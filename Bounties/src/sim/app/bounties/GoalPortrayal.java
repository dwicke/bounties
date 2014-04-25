/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.bounties;

import java.awt.Graphics2D;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.simple.OvalPortrayal2D;
import sim.portrayal.simple.RectanglePortrayal2D;

/**
 *
 * @author drew
 */
public class GoalPortrayal extends RectanglePortrayal2D {
       private static final long serialVersionUID = 1;

    Goal model;

    public GoalPortrayal(Goal model) {
        this.model = model;
    }
    
    
   
    @Override
    public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
        super.draw(object, graphics, info);
        
        graphics.setColor(model.getGoalColor());
        
        // this code was stolen from OvalPortrayal2D
        int x = (int) (info.draw.x - info.draw.width / 2.0);
        int y = (int) (info.draw.y - info.draw.height / 2.0);
        int width = (int) (info.draw.width);
        int height = (int) (info.draw.height);
        graphics.fillRect(x, y, width, height);
    }

}
