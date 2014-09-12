/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.bounties;

/**
 *
 * @author drew
 */
public class Leaderboard {
    
    int leaderID[];
    double leaderRate[];
    long leaderTimestep[];
    long deadPeriod;
    
    /**
     * 
     * @param numTasks how many tasks are available
     * @param deadPeriod how long until someone else who has a higher rate can 
     * replace a lower score. suggested to make it very large.
     */
    public Leaderboard(int numTasks, long deadPeriod) {
        leaderID = new int[numTasks];
        leaderRate = new double[numTasks];
        leaderTimestep = new long[numTasks];
        this.deadPeriod = deadPeriod;
    }
    
    public int getLeaderID(int taskID) {
        return leaderID[taskID];
    }
    public double getLeaderRate(int taskID) {
        return leaderRate[taskID];
    }
    public long getLeaderTimestep(int taskID) {
        return leaderTimestep[taskID];
    }
    
    public void setLeader(int taskID, int robotID, double rate, long timestep) {
        if(leaderRate[taskID] > rate || timestep - leaderTimestep[taskID] > deadPeriod) {
            leaderRate[taskID] = rate;
            leaderID[taskID] = robotID;
        }
    }
    
}
