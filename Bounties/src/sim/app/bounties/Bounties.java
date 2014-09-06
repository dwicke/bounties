/*
 Copyright 2009 by Sean Luke and George Mason University
 Licensed under the Academic Free License version 3.0
 See the file "LICENSE" for more information
 */
package sim.app.bounties;


import sim.app.bounties.jumpship.Jumpship;
import sim.app.bounties.jumpship.LonelyJumpship;
import sim.app.bounties.jumpship.ResetJumpship;
import sim.display.Console;
import sim.engine.*;
import static sim.engine.SimState.doLoop;
import sim.field.grid.*;
import sim.util.*;
/**
 * 
 * @author drew
 */
public class Bounties extends SimState {

    private static final long serialVersionUID = 1;

    public static final int GRID_HEIGHT = 40;
    public static final int GRID_WIDTH = 60;

    public Console con;
    public double[] robotTabsCols;
    public double[] prevRobotTabsCols; // for debugging, see if to many people are getting reward
    public double[] rollingAverage = new double[1000];
    int avgCount = 0;
    public Bondsman bondsman;
    public int numRobots = 2;
    
    public IRobot robots[];// index into this array corresponds to its id
    
    int numTasks = 2;
    int numGoals = 1;    
    double averageTicks = 0;
    boolean rotateRobots  = false;
    boolean lastRotateValue = false;
    int offset = 0;
    public void setRotateRobots(boolean value){
           
        
        Int2D quads[] = new Int2D[4];
        quads[0] = new Int2D(0, 0);
        quads[1] = new Int2D(0, GRID_HEIGHT -1);
        quads[2] = new Int2D(GRID_WIDTH - 1, 0);
        quads[3] = new Int2D(GRID_WIDTH - 1, GRID_HEIGHT - 1);

        //robotgrid = new SparseGrid2D(GRID_WIDTH, GRID_HEIGHT);
        if(value!= lastRotateValue){
            lastRotateValue = value;
            offset++;
        }
        for (int x = 0; x < numRobots; x++) {
            //GreedyBot bot = new GreedyBot();

            
            IRobot bot = robots[x];
            //int xloc = random.nextInt(GRID_WIDTH);
            //int yloc = random.nextInt(GRID_HEIGHT);
            
            //robotgrid.setObjectLocation(bot, quads[x%4]);
            bot.setRobotHome(quads[(offset+x)%4]);
        }
        rotateRobots = value;
    }
    public boolean getRotateRobots(){
        return rotateRobots;
    }
    public void setConsole(Console con) {
        this.con = con;
    }
    public Console getConsole() {
        return con;
    }
    public double getStatistics(){
        //System.err.println("fun " + ((Robot)robots[1]).decisionsMade[0]);
        try{
            
            if(robots !=null && robots[0]!=null && ((AbstractRobot)robots[0]).decisionsMade!=null)
                return Math.abs(((AbstractRobot)robots[0]).decisionsMade[0]);
            
        }catch(Exception e){
            
            e.printStackTrace();
            System.exit(0);
        }
     return 10;
    }
    public double getAverageTicks(){
        if (bondsman != null) {
            Bag tasks = bondsman.getTasks();
            if(tasks==null) return -1;
            double sum =0;
            for(int i = 0; i< tasks.objs.length; i++){
                if(tasks.objs[i] !=null){ // shouldnt really be null normally.....
                    sum+=((Task)tasks.objs[i]).getCurrentReward();
                }
            }

            sum/=tasks.objs.length;
            rollingAverage[avgCount] = sum;
            avgCount++;
            if(avgCount == rollingAverage.length) avgCount= 0;
            sum = 0;
            for(int i = 0; i<rollingAverage.length; i++){
                sum+=rollingAverage[i];
            }
            sum/=rollingAverage.length;
            return sum;
        }
        return 0;
//getTasks
    }
    public IRobot[] getRobots() {
        return robots;
    }
    // some properties
    public int getNumRobots() {
        return numRobots;
    }

    public void setNumRobots(int val) {
        if (val > 0) {
            numRobots = val;
        }
    }
    public int getAvgCount() {
        return rollingAverage.length;
    }

    public void setAvgCount(int val) {
        if (val > 0) {
            rollingAverage = new double[val];
            
        }
    }
    public void setNumGoals(int numGoals) {
        if (numGoals > 0)
            this.numGoals = numGoals;
    }

    public void setNumTasks(int numTasks) {
        if (numTasks > 0)
            this.numTasks = numTasks;
    }

    public int getNumGoals() {
        return numGoals;
    }

    public int getNumTasks() {
        return numTasks;
    }
    
    
    
    

    public SparseGrid2D goalsGrid = new SparseGrid2D(GRID_WIDTH, GRID_HEIGHT);
    public SparseGrid2D tasksGrid = new SparseGrid2D(GRID_WIDTH, GRID_HEIGHT);
    public SparseGrid2D robotgrid = new SparseGrid2D(GRID_WIDTH, GRID_HEIGHT);

    
    public Bounties(long seed) {
        super(seed);
    }
    
    //debug thing again
    boolean firstTimeThrough = true;
    public double[] getRobotTabsCols() {
        // loop over the tasks and then the robots
        robotTabsCols = new double[numTasks];
        
        boolean debugFail = false;
        if (robots != null) {
            System.err.printf("sums: ");
            for (int i = 0; i < getNumTasks(); i++) {
               
                robotTabsCols[i] = 0;
                for (int j = 0; j < getNumRobots(); j++) {
                    robotTabsCols[i] += ((JointTaskQRobot)robots[j]).myQtable.getQValue(i, 0);
                }
                if(!firstTimeThrough)
                if(prevRobotTabsCols[i]-robotTabsCols[i] < -1){
                    debugFail = true;
                }
                System.err.printf("%.02f ",robotTabsCols[i]);
                prevRobotTabsCols[i] = robotTabsCols[i];
            }
            firstTimeThrough = false;
        }
        
        System.err.println();
        if(debugFail){
            //System.exit(0);
        }
        return robotTabsCols;
    }

    public void start() {
        super.start();  // clear out the schedule
        
        //debug 
        prevRobotTabsCols = new double[numTasks];
        //debug
        
        /*
        Jumpship methods can be constructed together like so:
        
        Jumpship js = new ResetJumpship(new LonelyJumpship())
        
        make sure that lonelyJumpship is the inner most one to be added
        otherwise the other penalties can be applied and
        
        */
        
        
        Jumpship js = new ResetJumpship();
        bondsman = new Bondsman(numGoals, numTasks, js);
        bondsman.setWorld(this);
        
        // make new grids
        goalsGrid = new SparseGrid2D(GRID_WIDTH, GRID_HEIGHT);
        
        // get the goal locations from the bondsman.
        Bag goalLocs = bondsman.initGoals(new Int2D(tasksGrid.getWidth(), tasksGrid.getHeight()),
                this.random);
        for (int i = 0; i < goalLocs.numObjs; i++) {
            Goal curGoal = ((Goal)(goalLocs.objs[i]));
            
            
            goalsGrid.setObjectLocation(goalLocs.objs[i], curGoal.getLocation());
        }
        
        tasksGrid = new SparseGrid2D(GRID_WIDTH, GRID_HEIGHT);
        
        
        
        
        // set up the tasks in each of the quadrants
        Bag tasksLocs = bondsman.initTasks(new Int2D(tasksGrid.getWidth(), tasksGrid.getHeight()),
                this.random);// bottom left
        
        for (int i = 0; i < tasksLocs.numObjs; i++) {
            Task curTask = ((Task)(tasksLocs.objs[i]));
            tasksGrid.setObjectLocation(tasksLocs.objs[i], curTask.getLocation()); 
            
            
        }
        
        
        robotTabsCols = new double[numTasks];
        
        
        Int2D quads[] = new Int2D[4];
        quads[0] = new Int2D(0, 0);
        quads[1] = new Int2D(0, GRID_HEIGHT -1);
        quads[2] = new Int2D(GRID_WIDTH - 1, 0);
        quads[3] = new Int2D(GRID_WIDTH - 1, GRID_HEIGHT - 1);
        
        
        robots = new IRobot[numRobots];
        robotgrid = new SparseGrid2D(GRID_WIDTH, GRID_HEIGHT);
        
        
        for (int x = 0; x < numRobots; x++) {
            //GreedyBot bot = new GreedyBot();

            GossipTableRobot bot = new GossipTableRobot();//139 //131
            robots[x] = bot;
            bot.setId(x);
            //int xloc = random.nextInt(GRID_WIDTH);
            //int yloc = random.nextInt(GRID_HEIGHT);
            
            robotgrid.setObjectLocation(bot, quads[x%4]);
            robots[x].setRobotHome(quads[x%4]);
            
            //robotgrid.setObjectLocation(bot, xloc, yloc);
            //robots[x].setRobotHome(new Int2D(xloc, yloc));
            
            
            TeleportController t = new TeleportController();
            t.setMyRobot(bot);
            robots[x].setRobotController(t);
            schedule.scheduleRepeating(Schedule.EPOCH + x, 0, bot, 1);
        }

        // now schedule the bondsman so that it can add more tasks as needed.
        schedule.scheduleRepeating(Schedule.EPOCH + numRobots, 0, bondsman, 1);
    }

    public static void main(String[] args) {
        doLoop(Bounties.class, args);
        System.exit(0);
    }
}
