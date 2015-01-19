/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package continuouslgp.interpreter;

import continuouslgp.common.controlflow.ControlFlow;
import continuouslgp.common.operators.Operator;
import java.util.Arrays;

/**
 *
 * @author dfreelan
 */
public class Interpreter {
    double genome[][];
    double registers[];
    ProgramCounter[] pc;
    Operator[] operators;
    ControlFlow[] controlFlow;
    int curThreads = 1;
    int maxThreads =10;
    int argsIndex;
    public Interpreter(double[][] genome, double[] registers, int numPcs, Operator[] operators, ControlFlow[] controlFlow){
        this.genome = genome;
        this.registers = registers;
        pc = new ProgramCounter[numPcs];
        this.controlFlow = controlFlow;
        this.operators = operators;
        argsIndex = operators.length + controlFlow.length;
        pc = new ProgramCounter[numPcs*3];
        pc[0] = new ProgramCounter(0,1);
        maxThreads = numPcs;
    }
    public void setRegisters(double[] registers){
        this.registers = registers;
    }
    public void doStep(){
        double[][][] registerClones = cloneRegisters(registers,curThreads,operators.length);
        
        for(int i = 0; i<curThreads; i++){
            
            //get the src and dest and calculate what the arguments will be
            double src = genome[pc[i].line][argsIndex];
            double dest = genome[pc[i].line][argsIndex+1];
            ContinuousArgs operatorArgs = new ContinuousArgs(src,dest);
            //get src/dest value and limit args to be within register range.
            operatorArgs.calculateValues(registers);
            
            for(int a = 0; a<operators.length; a++){
                operators[a].doOperation(registerClones[i][a], operatorArgs);
                multiplyWeight(registerClones[i][a],pc[i].weight * genome[pc[i].line][a]);
            }
        }
        //add em up!
        double[] new_registers = cumulateRegister(registerClones);
        
        // now for control flow stuf 
        for(int i = 0; i<curThreads; i++){
            double src = genome[pc[i].line][argsIndex];
            double dest = genome[pc[i].line][argsIndex+1]; 
            ContinuousArgs controlFlowArgs = new ContinuousArgs(src,dest);
            for(int a = 0; a<controlFlow.length; a++){
                ProgramCounter[] newPcs = controlFlow[a].getProgramCounters(registers,controlFlowArgs,pc[i]);
                for(int k = 0; k<newPcs.length; i++){
                    // if the weight is zero, means it has no impact on the execution
                    if(newPcs[k].weight>0){
                        //add the pc to the thread and assign it the appropriate weight
                        pc[curThreads] = newPcs[k];
                        double instrWeight = genome[pc[i].line][a+operators.length];
                        pc[curThreads].weight = pc[i].weight*instrWeight;
                        curThreads++;
                    }
                }
            }
            
        }
        if(curThreads>maxThreads)
            ConsolidatePCs();
        for(int i = 0; i<curThreads; i++){
            pc[i].line++;
            if(pc[i].line >= genome.length){
                curThreads--;
                pc[i] = pc[curThreads];//grab the last thread
            }
        }
        registers = new_registers;
        
    }
    public void ConsolidatePCs(){
        Arrays.sort(pc);
        for(int i = maxThreads-1; i<curThreads; i++){
            for(int a = 0; a<maxThreads-1; a++){
                pc[a].weight += pc[a].weight * pc[i].weight;
            }
            pc[i] = null;
        }
        curThreads = maxThreads;
    }
    private double[] cumulateRegister(double[][][] registerResults){
        double averageRegisters[] = new double[registerResults[0][0].length]; 
        for(int i = 0; i<registerResults.length; i++){
            for(int a = 0; a<registerResults[i].length; a++){
                for(int z = 0; z<registerResults[i][a].length; z++){
                    
                    averageRegisters[z] += registerResults[i][a][z];
                    if(z==1){
                        System.out.println("average[z] is now" + averageRegisters[z]);
                    }
                }
            }
        }
        return averageRegisters;
    }
    private void multiplyWeight(double[] registers, double weight){
        for(int i = 0; i<registers.length; i++){
            registers[i] *= weight;
        }
    }
    //make a clone for each thread, and each potential instruction
    private double[][][] cloneRegisters(double[] registers, int threads, int instructions){
        double result[][][] = new double[threads][instructions][registers.length];
        for(int i = 0; i<threads; i++){
            for(int a = 0; a<instructions; a++){
                result[i][a] = registers.clone();
            }
        }
        return result;
    }
    
}
