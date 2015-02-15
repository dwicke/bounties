/*
 Copyright 2009 by Sean Luke and George Mason University
 Licensed under the Academic Free License version 3.0
 See the file "LICENSE" for more information
 */
package sim.app.bounties;




import sim.app.bounties.control.TeleportController;
import sim.app.bounties.agent.SemiOptimalRobot;
import sim.app.bounties.agent.BadRobot;
import sim.app.bounties.agent.IAgent;
import sim.app.bounties.agent.AbstractRobot;
import sim.app.bounties.agent.NewSimpleRobot;
import sim.app.bounties.agent.NewComplexRobot;
import sim.app.bounties.agent.RandomRobot;
import sim.app.bounties.agent.SeanAuctionRobot;
import sim.app.bounties.jumpship.Jumpship;
import sim.app.bounties.jumpship.ResetJumpship;
import sim.app.bounties.statistics.StatsPublisher;
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
    public int numRobots = 4;
    public static String[] myArgs;
    
    public IAgent robots[];// index into this array corresponds to its id
    
    int numTasks = 20;
    int numGoals = 1;    
    double averageTicks = 0;
    boolean rotateRobots  = false;
    boolean lastRotateValue = false;
    int offset = 0;
    long maxRotateSteps = 25000;
    int willRotate = 0; // 0 don't rotate 1 will rotate
    private int agentType = 0; // 0 - simple, 1 - simpleP, 2 - simpleR, 3 - complex, 4 - complexP, 5 - complexR, 6 - random, 7 - psuedoOptimal
    private int willdie = 0; // 0 - won't die, 1 - will die
    private int hasTraps = 0;
    
    public void setRotateRobots(boolean value){
           
        
        Int2D quads[] = new Int2D[4];
        quads[0] = new Int2D(0, 0);
        quads[1] = new Int2D(0, GRID_HEIGHT -1);
        quads[3] = new Int2D(GRID_WIDTH - 1, 0);
        quads[2] = new Int2D(GRID_WIDTH - 1, GRID_HEIGHT - 1);

        //robotgrid = new SparseGrid2D(GRID_WIDTH, GRID_HEIGHT);
        if(value!= lastRotateValue){
            lastRotateValue = value;
            offset++;
        }
        for (int x = 0; x < numRobots; x++) {
            //GreedyBot bot = new GreedyBot();

            
            IAgent bot = robots[x];
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
    public double getAverageStepping(){
        double sumTasks = 0;
        double sumBetrayed = 0;
        for(IAgent a : robots){
            NewSimpleRobot curBot = (NewSimpleRobot)a;
            sumTasks += curBot.totalTasksChosen;
            sumBetrayed +=  curBot.tasksNotTrusted;
        }
        return sumBetrayed/sumTasks;
    }
    
    public double getAverageTicks(){
        double sum =0;
        if(bondsman==null) return 0.0;
        Bag tasks = bondsman.getTasks();
        if(tasks==null) return -1;
        double count = 0;
        for(int i = 0; i< tasks.objs.length; i++){
            if(tasks.objs[i] !=null){ // shouldnt really be null normally.....
                sum+=((Task)tasks.objs[i]).getCurrentReward();
                count++;
            }
        }

         return sum/count;
    }
    public double getTotalTicks(){
        double sum =0;
        if(bondsman==null) return 0.0;
        Bag tasks = bondsman.getTasks();
        if(tasks==null) return -1;
        for(int i = 0; i< tasks.objs.length; i++){
            if(tasks.objs[i] !=null){ // shouldnt really be null normally.....
                sum+=((Task)tasks.objs[i]).getCurrentReward();
               
            }
        }
        return sum;
    }
    public double getRollingAverageTicks(){
        if (bondsman != null) {
          
            
            double sum = getAverageTicks();
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
    public IAgent[] getRobots() {
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
    
    static boolean keyExists(String key, String[] args) {
        
        for (String arg : args) {
            if (arg.equalsIgnoreCase(key)) {
                return true;
            }
        }
        return false;
    }

    static String argumentForKey(String key, String[] args) {
        for (int x = 0; x < args.length - 1; x++) // if a key has an argument, it can't be the last string
        {
            if (args[x].equalsIgnoreCase(key)) {
                return args[x + 1];
            }
        }
        return null;
    }
    double pUpdateValue = .001;
    public void start() {
        super.start();  // clear out the schedule
        int numBadRobot = 0;
        long maxNumSteps = Long.MAX_VALUE;
        if(myArgs !=null && keyExists("-for", myArgs)) {
            maxNumSteps = Long.parseLong(argumentForKey("-for", myArgs));
        }
        String dir = "C:\\Users\\dfreelan\\Dropbox";
        if(myArgs !=null && keyExists("-dir", myArgs)) {
            dir = argumentForKey("-dir", myArgs);
        }

        if(myArgs !=null && keyExists("-rot", myArgs)) {
            maxRotateSteps = Long.parseLong(argumentForKey("-rot", myArgs));
        }
        
        // 0 don't rotate 1 will rotate
        if(myArgs !=null && keyExists("-prot", myArgs)) {
            willRotate = Integer.parseInt(argumentForKey("-prot", myArgs));
        
        }
        // 0 - simple, 1 - simpleP, 2 - simpleR, 3 - complex, 4 - complexP, 5 - complexR, 6 - random, 7 - psuedoOptimal
        if(myArgs !=null && keyExists("-agt", myArgs)) {
            agentType = Integer.parseInt(argumentForKey("-agt", myArgs));
        }
        // 0 - won't die, 1 - will die
        if(myArgs !=null && keyExists("-die", myArgs)) {
            willdie = Integer.parseInt(argumentForKey("-die", myArgs));
        }
        if(myArgs !=null && keyExists("-bad", myArgs)) {
            numBadRobot = Integer.parseInt(argumentForKey("-bad", myArgs));
            
        }
        if(myArgs !=null && keyExists("-pval", myArgs)) {
            pUpdateValue = Double.parseDouble(argumentForKey("-pval", myArgs));
        }
        if(myArgs !=null && keyExists("-ptrap", myArgs)) {
            hasTraps = Integer.parseInt(argumentForKey("-ptrap", myArgs));
        }
        
        
        
        //willdie = 1;
        //willRotate = 1;
        numRobots+=numBadRobot;
        //maxRotateSteps= 25000;
        //maxRotateSteps = Long.MAX_VALUE;
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
        bondsman = new Bondsman(numGoals, numTasks, js,0);
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
        
        
        
        
        
        robots = new IAgent[numRobots];
        robotgrid = new SparseGrid2D(GRID_WIDTH, GRID_HEIGHT);
        
        // create the edge robots usually this should just be numRobots but we want a center so don't
        
        createEdgeRobots(numRobots,numBadRobot);
        
        
        // Now make a BadRobot in the center
        
        for (int i = numRobots-numBadRobot; i < numRobots; i++) {
            System.err.println("look here? " + i);
            robots[i] = createBadBot(i);
            schedule.scheduleRepeating(Schedule.EPOCH + i, 0, (Steppable)robots[i], 1);
        }
        

        //robotgrid/.setObjectLocation(bot, xloc, yloc);
        //robots[x].setRobotHome(new Int2D(xloc, yloc));
       

        
        
        
        
        
        StatsPublisher stats = new StatsPublisher(this, maxNumSteps,dir);
        // now schedule the bondsman so that it can add more tasks as needed.
        schedule.scheduleRepeating(Schedule.EPOCH+numRobots,0, bondsman, 1);
        //schedule statistics gatherer
        schedule.scheduleRepeating(Schedule.EPOCH+numRobots+1,0,stats,1);
        if (willRotate == 1)
            schedule.scheduleRepeating(Schedule.EPOCH+numRobots+2,0,new RotateBots(maxRotateSteps),1);
        
        
    }
    
    
    public BadRobot createBadBot(int badID) {
        BadRobot br = new BadRobot();
        br.setId(badID);
        Int2D center = new Int2D(0, 0);//new Int2D(GRID_WIDTH / 2, GRID_HEIGHT / 2);
        robotgrid.setObjectLocation(br, center);
        br.setRobotHome(center);
        br.init(this);
        TeleportController t = new TeleportController();
        t.setMyRobot(br);
        br.setRobotController(t);
        return br;
    }
   
    public void createEdgeRobots(int numBots, int badRobots) {
        
        Int2D quads[] = new Int2D[4];
        quads[0] = new Int2D(0, 0);
        quads[1] = new Int2D(0, GRID_HEIGHT -1);
        quads[3] = new Int2D(GRID_WIDTH - 1, 0);
        quads[2] = new Int2D(GRID_WIDTH - 1, GRID_HEIGHT - 1);        
        
        for (int x = 0; x < numBots-badRobots; x++) {
            //GreedyBot bot = new GreedyBot();

            //NewComplexRobotWithJumpship bot = new NewComplexRobotWithJumpship();
            
            IAgent bot = null;
            
             // 0 - simple, 1 - simpleP, 2 - simpleR, 3 - complex, 4 - complexP, 5 - complexR, 6 - random, 7 - psuedoOptimal
            //agentType = 3;
            switch(agentType)        
            {
                case 0:
                    bot = new NewSimpleRobot();
                    break;
                case 1:
                    bot = new NewSimpleRobot();
                    ((NewSimpleRobot)bot).setHasOneUpdate(true);
                    break;
                case 2:
                    bot = new NewSimpleRobot();
                    ((NewSimpleRobot)bot).setHasRandom(true);
                    break;
                case 3:
                    bot = new NewComplexRobot();
                    break;
                case 4:
                    bot = new NewComplexRobot();
                    ((NewComplexRobot)bot).setHasOneUpdate(true);
                    break;
                case 5:
                    bot = new NewComplexRobot();
                    ((NewComplexRobot)bot).setHasRandom(true);
                    break;
                case 6:
                    bot = new RandomRobot();
                    break;
                case 7:
                    bot = new SemiOptimalRobot();
                    break;
                case 8:
                    bot = new SeanAuctionRobot();
                    break;
                case 9:
                    bot = new NewSimpleRobot();
                    ((NewSimpleRobot)bot).isExclusive = true;
                    break;
                default:
                    break;
            }
            
            ((AbstractRobot)bot).hasTraps = hasTraps == 1;
            //willdie = 1;
            bot.setCanDie(willdie == 1);
            if(bot instanceof NewSimpleRobot)
                (( NewSimpleRobot)bot).setPUpdate(pUpdateValue);
            robots[x] = bot;
            bot.setId(x);
            //int xloc = random.nextInt(GRID_WIDTH);
            //int yloc = random.nextInt(GRID_HEIGHT);
            
            robotgrid.setObjectLocation(bot, quads[x%4]);
            robots[x].setRobotHome(quads[x%4]);
            
            //robotgrid/.setObjectLocation(bot, xloc, yloc);
            //robots[x].setRobotHome(new Int2D(xloc, yloc));
            bot.init(this);
            
            TeleportController t = new TeleportController();
            t.setMyRobot(bot);
            robots[x].setRobotController(t);
            schedule.scheduleRepeating(Schedule.EPOCH + x, 0, (Steppable)bot, 1);
            
        }
    }
    
    
    
    public class RotateBots implements Steppable {

        long rotateStep;
        boolean rotated = false;
        int howManyTimes = 0;
        public RotateBots(long rotateStep) {
            this.rotateStep = rotateStep;
        }
        
        @Override
        public void step(SimState state) {
            long numSteps = state.schedule.getSteps();
            if (numSteps % rotateStep == 0 && numSteps>0) {
                rotated = true;
                if(howManyTimes%2 == 0){
                    howManyTimes++;
                    setRotateRobots(!lastRotateValue);
                }else{
                    setRotateRobots(!lastRotateValue);
                    setRotateRobots(!lastRotateValue);// make them all go to opposite side of the board
                }
               
            }
        }
        
    }
    

    public static void main(String[] args) {
        myArgs = args;
        doLoop(Bounties.class, args);
        System.exit(0);
    }
}