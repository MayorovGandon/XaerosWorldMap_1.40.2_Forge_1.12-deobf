//Decompiled by Procyon!

package xaero.map.server.player;

import xaero.map.server.radar.tracker.*;
import java.util.*;
import net.minecraft.entity.player.*;

public class ServerPlayerData
{
    private final UUID playerId;
    private SyncedTrackedPlayer lastSyncedData;
    private Set<UUID> currentlySyncedPlayers;
    private long lastTrackedPlayerSync;
    private int clientModNetworkVersion;
    private Object opacData;
    
    public ServerPlayerData(final UUID playerId) {
        this.playerId = playerId;
    }
    
    public SyncedTrackedPlayer getLastSyncedData() {
        return this.lastSyncedData;
    }
    
    public SyncedTrackedPlayer ensureLastSyncedData() {
        if (this.lastSyncedData == null) {
            this.lastSyncedData = new SyncedTrackedPlayer(this.playerId, 0.0, 0.0, 0.0, 0);
        }
        return this.lastSyncedData;
    }
    
    public Set<UUID> getCurrentlySyncedPlayers() {
        return this.currentlySyncedPlayers;
    }
    
    public Set<UUID> ensureCurrentlySyncedPlayers() {
        if (this.currentlySyncedPlayers == null) {
            this.currentlySyncedPlayers = new HashSet<UUID>();
        }
        return this.currentlySyncedPlayers;
    }
    
    public long getLastTrackedPlayerSync() {
        return this.lastTrackedPlayerSync;
    }
    
    public void setLastTrackedPlayerSync(final long lastTrackedPlayerSync) {
        this.lastTrackedPlayerSync = lastTrackedPlayerSync;
    }
    
    public static ServerPlayerData get(final EntityPlayerMP player) {
        ServerPlayerData result = ((IServerPlayer)player).getXaeroWorldMapPlayerData();
        if (result == null) {
            ((IServerPlayer)player).setXaeroWorldMapPlayerData(result = new ServerPlayerData(player.func_110124_au()));
        }
        return result;
    }
    
    public boolean hasMod() {
        return this.clientModNetworkVersion != 0;
    }
    
    public void setClientModNetworkVersion(final int clientModNetworkVersion) {
        this.clientModNetworkVersion = clientModNetworkVersion;
    }
    
    public int getClientModNetworkVersion() {
        return this.clientModNetworkVersion;
    }
    
    public void setOpacData(final Object opacData) {
        this.opacData = opacData;
    }
    
    public Object getOpacData() {
        return this.opacData;
    }
}
