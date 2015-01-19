/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package continuouslgp.interpreter;

/**
 *
 * @author dfreelan
 */
public class ProgramCounter implements Comparable{
    public int line = 0;
    public double weight =1;
    public ProgramCounter(int line, double weight){
        this.line = line;
        this.weight = weight;
    }
    public ProgramCounter(){}
    public ProgramCounter clone(){
        return new ProgramCounter(line,weight);
    }
    public int compareTo(Object t1){
        if(((ProgramCounter)t1).weight > weight)
            return -1; 
        return 1;
    }

    
}
