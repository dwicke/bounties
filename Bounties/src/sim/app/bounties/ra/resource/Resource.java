/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.ra.resource;

/**
 *
 * @author drew
 */
public interface Resource {
    
    public double getReservePrice();
    
    public double getQuantity();
    
    public ResourceType getType();
    
    
    /* // things to implement in the future...
    public long resourceLife();
    
    public boolean isTransferable();
    
    public boolean isCompoundable();
    */
    
}
