/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package continuouslgp.interpreter;

import continuouslgp.common.controlflow.ControlFlow;
import continuouslgp.common.operators.*;
import continuouslgp.common.operators.Operator;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author dfreelan
 */
public class InterpreterTest {
    
    public InterpreterTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of setRegisters method, of class Interpreter.
     */
    @Test
    public void testSetRegisters() {
        System.out.println("setRegisters");
        double[] registers = null;
        Interpreter instance = null;
        instance.setRegisters(registers);
        // TODO review the generated test code and remove the default call to fail.
       // fail("The test case is a prototype.");
    }

    /**
     * Test of doStep method, of class Interpreter.
     */
    @Test
    public void testDoStep() {
        System.out.println("doStep");
        
        //testing add 
        double[][] genome = {{1,0,1}};
        double registers[] = {1,1};
        Operator[] operators = new Operator[1];
        operators[0]  = new Add();
        ControlFlow[] controlflow = new ControlFlow[0];
        Interpreter instance = new Interpreter(genome,registers,1,operators,controlflow);
        instance.doStep();
        System.out.println("add result:" + instance.registers[1]);
        
        double[] expectedRegisterResult = {1,2};
        assertArrayEquals(expectedRegisterResult ,instance.registers,0.000001);
        // TODO review the generated test code and remove the default call to fail.
        
    }
    /*
       * Test of doStep method, of class Interpreter.
     */
    @Test
    public void testSub() {
        System.out.println("doStep");
        
        //testing add 
        double[][] genome = {{1,0,1}};
        double registers[] = {1,1};
        Operator[] operators = new Operator[1];
        operators[0]  = new Sub();
        ControlFlow[] controlflow = new ControlFlow[0];
        continuouslgp.interpreter.Interpreter instance = new continuouslgp.interpreter.Interpreter(genome,registers,1,operators,controlflow);
        instance.doStep();
        
        double[] expectedRegisterResult = {1,0};
        System.out.println(instance.registers[1]);
        assertArrayEquals(expectedRegisterResult ,instance.registers,0.000001);
        // TODO review the generated test code and remove the default call to fail.
        
    }
    /*
       * Test of doStep method, of class Interpreter.
     */
    @Test
    public void testMult() {
        System.out.println("doStep");
        
        //testing add 
        double[][] genome = {{1,0,1}};
        double registers[] = {3,3};
        Operator[] operators = new Operator[1];
        operators[0]  = new Mult();
        ControlFlow[] controlflow = new ControlFlow[0];
        continuouslgp.interpreter.Interpreter instance = new continuouslgp.interpreter.Interpreter(genome,registers,1,operators,controlflow);
        instance.doStep();
        
        double[] expectedRegisterResult = {3,9};
        System.out.println(instance.registers[1]);
        assertArrayEquals(expectedRegisterResult ,instance.registers,0.000001);
               
    }
    @Test
    public void testTwoOps() {
        System.out.println("doStep");
        
        //testing add 
        double[][] genome = {{.5,.5,0,1}};
        double registers[] = {3,3};
        Operator[] operators = new Operator[2];
        operators[0]  = new Mult();
        operators[1]  = new Add();
        ControlFlow[] controlflow = new ControlFlow[0];
        continuouslgp.interpreter.Interpreter instance = new continuouslgp.interpreter.Interpreter(genome,registers,1,operators,controlflow);
        instance.doStep();
        
        double[] expectedRegisterResult = {3,7.5};
        System.out.println(instance.registers[1]);
        assertArrayEquals(expectedRegisterResult ,instance.registers,0.000001);
               
    }
    @Test
    public void testTwoSteps() {
        System.out.println("doStep");
        
        //testing add 
        double[][] genome = {{0,1,0,1}};
        double registers[] = {3,3};
        Operator[] operators = new Operator[2];
        operators[0]  = new Mult();
        operators[1]  = new Add();
        ControlFlow[] controlflow = new ControlFlow[0];
        continuouslgp.interpreter.Interpreter instance = new continuouslgp.interpreter.Interpreter(genome,registers,1,operators,controlflow);
        instance.doStep();
        
        double[] expectedRegisterResult = {3,7.5};
        System.out.println(instance.registers[1]);
        assertArrayEquals(expectedRegisterResult ,instance.registers,0.000001);
               
    }
    /**
     * Test of ConsolidatePCs method, of class Interpreter.
     */
    @Test
    public void testConsolidatePCs() {
        System.out.println("ConsolidatePCs");
        Interpreter instance = null;
        instance.ConsolidatePCs();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
