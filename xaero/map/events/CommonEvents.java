//Decompiled by Procyon!

package xaero.map.events;

import net.minecraftforge.event.entity.player.*;
import xaero.map.server.player.*;
import net.minecraft.entity.player.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.fml.common.event.*;
import xaero.map.*;
import xaero.map.message.tracker.*;
import xaero.lib.common.packet.*;
import xaero.map.common.config.option.*;
import xaero.lib.common.config.option.*;
import java.util.*;
import xaero.map.message.basic.*;
import xaero.map.server.*;
import net.minecraft.util.text.*;
import xaero.lib.common.config.server.*;
import xaero.lib.common.config.profile.*;
import net.minecraft.server.*;
import java.nio.file.*;
import xaero.map.server.level.*;
import net.minecraftforge.fml.common.gameevent.*;

public class CommonEvents
{
    @SubscribeEvent
    public void onPlayerClone(final PlayerEvent.Clone event) {
        final EntityPlayer oldPlayer = event.getOriginal();
        if (oldPlayer instanceof EntityPlayerMP) {
            final EntityPlayer newPlayer = event.getEntityPlayer();
            ((IServerPlayer)newPlayer).setXaeroWorldMapPlayerData(ServerPlayerData.get((EntityPlayerMP)oldPlayer));
        }
    }
    
    public void onServerStarting(final FMLServerStartingEvent event) {
        new MineraftServerDataInitializer().init(event.getServer());
    }
    
    public void onServerStopped(final FMLServerStoppedEvent event) {
    }
    
    @SubscribeEvent
    public void onPlayerLogIn(final net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {
        final EntityPlayer player = event.player;
        if (player instanceof EntityPlayerMP) {
            WorldMap.messageHandler.sendToPlayer((EntityPlayerMP)player, (XaeroPacket)new ClientboundPlayerTrackerResetPacket());
        }
    }
    
    public void onPlayerWorldJoin(final EntityPlayerMP player) {
        WorldMap.messageHandler.sendToPlayer(player, (XaeroPacket)new HandshakePacket());
        final ServerConfigManager configManager = WorldMap.INSTANCE.getConfigs().getServerConfigManager();
        final ConfigProfile defaultEnforcedProfile = configManager.getDefaultEnforcedProfile();
        final boolean caveModeConfig = !Boolean.FALSE.equals(defaultEnforcedProfile.get((ConfigOption)WorldMapProfiledConfigOptions.CAVE_MODE_ALLOWED));
        final Set<Integer> caveModeDimensionsConfig = (Set<Integer>)defaultEnforcedProfile.get(WorldMapProfiledConfigOptions.CAVE_MODE_ALLOWED_DIMENSIONS);
        final boolean allowOverworldCaveModeOnServer = caveModeConfig && (caveModeDimensionsConfig == null || caveModeDimensionsConfig.isEmpty() || caveModeDimensionsConfig.contains(0));
        final boolean allowNetherCaveModeOnServer = caveModeConfig && (caveModeDimensionsConfig == null || caveModeDimensionsConfig.isEmpty() || caveModeDimensionsConfig.contains(-1));
        WorldMap.messageHandler.sendToPlayer(player, (XaeroPacket)new ClientboundRulesPacket(allowOverworldCaveModeOnServer, allowNetherCaveModeOnServer));
        final MinecraftServer mcServer = player.field_70170_p.func_73046_m();
        final Path propertiesPath = mcServer.func_71254_M().func_186352_b(mcServer.func_71270_I(), "xaeromap.txt").toPath();
        try {
            final MinecraftServerData serverData = MinecraftServerData.get(player.func_184102_h());
            final LevelMapProperties properties = serverData.getLevelProperties(propertiesPath);
            if (properties.isUsable()) {
                WorldMap.messageHandler.sendToPlayer(player, (XaeroPacket)properties);
            }
        }
        catch (Throwable t) {
            WorldMap.LOGGER.error("suppressed exception", t);
            player.field_71135_a.func_194028_b((ITextComponent)new TextComponentTranslation("gui.xaero_wm_error_loading_properties", new Object[0]));
        }
    }
    
    @SubscribeEvent
    public void handlePlayerTickStart(final TickEvent.PlayerTickEvent event) {
        final EntityPlayer player = event.player;
        if (player instanceof EntityPlayerMP) {
            WorldMap.serverPlayerTickHandler.tick((EntityPlayerMP)player);
        }
    }
}
