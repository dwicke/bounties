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
public class Constant implements Operator{
    int constant = 0;
    public Constant(int constant){
        this.constant = constant;
    }
    public void doOperation(double[] registers, ContinuousArgs args){
       registers[args.dest1] = args.dest1Weight * constant;
       registers[args.dest2] = args.dest2Weight * constant;
    }
}
