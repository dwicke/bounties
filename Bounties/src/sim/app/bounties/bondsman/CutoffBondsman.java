/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.bondsman;

import sim.app.bounties.Bounties;
import sim.app.bounties.environment.Task;

/**
 *
 * @author drew
 */
public class CutoffBondsman extends Bondsman {
    private static final long serialVersionUID = 1;

    
    public CutoffBondsman(Bounties bounties, int exclusiveType) {
        super(bounties, exclusiveType);
    }
    
    /**
     * Given that agent a decided to do this task t should we keep it available
     * for other agents to go after?
     * @param t task id t
     * @param a agent id a
     */
    @Override
    public void isExclusive(Task task, int a) {
        if (whoseDoingTaskByID(task).numObjs >= 2) {
            task.setIsNonExclusive(false);
            isExclusive[task.getID()] = true;
        } else {
            task.setIsNonExclusive(true);
            isExclusive[task.getID()] = false;
        }
    }

}
