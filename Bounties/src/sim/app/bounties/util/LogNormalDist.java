/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.bounties.util;

import ec.util.MersenneTwisterFast;

/**
 *
 * @author dfreelan
 */
public class LogNormalDist {
    double scale, shape;
    MersenneTwisterFast rand;
    public LogNormalDist(double scale, double shape, MersenneTwisterFast rand){
        this.scale = scale;
        this.shape = shape;
        this.rand = rand;
    }
    public double getMean(){
        return Math.exp(scale + shape*shape/2);
    }
    public double sample(){
        return Math.exp( scale + shape*rand.nextGaussian());
    }
}
