/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.ra.resource;

/**
 * This type of resource must be used in order to complete a task.
 * @author drew
 */
public class TaskResource implements Resource{

    private final double quantity, reservePrice;
    private final ResourceType type;

    public TaskResource(double quantity, double reservePrice, ResourceType type) {
        this.quantity = quantity;
        this.reservePrice = reservePrice;
        this.type = type;
    }
    
    @Override
    public double getReservePrice() {
        return reservePrice;
    }

    @Override
    public ResourceType getType() {
        return type;
    }

    @Override
    public double getQuantity() {
        return quantity;
    }

    
    
}
