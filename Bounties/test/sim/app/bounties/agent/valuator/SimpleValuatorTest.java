/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.agent.valuator;

import ec.util.MersenneTwisterFast;

import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sim.app.bounties.Task;

/**
 *
 * @author drew
 */
public class SimpleValuatorTest {
    
    public SimpleValuatorTest() {
        tasks = new Task[2];
        tasks[0] = new Task();
        tasks[1] = new Task();
        tasks[0].setID(0);
        tasks[1].setID(1);
    }
    MersenneTwisterFast random = new MersenneTwisterFast(1001);
    double epsilonChooseRandomTask = .001;
    int agentID = 1;
    int numTasks = 2;
    int numRobots = 2;
    boolean hasOneUpdate = false;
    SimpleValuator sval = new SimpleValuator(random, epsilonChooseRandomTask, agentID, hasOneUpdate, numTasks, numRobots);
    
    Task [] tasks;
    
    
    @Test
    public void testgetPValue() {
        double pval0 = sval.getPValue(tasks[0]);
        // they should be the same
        assertEquals(pval0, sval.pTable.getQValue(tasks[0].getID(), 0));
    }
    
    @Test
    public void testLearn() {
        
        double tvalBefore = sval.timeTable.getQValue(tasks[0].getID(), 0);
        double pvalBefore = sval.pTable.getQValue(tasks[0].getID(), 0);
        sval.learn(tasks[0], 10.0, null, numTasks);
        double tvalAfter = sval.timeTable.getQValue(tasks[0].getID(), 0);
        double pvalAfter = sval.pTable.getQValue(tasks[0].getID(), 0);
        System.out.println("tvals = " + tvalBefore + " " + tvalAfter);
        System.out.println("pvals = " + pvalBefore + " " + pvalAfter);
        assertEquals(tvalBefore, tvalAfter);
        assertNotEquals(pvalBefore,pvalAfter);
        
        
        
        tvalBefore = sval.timeTable.getQValue(tasks[0].getID(), 0);
        pvalBefore = sval.pTable.getQValue(tasks[0].getID(), 0);
        sval.learn(tasks[0], 1.0, null, numTasks);
        tvalAfter = sval.timeTable.getQValue(tasks[0].getID(), 0);
        pvalAfter = sval.pTable.getQValue(tasks[0].getID(), 0);
        System.out.println("2tvals = " + tvalBefore + " " + tvalAfter);
        System.out.println("2pvals = " + pvalBefore + " " + pvalAfter);
        assertNotEquals(tvalBefore, tvalAfter);
        assertNotEquals(pvalBefore,pvalAfter);
        
    }
    
    @Test
    public void testLearnWithOneUpdate() {
        SimpleValuator sval2 = new SimpleValuator(random, epsilonChooseRandomTask, agentID, hasOneUpdate, numTasks, numRobots);
        
        double tvalBefore = sval2.timeTable.getQValue(tasks[0].getID(), 0);
        double pvalBefore = sval2.pTable.getQValue(tasks[0].getID(), 0);
        sval2.learn(tasks[0], 10.0, null, 10);
        double tvalAfter = sval2.timeTable.getQValue(tasks[0].getID(), 0);
        double pvalAfter = sval2.pTable.getQValue(tasks[0].getID(), 0);
        System.out.println("tvals0 = " + tvalBefore + " " + tvalAfter);
        System.out.println("pvals0 = " + pvalBefore + " " + pvalAfter);
        assertEquals(tvalBefore, tvalAfter);
        assertNotEquals(pvalBefore,pvalAfter); //ptable should stay the same since reward is 1.0
        
        SimpleValuator svalOneupdate = new SimpleValuator(random, epsilonChooseRandomTask, agentID, !hasOneUpdate, numTasks, numRobots);
        //double tvalBefore1 = svalOneupdate.timeTable.getQValue(tasks[0].getID(), 0);
        double pvalBefore1 = svalOneupdate.pTable.getQValue(tasks[0].getID(), 0);
        //assertEquals(tvalBefore, tvalBefore1);
        assertEquals(pvalBefore, pvalBefore1);
        svalOneupdate.learn(tasks[0], 10.0, null, 100);
        //double tvalAfter1 = svalOneupdate.timeTable.getQValue(tasks[0].getID(), 0);
        double pvalAfter1 = svalOneupdate.pTable.getQValue(tasks[0].getID(), 0);
        //System.out.println("tvals1 = " + tvalBefore1 + " " + tvalAfter1);
        System.out.println("pvals1 = " + pvalBefore1 + " " + pvalAfter1);
        //assertEquals(tvalAfter, tvalAfter1);
        assertTrue(pvalAfter > pvalAfter1);// without a one update the value is greater than with a 1 update
    }
    
    
    
}
