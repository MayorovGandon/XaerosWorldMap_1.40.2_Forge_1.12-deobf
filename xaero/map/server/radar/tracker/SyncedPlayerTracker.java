//Decompiled by Procyon!

package xaero.map.server.radar.tracker;

import net.minecraft.server.*;
import xaero.map.server.*;
import xaero.map.server.player.*;
import xaero.map.server.mods.*;
import xaero.map.*;
import xaero.lib.*;
import xaero.lib.common.config.primary.option.*;
import xaero.lib.common.config.option.*;
import net.minecraft.entity.player.*;
import xaero.lib.common.config.single.*;
import xaero.lib.common.config.*;
import java.util.*;
import xaero.map.message.tracker.*;
import xaero.lib.common.packet.*;

public class SyncedPlayerTracker
{
    public void onTick(final MinecraftServer server, final EntityPlayerMP player, final MinecraftServerData serverData, final ServerPlayerData playerData) {
        final long currentTime = System.currentTimeMillis();
        if (currentTime - playerData.getLastTrackedPlayerSync() < 250L) {
            return;
        }
        playerData.setLastTrackedPlayerSync(currentTime);
        boolean shouldSyncToPlayer;
        final boolean playerHasMod = shouldSyncToPlayer = playerData.hasMod();
        if (SupportServerMods.hasMinimap() && SupportServerMods.getMinimap().supportsTrackedPlayers() && SupportServerMods.getMinimap().playerSupportsTrackedPlayers(player)) {
            if (playerData.getCurrentlySyncedPlayers() != null && !playerData.getCurrentlySyncedPlayers().isEmpty()) {
                for (final UUID id : playerData.getCurrentlySyncedPlayers()) {
                    this.sendRemovePacket(player, id);
                }
                playerData.getCurrentlySyncedPlayers().clear();
            }
            shouldSyncToPlayer = false;
        }
        final SingleConfigManager<Config> primaryCommonConfig = (SingleConfigManager<Config>)WorldMap.INSTANCE.getConfigs().getPrimaryCommonConfigManager();
        final SingleConfigManager<Config> libPrimaryCommonConfig = (SingleConfigManager<Config>)XaeroLib.INSTANCE.getLibConfigChannel().getPrimaryCommonConfigManager();
        final boolean everyoneIsTracked = (boolean)libPrimaryCommonConfig.getEffective((ConfigOption)LibPrimaryCommonConfigOptions.EVERYONE_TRACKS_EVERYONE);
        final Iterable<ISyncedPlayerTrackerSystem> playerTrackerSystems = serverData.getSyncedPlayerTrackerSystemManager().getSystems();
        final Set<UUID> syncedPlayers = (Set<UUID>)playerData.ensureCurrentlySyncedPlayers();
        final Set<UUID> leftoverPlayers = new HashSet<UUID>(syncedPlayers);
        SyncedTrackedPlayer toSync = playerData.getLastSyncedData();
        final boolean shouldSyncToOthers = toSync == null || !toSync.matchesEnough((EntityPlayer)player, 0.0);
        if (shouldSyncToOthers) {
            toSync = playerData.ensureLastSyncedData();
            toSync.update((EntityPlayer)player);
        }
        for (final EntityPlayerMP otherPlayer : server.func_184103_al().func_181057_v()) {
            if (otherPlayer == player) {
                continue;
            }
            leftoverPlayers.remove(otherPlayer.func_110124_au());
            final ServerPlayerData otherPlayerData = ServerPlayerData.get(otherPlayer);
            if (shouldSyncToOthers) {
                final Set<UUID> otherPlayerSyncedPlayers = (Set<UUID>)otherPlayerData.getCurrentlySyncedPlayers();
                if (otherPlayerSyncedPlayers != null && otherPlayerSyncedPlayers.contains(player.func_110124_au())) {
                    this.sendTrackedPlayerPacket(otherPlayer, toSync);
                }
            }
            if (!shouldSyncToPlayer) {
                continue;
            }
            boolean tracked = everyoneIsTracked;
            if (!tracked) {
                final boolean opacConfigsAllowPartySync = true;
                final boolean opacConfigsAllowAllySync = true;
                for (final ISyncedPlayerTrackerSystem system : playerTrackerSystems) {
                    final int trackingLevel = system.getTrackingLevel((EntityPlayer)player, (EntityPlayer)otherPlayer);
                    if (trackingLevel > 0 && (!system.isPartySystem() || (trackingLevel == 1 && opacConfigsAllowAllySync) || (trackingLevel > 1 && opacConfigsAllowPartySync))) {
                        tracked = true;
                        break;
                    }
                }
            }
            final boolean alreadySynced = syncedPlayers.contains(otherPlayer.func_110124_au());
            if (!tracked) {
                if (!alreadySynced) {
                    continue;
                }
                syncedPlayers.remove(otherPlayer.func_110124_au());
                this.sendRemovePacket(player, otherPlayer.func_110124_au());
            }
            else {
                if (alreadySynced) {
                    continue;
                }
                if (otherPlayerData.getLastSyncedData() == null) {
                    continue;
                }
                syncedPlayers.add(otherPlayer.func_110124_au());
                this.sendTrackedPlayerPacket(player, otherPlayerData.getLastSyncedData());
            }
        }
        for (final UUID offlineId : leftoverPlayers) {
            syncedPlayers.remove(offlineId);
            this.sendRemovePacket(player, offlineId);
        }
    }
    
    private void sendRemovePacket(final EntityPlayerMP player, final UUID toRemove) {
        WorldMap.messageHandler.sendToPlayer(player, (XaeroPacket)new ClientboundTrackedPlayerPacket(true, toRemove, 0.0, 0.0, 0.0, 0));
    }
    
    private void sendTrackedPlayerPacket(final EntityPlayerMP player, final SyncedTrackedPlayer tracked) {
        WorldMap.messageHandler.sendToPlayer(player, (XaeroPacket)new ClientboundTrackedPlayerPacket(false, tracked.getId(), tracked.getX(), tracked.getY(), tracked.getZ(), tracked.getDimension()));
    }
}
