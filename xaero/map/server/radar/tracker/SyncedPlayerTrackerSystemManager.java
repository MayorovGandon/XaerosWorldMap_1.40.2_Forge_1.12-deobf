//Decompiled by Procyon!

package xaero.map.server.radar.tracker;

import java.util.*;
import xaero.map.*;

public class SyncedPlayerTrackerSystemManager
{
    private final Map<String, ISyncedPlayerTrackerSystem> systems;
    
    public SyncedPlayerTrackerSystemManager() {
        this.systems = new HashMap<String, ISyncedPlayerTrackerSystem>();
    }
    
    public void register(final String name, final ISyncedPlayerTrackerSystem system) {
        if (this.systems.containsKey(name)) {
            WorldMap.LOGGER.error("Synced player tracker system with the name " + name + " has already been registered!");
            return;
        }
        this.systems.put(name, system);
        WorldMap.LOGGER.info("Registered synced player tracker system: " + name);
    }
    
    public Iterable<ISyncedPlayerTrackerSystem> getSystems() {
        return this.systems.values();
    }
}
