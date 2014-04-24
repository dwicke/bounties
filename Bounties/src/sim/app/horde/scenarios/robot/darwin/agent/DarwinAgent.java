/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.horde.scenarios.robot.darwin.agent;


import sim.app.horde.Horde;
import sim.app.horde.agent.Agent;
import sim.app.horde.agent.SimAgent;
import sim.app.horde.behaviors.Behavior;
import sim.app.horde.behaviors.Macro;
import sim.app.horde.scenarios.robot.behaviors.CommandMotions;
import sim.app.horde.scenarios.robot.darwin.comm.DarwinParser;
import sim.engine.SimState;
import sim.util.Double2D;

/**
 * 
 * @author drew
 */
public class DarwinAgent extends SimAgent implements Real
{
        private static final long serialVersionUID = 1;

static Darwins[] available = new Darwins[4];
        {
        available[0] = Darwins.FIFTY;
        available[1] = Darwins.FIFTYONE;
        available[2] = Darwins.FIFTYTWO;
        available[3] = Darwins.FIFTYTHREE;
        }
    
    Darwin bot;
    int myID = 0;
    public Behavior prevBehavior;
    
     public String getLabel(Agent agent)
     	{
     	String s = super.getLabel(agent);
     	return this + "\n" + s;
     	}


    public DarwinAgent(int id) {
        super();
        myID = id;
        this.bot = available[myID].build();
        }

    
     /*
  +-------------+
  |+-----+-----+|
  ||     |     ||
  |+-----C-----+|
  ||     |     ||
  |B-----+-----+|
  A-------------+

  In Horde World, units of measure are in cm
  In Horde World, A is (0,0), B is (2.6, 2.0), and C is (30.0, 20.0)
  In Real World, A is (-30.0, -20.0), B is (-27.4, -18.0), and C is (0.0, 0.0)
*/

 public Double2D getRealTargetLocation(Agent agent, Macro parent, Horde horde)
        {
        Double2D d = getLocation();
        return new Double2D((d.x - 30) * 0.1, (d.y - 20) * 0.1);
        }
 
 public double getOrientation(Agent agent, Macro parent, Horde horde) { return getOrientation(); }

 public void step(SimState state)
        {
        super.step(state);
                
        DarwinParser dp = getCurrentData();
        
        //System.err.println("Parser " + dp + " for " + myID);
        if (dp != null)
            {
            // get current agent location                     
            double x = dp.getPoseX() * 10;
            double y = dp.getPoseY() * 10;
            double theta = dp.getPoseAngle();
                //System.err.println("X, y theta " + x + "  " + y + " " + theta + " id = " + myID);
            // translate from C to A
            x += 30;
            y += 20;
            Double2D loc = new Double2D(x,y);
            //System.err.println("Translated Agent Location: " + loc);
            setLocation(loc);
            setOrientation(theta);
            }
        }
    
    
    public DarwinParser getCurrentData() {
        //System.err.println("Parser for id = " + myID + " " + bot.getParser());
        return (DarwinParser) bot.getParser();
        }
    
    public void sendMotion(CommandMotions cms) {
        // interact with the robot to send the command
        bot.sendCommand(cms);
        }
    
    public void sendMotion(CommandMotions cms, byte customSpeed) {
        bot.sendCommand(cms, customSpeed);
        }
    }
