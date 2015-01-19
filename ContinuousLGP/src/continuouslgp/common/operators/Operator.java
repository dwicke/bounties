/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package continuouslgp.common.operators;

import continuouslgp.interpreter.ContinuousArgs;

/**
 *
 * @author dfreelan
 */
public interface Operator {

    void doOperation(double[] registers, ContinuousArgs args);

}
