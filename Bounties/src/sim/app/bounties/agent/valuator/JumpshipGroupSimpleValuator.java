/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.agent.valuator;

import ec.util.MersenneTwisterFast;
import sim.app.bounties.agent.Agent;
import sim.app.bounties.agent.IAgent;
import sim.app.bounties.environment.Task;
import sim.util.Bag;

/**
 *
 * @author drew
 */
public class JumpshipGroupSimpleValuator extends LearningValuator implements DecisionValuator {
    private static final long serialVersionUID = 1;
    
    private int numTasks;
    
    public JumpshipGroupSimpleValuator(MersenneTwisterFast random, double epsilonChooseRandomTask, int agentID, boolean hasOneUp, int numTasks, int numRobots) {
        super(random, epsilonChooseRandomTask, agentID, hasOneUp, numTasks, numRobots);
        this.numTasks = numTasks;
    }
    
    @Override
    Task pickTask(Task availableTasks[], Task unavailableTasks[]) {
        return pickTask(availableTasks);
    }
    @Override
    Task pickTask(Task availableTasks[]) {
        double max = -1;
        Task curTask = null;
        for (Task availTask : availableTasks) {
            // over all tasks
            double tval = timeTable.getQValue(availTask.getID(), 0);
           /* if (tval > numTimeSteps) {
                tval -= numTimeSteps;
            }*/
            /*
            double curReward = availTask.getCurrentReward();
            if (this.preTask != null && this.preTask.getID() != availTask.getID()) {
                curReward = availTask.getCurrentReward() - this.preTask.getCurrentReward();
                if (curReward < 0) {
                    curReward = 0;
                }
            }
            */
            double pval = getPValue(availTask);
            //double value = 1.0 / tval * pval * (curReward + tval);
            
            int numAtTask = 0;
            //get the number of agents at a task that aren't me
            for (int i = 0; i < availTask.getAgentsAtTask().numObjs; i++) {
                if (((IAgent)availTask.getAgentsAtTask().get(i)).getId() != agentID && tval < 50) {
                    numAtTask++;
                }
            }
            
            int numGoingAfterTask = -numAtTask; // don't count those that are already present at the task
            // this is how many agents that are not at the task but are still going after it
            for (int i = 0; i < availTask.getCurrentAgentsOnTask().numObjs; i++) {
                if ((Integer)(availTask.getCurrentAgentsOnTask().get(i)) != agentID && tval < 50) {
                    numGoingAfterTask++;
                }
            }
           
            int totalAgentsNeeded = availTask.getnumAgentsNeeded();
            
            pval = pval + ((totalAgentsNeeded - numAtTask + 1) / totalAgentsNeeded); //+ ((totalAgentsNeeded - numGoingAfterTask + 1) / totalAgentsNeeded);
            
           // double proportionPresent = ((numAtTask + 1)/availTask.getnumAgentsNeeded());
            
            double value = 1.0 / tval * (pval) * (availTask.getCurrentReward() + tval);
            //System.err.printf("agent id %d Task id %d, pval %f, value %f\n",agentID, availTask.getID(), pval, value);
            
            if (value > max) {
                max = value;
                curTask = availTask;
            }
        }
        return curTask;
    }
    @Override
    double getPValue(Task availTask) {
        return pTable.getQValue(availTask.getID(), 0);
    }
    
    private void learn(Task curTask, double reward, int numTimeSteps) {
        if(reward == 1.0) {
            //updateLearningRate(timeTable.getQValue(curTask.getID(), 0), numTimeSteps); // really bad
            
            timeTable.update(curTask.getID(), 0, numTimeSteps);
            pTable.update(curTask.getID(), 0, reward);
        }else{
            pTable.update(curTask.getID(), 0, reward);
        }
        if (this.hasOneUp)
                pTable.oneUpdate(oneUpdateGamma);
    }
    public void updateLearningRate(double oldT, double newT) {
        double f = Math.abs(newT - oldT) / oldT;
        timeTable.setAlpha(tTableLearningRate*f);
        pTable.setAlpha(pTableLearningRate*f);
        
    }

    public void learn(Task curTask, double reward, Bag agentsWorking, int numTimeSteps) {
        double oldT = timeTable.getQValue(curTask.getID(), 0);
        learn(curTask, reward, numTimeSteps);// I don't use agentsWorking.
        updateEpsilon(oldT, timeTable.getQValue(curTask.getID(), 0));
        
        System.err.println("Number of agents working on task: " + agentsWorking.numObjs + "\nMy id = " + this.agentID);
        if (reward == .25) {
            System.err.println("Jumpship learning");
        }
        else {
            System.err.println("Finished task learning");
        }
        for (int i = 0; i < agentsWorking.numObjs; i++) {
            System.err.println("agent working: " + (agentsWorking.get(i)));
        }
    }
    
     public void updateEpsilon(double oldT, double newT) {
         double delta = (1.0 / (double)this.numTasks * 2);
        epsilonChooseRandomTask = (1.0 - delta)*epsilonChooseRandomTask +
                                    delta*(boltzman(oldT,newT, .95));
    }
     
     public double boltzman(double oldT, double newT, double sigma) {
         return ( 1 - Math.exp(-Math.abs(newT - oldT) / sigma)) /
                 (1 + Math.exp(-Math.abs(newT - oldT) / sigma));
     }

}
