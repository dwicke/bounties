package sim.app.bounties;

import java.awt.Color;
import java.awt.Graphics2D;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.simple.OvalPortrayal2D;
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
public class Task extends OvalPortrayal2D {
    
    private int currentReward = 0; // controlled by bondsman to increase
    private boolean done = false;
    private boolean available = true;
    private Int2D initialLocation;// location
    private int id = 0;
    public boolean isDone(){
        return done;
    }
    public void setDone(boolean val){
        done = val;
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

    private Color availableColor = Color.RED;// may want to change color if we have different types of tasks
    private Color notAvailableColor = Color.WHITE;
    
    @Override
    public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
        super.draw(object, graphics, info);
        if (!available)// then don't draw it
            graphics.setColor(notAvailableColor);
         else 
            graphics.setColor(availableColor);
        
        
        // this code was stolen from OvalPortrayal2D
        int x = (int) (info.draw.x - info.draw.width / 2.0);
        int y = (int) (info.draw.y - info.draw.height / 2.0);
        int width = (int) (info.draw.width);
        int height = (int) (info.draw.height);
        graphics.fillOval(x, y, width, height);
    }
    
}
