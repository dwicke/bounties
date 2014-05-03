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

    boolean isReady = false;
    boolean sentApproach = false;
    @Override
    public boolean gotoTaskPosition(SimState state, Real position) {
        if (prevTaskPos == null || !prevTaskPos.getLocation().equals(position.getLocation())) {
            isReady = false; // must reset since must be going to a new location
            sentApproach = false; // must reset since we must be going to a new location
            prevTaskPos = position;
            int x = (int) (((DarwinParser)darwin.getParser()).getPoseX() + 30);
            int y = (int) (((DarwinParser)darwin.getParser()).getPoseY() + 20);
            
            Bounties af = (Bounties) state;
            ((Bounties) state).robotgrid.setObjectLocation(me, x, y);
            darwin.sendCommand(Motions.getGotoPose(position.getRealTargetLocation().x, position.getRealTargetLocation().y, 0));
            if (((DarwinParser)darwin.getParser()).getReady() == 1) {
                isReady = true;
                return false;// don't want to be done until I have kicked
            } else {
                isReady = false;
            }
        }
        
        // we have to locate the ball
        if (isReady == true && sentApproach == false) {
            darwin.sendCommand(Motions.MOVE_THETA);
            int x = (int) (((DarwinParser)darwin.getParser()).getPoseX() + 30);
            int y = (int) (((DarwinParser)darwin.getParser()).getPoseY() + 20);
            
            Bounties af = (Bounties) state;
            ((Bounties) state).robotgrid.setObjectLocation(me, x, y);
            if (((DarwinParser)darwin.getParser()).detectBall() == 1) {
                
                darwin.sendCommand(Motions.APPROACH_BALL); // immediately approach ball before I loose it.
                sentApproach = true;
                isReady = false;
                return false;
            }
        }
        
        
        if (sentApproach) {
            if (((DarwinParser)darwin.getParser()).doneApproach() == 1) {
                darwin.sendCommand(Motions.KICK_BALL);
                int x = (int) (((DarwinParser)darwin.getParser()).getPoseX() + 30);
                int y = (int) (((DarwinParser)darwin.getParser()).getPoseY() + 20);

                Bounties af = (Bounties) state;
                ((Bounties) state).robotgrid.setObjectLocation(me, x, y);
                sentApproach = false;
                isReady = false;
                return true; // finished gototask now that we have kicked
            }
        }
        
        
        return false;
    }

    @Override
    public void setMyRobot(IRobot robot) {
        me = robot;
    }
    
}
