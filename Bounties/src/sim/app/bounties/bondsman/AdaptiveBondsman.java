/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.bondsman;

import sim.app.bounties.Bounties;
import sim.app.bounties.Task;

/**
 *
 * @author drew
 */
public class AdaptiveBondsman extends Bondsman {
    private static final long serialVersionUID = 1;

    public double bountyHist[][];
    double alpha = 0.85; // closer to 1 the more current information is used in the history
    double oneminusalpha = 1 - alpha;
    
    boolean mightDoubleBounty[];
    
    
    public AdaptiveBondsman(Bounties bounties, int exclusiveType) {
        super(bounties, exclusiveType);
        bountyHist = new double[bounties.numTasks][bounties.numAgents];
        mightDoubleBounty = new boolean[bounties.numTasks];// all are false to begin with
    }

    public double[][] getBountyHist() {
        return bountyHist;
    }
    
    /**
     * Given that agent a decided to do this task t should we keep it available
     * for other agents to go after?
     * @param t task id t
     * @param a agent id a
     */
    @Override
    public void isExclusive(int t, int a) {
        // first average the bounty paid to the other agents
        double minOthers = 0;
        int numOthers = 0;
        double mine = 0;
        for (int i = 0; i < this.bounties.numAgents; i++) {
            if (a == i) {
                mine = bountyHist[t][i];
            }
            else if (minOthers == 0 || (bountyHist[t][i] < minOthers && bountyHist[t][i] != 0))
            {
                minOthers = bountyHist[t][i];
            }
        }
        
        
        if(minOthers > mine && mine != 0) {
            // make it exclusive
            isExclusive[t] = true;
        }
        else {
            isExclusive[t] = false; /// therefore non-exclusive if no one has done it
        }
        
        
    }

    @Override
    public void finishTask(Task curTask, int robotID, long timestamp) {
        super.finishTask(curTask, robotID, timestamp);
        // "learn" the history average
        bountyHist[curTask.getID()][robotID] = alpha * curTask.getLastReward() + 
                oneminusalpha * bountyHist[curTask.getID()][robotID];
        
        isExclusive[curTask.getID()] = false;
    }
    
    
    
    
       
    /**
     * By using this method we reduce the amount of redundant agents.
     * We make the bondsman very leary as soon as the average bounty available
     * exceeds the last reward for the particular task that has become available
     * we make that task non-exclusive (ie the sky is falling)
     * 
     * If the average bounty available is less than what the task was at
     * last time then life is good so make it exclusive.
     * 
     * This method is very sharp and has no "lenience" due to agent noise.
     * @param task 
     */
    @Override
    public void decideExclusivity(Task task) {
        
        int totalBounty = 0;
        Task [] availTasks = this.getAvailableTasks();
        for (Task t : availTasks) {
            totalBounty += t.getCurrentReward();
        }
        //System.err.println("Exclusivity: " + isExclusive[task.getID()]);
        if (mightDoubleBounty[task.getID()]) {
            // decide if we should change to non-exclusive
            // i think that this might be a
            //System.err.println("Exclusive deciding if should be");

            // the sky is falling! so go to non-exclusive when avg > last reward
            if((totalBounty/availTasks.length) > task.getLastReward()) {
                // then make it non-exclusive
                mightDoubleBounty[task.getID()] = false;
                // promote it as well by starting the bounty out higher?
                task.setCurrentReward(task.getDefaultReward()*2);
                
                
            }
            
        } else {
            // decide if we should change to exclusive
            // get what the current total bounty / task is right now
            // basically check if this tasks should be made exclusive
            // to make other agents go after the other tasks.
            
            // so exclusivity is best when the world is all nice and happy
            // so that means we have somewhat of a stable system.
            // 
            // also if average bounty is close to the starting bounty
            // curAvg, base, lastRew
            // meaning whoever is doing this task knows what he is doing
            //System.err.println("Not exclusive deciding if should be");
            if((totalBounty/availTasks.length) < task.getLastReward()) {
                // then make it exclusive
                mightDoubleBounty[task.getID()] = true;
            }
        }
    }
}
