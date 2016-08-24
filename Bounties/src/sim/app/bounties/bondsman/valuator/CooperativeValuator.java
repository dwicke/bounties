/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.bondsman.valuator;

import sim.app.bounties.Bounties;
import sim.app.bounties.environment.Task;

/**
 *
 * @author drew
 */
public class CooperativeValuator extends DefaultBondsmanValuator{

    
    int prevAgentCount[];
    int adjBounty[];
    int numAdj;
    public CooperativeValuator(Bounties bounties) {
        super(bounties);
    }
    
    
    /**
     * So basically we have to look at how many agents there are and how many 
     * agents need to be at that task.  From that can adjust the bounty accordingly
     * We don't want to make it so that if the task requires 2 agents to complete
     * and there are 4 agents and there are 4 tasks and they each go after different
     * tasks and for all of the tasks to then be adjusted by the same amount.
     * 
     * 
     * Basically i need to be fair and also guaruntee that the tasks won't be the
     * exact same...
     */
//    @Override
//    public void incrementBounty(){
//        int availAgents = bounties.numAgents - getNumAgentsAtTasks();
//        // don't increment more than 1/maxnumagentreq*numagents
//        System.err.println("In Increment");
//        for(int i = 0; i< tasks.size(); i++){
//           // if (((Task) tasks.objs[i]).getIsAvailable()) // only increment the 
//            Task incTask = ((Task)tasks.objs[i]);
//            int numAgentsAtTask = incTask.getNumAgentsAtTask();
//            // I think we need to change both the increment amount and the 
//            // current bounty
//            // need to know the number of required agents for this task nN
//            // number of available agents not already at a task nA
//            // number of agents at this task nH
//            // currentBounty = currentBounty + defaultBounty* nA/(nN-nH)
//            incTask.incrementCurrentReward(incrementAmount[incTask.getID()]);
//            
//            if (prevAgentCount[i] < numAgentsAtTask && numAdj == 0) {
//                prevAgentCount[i] = numAgentsAtTask;
//                adjBounty[i] = 1;
//                numAdj++;
//                incrementAmount[incTask.getID()] += 2;
//                //incTask.setCurrentReward(incTask.getCurrentReward() + incTask.getDefaultReward()*(availAgents)/(incTask.getnumAgentsNeeded()-numAgentsAtTask));
//            }
//            //System.err.printf("(%d, %d, %d),", incTask.getID(), incTask.getCurrentReward(), incTask.getNumAgentsAtTask());
//        }
//        //System.err.print("\n");
//        
//        
//    }

//    @Override
//    public void finishTask(Task curTask, int robotID, long timestamp, int numTimeSteps) {
//        super.finishTask(curTask, robotID, timestamp, numTimeSteps);
//        prevAgentCount[curTask.getID()] = 0;
//        if (adjBounty[curTask.getID()] == 1) {
//            numAdj--;
//            adjBounty[curTask.getID()] = 0;
//            incrementAmount[curTask.getID()] = 1;
//        }
//    }
//    
//    
//    
//    public int getNumAgentsAtTasks() {
//        int count = 0;
//        for(int i = 0; i < tasks.size(); i++){
//            count += ((Task)tasks.objs[i]).getNumAgentsAtTask();
//        }
//        return count;
//    }
}
