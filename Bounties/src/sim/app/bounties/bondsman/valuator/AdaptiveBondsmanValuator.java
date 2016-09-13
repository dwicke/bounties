/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.bondsman.valuator;

import java.util.Arrays;
import sim.app.bounties.Bounties;
import sim.app.bounties.environment.Task;

/**
 *
 * @author drew
 */
public class AdaptiveBondsmanValuator implements BondsmanValuator{

    double [] xi;
    double alpha;
    double learningrate = .01;
    
    double beta; // should be strictly > (sum xi)/ max(xi)
     
    
    Bounties bounties;
    
    public AdaptiveBondsmanValuator(Bounties bounties, double initialBounty, double alpha) {
        xi = new double[bounties.numTasks];
        Arrays.fill(xi, initialBounty);
        this.alpha = alpha;
        this.bounties = bounties;
    }
    
    
    
    @Override
    public void setInitialBounty(Task t) {
        t.setDefaultReward(xi[t.getID()]);
    }

    @Override
    public double getBountyIncrement(Task t) {
        //System.err.println("bounty rate for task i = " + t.getID() + " = " + Math.pow((1 - 1 / (alpha * xi[t.getID()])), t.getTimeNotFinished()));
        return Math.pow((1 - 1 / (alpha * xi[t.getID()])), t.getTimeNotFinished());
    }

    @Override
    public void updateBounty(Task t, int numTimeSteps) {
        // determine how long it took the task to be completed once started by the agent that completes
        // this is x_i and it is how we set the inital bounty.
        xi[t.getID()] = (1-learningrate) * xi[t.getID()] + learningrate*numTimeSteps;
        //System.out.println("bounty initial for id " + t.getID() + " = " + xi[t.getID()]);
    }
    
}
