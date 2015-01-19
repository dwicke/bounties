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
public class Goto implements ControlFlow {

    @Override
    public ProgramCounter[] getProgramCounters(double[] registers, ContinuousArgs args, ProgramCounter pc) {
        ProgramCounter myPc[] = new ProgramCounter[2];
        
        myPc[0] = new ProgramCounter(args.src1,args.src1Weight);
        myPc[1] = new ProgramCounter(args.src2,args.src2Weight);
        
        return myPc;
    }
    
    
}
