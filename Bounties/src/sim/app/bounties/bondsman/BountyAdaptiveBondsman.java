/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.bondsman;

import sim.app.bounties.Bounties;
import sim.app.bounties.environment.Task;

/**
 *
 * @author drew
 */
public class BountyAdaptiveBondsman extends Bondsman {
    private static final long serialVersionUID = 1;

    public double timeEst[];
    double alpha = 0.85; // closer to 1 the more current information is used in the history
    double oneminusalpha = 1 - alpha;
    
    
    int lengthOnBoard[];
    
    public BountyAdaptiveBondsman(Bounties bounties, int exclusiveType) {
        super(bounties, exclusiveType);
        timeEst = new double[bounties.numTasks];// all are false to begin with
        for (int i = 0; i < bounties.numTasks; i++) {
            timeEst[i] = 1.0;
        }
        lengthOnBoard = new int[bounties.numTasks];
    }

    

    @Override
    public void incrementBounty() {
        if (exclusiveType == 2) {
            
            for(int i = 0; i< tasks.size(); i++) {
                lengthOnBoard[i]++;
                int newReward = (int) ((((Task)tasks.objs[i]).getDefaultReward() + lengthOnBoard[i]) * Math.pow(getTimeEst(i), getGamma()));
                ((Task)tasks.objs[i]).setCurrentReward(newReward);
            }
        } else {
            super.incrementBounty();
        }
    }
    
    public double getTimeEst(int taskID) {
        return timeEst[taskID];
    }
    
    public double getGamma() {
        return 0.99;
    }
    
    
    @Override
    public void finishTask(Task curTask, int robotID, long timestamp, int numTimeSteps) {
        super.finishTask(curTask, robotID, timestamp, numTimeSteps);
        timeEst[curTask.getID()] = alpha * numTimeSteps + 
                oneminusalpha * timeEst[curTask.getID()];
        lengthOnBoard[curTask.getID()] = 0;
    }
    /*
    @Override
    public void isExclusive(Task task, int a) {
        if (whoseDoingTaskByID(task).numObjs >= 2) {
            task.setIsExclusive(true);
            isExclusive[task.getID()] = true;
        } else {
            task.setIsExclusive(false);
            isExclusive[task.getID()] = false;
        }
    }*/
       
}
