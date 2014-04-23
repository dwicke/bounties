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
public class Robot extends OvalPortrayal2D implements Steppable
    {
    private static final long serialVersionUID = 1;
    
    public boolean getHasFoodItem() { return hasFoodItem; }
    public void setHasFoodItem(boolean val) { hasFoodItem = val; }
    public boolean hasFoodItem = false;
    double reward = 0;
        
    int x;
    int y;
        
    Int2D last;
        
    public Robot(double initialReward) { reward = initialReward; }
        
        
    // at present we have only one algorithm: value iteration.  I might
    // revise this and add our alternate (TD) algorithm.  See the papers.
        
        
    public void depositPheromone( final SimState state)
        {
        final Bounties af = (Bounties)state;
                
        Int2D location = af.robotgrid.getObjectLocation(this);
        int x = location.x;
        int y = location.y;
                
        if (Bounties.ALGORITHM == Bounties.ALGORITHM_VALUE_ITERATION)
            {
            // test all around
            if (hasFoodItem)  // deposit food pheromone
                {
                double max = af.toFoodGrid.field[x][y];
                for(int dx = -1; dx < 2; dx++)
                    for(int dy = -1; dy < 2; dy++)
                        {
                        int _x = dx+x;
                        int _y = dy+y;
                        if (_x < 0 || _y < 0 || _x >= Bounties.GRID_WIDTH || _y >= Bounties.GRID_HEIGHT) continue;  // nothing to see here
                        double m = af.toFoodGrid.field[_x][_y] * 
                            (dx * dy != 0 ? // diagonal corners
                            af.diagonalCutDown : af.updateCutDown) +
                            reward;
                        if (m > max) max = m;
                        }
                af.toFoodGrid.field[x][y] = max;
                }
            else
                {
                double max = af.toHomeGrid.field[x][y];
                for(int dx = -1; dx < 2; dx++)
                    for(int dy = -1; dy < 2; dy++)
                        {
                        int _x = dx+x;
                        int _y = dy+y;
                        if (_x < 0 || _y < 0 || _x >= Bounties.GRID_WIDTH || _y >= Bounties.GRID_HEIGHT) continue;  // nothing to see here
                        double m = af.toHomeGrid.field[_x][_y] * 
                            (dx * dy != 0 ? // diagonal corners
                            af.diagonalCutDown : af.updateCutDown) +
                            reward;
                        if (m > max) max = m;
                        }
                af.toHomeGrid.field[x][y] = max;
                }
            }
        reward = 0.0;
        }

    public void act( final SimState state )
        {
        final Bounties af = (Bounties)state;
                
        Int2D location = af.robotgrid.getObjectLocation(this);
        int x = location.x;
        int y = location.y;
       
            double max = Bounties.IMPOSSIBLY_BAD_PHEROMONE;
            int max_x = x;
            int max_y = y;
            int count = 2;
            for(int dx = -1; dx < 2; dx++)
                for(int dy = -1; dy < 2; dy++)
                    {
                    int _x = dx+x;
                    int _y = dy+y;
                    if ((dx == 0 && dy == 0) ||
                        _x < 0 || _y < 0 ||
                        _x >= Bounties.GRID_WIDTH || _y >= Bounties.GRID_HEIGHT || 
                        af.obstacles.field[_x][_y] == 1) continue;  // nothing to see here
                    double m = af.toHomeGrid.field[_x][_y];
                    if (m > max)
                        {
                        count = 2;
                        }
                    // no else, yes m > max is repeated
                    if (m > max || (m == max && state.random.nextBoolean(1.0 / count++)))  // this little magic makes all "==" situations equally likely
                        {
                        max = m;
                        max_x = _x;
                        max_y = _y;
                        }
                    }
        
            af.robotgrid.setObjectLocation(this, new Int2D(max_x, max_y));
            
        last = location;
        }

    public void step( final SimState state ) //called by scheduler each time step
        {
        depositPheromone(state);
        act(state);
        }

    // a few tweaks by Sean
    private Color noFoodColor = Color.black;
    private Color foodColor = Color.red;
    public final void draw(Object object, Graphics2D graphics, DrawInfo2D info)
        {
        if( hasFoodItem )
            graphics.setColor( foodColor );
        else
            graphics.setColor( noFoodColor );

        // this code was stolen from OvalPortrayal2D
        int x = (int)(info.draw.x - info.draw.width / 2.0);
        int y = (int)(info.draw.y - info.draw.height / 2.0);
        int width = (int)(info.draw.width);
        int height = (int)(info.draw.height);
        graphics.fillOval(x,y,width, height);

        }
    }
