/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.agent.control;

import sim.app.bounties.Task;
import sim.util.Bag;

/**
 *
 * @author drew
 */
public class SimpleValuator implements DecisionValuator {

    
    
    
    @Override
    public Task decideNextTask(Task[] availableTasks) {
        if(epsilonChooseRandomTask > random.nextDouble()){
            return (Task)availableTasks[random.nextInt(availableTasks.length)];
            
        }else{
            return pickTask(availableTasks);
        }
    }
    
 public Task pickTask(Task availTasks[]) {
        
        
        //System.err.println("Num Avail Tasks == " + availTasks.numObjs);
        Task curTask = null;
        double max = 0; 
       
        for (int i = 0; i < availTasks.length; i++) { // over all tasks
          
            double tval = timeTable.getQValue(availTasks[i].getID(), 0);
            double pval = pTable.getQValue(availTasks[i].getID(), 0);
            double value = 1.0/tval * pval*availTasks[i].getCurrentReward();
            //if (bountyState.schedule.getSteps() > 50000){
            if  (bondsman.whoseDoingTask(availTasks[i]).size() > 0){
                if(isExclusive == true)
                    value*=-1;
            }
            if(value > max)
            {
                max = value;
                curTask = availTasks[i];       
            }
        }
        
        return curTask;
        }

    @Override
    public void learn(Task curTask, double reward, Bag agentsWorking, int numTimeSteps) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

   
    
}
