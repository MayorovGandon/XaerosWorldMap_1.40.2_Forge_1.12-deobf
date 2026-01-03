//Decompiled by Procyon!

package xaero.map;

import xaero.map.file.worldsave.*;
import xaero.deallocator.*;
import xaero.map.graphics.*;
import xaero.map.cache.*;
import xaero.map.misc.*;
import xaero.map.radar.tracker.synced.*;
import net.minecraft.client.multiplayer.*;
import java.nio.channels.*;
import xaero.map.executor.*;
import xaero.map.pool.*;
import xaero.map.gui.message.*;
import xaero.map.gui.message.render.*;
import xaero.map.minimap.*;
import xaero.map.biome.*;
import java.lang.reflect.*;
import net.minecraft.item.*;
import net.minecraft.client.*;
import xaero.lib.common.reflection.util.*;
import xaero.map.gui.*;
import xaero.lib.client.gui.*;
import net.minecraft.client.gui.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.renderer.*;
import xaero.map.exception.*;
import xaero.map.config.primary.option.*;
import xaero.lib.common.config.option.*;
import xaero.map.region.texture.*;
import xaero.map.highlight.*;
import xaero.lib.client.config.*;
import xaero.lib.common.config.single.*;
import xaero.lib.common.config.*;
import xaero.map.config.util.*;
import xaero.map.common.config.option.*;
import xaero.map.mods.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import xaero.map.controls.*;
import net.minecraft.client.resources.*;
import java.nio.file.attribute.*;
import java.io.*;
import xaero.map.file.*;
import xaero.map.region.*;
import xaero.map.mcworld.*;
import java.util.*;
import java.util.concurrent.*;
import net.minecraft.entity.*;
import xaero.map.world.*;
import xaero.map.task.*;
import java.nio.file.*;
import net.minecraft.util.*;

public class MapProcessor
{
    public static final int ROOT_FOLDER_FORMAT = 4;
    public static final int DEFAULT_LIGHT_LEVELS = 4;
    private MapSaveLoad mapSaveLoad;
    private MapWriter mapWriter;
    private MapLimiter mapLimiter;
    private WorldDataHandler worldDataHandler;
    private ByteBufferDeallocator bufferDeallocator;
    private TextureUploader textureUploader;
    private BranchTextureRenderer branchTextureRenderer;
    private BiomeColorCalculator biomeColorCalculator;
    private BlockStateColorTypeCache blockStateColorTypeCache;
    private final BlockStateShortShapeCache blockStateShortShapeCache;
    private final MapRegionHighlightsPreparer mapRegionHighlightsPreparer;
    private final CaveStartCalculator caveStartCalculator;
    private final ClientSyncedTrackedPlayerManager clientSyncedTrackedPlayerManager;
    private WorldClient world;
    private WorldClient newWorld;
    public final Object mainStuffSync;
    public WorldClient mainWorld;
    public double mainPlayerX;
    public double mainPlayerY;
    public double mainPlayerZ;
    private boolean mainWorldUnloaded;
    private ArrayList<Double[]> footprints;
    private int footprintsTimer;
    private boolean mapWorldUsable;
    private MapWorld mapWorld;
    private String currentWorldId;
    private String currentDimId;
    private String currentMWId;
    private FileLock mapLockToRelease;
    private FileChannel mapLockChannelToClose;
    private FileChannel currentMapLockChannel;
    private FileLock currentMapLock;
    private boolean mapWorldUsableRequest;
    private final Executor renderExecutor;
    public final Object renderThreadPauseSync;
    private int pauseUploading;
    private int pauseRendering;
    private int pauseWriting;
    public final Object processorThreadPauseSync;
    private int pauseProcessing;
    private final Object loadingSync;
    private boolean isLoading;
    public final Object uiSync;
    private boolean waitingForWorldUpdate;
    public final Object uiPauseSync;
    private boolean isUIPaused;
    private ArrayList<LeveledRegion<?>>[] toProcessLevels;
    private ArrayList<MapRegion> toRefresh;
    private static final int SPAWNPOINT_TIMEOUT = 3000;
    private BlockPos spawnToRestore;
    private long mainWorldChangedTime;
    private MapTilePool tilePool;
    private int firstBranchLevel;
    private long lastRenderProcessTime;
    private int workingFramesCount;
    public long freeFramePeriod;
    private int testingFreeFrame;
    private final MessageBox messageBox;
    private final MessageBoxRenderer messageBoxRenderer;
    private MinimapRenderListener minimapRenderListener;
    private boolean currentMapNeedsDeletion;
    private OverlayManager overlayManager;
    private MapBiomes mapBiomes;
    private long renderStartTime;
    private Field scheduledTasksField;
    private Callable<Object> renderStartTimeUpdater;
    private boolean finalizing;
    private int state;
    private final HighlighterRegistry highlighterRegistry;
    private int currentCaveLayer;
    private long lastLocalCaveModeToggle;
    private int nextLocalCaveMode;
    private int localCaveMode;
    private Item mapItem;
    private boolean consideringNetherFairPlayMessage;
    private boolean fairplayMessageReceived;
    private String[] dimensionsToIgnore;
    public Field selectedField;
    
    public MapProcessor(final MapSaveLoad mapSaveLoad, final MapWriter mapWriter, final MapLimiter mapLimiter, final ByteBufferDeallocator bufferDeallocator, final MapTilePool tilePool, final OverlayManager overlayManager, final TextureUploader textureUploader, final WorldDataHandler worldDataHandler, final MapBiomes mapBiomes, final BranchTextureRenderer branchTextureRenderer, final BiomeColorCalculator biomeColorCalculator, final BlockStateColorTypeCache blockStateColorTypeCache, final BlockStateShortShapeCache blockStateShortShapeCache, final HighlighterRegistry highlighterRegistry, final MapRegionHighlightsPreparer mapRegionHighlightsPreparer, final MessageBox messageBox, final MessageBoxRenderer messageBoxRenderer, final CaveStartCalculator caveStartCalculator, final ClientSyncedTrackedPlayerManager clientSyncedTrackedPlayerManager, final Executor renderExecutor) throws NoSuchFieldException {
        this.footprints = new ArrayList<Double[]>();
        this.renderThreadPauseSync = new Object();
        this.processorThreadPauseSync = new Object();
        this.loadingSync = new Object();
        this.uiSync = new Object();
        this.uiPauseSync = new Object();
        this.toRefresh = new ArrayList<MapRegion>();
        this.mainWorldChangedTime = -1L;
        this.lastRenderProcessTime = -1L;
        this.freeFramePeriod = -1L;
        this.testingFreeFrame = 1;
        this.currentCaveLayer = Integer.MAX_VALUE;
        this.nextLocalCaveMode = Integer.MAX_VALUE;
        this.localCaveMode = Integer.MAX_VALUE;
        this.dimensionsToIgnore = new String[] { "FZHammer" };
        this.selectedField = null;
        this.branchTextureRenderer = branchTextureRenderer;
        this.mapSaveLoad = mapSaveLoad;
        this.mapWriter = mapWriter;
        this.mapLimiter = mapLimiter;
        this.bufferDeallocator = bufferDeallocator;
        this.tilePool = tilePool;
        this.overlayManager = overlayManager;
        this.textureUploader = textureUploader;
        this.worldDataHandler = worldDataHandler;
        this.mapBiomes = mapBiomes;
        this.scheduledTasksField = ReflectionUtils.getFieldReflection((Class)Minecraft.class, "scheduledTasks", "", "", "field_152351_aB");
        final Runnable renderStartTimeUpdaterRunnable = new Runnable() {
            @Override
            public void run() {
                MapProcessor.this.updateRenderStartTime();
            }
        };
        this.renderStartTimeUpdater = Executors.callable(renderStartTimeUpdaterRunnable);
        this.mainStuffSync = new Object();
        this.toProcessLevels = (ArrayList<LeveledRegion<?>>[])new ArrayList[4];
        for (int i = 0; i < this.toProcessLevels.length; ++i) {
            this.toProcessLevels[i] = new ArrayList<LeveledRegion<?>>();
        }
        this.biomeColorCalculator = biomeColorCalculator;
        this.blockStateColorTypeCache = blockStateColorTypeCache;
        this.blockStateShortShapeCache = blockStateShortShapeCache;
        this.highlighterRegistry = highlighterRegistry;
        this.mapRegionHighlightsPreparer = mapRegionHighlightsPreparer;
        this.messageBox = messageBox;
        this.messageBoxRenderer = messageBoxRenderer;
        this.caveStartCalculator = caveStartCalculator;
        this.clientSyncedTrackedPlayerManager = clientSyncedTrackedPlayerManager;
        this.renderExecutor = renderExecutor;
        this.minimapRenderListener = new MinimapRenderListener();
        this.updateMapItem();
    }
    
    public void onInit() {
        final String mainId = this.getMainId(4);
        this.fixRootFolder(mainId);
        (this.mapWorld = new MapWorld(mainId, this.getMainId(0), this)).load();
    }
    
    public void run(final MapRunner runner) {
        if (this.state < 2) {
            try {
                while (this.state < 2 && WorldMap.crashHandler.getCrashedBy() == null) {
                    synchronized (this.processorThreadPauseSync) {
                        if (!this.isProcessingPaused()) {
                            this.updateWorld();
                            if (this.world != null) {
                                this.updateFootprints((Minecraft.func_71410_x().field_71462_r instanceof GuiMap) ? 1 : 10);
                            }
                            if (this.mapWorldUsable) {
                                this.mapLimiter.applyLimit(this.mapWorld, this);
                                final long currentTime = System.currentTimeMillis();
                                for (int l = 0; l < this.toProcessLevels.length; ++l) {
                                    final ArrayList<LeveledRegion<?>> regionsToProcess = this.toProcessLevels[l];
                                    for (int i = 0; i < regionsToProcess.size(); ++i) {
                                        final LeveledRegion<?> leveledRegion;
                                        synchronized (regionsToProcess) {
                                            if (i >= regionsToProcess.size()) {
                                                break;
                                            }
                                            leveledRegion = regionsToProcess.get(i);
                                        }
                                        this.mapSaveLoad.updateSave((LeveledRegion)leveledRegion, currentTime, this.currentCaveLayer);
                                    }
                                }
                            }
                            this.mapSaveLoad.run((World)this.world, this.blockStateColorTypeCache);
                            this.handleRefresh();
                            runner.doTasks(this);
                            this.releaseLocksIfNeeded();
                        }
                    }
                    try {
                        Thread.sleep((this.world == null || shouldSkipWorldRender() || this.state > 0) ? 40L : 100L);
                    }
                    catch (InterruptedException ex) {}
                }
            }
            catch (Throwable e) {
                WorldMap.crashHandler.setCrashedBy(e);
            }
            if (this.state < 2) {
                this.forceClean();
            }
        }
        if (this.state == 2) {
            this.state = 3;
        }
    }
    
    public static boolean shouldSkipWorldRender() {
        if (!(Minecraft.func_71410_x().field_71462_r instanceof IScreenBase)) {
            return false;
        }
        final IScreenBase screenBase = (IScreenBase)Minecraft.func_71410_x().field_71462_r;
        return screenBase.shouldSkipWorldRender();
    }
    
    public void onRenderProcess(final Minecraft mc, final ScaledResolution scaledRes) throws RuntimeException {
        try {
            this.renderExecutor.drainTasks();
            this.mapWriter.onRender(this.biomeColorCalculator, this.overlayManager);
            final long renderProcessTime = System.nanoTime();
            if (this.testingFreeFrame == 1) {
                this.testingFreeFrame = 2;
            }
            else {
                synchronized (this.renderThreadPauseSync) {
                    if (this.lastRenderProcessTime == -1L) {
                        this.lastRenderProcessTime = renderProcessTime;
                    }
                    final long sinceLastProcessTime = renderProcessTime - this.lastRenderProcessTime;
                    if (this.testingFreeFrame == 2) {
                        this.freeFramePeriod = sinceLastProcessTime;
                        this.testingFreeFrame = 0;
                    }
                    if (this.pauseUploading == 0 && this.mapWorldUsable && this.currentWorldId != null) {
                        while (GL11.glGetError() != 0) {}
                        GlStateManager.func_179082_a(0.0f, 0.0f, 0.0f, 0.0f);
                        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
                        GlStateManager.func_187425_g(3317, 4);
                        GlStateManager.func_187425_g(3316, 0);
                        GlStateManager.func_187425_g(3315, 0);
                        GlStateManager.func_187425_g(3314, 0);
                        GlStateManager.func_179131_c(1.0f, 1.0f, 1.0f, 1.0f);
                        OpenGLException.checkGLError();
                        this.updateCaveStart();
                        final MapDimension currentDim = this.mapWorld.getCurrentDimension();
                        if (currentDim.getFullReloader() != null) {
                            currentDim.getFullReloader().onRenderProcess();
                        }
                        final DimensionHighlighterHandler highlighterHandler = currentDim.getHighlightHandler();
                        final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
                        final SingleConfigManager<Config> primaryConfigManager = (SingleConfigManager<Config>)configManager.getPrimaryConfigManager();
                        final int globalRegionCacheHashCode = WorldMap.settings.getRegionCacheHashCode();
                        final boolean debugConfig = (boolean)primaryConfigManager.getEffective((ConfigOption)WorldMapPrimaryClientConfigOptions.DEBUG);
                        final boolean detailedDebug = WorldMap.detailed_debug;
                        final MapUpdateFastConfig updateConfig = new MapUpdateFastConfig();
                        final long uploadStart = System.nanoTime();
                        final long totalTime = Math.min(sinceLastProcessTime, this.freeFramePeriod);
                        final long passed = uploadStart - this.renderStartTime;
                        final long timeAvailable = Math.max(3000000L, totalTime - passed);
                        final long uploadUntil = uploadStart + timeAvailable / 4L;
                        long gpuLimit = Math.max(1000000L, (Minecraft.func_71410_x().field_71462_r instanceof GuiMap) ? (totalTime * 5L / 12L) : Math.min(totalTime / 5L, timeAvailable));
                        boolean noLimits = false;
                        if (Minecraft.func_71410_x().field_71462_r instanceof GuiMap) {
                            final GuiMap guiMap = (GuiMap)Minecraft.func_71410_x().field_71462_r;
                            noLimits = guiMap.noUploadingLimits;
                            guiMap.noUploadingLimits = false;
                        }
                        int firstLevel = 0;
                        final boolean branchesCatchup = (int)(Math.random() * 5.0) == 0;
                        if (branchesCatchup) {
                            firstLevel = 1 + this.firstBranchLevel;
                        }
                        this.firstBranchLevel = (this.firstBranchLevel + 1) % (this.toProcessLevels.length - 1);
                        for (int j = 0; j < this.toProcessLevels.length; ++j) {
                            final int level = (firstLevel + j) % this.toProcessLevels.length;
                            final ArrayList<LeveledRegion<?>> toProcess = this.toProcessLevels[level];
                            for (int i = 0; i < toProcess.size(); ++i) {
                                final LeveledRegion<? extends RegionTexture<?>> region;
                                synchronized (toProcess) {
                                    if (i >= toProcess.size()) {
                                        break;
                                    }
                                    region = toProcess.get(i);
                                }
                                if (region != null) {
                                    synchronized (region) {
                                        if (region.shouldBeProcessed()) {
                                            final boolean cleanAndCacheRequestsBlocked = region.cleanAndCacheRequestsBlocked();
                                            boolean allCleaned = true;
                                            boolean allCached = true;
                                            boolean allUploaded = true;
                                            boolean hasLoadedTextures = false;
                                            for (int x = 0; x < 8; ++x) {
                                                for (int z = 0; z < 8; ++z) {
                                                    final RegionTexture texture = (RegionTexture)region.getTexture(x, z);
                                                    if (texture != null) {
                                                        if (texture.canUpload()) {
                                                            hasLoadedTextures = true;
                                                            if (noLimits || (gpuLimit > 0L && System.nanoTime() < uploadUntil)) {
                                                                texture.preUpload(this, this.biomeColorCalculator, this.overlayManager, region, detailedDebug, this.blockStateShortShapeCache, updateConfig);
                                                                if (texture.shouldUpload()) {
                                                                    if (texture.getTimer() == 0) {
                                                                        gpuLimit -= texture.uploadBuffer(highlighterHandler, this.textureUploader, region, this.branchTextureRenderer, x, z, scaledRes);
                                                                    }
                                                                    else {
                                                                        texture.decTimer();
                                                                    }
                                                                }
                                                            }
                                                            texture.postUpload(this, region, cleanAndCacheRequestsBlocked);
                                                        }
                                                        if (texture.hasSourceData()) {
                                                            allCleaned = false;
                                                        }
                                                        if (texture.shouldIncludeInCache() && !texture.isCachePrepared()) {
                                                            allCached = false;
                                                        }
                                                        if (!texture.isUploaded()) {
                                                            allUploaded = false;
                                                        }
                                                    }
                                                }
                                            }
                                            if (hasLoadedTextures) {
                                                region.processWhenLoadedChunksExist(globalRegionCacheHashCode);
                                            }
                                            allUploaded = (allUploaded && region.isLoaded() && !cleanAndCacheRequestsBlocked);
                                            allCached = (allCached && allUploaded);
                                            if ((!region.shouldCache() || !region.recacheHasBeenRequested()) && region.shouldEndProcessingAfterUpload() && allCleaned && allUploaded) {
                                                region.onProcessingEnd();
                                                region.deleteGLBuffers();
                                                synchronized (toProcess) {
                                                    if (i < toProcess.size()) {
                                                        toProcess.remove(i);
                                                        --i;
                                                    }
                                                }
                                                if (debugConfig) {
                                                    WorldMap.LOGGER.info("Region freed: " + region + " " + this.mapWriter.getUpdateCounter() + " " + this.currentWorldId + " " + this.currentDimId);
                                                }
                                            }
                                            if (allCached && !region.isAllCachePrepared()) {
                                                region.setAllCachePrepared(true);
                                            }
                                            if (region.shouldCache() && region.recacheHasBeenRequested() && region.isAllCachePrepared() && !cleanAndCacheRequestsBlocked) {
                                                this.getMapSaveLoad().requestCache((LeveledRegion)region);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        ++this.workingFramesCount;
                        if (this.workingFramesCount >= 30) {
                            this.testingFreeFrame = 1;
                            this.workingFramesCount = 0;
                        }
                        this.textureUploader.uploadTextures();
                    }
                }
            }
            this.mapLimiter.updateAvailableVRAM();
            this.lastRenderProcessTime = renderProcessTime;
        }
        catch (Throwable e) {
            WorldMap.crashHandler.setCrashedBy(e);
        }
        WorldMap.crashHandler.checkForCrashes();
    }
    
    public void updateCaveStart() {
        final Minecraft mc = Minecraft.func_71410_x();
        final MapDimension dimension = this.mapWorld.getCurrentDimension();
        final boolean caveModeAllowed = WorldMapClientConfigUtils.getEffectiveCaveModeAllowed();
        int newCaveStart;
        if (!caveModeAllowed || dimension.getCaveModeType() == 0) {
            newCaveStart = Integer.MAX_VALUE;
        }
        else {
            final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
            final SingleConfigManager<Config> primaryConfigManager = (SingleConfigManager<Config>)configManager.getPrimaryConfigManager();
            final int caveModeStartConfig = (int)primaryConfigManager.getEffective(WorldMapPrimaryClientConfigOptions.CAVE_MODE_START);
            if (caveModeStartConfig == Integer.MAX_VALUE) {
                newCaveStart = Integer.MIN_VALUE;
            }
            else {
                newCaveStart = caveModeStartConfig;
            }
            final boolean customDim = dimension.getDimId() != mc.field_71441_e.field_73011_w.getDimension();
            final boolean isMapScreen = mc.field_71462_r instanceof GuiMap || shouldSkipWorldRender();
            final int autoCaveModeConfig = (int)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.AUTO_CAVE_MODE);
            final double caveModeToggleTimerConfig = (double)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.CAVE_MODE_TOGGLE_TIMER);
            if (SupportMods.minimap() && ((!customDim && autoCaveModeConfig < 0 && newCaveStart == Integer.MIN_VALUE) || !isMapScreen)) {
                newCaveStart = SupportMods.xaeroMinimap.getCaveStart(newCaveStart, isMapScreen);
            }
            if (newCaveStart == Integer.MIN_VALUE) {
                final long currentTime = System.currentTimeMillis();
                final int nextLocalCaveMode = customDim ? Integer.MAX_VALUE : this.caveStartCalculator.getCaving(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v, (World)mc.field_71441_e);
                final boolean toggling = this.localCaveMode == Integer.MAX_VALUE != (nextLocalCaveMode == Integer.MAX_VALUE);
                if (!toggling || currentTime - this.lastLocalCaveModeToggle > (long)(caveModeToggleTimerConfig * 1000.0)) {
                    if (toggling) {
                        this.lastLocalCaveModeToggle = currentTime;
                    }
                    this.localCaveMode = nextLocalCaveMode;
                }
                newCaveStart = this.localCaveMode;
            }
            if (newCaveStart != Integer.MAX_VALUE) {
                if (dimension.getCaveModeType() == 2) {
                    newCaveStart = Integer.MIN_VALUE;
                }
                else {
                    newCaveStart = MathHelper.func_76125_a(newCaveStart, 0, this.world.func_72800_K() - 1);
                }
            }
        }
        final int newCaveLayer = this.getCaveLayer(newCaveStart);
        dimension.getLayeredMapRegions().getLayer(newCaveLayer).setCaveStart(newCaveStart);
        this.currentCaveLayer = newCaveLayer;
    }
    
    public boolean ignoreWorld(final World world) {
        for (int i = 0; i < this.dimensionsToIgnore.length; ++i) {
            if (this.dimensionsToIgnore[i].equals(world.field_73011_w.func_186058_p().func_186065_b())) {
                return true;
            }
        }
        return false;
    }
    
    public String getDimensionName(final int id) {
        String name = "null";
        if (id != 0) {
            name = "DIM" + id;
        }
        return name;
    }
    
    public String getDimensionLegacyName(final WorldProvider worldProvider) {
        String legacyName = worldProvider.getSaveFolder();
        if (legacyName != null) {
            legacyName = legacyName.replaceAll("_", "^us^");
        }
        return legacyName;
    }
    
    public Integer getDimensionIdForFolder(final String folderName) {
        if (folderName.equals("null")) {
            return 0;
        }
        if (folderName.equals("DIM-1")) {
            return -1;
        }
        if (folderName.equals("DIM1")) {
            return 1;
        }
        if (folderName.length() < 4 || !folderName.startsWith("DIM")) {
            return null;
        }
        final String idString = folderName.substring(3);
        try {
            return Integer.parseInt(idString);
        }
        catch (NumberFormatException nfe) {
            return null;
        }
    }
    
    public void waitForLoadingToFinish(final Runnable onFinish) {
        while (true) {
            synchronized (this.loadingSync) {
                if (!this.isLoading) {
                    onFinish.run();
                    break;
                }
                this.renderExecutor.drainTasks();
                this.worldDataHandler.handleRenderExecutor();
            }
        }
    }
    
    public synchronized void changeWorld(final WorldClient world) {
        this.pushWriterPause();
        this.waitForLoadingToFinish(new Runnable() {
            @Override
            public void run() {
                MapProcessor.this.waitingForWorldUpdate = true;
            }
        });
        this.newWorld = world;
        if (world == null) {
            this.mapWorldUsableRequest = false;
        }
        else {
            this.mapWorldUsableRequest = true;
            final int dimId = this.mapWorld.getPotentialDimId();
            this.mapWorld.setFutureDimensionId(dimId);
            this.updateDimension(world, dimId);
            this.mapWorld.getFutureDimension().resetCustomMultiworldUnsynced();
        }
        this.popWriterPause();
    }
    
    public void updateVisitedDimension(final WorldClient world) {
        this.updateDimension(world, world.field_73011_w.getDimension());
    }
    
    public synchronized void updateDimension(final WorldClient world, final int dimId) {
        if (world == null) {
            return;
        }
        final Object autoIdBase = this.getAutoIdBase(world);
        MapDimension mapDimension = this.mapWorld.getDimension(dimId);
        if (mapDimension == null) {
            mapDimension = this.mapWorld.createDimensionUnsynced((World)world, dimId);
        }
        mapDimension.updateFutureAutomaticUnsynced(Minecraft.func_71410_x(), autoIdBase);
    }
    
    @Deprecated
    private String getMainId(final boolean rootFolderFormat, final boolean preIP6Fix) {
        if (!rootFolderFormat) {
            return this.getMainId(0);
        }
        return this.getMainId(preIP6Fix ? 1 : 2);
    }
    
    private String getMainId(final int version) {
        final Minecraft mc = Minecraft.func_71410_x();
        String result = null;
        if (mc.func_71401_C() != null) {
            result = MapWorld.convertWorldFolderToRootId(version, mc.func_71401_C().func_71270_I());
        }
        else if (mc.func_181540_al() && WorldMap.events.getLatestRealm() != null) {
            result = "Realms_" + WorldMap.events.getLatestRealm().ownerUUID + "." + WorldMap.events.getLatestRealm().id;
        }
        else if (mc.func_147104_D() != null) {
            final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
            final SingleConfigManager<Config> primaryConfigManager = (SingleConfigManager<Config>)configManager.getPrimaryConfigManager();
            final boolean differentiateByServerAddress = (boolean)primaryConfigManager.getEffective((ConfigOption)WorldMapPrimaryClientConfigOptions.DIFFERENTIATE_BY_SERVER_ADDRESS);
            String serverIP = differentiateByServerAddress ? mc.func_147104_D().field_78845_b : "Any Address";
            int portDivider;
            if (version >= 2 && serverIP.indexOf(":") != serverIP.lastIndexOf(":")) {
                portDivider = serverIP.lastIndexOf("]:") + 1;
            }
            else {
                portDivider = serverIP.indexOf(":");
            }
            if (portDivider > 0) {
                serverIP = serverIP.substring(0, portDivider);
            }
            while (version >= 1 && serverIP.endsWith(".")) {
                serverIP = serverIP.substring(0, serverIP.length() - 1);
            }
            if (version >= 3) {
                serverIP = serverIP.replace("[", "").replace("]", "");
            }
            result = "Multiplayer_" + serverIP.replaceAll(":", (version < 4) ? "§" : ".");
        }
        else {
            result = "Multiplayer_Unknown";
        }
        return result;
    }
    
    public synchronized void toggleMultiworldType(final MapDimension dim) {
        if (this.mapWorldUsable && !this.waitingForWorldUpdate && this.mapWorld.isMultiplayer() && this.mapWorld.getCurrentDimension() == dim) {
            this.mapWorld.toggleMultiworldTypeUnsynced();
        }
    }
    
    public synchronized void quickConfirmMultiworld() {
        if (this.canQuickConfirmUnsynced() && this.mapWorld.getCurrentDimension().hasConfirmedMultiworld()) {
            this.confirmMultiworld(this.mapWorld.getCurrentDimension());
        }
    }
    
    public synchronized boolean confirmMultiworld(final MapDimension dim) {
        if (this.mapWorldUsable && this.mainWorld != null && Objects.equals(this.mapWorld.getPotentialDimId(), this.mapWorld.getCurrentDimensionId()) && this.mapWorld.getCurrentDimension() == dim) {
            this.mapWorld.confirmMultiworldTypeUnsynced();
            this.mapWorld.getCurrentDimension().confirmMultiworldUnsynced();
            return true;
        }
        return false;
    }
    
    public synchronized void setMultiworld(final MapDimension dimToCompare, final String customMW) {
        if (this.mapWorldUsable && dimToCompare.getMapWorld() == this.mapWorld) {
            dimToCompare.setMultiworldUnsynced(customMW);
        }
    }
    
    public boolean canQuickConfirmUnsynced() {
        return this.mapWorldUsable && !this.mapWorld.getCurrentDimension().futureMultiworldWritable && Objects.equals(this.mapWorld.getPotentialDimId(), this.mapWorld.getCurrentDimensionId());
    }
    
    public String getCrosshairMessage() {
        synchronized (this.uiPauseSync) {
            if (this.isUIPaused) {
                return null;
            }
            if (this.canQuickConfirmUnsynced()) {
                final String selectedMWName = this.mapWorld.getCurrentDimension().getMultiworldName(this.mapWorld.getCurrentDimension().getFutureMultiworldUnsynced());
                String message = "§2(" + ControlsRegister.keyOpenMap.getDisplayName().toUpperCase() + ")§r " + I18n.func_135052_a("gui.xaero_map_unconfirmed", new Object[0]);
                if (this.mapWorld.getCurrentDimension().hasConfirmedMultiworld()) {
                    message = message + " §2" + ControlsRegister.keyQuickConfirm.getDisplayName().toUpperCase() + "§r for map \"" + I18n.func_135052_a(selectedMWName, new Object[0]) + "\"";
                }
                return message;
            }
        }
        return null;
    }
    
    public synchronized void checkForWorldUpdate() {
        if (this.mainWorld != null) {
            final Object autoIdBase = this.getAutoIdBase(this.mainWorld);
            if (autoIdBase != null) {
                final boolean baseChanged = !autoIdBase.equals(this.getUsedAutoIdBase(this.mainWorld));
                final int potentialDimId = this.mapWorld.getPotentialDimId();
                if (baseChanged && this.mapWorldUsableRequest) {
                    final MapDimension mapDimension = this.mapWorld.getDimension(potentialDimId);
                    if (mapDimension != null) {
                        final boolean serverBasedBefore = mapDimension.isFutureMultiworldServerBased();
                        mapDimension.updateFutureAutomaticUnsynced(Minecraft.func_71410_x(), autoIdBase);
                        if (serverBasedBefore != mapDimension.isFutureMultiworldServerBased()) {
                            mapDimension.resetCustomMultiworldUnsynced();
                        }
                    }
                }
                if (this.mainWorld != this.world || potentialDimId != this.mapWorld.getFutureDimensionId()) {
                    this.changeWorld(this.mainWorld);
                }
                final Object updatedAutoIdBase = this.getAutoIdBase(this.mainWorld);
                if (updatedAutoIdBase != null) {
                    this.setUsedAutoIdBase(this.mainWorld, updatedAutoIdBase);
                }
                else {
                    this.removeUsedAutoIdBase(this.mainWorld);
                }
                if (potentialDimId != this.mainWorld.field_73011_w.getDimension()) {
                    this.updateVisitedDimension(this.mainWorld);
                }
            }
        }
    }
    
    private void updateWorld() throws IOException {
        this.pushUIPause();
        this.updateWorldSynced();
        this.popUIPause();
        if (this.mapWorldUsable && !this.mapSaveLoad.isRegionDetectionComplete()) {
            this.mapSaveLoad.detectRegions(10);
            this.mapSaveLoad.setRegionDetectionComplete(true);
        }
    }
    
    private synchronized void updateWorldSynced() throws IOException {
        synchronized (this.uiSync) {
            if (this.mapWorldUsable != this.mapWorldUsableRequest || (this.mapWorldUsableRequest && (this.mapWorld.getFutureDimension() != this.mapWorld.getCurrentDimension() || !this.mapWorld.getFutureDimension().getFutureMultiworldUnsynced().equals(this.mapWorld.getFutureDimension().getCurrentMultiworld())))) {
                final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
                final SingleConfigManager<Config> primaryConfigManager = (SingleConfigManager<Config>)configManager.getPrimaryConfigManager();
                final boolean debugConfig = (boolean)primaryConfigManager.getEffective((ConfigOption)WorldMapPrimaryClientConfigOptions.DEBUG);
                final String newMWId = this.mapWorldUsableRequest ? this.mapWorld.getFutureMultiworldUnsynced() : null;
                this.pushRenderPause(true, true);
                this.pushWriterPause();
                final String newWorldId = this.mapWorldUsableRequest ? this.mapWorld.getMainId() : null;
                final boolean shouldClearAllDimensions = this.state == 1;
                final boolean shouldClearNewDimension = this.mapWorldUsableRequest && !this.mapWorld.getFutureMultiworldUnsynced().equals(this.mapWorld.getFutureDimension().getCurrentMultiworld());
                this.mapSaveLoad.getToSave().clear();
                if (this.currentMapLock != null) {
                    this.mapLockToRelease = this.currentMapLock;
                    this.mapLockChannelToClose = this.currentMapLockChannel;
                    this.currentMapLock = null;
                    this.currentMapLockChannel = null;
                }
                this.releaseLocksIfNeeded();
                if (this.mapWorld.getCurrentDimensionId() != null) {
                    final MapDimension currentDim = this.mapWorld.getCurrentDimension();
                    final MapDimension reqDim = this.mapWorldUsableRequest ? this.mapWorld.getFutureDimension() : null;
                    final boolean shouldFinishCurrentDim = this.mapWorldUsable && !this.currentMapNeedsDeletion;
                    boolean currentDimChecked = false;
                    if (shouldFinishCurrentDim) {
                        this.mapSaveLoad.saveAll = true;
                    }
                    if (shouldFinishCurrentDim || (shouldClearNewDimension && reqDim == currentDim)) {
                        for (final LeveledRegion<?> region : currentDim.getLayeredMapRegions().getUnsyncedSet()) {
                            if (shouldFinishCurrentDim) {
                                if (region.getLevel() == 0) {
                                    final MapRegion leafRegion = (MapRegion)region;
                                    if (!leafRegion.isNormalMapData() && !leafRegion.hasLookedForCache() && leafRegion.isOutdatedWithOtherLayers()) {
                                        final File potentialCacheFile = this.mapSaveLoad.getCacheFile((MapRegionInfo)leafRegion, leafRegion.getCaveLayer(), false, false);
                                        if (potentialCacheFile.exists()) {
                                            leafRegion.setCacheFile(potentialCacheFile);
                                            leafRegion.setLookedForCache(true);
                                        }
                                    }
                                    if (leafRegion.shouldConvertCacheToOutdatedOnFinishDim() && leafRegion.getCacheFile() != null) {
                                        leafRegion.convertCacheToOutdated(this.mapSaveLoad, "might be outdated");
                                        if (debugConfig) {
                                            WorldMap.LOGGER.info(String.format("Converting cache for region %s because it might be outdated.", leafRegion));
                                        }
                                    }
                                }
                                region.setReloadHasBeenRequested(false, "world/dim change");
                                region.onCurrentDimFinish(this.mapSaveLoad, this);
                            }
                            if (shouldClearAllDimensions || (shouldClearNewDimension && reqDim == currentDim)) {
                                region.onDimensionClear(this);
                            }
                        }
                        currentDimChecked = true;
                    }
                    if (reqDim != currentDim && shouldClearNewDimension) {
                        for (final LeveledRegion<?> region : reqDim.getLayeredMapRegions().getUnsyncedSet()) {
                            region.onDimensionClear(this);
                        }
                    }
                    if (shouldClearAllDimensions) {
                        for (final MapDimension dim : this.mapWorld.getDimensionsList()) {
                            if (!currentDimChecked || dim != currentDim) {
                                for (final LeveledRegion<?> region2 : dim.getLayeredMapRegions().getUnsyncedSet()) {
                                    region2.onDimensionClear(this);
                                }
                            }
                        }
                    }
                    if (this.currentMapNeedsDeletion) {
                        this.mapWorld.getCurrentDimension().deleteMultiworldMapDataUnsynced(this.mapWorld.getCurrentDimension().getCurrentMultiworld());
                    }
                }
                this.currentMapNeedsDeletion = false;
                if (shouldClearAllDimensions) {
                    if (this.mapWorld.getCurrentDimensionId() != null) {
                        for (final MapDimension dim2 : this.mapWorld.getDimensionsList()) {
                            dim2.clear();
                        }
                    }
                    if (debugConfig) {
                        WorldMap.LOGGER.info("All map data cleared!");
                    }
                    if (this.state == 1) {
                        WorldMap.LOGGER.info("World map cleaned normally!");
                        this.state = 2;
                    }
                }
                else if (shouldClearNewDimension) {
                    this.mapWorld.getFutureDimension().clear();
                    if (debugConfig) {
                        WorldMap.LOGGER.info("Dimension map data cleared!");
                    }
                }
                if (debugConfig) {
                    WorldMap.LOGGER.info("World changed!");
                }
                this.mapWorldUsable = this.mapWorldUsableRequest;
                if (this.mapWorldUsableRequest) {
                    this.mapWorld.switchToFutureUnsynced();
                }
                this.currentWorldId = newWorldId;
                this.currentDimId = (this.mapWorldUsableRequest ? this.getDimensionName(this.mapWorld.getFutureDimensionId()) : null);
                this.currentMWId = newMWId;
                final Path mapPath = this.mapSaveLoad.getMWSubFolder(this.currentWorldId, this.currentDimId, this.currentMWId);
                if (this.mapWorldUsable) {
                    Files.createDirectories(mapPath, (FileAttribute<?>[])new FileAttribute[0]);
                    final Path mapLockPath = mapPath.resolve(".lock");
                    final int totalLockAttempts = 10;
                    int lockAttempts = 10;
                    while (lockAttempts-- > 0) {
                        if (lockAttempts < 9) {
                            WorldMap.LOGGER.info("Failed attempt to lock the current world map! Retrying in 50 ms... " + lockAttempts);
                            try {
                                Thread.sleep(50L);
                            }
                            catch (InterruptedException ex) {}
                        }
                        try {
                            final FileChannel lockChannel = FileChannel.open(mapLockPath, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
                            this.currentMapLock = lockChannel.tryLock();
                            if (this.currentMapLock == null) {
                                continue;
                            }
                            this.currentMapLockChannel = lockChannel;
                        }
                        catch (Exception e) {
                            WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
                            continue;
                        }
                        break;
                    }
                }
                this.checkFootstepsReset((World)this.world, (World)this.newWorld);
                this.mapSaveLoad.clearToLoad();
                this.mapSaveLoad.setNextToLoadByViewing((LeveledRegion)null);
                this.clearToRefresh();
                for (int i = 0; i < this.toProcessLevels.length; ++i) {
                    this.toProcessLevels[i].clear();
                }
                if (this.mapWorldUsable && !this.isCurrentMapLocked()) {
                    for (final LeveledRegion<?> region3 : this.mapWorld.getCurrentDimension().getLayeredMapRegions().getUnsyncedSet()) {
                        if (region3.shouldBeProcessed()) {
                            this.addToProcess(region3);
                        }
                    }
                }
                this.mapWriter.resetPosition();
                this.world = this.newWorld;
                if (SupportMods.framedBlocks()) {
                    SupportMods.supportFramedBlocks.onWorldChange();
                }
                this.mapWorld.onWorldChangeUnsynced(this.world);
                if (debugConfig) {
                    WorldMap.LOGGER.info("World/dimension changed to: " + this.currentWorldId + " " + this.currentDimId + " " + this.currentMWId);
                }
                this.worldDataHandler.prepareSingleplayer((World)this.world, this);
                if (this.worldDataHandler.getWorldDir() == null && this.currentWorldId != null && this.mapWorld.getCurrentDimension().isUsingWorldSave()) {
                    final String s = null;
                    this.currentDimId = s;
                    this.currentWorldId = s;
                }
                final boolean shouldDetect = this.mapWorldUsable && !this.mapWorld.getCurrentDimension().hasDoneRegionDetection();
                this.mapSaveLoad.setRegionDetectionComplete(!shouldDetect);
                this.popRenderPause(true, true);
                this.popWriterPause();
            }
            else if (this.newWorld != this.world) {
                this.pushRenderPause(false, true);
                this.pushWriterPause();
                this.checkFootstepsReset((World)this.world, (World)this.newWorld);
                this.world = this.newWorld;
                if (SupportMods.framedBlocks()) {
                    SupportMods.supportFramedBlocks.onWorldChange();
                }
                this.mapWorld.onWorldChangeUnsynced(this.world);
                this.popRenderPause(false, true);
                this.popWriterPause();
            }
            if (this.mapWorldUsable) {
                this.mapWorld.getCurrentDimension().switchToFutureMultiworldWritableValueUnsynced();
                this.mapWorld.switchToFutureMultiworldTypeUnsynced();
            }
            this.waitingForWorldUpdate = false;
        }
    }
    
    public void updateFootprints(final int step) {
        final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        if (configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.FOOTSTEPS)) {
            if (this.footprintsTimer > 0) {
                this.footprintsTimer -= step;
            }
            else {
                final Double[] coords = { this.mainPlayerX, this.mainPlayerZ };
                synchronized (this.footprints) {
                    this.footprints.add(coords);
                    if (this.footprints.size() > 32) {
                        this.footprints.remove(0);
                    }
                }
                this.footprintsTimer = 20;
            }
        }
    }
    
    public void addToRefresh(final MapRegion region, final boolean prepareHighlights) {
        synchronized (this.toRefresh) {
            if (!this.toRefresh.contains(region)) {
                this.toRefresh.add(0, region);
            }
        }
        if (prepareHighlights) {
            this.mapRegionHighlightsPreparer.prepare(region, false);
        }
    }
    
    public void removeToRefresh(final MapRegion region) {
        synchronized (this.toRefresh) {
            this.toRefresh.remove(region);
        }
    }
    
    private void clearToRefresh() {
        synchronized (this.toRefresh) {
            this.toRefresh.clear();
        }
    }
    
    private void handleRefresh() throws RuntimeException {
        this.pushIsLoading();
        if (!this.waitingForWorldUpdate && !this.toRefresh.isEmpty()) {
            final MapRegion region = this.toRefresh.get(0);
            if (!region.isRefreshing()) {
                throw new RuntimeException(String.format("Trying to refresh region %s, which is not marked as being refreshed!", region));
            }
            final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
            final SingleConfigManager<Config> primaryConfigManager = (SingleConfigManager<Config>)configManager.getPrimaryConfigManager();
            final boolean debugConfig = (boolean)primaryConfigManager.getEffective((ConfigOption)WorldMapPrimaryClientConfigOptions.DEBUG);
            final int globalVersion = (int)primaryConfigManager.getEffective(WorldMapPrimaryClientConfigOptions.GLOBAL_VERSION);
            final int globalReloadVersion = (int)primaryConfigManager.getEffective(WorldMapPrimaryClientConfigOptions.RELOAD_VIEWED_VERSION);
            final int globalCacheHashCode = WorldMap.settings.getRegionCacheHashCode();
            final boolean regionLoaded;
            synchronized (region) {
                regionLoaded = (region.getLoadState() == 2);
                if (regionLoaded) {
                    region.setRecacheHasBeenRequested(true, "refresh handle");
                    region.setShouldCache(true, "refresh handle");
                    region.setVersion(globalVersion);
                    region.setCacheHashCode(globalCacheHashCode);
                    region.setReloadVersion(globalReloadVersion);
                    region.setHighlightsHash(region.getTargetHighlightsHash());
                }
            }
            boolean isEmpty = true;
            if (regionLoaded) {
                synchronized (region) {
                    region.setAllCachePrepared(false);
                }
                boolean skipRegularRefresh = false;
                final int upToDateCaveStart = region.getUpToDateCaveStart();
                final int caveModeDepth = (int)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.CAVE_MODE_DEPTH);
                if (region.isBeingWritten() && region.caveStartOutdated(upToDateCaveStart, caveModeDepth)) {
                    try {
                        this.getWorldDataHandler().buildRegion((World)this.world, region, false, (int[])null);
                        skipRegularRefresh = true;
                    }
                    catch (Throwable e) {
                        WorldMap.LOGGER.info("Region failed to refresh from world save: " + region + " " + region.getWorldId() + " " + region.getDimId() + " " + region.getMwId());
                    }
                }
                for (int i = 0; i < 8; ++i) {
                    for (int j = 0; j < 8; ++j) {
                        final MapTileChunk chunk = region.getChunk(i, j);
                        if (chunk != null) {
                            if (chunk.hasHadTerrain()) {
                                if (!skipRegularRefresh && chunk.getLoadState() == 2) {
                                    for (int tileX = 0; tileX < 4; ++tileX) {
                                        for (int tileZ = 0; tileZ < 4; ++tileZ) {
                                            region.pushWriterPause();
                                            final MapTile tile = chunk.getTile(tileX, tileZ);
                                            if (tile != null && tile.isLoaded()) {
                                                for (int o = 0; o < 16; ++o) {
                                                    final MapBlock[] column = tile.getBlockColumn(o);
                                                    for (int p = 0; p < 16; ++p) {
                                                        column[p].setSlopeUnknown(true);
                                                    }
                                                }
                                            }
                                            chunk.setTile(tileX, tileZ, tile, this.blockStateShortShapeCache);
                                            region.popWriterPause();
                                        }
                                    }
                                    chunk.setToUpdateBuffers(true);
                                }
                            }
                            else {
                                region.pushWriterPause();
                                if (!chunk.hasHadTerrain() && !chunk.wasChanged() && !chunk.getToUpdateBuffers()) {
                                    chunk.getLeafTexture().resetBiomes();
                                    if (chunk.hasHighlightsIfUndiscovered()) {
                                        chunk.getLeafTexture().requestHighlightOnlyUpload();
                                    }
                                    else {
                                        region.setChunk(i, j, null);
                                        chunk.getLeafTexture().deleteTexturesAndBuffers();
                                    }
                                }
                                region.popWriterPause();
                            }
                            isEmpty = false;
                        }
                    }
                }
                if (debugConfig) {
                    WorldMap.LOGGER.info("Region refreshed: " + region + " " + region + " " + this.mapWriter.getUpdateCounter());
                }
            }
            synchronized (region) {
                region.setRefreshing(false);
                if (isEmpty) {
                    region.setShouldCache(false, "refresh handle");
                    region.setRecacheHasBeenRequested(false, "refresh handle");
                }
            }
            if (region.isResaving()) {
                region.setLastSaveTime(-60000L);
            }
            this.removeToRefresh(region);
        }
        this.popIsLoading();
    }
    
    @Deprecated
    public boolean regionExists(final int x, final int z) {
        return this.regionExists(Integer.MAX_VALUE, x, z);
    }
    
    public boolean regionExists(final int caveLayer, final int x, final int z) {
        return this.regionDetectionExists(caveLayer, x, z) || this.mapWorld.getCurrentDimension().getHighlightHandler().shouldApplyRegionHighlights(x, z, false);
    }
    
    public boolean regionDetectionExists(final int caveLayer, final int x, final int z) {
        return this.mapSaveLoad.isRegionDetectionComplete() && this.mapWorld.getCurrentDimension().getLayeredMapRegions().getLayer(caveLayer).regionDetectionExists(x, z);
    }
    
    public void removeMapRegion(final LeveledRegion<?> region) {
        final MapDimension regionDim = region.getDim();
        final LayeredRegionManager regions = regionDim.getLayeredMapRegions();
        if (region.getLevel() == 0) {
            regions.remove(region.getCaveLayer(), region.getRegionX(), region.getRegionZ(), region.getLevel());
            regions.removeListRegion(region);
        }
        regions.removeLoadedRegion(region);
        this.removeToProcess(region);
    }
    
    public LeveledRegion<?> getLeveledRegion(final int caveLayer, final int leveledRegX, final int leveledRegZ, final int level) {
        final MapDimension mapDimension = this.mapWorld.getCurrentDimension();
        final LayeredRegionManager regions = mapDimension.getLayeredMapRegions();
        return regions.get(caveLayer, leveledRegX, leveledRegZ, level);
    }
    
    public void initMinimapRender(final int flooredMapCameraX, final int flooredMapCameraZ) {
        this.minimapRenderListener.init(this, flooredMapCameraX, flooredMapCameraZ);
    }
    
    public void beforeMinimapRegionRender(final MapRegion region) {
        this.minimapRenderListener.beforeMinimapRender(region);
    }
    
    public void finalizeMinimapRender() {
        this.minimapRenderListener.finalize(this);
    }
    
    @Deprecated
    public MapRegion getMapRegion(final int regX, final int regZ, final boolean create) {
        return this.getMapRegion(Integer.MAX_VALUE, regX, regZ, create);
    }
    
    @Deprecated
    public MapRegion getMapRegion(final int caveLayer, final int regX, final int regZ, final boolean create) {
        if (this.mapWorld.isUsingCustomDimension()) {
            return null;
        }
        return this.getLeafMapRegion(caveLayer, regX, regZ, create);
    }
    
    public MapRegion getLeafMapRegion(final int caveLayer, final int regX, final int regZ, final boolean create) {
        if (!this.mapSaveLoad.isRegionDetectionComplete()) {
            return null;
        }
        final MapDimension mapDimension = this.mapWorld.getCurrentDimension();
        final LayeredRegionManager regions = mapDimension.getLayeredMapRegions();
        MapRegion region = regions.getLeaf(caveLayer, regX, regZ);
        if (region == null) {
            if (!create) {
                return null;
            }
            if (!Minecraft.func_71410_x().func_152345_ab()) {
                throw new IllegalAccessError();
            }
            final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
            final SingleConfigManager<Config> primaryConfigManager = (SingleConfigManager<Config>)configManager.getPrimaryConfigManager();
            final int globalVersion = (int)primaryConfigManager.getEffective(WorldMapPrimaryClientConfigOptions.GLOBAL_VERSION);
            region = new MapRegion(this.currentWorldId, this.currentDimId, this.currentMWId, mapDimension, regX, regZ, caveLayer, globalVersion, !mapDimension.isUsingWorldSave());
            final MapLayer mapLayer = regions.getLayer(caveLayer);
            region.updateCaveMode();
            final RegionDetection regionDetection = mapLayer.getRegionDetection(regX, regZ);
            if (regionDetection != null) {
                regionDetection.transferInfoTo(region);
                mapLayer.removeRegionDetection(regX, regZ);
            }
            else if (mapLayer.getCompleteRegionDetection(regX, regZ) == null) {
                final RegionDetection perpetualRegionDetection = new RegionDetection(region.getWorldId(), region.getDimId(), region.getMwId(), region.getRegionX(), region.getRegionZ(), region.getRegionFile(), globalVersion, true);
                mapLayer.tryAddingToCompleteRegionDetection(perpetualRegionDetection);
                if (!region.isNormalMapData()) {
                    mapLayer.removeRegionDetection(regX, regZ);
                }
            }
            if (!region.hasHadTerrain()) {
                regions.getLayer(caveLayer).getRegionHighlightExistenceTracker().stopTracking(regX, regZ);
                region.setVersion(globalVersion);
                region.setCacheHashCode(WorldMap.settings.getRegionCacheHashCode());
                region.setReloadVersion((int)primaryConfigManager.getEffective(WorldMapPrimaryClientConfigOptions.RELOAD_VIEWED_VERSION));
            }
            regions.putLeaf(regX, regZ, region);
            regions.addListRegion(region);
            if (regionDetection != null) {
                regionDetection.transferInfoPostAddTo(region, this);
            }
        }
        return region;
    }
    
    public MapRegion getMinimapMapRegion(final int regX, final int regZ) {
        final int renderedCaveLayer = this.minimapRenderListener.getRenderedCaveLayer();
        return this.getLeafMapRegion(renderedCaveLayer, regX, regZ, this.regionExists(renderedCaveLayer, regX, regZ));
    }
    
    public MapTileChunk getMapChunk(final int caveLayer, final int chunkX, final int chunkZ) {
        final int regionX = chunkX >> 3;
        final int regionZ = chunkZ >> 3;
        final MapRegion region = this.getLeafMapRegion(caveLayer, regionX, regionZ, false);
        if (region == null) {
            return null;
        }
        final int localChunkX = chunkX & 0x7;
        final int localChunkZ = chunkZ & 0x7;
        return region.getChunk(localChunkX, localChunkZ);
    }
    
    public MapTile getMapTile(final int caveLayer, final int x, final int z) {
        final MapTileChunk tileChunk = this.getMapChunk(caveLayer, x >> 2, z >> 2);
        if (tileChunk == null) {
            return null;
        }
        final int tileX = x & 0x3;
        final int tileZ = z & 0x3;
        return tileChunk.getTile(tileX, tileZ);
    }
    
    public void updateWorldSpawn(final BlockPos newSpawn, final WorldClient world) {
        final boolean debugConfig = WorldMapClientConfigUtils.getDebug();
        final int dimId = world.field_73011_w.getDimension();
        final WorldMapClientWorldData worldData = WorldMapClientWorldDataHelper.getWorldData(world);
        worldData.latestSpawn = newSpawn;
        if (debugConfig) {
            WorldMap.LOGGER.info("Updated spawn for dimension " + dimId + " " + newSpawn);
        }
        this.spawnToRestore = newSpawn;
        if (world == this.mainWorld) {
            this.mainWorldChangedTime = -1L;
            if (debugConfig) {
                WorldMap.LOGGER.info("Done waiting for main spawn.");
            }
        }
        this.checkForWorldUpdate();
    }
    
    public void onServerLevelId(final int serverLevelId) {
        final WorldMapClientWorldData worldData = WorldMapClientWorldDataHelper.getCurrentWorldData();
        worldData.serverLevelId = serverLevelId;
        if (WorldMapClientConfigUtils.getDebug()) {
            WorldMap.LOGGER.info("Updated server level id " + serverLevelId);
        }
        this.checkForWorldUpdate();
    }
    
    public void onWorldUnload() {
        if (this.mainWorldUnloaded) {
            return;
        }
        if (WorldMapClientConfigUtils.getDebug()) {
            WorldMap.LOGGER.info("Changing worlds, pausing the world map...");
        }
        this.mainWorldUnloaded = true;
        this.mapWorld.clearAllCachedHighlightHashes();
        this.mainWorldChangedTime = -1L;
        this.changeWorld(null);
    }
    
    public void onClientTickStart() throws RuntimeException {
        if (this.mainWorld != null && this.spawnToRestore != null && this.mainWorldChangedTime != -1L && System.currentTimeMillis() - this.mainWorldChangedTime >= 3000L) {
            if (WorldMapClientConfigUtils.getDebug()) {
                WorldMap.LOGGER.info("SPAWN SET TIME OUT");
            }
            this.updateWorldSpawn(this.spawnToRestore, this.mainWorld);
        }
    }
    
    private void updateRenderStartTime() {
        if (this.renderStartTime == -1L) {
            this.renderStartTime = System.nanoTime();
        }
    }
    
    public void pushWriterPause() {
        synchronized (this.renderThreadPauseSync) {
            ++this.pauseWriting;
        }
    }
    
    public void popWriterPause() {
        synchronized (this.renderThreadPauseSync) {
            --this.pauseWriting;
        }
    }
    
    public void pushRenderPause(final boolean rendering, final boolean uploading) {
        synchronized (this.renderThreadPauseSync) {
            if (rendering) {
                ++this.pauseRendering;
            }
            if (uploading) {
                ++this.pauseUploading;
            }
        }
    }
    
    public void popRenderPause(final boolean rendering, final boolean uploading) {
        synchronized (this.renderThreadPauseSync) {
            if (rendering) {
                --this.pauseRendering;
            }
            if (uploading) {
                --this.pauseUploading;
            }
        }
    }
    
    public void pushIsLoading() {
        synchronized (this.loadingSync) {
            this.isLoading = true;
        }
    }
    
    public void popIsLoading() {
        synchronized (this.loadingSync) {
            this.isLoading = false;
        }
    }
    
    public void pushUIPause() {
        synchronized (this.uiPauseSync) {
            this.isUIPaused = true;
        }
    }
    
    public void popUIPause() {
        synchronized (this.uiPauseSync) {
            this.isUIPaused = false;
        }
    }
    
    public boolean isUIPaused() {
        return this.isUIPaused;
    }
    
    public boolean isWritingPaused() {
        return this.pauseWriting > 0;
    }
    
    public boolean isRenderingPaused() {
        return this.pauseRendering > 0;
    }
    
    public boolean isUploadingPaused() {
        return this.pauseUploading > 0;
    }
    
    public boolean isProcessingPaused() {
        return this.pauseProcessing > 0;
    }
    
    public boolean isProcessed(final LeveledRegion<?> region) {
        final ArrayList<LeveledRegion<?>> toProcess = this.toProcessLevels[region.getLevel()];
        synchronized (toProcess) {
            return toProcess.contains(region);
        }
    }
    
    public void addToProcess(final LeveledRegion<?> region) {
        final ArrayList<LeveledRegion<?>> toProcess = this.toProcessLevels[region.getLevel()];
        synchronized (toProcess) {
            toProcess.add(region);
        }
    }
    
    public void removeToProcess(final LeveledRegion<?> region) {
        final ArrayList<LeveledRegion<?>> toProcess = this.toProcessLevels[region.getLevel()];
        synchronized (toProcess) {
            toProcess.remove(region);
        }
    }
    
    public int getProcessedCount() {
        int total = 0;
        for (int i = 0; i < this.toProcessLevels.length; ++i) {
            total += this.toProcessLevels[i].size();
        }
        return total;
    }
    
    public int getAffectingLoadingFrequencyCount() {
        int total = 0;
        for (int i = 0; i < this.toProcessLevels.length; ++i) {
            final ArrayList<LeveledRegion<?>> processed = this.toProcessLevels[i];
            for (int j = 0; j < processed.size(); ++j) {
                synchronized (processed) {
                    if (j >= processed.size()) {
                        break;
                    }
                    if (processed.get(j).shouldAffectLoadingRequestFrequency()) {
                        ++total;
                    }
                }
            }
        }
        return total;
    }
    
    public MapSaveLoad getMapSaveLoad() {
        return this.mapSaveLoad;
    }
    
    public WorldClient getWorld() {
        return this.world;
    }
    
    public WorldClient getNewWorld() {
        return this.newWorld;
    }
    
    public String getCurrentWorldId() {
        return this.currentWorldId;
    }
    
    public String getCurrentDimId() {
        return this.currentDimId;
    }
    
    public String getCurrentMWId() {
        return this.currentMWId;
    }
    
    public MapWriter getMapWriter() {
        return this.mapWriter;
    }
    
    public MapLimiter getMapLimiter() {
        return this.mapLimiter;
    }
    
    public ArrayList<Double[]> getFootprints() {
        return this.footprints;
    }
    
    public ByteBufferDeallocator getBufferDeallocator() {
        return this.bufferDeallocator;
    }
    
    public MapTilePool getTilePool() {
        return this.tilePool;
    }
    
    public OverlayManager getOverlayManager() {
        return this.overlayManager;
    }
    
    public int getGlobalVersion() {
        final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        final SingleConfigManager<Config> primaryConfigManager = (SingleConfigManager<Config>)configManager.getPrimaryConfigManager();
        return (int)primaryConfigManager.getEffective(WorldMapPrimaryClientConfigOptions.GLOBAL_VERSION);
    }
    
    public long getRenderStartTime() {
        return this.renderStartTime;
    }
    
    public void resetRenderStartTime() {
        this.renderStartTime = -1L;
    }
    
    public Queue<FutureTask<?>> getMinecraftScheduledTasks() {
        this.scheduledTasksField.setAccessible(true);
        Queue<FutureTask<?>> result;
        try {
            result = (Queue<FutureTask<?>>)this.scheduledTasksField.get(Minecraft.func_71410_x());
        }
        catch (IllegalArgumentException e) {
            result = null;
        }
        catch (IllegalAccessException e2) {
            result = null;
        }
        this.scheduledTasksField.setAccessible(false);
        return result;
    }
    
    public Callable<Object> getRenderStartTimeUpdater() {
        return this.renderStartTimeUpdater;
    }
    
    public boolean isWaitingForWorldUpdate() {
        return this.waitingForWorldUpdate;
    }
    
    public WorldDataHandler getWorldDataHandler() {
        return this.worldDataHandler;
    }
    
    public void setMainValues() {
        synchronized (this.mainStuffSync) {
            final Entity player = Minecraft.func_71410_x().func_175606_aa();
            if (player != null) {
                final WorldClient worldToChangeTo = (WorldClient)((this.ignoreWorld(player.field_70170_p) || !(player.field_70170_p instanceof WorldClient)) ? this.mainWorld : player.field_70170_p);
                final boolean worldChanging = worldToChangeTo != this.mainWorld;
                if (worldChanging) {
                    this.mainWorldChangedTime = -1L;
                    if (this.spawnToRestore != null) {
                        final WorldMapClientWorldData worldData = WorldMapClientWorldDataHelper.getWorldData(worldToChangeTo);
                        if (worldData.latestSpawn == null) {
                            this.mainWorldChangedTime = System.currentTimeMillis();
                        }
                    }
                    this.mainWorldUnloaded = false;
                }
                this.mainWorld = worldToChangeTo;
                this.mainPlayerX = player.field_70165_t;
                this.mainPlayerY = player.field_70163_u;
                this.mainPlayerZ = player.field_70161_v;
                if (worldChanging) {
                    this.checkForWorldUpdate();
                }
                this.blockStateColorTypeCache.updateDefaultResolvers((World)this.mainWorld);
            }
            else {
                if (this.mainWorld != null && !this.mainWorldUnloaded) {
                    this.onWorldUnload();
                }
                this.mainWorld = null;
            }
        }
    }
    
    public float getBrightness() {
        final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        return this.getBrightness((boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.LIGHTING));
    }
    
    public float getBrightness(final boolean lighting) {
        return this.getBrightness(this.currentCaveLayer, this.world, lighting);
    }
    
    public float getBrightness(final int layer, final WorldClient world, final boolean lighting) {
        if (world == null || world != this.world) {
            return 1.0f;
        }
        final MapDimension dim = this.mapWorld.getCurrentDimension();
        final MapDimensionTypeInfo dimType = dim.getDimensionType();
        float sunBrightness;
        if (layer == Integer.MAX_VALUE || (dimType != null && !dimType.hasSkyLight())) {
            if (!lighting) {
                return 1.0f;
            }
            if (dimType != null && dimType.isEnd()) {
                return 1.0f;
            }
            sunBrightness = (dim.getSkyDarken(1.0f, world) - 0.2f) / 0.8f;
        }
        else {
            if (!lighting) {
                return 1.0f;
            }
            sunBrightness = 0.0f;
        }
        final float ambient = this.getAmbientBrightness(dimType);
        return ambient + (1.0f - ambient) * MathHelper.func_76131_a(sunBrightness, 0.0f, 1.0f);
    }
    
    public MapBiomes getMapBiomes() {
        return this.mapBiomes;
    }
    
    public float getAmbientBrightness(final MapDimensionTypeInfo dimType) {
        float result = 0.375f + ((dimType == null) ? 0.0f : dimType.getAmbientLight());
        if (result > 1.0f) {
            result = 1.0f;
        }
        return result;
    }
    
    public static boolean isWorldRealms(final String world) {
        return world.startsWith("Realms_");
    }
    
    public static boolean isWorldMultiplayer(final boolean realms, final String world) {
        return realms || world.startsWith("Multiplayer_");
    }
    
    public MapWorld getMapWorld() {
        return this.mapWorld;
    }
    
    public boolean isCurrentMultiworldWritable() {
        return this.mapWorldUsable && this.mapWorld.getCurrentDimension().currentMultiworldWritable;
    }
    
    public String getCurrentDimension() {
        return "placeholder";
    }
    
    public void requestCurrentMapDeletion() {
        if (this.currentMapNeedsDeletion) {
            throw new RuntimeException("Requesting map deletion at a weird time!");
        }
        this.currentMapNeedsDeletion = true;
    }
    
    public boolean isFinalizing() {
        return this.finalizing;
    }
    
    public void stop() {
        this.finalizing = true;
        WorldMap.mapRunner.addTask(new MapRunnerTask() {
            @Override
            public void run(final MapProcessor doNotUse) {
                if (MapProcessor.this.state == 0) {
                    MapProcessor.this.state = 1;
                    if (!MapProcessor.this.mapWorldUsable) {
                        MapProcessor.this.forceClean();
                    }
                    else {
                        MapProcessor.this.changeWorld(null);
                    }
                }
            }
        });
    }
    
    private synchronized void forceClean() {
        this.pushRenderPause(true, true);
        this.pushWriterPause();
        if (this.mapWorld != null) {
            for (final MapDimension dim : this.mapWorld.getDimensionsList()) {
                for (final LeveledRegion<?> region : dim.getLayeredMapRegions().getUnsyncedSet()) {
                    region.onDimensionClear(this);
                }
            }
        }
        this.popRenderPause(true, true);
        this.popWriterPause();
        if (this.currentMapLock != null) {
            if (this.mapLockToRelease != null) {
                this.releaseLocksIfNeeded();
            }
            this.mapLockToRelease = this.currentMapLock;
            this.mapLockChannelToClose = this.currentMapLockChannel;
            this.releaseLocksIfNeeded();
        }
        this.state = 2;
        WorldMap.LOGGER.info("World map force-cleaned!");
    }
    
    public boolean isMapWorldUsable() {
        return this.mapWorldUsable;
    }
    
    private Object getAutoIdBase(final WorldClient world) {
        return this.hasServerLevelId() ? WorldMapClientWorldDataHelper.getCurrentWorldData().serverLevelId : WorldMapClientWorldDataHelper.getWorldData(world).latestSpawn;
    }
    
    private Object getUsedAutoIdBase(final WorldClient world) {
        final WorldMapClientWorldData worldData = WorldMapClientWorldDataHelper.getWorldData(world);
        return this.hasServerLevelId() ? WorldMapClientWorldDataHelper.getCurrentWorldData().usedServerLevelId : worldData.usedSpawn;
    }
    
    private void setUsedAutoIdBase(final WorldClient world, final Object autoIdBase) {
        final WorldMapClientWorldData worldData = WorldMapClientWorldDataHelper.getWorldData(world);
        if (this.hasServerLevelId()) {
            WorldMapClientWorldDataHelper.getCurrentWorldData().usedServerLevelId = (Integer)autoIdBase;
        }
        else {
            worldData.usedSpawn = (BlockPos)autoIdBase;
        }
    }
    
    private void removeUsedAutoIdBase(final WorldClient world) {
        final WorldMapClientWorldData worldData = WorldMapClientWorldDataHelper.getWorldData(world);
        if (this.hasServerLevelId()) {
            WorldMapClientWorldDataHelper.getCurrentWorldData().usedServerLevelId = null;
        }
        else {
            worldData.usedSpawn = null;
        }
    }
    
    private boolean hasServerLevelId() {
        final WorldMapClientWorldData worldData = WorldMapClientWorldDataHelper.getCurrentWorldData();
        return worldData != null && worldData.serverLevelId != null && !this.mapWorld.isIgnoreServerLevelId();
    }
    
    public boolean isEqual(final String worldId, final String dimId, final String mwId) {
        return worldId.equals(this.currentWorldId) && dimId.equals(this.currentDimId) && (mwId == this.currentMWId || (mwId != null && mwId.equals(this.currentMWId)));
    }
    
    @Deprecated
    public String getCurrentWorldString() {
        return this.getCurrentWorldId();
    }
    
    public boolean isFinished() {
        return this.state == 3;
    }
    
    public boolean isCurrentMapLocked() {
        return this.currentMapLock == null;
    }
    
    private void releaseLocksIfNeeded() {
        if (this.mapLockToRelease != null) {
            int lockAttempts = 10;
            while (lockAttempts-- > 0) {
                try {
                    if (this.mapLockToRelease.isValid()) {
                        this.mapLockToRelease.release();
                    }
                    this.mapLockChannelToClose.close();
                }
                catch (Exception e) {
                    WorldMap.LOGGER.error("Failed attempt to release the lock for the world map! Retrying in 50 ms... " + lockAttempts, (Throwable)e);
                    try {
                        Thread.sleep(50L);
                    }
                    catch (InterruptedException ex) {}
                    continue;
                }
                break;
            }
            this.mapLockToRelease = null;
            this.mapLockChannelToClose = null;
        }
    }
    
    private int getCaveLayer(final int caveStart) {
        if (caveStart == Integer.MAX_VALUE || caveStart == Integer.MIN_VALUE) {
            return caveStart;
        }
        return caveStart >> 4;
    }
    
    public int getCurrentCaveLayer() {
        return this.currentCaveLayer;
    }
    
    public BlockStateShortShapeCache getBlockStateShortShapeCache() {
        return this.blockStateShortShapeCache;
    }
    
    public HighlighterRegistry getHighlighterRegistry() {
        return this.highlighterRegistry;
    }
    
    public MapRegionHighlightsPreparer getMapRegionHighlightsPreparer() {
        return this.mapRegionHighlightsPreparer;
    }
    
    public MessageBox getMessageBox() {
        return this.messageBox;
    }
    
    public MessageBoxRenderer getMessageBoxRenderer() {
        return this.messageBoxRenderer;
    }
    
    public BiomeColorCalculator getBiomeColorCalculator() {
        return this.biomeColorCalculator;
    }
    
    public boolean isConsideringNetherFairPlay() {
        return this.consideringNetherFairPlayMessage;
    }
    
    public void setConsideringNetherFairPlayMessage(final boolean consideringNetherFairPlay) {
        this.consideringNetherFairPlayMessage = consideringNetherFairPlay;
    }
    
    public Executor getRenderExecutor() {
        return this.renderExecutor;
    }
    
    public ClientSyncedTrackedPlayerManager getClientSyncedTrackedPlayerManager() {
        return this.clientSyncedTrackedPlayerManager;
    }
    
    public boolean serverHasMod() {
        final WorldMapClientWorldData worldData = WorldMapClientWorldDataHelper.getCurrentWorldData();
        return worldData != null && worldData.serverLevelId != null;
    }
    
    public void setServerModNetworkVersion(final int networkVersion) {
        final WorldMapClientWorldData worldData = WorldMapClientWorldDataHelper.getCurrentWorldData();
        if (worldData == null) {
            return;
        }
        worldData.setServerModNetworkVersion(networkVersion);
    }
    
    public int getServerModNetworkVersion() {
        final WorldMapClientWorldData worldData = WorldMapClientWorldDataHelper.getCurrentWorldData();
        if (worldData == null) {
            return 0;
        }
        return worldData.getServerModNetworkVersion();
    }
    
    private void checkFootstepsReset(final World oldWorld, final World newWorld) {
        final Integer oldDimId = (oldWorld == null) ? null : Integer.valueOf(oldWorld.field_73011_w.getDimension());
        final Integer newDimId = (newWorld == null) ? null : Integer.valueOf(newWorld.field_73011_w.getDimension());
        if (!Objects.equals(oldDimId, newDimId)) {
            this.footprints.clear();
        }
    }
    
    private void fixRootFolder(final String mainId) {
        for (int format = 3; format >= 1; --format) {
            this.fixRootFolder(mainId, this.getMainId(format));
        }
    }
    
    private void fixRootFolder(final String mainId, final String oldMainId) {
        if (!mainId.equals(oldMainId)) {
            Path oldFolder;
            try {
                oldFolder = WorldMap.saveFolder.toPath().resolve(oldMainId);
            }
            catch (InvalidPathException ipe) {
                return;
            }
            if (Files.exists(oldFolder, new LinkOption[0])) {
                final Path fixedFolder = WorldMap.saveFolder.toPath().resolve(mainId);
                if (!Files.exists(fixedFolder, new LinkOption[0])) {
                    try {
                        Files.move(oldFolder, fixedFolder, new CopyOption[0]);
                    }
                    catch (IOException e) {
                        throw new RuntimeException("failed to auto-restore old world map folder", e);
                    }
                }
            }
        }
    }
    
    public boolean fairplayMessageWasReceived() {
        return this.fairplayMessageReceived;
    }
    
    public void setFairplayMessageReceived(final boolean fairplayMessageReceived) {
        this.fairplayMessageReceived = fairplayMessageReceived;
    }
    
    public void updateMapItem() {
        final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        final String mapItemString = ((String)configManager.getEffective(WorldMapProfiledConfigOptions.MAP_ITEM)).trim();
        if (mapItemString.isEmpty() || mapItemString.equals("-")) {
            this.mapItem = null;
            WorldMap.LOGGER.info("Fullscreen map required item set to nothing.");
            return;
        }
        ResourceLocation mapItemRL;
        try {
            mapItemRL = new ResourceLocation(mapItemString);
        }
        catch (Exception rle) {
            WorldMap.LOGGER.error("Tried setting the full screen map required item to a misformatted ID: {}, Error: {}", (Object)mapItemString, (Object)rle.getMessage());
            this.mapItem = null;
            return;
        }
        this.mapItem = (Item)Item.field_150901_e.func_82594_a((Object)mapItemRL);
        if (this.mapItem == null) {
            this.mapItem = null;
            WorldMap.LOGGER.error("Tried setting the full screen map required item to an invalid ID: {}", (Object)mapItemString);
            return;
        }
        WorldMap.LOGGER.info("Fullscreen map required item set to: {}", (Object)mapItemString);
    }
    
    public Item getMapItem() {
        return this.mapItem;
    }
}
