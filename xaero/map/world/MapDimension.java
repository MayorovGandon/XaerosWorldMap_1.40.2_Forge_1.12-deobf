//Decompiled by Procyon!

package xaero.map.world;

import xaero.map.file.*;
import xaero.map.highlight.*;
import xaero.map.common.config.option.*;
import xaero.lib.common.config.option.*;
import xaero.lib.client.config.*;
import net.minecraft.client.*;
import java.nio.charset.*;
import java.util.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.io.*;
import java.util.stream.*;
import org.apache.commons.io.*;
import net.minecraftforge.common.*;
import xaero.map.*;
import xaero.map.util.linked.*;
import xaero.map.region.*;
import net.minecraft.server.integrated.*;
import net.minecraft.world.*;
import net.minecraft.client.multiplayer.*;
import net.minecraft.util.math.*;
import xaero.map.misc.*;

public class MapDimension
{
    private final MapWorld mapWorld;
    private final int dimId;
    private final List<String> multiworldIds;
    private final Hashtable<String, String> multiworldNames;
    private final Hashtable<String, String> autoMultiworldBindings;
    private final DimensionHighlighterHandler highlightHandler;
    private MapDimensionTypeInfo dimensionType;
    private float shadowR;
    private float shadowG;
    private float shadowB;
    private String futureAutoMultiworldBinding;
    private String futureCustomSelectedMultiworld;
    public boolean futureMultiworldWritable;
    public boolean futureMultiworldServerBased;
    private String currentMultiworld;
    public boolean currentMultiworldWritable;
    private String confirmedMultiworld;
    private final LayeredRegionManager mapRegions;
    private List<MapRegion> regionBackCompList;
    private final Hashtable<Integer, Hashtable<Integer, RegionDetection>> worldSaveDetectedRegions;
    private final LinkedChain<RegionDetection> worldSaveDetectedRegionsLinked;
    private boolean doneRegionDetection;
    public final ArrayList<LeveledRegion<?>> regionsToCache;
    private MapFullReloader fullReloader;
    private int caveModeType;
    private static final int CAVE_MODE_TYPES = 3;
    
    public MapDimension(final MapWorld mapWorld, final int dimId, final HighlighterRegistry highlighterRegistry) {
        this.shadowR = 1.0f;
        this.shadowG = 1.0f;
        this.shadowB = 1.0f;
        this.mapWorld = mapWorld;
        this.dimId = dimId;
        this.multiworldIds = new ArrayList<String>();
        this.multiworldNames = new Hashtable<String, String>();
        this.mapRegions = new LayeredRegionManager(this);
        this.autoMultiworldBindings = new Hashtable<String, String>();
        this.regionsToCache = new ArrayList<LeveledRegion<?>>();
        this.regionBackCompList = new ArrayList<MapRegion>();
        this.highlightHandler = new DimensionHighlighterHandler(this, dimId, highlighterRegistry);
        this.worldSaveDetectedRegions = new Hashtable<Integer, Hashtable<Integer, RegionDetection>>();
        this.worldSaveDetectedRegionsLinked = (LinkedChain<RegionDetection>)new LinkedChain();
        final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        this.caveModeType = (int)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.DEFAULT_CAVE_MODE_TYPE);
    }
    
    public String getCurrentMultiworld() {
        return this.currentMultiworld;
    }
    
    public boolean isUsingWorldSave() {
        return !this.mapWorld.isMultiplayer() && (this.currentMultiworld == null || this.currentMultiworld.isEmpty());
    }
    
    public boolean isFutureUsingWorldSaveUnsynced() {
        return !this.mapWorld.isMultiplayer() && (this.getFutureMultiworldUnsynced() == null || this.getFutureMultiworldUnsynced().isEmpty());
    }
    
    public List<String> getMultiworldIdsCopy() {
        synchronized (this.multiworldIds) {
            return new ArrayList<String>(this.multiworldIds);
        }
    }
    
    public void updateFutureAutomaticUnsynced(final Minecraft mc, final Object baseObject) {
        if (!this.mapWorld.isMultiplayer()) {
            this.futureAutoMultiworldBinding = "";
            this.futureMultiworldServerBased = false;
        }
        else if (baseObject != null) {
            if (baseObject instanceof BlockPos) {
                final BlockPos dimSpawn = (BlockPos)baseObject;
                this.futureAutoMultiworldBinding = "mw" + (dimSpawn.func_177958_n() >> 6) + "," + (dimSpawn.func_177956_o() >> 6) + "," + (dimSpawn.func_177952_p() >> 6);
                this.futureMultiworldServerBased = false;
            }
            else if (baseObject instanceof Integer) {
                final int levelId = (int)baseObject;
                this.futureAutoMultiworldBinding = "mw$" + levelId;
                this.futureMultiworldServerBased = true;
            }
        }
        else {
            this.futureAutoMultiworldBinding = "unknown";
        }
    }
    
    public String getFutureCustomSelectedMultiworld() {
        return this.futureCustomSelectedMultiworld;
    }
    
    public String getFutureMultiworldUnsynced() {
        if (this.futureCustomSelectedMultiworld == null) {
            return this.getFutureAutoMultiworld();
        }
        return this.futureCustomSelectedMultiworld;
    }
    
    public void switchToFutureUnsynced() {
        this.addMultiworldChecked(this.currentMultiworld = this.getFutureMultiworldUnsynced());
    }
    
    public void switchToFutureMultiworldWritableValueUnsynced() {
        this.currentMultiworldWritable = this.futureMultiworldWritable;
    }
    
    public LayeredRegionManager getLayeredMapRegions() {
        return this.mapRegions;
    }
    
    @Deprecated
    public LeveledRegionManager getMapRegions() {
        return this.mapRegions.getLayer(Integer.MAX_VALUE).getMapRegions();
    }
    
    @Deprecated
    public List<MapRegion> getMapRegionsList() {
        return this.regionBackCompList;
    }
    
    public void clear() {
        this.regionsToCache.clear();
        this.mapRegions.clear();
        this.regionBackCompList.clear();
        this.worldSaveDetectedRegions.clear();
        this.worldSaveDetectedRegionsLinked.reset();
        this.doneRegionDetection = false;
        this.clearFullMapReload();
    }
    
    public void preDetection() {
        this.doneRegionDetection = true;
        this.mapRegions.preDetection();
    }
    
    public Path getMainFolderPath() {
        return this.mapWorld.getMapProcessor().getMapSaveLoad().getMainFolder(this.mapWorld.getMainId(), this.mapWorld.getMapProcessor().getDimensionName(this.dimId));
    }
    
    public Path getOldFolderPath() {
        return this.mapWorld.getMapProcessor().getMapSaveLoad().getOldFolder(this.mapWorld.getOldUnfixedMainId(), this.mapWorld.getMapProcessor().getDimensionName(this.dimId));
    }
    
    public void saveConfigUnsynced() {
        final Path dimensionSavePath = this.getMainFolderPath();
        try (final BufferedOutputStream bufferedOutput = new BufferedOutputStream(new FileOutputStream(dimensionSavePath.resolve("dimension_config.txt").toFile()));
             final PrintWriter writer = new PrintWriter(new OutputStreamWriter(bufferedOutput, StandardCharsets.UTF_8))) {
            if (this.confirmedMultiworld != null) {
                writer.println("confirmedMultiworld:" + this.confirmedMultiworld);
            }
            for (final Map.Entry<String, String> bindingEntry : this.autoMultiworldBindings.entrySet()) {
                writer.println("autoMWBinding:" + bindingEntry.getKey() + ":" + bindingEntry.getValue());
            }
            for (final Map.Entry<String, String> bindingEntry : this.multiworldNames.entrySet()) {
                writer.println("MWName:" + bindingEntry.getKey() + ":" + bindingEntry.getValue().replace(":", "^col^"));
            }
            writer.println("caveModeType:" + this.caveModeType);
            if (this.dimensionType != null) {
                writer.println("dimensionType:" + this.dimensionType);
            }
        }
        catch (IOException e) {
            WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
        }
    }
    
    private void loadConfigUnsynced(final int attempts) {
        final Path dimensionSavePath = this.getMainFolderPath();
        BufferedReader reader = null;
        try {
            final Path oldDimensionSavePath = this.getOldFolderPath();
            if (!Files.exists(dimensionSavePath, new LinkOption[0]) && Files.exists(oldDimensionSavePath, new LinkOption[0])) {
                Files.move(oldDimensionSavePath, dimensionSavePath, new CopyOption[0]);
            }
            if (!Files.exists(dimensionSavePath, new LinkOption[0])) {
                Files.createDirectories(dimensionSavePath, (FileAttribute<?>[])new FileAttribute[0]);
            }
            this.loadMultiworldsList(dimensionSavePath);
            final Path configFile = dimensionSavePath.resolve("dimension_config.txt");
            if (Files.exists(configFile, new LinkOption[0])) {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(configFile.toFile()), "UTF8"));
                String line;
                while ((line = reader.readLine()) != null) {
                    final String[] args = line.split(":");
                    if (args[0].equals("confirmedMultiworld")) {
                        final String savedMultiworld = (args.length > 1) ? args[1] : "";
                        if (this.multiworldIds.contains(savedMultiworld)) {
                            this.confirmedMultiworld = savedMultiworld;
                        }
                    }
                    else if (args[0].equals("autoMWBinding")) {
                        this.bindAutoMultiworld(args[1], args[2]);
                    }
                    else if (args[0].equals("MWName")) {
                        this.setMultiworldName(args[1], args[2].replace("^col^", ":"));
                    }
                    else if (args[0].equals("dimensionType")) {
                        this.dimensionType = MapDimensionTypeInfo.fromString(args[1], line.substring(line.lastIndexOf(58) + 1));
                    }
                    if (args[0].equals("caveModeType")) {
                        this.caveModeType = Integer.parseInt(args[1]);
                    }
                }
            }
            else {
                this.saveConfigUnsynced();
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
                WorldMap.LOGGER.warn("IO exception while loading world map dimension config. Retrying... " + attempts);
                try {
                    Thread.sleep(20L);
                }
                catch (InterruptedException ex) {}
                this.loadConfigUnsynced(attempts - 1);
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
    
    public void pickDefaultCustomMultiworldUnsynced() {
        if (this.multiworldIds.isEmpty()) {
            this.futureCustomSelectedMultiworld = "mw$default";
            this.multiworldIds.add(this.futureCustomSelectedMultiworld);
            this.setMultiworldName(this.futureCustomSelectedMultiworld, "Default");
        }
        else {
            final int indexOfAuto = this.multiworldIds.indexOf(this.getFutureAutoMultiworld());
            this.futureCustomSelectedMultiworld = this.multiworldIds.get((indexOfAuto != -1) ? indexOfAuto : 0);
        }
    }
    
    private void loadMultiworldsList(final Path dimensionSavePath) {
        if (!this.mapWorld.isMultiplayer()) {
            this.multiworldIds.add("");
        }
        try {
            final Stream<Path> subFolders = Files.list(dimensionSavePath);
            for (final Path path : subFolders) {
                if (path.toFile().isDirectory()) {
                    final String folderName = path.getFileName().toString();
                    final boolean autoMultiworldFormat = folderName.matches("^mw(-?\\d+),(-?\\d+),(-?\\d+)$");
                    final boolean levelIdMultiworldFormat = folderName.startsWith("mw$");
                    final boolean customMultiworldFormat = folderName.startsWith("cm$");
                    if (!autoMultiworldFormat && !levelIdMultiworldFormat && !customMultiworldFormat) {
                        continue;
                    }
                    this.multiworldIds.add(folderName);
                }
            }
            subFolders.close();
        }
        catch (IOException e) {
            WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
        }
    }
    
    public void confirmMultiworldUnsynced() {
        if (!this.futureMultiworldWritable) {
            this.futureMultiworldWritable = true;
            if (this.mapWorld.getFutureMultiworldType(this) == 2 && this.futureCustomSelectedMultiworld != null) {
                this.makeCustomSelectedMultiworldAutoUnsynced();
            }
            this.confirmedMultiworld = this.getFutureMultiworldUnsynced();
            this.saveConfigUnsynced();
        }
    }
    
    private void makeCustomSelectedMultiworldAutoUnsynced() {
        final String currentAutoMultiworld = this.getFutureAutoMultiworld();
        boolean currentBindingFound = false;
        for (final Map.Entry<String, String> bindingEntry : this.autoMultiworldBindings.entrySet()) {
            if (bindingEntry.getValue().equals(this.futureCustomSelectedMultiworld)) {
                this.bindAutoMultiworld(bindingEntry.getKey(), currentAutoMultiworld);
                currentBindingFound = true;
                break;
            }
        }
        if (!currentBindingFound && !this.futureCustomSelectedMultiworld.startsWith("cm$")) {
            this.bindAutoMultiworld(this.futureCustomSelectedMultiworld, currentAutoMultiworld);
        }
        this.bindAutoMultiworld(this.futureAutoMultiworldBinding, this.futureCustomSelectedMultiworld);
        this.futureCustomSelectedMultiworld = null;
        this.saveConfigUnsynced();
    }
    
    private void bindAutoMultiworld(final String binding, final String multiworld) {
        if (binding.equals(multiworld)) {
            this.autoMultiworldBindings.remove(binding);
        }
        else {
            this.autoMultiworldBindings.put(binding, multiworld);
        }
    }
    
    public void resetCustomMultiworldUnsynced() {
        this.futureCustomSelectedMultiworld = ((this.mapWorld.getFutureMultiworldType(this) == 2) ? null : this.confirmedMultiworld);
        if (this.futureCustomSelectedMultiworld == null && this.mapWorld.isMultiplayer() && this.mapWorld.getFutureMultiworldType(this) < 2) {
            this.pickDefaultCustomMultiworldUnsynced();
        }
        this.futureMultiworldWritable = (this.mapWorld.getFutureMultiworldType(this) != 1 && this.mapWorld.isFutureMultiworldTypeConfirmed(this));
    }
    
    public void setMultiworldUnsynced(final String nextMW) {
        final String cmw = (this.futureCustomSelectedMultiworld == null) ? this.getFutureMultiworldUnsynced() : this.futureCustomSelectedMultiworld;
        this.futureCustomSelectedMultiworld = nextMW;
        this.futureMultiworldWritable = false;
        WorldMap.LOGGER.info(cmw + " -> " + this.futureCustomSelectedMultiworld);
    }
    
    private boolean multiworldExists(final String mw) {
        synchronized (this.multiworldIds) {
            return this.multiworldIds.contains(mw);
        }
    }
    
    public boolean addMultiworldChecked(final String mw) {
        synchronized (this.multiworldIds) {
            if (!this.multiworldIds.contains(mw)) {
                this.multiworldIds.add(mw);
                return true;
            }
        }
        return false;
    }
    
    public String getMultiworldName(final String mwId) {
        if (mwId.isEmpty()) {
            return "gui.xaero_world_save";
        }
        final String tableName = this.multiworldNames.get(mwId);
        if (tableName != null) {
            return tableName;
        }
        if (this.multiworldExists(mwId)) {
            int index = 1;
            String automaticName;
            while (this.multiworldNames.containsValue(automaticName = "Map " + index++)) {}
            this.setMultiworldName(mwId, automaticName);
            synchronized (this.mapWorld.getMapProcessor().uiSync) {
                this.saveConfigUnsynced();
            }
            return automaticName;
        }
        return mwId;
    }
    
    public void setMultiworldName(final String mwId, final String mwName) {
        this.multiworldNames.put(mwId, mwName);
    }
    
    private String getFutureAutoMultiworld() {
        final String futureAutoMultiworldBinding = this.futureAutoMultiworldBinding;
        if (futureAutoMultiworldBinding == null) {
            return null;
        }
        final String boundMultiworld = this.autoMultiworldBindings.get(futureAutoMultiworldBinding);
        if (boundMultiworld == null) {
            return futureAutoMultiworldBinding;
        }
        return boundMultiworld;
    }
    
    public MapWorld getMapWorld() {
        return this.mapWorld;
    }
    
    public void deleteMultiworldMapDataUnsynced(final String mwId) {
        try {
            final Path currentDimFolder = this.getMainFolderPath();
            final Path currentMWFolder = currentDimFolder.resolve(mwId);
            final Path binFolder = currentDimFolder.resolve("last deleted");
            final Path binMWFolder = binFolder.resolve(mwId);
            if (!Files.exists(binFolder, new LinkOption[0])) {
                Files.createDirectories(binFolder, (FileAttribute<?>[])new FileAttribute[0]);
            }
            FileUtils.cleanDirectory(binFolder.toFile());
            Files.move(currentMWFolder, binMWFolder, new CopyOption[0]);
        }
        catch (Exception e) {
            WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
        }
    }
    
    public void deleteMultiworldId(final String mwId) {
        synchronized (this.multiworldIds) {
            this.multiworldIds.remove(mwId);
            this.multiworldNames.remove(mwId);
            if (mwId.equals(this.confirmedMultiworld)) {
                this.confirmedMultiworld = null;
            }
        }
    }
    
    public int getDimId() {
        return this.dimId;
    }
    
    public boolean hasConfirmedMultiworld() {
        return this.confirmedMultiworld != null;
    }
    
    public boolean isFutureMultiworldServerBased() {
        return this.futureMultiworldServerBased;
    }
    
    public void renameLegacyFolder(final World world) {
        final MapProcessor mapProcessor = this.mapWorld.getMapProcessor();
        final Path newerFolderPath = this.getOldFolderPath();
        if (!Files.exists(this.getMainFolderPath(), new LinkOption[0]) && !Files.exists(newerFolderPath, new LinkOption[0])) {
            String legacyFolderName;
            if (world != null && world.field_73011_w.getDimension() == this.dimId) {
                legacyFolderName = mapProcessor.getDimensionLegacyName(world.field_73011_w);
            }
            else {
                WorldProvider dimWorldProvider = null;
                try {
                    dimWorldProvider = DimensionManager.createProviderFor(this.dimId);
                    legacyFolderName = mapProcessor.getDimensionLegacyName(dimWorldProvider);
                }
                catch (RuntimeException re) {
                    WorldMap.LOGGER.info("Couldn't create world provider to get dimension folder name: " + this.dimId);
                    return;
                }
            }
            final Path legacyFolderPath = mapProcessor.getMapSaveLoad().getOldFolder(this.mapWorld.getOldUnfixedMainId(), legacyFolderName);
            if (Files.exists(legacyFolderPath, new LinkOption[0])) {
                try {
                    Files.move(legacyFolderPath, newerFolderPath, new CopyOption[0]);
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    
    public DimensionHighlighterHandler getHighlightHandler() {
        return this.highlightHandler;
    }
    
    public void onClearCachedHighlightHash(final int regionX, final int regionZ) {
        this.mapRegions.onClearCachedHighlightHash(regionX, regionZ);
    }
    
    public void onClearCachedHighlightHashes() {
        this.mapRegions.onClearCachedHighlightHashes();
    }
    
    public boolean hasDoneRegionDetection() {
        return this.doneRegionDetection;
    }
    
    public void addWorldSaveRegionDetection(final RegionDetection regionDetection) {
        synchronized (this.worldSaveDetectedRegions) {
            Hashtable<Integer, RegionDetection> column = this.worldSaveDetectedRegions.get(regionDetection.getRegionX());
            if (column == null) {
                this.worldSaveDetectedRegions.put(regionDetection.getRegionX(), column = new Hashtable<Integer, RegionDetection>());
            }
            column.put(regionDetection.getRegionZ(), regionDetection);
            this.worldSaveDetectedRegionsLinked.add((ILinkedChainNode)regionDetection);
        }
    }
    
    public RegionDetection getWorldSaveRegionDetection(final int x, final int z) {
        if (this.worldSaveDetectedRegions == null) {
            return null;
        }
        final Hashtable<Integer, RegionDetection> column = this.worldSaveDetectedRegions.get(x);
        if (column != null) {
            return column.get(z);
        }
        return null;
    }
    
    public int getCaveModeType() {
        return this.caveModeType;
    }
    
    public void toggleCaveModeType(final boolean forward) {
        this.caveModeType += (forward ? 1 : -1);
        if (forward) {
            if (this.caveModeType >= 3) {
                this.caveModeType = 0;
            }
            return;
        }
        if (this.caveModeType < 0) {
            this.caveModeType = 2;
        }
    }
    
    public Iterable<Hashtable<Integer, RegionDetection>> getWorldSaveDetectedRegions() {
        return this.worldSaveDetectedRegions.values();
    }
    
    public Iterable<RegionDetection> getLinkedWorldSaveDetectedRegions() {
        return (Iterable<RegionDetection>)this.worldSaveDetectedRegionsLinked;
    }
    
    public MapFullReloader getFullReloader() {
        return this.fullReloader;
    }
    
    public void startFullMapReload(final int caveLayer, final boolean resave, final MapProcessor mapProcessor) {
        final MapLayer layer = this.mapRegions.getLayer(caveLayer);
        this.fullReloader = new MapFullReloader(caveLayer, resave, (Iterator)layer.getLinkedCompleteWorldSaveDetectedRegions().iterator(), this, mapProcessor);
    }
    
    public void clearFullMapReload() {
        this.fullReloader = null;
    }
    
    public MapDimensionTypeInfo getDimensionType() {
        if (this.dimensionType != null) {
            return this.dimensionType;
        }
        return this.dimensionType = getDimensionType(this.dimId);
    }
    
    private static MapDimensionTypeInfo getDimensionType(final Integer dimId) {
        if (dimId == null) {
            return null;
        }
        if (dimId == -1) {
            return new MapDimensionTypeInfo(DimensionType.NETHER.func_186065_b(), false, 0.1f, 256, 128, true, false, false, 0.5f, 8.0, "DIM-1");
        }
        if (dimId == 0) {
            return new MapDimensionTypeInfo(DimensionType.OVERWORLD.func_186065_b(), true, 0.0f, 256, 256, false, true, false, new WorldProviderSurface().func_76563_a(6000L, 1.0f), 1.0, null);
        }
        if (dimId == 1) {
            return new MapDimensionTypeInfo(DimensionType.THE_END.func_186065_b(), false, 0.0f, 256, 256, false, false, true, 0.0f, 1.0, "DIM1");
        }
        final IntegratedServer integratedServer = Minecraft.func_71410_x().func_71401_C();
        if (integratedServer == null) {
            return null;
        }
        final WorldServer serverLevel = integratedServer.func_71218_a((int)dimId);
        if (serverLevel == null) {
            return null;
        }
        return createDimensionType((World)serverLevel);
    }
    
    public static MapDimensionTypeInfo getDimensionType(final MapDimension dim, final Integer dimId) {
        return (dim == null) ? getDimensionType(dimId) : dim.getDimensionType();
    }
    
    public void onWorldChangeUnsynced(final World newWorld) {
        if (newWorld != null && this.dimId == newWorld.field_73011_w.getDimension()) {
            this.dimensionType = createDimensionType(newWorld);
            this.saveConfigUnsynced();
        }
    }
    
    public boolean isUsingUnknownDimensionType() {
        return this.getDimensionType() == null;
    }
    
    public boolean isCacheOnlyMode() {
        return this.isUsingUnknownDimensionType();
    }
    
    public void onCreationUnsynced() {
        this.loadConfigUnsynced(10);
        if (this.dimId == 0 || (this.dimensionType != null && this.dimensionType.isSurfaceWorld())) {
            this.shadowR = 0.518f;
            this.shadowG = 0.678f;
            this.shadowB = 1.0f;
        }
        else if (this.dimId == -1 || (this.dimensionType != null && this.dimensionType.isNether())) {
            this.shadowR = 1.0f;
            this.shadowG = 0.0f;
            this.shadowB = 0.0f;
        }
    }
    
    public float getShadowR() {
        return this.shadowR;
    }
    
    public float getShadowG() {
        return this.shadowG;
    }
    
    public float getShadowB() {
        return this.shadowB;
    }
    
    public float getSkyDarken(final float partial, final WorldClient world) {
        if (this.dimId == world.field_73011_w.getDimension()) {
            return world.getSunBrightnessFactor(1.0f);
        }
        final MapDimensionTypeInfo dimType = this.getDimensionType();
        if (dimType == null) {
            return 1.0f;
        }
        final float timeOfDay = dimType.getNoonCelestialAngle();
        float brightness = 1.0f - (MathHelper.func_76134_b(timeOfDay * 6.2831855f) * 2.0f + 0.5f);
        brightness = 1.0f - MathHelper.func_76131_a(brightness, 0.0f, 1.0f);
        return brightness * 0.8f + 0.2f;
    }
    
    public double calculateDimScale() {
        final MapDimensionTypeInfo dimType = this.getDimensionType();
        return (dimType == null) ? 1.0 : dimType.getCoordinateScale();
    }
    
    public double calculateDimDiv(final WorldProvider actualDimension) {
        return this.calculateDimScale() / ((actualDimension == null) ? 1.0 : Misc.getDimensionTypeScale(actualDimension));
    }
    
    public double calculateDimDiv(final MapDimensionTypeInfo actualDimension) {
        return this.calculateDimScale() / ((actualDimension == null) ? 1.0 : actualDimension.getCoordinateScale());
    }
    
    public MapConnectionNode getPlayerMapKey() {
        String playerMW = (this.mapWorld.getFutureMultiworldType(this) == 1) ? null : this.getFutureAutoMultiworld();
        if (playerMW == null) {
            playerMW = this.confirmedMultiworld;
        }
        if (playerMW == null) {
            return null;
        }
        return new MapConnectionNode(this.dimId, playerMW);
    }
    
    public MapConnectionNode getSelectedMapKeyUnsynced() {
        String selectedMW = this.getFutureMultiworldUnsynced();
        if (selectedMW == null) {
            selectedMW = this.getCurrentMultiworld();
        }
        if (selectedMW == null) {
            return null;
        }
        return new MapConnectionNode(this.dimId, selectedMW);
    }
    
    public boolean isAutoSelected() {
        final String selectedMW = this.getFutureCustomSelectedMultiworld();
        return selectedMW == null || selectedMW.equals(this.getFutureAutoMultiworld());
    }
    
    public String getDropdownLabel() {
        final MapDimensionTypeInfo dimType = this.getDimensionType();
        return ((dimType == null) ? "" : (dimType.getName() + " ")) + this.dimId;
    }
    
    public static MapDimensionTypeInfo createDimensionType(final World level) {
        return new MapDimensionTypeInfo(level.field_73011_w.func_186058_p().func_186065_b(), level.field_73011_w.func_191066_m(), level.field_73011_w.func_177497_p()[0], level.field_73011_w.getHeight(), level.field_73011_w.getActualHeight(), level.field_73011_w.func_177495_o(), level.field_73011_w.func_76569_d(), level.field_73011_w.func_186058_p() == DimensionType.THE_END, level.field_73011_w.func_76563_a(6000L, 1.0f), Misc.getDimensionTypeScale(level), level.field_73011_w.getSaveFolder());
    }
}
