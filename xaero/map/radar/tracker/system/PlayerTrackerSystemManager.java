//Decompiled by Procyon!

package xaero.map.radar.tracker.system;

import java.util.*;
import xaero.map.*;

public class PlayerTrackerSystemManager
{
    private final Map<String, IPlayerTrackerSystem<?>> systems;
    
    public PlayerTrackerSystemManager() {
        this.systems = new HashMap<String, IPlayerTrackerSystem<?>>();
    }
    
    public void register(final String name, final IPlayerTrackerSystem<?> system) {
        if (this.systems.containsKey(name)) {
            WorldMap.LOGGER.error("Player tracker system with the name " + name + " has already been registered!");
            return;
        }
        this.systems.put(name, system);
        WorldMap.LOGGER.info("Registered player tracker system: " + name);
    }
    
    public Iterable<IPlayerTrackerSystem<?>> getSystems() {
        return this.systems.values();
    }
}
