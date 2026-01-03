//Decompiled by Procyon!

package xaero.map.mods.minimap.tracker.system;

import xaero.common.server.radar.tracker.*;
import xaero.map.mods.*;
import xaero.map.radar.tracker.system.*;
import java.util.*;
import xaero.common.*;
import net.minecraft.client.*;
import xaero.common.minimap.mcworld.*;
import xaero.common.minimap.radar.tracker.synced.*;

public class MinimapSyncedPlayerTrackerSystem implements IPlayerTrackerSystem<SyncedTrackedPlayer>
{
    private final MinimapSyncedTrackedPlayerReader reader;
    
    public MinimapSyncedPlayerTrackerSystem(final SupportXaeroMinimap minimapSupport) {
        this.reader = new MinimapSyncedTrackedPlayerReader();
    }
    
    @Override
    public ITrackedPlayerReader<SyncedTrackedPlayer> getReader() {
        return this.reader;
    }
    
    @Override
    public Iterator<SyncedTrackedPlayer> getTrackedPlayerIterator() {
        final XaeroMinimapSession session = XaeroMinimapSession.getCurrentSession();
        if (session == null) {
            return null;
        }
        if (Minecraft.func_71410_x().func_71401_C() == null) {
            final MinimapClientWorldData worldData = MinimapClientWorldDataHelper.getCurrentWorldData();
            if (worldData.serverLevelId == null) {
                return null;
            }
        }
        final ClientSyncedTrackedPlayerManager manager = session.getMinimapProcessor().getClientSyncedTrackedPlayerManager();
        return manager.getPlayers().iterator();
    }
}
