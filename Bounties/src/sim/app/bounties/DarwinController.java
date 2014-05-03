package sim.app.bounties;


import sim.app.bounties.robot.darwin.agent.Darwin;
import sim.app.bounties.robot.darwin.agent.Darwins;
import sim.app.bounties.robot.darwin.agent.Real;
import sim.app.bounties.robot.darwin.behaviors.Motions;
import sim.app.bounties.robot.darwin.comm.DarwinParser;
import sim.engine.SimState;
import sim.util.Int2D;

/**
 *
 * @author drew
 */
public class DarwinController implements IController{

    
    IRobot me;
    static Darwins[] available = new Darwins[4];
        {
        available[0] = Darwins.FIFTY;
        available[1] = Darwins.FIFTYONE;
        available[2] = Darwins.FIFTYTWO;
        available[3] = Darwins.FIFTYTHREE;
        }
        
    Darwin darwin;
    Real prevTaskPos, prevGoalPos;
    public DarwinController(int id) {
        darwin = available[id].build();
    }
    
    
    @Override
    public boolean gotoPosition(SimState state, Int2D position) {
        return false;
    }

    @Override
    public boolean gotoGoalPosition(SimState state, Real position) {
        if (prevGoalPos == null || !prevGoalPos.getLocation().equals(position.getLocation())) {
            prevGoalPos = position;
            int x = (int) (((DarwinParser)darwin.getParser()).getPoseX() + 30);
            int y = (int) (((DarwinParser)darwin.getParser()).getPoseY() + 20);
            
            ((Bounties) state).robotgrid.setObjectLocation(me, x, y);
                    
            gotoPosition(state, null);
            darwin.sendCommand(Motions.getGotoPose(position.getRealTargetLocation().x, position.getRealTargetLocation().y, 0));
            if (((DarwinParser)darwin.getParser()).getReady() == 1) {
                return true;
            }
        }
       return false;
    }

    @Override
    public boolean gotoTaskPosition(SimState state, Real position) {
        if (prevTaskPos == null || !prevTaskPos.getLocation().equals(position.getLocation())) {
            prevTaskPos = position;
            int x = (int) (((DarwinParser)darwin.getParser()).getPoseX() + 30);
            int y = (int) (((DarwinParser)darwin.getParser()).getPoseY() + 20);
            
            Bounties af = (Bounties) state;
            ((Bounties) state).robotgrid.setObjectLocation(me, x, y);
            darwin.sendCommand(Motions.getGotoPose(position.getRealTargetLocation().x, position.getRealTargetLocation().y, 0));
            if (((DarwinParser)darwin.getParser()).getReady() == 1) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setMyRobot(IRobot robot) {
        me = robot;
    }
    
}
