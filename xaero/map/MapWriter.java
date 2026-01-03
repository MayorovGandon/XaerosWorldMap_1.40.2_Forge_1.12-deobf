//Decompiled by Procyon!

package xaero.map;

import net.minecraft.util.math.*;
import net.minecraft.block.state.*;
import xaero.map.cache.*;
import it.unimi.dsi.fastutil.objects.*;
import xaero.map.common.config.option.*;
import xaero.lib.common.config.option.*;
import xaero.map.core.*;
import xaero.map.misc.*;
import xaero.lib.client.config.*;
import net.minecraft.client.*;
import xaero.map.gui.*;
import java.util.function.*;
import xaero.lib.common.reflection.util.*;
import net.minecraft.world.chunk.*;
import xaero.map.region.*;
import net.minecraft.world.chunk.storage.*;
import net.minecraft.init.*;
import xaero.map.mods.*;
import net.minecraft.util.registry.*;
import net.minecraft.tileentity.*;
import net.minecraft.block.material.*;
import net.minecraft.world.*;
import xaero.map.biome.*;
import xaero.map.config.util.*;
import net.minecraftforge.common.property.*;
import xaero.map.exception.*;
import net.minecraft.util.*;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.block.*;
import java.util.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.*;
import java.io.*;
import java.awt.image.*;
import net.minecraft.entity.player.*;

public class MapWriter
{
    public static final int MAX_TRANSPARENCY_BLEND_DEPTH = 5;
    public static final String[] DEFAULT_RESOURCE;
    private int X;
    private int Z;
    private int playerChunkX;
    private int playerChunkZ;
    private int loadDistance;
    private int startTileChunkX;
    private int startTileChunkZ;
    private int endTileChunkX;
    private int endTileChunkZ;
    private int insideX;
    private int insideZ;
    private long updateCounter;
    private int caveStart;
    private int writingLayer;
    private int writtenCaveStart;
    private boolean clearCachedColours;
    private MapBlock loadingObject;
    private OverlayBuilder overlayBuilder;
    private final BlockPos.MutableBlockPos mutableLocalPos;
    private final BlockPos.MutableBlockPos mutableGlobalPos;
    private int[] biomeBuffer;
    private long lastWrite;
    private long lastWriteTry;
    private int workingFrameCount;
    private long framesFreedTime;
    public long writeFreeSinceLastWrite;
    private int writeFreeSizeTiles;
    private int writeFreeFullUpdateTargetTime;
    private BlockStateColorTypeCache colorTypeCache;
    private MapProcessor mapProcessor;
    private ArrayList<IBlockState> buggedStates;
    private WriterBiomeInfoSupplier writerBiomeInfoSupplier;
    private BlockStateShortShapeCache blockStateShortShapeCache;
    private int topH;
    private final CachedFunction<IBlockState, Boolean> transparentCache;
    private int firstTransparentStateY;
    private final BlockPos.MutableBlockPos mutableBlockPos3;
    private ArrayList<MapRegion> regionBuffer;
    private MapTileChunk rightChunk;
    private MapTileChunk bottomRightChunk;
    private HashMap<String, Integer> textureColours;
    private HashMap<Integer, Integer> blockColours;
    private final Object2IntMap<IBlockState> blockTintIndices;
    private long lastLayerSwitch;
    private OtherLayerNotifier otherLayerNotifier;
    private int lastBlockStateForTextureColor;
    private int lastBlockStateForTextureColorResult;
    
    public MapWriter(final OverlayManager overlayManager, final BlockStateColorTypeCache colorTypeCache, final BlockStateShortShapeCache blockStateShortShapeCache) {
        this.writingLayer = Integer.MAX_VALUE;
        this.writtenCaveStart = Integer.MAX_VALUE;
        this.lastWrite = -1L;
        this.lastWriteTry = -1L;
        this.framesFreedTime = -1L;
        this.writeFreeSinceLastWrite = -1L;
        this.regionBuffer = new ArrayList<MapRegion>();
        this.rightChunk = null;
        this.bottomRightChunk = null;
        this.lastBlockStateForTextureColor = -1;
        this.lastBlockStateForTextureColorResult = -1;
        this.loadingObject = new MapBlock();
        this.textureColours = new HashMap<String, Integer>();
        this.blockColours = new HashMap<Integer, Integer>();
        this.overlayBuilder = new OverlayBuilder(overlayManager);
        this.mutableLocalPos = new BlockPos.MutableBlockPos();
        this.mutableGlobalPos = new BlockPos.MutableBlockPos();
        this.biomeBuffer = new int[3];
        this.colorTypeCache = colorTypeCache;
        this.buggedStates = new ArrayList<IBlockState>();
        this.writerBiomeInfoSupplier = new WriterBiomeInfoSupplier((BlockPos)this.mutableGlobalPos);
        this.blockStateShortShapeCache = blockStateShortShapeCache;
        this.transparentCache = new CachedFunction<IBlockState, Boolean>(new Function<IBlockState, Boolean>() {
            @Override
            public Boolean apply(final IBlockState state) {
                return MapWriter.this.shouldOverlay(state);
            }
        });
        this.mutableBlockPos3 = new BlockPos.MutableBlockPos();
        this.otherLayerNotifier = new OtherLayerNotifier();
        this.blockTintIndices = (Object2IntMap<IBlockState>)new Object2IntOpenHashMap();
    }
    
    public void onRender(final BiomeColorCalculator biomeColorCalculator, final OverlayManager overlayManager) {
        final long before = System.nanoTime();
        try {
            if (WorldMap.crashHandler.getCrashedBy() == null) {
                synchronized (this.mapProcessor.renderThreadPauseSync) {
                    if (!this.mapProcessor.isWritingPaused() && !this.mapProcessor.isWaitingForWorldUpdate() && this.mapProcessor.getMapSaveLoad().isRegionDetectionComplete() && this.mapProcessor.isCurrentMultiworldWritable()) {
                        final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
                        final boolean loadChunksConfig = (boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.LOAD_NEW_CHUNKS);
                        final boolean updateChunksConfig = (boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.UPDATE_CHUNKS);
                        if (this.mapProcessor.getWorld() == null || this.mapProcessor.isCurrentMapLocked() || this.mapProcessor.getMapWorld().isCacheOnlyMode()) {
                            return;
                        }
                        if (this.mapProcessor.getCurrentWorldId() != null && !this.mapProcessor.ignoreWorld((World)this.mapProcessor.getWorld()) && (updateChunksConfig || loadChunksConfig || this.mapProcessor.getMapWorld().getCurrentDimension().isUsingWorldSave())) {
                            final double playerX;
                            final double playerY;
                            final double playerZ;
                            synchronized (this.mapProcessor.mainStuffSync) {
                                if (this.mapProcessor.mainWorld != this.mapProcessor.getWorld()) {
                                    return;
                                }
                                if (this.mapProcessor.getMapWorld().getCurrentDimensionId() == null || this.mapProcessor.getWorld().field_73011_w.getDimension() != this.mapProcessor.getMapWorld().getCurrentDimensionId()) {
                                    return;
                                }
                                playerX = this.mapProcessor.mainPlayerX;
                                playerY = this.mapProcessor.mainPlayerY;
                                playerZ = this.mapProcessor.mainPlayerZ;
                            }
                            XaeroWorldMapCore.ensureField();
                            int lengthX = this.endTileChunkX - this.startTileChunkX + 1;
                            int lengthZ = this.endTileChunkZ - this.startTileChunkZ + 1;
                            if (this.lastWriteTry == -1L) {
                                lengthX = 3;
                                lengthZ = 3;
                            }
                            final int sizeTileChunks = lengthX * lengthZ;
                            final int sizeTiles = sizeTileChunks * 4 * 4;
                            final int sizeBasedTargetTime = sizeTiles * 1000 / 1500;
                            final int fullUpdateTargetTime = Math.max(100, sizeBasedTargetTime);
                            final long time = System.currentTimeMillis();
                            final long passed = (this.lastWrite == -1L) ? 0L : (time - this.lastWrite);
                            if (this.lastWriteTry == -1L || this.writeFreeSizeTiles != sizeTiles || this.writeFreeFullUpdateTargetTime != fullUpdateTargetTime || this.workingFrameCount > 30) {
                                this.framesFreedTime = time;
                                this.writeFreeSizeTiles = sizeTiles;
                                this.writeFreeFullUpdateTargetTime = fullUpdateTargetTime;
                                this.workingFrameCount = 0;
                            }
                            long sinceLastWrite = Math.min(passed, this.writeFreeSinceLastWrite);
                            if (this.framesFreedTime != -1L) {
                                sinceLastWrite = time - this.framesFreedTime;
                            }
                            final long tilesToUpdate = Math.min(sinceLastWrite * sizeTiles / fullUpdateTargetTime, 100L);
                            if (this.lastWrite == -1L || tilesToUpdate != 0L) {
                                this.lastWrite = time;
                            }
                            if (tilesToUpdate != 0L) {
                                if (this.framesFreedTime == -1L) {
                                    final int timeLimit = (int)(Math.min(sinceLastWrite, 50L) * 86960L);
                                    final long writeStartNano = System.nanoTime();
                                    final boolean loadChunks = loadChunksConfig || this.mapProcessor.getMapWorld().getCurrentDimension().isUsingWorldSave();
                                    final boolean updateChunks = updateChunksConfig || this.mapProcessor.getMapWorld().getCurrentDimension().isUsingWorldSave();
                                    final boolean ignoreHeightmaps = this.mapProcessor.getMapWorld().isIgnoreHeightmaps();
                                    final boolean flowers = (boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.FLOWERS);
                                    final boolean detailedDebug = WorldMap.detailed_debug;
                                    final int caveDepth = (int)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.CAVE_MODE_DEPTH);
                                    final BlockPos.MutableBlockPos mutableBlockPos3 = this.mutableBlockPos3;
                                    final MapUpdateFastConfig config = new MapUpdateFastConfig();
                                    for (int i = 0; i < tilesToUpdate; ++i) {
                                        if (this.writeMap((World)this.mapProcessor.getWorld(), playerX, playerY, playerZ, biomeColorCalculator, overlayManager, loadChunks, updateChunks, ignoreHeightmaps, flowers, detailedDebug, mutableBlockPos3, caveDepth, config)) {
                                            --i;
                                        }
                                        if (System.nanoTime() - writeStartNano >= timeLimit) {
                                            break;
                                        }
                                    }
                                    ++this.workingFrameCount;
                                }
                                else {
                                    this.writeFreeSinceLastWrite = sinceLastWrite;
                                    this.framesFreedTime = -1L;
                                }
                            }
                            this.lastWriteTry = time;
                            final int startRegionX = this.startTileChunkX >> 3;
                            final int startRegionZ = this.startTileChunkZ >> 3;
                            final int endRegionX = this.endTileChunkX >> 3;
                            final int endRegionZ = this.endTileChunkZ >> 3;
                            boolean shouldRequestLoading = false;
                            final LeveledRegion<?> nextToLoad = (LeveledRegion<?>)this.mapProcessor.getMapSaveLoad().getNextToLoadByViewing();
                            shouldRequestLoading = (nextToLoad == null || nextToLoad.shouldAllowAnotherRegionToLoad());
                            this.regionBuffer.clear();
                            final int comparisonChunkX = this.playerChunkX - 16;
                            final int comparisonChunkZ = this.playerChunkZ - 16;
                            LeveledRegion.setComparison(comparisonChunkX, comparisonChunkZ, 0, comparisonChunkX, comparisonChunkZ);
                            for (int visitRegionX = startRegionX; visitRegionX <= endRegionX; ++visitRegionX) {
                                for (int visitRegionZ = startRegionZ; visitRegionZ <= endRegionZ; ++visitRegionZ) {
                                    final MapRegion visitRegion = this.mapProcessor.getLeafMapRegion(this.writingLayer, visitRegionX, visitRegionZ, true);
                                    if (visitRegion != null && visitRegion.getLoadState() == 2) {
                                        visitRegion.registerVisit();
                                    }
                                    synchronized (visitRegion) {
                                        if (visitRegion.isResting() && shouldRequestLoading && visitRegion.canRequestReload_unsynced() && visitRegion.getLoadState() != 2) {
                                            visitRegion.calculateSortingChunkDistance();
                                            Misc.addToListOfSmallest(10, this.regionBuffer, visitRegion);
                                        }
                                    }
                                }
                            }
                            for (int toRequest = 1, counter = 0, j = 0; j < this.regionBuffer.size() && counter < toRequest; ++j) {
                                final MapRegion region = this.regionBuffer.get(j);
                                if (region != nextToLoad || this.regionBuffer.size() <= 1) {
                                    synchronized (region) {
                                        if (region.canRequestReload_unsynced() && region.getLoadState() != 2) {
                                            region.setBeingWritten(true);
                                            this.mapProcessor.getMapSaveLoad().requestLoad(region, "writing");
                                            if (counter == 0) {
                                                this.mapProcessor.getMapSaveLoad().setNextToLoadByViewing((LeveledRegion)region);
                                            }
                                            ++counter;
                                            if (region.getLoadState() == 4) {
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (Throwable e) {
            WorldMap.crashHandler.setCrashedBy(e);
        }
    }
    
    private int getWriteDistance() {
        int limit = (int)(this.mapProcessor.getMapWorld().getCurrentDimension().isUsingWorldSave() ? Integer.MAX_VALUE : WorldMap.INSTANCE.getConfigs().getClientConfigManager().getEffective((ConfigOption)WorldMapProfiledConfigOptions.WRITING_DISTANCE));
        if (limit < 0) {
            limit = Integer.MAX_VALUE;
        }
        return Math.min(limit, Math.min(32, Minecraft.func_71410_x().field_71474_y.field_151451_c));
    }
    
    public boolean writeMap(final World world, final double playerX, final double playerY, final double playerZ, final BiomeColorCalculator biomeColorCalculator, final OverlayManager overlayManager, final boolean loadChunks, final boolean updateChunks, final boolean ignoreHeightmaps, final boolean flowers, final boolean detailedDebug, final BlockPos.MutableBlockPos mutableBlockPos3, final int caveDepth, final MapUpdateFastConfig config) {
        final boolean onlyLoad = loadChunks && (!updateChunks || this.updateCounter % 5L != 0L);
        synchronized (world) {
            if (this.insideX == 0 && this.insideZ == 0) {
                if (this.X == 0 && this.Z == 0) {
                    this.writtenCaveStart = this.caveStart;
                }
                this.mapProcessor.updateCaveStart();
                final int newWritingLayer = this.mapProcessor.getCurrentCaveLayer();
                if (this.writingLayer != newWritingLayer && System.currentTimeMillis() - this.lastLayerSwitch > 300L) {
                    this.writingLayer = newWritingLayer;
                    this.lastLayerSwitch = System.currentTimeMillis();
                }
                this.loadDistance = this.getWriteDistance();
                if (this.writingLayer != Integer.MAX_VALUE && !(Minecraft.func_71410_x().field_71462_r instanceof GuiMap)) {
                    this.loadDistance = Math.min(16, this.loadDistance);
                }
                this.caveStart = this.mapProcessor.getMapWorld().getCurrentDimension().getLayeredMapRegions().getLayer(this.writingLayer).getCaveStart();
                if (this.caveStart != this.writtenCaveStart) {
                    this.loadDistance = Math.min(4, this.loadDistance);
                }
                this.playerChunkX = (int)Math.floor(playerX) >> 4;
                this.playerChunkZ = (int)Math.floor(playerZ) >> 4;
                this.startTileChunkX = this.playerChunkX - this.loadDistance >> 2;
                this.startTileChunkZ = this.playerChunkZ - this.loadDistance >> 2;
                this.endTileChunkX = this.playerChunkX + this.loadDistance >> 2;
                this.endTileChunkZ = this.playerChunkZ + this.loadDistance >> 2;
            }
            final int tileChunkX = this.startTileChunkX + this.X;
            final int tileChunkZ = this.startTileChunkZ + this.Z;
            final int tileChunkLocalX = tileChunkX & 0x7;
            final int tileChunkLocalZ = tileChunkZ & 0x7;
            final int chunkX = tileChunkX * 4 + this.insideX;
            final int chunkZ = tileChunkZ * 4 + this.insideZ;
            final boolean wasSkipped = this.writeChunk(world, this.loadDistance, onlyLoad, biomeColorCalculator, overlayManager, loadChunks, updateChunks, ignoreHeightmaps, flowers, detailedDebug, mutableBlockPos3, caveDepth, this.caveStart, this.writingLayer, tileChunkX, tileChunkZ, tileChunkLocalX, tileChunkLocalZ, chunkX, chunkZ, config);
            return wasSkipped && (Math.abs(chunkX - this.playerChunkX) > 8 || Math.abs(chunkZ - this.playerChunkZ) > 8);
        }
    }
    
    public boolean writeChunk(final World world, final int distance, final boolean onlyLoad, final BiomeColorCalculator biomeColorCalculator, final OverlayManager overlayManager, final boolean loadChunks, final boolean updateChunks, final boolean ignoreHeightmaps, final boolean flowers, final boolean detailedDebug, final BlockPos.MutableBlockPos mutableBlockPos3, final int caveDepth, final int caveStart, final int layerToWrite, final int tileChunkX, final int tileChunkZ, final int tileChunkLocalX, final int tileChunkLocalZ, final int chunkX, final int chunkZ, final MapUpdateFastConfig updateConfig) {
        final int regionX = tileChunkX >> 3;
        final int regionZ = tileChunkZ >> 3;
        MapTileChunk tileChunk = null;
        this.rightChunk = null;
        MapTileChunk bottomChunk = null;
        this.bottomRightChunk = null;
        final MapRegion region = this.mapProcessor.getLeafMapRegion(layerToWrite, regionX, regionZ, true);
        boolean wasSkipped = true;
        synchronized (region.writerThreadPauseSync) {
            if (!region.isWritingPaused()) {
                boolean createdTileChunk = false;
                final boolean isProperLoadState;
                final boolean regionIsResting;
                synchronized (region) {
                    isProperLoadState = (region.getLoadState() == 2);
                    if (isProperLoadState) {
                        region.registerVisit();
                    }
                    regionIsResting = region.isResting();
                    if (regionIsResting) {
                        region.setBeingWritten(true);
                        tileChunk = region.getChunk(tileChunkLocalX, tileChunkLocalZ);
                        if (isProperLoadState && tileChunk == null) {
                            region.setChunk(tileChunkLocalX, tileChunkLocalZ, tileChunk = new MapTileChunk(region, tileChunkX, tileChunkZ));
                            tileChunk.setLoadState((byte)2);
                            region.setAllCachePrepared(false);
                            createdTileChunk = true;
                        }
                        if (!region.isNormalMapData()) {
                            region.getDim().getLayeredMapRegions().applyToEachLoadedLayer(this.otherLayerNotifier.get(region));
                        }
                    }
                }
                if (regionIsResting && isProperLoadState) {
                    if (tileChunk != null && tileChunk.getLoadState() == 2) {
                        if (!tileChunk.getLeafTexture().shouldUpload()) {
                            final boolean cave = caveStart != Integer.MAX_VALUE;
                            final boolean fullCave = caveStart == Integer.MIN_VALUE;
                            int lowH = 0;
                            if (cave && !fullCave) {
                                lowH = caveStart + 1 - caveDepth;
                                if (lowH < 0) {
                                    lowH = 0;
                                }
                            }
                            if (chunkX >= this.playerChunkX - distance && chunkX <= this.playerChunkX + distance && chunkZ >= this.playerChunkZ - distance && chunkZ <= this.playerChunkZ + distance) {
                                final Chunk chunk = world.func_72964_e(chunkX, chunkZ);
                                MapTile mapTile = tileChunk.getTile(this.insideX, this.insideZ);
                                boolean chunkUpdated = false;
                                try {
                                    chunkUpdated = (chunk != null && (mapTile == null || mapTile.getWrittenCaveStart() != caveStart || mapTile.getWrittenCaveDepth() != caveDepth || !(boolean)XaeroWorldMapCore.chunkCleanField.get(chunk)));
                                }
                                catch (IllegalArgumentException | IllegalAccessException ex2) {
                                    final Exception ex;
                                    final Exception e = ex;
                                    throw new RuntimeException(e);
                                }
                                if (chunkUpdated && chunk.func_177410_o()) {
                                    boolean connectedToOthers = false;
                                Label_0549:
                                    for (int i = -1; i < 2; ++i) {
                                        for (int j = -1; j < 2; ++j) {
                                            if (i != 0 || j != 0) {
                                                final Chunk neighbor = world.func_72964_e(chunkX + i, chunkZ + j);
                                                if (neighbor != null && neighbor.func_177410_o()) {
                                                    connectedToOthers = true;
                                                    break Label_0549;
                                                }
                                            }
                                        }
                                    }
                                    if (connectedToOthers && ((mapTile == null && loadChunks) || (mapTile != null && updateChunks && (!onlyLoad || mapTile.getWrittenCaveStart() != caveStart || mapTile.getWrittenCaveDepth() != caveDepth)))) {
                                        wasSkipped = false;
                                        if (mapTile == null) {
                                            mapTile = this.mapProcessor.getTilePool().get(this.mapProcessor.getCurrentDimension(), chunkX, chunkZ);
                                            tileChunk.setChanged(true);
                                        }
                                        final MapTileChunk prevTileChunk = tileChunk.getNeighbourTileChunk(0, -1, this.mapProcessor, false);
                                        final MapTileChunk prevTileChunkDiagonal = tileChunk.getNeighbourTileChunk(-1, -1, this.mapProcessor, false);
                                        final MapTileChunk prevTileChunkHorisontal = tileChunk.getNeighbourTileChunk(-1, 0, this.mapProcessor, false);
                                        final int sectionBasedHeight = this.getSectionBasedHeight(chunk, 64);
                                        MapTile bottomTile = (this.insideZ < 3) ? tileChunk.getTile(this.insideX, this.insideZ + 1) : null;
                                        MapTile rightTile = (this.insideX < 3) ? tileChunk.getTile(this.insideX + 1, this.insideZ) : null;
                                        boolean triedFetchingBottomChunk = false;
                                        boolean triedFetchingRightChunk = false;
                                        for (int x = 0; x < 16; ++x) {
                                            for (int z = 0; z < 16; ++z) {
                                                final int mappedHeight = chunk.func_76611_b(x, z);
                                                int startHeight;
                                                if (cave && !fullCave) {
                                                    startHeight = caveStart;
                                                }
                                                else if (ignoreHeightmaps || mappedHeight == -1) {
                                                    startHeight = sectionBasedHeight;
                                                }
                                                else {
                                                    startHeight = mappedHeight;
                                                }
                                                final MapBlock currentPixel = mapTile.isLoaded() ? mapTile.getBlock(x, z) : null;
                                                this.loadPixel(world, this.loadingObject, currentPixel, chunk, x, z, startHeight, lowH, cave, fullCave, mappedHeight, mapTile.wasWrittenOnce(), ignoreHeightmaps, flowers, mutableBlockPos3);
                                                this.loadingObject.fixHeightType(x, z, mapTile, tileChunk, prevTileChunk, prevTileChunkDiagonal, prevTileChunkHorisontal, this.loadingObject.getEffectiveHeight(this.blockStateShortShapeCache, updateConfig), true, this.blockStateShortShapeCache, updateConfig);
                                                final boolean equalsSlopesExcluded = this.loadingObject.equalsSlopesExcluded(currentPixel);
                                                final boolean fullyEqual = this.loadingObject.equals(currentPixel, equalsSlopesExcluded);
                                                if (!fullyEqual) {
                                                    final MapBlock loadedBlock = this.loadingObject;
                                                    mapTile.setBlock(x, z, loadedBlock);
                                                    if (currentPixel != null) {
                                                        this.loadingObject = currentPixel;
                                                    }
                                                    else {
                                                        this.loadingObject = new MapBlock();
                                                    }
                                                    if (!equalsSlopesExcluded) {
                                                        tileChunk.setChanged(true);
                                                        final boolean zEdge = z == 15;
                                                        final boolean xEdge = x == 15;
                                                        if ((zEdge || xEdge) && (currentPixel == null || currentPixel.getEffectiveHeight(this.blockStateShortShapeCache, updateConfig) != loadedBlock.getEffectiveHeight(this.blockStateShortShapeCache, updateConfig))) {
                                                            if (zEdge) {
                                                                if (!triedFetchingBottomChunk && bottomTile == null && this.insideZ == 3 && tileChunkLocalZ < 7) {
                                                                    bottomChunk = region.getChunk(tileChunkLocalX, tileChunkLocalZ + 1);
                                                                    triedFetchingBottomChunk = true;
                                                                    bottomTile = ((bottomChunk != null) ? bottomChunk.getTile(this.insideX, 0) : null);
                                                                    if (bottomTile != null) {
                                                                        bottomChunk.setChanged(true);
                                                                    }
                                                                }
                                                                if (bottomTile != null && bottomTile.isLoaded()) {
                                                                    bottomTile.getBlock(x, 0).setSlopeUnknown(true);
                                                                    if (!xEdge) {
                                                                        bottomTile.getBlock(x + 1, 0).setSlopeUnknown(true);
                                                                    }
                                                                }
                                                                if (xEdge) {
                                                                    this.updateBottomRightTile(region, tileChunk, bottomChunk, tileChunkLocalX, tileChunkLocalZ);
                                                                }
                                                            }
                                                            else if (xEdge) {
                                                                if (!triedFetchingRightChunk && rightTile == null && this.insideX == 3 && tileChunkLocalX < 7) {
                                                                    this.rightChunk = region.getChunk(tileChunkLocalX + 1, tileChunkLocalZ);
                                                                    triedFetchingRightChunk = true;
                                                                    rightTile = ((this.rightChunk != null) ? this.rightChunk.getTile(0, this.insideZ) : null);
                                                                    if (rightTile != null) {
                                                                        this.rightChunk.setChanged(true);
                                                                    }
                                                                }
                                                                if (rightTile != null && rightTile.isLoaded()) {
                                                                    rightTile.getBlock(0, z + 1).setSlopeUnknown(true);
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        mapTile.setWorldInterpretationVersion(1);
                                        if (mapTile.getWrittenCaveStart() != caveStart) {
                                            tileChunk.setChanged(true);
                                        }
                                        mapTile.setWrittenCave(caveStart, caveDepth);
                                        tileChunk.setTile(this.insideX, this.insideZ, mapTile, this.blockStateShortShapeCache);
                                        mapTile.setWrittenOnce(true);
                                        mapTile.setLoaded(true);
                                        ReflectionUtils.setReflectFieldValue((Object)chunk, XaeroWorldMapCore.chunkCleanField, (Object)true);
                                    }
                                }
                            }
                        }
                        if (createdTileChunk) {
                            if (tileChunk.includeInSave()) {
                                tileChunk.setHasHadTerrain();
                            }
                            this.mapProcessor.getMapRegionHighlightsPreparer().prepare(region, tileChunkLocalX, tileChunkLocalZ, false);
                            if (!tileChunk.includeInSave() && !tileChunk.hasHighlightsIfUndiscovered()) {
                                region.setChunk(tileChunkLocalX, tileChunkLocalZ, null);
                                tileChunk = null;
                            }
                        }
                    }
                    if (tileChunk != null && this.insideX == 3 && this.insideZ == 3 && tileChunk.wasChanged()) {
                        tileChunk.updateBuffers(this.mapProcessor, biomeColorCalculator, overlayManager, detailedDebug, this.blockStateShortShapeCache, updateConfig);
                        if (bottomChunk == null && tileChunkLocalZ < 7) {
                            bottomChunk = region.getChunk(tileChunkLocalX, tileChunkLocalZ + 1);
                        }
                        if (this.rightChunk == null && tileChunkLocalX < 7) {
                            this.rightChunk = region.getChunk(tileChunkLocalX + 1, tileChunkLocalZ);
                        }
                        if (this.bottomRightChunk == null && tileChunkLocalX < 7 && tileChunkLocalZ < 7) {
                            this.bottomRightChunk = region.getChunk(tileChunkLocalX + 1, tileChunkLocalZ + 1);
                        }
                        if (bottomChunk != null && bottomChunk.wasChanged()) {
                            bottomChunk.updateBuffers(this.mapProcessor, biomeColorCalculator, overlayManager, detailedDebug, this.blockStateShortShapeCache, updateConfig);
                            bottomChunk.setChanged(false);
                        }
                        if (this.rightChunk != null && this.rightChunk.wasChanged()) {
                            this.rightChunk.setToUpdateBuffers(true);
                            this.rightChunk.setChanged(false);
                        }
                        if (this.bottomRightChunk != null && this.bottomRightChunk.wasChanged()) {
                            this.bottomRightChunk.setToUpdateBuffers(true);
                            this.bottomRightChunk.setChanged(false);
                        }
                        tileChunk.setChanged(false);
                    }
                }
            }
            else {
                this.insideX = 3;
                this.insideZ = 3;
            }
        }
        ++this.insideZ;
        if (this.insideZ > 3) {
            this.insideZ = 0;
            ++this.insideX;
            if (this.insideX > 3) {
                this.insideX = 0;
                ++this.Z;
                if (this.Z > this.endTileChunkZ - this.startTileChunkZ) {
                    this.Z = 0;
                    ++this.X;
                    if (this.X > this.endTileChunkX - this.startTileChunkX) {
                        this.X = 0;
                        ++this.updateCounter;
                    }
                }
            }
        }
        return wasSkipped;
    }
    
    public void updateBottomRightTile(final MapRegion region, final MapTileChunk tileChunk, final MapTileChunk bottomChunk, final int tileChunkLocalX, final int tileChunkLocalZ) {
        MapTile bottomRightTile = (this.insideX < 3 && this.insideZ < 3) ? tileChunk.getTile(this.insideX + 1, this.insideZ + 1) : null;
        if (bottomRightTile == null) {
            if (this.insideX == 3 && tileChunkLocalX < 7) {
                if (this.insideZ == 3) {
                    if (tileChunkLocalZ < 7) {
                        this.bottomRightChunk = region.getChunk(tileChunkLocalX + 1, tileChunkLocalZ + 1);
                    }
                    bottomRightTile = ((this.bottomRightChunk != null) ? this.bottomRightChunk.getTile(0, 0) : null);
                    if (bottomRightTile != null) {
                        this.bottomRightChunk.setChanged(true);
                    }
                }
                else {
                    if (this.rightChunk == null) {
                        this.rightChunk = region.getChunk(tileChunkLocalX + 1, tileChunkLocalZ);
                    }
                    bottomRightTile = ((this.rightChunk != null) ? this.rightChunk.getTile(0, this.insideZ + 1) : null);
                    if (bottomRightTile != null) {
                        this.rightChunk.setChanged(true);
                    }
                }
            }
            else if (this.insideX != 3 && this.insideZ == 3 && tileChunkLocalZ < 7) {
                bottomRightTile = ((bottomChunk != null) ? bottomChunk.getTile(this.insideX + 1, 0) : null);
                if (bottomRightTile != null) {
                    bottomChunk.setChanged(true);
                }
            }
        }
        if (bottomRightTile != null && bottomRightTile.isLoaded()) {
            bottomRightTile.getBlock(0, 0).setSlopeUnknown(true);
        }
    }
    
    public int getSectionBasedHeight(final Chunk bchunk, final int startY) {
        final ExtendedBlockStorage[] sections = bchunk.func_76587_i();
        final int playerSection = startY >> 4;
        int result = -1;
        for (int i = playerSection; i < sections.length; ++i) {
            final ExtendedBlockStorage searchedSection = sections[i];
            if (searchedSection != Chunk.field_186036_a) {
                result = (i << 4) + 15;
            }
        }
        if (playerSection > 0 && result == -1) {
            for (int i = playerSection - 1; i >= 0; --i) {
                final ExtendedBlockStorage searchedSection = sections[i];
                if (searchedSection != Chunk.field_186036_a) {
                    result = (i << 4) + 15;
                    break;
                }
            }
        }
        return result;
    }
    
    public boolean isGlowing(final IBlockState state) {
        return state.func_185906_d() >= 0.5;
    }
    
    private boolean shouldOverlayCached(final IBlockState state) {
        return this.transparentCache.apply(state);
    }
    
    public boolean shouldOverlay(final IBlockState state) {
        final IBlockState blockState = state;
        if (blockState.func_177230_c() instanceof BlockAir || blockState.func_177230_c() instanceof BlockGlass || state.func_177230_c().func_180664_k() == BlockRenderLayer.TRANSLUCENT) {
            return true;
        }
        if (state.func_177230_c() instanceof BlockLiquid) {
            final int lightOpacity = state.getLightOpacity((IBlockAccess)this.mapProcessor.getWorld(), BlockPos.field_177992_a);
            return lightOpacity != 255 && lightOpacity != 0;
        }
        return false;
    }
    
    public boolean isInvisible(final IBlockState state, final Block b, final boolean flowers) {
        if (state.func_185901_i() == EnumBlockRenderType.INVISIBLE) {
            return true;
        }
        if (b == Blocks.field_150478_aa) {
            return true;
        }
        if (b == Blocks.field_150329_H) {
            return true;
        }
        if (b == Blocks.field_150359_w || b == Blocks.field_150410_aZ) {
            return true;
        }
        if (b == Blocks.field_150398_cm) {
            return true;
        }
        if ((b instanceof BlockFlower || b instanceof BlockDoublePlant) && !flowers) {
            return true;
        }
        synchronized (this.buggedStates) {
            if (this.buggedStates.contains(state)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasVanillaColor(final IBlockState state, final World world, final BlockPos pos) {
        MapColor materialColor = null;
        try {
            materialColor = state.func_185909_g((IBlockAccess)world, pos);
        }
        catch (Throwable t) {
            synchronized (this.buggedStates) {
                this.buggedStates.add(state);
            }
            WorldMap.LOGGER.info("Broken vanilla map color definition found: " + state.func_177230_c().getRegistryName());
        }
        return materialColor != null && materialColor.field_76291_p != 0;
    }
    
    private IBlockState unpackFramedBlocks(final IBlockState original, final World world, final BlockPos globalPos) {
        if (original.func_177230_c() instanceof BlockAir) {
            return original;
        }
        IBlockState result = original;
        if (SupportMods.framedBlocks() && SupportMods.supportFramedBlocks.isFrameBlock(null, original)) {
            final TileEntity tileEntity = world.func_175625_s(globalPos);
            if (tileEntity != null) {
                result = SupportMods.supportFramedBlocks.unpackFramedBlock(null, original, tileEntity);
                if (result == null || result.func_177230_c() instanceof BlockAir) {
                    result = original;
                }
            }
        }
        return result;
    }
    
    public void loadPixel(final World world, final MapBlock pixel, final MapBlock currentPixel, final Chunk bchunk, final int insideX, final int insideZ, final int highY, final int lowY, final boolean cave, final boolean fullCave, final int mappedHeight, final boolean canReuseBiomeColours, final boolean ignoreHeightmaps, final boolean flowers, final BlockPos.MutableBlockPos mutableBlockPos3) {
        pixel.prepareForWriting();
        this.overlayBuilder.startBuilding();
        final IBlockState prevOverlay = null;
        boolean underair = !cave || fullCave;
        boolean shouldEnterGround = fullCave;
        IBlockState opaqueState = null;
        byte workingLight = -1;
        final boolean worldHasSkyLight = world.field_73011_w.func_191066_m();
        byte workingSkyLight = (byte)(worldHasSkyLight ? 15 : 0);
        this.topH = lowY;
        this.mutableGlobalPos.func_181079_c((bchunk.func_76632_l().field_77276_a << 4) + insideX, lowY - 1, (bchunk.func_76632_l().field_77275_b << 4) + insideZ);
        boolean shouldExtendTillTheBottom;
        int transparentSkipY;
        int h;
        IBlockState state;
        IBlockState traceState;
        Block b;
        byte overlayLight;
        int stateId;
        for (shouldExtendTillTheBottom = false, transparentSkipY = 0, h = highY; h >= lowY; h = (shouldExtendTillTheBottom ? transparentSkipY : (h - 1))) {
            this.mutableLocalPos.func_181079_c(insideX, h, insideZ);
            this.mutableGlobalPos.func_185336_p(h);
            state = bchunk.func_177435_g((BlockPos)this.mutableLocalPos);
            if (state == null) {
                state = Blocks.field_150350_a.func_176223_P();
            }
            state = this.unpackFramedBlocks(state, world, (BlockPos)this.mutableGlobalPos);
            shouldExtendTillTheBottom = (!shouldExtendTillTheBottom && !this.overlayBuilder.isEmpty() && this.firstTransparentStateY - h >= 5);
            if (shouldExtendTillTheBottom) {
                for (transparentSkipY = h - 1; transparentSkipY >= lowY; --transparentSkipY) {
                    traceState = bchunk.func_177435_g((BlockPos)mutableBlockPos3.func_181079_c(insideX, transparentSkipY, insideZ));
                    if (traceState == null) {
                        traceState = Blocks.field_150350_a.func_176223_P();
                    }
                    if (!this.shouldOverlayCached(traceState)) {
                        break;
                    }
                }
            }
            b = state.func_177230_c();
            if (b instanceof BlockAir) {
                underair = true;
            }
            else if (underair) {
                if (!this.isInvisible(state, b, flowers)) {
                    if (cave && shouldEnterGround) {
                        if (!state.func_185904_a().func_76217_h() && !state.func_185904_a().func_76222_j() && state.func_185904_a().func_186274_m() != EnumPushReaction.DESTROY && !this.shouldOverlayCached(state)) {
                            underair = false;
                            shouldEnterGround = false;
                        }
                    }
                    else {
                        this.mutableLocalPos.func_185336_p(Math.min(255, h + 1));
                        workingLight = (byte)bchunk.func_177413_a(EnumSkyBlock.BLOCK, (BlockPos)this.mutableLocalPos);
                        if (cave && workingLight < 15 && worldHasSkyLight) {
                            if (!ignoreHeightmaps && !fullCave && highY >= mappedHeight) {
                                workingSkyLight = 15;
                            }
                            else {
                                workingSkyLight = (byte)bchunk.func_177413_a(EnumSkyBlock.SKY, (BlockPos)this.mutableLocalPos);
                            }
                        }
                        if (this.shouldOverlayCached(state)) {
                            if (h > this.topH) {
                                this.topH = h;
                            }
                            overlayLight = workingLight;
                            if (this.overlayBuilder.isEmpty()) {
                                this.firstTransparentStateY = h;
                                if (cave && workingSkyLight > overlayLight) {
                                    overlayLight = workingSkyLight;
                                }
                            }
                            if (shouldExtendTillTheBottom) {
                                this.overlayBuilder.getCurrentOverlay().increaseOpacity(Misc.getStateById(this.overlayBuilder.getCurrentOverlay().getState()).getLightOpacity((IBlockAccess)world, (BlockPos)this.mutableGlobalPos) * (h - transparentSkipY));
                            }
                            else {
                                this.writerBiomeInfoSupplier.set(currentPixel, canReuseBiomeColours);
                                stateId = Block.func_176210_f(state);
                                this.overlayBuilder.build(stateId, this.biomeBuffer, b.getLightOpacity(state, (IBlockAccess)world, (BlockPos)this.mutableGlobalPos), overlayLight, world, this.mapProcessor, (BlockPos)this.mutableGlobalPos, this.overlayBuilder.getOverlayBiome(), this.colorTypeCache, (BiomeInfoSupplier)this.writerBiomeInfoSupplier);
                            }
                        }
                        else if (this.hasVanillaColor(state, world, (BlockPos)this.mutableGlobalPos)) {
                            if (h > this.topH) {
                                this.topH = h;
                            }
                            opaqueState = state;
                            break;
                        }
                    }
                }
            }
        }
        if (h < lowY) {
            h = lowY;
        }
        state = ((opaqueState == null) ? Blocks.field_150350_a.func_176223_P() : opaqueState);
        final int stateId2 = Block.func_176210_f(state);
        this.overlayBuilder.finishBuilding(pixel);
        byte light = 0;
        if (opaqueState != null) {
            light = workingLight;
            if (cave && light < 15 && pixel.getNumberOfOverlays() == 0 && workingSkyLight > light) {
                light = workingSkyLight;
            }
        }
        else {
            h = 0;
        }
        if (!canReuseBiomeColours || currentPixel == null || currentPixel.getState() != stateId2) {
            this.colorTypeCache.getBlockBiomeColour(world, state, (BlockPos)this.mutableGlobalPos, this.biomeBuffer, -1);
        }
        else {
            this.biomeBuffer[0] = currentPixel.getColourType();
            this.biomeBuffer[1] = currentPixel.getBiome();
            this.biomeBuffer[2] = currentPixel.getCustomColour();
        }
        if (this.overlayBuilder.getOverlayBiome() != -1) {
            this.biomeBuffer[1] = this.overlayBuilder.getOverlayBiome();
        }
        final boolean glowing = this.isGlowing(state);
        pixel.write(stateId2, h, this.topH, this.biomeBuffer, light, glowing, cave);
    }
    
    public int loadBlockColourFromTexture(final int stateId, final boolean convert, final World world, final BlockPos globalPos) {
        if (this.clearCachedColours) {
            this.textureColours.clear();
            this.blockColours.clear();
            this.blockTintIndices.clear();
            this.lastBlockStateForTextureColor = -1;
            this.lastBlockStateForTextureColorResult = -1;
            this.clearCachedColours = false;
            if (WorldMapClientConfigUtils.getDebug()) {
                WorldMap.LOGGER.info("Xaero's World Map cache cleared!");
            }
        }
        if (stateId == this.lastBlockStateForTextureColor) {
            return this.lastBlockStateForTextureColorResult;
        }
        Integer c = this.blockColours.get(stateId);
        int red = 0;
        int green = 0;
        int blue = 0;
        int alpha = 0;
        final IBlockState state = Misc.getStateById(stateId);
        final Block b = state.func_177230_c();
        if (c == null) {
            String name = null;
            int tintIndex = -1;
            try {
                if (state instanceof IExtendedBlockState) {
                    final ResourceLocation blockResource = (ResourceLocation)Block.field_149771_c.func_177774_c((Object)b);
                    if (blockResource == null || !blockResource.func_110624_b().equals("minecraft")) {
                        final Collection<IUnlistedProperty<?>> unlitedPropertyKeys = (Collection<IUnlistedProperty<?>>)((IExtendedBlockState)state).getUnlistedNames();
                        for (final IUnlistedProperty<?> p : unlitedPropertyKeys) {
                            if (((IExtendedBlockState)state).getValue((IUnlistedProperty)p) == null) {
                                throw new SilentException("Didn't pass the unlisted property check.");
                            }
                        }
                    }
                }
                List<BakedQuad> upQuads = null;
                final BlockModelShapes bms = Minecraft.func_71410_x().func_175602_ab().func_175023_a();
                if (convert) {
                    upQuads = (List<BakedQuad>)bms.func_178125_b(state).func_188616_a(state, EnumFacing.UP, 0L);
                }
                TextureAtlasSprite texture;
                if (upQuads == null || upQuads.isEmpty() || upQuads.get(0).func_187508_a() == bms.func_178126_b().func_174952_b().func_174944_f()) {
                    texture = bms.func_178122_a(state);
                    tintIndex = 0;
                }
                else {
                    texture = upQuads.get(0).func_187508_a();
                    tintIndex = upQuads.get(0).func_178211_c();
                }
                if (texture == null) {
                    throw new SilentException("No texture for " + state);
                }
                name = texture.func_94215_i() + ".png";
                if (b instanceof BlockOre && b != Blocks.field_150449_bY) {
                    name = "minecraft:blocks/stone.png";
                }
                c = -1;
                String[] args = name.split(":");
                if (args.length < 2) {
                    MapWriter.DEFAULT_RESOURCE[1] = args[0];
                    args = MapWriter.DEFAULT_RESOURCE;
                }
                final Integer cachedColour = this.textureColours.get(name);
                if (cachedColour == null) {
                    final ResourceLocation location = new ResourceLocation(args[0], "textures/" + args[1]);
                    final IResource resource = Minecraft.func_71410_x().func_110442_L().func_110536_a(location);
                    if (resource == null) {
                        throw new SilentException("No texture " + location);
                    }
                    final InputStream input = resource.func_110527_b();
                    final BufferedImage img = TextureUtil.func_177053_a(input);
                    red = 0;
                    green = 0;
                    blue = 0;
                    int total = 0;
                    final int ts = Math.min(img.getWidth(), img.getHeight());
                    if (ts > 0) {
                        final int diff = Math.max(1, Math.min(4, ts / 8));
                        final int parts = ts / diff;
                        final Raster raster = img.getData();
                        int[] colorHolder = null;
                        for (int i = 0; i < parts; ++i) {
                            for (int j = 0; j < parts; ++j) {
                                int rgb;
                                if (img.getColorModel().getNumComponents() < 3) {
                                    colorHolder = raster.getPixel(i * diff, j * diff, colorHolder);
                                    final int sample = colorHolder[0] & 0xFF;
                                    int a = 255;
                                    if (colorHolder.length > 1) {
                                        a = colorHolder[1];
                                    }
                                    rgb = (a << 24 | sample << 16 | sample << 8 | sample);
                                }
                                else {
                                    rgb = img.getRGB(i * diff, j * diff);
                                }
                                final int a2 = rgb >> 24 & 0xFF;
                                if (rgb != 0 && a2 != 0) {
                                    red += (rgb >> 16 & 0xFF);
                                    green += (rgb >> 8 & 0xFF);
                                    blue += (rgb & 0xFF);
                                    alpha += a2;
                                    ++total;
                                }
                            }
                        }
                    }
                    input.close();
                    if (total == 0) {
                        total = 1;
                    }
                    red /= total;
                    green /= total;
                    blue /= total;
                    alpha /= total;
                    if (convert && red == 0 && green == 0 && blue == 0) {
                        throw new SilentException("Black texture " + ts);
                    }
                    c = (alpha << 24 | red << 16 | green << 8 | blue);
                    this.textureColours.put(name, c);
                }
                else {
                    c = cachedColour;
                }
            }
            catch (FileNotFoundException e2) {
                if (convert) {
                    return this.loadBlockColourFromTexture(stateId, false, world, globalPos);
                }
                WorldMap.LOGGER.info("Block file not found: " + Block.field_149771_c.func_177774_c((Object)b));
                c = 0;
                if (state != null && state.func_185909_g((IBlockAccess)world, globalPos) != null) {
                    c = state.func_185909_g((IBlockAccess)world, globalPos).field_76291_p;
                }
                if (name != null) {
                    this.textureColours.put(name, c);
                }
            }
            catch (Exception e) {
                WorldMap.LOGGER.info("Exception when loading " + Block.field_149771_c.func_177774_c((Object)b) + " texture, using material colour.");
                c = 0;
                if (state.func_185909_g((IBlockAccess)world, globalPos) != null) {
                    c = state.func_185909_g((IBlockAccess)world, globalPos).field_76291_p;
                }
                if (name != null) {
                    this.textureColours.put(name, c);
                }
                if (e instanceof SilentException) {
                    WorldMap.LOGGER.info(e.getMessage());
                }
                else {
                    WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
                }
            }
            if (c != null) {
                this.blockColours.put(stateId, c);
                this.blockTintIndices.put((Object)state, tintIndex);
            }
        }
        this.lastBlockStateForTextureColor = stateId;
        this.lastBlockStateForTextureColorResult = c;
        return c;
    }
    
    public long getUpdateCounter() {
        return this.updateCounter;
    }
    
    public void resetPosition() {
        this.X = 0;
        this.Z = 0;
        this.insideX = 0;
        this.insideZ = 0;
    }
    
    public void requestCachedColoursClear() {
        this.clearCachedColours = true;
    }
    
    public BlockStateColorTypeCache getColorTypeCache() {
        return this.colorTypeCache;
    }
    
    public void setMapProcessor(final MapProcessor mapProcessor) {
        this.mapProcessor = mapProcessor;
    }
    
    public MapProcessor getMapProcessor() {
        return this.mapProcessor;
    }
    
    public void setDirtyInWriteDistance(final EntityPlayer player, final World level) {
        final int writeDistance = this.getWriteDistance();
        final int playerChunkX = player.func_180425_c().func_177958_n() >> 4;
        final int playerChunkZ = player.func_180425_c().func_177952_p() >> 4;
        final int startChunkX = playerChunkX - writeDistance;
        final int startChunkZ = playerChunkZ - writeDistance;
        final int endChunkX = playerChunkX + writeDistance;
        final int endChunkZ = playerChunkZ + writeDistance;
        for (int x = startChunkX; x < endChunkX; ++x) {
            for (int z = startChunkZ; z < endChunkZ; ++z) {
                final Chunk chunk = level.func_72964_e(x, z);
                if (chunk != null) {
                    try {
                        XaeroWorldMapCore.chunkCleanField.set(chunk, false);
                    }
                    catch (IllegalArgumentException | IllegalAccessException ex2) {
                        final Exception ex;
                        final Exception e = ex;
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
    
    public int getBlockTintIndex(final IBlockState state) {
        return this.blockTintIndices.getInt((Object)state);
    }
    
    static {
        DEFAULT_RESOURCE = new String[] { "minecraft", "" };
    }
    
    public final class OtherLayerNotifier implements BiConsumer<Integer, MapLayer>
    {
        private MapRegion region;
        
        public OtherLayerNotifier get(final MapRegion region) {
            this.region = region;
            return this;
        }
        
        @Override
        public void accept(final Integer i, final MapLayer u) {
            if (i != this.region.getCaveLayer()) {
                final MapRegion sameRegionAnotherLayer = MapWriter.this.mapProcessor.getLeafMapRegion((int)i, this.region.getRegionX(), this.region.getRegionZ(), true);
                sameRegionAnotherLayer.setOutdatedWithOtherLayers(true);
                sameRegionAnotherLayer.setHasHadTerrain();
            }
        }
    }
}
