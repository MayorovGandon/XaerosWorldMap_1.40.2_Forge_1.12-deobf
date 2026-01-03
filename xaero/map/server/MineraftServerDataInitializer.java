//Decompiled by Procyon!

package xaero.map.server;

import net.minecraft.server.*;
import xaero.map.server.mods.*;
import xaero.map.server.radar.tracker.*;

public class MineraftServerDataInitializer
{
    public void init(final MinecraftServer server) {
        final SyncedPlayerTrackerSystemManager syncedPlayerTrackerSystemManager = new SyncedPlayerTrackerSystemManager();
        if (SupportServerMods.hasFtbTeams()) {
            syncedPlayerTrackerSystemManager.register("ftb_teams", SupportServerMods.getFtbTeams().getSyncedPlayerTrackerSystem());
        }
        final SyncedPlayerTracker syncedPlayerTracker = new SyncedPlayerTracker();
        final MinecraftServerData data = new MinecraftServerData(syncedPlayerTrackerSystemManager, syncedPlayerTracker);
        ((IMinecraftServer)server).setXaeroWorldMapServerData(data);
    }
}
