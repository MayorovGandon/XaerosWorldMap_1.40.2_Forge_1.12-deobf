//Decompiled by Procyon!

package xaero.map.config.util;

import xaero.map.mods.*;
import xaero.map.*;
import net.minecraft.client.*;
import xaero.map.mcworld.*;
import xaero.map.common.config.option.*;
import xaero.lib.common.config.option.*;
import java.util.*;
import xaero.lib.client.config.*;
import xaero.map.config.primary.option.*;
import xaero.lib.common.config.single.*;
import xaero.lib.common.config.*;
import xaero.lib.common.config.profile.*;

public class WorldMapClientConfigUtils
{
    public static boolean isFairPlay() {
        boolean defaultValue = false;
        if (SupportMods.minimap() && SupportMods.xaeroMinimap.isFairPlay()) {
            defaultValue = true;
        }
        final WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
        if (worldmapSession == null) {
            return defaultValue;
        }
        if (!worldmapSession.getMapProcessor().fairplayMessageWasReceived()) {
            return defaultValue;
        }
        return !worldmapSession.getMapProcessor().isConsideringNetherFairPlay() || worldmapSession.getMapProcessor().getMapWorld().getCurrentDimensionId() != -1;
    }
    
    public static boolean isCaveModeDisabledLegacy() {
        if (WorldMap.INSTANCE.getConfigs().getClientConfigManager().getServerSynced().isChannelPresentOnServer()) {
            return false;
        }
        if (Minecraft.func_71410_x().field_71441_e == null) {
            return false;
        }
        final WorldMapClientWorldData clientData = WorldMapClientWorldDataHelper.getCurrentWorldData();
        return (!clientData.getSyncedRules().allowCaveModeOnServer && Minecraft.func_71410_x().field_71441_e.field_73011_w.getDimension() != -1) || (!clientData.getSyncedRules().allowNetherCaveModeOnServer && Minecraft.func_71410_x().field_71441_e.field_73011_w.getDimension() == -1);
    }
    
    public static boolean getEffectiveCaveModeAllowed() {
        final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        if (!(boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.CAVE_MODE_ALLOWED)) {
            return false;
        }
        if (Minecraft.func_71410_x().field_71441_e == null) {
            return true;
        }
        final int currentDimension = Minecraft.func_71410_x().field_71441_e.field_73011_w.getDimension();
        final Set<Integer> localCaveModeDimensions = (Set<Integer>)configManager.getCurrentProfile().get(WorldMapProfiledConfigOptions.CAVE_MODE_ALLOWED_DIMENSIONS);
        if (!localCaveModeDimensions.isEmpty() && !localCaveModeDimensions.contains(currentDimension)) {
            return false;
        }
        final Set<Integer> serverCaveModeDimensions = (Set<Integer>)configManager.getCurrentProfile().get(WorldMapProfiledConfigOptions.CAVE_MODE_ALLOWED_DIMENSIONS);
        return serverCaveModeDimensions == null || serverCaveModeDimensions.isEmpty() || serverCaveModeDimensions.contains(currentDimension);
    }
    
    public static boolean getDebug() {
        final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        final SingleConfigManager<Config> primaryConfigManager = (SingleConfigManager<Config>)configManager.getPrimaryConfigManager();
        return (boolean)primaryConfigManager.getEffective((ConfigOption)WorldMapPrimaryClientConfigOptions.DEBUG);
    }
    
    public static void togglePrimaryOption(final ConfigOption<Boolean> option) {
        final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        final SingleConfigManager<Config> primaryConfigManager = (SingleConfigManager<Config>)configManager.getPrimaryConfigManager();
        primaryConfigManager.getConfig().set((ConfigOption)option, (Object)!(boolean)primaryConfigManager.getConfig().get((ConfigOption)option));
        WorldMap.INSTANCE.getConfigs().getPrimaryClientConfigManagerIO().save();
    }
    
    public static void tryTogglingCurrentProfileOption(final ConfigOption<Boolean> option) {
        final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        if (configManager.getServerSynced().getEffective((ConfigOption)option) != null) {
            return;
        }
        final ConfigProfile currentProfile = configManager.getCurrentProfile();
        currentProfile.set((ConfigOption)option, (Object)!(boolean)currentProfile.get((ConfigOption)option));
        WorldMap.INSTANCE.getConfigs().getClientConfigProfileIO().save(currentProfile);
    }
    
    public static boolean isOptionServerEnforced(final ConfigOption<Boolean> option) {
        final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        return configManager.getServerSynced().getEffective((ConfigOption)option) != null;
    }
}
