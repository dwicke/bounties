/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.util;

import sim.util.Double2D;
import sim.util.Int2D;

/**
 *
 * @author drew
 */
public class DefaultReal implements Real{

    private Int2D loc;
    private Double2D realLoc;
    private double orientation;
    
    public DefaultReal(Int2D loc) {
        this.loc = loc;
    }
    
    @Override
    public Double2D getRealTargetLocation() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getOrientation() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Int2D getLocation() {
        return loc;
    }
    
}
