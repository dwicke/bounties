/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties;

import ec.util.MersenneTwisterFast;
import static org.testng.Assert.*;
import org.testng.FileAssert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sim.util.Int2D;

/**
 *
 * @author drew
 */
public class TaskTest {
    
    public TaskTest() {
        
    }
    
    @Test
    public void testDone() {
        Task testTask = new Task();
        assertTrue(!testTask.isDone());
        testTask.setDone(true);
        assertTrue(testTask.isDone());
        testTask.setDone(false);
        assertTrue(!testTask.isDone());
    }
    
    @Test
    public void testLocation() {
        Task testTask = new Task();
        assertNull(testTask.getInitialPosition());
        Int2D initPos = new Int2D(2, 2);
        testTask.setInitialLocation(initPos);
        assertEquals(initPos, testTask.getInitialPosition());
        
        
        // now generate the real location
        assertNull(testTask.realLocation);
        testTask.generateRealTaskLocation(new MersenneTwisterFast(2));
        assertTrue(testTask.realLocation != null);
    }
    
    @Test
    public void testRespawnTime() {
        Task testTask = new Task();
        MersenneTwisterFast r = new MersenneTwisterFast(2);
        testTask.makeRespawnTime(r);
        int left = testTask.getTimeUntilRespawn();
        System.err.println("left: " + left);
        for (int i = 0; i < left - 1; i++) {// subtract one since the last time it will be true
            System.err.println("i = " + i);
            assertFalse(testTask.isTaskReady());
        }
        assertTrue(testTask.isTaskReady());// should be true
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeMethod
    public void setUpMethod() throws Exception {
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
    }
}
