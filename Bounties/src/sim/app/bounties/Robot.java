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
    public boolean hasTaskItem = false;
    public Task curTask;
    public Goal curGoal;
    double reward = 0;
    QTable myQtable;
    int x;
    int y;

    Bondsman bondsman;
    
    public boolean getHasTaskItem() {
        return hasTaskItem;
    }

    public void setHasTaskItem(boolean val) {
        hasTaskItem = val;
    }

    public Bondsman getBondsman() {
        return bondsman;
    }
    
    
  
//TODO: initialize Q-table
//update reward when task is done/failed
//consult the qtable for a decision
    public Robot() {
       // myQtable = new Qtable();
       // 5 states (num tasks and 2 actions do it or not)
       myQtable = new QTable(5, 2, .3, .4);
        
    }

    public boolean gotoPosition(final SimState state, Int2D position) { // exeucute task we're on if we have one
        final Bounties af = (Bounties) state;
        
        Int2D location = af.robotgrid.getObjectLocation(this);
        int x = location.x;
        int y = location.y;
        
        System.err.println("X loc " + x + " y loc:" + y + " goal x and y: " + position.toCoordinates());
        // really simple first get inline with the x
        if (position.x != x) {
            int unit = (position.x - x) / Math.abs(position.x - x);
            af.robotgrid.setObjectLocation(this, new Int2D(x + unit, y));
            int newX = x + unit;
            return (position.x == newX) && y == position.y;
        }
        // then in y
        if (position.y != y) {
            int unit = (y - position.y) / Math.abs(y - position.y);
            af.robotgrid.setObjectLocation(this, new Int2D(x, y - unit));
            int newY = y - unit;
            return (position.x == x) && (newY == position.y);
        }
        return true;// we are there already
    }

    public void step(final SimState state) {
        final Bounties af = (Bounties) state;
        bondsman = af.bondsman; // set the bondsman
        
        
        if(hasTaskItem){// if I have it goto the goal
            if(gotoPosition(state, curGoal.getLocation())) {
                // we have made it to the goal so we should learn
                // update q-table
                // then we should tell the bondsman that we have done that task
                bondsman.finishTask(curTask);
            }
            
            
        }else if (curTask != null) {
            gotoPosition(state, curTask.getLocation());
            
        }else{
            decideTask();
        }
        
        
       
    }
    public void decideTask(){
        //consult q table
        //myQTable.getBestAction(0);
        double max = 0;
        
        for (int i = 0; i < 5; i++) {
            
            double cur = myQtable.getBestAction(x);
            if (cur > max) {
                
            }
        }
        
        // must set the goal and task 
    }
    // a few tweaks by Sean
    private Color noTaskColor = Color.black;
    private Color hasTaskColor = Color.red;

    public final void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
        if (hasTaskItem) {
            graphics.setColor(hasTaskColor);
        } else {
            graphics.setColor(noTaskColor);
        }

        // this code was stolen from OvalPortrayal2D
        int x = (int) (info.draw.x - info.draw.width / 2.0);
        int y = (int) (info.draw.y - info.draw.height / 2.0);
        int width = (int) (info.draw.width);
        int height = (int) (info.draw.height);
        graphics.fillOval(x, y, width, height);

    }
}
