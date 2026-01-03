//Decompiled by Procyon!

package xaero.map.server.mods;

import xaero.common.server.*;
import net.minecraft.entity.player.*;
import xaero.common.server.player.*;

public class SupportMinimapServer
{
    private final int compatibilityVersion;
    
    public SupportMinimapServer() {
        int compatibilityVersion = 0;
        try {
            compatibilityVersion = XaeroMinimapServer.SERVER_COMPATIBILITY;
        }
        catch (Throwable t) {}
        this.compatibilityVersion = compatibilityVersion;
    }
    
    public boolean supportsTrackedPlayers() {
        return this.compatibilityVersion >= 1;
    }
    
    public boolean playerSupportsTrackedPlayers(final EntityPlayerMP player) {
        final ServerPlayerData playerData = ServerPlayerData.get(player);
        return playerData.hasMod();
    }
}
