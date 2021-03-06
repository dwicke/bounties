/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.bounties.portrayal;

import java.awt.Graphics2D;
import sim.app.bounties.environment.Task;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.simple.OvalPortrayal2D;

/**
 *
 * @author drew
 */
public class TaskPortrayal extends OvalPortrayal2D {
    private static final long serialVersionUID = 1;

    Task model;
    
    public TaskPortrayal(Task model) {
        this.model = model;
    }
    
    
    @Override
    public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
        super.draw(object, graphics, info);
        if (!model.getIsAvailable())// then don't draw it
            graphics.setColor(model.getNotAvailableColor());
         else 
            graphics.setColor(model.getAvailableColor());
        
        
        // this code was stolen from OvalPortrayal2D
        int x = (int) (info.draw.x - info.draw.width / 2.0);
        int y = (int) (info.draw.y - info.draw.height / 2.0);
        int width = (int) (info.draw.width);
        int height = (int) (info.draw.height);
        graphics.fillOval(x, y, width, height);
    }
    
}
