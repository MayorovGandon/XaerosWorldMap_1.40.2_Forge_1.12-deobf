//Decompiled by Procyon!

package xaero.map.settings;

import java.nio.file.attribute.*;
import xaero.lib.common.util.*;
import java.nio.file.*;
import java.io.*;
import xaero.map.config.primary.option.*;
import xaero.lib.common.config.option.*;
import xaero.lib.*;
import xaero.lib.common.config.primary.option.*;
import xaero.map.common.config.option.*;
import xaero.lib.client.config.*;
import xaero.lib.common.config.profile.*;
import xaero.lib.common.config.*;
import net.minecraft.client.*;
import xaero.map.*;
import org.apache.commons.lang3.builder.*;
import net.minecraft.client.resources.*;
import java.util.*;

public class ModSettings
{
    public static final String format = "§";
    private int regionCacheHashCode;
    
    private void loadDefaultSettings() throws IOException {
        final File mainConfigFile = WorldMap.optionsFile;
        final File defaultConfigFile = mainConfigFile.toPath().getParent().resolveSibling("defaultconfigs").resolve(mainConfigFile.getName()).toFile();
        if (defaultConfigFile.exists()) {
            this.loadSettingsFile(defaultConfigFile);
        }
    }
    
    public void loadSettings() throws IOException {
        this.loadDefaultSettings();
        final File mainConfigFile = WorldMap.optionsFile;
        final Path configFolderPath = mainConfigFile.toPath().getParent();
        if (!Files.exists(configFolderPath, new LinkOption[0])) {
            Files.createDirectories(configFolderPath, (FileAttribute<?>[])new FileAttribute[0]);
        }
        if (mainConfigFile.exists()) {
            this.loadSettingsFile(mainConfigFile);
            IOUtils.quickFileBackupMove(mainConfigFile.toPath());
        }
    }
    
    private void loadSettingsFile(final File file) throws IOException {
        BufferedReader reader = null;
        final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        final ConfigProfile currentProfile = configManager.getCurrentProfile();
        final Config primaryConfig = configManager.getPrimaryConfigManager().getConfig();
        try {
            reader = new BufferedReader(new FileReader(file));
            String s;
            while ((s = reader.readLine()) != null) {
                final String[] args = s.split(":");
                try {
                    if (args[0].equalsIgnoreCase("ignoreUpdate")) {
                        primaryConfig.set(WorldMapPrimaryClientConfigOptions.IGNORED_UPDATE, (Object)Integer.parseInt(args[1]));
                    }
                    else if (args[0].equalsIgnoreCase("updateNotification")) {
                        primaryConfig.set((ConfigOption)WorldMapPrimaryClientConfigOptions.UPDATE_NOTIFICATIONS, (Object)args[1].equals("true"));
                    }
                    else if (args[0].equalsIgnoreCase("allowInternetAccess")) {
                        final boolean savedAllowInternetAccess = args[1].equals("true");
                        if (savedAllowInternetAccess) {
                            continue;
                        }
                        XaeroLib.INSTANCE.getLibConfigChannel().getPrimaryCommonConfigManager().getConfig().set((ConfigOption)LibPrimaryCommonConfigOptions.ALLOW_INTERNET, (Object)false);
                        XaeroLib.INSTANCE.getLibConfigChannel().getPrimaryCommonConfigManagerIO().save();
                    }
                    else if (args[0].equalsIgnoreCase("differentiateByServerAddress")) {
                        primaryConfig.set((ConfigOption)WorldMapPrimaryClientConfigOptions.DIFFERENTIATE_BY_SERVER_ADDRESS, (Object)args[1].equals("true"));
                    }
                    else if (args[0].equalsIgnoreCase("caveMapsAllowed")) {
                        currentProfile.set((ConfigOption)WorldMapProfiledConfigOptions.CAVE_MODE_ALLOWED, (Object)args[1].equals("true"));
                    }
                    else if (args[0].equalsIgnoreCase("debug")) {
                        primaryConfig.set((ConfigOption)WorldMapPrimaryClientConfigOptions.DEBUG, (Object)args[1].equals("true"));
                    }
                    else if (args[0].equalsIgnoreCase("lighting")) {
                        currentProfile.set((ConfigOption)WorldMapProfiledConfigOptions.LIGHTING, (Object)args[1].equals("true"));
                    }
                    else if (args[0].equalsIgnoreCase("colours")) {
                        currentProfile.set((ConfigOption)WorldMapProfiledConfigOptions.BLOCK_COLORS, (Object)Integer.parseInt(args[1]));
                    }
                    else if (args[0].equalsIgnoreCase("loadChunks")) {
                        currentProfile.set((ConfigOption)WorldMapProfiledConfigOptions.LOAD_NEW_CHUNKS, (Object)args[1].equals("true"));
                    }
                    else if (args[0].equalsIgnoreCase("updateChunks")) {
                        currentProfile.set((ConfigOption)WorldMapProfiledConfigOptions.UPDATE_CHUNKS, (Object)args[1].equals("true"));
                    }
                    else if (args[0].equalsIgnoreCase("terrainSlopes")) {
                        currentProfile.set((ConfigOption)WorldMapProfiledConfigOptions.TERRAIN_SLOPES, (Object)(args[1].equals("true") ? 2 : (args[1].equals("false") ? 0 : Integer.parseInt(args[1]))));
                    }
                    else if (args[0].equalsIgnoreCase("terrainDepth")) {
                        currentProfile.set((ConfigOption)WorldMapProfiledConfigOptions.TERRAIN_DEPTH, (Object)args[1].equals("true"));
                    }
                    else if (args[0].equalsIgnoreCase("footsteps")) {
                        currentProfile.set((ConfigOption)WorldMapProfiledConfigOptions.FOOTSTEPS, (Object)args[1].equals("true"));
                    }
                    else if (args[0].equalsIgnoreCase("flowers")) {
                        currentProfile.set((ConfigOption)WorldMapProfiledConfigOptions.FLOWERS, (Object)args[1].equals("true"));
                    }
                    else if (args[0].equalsIgnoreCase("coordinates")) {
                        currentProfile.set((ConfigOption)WorldMapProfiledConfigOptions.COORDINATES, (Object)args[1].equals("true"));
                    }
                    else if (args[0].equalsIgnoreCase("hoveredBiome")) {
                        currentProfile.set((ConfigOption)WorldMapProfiledConfigOptions.DISPLAY_HOVERED_BIOME, (Object)args[1].equals("true"));
                    }
                    else if (args[0].equalsIgnoreCase("biomeColorsVanillaMode")) {
                        currentProfile.set((ConfigOption)WorldMapProfiledConfigOptions.BIOME_COLORS_IN_VANILLA, (Object)args[1].equals("true"));
                    }
                    else if (args[0].equalsIgnoreCase("waypoints")) {
                        currentProfile.set((ConfigOption)WorldMapProfiledConfigOptions.WAYPOINTS, (Object)args[1].equals("true"));
                    }
                    else if (args[0].equalsIgnoreCase("renderArrow")) {
                        currentProfile.set((ConfigOption)WorldMapProfiledConfigOptions.ARROW, (Object)args[1].equals("true"));
                    }
                    else if (args[0].equalsIgnoreCase("displayZoom")) {
                        currentProfile.set((ConfigOption)WorldMapProfiledConfigOptions.DISPLAY_ZOOM, (Object)args[1].equals("true"));
                    }
                    else if (args[0].equalsIgnoreCase("worldmapWaypointsScale")) {
                        currentProfile.set((ConfigOption)WorldMapProfiledConfigOptions.WAYPOINT_SCALE, (Object)(double)Float.parseFloat(args[1]));
                    }
                    else if (args[0].equalsIgnoreCase("openMapAnimation")) {
                        currentProfile.set((ConfigOption)WorldMapProfiledConfigOptions.OPENING_ANIMATION, (Object)args[1].equals("true"));
                    }
                    else if (args[0].equalsIgnoreCase("reloadVersion")) {
                        primaryConfig.set(WorldMapPrimaryClientConfigOptions.RELOAD_VIEWED_VERSION, (Object)Integer.parseInt(args[1]));
                    }
                    else if (args[0].equalsIgnoreCase("reloadEverything")) {
                        primaryConfig.set((ConfigOption)WorldMapPrimaryClientConfigOptions.RELOAD_VIEWED, (Object)args[1].equals("true"));
                    }
                    else if (args[0].equalsIgnoreCase("zoomButtons")) {
                        currentProfile.set((ConfigOption)WorldMapProfiledConfigOptions.ZOOM_BUTTONS, (Object)args[1].equals("true"));
                    }
                    else if (args[0].equalsIgnoreCase("waypointBackgrounds")) {
                        currentProfile.set((ConfigOption)WorldMapProfiledConfigOptions.WAYPOINT_BACKGROUNDS, (Object)args[1].equals("true"));
                    }
                    else if (args[0].equalsIgnoreCase("mapItemId")) {
                        currentProfile.set(WorldMapProfiledConfigOptions.MAP_ITEM, (Object)(args[1] + ":" + args[2]));
                    }
                    else if (args[0].equalsIgnoreCase("detectAmbiguousY")) {
                        currentProfile.set((ConfigOption)WorldMapProfiledConfigOptions.DETECT_AMBIGUOUS_Y, (Object)args[1].equals("true"));
                    }
                    else if (args[0].equalsIgnoreCase("showDisabledWaypoints")) {
                        primaryConfig.set((ConfigOption)WorldMapPrimaryClientConfigOptions.DISPLAY_DISABLED_WAYPOINTS, (Object)args[1].equals("true"));
                    }
                    else if (args[0].equalsIgnoreCase("closeWaypointsWhenHopping")) {
                        primaryConfig.set((ConfigOption)WorldMapPrimaryClientConfigOptions.CLOSE_WAYPOINTS_AFTER_HOP, (Object)args[1].equals("true"));
                    }
                    else if (args[0].equalsIgnoreCase("adjustHeightForCarpetLikeBlocks")) {
                        currentProfile.set((ConfigOption)WorldMapProfiledConfigOptions.ADJUST_HEIGHT_FOR_SHORT_BLOCKS, (Object)args[1].equals("true"));
                    }
                    else if (args[0].equalsIgnoreCase("onlyCurrentMapWaypoints")) {
                        primaryConfig.set((ConfigOption)WorldMapPrimaryClientConfigOptions.ONLY_CURRENT_MAP_WAYPOINTS, (Object)args[1].equals("true"));
                    }
                    else if (args[0].equalsIgnoreCase("minZoomForLocalWaypoints")) {
                        currentProfile.set((ConfigOption)WorldMapProfiledConfigOptions.MIN_ZOOM_LOCAL_WAYPOINTS, (Object)Double.parseDouble(args[1]));
                    }
                    else if (args[0].equalsIgnoreCase("arrowColour")) {
                        currentProfile.set((ConfigOption)WorldMapProfiledConfigOptions.ARROW_COLOR, (Object)Integer.parseInt(args[1]));
                    }
                    else if (args[0].equalsIgnoreCase("minimapRadar")) {
                        currentProfile.set((ConfigOption)WorldMapProfiledConfigOptions.MINIMAP_RADAR, (Object)args[1].equals("true"));
                    }
                    else if (args[0].equalsIgnoreCase("renderWaypoints")) {
                        currentProfile.set((ConfigOption)WorldMapProfiledConfigOptions.RENDER_WAYPOINTS, (Object)args[1].equals("true"));
                    }
                    else if (args[0].equalsIgnoreCase("partialYTeleportation")) {
                        currentProfile.set((ConfigOption)WorldMapProfiledConfigOptions.PARTIAL_Y_TELEPORT, (Object)args[1].equals("true"));
                    }
                    else if (args[0].equalsIgnoreCase("displayStainedGlass")) {
                        currentProfile.set((ConfigOption)WorldMapProfiledConfigOptions.STAINED_GLASS, (Object)args[1].equals("true"));
                    }
                    else if (args[0].equalsIgnoreCase("caveModeDepth")) {
                        currentProfile.set((ConfigOption)WorldMapProfiledConfigOptions.CAVE_MODE_DEPTH, (Object)Integer.parseInt(args[1]));
                    }
                    else if (args[0].equalsIgnoreCase("caveModeStart")) {
                        primaryConfig.set(WorldMapPrimaryClientConfigOptions.CAVE_MODE_START, (Object)Integer.parseInt(args[1]));
                    }
                    else if (args[0].equalsIgnoreCase("autoCaveMode")) {
                        currentProfile.set((ConfigOption)WorldMapProfiledConfigOptions.AUTO_CAVE_MODE, (Object)Integer.parseInt(args[1]));
                    }
                    else if (args[0].equalsIgnoreCase("legibleCaveMaps")) {
                        currentProfile.set((ConfigOption)WorldMapProfiledConfigOptions.LEGIBLE_CAVE_MAPS, (Object)args[1].equals("true"));
                    }
                    else if (args[0].equalsIgnoreCase("displayCaveModeStart")) {
                        currentProfile.set((ConfigOption)WorldMapProfiledConfigOptions.DISPLAY_CAVE_MODE_START, (Object)args[1].equals("true"));
                    }
                    else if (args[0].equalsIgnoreCase("caveModeToggleTimer")) {
                        currentProfile.set((ConfigOption)WorldMapProfiledConfigOptions.CAVE_MODE_TOGGLE_TIMER, (Object)(Integer.parseInt(args[1]) / 1000.0));
                    }
                    else if (args[0].equalsIgnoreCase("defaultCaveModeType")) {
                        currentProfile.set((ConfigOption)WorldMapProfiledConfigOptions.DEFAULT_CAVE_MODE_TYPE, (Object)Integer.parseInt(args[1]));
                    }
                    else if (args[0].equalsIgnoreCase("biomeBlending")) {
                        currentProfile.set((ConfigOption)WorldMapProfiledConfigOptions.BIOME_BLENDING, (Object)args[1].equals("true"));
                    }
                    else if (args[0].equalsIgnoreCase("trackedPlayers") || args[0].equalsIgnoreCase("pacPlayers")) {
                        currentProfile.set((ConfigOption)WorldMapProfiledConfigOptions.DISPLAY_TRACKED_PLAYERS, (Object)args[1].equals("true"));
                    }
                    else if (args[0].equalsIgnoreCase("multipleImagesExport")) {
                        primaryConfig.set((ConfigOption)WorldMapPrimaryClientConfigOptions.EXPORT_MULTIPLE_IMAGES, (Object)args[1].equals("true"));
                    }
                    else if (args[0].equalsIgnoreCase("nightExport")) {
                        primaryConfig.set((ConfigOption)WorldMapPrimaryClientConfigOptions.NIGHT_EXPORT, (Object)args[1].equals("true"));
                    }
                    else if (args[0].equalsIgnoreCase("highlightsExport")) {
                        primaryConfig.set((ConfigOption)WorldMapPrimaryClientConfigOptions.EXPORT_HIGHLIGHTS, (Object)args[1].equals("true"));
                    }
                    else if (args[0].equalsIgnoreCase("exportScaleDownSquare")) {
                        primaryConfig.set((ConfigOption)WorldMapPrimaryClientConfigOptions.EXPORT_SCALE_DOWN_SQUARE, (Object)Integer.parseInt(args[1]));
                    }
                    else if (args[0].equalsIgnoreCase("mapWritingDistance")) {
                        currentProfile.set((ConfigOption)WorldMapProfiledConfigOptions.WRITING_DISTANCE, (Object)Integer.parseInt(args[1]));
                    }
                    else if (args[0].equalsIgnoreCase("displayClaims")) {
                        currentProfile.set((ConfigOption)WorldMapProfiledConfigOptions.OPAC_CLAIMS, (Object)args[1].equals("true"));
                    }
                    else if (args[0].equalsIgnoreCase("claimsOpacity")) {
                        final int claimsBorderOpacity = Math.min(100, Math.max(0, Integer.parseInt(args[1])));
                        final int claimsFillOpacity = claimsBorderOpacity * 58 / 100;
                        currentProfile.set((ConfigOption)WorldMapProfiledConfigOptions.OPAC_CLAIMS_BORDER_OPACITY, (Object)claimsBorderOpacity);
                        currentProfile.set((ConfigOption)WorldMapProfiledConfigOptions.OPAC_CLAIMS_FILL_OPACITY, (Object)claimsFillOpacity);
                    }
                    else if (args[0].equalsIgnoreCase("claimsBorderOpacity")) {
                        currentProfile.set((ConfigOption)WorldMapProfiledConfigOptions.OPAC_CLAIMS_BORDER_OPACITY, (Object)Integer.parseInt(args[1]));
                    }
                    else if (args[0].equalsIgnoreCase("claimsFillOpacity")) {
                        currentProfile.set((ConfigOption)WorldMapProfiledConfigOptions.OPAC_CLAIMS_FILL_OPACITY, (Object)Integer.parseInt(args[1]));
                    }
                    else {
                        if (!args[0].equalsIgnoreCase("globalVersion")) {
                            continue;
                        }
                        primaryConfig.set(WorldMapPrimaryClientConfigOptions.GLOBAL_VERSION, (Object)Integer.parseInt(args[1]));
                    }
                }
                catch (Exception e) {
                    WorldMap.LOGGER.info("Skipping setting:" + args[0]);
                }
            }
        }
        finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
    
    public int getRegionCacheHashCode() {
        return this.regionCacheHashCode;
    }
    
    public void updateRegionCacheHashCode() {
        final int currentRegionCacheHashCode = this.regionCacheHashCode;
        if (!Minecraft.func_71410_x().func_152345_ab()) {
            throw new RuntimeException("Wrong thread!");
        }
        final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        final int colours = (int)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.BLOCK_COLORS);
        final boolean terrainDepth = (boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.TERRAIN_DEPTH);
        final int terrainSlopes = (int)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.TERRAIN_SLOPES);
        final boolean biomeBlending = (boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.BIOME_BLENDING);
        final boolean biomeColorsVanillaMode = (boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.BIOME_COLORS_IN_VANILLA);
        final boolean adjustHeightForCarpetLikeBlocks = (boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.ADJUST_HEIGHT_FOR_SHORT_BLOCKS);
        final boolean displayStainedGlass = (boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.STAINED_GLASS);
        final boolean legibleCaveMaps = (boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.LEGIBLE_CAVE_MAPS);
        boolean ignoreHeightMaps = false;
        final WorldMapSession session = WorldMapSession.getCurrentSession();
        if (session != null) {
            ignoreHeightMaps = session.getMapProcessor().getMapWorld().isIgnoreHeightmaps();
        }
        final HashCodeBuilder hcb = new HashCodeBuilder();
        hcb.append(colours).append(terrainDepth).append(terrainSlopes).append(false).append(colours == 1 && biomeColorsVanillaMode).append(ignoreHeightMaps).append(adjustHeightForCarpetLikeBlocks).append(displayStainedGlass).append(legibleCaveMaps).append(biomeBlending);
        final Collection<ResourcePackRepository.Entry> enabledResourcePacks = (Collection<ResourcePackRepository.Entry>)Minecraft.func_71410_x().func_110438_M().func_110613_c();
        for (final ResourcePackRepository.Entry resourcePack : enabledResourcePacks) {
            hcb.append((Object)resourcePack.func_110515_d());
        }
        this.regionCacheHashCode = hcb.toHashCode();
        if (currentRegionCacheHashCode != this.regionCacheHashCode) {
            WorldMap.LOGGER.info("New world map region cache hash code: " + this.regionCacheHashCode);
        }
    }
    
    public static boolean canEditIngameSettings() {
        final WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
        return worldmapSession != null && worldmapSession.getMapProcessor().getMapWorld() != null;
    }
}
