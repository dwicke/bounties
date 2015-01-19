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
public class ContinuousArgs {
    public int src1, src2, dest1, dest2;
    public double src1Weight,src2Weight,dest1Weight,dest2Weight;
    public double srcValue, destValue;
   
    public ContinuousArgs(double src, double dest){
        populateActiveRegisters(src,dest);
        
    }
    public ContinuousArgs(){}
    public ContinuousArgs clone(){
        ContinuousArgs myClone = new ContinuousArgs();
        myClone.src1 = this.src1;
        myClone.src2 = this.src2;
        myClone.dest1 = this.dest1;
        myClone.dest2 = this.dest2;
        myClone.src1Weight = this.src1Weight;
        myClone.src2Weight = this.src2Weight;
        myClone.dest1Weight = this.dest1Weight;
        myClone.dest2Weight = this.dest2Weight;
        myClone.srcValue = this.srcValue;
        myClone.destValue = this.destValue;
        return myClone;
    }
    //takes the source and mods by the max register value
    //also populates srcValue and destvalue
    public void calculateValues(double[] registers){
        src1 = src1 % registers.length;
        src2 = src2 % registers.length;
        dest1 = dest1 % registers.length;
        dest2 = dest2 % registers.length;
        
        srcValue = (src1Weight*registers[src1] + src2Weight*registers[src2]);
        destValue = (dest1Weight*registers[dest1] + dest2Weight*registers[dest2]);

    }
    private void populateActiveRegisters(double src, double dest){
        src1 = (int)src;
        src2 = (int)(src+1);
        src2Weight = src - ((int)src);
        
        src1Weight = 1-src2Weight;
        
        dest1 = (int)dest;
        dest2 = (int)(dest+1);
        
        dest2Weight = dest - ((int)dest);
        dest1Weight = 1-dest2Weight;
       
    }        
}
