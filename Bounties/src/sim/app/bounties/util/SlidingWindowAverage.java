/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.util;

/**
 *
 * @author drew
 */
public class SlidingWindowAverage {

    private final int windowSize;
    private int curIndex = 0;
    private double numerators[];
    private double denominators[];
    
    
    public SlidingWindowAverage(int windowSize) {
        this.windowSize = windowSize;
        numerators = new double[windowSize];
        denominators = new double[windowSize];
    }
    
    public void addValue(double numerator, double denominator) {
        
        numerators[curIndex] = numerator;
        denominators[curIndex] = denominator;
        curIndex++;
        if(curIndex == windowSize) {
            curIndex = 0;
        }
    }
    
    
    
    public double getRollingAverage() {
        double numSum = 0.0;
        double denSum = 0.0;
        for (int i = 0; i < windowSize; i++) {
            numSum += numerators[i];
            denSum += denominators[i];
        }
        if (denSum > 0) {
            return numSum / denSum;
        }
        
        return 0.0;
    }
}
