//Decompiled by Procyon!

package xaero.map;

import xaero.map.controls.*;
import xaero.map.file.*;
import xaero.map.graphics.*;
import xaero.map.cache.*;
import xaero.map.biome.*;
import xaero.map.executor.*;
import xaero.map.file.worldsave.*;
import xaero.map.highlight.*;
import xaero.map.radar.tracker.synced.*;
import xaero.map.gui.message.*;
import xaero.map.gui.message.render.*;
import xaero.map.misc.*;
import net.minecraft.client.*;
import net.minecraft.client.entity.*;
import xaero.map.core.*;

public class WorldMapSession
{
    private ControlsHandler controlsHandler;
    private MapProcessor mapProcessor;
    private MapWriter mapWriter;
    private boolean usable;
    
    public void init() throws NoSuchFieldException {
        final BlockStateShortShapeCache blockStateShortShapeCache = new BlockStateShortShapeCache(this);
        final MapSaveLoad mapSaveLoad = new MapSaveLoad(WorldMap.overlayManager, WorldMap.pngExporter, blockStateShortShapeCache);
        final TextureUploader textureUploader = new TextureUploader(WorldMap.normalTextureUploadPool, WorldMap.compressedTextureUploadPool, WorldMap.branchUpdatePool, WorldMap.branchUpdateAllocatePool, WorldMap.branchDownloadPool, WorldMap.subsequentNormalTextureUploadPool, WorldMap.textureUploadBenchmark);
        final BlockStateColorTypeCache blockStateColorTypeCache = new BlockStateColorTypeCache();
        final BiomeColorCalculator biomeColorCalculator = new BiomeColorCalculator();
        final WorldDataReader worldDataReader = new WorldDataReader(WorldMap.overlayManager, blockStateColorTypeCache, blockStateShortShapeCache);
        final Executor worldDataRenderExecutor = new Executor("world data render executor", Thread.currentThread());
        final WorldDataHandler worldDataHandler = new WorldDataHandler(worldDataReader, worldDataRenderExecutor);
        blockStateColorTypeCache.setMapWriter(this.mapWriter = new MapWriter(WorldMap.overlayManager, blockStateColorTypeCache, blockStateShortShapeCache));
        blockStateColorTypeCache.updateGrassColor();
        final HighlighterRegistry highlightRegistry = new HighlighterRegistry();
        highlightRegistry.end();
        final MapRegionHighlightsPreparer mapRegionHighlightsPreparer = new MapRegionHighlightsPreparer();
        final ClientSyncedTrackedPlayerManager clientSyncedTrackedPlayerManager = new ClientSyncedTrackedPlayerManager();
        this.mapProcessor = new MapProcessor(mapSaveLoad, this.mapWriter, WorldMap.mapLimiter, WorldMap.bufferDeallocator, WorldMap.tilePool, WorldMap.overlayManager, textureUploader, worldDataHandler, WorldMap.mapBiomes, WorldMap.worldMapClient.branchTextureRenderer, biomeColorCalculator, blockStateColorTypeCache, blockStateShortShapeCache, highlightRegistry, mapRegionHighlightsPreparer, MessageBox.Builder.begin().build(), new MessageBoxRenderer(), new CaveStartCalculator(this.mapWriter), clientSyncedTrackedPlayerManager, new Executor("generic render thread", Thread.currentThread()));
        this.mapWriter.setMapProcessor(this.mapProcessor);
        mapSaveLoad.setMapProcessor(this.mapProcessor);
        worldDataReader.setMapProcessor(this.mapProcessor);
        this.controlsHandler = new ControlsHandler(this.mapProcessor);
        this.mapProcessor.onInit();
        this.usable = true;
        WorldMap.LOGGER.info("New world map session initialized!");
    }
    
    public void cleanup() {
        try {
            if (this.usable) {
                this.mapProcessor.stop();
                WorldMap.LOGGER.info("Finalizing world map session...");
                WorldMap.mapRunnerThread.interrupt();
                while (!this.mapProcessor.isFinished()) {
                    this.mapProcessor.waitForLoadingToFinish((Runnable)new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                    try {
                        Thread.sleep(20L);
                    }
                    catch (InterruptedException e) {
                        WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
                    }
                }
            }
            WorldMap.LOGGER.info("World map session finalized.");
            WorldMap.onSessionFinalized();
        }
        catch (Throwable t) {
            WorldMap.LOGGER.error("World map session failed to finalize properly.", t);
        }
        this.usable = false;
    }
    
    public ControlsHandler getControlsHandler() {
        return this.controlsHandler;
    }
    
    public MapProcessor getMapProcessor() {
        return this.mapProcessor;
    }
    
    public static WorldMapSession getCurrentSession() {
        WorldMapSession session = getForPlayer(Minecraft.func_71410_x().field_71439_g);
        if (session == null && XaeroWorldMapCore.currentSession != null && XaeroWorldMapCore.currentSession.usable) {
            session = XaeroWorldMapCore.currentSession;
        }
        return session;
    }
    
    public static WorldMapSession getForPlayer(final EntityPlayerSP player) {
        if (player == null || player.field_71174_a == null) {
            return null;
        }
        return ((IWorldMapClientPlayNetHandler)player.field_71174_a).getXaero_worldmapSession();
    }
    
    public boolean isUsable() {
        return this.usable;
    }
}
