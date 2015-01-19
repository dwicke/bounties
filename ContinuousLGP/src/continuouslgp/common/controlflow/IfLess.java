/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package continuouslgp.common.controlflow;

import continuouslgp.interpreter.ContinuousArgs;
import continuouslgp.interpreter.ProgramCounter;

/**
 *
 * @author dfreelan
 */
public class IfLess implements ControlFlow {

    @Override
    public ProgramCounter[] getProgramCounters(double[] registers, ContinuousArgs args, ProgramCounter pc) {
        ProgramCounter myPc[] = new ProgramCounter[1];
        args.calculateValues(registers);
        
        if(args.srcValue>= args.destValue){ // greater or equal to then skip next line
            myPc[0] = new ProgramCounter(pc.line+1,1);
        }else{
            myPc[0] = new ProgramCounter(0,0);
        }
        
        return myPc;
    }
    
    
}
