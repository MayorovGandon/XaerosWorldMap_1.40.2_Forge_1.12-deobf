//Decompiled by Procyon!

package xaero.map.radar.tracker.synced;

import xaero.map.server.radar.tracker.*;
import java.util.*;

public class ClientSyncedTrackedPlayerManager
{
    private final Map<UUID, SyncedTrackedPlayer> trackedPlayers;
    
    public ClientSyncedTrackedPlayerManager() {
        this.trackedPlayers = new HashMap<UUID, SyncedTrackedPlayer>();
    }
    
    public void remove(final UUID id) {
        this.trackedPlayers.remove(id);
    }
    
    public void update(final UUID id, final double x, final double y, final double z, final int dim) {
        final SyncedTrackedPlayer current = this.trackedPlayers.get(id);
        if (current != null) {
            current.setPos(x, y, z).setDimension(dim);
            return;
        }
        this.trackedPlayers.put(id, new SyncedTrackedPlayer(id, x, y, z, dim));
    }
    
    public Iterable<SyncedTrackedPlayer> getPlayers() {
        return this.trackedPlayers.values();
    }
    
    public void reset() {
        this.trackedPlayers.clear();
    }
}
