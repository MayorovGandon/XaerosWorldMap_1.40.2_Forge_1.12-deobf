//Decompiled by Procyon!

package xaero.map.mods.minimap.tracker.system;

import xaero.map.radar.tracker.system.*;
import xaero.common.server.radar.tracker.*;
import java.util.*;

public class MinimapSyncedTrackedPlayerReader implements ITrackedPlayerReader<SyncedTrackedPlayer>
{
    @Override
    public UUID getId(final SyncedTrackedPlayer player) {
        return player.getId();
    }
    
    @Override
    public double getX(final SyncedTrackedPlayer player) {
        return player.getX();
    }
    
    @Override
    public double getY(final SyncedTrackedPlayer player) {
        return player.getY();
    }
    
    @Override
    public double getZ(final SyncedTrackedPlayer player) {
        return player.getZ();
    }
    
    @Override
    public int getDimension(final SyncedTrackedPlayer player) {
        return player.getDimension();
    }
}
