/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.horde.scenarios.robot.comm;

/**
 * Can be used in conjunction with a parser to be able to do something after input
 * has been set.
 * @author drew
 */
public interface ParserCallback {
    public void doSomething(Parse parse);
}
