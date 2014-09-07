package sim.app.bounties;

import ec.util.MersenneTwisterFast;


/**
 * Very straight forward implementation of q-learning.
 */
public class QTable implements java.io.Serializable {

    private static final long serialVersionUID = 1;

    private int numStates, numActions;
    private double qtable[][];
    private double V[];
    private double alpha, oneMinusAlpha;
    private double beta;
    public QTable(int numStates, int numActions, double learningRate, double discountBeta, MersenneTwisterFast rand) {
        this.numActions = numActions;
        this.numStates = numStates;
        setAlpha(learningRate);
        qtable = new double[numStates][numActions];
        for (int i = 0; i < numStates; i++) {
            for (int j = 0; j < numActions; j++) {
                qtable[i][j] = rand.nextDouble(false, true);
            }
        }
        V = new double[numStates];
        beta = discountBeta;
    }
    public QTable(int numStates, int numActions, double learningRate, double discountBeta) {
        this.numActions = numActions;
        this.numStates = numStates;
        setAlpha(learningRate);
        qtable = new double[numStates][numActions];
        for (int i = 0; i < numStates; i++) {
            for (int j = 0; j < numActions; j++) {
                qtable[i][j] = 0;
            }
        }
        V = new double[numStates];
        beta = discountBeta;
    }
    
    public QTable(int numStates, int numActions, double learningRate, double discountBeta, MersenneTwisterFast rand, double max, double min) {
        this.numActions = numActions;
        this.numStates = numStates;
        setAlpha(learningRate);
        qtable = new double[numStates][numActions];
        for (int i = 0; i < numStates; i++) {
            for (int j = 0; j < numActions; j++) {
                qtable[i][j] = rand.nextDouble(true, true) * (max - min) + min;
            }
        }
        V = new double[numStates];
        beta = discountBeta;
    }
    
    
    
    
    public void setAlpha(double alphaT) {
        alpha = alphaT;
        oneMinusAlpha = 1 - alpha;
    }
    
    public void updateQ(int state, int action, double reward, int nextState) {
       // if(reward ==1 )
          qtable[state][action] = oneMinusAlpha * qtable[state][action] + alpha * ( reward + beta * V[nextState]);
       
          printTable();
//else 
       //   qtable[state][action] = .3 * qtable[state][action];

        // find max q-value for the state
        double max = qtable[state][action];
        for (int i = 0; i < numActions; i++) {
            if (max < qtable[state][i])
                max = qtable[state][i];
        }
        V[state] = max;
    }
    
    public void update(int state, int action, double reward, int nextState) {
      
    //    System.err.println("BEFORE reward: " + reward + " qvalue: " + qtable[state][action]);
    //    System.err.println("alpha: " + alpha + " one minus alpha: " + oneMinusAlpha);
        
     //   for (int i = 0; i < numActions; i++) {
      //      if(qtable[state][i]>.93)
      //       qtable[state][i] *= 1-(1/numActions);
     //   }
        //if(reward ==1 )
          qtable[state][action] = oneMinusAlpha * qtable[state][action] + alpha * ( (double)reward /*+ beta * V[nextState]*/);
          printTable();


// else 
        //  qtable[state][action] = alpha * qtable[state][action];
     //   System.err.println("AFTER reward: " + reward + " qvalue: " + qtable[state][action]);
      //  printTable();
        // find max q-value for the state
     /*   double max = qtable[state][action];
        for (int i = 0; i < numActions; i++) {
            if (max < qtable[state][i])
                max = qtable[state][i];
        }
        V[state] = max;*/
    }
    

    
    public double getQValue(int state, int action) {
       // System.err.println("Q_" + state + " = " + qtable[state][action]);
        return qtable[state][action];
    }
    
    public int getBestAction(int state) {
        double max = qtable[state][0];
        int best = 0;
        for (int i = 0; i < numActions; i++) {
            if (max < qtable[state][i]) {
                max = qtable[state][i];
                best = i;
            }
        }
        return best;
    }
    public double getNormalQValue(int state, int action) {
        //return qtable[state][action];
        double sum = 0;
        for (int i = 0; i < qtable.length; i++) {
            sum+=qtable[i][action];
            System.err.println("Q_" + i + " = " + qtable[i][action]);

        }
        if(sum==0)
               return 1;
        if(qtable[state][action]/sum <0){
            System.err.println("you fail");
            System.exit(0);
        }
        return qtable[state][action]/sum;
    }

    void printTable() {
        
        for (int i = 0; i < qtable.length; i++) {
            StringBuilder build = new StringBuilder();
            build.append("state ").append(i).append(" vals: ");
            for (int j = 0; j < qtable[i].length; j++) {
                build.append(qtable[i][j]).append(" ");
            }
            System.err.println(build.toString());
        }
    }
    
    String getQTableAsString() {
        StringBuilder build = new StringBuilder();
        for (int i = 0; i < qtable.length; i++) {
            
            build.append("state ").append(i).append(" vals: ");
            for (int j = 0; j < qtable[i].length; j++) {
                build.append(qtable[i][j]).append(" ");
            }
            build.append("\n");
        }
        return build.toString();
    }
}

