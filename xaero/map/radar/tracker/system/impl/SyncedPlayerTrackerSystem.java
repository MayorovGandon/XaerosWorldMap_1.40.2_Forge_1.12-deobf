//Decompiled by Procyon!

package xaero.map.radar.tracker.system.impl;

import xaero.map.server.radar.tracker.*;
import xaero.map.radar.tracker.system.*;
import java.util.*;
import xaero.map.*;
import net.minecraft.client.*;
import xaero.map.mcworld.*;
import xaero.map.radar.tracker.synced.*;

public class SyncedPlayerTrackerSystem implements IPlayerTrackerSystem<SyncedTrackedPlayer>
{
    private final SyncedTrackedPlayerReader reader;
    
    public SyncedPlayerTrackerSystem() {
        this.reader = new SyncedTrackedPlayerReader();
    }
    
    @Override
    public ITrackedPlayerReader<SyncedTrackedPlayer> getReader() {
        return this.reader;
    }
    
    @Override
    public Iterator<SyncedTrackedPlayer> getTrackedPlayerIterator() {
        final WorldMapSession session = WorldMapSession.getCurrentSession();
        if (session == null) {
            return null;
        }
        if (Minecraft.func_71410_x().func_71401_C() == null) {
            final WorldMapClientWorldData worldData = WorldMapClientWorldDataHelper.getCurrentWorldData();
            if (worldData.serverLevelId == null) {
                return null;
            }
        }
        final ClientSyncedTrackedPlayerManager manager = session.getMapProcessor().getClientSyncedTrackedPlayerManager();
        return manager.getPlayers().iterator();
    }
}
