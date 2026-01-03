//Decompiled by Procyon!

package xaero.map.mcworld;

import net.minecraft.util.math.*;
import xaero.map.message.basic.*;
import net.minecraft.client.multiplayer.*;

public class WorldMapClientWorldData
{
    private int serverModNetworkVersion;
    public Integer serverLevelId;
    public Integer usedServerLevelId;
    public BlockPos latestSpawn;
    public BlockPos usedSpawn;
    private ClientboundRulesPacket syncedRules;
    
    public WorldMapClientWorldData(final WorldClient world) {
    }
    
    public void setServerModNetworkVersion(final int serverModNetworkVersion) {
        this.serverModNetworkVersion = serverModNetworkVersion;
    }
    
    public int getServerModNetworkVersion() {
        return this.serverModNetworkVersion;
    }
    
    public void setSyncedRules(final ClientboundRulesPacket syncedRules) {
        this.syncedRules = syncedRules;
    }
    
    public ClientboundRulesPacket getSyncedRules() {
        if (this.syncedRules == null) {
            this.syncedRules = new ClientboundRulesPacket(true, true);
        }
        return this.syncedRules;
    }
}
