/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties;

import java.awt.Color;
import java.awt.Graphics2D;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.simple.OvalPortrayal2D;
import sim.util.Int2D;

/**
 *
 * @author drew
 */
public class Robot extends OvalPortrayal2D implements Steppable {

    private static final long serialVersionUID = 1;

    public boolean getHasTaskItem() {
        return hasTaskItem;
    }

    public void setHasTaskItem(boolean val) {
        hasTaskItem = val;
    }
    public boolean hasTaskItem = false;
    double reward = 0;

    int x;
    int y;

    Int2D last;

    public Robot() {
    }

    public void act(final SimState state) {
        final Bounties af = (Bounties) state;

        Int2D location = af.robotgrid.getObjectLocation(this);
        int x = location.x;
        int y = location.y;

        // use this method to move the robot to the next robot    
        //af.robotgrid.setObjectLocation(this, new Int2D(max_x, max_y));
        last = location;
    }

    public void step(final SimState state) {
        act(state);
    }

    // a few tweaks by Sean
    private Color noFoodColor = Color.black;
    private Color foodColor = Color.red;

    public final void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
        if (hasTaskItem) {
            graphics.setColor(foodColor);
        } else {
            graphics.setColor(noFoodColor);
        }

        // this code was stolen from OvalPortrayal2D
        int x = (int) (info.draw.x - info.draw.width / 2.0);
        int y = (int) (info.draw.y - info.draw.height / 2.0);
        int width = (int) (info.draw.width);
        int height = (int) (info.draw.height);
        graphics.fillOval(x, y, width, height);

    }
}
