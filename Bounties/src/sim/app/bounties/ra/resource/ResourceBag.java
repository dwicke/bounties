/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.bounties.ra.resource;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author drew
 */
public class ResourceBag {
    
    EnumMap<ResourceType, Integer> resourcebag;

    public ResourceBag() {
        resourcebag = new EnumMap<ResourceType, Integer>(ResourceType.class);
    }
    
    
    public Integer getNumResourcesForType(ResourceType type) {
        return resourcebag.get(type);
    }
    
    public void addResourcesForType(ResourceType type, int newResources) {
        if (resourcebag.containsKey(type)) {
            resourcebag.put(type, resourcebag.get(type) + newResources);
        }
    }
    
    public void removeResourcesForType(ResourceType type, int newResources) {
        if (resourcebag.containsKey(type)) {
            resourcebag.put(type, resourcebag.get(type) - newResources);
        }
    }
    
    public Iterator<Map.Entry<ResourceType, Integer> > getIterator() {
        return resourcebag.entrySet().iterator();
    }

    @Override
    public String toString() {
        return resourcebag.toString(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
