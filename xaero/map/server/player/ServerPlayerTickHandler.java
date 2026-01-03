//Decompiled by Procyon!

package xaero.map.server.player;

import net.minecraft.entity.player.*;
import xaero.map.server.*;
import net.minecraft.server.*;

public class ServerPlayerTickHandler
{
    public void tick(final EntityPlayerMP player) {
        final MinecraftServer server = player.func_184102_h();
        final MinecraftServerData serverData = MinecraftServerData.get(server);
        final ServerPlayerData playerData = ServerPlayerData.get(player);
        serverData.getSyncedPlayerTracker().onTick(server, player, serverData, playerData);
    }
}
