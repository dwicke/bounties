/*
 Copyright 2009 by Sean Luke and George Mason University
 Licensed under the Academic Free License version 3.0
 See the file "LICENSE" for more information
 */
package sim.app.bounties;




import java.util.Arrays;
import sim.app.bounties.bondsman.Bondsman;
import sim.app.bounties.agent.valuator.BadValuator;
import sim.app.bounties.agent.valuator.DecisionValuator;
import sim.app.bounties.agent.valuator.RandomValuator;
import sim.app.bounties.agent.valuator.SemiOptimalValuator;
import sim.app.bounties.agent.valuator.LearningValuator;
import sim.app.bounties.agent.valuator.SimpleValuator;
import sim.app.bounties.agent.valuator.SeanAuctionValuator;
import sim.app.bounties.agent.valuator.ComplexValuator;
import sim.app.bounties.agent.Agent;
import sim.app.bounties.control.TeleportController;
import sim.app.bounties.agent.IAgent;
import sim.app.bounties.bondsman.AdaptiveBondsman;
import sim.app.bounties.statistics.StatsPublisher;
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
    public static String[] myArgs;


    public double[] rollingAverage = new double[1000];
    int avgCount = 0;
    
    public double[] rollingAverageRed = new double[1000];
    int avgRedCount = 0;
    
    public Bondsman bondsman;// the bondsman ie the task allocator
    
    public IAgent[] agents;// index into this array corresponds to its id
    
    public SparseGrid2D tasksGrid = new SparseGrid2D(GRID_WIDTH, GRID_HEIGHT);
    public SparseGrid2D robotgrid = new SparseGrid2D(GRID_WIDTH, GRID_HEIGHT);

    
    
    boolean rotateRobots  = false;
    boolean lastRotateValue = false;
    int offset = 0;
    
    long maxRotateSteps = 25000;
    int willRotate = 0; // 0 don't rotate 1 will rotate
    private int agentType = 0; // 0 - simple, 1 - simpleP, 2 - simpleR, 3 - complex, 4 - complexP, 5 - complexR, 6 - random, 7 - psuedoOptimal
    private int willdie = 0; // 0 - won't die, 1 - will die
    private int hasTraps = 0;
    public int numAgents = 4;
    public int numTasks = 20;
    public int numBadRobot = 0;
    private double epsilonChooseRandomTask = 0.1;
    double pUpdateValue = .001;
    public int isExclusive = 0;
    
    
    public void setWillRotate(int val) {
        this.willRotate = val;
    }
    
    public int getWillRotate() {
        return this.willRotate;
    }
    
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
        for (int x = 0; x < numAgents; x++) {
            
            IAgent bot = agents[x];
            
            bot.setRobotHome(quads[(offset+x)%4]);
        }
        rotateRobots = value;
    }
    public boolean getRotateRobots(){
        return rotateRobots;
    }
    
    
    public int getAgentType() {
        return agentType;
    }
    public void setAgentType(int agentType) {
        this.agentType = agentType;
    }
    public int getWillDie() {
        return willdie;
    }
    public void setWillDie(int willDie) {
        this.willdie = willDie;
    }
    public int getHasTraps() {
        return this.hasTraps;
    }
    public void setHasTraps(int hasTraps) {
        this.hasTraps = hasTraps;
    }
    public int getIsExclusive() {
        return isExclusive;
    }
    public void setIsExclusive(int isExclusive) {
        this.isExclusive = isExclusive;
    }

    public int getNumBadRobot() {
        return numBadRobot;
    }

    public void setNumBadRobot(int numBadRobot) {
        this.numBadRobot = numBadRobot;
    }
    
    
    
    
    public double getAverageTicks(){
        double sum =0;
        if(bondsman==null) return 0.0;
        Bag tasks = bondsman.getTasks();
        if(tasks==null) return -1;
        double count = 0;
        for(Object ob: tasks){
            sum+=((Task)ob).getCurrentReward();
            count++;
        }

         return sum/count;
    }
    public double getTotalRedunantAgents() {
        double sum = 0;
        if(bondsman == null || numAgents < 2) return 0.0;
        
        int[] nums = bondsman.whosDoingWhatTaskID.clone();
        Arrays.sort(nums);
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] == nums[i - 1] && nums[i] != -1) {
                sum++;
                
            }
        }
        return sum;
    }
    public double getAverageRedudantAgents() {
        return getTotalRedunantAgents() / (numAgents - 1);
    }
    
    public double getRollingAvergeRedudantAgents() {
        if (bondsman != null) {
          
            
            double sum = getAverageRedudantAgents();
            rollingAverageRed[avgRedCount] = sum;
            avgRedCount++;
            if(avgRedCount == rollingAverageRed.length) 
                avgRedCount= 0;
            sum = 0;
            for(int i = 0; i<rollingAverageRed.length; i++){
                sum+=rollingAverageRed[i];
            }
            sum/=rollingAverageRed.length;
            return sum;
        }
        return 0;
    }
    
    public double getTotalTicks(){
        double sum =0;
        if(bondsman==null) return 0.0;
        Bag tasks = bondsman.getTasks();
        if(tasks==null) return -1;
        for(Object ob: tasks){
             sum+=((Task)ob).getCurrentReward();
        }
        return sum;
    }
    public double getRollingAverageTicks(){
        if (bondsman != null) {
          
            
            double sum = getAverageTicks();
            rollingAverage[avgCount] = sum;
            avgCount++;
            if(avgCount == rollingAverage.length) 
                avgCount= 0;
            sum = 0;
            for(int i = 0; i<rollingAverage.length; i++){
                sum+=rollingAverage[i];
            }
            sum/=rollingAverage.length;
            return sum;
        }
        return 0;

    }
    public IAgent[] getAgents() {
        return agents;
    }
    // some properties
    public int getNumAgents() {
        return numAgents;
    }

    public void setNumAgents(int val) {
        if (val > 0) {
            numAgents = val;
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

    public void setNumTasks(int numTasks) {
        if (numTasks > 0)
            this.numTasks = numTasks;
    }

    public int getNumTasks() {
        return numTasks;
    }
    
    
    
    

    
    public Bounties(long seed) {
        super(seed);
    }
    
    
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
    
    
    public void start() {
        super.start();  // clear out the schedule
        
        long maxNumSteps = Long.MAX_VALUE;
        if(myArgs !=null && keyExists("-for", myArgs)) {
            maxNumSteps = Long.parseLong(argumentForKey("-for", myArgs));
        }
        String dir = "C:\\Users\\dfreelan\\Dropbox";
        if(myArgs !=null && keyExists("-dir", myArgs)) {
            dir = argumentForKey("-dir", myArgs);
        }

        // rotates robots on this timestep
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
        // 0 - won't die, 1 - will die as defined in Agent
        if(myArgs !=null && keyExists("-die", myArgs)) {
            willdie = Integer.parseInt(argumentForKey("-die", myArgs));
        }
        // n - number of bad robots all located in the top left corner (0,0) currently
        if(myArgs !=null && keyExists("-bad", myArgs)) {
            numBadRobot = Integer.parseInt(argumentForKey("-bad", myArgs));
        }
        // 0 - no traps, 1 - traps meaning that p=.1 that there exists an agent that is bad at a particular task and that agent is uniformly chosen
        if(myArgs !=null && keyExists("-ptrap", myArgs)) {
            hasTraps = Integer.parseInt(argumentForKey("-ptrap", myArgs));
        }
        // the gamma value to use to update the p table (used to move value closer to 1 and is an exploration technique
        if(myArgs !=null && keyExists("-pval", myArgs)) {
            pUpdateValue = Double.parseDouble(argumentForKey("-pval", myArgs));
        }
        // the prob that the robot chooses a random task normally .1
        if(myArgs !=null && keyExists("-epsRand", myArgs)) {
            epsilonChooseRandomTask = Double.parseDouble(argumentForKey("-epsRand", myArgs));
        }
        // 0 if more than one agent can go after a task, 1 if only one can go after, 2 if bondsman decides (initially random)
        if(myArgs !=null && keyExists("-exclType", myArgs)) {
            isExclusive = Integer.parseInt(argumentForKey("-exclType", myArgs));
        }
        
        numAgents+=numBadRobot;

        bondsman = new AdaptiveBondsman(this, isExclusive);
        
        // make new grids
        tasksGrid = new SparseGrid2D(GRID_WIDTH, GRID_HEIGHT);
        
        // set up the tasks in each of the quadrants
        Bag tasksLocs = bondsman.initTasks(new Int2D(tasksGrid.getWidth(), tasksGrid.getHeight()));// bottom left
        
        for (int i = 0; i < tasksLocs.numObjs; i++) {
            Task curTask = ((Task)(tasksLocs.objs[i]));
            tasksGrid.setObjectLocation(tasksLocs.objs[i], curTask.getLocation());
        }
        
        agents = new IAgent[numAgents];
        robotgrid = new SparseGrid2D(GRID_WIDTH, GRID_HEIGHT);
        
        // create the edge robots usually this should just be numRobots but we want a center so don't
        createEdgeRobots(numAgents,numBadRobot);
        
        
        // Now make BadRobots
        for (int i = numAgents-numBadRobot; i < numAgents; i++) {
            agents[i] = createBadBot(i);
            schedule.scheduleRepeating(Schedule.EPOCH + i, 0, (Steppable)agents[i], 1);
        }
        
        StatsPublisher stats = new StatsPublisher(this, maxNumSteps,dir);
        // now schedule the bondsman so that it can add more tasks as needed.
        schedule.scheduleRepeating(Schedule.EPOCH+numAgents,0, bondsman, 1);
        //schedule statistics gatherer
        schedule.scheduleRepeating(Schedule.EPOCH+numAgents+1,0,stats,1);
        if (willRotate == 1)
            schedule.scheduleRepeating(Schedule.EPOCH+numAgents+2,0,new RotateBots(maxRotateSteps),1);
        
        
    }
    
    
    public IAgent createBadBot(int badID) {
        IAgent br = new Agent();
        br.setId(badID);
        br.setIsBad(true);
        br.setDecisionValuator(new BadValuator(random, badID, bondsman));
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
        Bag auctionVals = new Bag();
        
        for (int x = 0; x < numBots-badRobots; x++) {
            
            IAgent bot = new Agent();
            DecisionValuator valuator = null;
            
             // 0 - simple, 1 - simpleP, 2 - simpleR, 3 - complex, 4 - complexP, 5 - complexR, 6 - random, 7 - psuedoOptimal
            switch(agentType)        
            {
                case 0:// Simple
                    valuator = new SimpleValuator(random, 0, x, false, numTasks, numAgents);
                    break;
                case 1:// simple with one update
                    valuator = new SimpleValuator(random, 0, x, true, numTasks, numAgents);
                    break;
                case 2:// simple with random exploration
                    valuator = new SimpleValuator(random, epsilonChooseRandomTask, x, false, numTasks, numAgents);
                    break;
                case 3:// complex
                    valuator = new ComplexValuator(random, 0, x, false, numTasks, numAgents);
                    break;
                case 4:// complex with one update
                    valuator = new ComplexValuator(random, 0, x, true, numTasks, numAgents);
                    break;
                case 5:// complex with random exploration
                    valuator = new ComplexValuator(random, epsilonChooseRandomTask, x, false, numTasks, numAgents);
                    break;
                case 6:// randomly chosen task
                    valuator = new RandomValuator(random, x);
                    break;
                case 7:// semi optimal 
                    valuator = new SemiOptimalValuator(random, 0, x, quads[x%4]);
                    break;
                case 8:// sean auction
                    valuator = new SeanAuctionValuator(random, 0, x, false, numTasks, numAgents);
                    auctionVals.add(valuator);
                    break;
                default:
                    break;
            }
            
            bot.setHasTraps(hasTraps == 1);
            bot.setCanDie(willdie == 1);
            if(valuator instanceof LearningValuator)
                (( LearningValuator)valuator).setOneUpdateGamma(pUpdateValue);
            agents[x] = bot;
            bot.setId(x);
            bot.setDecisionValuator(valuator);
            robotgrid.setObjectLocation(bot, quads[x%4]);
            bot.setRobotHome(quads[x%4]);
            
            
            TeleportController t = new TeleportController();
            t.setMyRobot(bot);
            bot.setRobotController(t);
            schedule.scheduleRepeating(Schedule.EPOCH + x, 0, bot, 1);
            
        }
        
        // first ensure the auction bots know who the other auction bots are
        for(Object ob : auctionVals) {
            ((SeanAuctionValuator)ob).setAuctionCompetitors((SeanAuctionValuator[])auctionVals.toArray());
        }
        
        for (int i = 0; i < numBots-badRobots; i++) {
            agents[i].init(this);
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
    
    public Bondsman getBondsman() {
        return bondsman;
    }

    public static void main(String[] args) {
        myArgs = args;
        doLoop(Bounties.class, args);
        System.exit(0);
    }
}