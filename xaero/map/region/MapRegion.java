//Decompiled by Procyon!

package xaero.map.region;

import net.minecraft.util.math.*;
import xaero.map.world.*;
import xaero.map.config.util.*;
import xaero.map.*;
import java.io.*;
import java.nio.file.*;
import xaero.map.file.*;
import java.util.*;
import xaero.map.misc.*;
import xaero.map.common.config.option.*;
import xaero.lib.common.config.option.*;
import xaero.map.region.texture.*;

public class MapRegion extends LeveledRegion<LeafRegionTexture> implements MapRegionInfo
{
    public static final int SIDE_LENGTH = 8;
    private Boolean saveExists;
    private File regionFile;
    private boolean beingWritten;
    private long lastVisited;
    private byte loadState;
    private int version;
    private int initialVersion;
    private int reloadVersion;
    private final boolean normalMapData;
    private MapTileChunk[][] chunks;
    private boolean isRefreshing;
    public final Object writerThreadPauseSync;
    private int pauseWriting;
    public boolean loadingPrioritized;
    public int loadingNeededForBranchLevel;
    private int cacheHashCode;
    private int caveStart;
    private int caveDepth;
    private int highlightsHash;
    private int targetHighlightsHash;
    private boolean hasHadTerrain;
    private boolean lookedForCache;
    private boolean outdatedWithOtherLayers;
    private boolean resaving;
    private int[] pixelResultBuffer;
    private BlockPos.MutableBlockPos mutableGlobalPos;
    
    public MapRegion(final String worldId, final String dimId, final String mwId, final MapDimension dim, final int x, final int z, final int caveLayer, final int initialVersion, final boolean normalMapData) {
        super(worldId, dimId, mwId, dim, 0, x, z, caveLayer, (BranchLeveledRegion)null);
        this.version = -1;
        this.chunks = new MapTileChunk[8][8];
        this.writerThreadPauseSync = new Object();
        this.pixelResultBuffer = new int[4];
        this.mutableGlobalPos = new BlockPos.MutableBlockPos();
        this.initialVersion = initialVersion;
        this.normalMapData = normalMapData;
        this.lastSaveTime = System.currentTimeMillis();
    }
    
    public void setParent(final BranchLeveledRegion parent) {
        this.parent = parent;
    }
    
    public void destroyBufferUpdateObjects() {
        this.pixelResultBuffer = null;
        this.mutableGlobalPos = null;
    }
    
    public void restoreBufferUpdateObjects() {
        this.pixelResultBuffer = new int[4];
        this.mutableGlobalPos = new BlockPos.MutableBlockPos();
    }
    
    public void requestRefresh(final MapProcessor mapProcessor) {
        this.requestRefresh(mapProcessor, true);
    }
    
    public void requestRefresh(final MapProcessor mapProcessor, final boolean prepareHighlights) {
        if (!this.isRefreshing) {
            this.isRefreshing = true;
            mapProcessor.addToRefresh(this, prepareHighlights);
            if (WorldMapClientConfigUtils.getDebug()) {
                WorldMap.LOGGER.info(String.format("Requesting refresh for region %s.", this));
            }
        }
    }
    
    public void cancelRefresh(final MapProcessor mapProcessor) {
        if (this.isRefreshing) {
            this.isRefreshing = false;
            mapProcessor.removeToRefresh(this);
            if (WorldMapClientConfigUtils.getDebug()) {
                WorldMap.LOGGER.info(String.format("Canceling refresh for region %s.", this));
            }
        }
    }
    
    protected int distanceFromPlayer() {
        return this.leafDistanceFromPlayer();
    }
    
    protected int leafDistanceFromPlayer() {
        return super.leafDistanceFromPlayer() + ((MapRegion.comparisonLevel == 0) ? (this.loadState * 3 / 2) : 0);
    }
    
    public void clean(final MapProcessor mapProcessor) {
        for (int i = 0; i < this.chunks.length; ++i) {
            for (int j = 0; j < this.chunks.length; ++j) {
                final MapTileChunk c = this.chunks[i][j];
                if (c != null) {
                    c.clean(mapProcessor);
                    this.chunks[i][j] = null;
                }
            }
        }
    }
    
    protected void writeCacheMetaData(final DataOutputStream output, final byte[] usableBuffer, final byte[] integerByteBuffer) throws IOException {
        output.writeInt(this.cacheHashCode);
        output.writeInt(this.reloadVersion);
        output.writeInt(this.getHighlightsHash());
        output.writeInt(this.getCaveStart());
        output.writeInt(this.caveDepth);
        super.writeCacheMetaData(output, usableBuffer, integerByteBuffer);
    }
    
    protected void readCacheMetaData(final DataInputStream input, final int cacheSaveVersion, final byte[] usableBuffer, final byte[] integerByteBuffer, final boolean[][] textureLoaded, final MapProcessor mapProcessor) throws IOException {
        if (cacheSaveVersion >= 9) {
            final int saveHashCode = input.readInt();
            this.setCacheHashCode(saveHashCode);
        }
        if (cacheSaveVersion >= 11) {
            this.reloadVersion = input.readInt();
        }
        if (cacheSaveVersion >= 18) {
            this.setHighlightsHash(input.readInt());
        }
        if (cacheSaveVersion >= 23) {
            this.setCaveStart(input.readInt());
        }
        if (cacheSaveVersion >= 24) {
            this.caveDepth = input.readInt();
        }
        super.readCacheMetaData(input, cacheSaveVersion, usableBuffer, integerByteBuffer, textureLoaded, mapProcessor);
    }
    
    public void clearRegion(final MapProcessor mapProcessor) {
        this.setRecacheHasBeenRequested(false, "clearing");
        this.cancelRefresh(mapProcessor);
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                final MapTileChunk c = this.getChunk(i, j);
                if (c != null) {
                    c.setLoadState((byte)3);
                    this.setLoadState((byte)3);
                    c.clean(mapProcessor);
                }
            }
        }
        if (!mapProcessor.getMapSaveLoad().toCacheContains((LeveledRegion)this)) {
            this.deleteBuffers();
        }
        this.deleteGLBuffers();
        this.setLoadState((byte)4);
        if (WorldMapClientConfigUtils.getDebug()) {
            WorldMap.LOGGER.info("Cleared region! " + this + " " + this.getWorldId() + " " + this.getDimId() + " " + this.getMwId());
        }
    }
    
    public boolean isResting() {
        return this.loadState != 3 && this.loadState != 1 && !this.recacheHasBeenRequested;
    }
    
    public String getWorldId() {
        return this.worldId;
    }
    
    public String getDimId() {
        return this.dimId;
    }
    
    public String getMwId() {
        return this.mwId;
    }
    
    public int getVersion() {
        return this.version;
    }
    
    public void setVersion(final int version) {
        this.version = version;
        if (WorldMap.detailed_debug) {
            WorldMap.LOGGER.info("Version set to " + version + " by for " + this);
        }
    }
    
    public boolean isBeingWritten() {
        return this.beingWritten;
    }
    
    public void setBeingWritten(final boolean beingWritten) {
        this.beingWritten = beingWritten;
    }
    
    public byte getLoadState() {
        return this.loadState;
    }
    
    public void setLoadState(final byte loadState) {
        this.loadState = loadState;
    }
    
    public MapTileChunk getChunk(final int x, final int z) {
        return this.chunks[x][z];
    }
    
    public void setChunk(final int x, final int z, final MapTileChunk chunk) {
        this.chunks[x][z] = chunk;
    }
    
    public int getInitialVersion() {
        return this.initialVersion;
    }
    
    public void setInitialVersion(final int initialVersion) {
        this.initialVersion = initialVersion;
    }
    
    public int[] getPixelResultBuffer() {
        return this.pixelResultBuffer;
    }
    
    public BlockPos.MutableBlockPos getMutableGlobalPos() {
        return this.mutableGlobalPos;
    }
    
    public File getRegionFile() {
        return this.regionFile;
    }
    
    public void setRegionFile(final File loadedFromFile) {
        this.regionFile = loadedFromFile;
    }
    
    public Boolean getSaveExists() {
        return this.saveExists;
    }
    
    public void setSaveExists(final Boolean saveExists) {
        this.saveExists = saveExists;
    }
    
    public long getLastSaveTime() {
        return this.lastSaveTime;
    }
    
    public void setLastSaveTime(final long lastSaveTime) {
        this.lastSaveTime = lastSaveTime;
    }
    
    public boolean isRefreshing() {
        return this.isRefreshing;
    }
    
    public void setRefreshing(final boolean isRefreshing) {
        this.isRefreshing = isRefreshing;
    }
    
    public boolean isWritingPaused() {
        return this.pauseWriting > 0;
    }
    
    public void pushWriterPause() {
        synchronized (this.writerThreadPauseSync) {
            ++this.pauseWriting;
        }
    }
    
    public void popWriterPause() {
        synchronized (this.writerThreadPauseSync) {
            --this.pauseWriting;
        }
    }
    
    public boolean hasVersion() {
        return this.version != -1;
    }
    
    public boolean isNormalMapData() {
        return this.normalMapData;
    }
    
    public long getLastVisited() {
        return this.lastVisited;
    }
    
    public long getTimeSinceVisit() {
        return System.currentTimeMillis() - this.lastVisited;
    }
    
    public void registerVisit() {
        this.lastVisited = System.currentTimeMillis();
    }
    
    public int countChunks() {
        int count = 0;
        for (int i = 0; i < this.chunks.length; ++i) {
            for (int j = 0; j < this.chunks.length; ++j) {
                final MapTileChunk chunk = this.chunks[i][j];
                if (chunk != null) {
                    ++count;
                }
            }
        }
        return count;
    }
    
    protected void putLeaf(final int X, final int Z, final MapRegion leaf) {
    }
    
    public void putTexture(final int x, final int y, final LeafRegionTexture texture) {
        throw new RuntimeException(new IllegalAccessException());
    }
    
    public LeafRegionTexture getTexture(final int x, final int y) {
        final MapTileChunk chunk = this.chunks[x][y];
        if (chunk != null) {
            return chunk.getLeafTexture();
        }
        return null;
    }
    
    protected LeveledRegion<?> get(final int leveledX, final int leveledZ, final int level) {
        if (level == 0) {
            return this;
        }
        throw new RuntimeException(new IllegalArgumentException());
    }
    
    protected boolean remove(final int leveledX, final int leveledZ, final int level) {
        throw new RuntimeException(new IllegalAccessException());
    }
    
    public boolean loadingAnimation() {
        return this.loadState < 2;
    }
    
    public boolean cleanAndCacheRequestsBlocked() {
        return this.isRefreshing;
    }
    
    public boolean shouldBeProcessed() {
        return this.loadState > 0 && this.loadState < 4;
    }
    
    public boolean isLoaded() {
        return this.loadState >= 2;
    }
    
    public boolean shouldEndProcessingAfterUpload() {
        return this.loadState == 3;
    }
    
    public void onProcessingEnd() {
        this.loadState = 4;
        this.destroyBufferUpdateObjects();
    }
    
    public void preCache() {
        this.pushWriterPause();
    }
    
    public void postCache(final File permFile, final MapSaveLoad mapSaveLoad, final boolean successfullySaved) throws IOException {
        this.popWriterPause();
        if (permFile != null && successfullySaved) {
            final Path outdatedCacheFile = permFile.toPath().resolveSibling(permFile.getName() + ".outdated");
            if (Files.exists(outdatedCacheFile, new LinkOption[0])) {
                Files.deleteIfExists(outdatedCacheFile);
            }
        }
    }
    
    public boolean skipCaching(final int globalVersion) {
        return this.getVersion() != globalVersion || !this.hasHadTerrain;
    }
    
    public File findCacheFile(final MapSaveLoad mapSaveLoad) throws IOException {
        return mapSaveLoad.getCacheFile((MapRegionInfo)this, this.caveLayer, false, false);
    }
    
    public void onCurrentDimFinish(final MapSaveLoad mapSaveLoad, final MapProcessor mapProcessor) {
        if (this.getLoadState() == 2) {
            if (this.isBeingWritten()) {
                mapSaveLoad.getToSave().add(this);
            }
            else {
                this.clearRegion(mapProcessor);
            }
        }
        else {
            this.setBeingWritten(false);
            if (this.isRefreshing()) {
                throw new RuntimeException("Detected non-loadstate 2 region with refreshing value being true.");
            }
        }
    }
    
    public void onLimiterRemoval(final MapProcessor mapProcessor) {
        this.pushWriterPause();
        final RegionDetection restoredDetection = new RegionDetection(this.getWorldId(), this.getDimId(), this.getMwId(), this.getRegionX(), this.getRegionZ(), this.getRegionFile(), mapProcessor.getGlobalVersion(), this.hasHadTerrain);
        restoredDetection.transferInfoFrom(this);
        this.dim.getLayeredMapRegions().getLayer(this.caveLayer).addRegionDetection(restoredDetection);
        mapProcessor.removeMapRegion((LeveledRegion)this);
    }
    
    public void afterLimiterRemoval(final MapProcessor mapProcessor) {
        mapProcessor.getMapSaveLoad().removeToLoad(this);
        this.popWriterPause();
    }
    
    public void addDebugLines(final List<String> debugLines, final MapProcessor mapProcessor, final int textureX, final int textureY) {
        super.addDebugLines((List)debugLines, mapProcessor, textureX, textureY);
        debugLines.add("paused: " + this.isWritingPaused() + " loadingNeededForBranchLevel: " + this.loadingNeededForBranchLevel);
        debugLines.add(String.format("writing: %s refreshing: %s", this.isBeingWritten(), this.isRefreshing()));
        debugLines.add("saveExists: " + this.getSaveExists());
        final int targetRegionHighlightsHash = this.getDim().getHighlightHandler().getRegionHash(this.getRegionX(), this.getRegionZ());
        debugLines.add(String.format("reg loadState: %s version: %d/%d hash: %d reloadVersion: %d highlights: %d/%d terrain: %s", this.getLoadState(), this.getVersion(), mapProcessor.getGlobalVersion(), this.getCacheHashCode(), this.getReloadVersion(), this.getHighlightsHash(), targetRegionHighlightsHash, this.hasHadTerrain));
        debugLines.add(String.format("caveStart: %s caveDepth: %s outdatedWithOtherLayers: %s", this.getCaveStart(), this.caveDepth, this.outdatedWithOtherLayers));
    }
    
    public String getExtraInfo() {
        return this.getLoadState() + " " + this.countChunks();
    }
    
    public LeafRegionTexture createTexture(final int x, final int y) {
        final MapTileChunk[] array = this.chunks[x];
        final MapTileChunk tileChunk = this.createTileChunk(x, y);
        array[y] = tileChunk;
        return tileChunk.getLeafTexture();
    }
    
    protected MapTileChunk createTileChunk(final int x, final int y) {
        return new MapTileChunk(this, this.regionX * 8 + x, this.regionZ * 8 + y);
    }
    
    public void checkForUpdates(final MapProcessor mapProcessor, final boolean prevWaitingForBranchCache, final boolean[] waitingForBranchCache, final ArrayList<BranchLeveledRegion> branchRegionBuffer, final int viewedLevel, final int minViewedLeafX, final int minViewedLeafZ, final int maxViewedLeafX, final int maxViewedLeafZ) {
        super.checkForUpdates(mapProcessor, prevWaitingForBranchCache, waitingForBranchCache, (ArrayList)branchRegionBuffer, viewedLevel, minViewedLeafX, minViewedLeafZ, maxViewedLeafX, maxViewedLeafZ);
        synchronized (this) {
            if (!this.isLoaded()) {
                this.loadingNeededForBranchLevel = viewedLevel;
            }
        }
    }
    
    public int getReloadVersion() {
        return this.reloadVersion;
    }
    
    public void setReloadVersion(final int reloadVersion) {
        this.reloadVersion = reloadVersion;
    }
    
    public void setCacheHashCode(final int cacheHashCode) {
        this.cacheHashCode = cacheHashCode;
    }
    
    public int getCacheHashCode() {
        return this.cacheHashCode;
    }
    
    public void setCaveStart(final int caveStart) {
        this.caveStart = caveStart;
    }
    
    public int getCaveStart() {
        if (this.caveLayer == Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return this.caveStart;
    }
    
    public void processWhenLoadedChunksExist(final int globalRegionCacheHashCode) {
        super.processWhenLoadedChunksExist(globalRegionCacheHashCode);
        if (this.getCacheHashCode() != 0 && this.getCacheHashCode() != globalRegionCacheHashCode) {
            this.setCacheHashCode(0);
        }
        if (this.getHighlightsHash() != 0) {
            this.updateTargetHighlightsHash();
            if (this.getHighlightsHash() != this.getTargetHighlightsHash()) {
                this.setHighlightsHash(0);
            }
        }
    }
    
    public void updateLeafTextureVersion(final int localTextureX, final int localTextureZ, final int newVersion) {
        final int oldVersion = this.leafTextureVersionSum[localTextureX][localTextureZ];
        int globalTextureX = 0;
        int globalTextureZ = 0;
        if (oldVersion != newVersion) {
            globalTextureX = (this.regionX << 3) + localTextureX;
            globalTextureZ = (this.regionZ << 3) + localTextureZ;
            this.leafTextureVersionSum[localTextureX][localTextureZ] = newVersion;
        }
        BranchLeveledRegion parentRegion = this.getParent();
        if (parentRegion != null) {
            parentRegion.setShouldCheckForUpdatesRecursive(true);
        }
        if (oldVersion != newVersion) {
            final int diff = newVersion - oldVersion;
            while (parentRegion != null) {
                final int parentLevel = parentRegion.getLevel();
                final int parentTextureX = globalTextureX >> parentLevel & 0x7;
                final int parentTextureY = globalTextureZ >> parentLevel & 0x7;
                final int[] array = parentRegion.leafTextureVersionSum[parentTextureX];
                final int n = parentTextureY;
                array[n] += diff;
                parentRegion = parentRegion.getParent();
            }
        }
    }
    
    public void onDimensionClear(final MapProcessor mapProcessor) {
        super.onDimensionClear(mapProcessor);
        if (this.loadState == 3) {
            this.clean(mapProcessor);
        }
    }
    
    public void restoreMetaData(final int[][] cachedTextureVersions, final int cacheHashCode, final int reloadVersion, final int highlightsHash, final int caveStart, final boolean outdatedWithOtherLayers, final MapProcessor mapProcessor) {
        if (cachedTextureVersions != null) {
            this.setVersion(mapProcessor.getGlobalVersion());
            this.cacheHashCode = cacheHashCode;
            this.reloadVersion = reloadVersion;
            this.highlightsHash = highlightsHash;
            this.caveStart = caveStart;
            this.outdatedWithOtherLayers = outdatedWithOtherLayers;
            for (int i = 0; i < 8; ++i) {
                for (int j = 0; j < 8; ++j) {
                    final int storedVersion = cachedTextureVersions[i][j];
                    this.updateLeafTextureVersion(i, j, this.cachedTextureVersions[i][j] = storedVersion);
                }
            }
            this.metaLoaded = true;
        }
    }
    
    public boolean loadCacheTextures(final MapProcessor mapProcessor, final boolean justMetaData, final boolean[][] textureLoaded, final int targetHighlightsHash, final boolean[] leafShouldAffectBranchesDest, final boolean[] metaLoadedDest, final int extraAttempts) {
        if (!this.hasHadTerrain) {
            this.setHighlightsHash(targetHighlightsHash);
            metaLoadedDest[0] = true;
            return justMetaData;
        }
        return super.loadCacheTextures(mapProcessor, justMetaData, textureLoaded, targetHighlightsHash, leafShouldAffectBranchesDest, metaLoadedDest, extraAttempts);
    }
    
    protected void onCacheLoadFailed(final boolean[][] textureLoaded) {
    }
    
    public void convertCacheToOutdated(final MapSaveLoad mapSaveLoad, final String reason) {
        try {
            final Path outdatedPath = Misc.convertToOutdated(this.getCacheFile().toPath(), 5);
            if (outdatedPath != null) {
                if (mapSaveLoad.removeTempCacheRequest(this.getCacheFile().toPath().toFile())) {
                    mapSaveLoad.addTempCacheRequest(outdatedPath.toFile());
                }
                this.setCacheFile(outdatedPath.toFile());
                this.setShouldCache(true, reason);
            }
        }
        catch (IOException e) {
            WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
        }
    }
    
    public boolean shouldAffectLoadingRequestFrequency() {
        return this.loadState > 2 && super.shouldAffectLoadingRequestFrequency();
    }
    
    public boolean hasRemovableSourceData() {
        return this.loadState == 2 && !this.beingWritten;
    }
    
    public int getHighlightsHash() {
        return this.highlightsHash;
    }
    
    public void setHighlightsHash(final int highlightsHash) {
        this.highlightsHash = highlightsHash;
    }
    
    protected boolean shouldLeafAffectCache(final int targetHighlightsHash) {
        return (!this.shouldCache || this.dim.getMapWorld().isCacheOnlyMode()) && this.highlightsHash != targetHighlightsHash && !this.isBeingWritten();
    }
    
    public void updateTargetHighlightsHash() {
        this.targetHighlightsHash = this.getDim().getHighlightHandler().getRegionHash(this.getRegionX(), this.getRegionZ());
    }
    
    public int getTargetHighlightsHash() {
        return this.targetHighlightsHash;
    }
    
    public void setHasHadTerrain() {
        this.hasHadTerrain = true;
    }
    
    public void unsetHasHadTerrain() {
        this.hasHadTerrain = false;
    }
    
    public boolean hasHadTerrain() {
        return this.hasHadTerrain;
    }
    
    public boolean hasLookedForCache() {
        return this.lookedForCache;
    }
    
    public void setLookedForCache(final boolean lookedForCache) {
        this.lookedForCache = lookedForCache;
    }
    
    public boolean caveStartOutdated(final int currentCaveStart, final int currentCaveDepth) {
        return !this.normalMapData && (this.outdatedWithOtherLayers || currentCaveStart != this.getCaveStart() || (currentCaveStart != Integer.MAX_VALUE && this.caveDepth != currentCaveDepth));
    }
    
    public void updateCaveMode() {
        this.caveStart = this.dim.getLayeredMapRegions().getLayer(this.caveLayer).getCaveStart();
        this.caveDepth = (int)WorldMap.INSTANCE.getConfigs().getClientConfigManager().getEffective((ConfigOption)WorldMapProfiledConfigOptions.CAVE_MODE_DEPTH);
        this.setOutdatedWithOtherLayers(false);
    }
    
    public int getUpToDateCaveStart() {
        return this.dim.getLayeredMapRegions().getLayer(this.caveLayer).getCaveStart();
    }
    
    public boolean shouldConvertCacheToOutdatedOnFinishDim() {
        return this.recacheHasBeenRequested() || this.isOutdatedWithOtherLayers();
    }
    
    public void setOutdatedWithOtherLayers(final boolean outdatedWithOtherLayers) {
        this.outdatedWithOtherLayers = outdatedWithOtherLayers;
    }
    
    public boolean isOutdatedWithOtherLayers() {
        return this.outdatedWithOtherLayers;
    }
    
    public int getCaveDepth() {
        return this.caveDepth;
    }
    
    public boolean canRequestReload_unsynced() {
        return !this.reloadHasBeenRequested() && !this.recacheHasBeenRequested() && !this.isRefreshing() && (this.getLoadState() == 0 || this.getLoadState() == 4 || (this.getLoadState() == 2 && this.isBeingWritten()));
    }
    
    public boolean isResaving() {
        return this.resaving;
    }
    
    public void setResaving(final boolean resaving) {
        this.resaving = resaving;
    }
}
