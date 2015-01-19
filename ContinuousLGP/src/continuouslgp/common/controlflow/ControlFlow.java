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
public interface ControlFlow {
    ProgramCounter[] getProgramCounters(double[] registers, ContinuousArgs args, ProgramCounter pc);
}
