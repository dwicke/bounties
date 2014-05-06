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
    int id;
    static Darwins[] available = new Darwins[4];
        {
        available[1] = Darwins.FIFTYTWO;
        available[0] = Darwins.FIFTYTHREE;
        available[2] = Darwins.FIFTYONE;
        available[3] = Darwins.FIFTY;
        
        }
        
    Darwin darwin;
    Real prevTaskPos, prevGoalPos;
    public DarwinController(int id) {
        darwin = available[id].build();
        this.id = id;
    }
    
    
    @Override
    public boolean gotoPosition(SimState state, Int2D position) {
        return false;
    }

    @Override
    public boolean gotoGoalPosition(SimState state, Real position) {
        if (prevGoalPos == null || !prevGoalPos.getLocation().equals(position.getLocation())) {
            prevGoalPos = position;
            int x = (int) (((DarwinParser)darwin.getParser()).getPoseX() * 10 + 30);
            int y = (int) (((DarwinParser)darwin.getParser()).getPoseY() * 10 + 20);
            
            ((Bounties) state).robotgrid.setObjectLocation(me, x, y);
                    
            gotoPosition(state, null);
            darwin.sendCommand(Motions.getGotoPose(position.getRealTargetLocation().x, position.getRealTargetLocation().y, 0));
            prevTaskPos = null; // reset;
        }
        // return true if i have reached the goal location else false.
       return ((DarwinParser)darwin.getParser()).getReady() == 1 || 
               (Math.abs(((DarwinParser)darwin.getParser()).getPoseX() - position.getRealTargetLocation().x) <= .2 &&
               Math.abs(((DarwinParser)darwin.getParser()).getPoseY() - position.getRealTargetLocation().y) <= .2 );
    }

    boolean isReady = false;
    boolean sentApproach = false;
    @Override
    public boolean gotoTaskPosition(SimState state, Real position) {
        if (darwin.isConnected() && ((DarwinParser)darwin.getParser()).hasData()) {
            int x = (int) (((DarwinParser)darwin.getParser()).getPoseX() * 10 + 30);
            int y = (int) (((DarwinParser)darwin.getParser()).getPoseY() * 10 + 20);

            Bounties af = (Bounties) state;
            af.robotgrid.setObjectLocation(me, x, y);
            System.err.println("ID" + id + "Darwin loc = " + x + " "  + y + " from robot: " + ((DarwinParser)darwin.getParser()).getPoseX() + " " + ((DarwinParser)darwin.getParser()).getPoseY() + " Target loc:" + position.getRealTargetLocation().toCoordinates()
                    + " isReady = " + isReady + " SentApproach="+ sentApproach + 
                    " Darwin Ready to kick: " + ((DarwinParser)darwin.getParser()).doneApproach());

        }
        else {
            System.err.println("Connection: " + darwin.isConnected() + "  has data: " + ((DarwinParser)darwin.getParser()).hasData());
        }
        
        if (prevTaskPos == null || !prevTaskPos.getLocation().equals(position.getLocation())) {
            isReady = false; // must reset since must be going to a new location
            sentApproach = false; // must reset since we must be going to a new location
            prevGoalPos = null; // reset that the goal.
            prevTaskPos = position;
            int x = (int) (((DarwinParser)darwin.getParser()).getPoseX() * 10 + 30);
            int y = (int) (((DarwinParser)darwin.getParser()).getPoseY() * 10 + 20);
            
            Bounties af = (Bounties) state;
            ((Bounties) state).robotgrid.setObjectLocation(me, x, y);
            // only want to send the command once but must check it multiple times.
            darwin.sendCommand(Motions.getGotoPose(position.getRealTargetLocation().x, position.getRealTargetLocation().y, 0));
            
        }
        
        if (isReady == false && sentApproach == false && 
                ( ((DarwinParser)darwin.getParser()).getReady() == 1 || 
                (Math.abs(((DarwinParser)darwin.getParser()).getPoseX() - position.getRealTargetLocation().x) <= .2 &&
                Math.abs(((DarwinParser)darwin.getParser()).getPoseY() - position.getRealTargetLocation().y) <= .2 ))) {
                isReady = true;
                return false;// don't want to be done until I have kicked
        } 
        
        // we have to locate the ball
        if (isReady == true && sentApproach == false) {
            darwin.sendCommand(Motions.MOVE_THETA);
            int x = (int) (((DarwinParser)darwin.getParser()).getPoseX() * 10 + 30);
            int y = (int) (((DarwinParser)darwin.getParser()).getPoseY() * 10 + 20);
            
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
                System.err.println("Darwin ID = " + id + " Sent kick.");
                darwin.sendCommand(Motions.STOP);
                System.err.println("Darwin ID = " + id + " Sent Stop.");
                int x = (int) (((DarwinParser)darwin.getParser()).getPoseX() * 10 + 30);
                int y = (int) (((DarwinParser)darwin.getParser()).getPoseY() * 10 + 20);

                Bounties af = (Bounties) state;
                ((Bounties) state).robotgrid.setObjectLocation(me, x, y);
                sentApproach = false;
                isReady = false;
                return true; // finished gototask now that we have kicked
            }
            // make more robust so that if afters so  long of trying to approach the ball
            // check if it can actually see it and if not rotate and then gotoball
            // then approach then kick...
            
        }
        
        
        return false;
    }

    @Override
    public void setMyRobot(IRobot robot) {
        me = robot;
    }
    
}
