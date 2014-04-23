/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.bounties;

import sim.util.Bag;

/**
 *
 * @author dfreelan
 * bondsman is in charge of making tasks.... goodluck?
 */
public class Bondsman {
    public Bondsman(){}
    private Bag tasks = new Bag();
    public Bag getTasks(){
        return tasks;
    }
    public void addTask(Task a){
        tasks.add(a);
    }
    public void incrementBounty(){
        for(int i = 0; i< tasks.size(); i++){
            ((Task)tasks.objs[i]).incrementCurrentReward();
        }
    }
}
