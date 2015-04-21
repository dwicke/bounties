/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.bounties.statistics;

import java.io.File;
import java.io.PrintWriter;
import sim.app.bounties.Bounties;
import sim.app.bounties.Task;
import sim.app.bounties.agent.Agent;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;
import sim.util.Int2D;

/**
 *
 * @author dfreelan
 */
public class StatsPublisher implements Steppable{
    Bounties board  = null;
    Bag bagOfTotal = new Bag();
    Bag bagOfRedundantTotal = new Bag();
    Bag bagOfStepsTotal = new Bag();
    
    Bag[] arrayOfBagsOfDecisions = null;
    String directoryName;
    private long maxNumSteps;
    int numberOfDecisionsToRecord =10000;
    public StatsPublisher(Bounties a, long maxNumSteps, String dir){
        this.board = a;
        this.maxNumSteps = maxNumSteps;
        directoryName = dir;
        arrayOfBagsOfDecisions = new Bag[board.getNumAgents()];
        for(int i = 0; i<arrayOfBagsOfDecisions.length; i++){
            arrayOfBagsOfDecisions[i] = new Bag();
        }
        System.out.println("numROBOTS " + board.getNumAgents());
    
    }
    int previousID = 0;
    @Override
    public void step(SimState state) {
        //if(state.schedule.getSteps()== 190000 || state.schedule.getSteps()== 350000)
        //IRobot[] robots = a.getRobots();
        bagOfTotal.add(board.getTotalTicks());
        bagOfRedundantTotal.add(board.getTotalRedunantAgents());
        bagOfStepsTotal.add(board.getTotalLengthOnBoard());
        
        if( maxNumSteps - state.schedule.getSteps() < numberOfDecisionsToRecord)
        for(int i = 0; i<arrayOfBagsOfDecisions.length; i++){
            int lastDecision = ((Agent)board.getAgents()[i]).getLastDecision();
            if(previousID != lastDecision)
                arrayOfBagsOfDecisions[i].add(lastDecision);
           // System.err.println("key word" +  arrayOfBagsOfDecisions[i].objs[0]);
        }

        if(state.schedule.getSteps() >= maxNumSteps-2){
           try{
            File file = new File(directoryName + "/" + "maxTicks" + state.seed() + ".bounties");
            file.getParentFile().mkdirs();   
            PrintWriter writer = new PrintWriter(file, "UTF-8");
           
            for(int i = 0; i<bagOfTotal.numObjs; i++){
                writer.print(((Double)bagOfTotal.objs[i]) + ",");
            }
            
            
            File fileRed = new File(directoryName + "Red" + "/" + "NumRedundAg" + state.seed() + ".bounties");
            fileRed.getParentFile().mkdirs();   
            PrintWriter writerRed = new PrintWriter(fileRed, "UTF-8");
           
            for(int i = 0; i<bagOfRedundantTotal.numObjs; i++){
                writerRed.print(((Double)bagOfRedundantTotal.objs[i]) + ",");
            }
            
            
            
            File fileSteps = new File(directoryName + "Steps" + "/" + "NumSteps" + state.seed() + ".bounties");
            fileSteps.getParentFile().mkdirs();   
            PrintWriter writerSteps = new PrintWriter(fileSteps, "UTF-8");
           
            for(int i = 0; i<bagOfStepsTotal.numObjs; i++){
                writerSteps.print(((Double)bagOfStepsTotal.objs[i]) + ",");
            }
            
            
            
            Bag tasks = board.bondsman.getTasks();
            for(int i = 0; i<arrayOfBagsOfDecisions.length; i++){
                
                File file2 = new File(directoryName + "/" + "robot#" + i + "/" +  "locations" +state.seed() + ".bounties");
                file2.getParentFile().mkdirs();
                PrintWriter writer2 = new PrintWriter(file2, "UTF-8");
                System.out.println("arraybag robots" + arrayOfBagsOfDecisions[i]);
                for(int a = 0; a<arrayOfBagsOfDecisions[i].numObjs; a++){
                    Task theTask = (Task)tasks.get(Math.abs((Integer)arrayOfBagsOfDecisions[i].get(a)));
                    writer2.write(theTask.getLocation().toCoordinates());
                }
                writer2.close();
            }
            System.out.println("wrote to " + directoryName + "/" + "maxTicks" + state.seed() + ".bounties");
            writer.close();
            writerRed.close();
            writerSteps.close();
           }catch(Exception e){e.printStackTrace(); System.exit(0);}
        }
    }
   
}
