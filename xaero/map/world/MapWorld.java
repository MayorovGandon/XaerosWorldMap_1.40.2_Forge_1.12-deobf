//Decompiled by Procyon!

package xaero.map.world;

import net.minecraft.world.*;
import xaero.map.file.*;
import java.util.function.*;
import net.minecraft.client.*;
import xaero.map.*;
import java.util.stream.*;
import xaero.map.common.config.option.*;
import java.nio.file.attribute.*;
import java.nio.file.*;
import xaero.lib.client.config.*;
import java.io.*;
import java.util.*;
import net.minecraft.client.multiplayer.*;
import xaero.map.gui.*;

public class MapWorld
{
    private MapProcessor mapProcessor;
    private boolean isMultiplayer;
    private String mainId;
    private String oldUnfixedMainId;
    private Hashtable<Integer, MapDimension> dimensions;
    private Integer currentDimensionId;
    private int futureDimensionId;
    private Integer customDimensionId;
    private int futureMultiworldType;
    private int currentMultiworldType;
    private boolean futureMultiworldTypeConfirmed;
    private boolean currentMultiworldTypeConfirmed;
    private boolean ignoreServerLevelId;
    private boolean ignoreHeightmaps;
    private String playerTeleportCommandFormat;
    private String normalTeleportCommandFormat;
    private String dimensionTeleportCommandFormat;
    private boolean useDefaultPlayerTeleport;
    private boolean useDefaultMapTeleport;
    private MapConnectionManager mapConnections;
    
    public MapWorld(final String mainId, final String oldUnfixedMainId, final MapProcessor mapProcessor) {
        this.futureMultiworldTypeConfirmed = true;
        this.currentMultiworldTypeConfirmed = false;
        this.playerTeleportCommandFormat = "/tp @s {name}";
        this.normalTeleportCommandFormat = "/tp @s {x} {y} {z}";
        this.dimensionTeleportCommandFormat = "/execute as @s in {d} run tp {x} {y} {z}";
        this.mainId = mainId;
        this.oldUnfixedMainId = oldUnfixedMainId;
        this.mapProcessor = mapProcessor;
        this.isMultiplayer = MapProcessor.isWorldMultiplayer(MapProcessor.isWorldRealms(mainId), mainId);
        this.dimensions = new Hashtable<Integer, MapDimension>();
        final int n = 0;
        this.currentMultiworldType = n;
        this.futureMultiworldType = n;
        this.useDefaultPlayerTeleport = true;
        this.useDefaultMapTeleport = true;
    }
    
    public MapDimension getDimension(final int dimId) {
        synchronized (this.dimensions) {
            return this.dimensions.get(dimId);
        }
    }
    
    public MapDimension createDimensionUnsynced(final World world, final int dimId) {
        synchronized (this.dimensions) {
            MapDimension result = this.dimensions.get(dimId);
            if (result == null) {
                this.dimensions.put(dimId, result = new MapDimension(this, dimId, this.mapProcessor.getHighlighterRegistry()));
                result.renameLegacyFolder(world);
                result.onCreationUnsynced();
            }
            return result;
        }
    }
    
    public String getMainId() {
        return this.mainId;
    }
    
    public String getOldUnfixedMainId() {
        return this.oldUnfixedMainId;
    }
    
    public String getCurrentMultiworld() {
        final MapDimension container = this.getDimension(this.currentDimensionId);
        return container.getCurrentMultiworld();
    }
    
    public String getFutureMultiworldUnsynced() {
        final MapDimension container = this.getDimension(this.futureDimensionId);
        return container.getFutureMultiworldUnsynced();
    }
    
    public MapDimension getCurrentDimension() {
        final Integer dimId = this.currentDimensionId;
        if (dimId == null) {
            return null;
        }
        return this.getDimension(dimId);
    }
    
    public MapDimension getFutureDimension() {
        return this.getDimension(this.futureDimensionId);
    }
    
    public Integer getCurrentDimensionId() {
        return this.currentDimensionId;
    }
    
    public int getFutureDimensionId() {
        return this.futureDimensionId;
    }
    
    public void setFutureDimensionId(final int dimension) {
        this.futureDimensionId = dimension;
    }
    
    public Integer getCustomDimensionId() {
        return this.customDimensionId;
    }
    
    public void setCustomDimensionId(final Integer dimension) {
        this.customDimensionId = dimension;
    }
    
    public void switchToFutureUnsynced() {
        this.currentDimensionId = this.futureDimensionId;
        this.getDimension(this.currentDimensionId).switchToFutureUnsynced();
    }
    
    public List<MapDimension> getDimensionsList() {
        final List<MapDimension> destList = new ArrayList<MapDimension>();
        this.getDimensions(destList);
        return destList;
    }
    
    public void getDimensions(final List<MapDimension> dest) {
        synchronized (this.dimensions) {
            dest.addAll(this.dimensions.values());
        }
    }
    
    public int getCurrentMultiworldType() {
        return this.currentMultiworldType;
    }
    
    public boolean isMultiplayer() {
        return this.isMultiplayer;
    }
    
    public boolean isCurrentMultiworldTypeConfirmed() {
        return this.currentMultiworldTypeConfirmed;
    }
    
    public int getFutureMultiworldType(final MapDimension dim) {
        return dim.isFutureMultiworldServerBased() ? 2 : this.futureMultiworldType;
    }
    
    public void toggleMultiworldTypeUnsynced() {
        this.unconfirmMultiworldTypeUnsynced();
        this.futureMultiworldType = (this.futureMultiworldType + 1) % 3;
        this.getCurrentDimension().resetCustomMultiworldUnsynced();
        this.saveConfig();
    }
    
    public void unconfirmMultiworldTypeUnsynced() {
        this.futureMultiworldTypeConfirmed = false;
    }
    
    public void confirmMultiworldTypeUnsynced() {
        this.futureMultiworldTypeConfirmed = true;
    }
    
    public boolean isFutureMultiworldTypeConfirmed(final MapDimension dim) {
        return dim.isFutureMultiworldServerBased() || this.futureMultiworldTypeConfirmed;
    }
    
    public void switchToFutureMultiworldTypeUnsynced() {
        final MapDimension futureDim = this.getFutureDimension();
        this.currentMultiworldType = this.getFutureMultiworldType(this.getFutureDimension());
        this.currentMultiworldTypeConfirmed = this.isFutureMultiworldTypeConfirmed(futureDim);
    }
    
    public void load() {
        this.mapConnections = (this.isMultiplayer ? new MapConnectionManager() : new MapConnectionManager() {
            public boolean isConnected(final MapConnectionNode mapKey1, final MapConnectionNode mapKey2) {
                return true;
            }
            
            public void save(final PrintWriter writer) {
            }
        });
        final Path rootSavePath = MapSaveLoad.getRootFolder(this.mainId);
        this.loadConfig(rootSavePath, 10);
        try (final Stream<Path> stream = Files.list(rootSavePath)) {
            stream.forEach(new Consumer<Path>() {
                @Override
                public void accept(final Path folder) {
                    if (!Files.isDirectory(folder, new LinkOption[0])) {
                        return;
                    }
                    final String folderName = folder.getFileName().toString();
                    final Integer folderDimensionId = MapWorld.this.mapProcessor.getDimensionIdForFolder(folderName);
                    if (folderDimensionId == null) {
                        return;
                    }
                    MapWorld.this.createDimensionUnsynced((World)Minecraft.func_71410_x().field_71441_e, folderDimensionId);
                }
            });
        }
        catch (IOException e) {
            WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
        }
    }
    
    private void loadConfig(final Path rootSavePath, final int attempts) {
        final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        final String defaultMapTeleportCommand = (String)configManager.getEffective(WorldMapProfiledConfigOptions.DEFAULT_MAP_TELEPORT_FORMAT);
        final String defaultMapTeleportDimensionCommand = (String)configManager.getEffective(WorldMapProfiledConfigOptions.DEFAULT_MAP_TELEPORT_DIMENSION_FORMAT);
        final String defaultPlayerTeleportCommand = (String)configManager.getEffective(WorldMapProfiledConfigOptions.DEFAULT_PLAYER_TELEPORT_FORMAT);
        final MapProcessor mp = this.mapProcessor;
        BufferedReader reader = null;
        try {
            if (!Files.exists(rootSavePath, new LinkOption[0])) {
                Files.createDirectories(rootSavePath, (FileAttribute<?>[])new FileAttribute[0]);
            }
            final Path configFile = rootSavePath.resolve("server_config.txt");
            final Path oldOverworldSavePath = mp.getMapSaveLoad().getOldFolder(this.oldUnfixedMainId, "null");
            final Path oldConfigFile = oldOverworldSavePath.resolve("server_config.txt");
            if (!Files.exists(configFile, new LinkOption[0]) && Files.exists(oldConfigFile, new LinkOption[0])) {
                Files.move(oldConfigFile, configFile, new CopyOption[0]);
            }
            if (Files.exists(configFile, new LinkOption[0])) {
                reader = new BufferedReader(new FileReader(configFile.toFile()));
                boolean setUseDefaultMapTeleport = false;
                boolean setUseDefaultPlayerTeleport = false;
                String line;
                while ((line = reader.readLine()) != null) {
                    final String[] args = line.split(":");
                    if (this.isMultiplayer) {
                        if (args[0].equals("multiworldType")) {
                            this.futureMultiworldType = Integer.parseInt(args[1]);
                        }
                        else if (args[0].equals("ignoreServerLevelId")) {
                            this.ignoreServerLevelId = args[1].equals("true");
                        }
                    }
                    if (args[0].equals("ignoreHeightmaps")) {
                        this.ignoreHeightmaps = args[1].equals("true");
                    }
                    else if (args[0].equals("playerTeleportCommandFormat")) {
                        this.playerTeleportCommandFormat = line.substring(line.indexOf(58) + 1);
                    }
                    else if (args[0].equals("teleportCommandFormat")) {
                        this.normalTeleportCommandFormat = line.substring(line.indexOf(58) + 1);
                        this.dimensionTeleportCommandFormat = "/execute as @s in {d} run " + this.normalTeleportCommandFormat.substring(1);
                    }
                    else if (args[0].equals("dimensionTeleportCommandFormat")) {
                        this.dimensionTeleportCommandFormat = line.substring(line.indexOf(58) + 1);
                    }
                    else if (args[0].equals("normalTeleportCommandFormat")) {
                        this.normalTeleportCommandFormat = line.substring(line.indexOf(58) + 1);
                    }
                    else if (args[0].equals("useDefaultMapTeleport")) {
                        this.useDefaultMapTeleport = args[1].equals("true");
                        setUseDefaultMapTeleport = true;
                    }
                    else if (args[0].equals("useDefaultPlayerTeleport")) {
                        this.useDefaultPlayerTeleport = args[1].equals("true");
                        setUseDefaultPlayerTeleport = true;
                    }
                    else {
                        if (!this.isMultiplayer || !args[0].equals("connection")) {
                            continue;
                        }
                        final String mapKey1 = args[1];
                        if (args.length <= 2) {
                            continue;
                        }
                        final String mapKey2 = args[2];
                        final MapConnectionNode connectionNode1 = MapConnectionNode.fromString(mapKey1);
                        final MapConnectionNode connectionNode2 = MapConnectionNode.fromString(mapKey2);
                        if (connectionNode1 == null || connectionNode2 == null) {
                            continue;
                        }
                        this.mapConnections.addConnection(connectionNode1, connectionNode2);
                    }
                }
                if (!setUseDefaultMapTeleport) {
                    this.useDefaultMapTeleport = (this.normalTeleportCommandFormat.equals(defaultMapTeleportCommand) && this.dimensionTeleportCommandFormat.equals(defaultMapTeleportDimensionCommand));
                }
                if (!setUseDefaultPlayerTeleport) {
                    this.useDefaultPlayerTeleport = this.playerTeleportCommandFormat.equals(defaultPlayerTeleportCommand);
                }
            }
            else {
                this.saveConfig();
            }
        }
        catch (IOException e2) {
            if (attempts > 1) {
                if (reader != null) {
                    try {
                        reader.close();
                    }
                    catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                WorldMap.LOGGER.warn("IO exception while loading world map config. Retrying... " + attempts);
                try {
                    Thread.sleep(20L);
                }
                catch (InterruptedException ex) {}
                this.loadConfig(rootSavePath, attempts - 1);
                return;
            }
            throw new RuntimeException(e2);
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e3) {
                    WorldMap.LOGGER.error("suppressed exception", (Throwable)e3);
                }
            }
        }
    }
    
    public void saveConfig() {
        final Path rootSavePath = MapSaveLoad.getRootFolder(this.mainId);
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter(rootSavePath.resolve("server_config.txt").toFile()));
            if (this.isMultiplayer) {
                writer.println("multiworldType:" + this.futureMultiworldType);
                writer.println("ignoreServerLevelId:" + this.ignoreServerLevelId);
            }
            writer.println("ignoreHeightmaps:" + this.ignoreHeightmaps);
            writer.println("playerTeleportCommandFormat:" + this.playerTeleportCommandFormat);
            writer.println("normalTeleportCommandFormat:" + this.normalTeleportCommandFormat);
            writer.println("dimensionTeleportCommandFormat:" + this.dimensionTeleportCommandFormat);
            writer.println("useDefaultMapTeleport:" + this.useDefaultMapTeleport);
            writer.println("useDefaultPlayerTeleport:" + this.useDefaultPlayerTeleport);
            if (this.isMultiplayer) {
                this.mapConnections.save(writer);
            }
        }
        catch (IOException e) {
            WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
        }
        finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
    
    public MapProcessor getMapProcessor() {
        return this.mapProcessor;
    }
    
    public boolean isIgnoreServerLevelId() {
        return this.ignoreServerLevelId;
    }
    
    public boolean isIgnoreHeightmaps() {
        return this.ignoreHeightmaps;
    }
    
    public void setIgnoreHeightmaps(final boolean ignoreHeightmaps) {
        this.ignoreHeightmaps = ignoreHeightmaps;
    }
    
    public String getPlayerTeleportCommandFormat() {
        return this.playerTeleportCommandFormat;
    }
    
    public void setPlayerTeleportCommandFormat(final String playerTeleportCommandFormat) {
        this.playerTeleportCommandFormat = playerTeleportCommandFormat;
    }
    
    public String getEffectivePlayerTeleportCommandFormat() {
        if (!this.useDefaultPlayerTeleport) {
            return this.getPlayerTeleportCommandFormat();
        }
        final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        return (String)configManager.getEffective(WorldMapProfiledConfigOptions.DEFAULT_PLAYER_TELEPORT_FORMAT);
    }
    
    public String getTeleportCommandFormat() {
        return this.normalTeleportCommandFormat;
    }
    
    public void setTeleportCommandFormat(final String teleportCommandFormat) {
        this.normalTeleportCommandFormat = teleportCommandFormat;
    }
    
    public String getEffectiveTeleportCommandFormat() {
        if (!this.useDefaultMapTeleport) {
            return this.getTeleportCommandFormat();
        }
        final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        return (String)configManager.getEffective(WorldMapProfiledConfigOptions.DEFAULT_MAP_TELEPORT_FORMAT);
    }
    
    public String getDimensionTeleportCommandFormat() {
        return this.dimensionTeleportCommandFormat;
    }
    
    public void setDimensionTeleportCommandFormat(final String dimensionTeleportCommandFormat) {
        this.dimensionTeleportCommandFormat = dimensionTeleportCommandFormat;
    }
    
    public String getEffectiveDimensionTeleportCommandFormat() {
        if (!this.useDefaultMapTeleport) {
            return this.getDimensionTeleportCommandFormat();
        }
        final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        return (String)configManager.getEffective(WorldMapProfiledConfigOptions.DEFAULT_MAP_TELEPORT_DIMENSION_FORMAT);
    }
    
    public void clearAllCachedHighlightHashes() {
        synchronized (this.dimensions) {
            for (final MapDimension dim : this.dimensions.values()) {
                dim.getHighlightHandler().clearCachedHashes();
            }
        }
    }
    
    public boolean isUsingCustomDimension() {
        final World world = (World)this.mapProcessor.getWorld();
        return world != null && (this.getCurrentDimensionId() == null || world.field_73011_w.getDimension() != this.getCurrentDimensionId());
    }
    
    public boolean isUsingUnknownDimensionType() {
        return this.getCurrentDimension().isUsingUnknownDimensionType();
    }
    
    public boolean isCacheOnlyMode() {
        return this.getCurrentDimension().isCacheOnlyMode();
    }
    
    public void onWorldChangeUnsynced(final WorldClient world) {
        synchronized (this.dimensions) {
            for (final MapDimension dim : this.dimensions.values()) {
                dim.onWorldChangeUnsynced((World)world);
            }
        }
    }
    
    public int getPotentialDimId() {
        final Integer customDimId = this.getCustomDimensionId();
        return (customDimId == null) ? this.mapProcessor.mainWorld.field_73011_w.getDimension() : customDimId;
    }
    
    public MapConnectionNode getPlayerMapKey() {
        this.mapProcessor.updateVisitedDimension(this.mapProcessor.mainWorld);
        final int dimId = this.mapProcessor.mainWorld.field_73011_w.getDimension();
        final MapDimension dim = this.getDimension(dimId);
        return (dim == null) ? null : dim.getPlayerMapKey();
    }
    
    public MapConnectionManager getMapConnections() {
        return this.mapConnections;
    }
    
    public void toggleDimension(final boolean forward) {
        final MapDimension futureDimension = this.getFutureDimension();
        if (futureDimension == null) {
            return;
        }
        final GuiDimensionOptions dimOptions = GuiMapSwitching.getSortedDimensionOptions(futureDimension);
        final int step = forward ? 1 : (dimOptions.values.length - 1);
        final int nextIndex = (dimOptions.selected == -1) ? 0 : ((dimOptions.selected + step) % dimOptions.values.length);
        Integer nextDimId = dimOptions.values[nextIndex];
        if (nextDimId == Minecraft.func_71410_x().field_71441_e.field_73011_w.getDimension()) {
            nextDimId = null;
        }
        this.setCustomDimensionId(nextDimId);
        this.mapProcessor.checkForWorldUpdate();
    }
    
    public static String convertWorldFolderToRootId(final int version, final String worldFolder) {
        String rootId = worldFolder.replaceAll("_", "^us^");
        if (MapProcessor.isWorldMultiplayer(MapProcessor.isWorldRealms(rootId), rootId)) {
            rootId = "^e^" + rootId;
        }
        if (version >= 3) {
            rootId = rootId.replace("[", "%lb%").replace("]", "%rb%");
        }
        return rootId;
    }
    
    public boolean isUsingDefaultMapTeleport() {
        return this.useDefaultMapTeleport;
    }
    
    public boolean isUsingDefaultPlayerTeleport() {
        return this.useDefaultPlayerTeleport;
    }
    
    public void setUseDefaultMapTeleport(final boolean useDefaultMapTeleport) {
        this.useDefaultMapTeleport = useDefaultMapTeleport;
    }
    
    public void setUseDefaultPlayerTeleport(final boolean useDefaultPlayerTeleport) {
        this.useDefaultPlayerTeleport = useDefaultPlayerTeleport;
    }
}
