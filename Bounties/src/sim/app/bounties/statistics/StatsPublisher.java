/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.bounties.statistics;

import java.io.PrintWriter;
import sim.app.bounties.Bounties;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;

/**
 *
 * @author dfreelan
 */
public class StatsPublisher implements Steppable{
    Bounties board  = null;
    Bag bagOfTotal = new Bag();
    public StatsPublisher(Bounties a, int numSteps){
        this.board = a;
    }
    @Override
    public void step(SimState state) {
        
        bagOfTotal.add(board.getTotalTicks());
        if(state.schedule.getSteps() >= 45000){
           try{ PrintWriter writer = new PrintWriter("/Users/dfreelan/initialTest.test" + state.seed(), "UTF-8");
            for(int i = 0; i<bagOfTotal.numObjs; i++){
                writer.print(((Double)bagOfTotal.objs[i]) + ",");
            }
            writer.close();}catch(Exception e){e.printStackTrace(); System.exit(0);}
        }
    }
   
}
