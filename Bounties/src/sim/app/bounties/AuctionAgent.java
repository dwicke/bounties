/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties;

import sim.engine.SimState;
import sim.engine.Steppable;

/**
 *
 * @author drew
 */
public abstract class AuctionAgent extends AbstractRobot implements Steppable {

    
    private boolean isAvailable = true;
    Bounties bountyState;
    Auctioneer auctioneer;
    long startTaskTime;

    @Override
    public void init(SimState state) {
        bountyState = ((Bounties)state);
        auctioneer = (Auctioneer) bountyState.bondsman;
    }
    
    
    
    
    @Override
    public void step(SimState state) {
        if(curTask != null) {
            // then I have a task to do so do it.
            if(gotoTask()) {
                // if I got to the task then I'm done with task so jump home
                // but learn first if able to
                learn();
                updateStatistics(false, curTask.getID(), (int)getNumStepsWorkedOnCurTask());
                isAvailable = true;
                jumpHome();
                // and let the auctioneer know
                auctioneer.finishTask(curTask, id, bountyState.schedule.getSteps());
                
            }
        }
    }
    
    public boolean isAvailable() {
        return isAvailable;
    }
    /**
     * Returns the bid.  smaller the value the more likely to get the task.
     * @param t
     * @return 
     */
    public abstract double getBid(Task t);
    
    /**
     * This gets called by the auctioneer if you win the auction to set the task
     * to be worked on.
     * @param t 
     */
    public void setTask(Task t) {
        curTask = t;
        auctioneer.doingTask(id, t.getID());// confirm that I am doing the task with the auctioneer.
        startTaskTime = bountyState.schedule.getSteps();
    }
    
    public long getNumStepsWorkedOnCurTask() {
        return bountyState.schedule.getSteps() - startTaskTime + 1;
    }
    
    /**
     * Move toward the curTask
     * @return true if i made it to the task
     */
    public boolean gotoTask() {
        if(bountyState == null || curTask == null){
            System.err.println("one was null " + bountyState + "  " + curTask);
        }
        return gotoTaskPosition(bountyState, curTask);
    }
    
    /**
     * Transport robot to home location
     */
    public void jumpHome() {
        bountyState.robotgrid.setObjectLocation(this,this.getRobotHome());// teleport home
    }
    
    
    public abstract void learn();
}
