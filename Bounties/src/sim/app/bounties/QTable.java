package sim.app.bounties;


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
    public QTable(int numStates, int numActions, double learningRate, double discountBeta) {
        this.numActions = numActions;
        this.numStates = numStates;
        alpha = learningRate;
        qtable = new double[numStates][numActions];
        V = new double[numStates];
        beta = discountBeta;
    }
    
    public void setAlpha(double alphaT) {
        alpha = alphaT;
        oneMinusAlpha = 1 - alpha;
    }
    
    public void update(int state, int action, double reward, int nextState) {
        qtable[state][action] = oneMinusAlpha * qtable[state][action] + alpha * ( reward + beta * V[nextState]);
        // find max q-value for the state
        double max = qtable[state][action];
        for (int i = 0; i < numActions; i++) {
            if (max < qtable[state][i])
                max = qtable[state][i];
        }
        V[state] = max;
    }
    
    public double getQValue(int state, int action) {
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
            System.err.println("q_" + i + " val = " + qtable[i][action]);
        }
        if(sum==0)
               return 1;
        if(qtable[state][action]/sum <0){
            System.err.println("you fail");
            System.exit(0);
        }
        return qtable[state][action]/sum;
    }
}

