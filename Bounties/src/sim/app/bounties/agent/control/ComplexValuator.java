/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.agent.control;

import ec.util.MersenneTwisterFast;
import sim.app.bounties.Task;
import sim.app.bounties.util.QTable;
import sim.util.Bag;

/**
 *
 * @author drew
 */
public class ComplexValuator implements DecisionValuator {

    MersenneTwisterFast random;
    double epsilonChooseRandomTask;
    int agentID;
    boolean hasOneUp;
    
    QTable timeTable; // time to do task
    QTable pTable; // probablility that I am successful at a task
    double oneUpdateGamma = .001;
    double tTableLearningRate = .1;
    double tTableDiscountBeta = .1;
    double pTableLearningRate = .2;
    double pTableDiscountBeta = .1;
    double initValue = 1;
    
    
    public ComplexValuator(MersenneTwisterFast random, double epsilonChooseRandomTask, 
            int agentID, boolean hasOneUp, int numTasks, int numRobots){
        this.random = random;
        this.epsilonChooseRandomTask = epsilonChooseRandomTask;
        this.agentID = agentID;
        this.hasOneUp = hasOneUp;
        timeTable = new QTable(numTasks, 1, tTableLearningRate, tTableDiscountBeta, initValue); 
        pTable = new QTable(numTasks, numRobots, pTableLearningRate, pTableDiscountBeta, initValue);
    }
    
    
    @Override
    public Task decideNextTask(Task availableTasks[]) {
        if(epsilonChooseRandomTask > random.nextDouble()){
            return (Task)availableTasks[random.nextInt(availableTasks.length)];
            
        }else{
            return pickTask(availableTasks);
        }
    }
    
    public Task pickTask(Task availTasks[]) {
        double max = -1;
        Task curTask = null;
        for (int i = 0; i < availTasks.length; i++) { // over all tasks

            double tval = timeTable.getQValue(availTasks[i].getID(), 0);
            double pval = getPValue(availTasks[i]);
            double value = 1.0 / tval * pval * availTasks[i].getCurrentReward();
           
            
            if (value > max) {
                max = value;
                curTask = availTasks[i];
            }
        }
        return curTask;
    }
    
    public double getPValue(Task taski) {
        
        if (taski.getLastAgentsWorkingOnTask().isEmpty()) {
            return 1.0;
        }
        
        double pmul = 1.0;
        
        for(int i = 0; i < taski.getLastAgentsWorkingOnTask().size(); i++) {
            if ((int)(taski.getLastAgentsWorkingOnTask().objs[i]) != agentID)
                pmul *= pTable.getQValue(taski.getID(), (int)(taski.getLastAgentsWorkingOnTask().objs[i]));
        }
        return pmul;
        
    }

    @Override
    public void learn(Task curTask, double reward, Bag agentsWorking, int numTimeSteps) {
        if(reward == 1.0) {
            //System.err.println("numSteps = " + numTimeSteps);
            timeTable.update(curTask.getID(), 0, numTimeSteps);
            for (int i = 0; i < curTask.getLastAgentsWorkingOnTask().numObjs; i++) {
                pTable.update(curTask.getID(), ((int)curTask.getLastAgentsWorkingOnTask().objs[i]), reward);
            }
            if (this.hasOneUp)
                pTable.oneUpdate(oneUpdateGamma);
            return;
        }
        pTable.update(curTask.getID(), curTask.getLastFinishedRobotID(), reward);
       if (this.hasOneUp)
                pTable.oneUpdate(oneUpdateGamma);
    }
    
}
