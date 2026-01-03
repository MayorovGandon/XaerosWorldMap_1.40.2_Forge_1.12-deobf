//Decompiled by Procyon!

package xaero.map.server.mods.ftbteams;

import xaero.map.server.radar.tracker.*;

public class SupportFTBTeamsServer
{
    private final ISyncedPlayerTrackerSystem syncedPlayerTrackerSystem;
    
    public SupportFTBTeamsServer() {
        this.syncedPlayerTrackerSystem = (ISyncedPlayerTrackerSystem)new FTBTeamsSyncedPlayerTrackerSystem();
    }
    
    public ISyncedPlayerTrackerSystem getSyncedPlayerTrackerSystem() {
        return this.syncedPlayerTrackerSystem;
    }
}
