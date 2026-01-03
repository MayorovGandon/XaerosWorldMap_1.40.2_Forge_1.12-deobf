//Decompiled by Procyon!

package xaero.map.region;

import xaero.map.region.texture.*;
import xaero.map.world.*;
import xaero.map.*;
import java.io.*;
import java.util.zip.*;
import java.util.*;
import xaero.map.file.*;

public abstract class LeveledRegion<T extends RegionTexture<T>> implements Comparable<LeveledRegion<T>>
{
    public static final int SIDE_LENGTH = 8;
    private static int comparisonX;
    private static int comparisonZ;
    protected static int comparisonLevel;
    private static int comparisonLeafX;
    private static int comparisonLeafZ;
    protected BranchLeveledRegion parent;
    protected int caveLayer;
    protected int regionX;
    protected int regionZ;
    protected int level;
    private boolean allCachePrepared;
    protected boolean shouldCache;
    protected boolean recacheHasBeenRequested;
    protected boolean reloadHasBeenRequested;
    protected File cacheFile;
    protected String worldId;
    protected String dimId;
    protected String mwId;
    protected MapDimension dim;
    public int activeBranchUpdateReferences;
    public int[][] leafTextureVersionSum;
    protected int[][] cachedTextureVersions;
    protected boolean metaLoaded;
    private int distanceFromPlayerCache;
    private int leafDistanceFromPlayerCache;
    protected long lastSaveTime;
    
    public LeveledRegion(final String worldId, final String dimId, final String mwId, final MapDimension dim, final int level, final int leveledX, final int leveledZ, final int caveLayer, final BranchLeveledRegion parent) {
        this.cacheFile = null;
        this.leafTextureVersionSum = new int[8][8];
        this.cachedTextureVersions = new int[8][8];
        this.worldId = worldId;
        this.dimId = dimId;
        this.mwId = mwId;
        this.dim = dim;
        this.level = level;
        this.regionX = leveledX;
        this.regionZ = leveledZ;
        this.caveLayer = caveLayer;
        this.parent = parent;
    }
    
    public void onDimensionClear(final MapProcessor mapProcessor) {
        this.deleteTexturesAndBuffers();
    }
    
    public void deleteTexturesAndBuffers() {
        synchronized (this) {
            this.setAllCachePrepared(false);
        }
        if (this.hasTextures()) {
            for (int i = 0; i < 8; ++i) {
                for (int j = 0; j < 8; ++j) {
                    final T texture = this.getTexture(i, j);
                    if (texture != null) {
                        synchronized (this) {
                            this.setAllCachePrepared(false);
                            texture.setCachePrepared(false);
                        }
                        texture.deleteTexturesAndBuffers();
                        if (this.level > 0) {
                            this.putTexture(i, j, null);
                        }
                    }
                }
            }
        }
    }
    
    public boolean hasTextures() {
        return true;
    }
    
    public void deleteBuffers() {
        synchronized (this) {
            this.setAllCachePrepared(false);
        }
        if (this.hasTextures()) {
            for (int i = 0; i < 8; ++i) {
                for (int j = 0; j < 8; ++j) {
                    final T texture = this.getTexture(i, j);
                    if (texture != null && texture.getColorBuffer() != null) {
                        synchronized (this) {
                            this.setAllCachePrepared(false);
                            texture.setCachePrepared(false);
                        }
                        texture.setToUpload(false);
                        texture.deleteColorBuffer();
                    }
                }
            }
        }
    }
    
    public void deleteGLBuffers() {
        if (this.hasTextures()) {
            for (int i = 0; i < 8; ++i) {
                for (int j = 0; j < 8; ++j) {
                    final T texture = this.getTexture(i, j);
                    if (texture != null) {
                        texture.deletePBOs();
                    }
                }
            }
        }
    }
    
    public boolean isAllCachePrepared() {
        return this.allCachePrepared;
    }
    
    public void setAllCachePrepared(final boolean allCachePrepared) {
        if (this.allCachePrepared && !allCachePrepared && WorldMap.detailed_debug) {
            WorldMap.LOGGER.info("Cancelling cache: " + this);
        }
        this.allCachePrepared = allCachePrepared;
    }
    
    public int getRegionX() {
        return this.regionX;
    }
    
    public int getRegionZ() {
        return this.regionZ;
    }
    
    public boolean shouldCache() {
        return this.shouldCache;
    }
    
    public int getLevel() {
        return this.level;
    }
    
    public void setShouldCache(final boolean shouldCache, final String by) {
        this.shouldCache = shouldCache;
        if (WorldMap.detailed_debug) {
            WorldMap.LOGGER.info("shouldCache set to " + shouldCache + " by " + by + " for " + this);
        }
    }
    
    public boolean recacheHasBeenRequested() {
        return this.recacheHasBeenRequested;
    }
    
    public void setRecacheHasBeenRequested(final boolean recacheHasBeenRequested, final String by) {
        if (WorldMap.detailed_debug && recacheHasBeenRequested != this.recacheHasBeenRequested) {
            WorldMap.LOGGER.info("Recache set to " + recacheHasBeenRequested + " by " + by + " for " + this);
        }
        this.recacheHasBeenRequested = recacheHasBeenRequested;
    }
    
    public File getCacheFile() {
        return this.cacheFile;
    }
    
    public void setCacheFile(final File cacheFile) {
        this.cacheFile = cacheFile;
    }
    
    public MapDimension getDim() {
        return this.dim;
    }
    
    @Override
    public String toString() {
        return "(" + this.caveLayer + ") " + this.regionX + "_" + this.regionZ + " L" + this.level + " " + super.toString();
    }
    
    public boolean reloadHasBeenRequested() {
        return this.reloadHasBeenRequested;
    }
    
    public void setReloadHasBeenRequested(final boolean reloadHasBeenRequested, final String by) {
        if (WorldMap.detailed_debug && reloadHasBeenRequested != this.reloadHasBeenRequested) {
            WorldMap.LOGGER.info("Reload set to " + reloadHasBeenRequested + " by " + by + " for " + this);
        }
        this.reloadHasBeenRequested = reloadHasBeenRequested;
    }
    
    public static void setComparison(final int x, final int z, final int level, final int leafX, final int leafZ) {
        LeveledRegion.comparisonX = x;
        LeveledRegion.comparisonZ = z;
        LeveledRegion.comparisonLevel = level;
        LeveledRegion.comparisonLeafX = leafX;
        LeveledRegion.comparisonLeafZ = leafZ;
    }
    
    protected int distanceFromPlayer() {
        final int toRegionX = (this.regionX << this.level >> LeveledRegion.comparisonLevel) - LeveledRegion.comparisonX;
        final int toRegionZ = (this.regionZ << this.level >> LeveledRegion.comparisonLevel) - LeveledRegion.comparisonZ;
        return (int)Math.sqrt(toRegionX * toRegionX + toRegionZ * toRegionZ);
    }
    
    protected int leafDistanceFromPlayer() {
        final int toRegionX = (this.regionX << this.level) - LeveledRegion.comparisonLeafX;
        final int toRegionZ = (this.regionZ << this.level) - LeveledRegion.comparisonLeafZ;
        return (int)Math.sqrt(toRegionX * toRegionX + toRegionZ * toRegionZ);
    }
    
    public void calculateSortingDistance() {
        this.distanceFromPlayerCache = this.distanceFromPlayer();
        this.leafDistanceFromPlayerCache = this.leafDistanceFromPlayer();
    }
    
    protected int chunkDistanceFromPlayer() {
        final int toRegionX = (this.regionX << this.level << 5) - LeveledRegion.comparisonX;
        final int toRegionZ = (this.regionZ << this.level << 5) - LeveledRegion.comparisonZ;
        return (int)Math.sqrt(toRegionX * toRegionX + toRegionZ * toRegionZ);
    }
    
    public void calculateSortingChunkDistance() {
        this.distanceFromPlayerCache = this.chunkDistanceFromPlayer();
        this.leafDistanceFromPlayerCache = this.distanceFromPlayerCache;
    }
    
    @Override
    public int compareTo(final LeveledRegion<T> arg0) {
        if (this.level == 3 && arg0.level != 3) {
            return -1;
        }
        if (arg0.level == 3 && this.level != 3) {
            return 1;
        }
        if (this.level == LeveledRegion.comparisonLevel && arg0.level != LeveledRegion.comparisonLevel) {
            return -1;
        }
        if (arg0.level == LeveledRegion.comparisonLevel && this.level != LeveledRegion.comparisonLevel) {
            return 1;
        }
        int toRegion = this.distanceFromPlayerCache;
        int toRegion2 = arg0.distanceFromPlayerCache;
        if (toRegion > toRegion2) {
            return 1;
        }
        if (toRegion != toRegion2) {
            return -1;
        }
        toRegion = this.leafDistanceFromPlayerCache;
        toRegion2 = arg0.leafDistanceFromPlayerCache;
        if (toRegion > toRegion2) {
            return 1;
        }
        if (toRegion == toRegion2) {
            return 0;
        }
        return -1;
    }
    
    public void onProcessingEnd() {
    }
    
    public void addDebugLines(final List<String> debugLines, final MapProcessor mapProcessor, final int textureX, final int textureY) {
        debugLines.add("processed: " + mapProcessor.isProcessed(this));
        debugLines.add(String.format("recache: %s reload: %s metaLoaded: %s", this.recacheHasBeenRequested(), this.reloadHasBeenRequested(), this.metaLoaded));
        debugLines.add("shouldCache: " + this.shouldCache() + " allCachePrepared: " + this.allCachePrepared);
        debugLines.add("activeBranchUpdateReferences: " + this.activeBranchUpdateReferences);
        debugLines.add("leafTextureVersionSum: " + this.leafTextureVersionSum[textureX][textureY] + " cachedTextureVersions: " + this.cachedTextureVersions[textureX][textureY] + " [" + textureX + "," + textureY + "]");
    }
    
    protected void writeCacheMetaData(final DataOutputStream output, final byte[] usableBuffer, final byte[] integerByteBuffer) throws IOException {
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                final T texture = this.getTexture(i, j);
                if (texture != null && texture.shouldIncludeInCache()) {
                    if (!texture.isCachePrepared()) {
                        throw new RuntimeException("Trying to save cache but " + i + " " + j + " in " + this + " is not prepared.");
                    }
                    output.write(i << 4 | j);
                    final int bufferedTextureVersion = texture.getBufferedTextureVersion();
                    output.writeInt(bufferedTextureVersion);
                }
            }
        }
        output.write(255);
    }
    
    public boolean saveCacheTextures(final File tempFile, final boolean debugConfig, final int extraAttempts) {
        if (debugConfig) {
            WorldMap.LOGGER.info("(World Map) Saving cache: " + this);
        }
        boolean success = false;
        try (final ZipOutputStream zipOutput = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(tempFile)));
             final DataOutputStream output = new DataOutputStream(zipOutput)) {
            final ZipEntry e = new ZipEntry("cache.xaero");
            zipOutput.putNextEntry(e);
            final byte[] usableBuffer = new byte[16384];
            final byte[] integerByteBuffer = new byte[4];
            output.writeInt(24);
            this.writeCacheMetaData(output, usableBuffer, integerByteBuffer);
            this.saveBiomePalette(output);
            for (int i = 0; i < 8; ++i) {
                for (int j = 0; j < 8; ++j) {
                    final T texture = this.getTexture(i, j);
                    if (texture != null && texture.shouldIncludeInCache()) {
                        if (!texture.isCachePrepared()) {
                            throw new RuntimeException("Trying to save cache but " + i + " " + j + " in " + this + " is not prepared.");
                        }
                        output.write(i << 4 | j);
                        texture.writeCacheMapData(output, usableBuffer, integerByteBuffer, this);
                    }
                }
            }
            output.write(255);
            zipOutput.closeEntry();
            success = true;
        }
        catch (IOException ioe) {
            WorldMap.LOGGER.info("(World Map) IO exception while trying to save cache textures for " + this, (Throwable)ioe);
            if (extraAttempts > 0) {
                WorldMap.LOGGER.info("Retrying...");
                try {
                    Thread.sleep(20L);
                }
                catch (InterruptedException ex) {}
                return this.saveCacheTextures(tempFile, debugConfig, extraAttempts - 1);
            }
        }
        synchronized (this) {
            this.setAllCachePrepared(false);
        }
        for (int k = 0; k < 8; ++k) {
            for (int l = 0; l < 8; ++l) {
                final T texture2 = this.getTexture(k, l);
                if (texture2 != null && texture2.shouldIncludeInCache()) {
                    texture2.deleteColorBuffer();
                    synchronized (this) {
                        texture2.setCachePrepared(false);
                        this.setAllCachePrepared(false);
                    }
                }
            }
        }
        return success;
    }
    
    protected void readCacheMetaData(final DataInputStream input, final int cacheSaveVersion, final byte[] usableBuffer, final byte[] integerByteBuffer, final boolean[][] textureLoaded, final MapProcessor mapProcessor) throws IOException {
        if (cacheSaveVersion == 8 || cacheSaveVersion >= 12) {
            this.readCacheInput(true, input, cacheSaveVersion, usableBuffer, integerByteBuffer, textureLoaded, false, mapProcessor);
        }
    }
    
    public boolean loadCacheTextures(final MapProcessor mapProcessor, final boolean justMetaData, final boolean[][] textureLoaded, final int targetHighlightsHash, final boolean[] leafShouldAffectBranchesDest, final boolean[] metaLoadedDest, final int extraAttempts) {
        if (this.cacheFile == null) {
            return false;
        }
        if (this.cacheFile.exists()) {
            try (final ZipInputStream zipInput = new ZipInputStream(new BufferedInputStream(new FileInputStream(this.cacheFile)));
                 final DataInputStream input = new DataInputStream(zipInput)) {
                final ZipEntry entry = zipInput.getNextEntry();
                if (entry != null) {
                    final byte[] integerByteBuffer = new byte[4];
                    final int cacheSaveVersion = input.readInt();
                    if (cacheSaveVersion > 24 || cacheSaveVersion == 7 || cacheSaveVersion == 21) {
                        input.close();
                        WorldMap.LOGGER.info("(World Map) Trying to load newer region cache " + this + " using an older version of Xaero's World Map!");
                        mapProcessor.getMapSaveLoad().backupFile(this.cacheFile, cacheSaveVersion);
                        this.cacheFile = null;
                        this.shouldCache = true;
                        return false;
                    }
                    if (cacheSaveVersion < 24) {
                        this.shouldCache = true;
                    }
                    final byte[] usableBuffer = new byte[16384];
                    if (cacheSaveVersion >= 8) {
                        this.readCacheMetaData(input, cacheSaveVersion, usableBuffer, integerByteBuffer, textureLoaded, mapProcessor);
                        metaLoadedDest[0] = true;
                        if (justMetaData && (cacheSaveVersion == 8 || cacheSaveVersion >= 12)) {
                            return true;
                        }
                    }
                    this.preCacheLoad();
                    this.loadBiomePalette(input, cacheSaveVersion, mapProcessor);
                    final boolean leafShouldAffectBranches = this.shouldLeafAffectCache(targetHighlightsHash);
                    if (leafShouldAffectBranchesDest != null) {
                        leafShouldAffectBranchesDest[0] = leafShouldAffectBranches;
                    }
                    this.readCacheInput(false, input, cacheSaveVersion, usableBuffer, integerByteBuffer, textureLoaded, leafShouldAffectBranches, mapProcessor);
                    metaLoadedDest[0] = true;
                    zipInput.closeEntry();
                    return false;
                }
            }
            catch (IOException ioe) {
                WorldMap.LOGGER.error("IO exception while trying to load cache for region " + this + "! " + this.cacheFile, (Throwable)ioe);
                if (extraAttempts > 0) {
                    WorldMap.LOGGER.info("(World Map) Retrying...");
                    try {
                        Thread.sleep(20L);
                    }
                    catch (InterruptedException ex) {}
                    metaLoadedDest[0] = false;
                    return this.loadCacheTextures(mapProcessor, justMetaData, textureLoaded, targetHighlightsHash, leafShouldAffectBranchesDest, metaLoadedDest, extraAttempts - 1);
                }
                this.cacheFile = null;
                this.shouldCache = true;
                this.onCacheLoadFailed(textureLoaded);
            }
            catch (Throwable e) {
                this.cacheFile = null;
                this.shouldCache = true;
                WorldMap.LOGGER.error("Failed to load cache for region " + this + "! " + this.cacheFile, e);
                this.onCacheLoadFailed(textureLoaded);
            }
        }
        else {
            this.cacheFile = null;
            this.shouldCache = true;
        }
        return false;
    }
    
    protected abstract void onCacheLoadFailed(final boolean[][] p0);
    
    public void saveBiomePalette(final DataOutputStream output) throws IOException {
        final int paletteSize = -1;
        output.writeInt(paletteSize);
    }
    
    private void loadBiomePalette(final DataInputStream input, final int cacheSaveVersion, final MapProcessor mapProcessor) throws IOException {
        if (cacheSaveVersion >= 19) {
            input.readInt();
        }
    }
    
    protected boolean shouldLeafAffectCache(final int targetHighlightsHash) {
        return false;
    }
    
    private void readCacheInput(final boolean isMeta, final DataInputStream input, final int cacheSaveVersion, final byte[] usableBuffer, final byte[] integerByteBuffer, final boolean[][] textureLoaded, final boolean leafShouldAffectBranches, final MapProcessor mapProcessor) throws IOException {
        for (int textureCoords = input.read(); textureCoords != -1 && textureCoords != 255; textureCoords = input.read()) {
            final int x = textureCoords >> 4;
            final int y = textureCoords & 0xF;
            if (isMeta) {
                final int cachedTextureVersion = input.readInt();
                this.updateLeafTextureVersion(x, y, this.cachedTextureVersions[x][y] = cachedTextureVersion);
            }
            else {
                RegionTexture<T> texture = (RegionTexture<T>)(this.hasTextures() ? this.getTexture(x, y) : null);
                if (texture == null) {
                    texture = (RegionTexture<T>)this.createTexture(x, y);
                    if (this.level == 0) {
                        synchronized (this) {
                            this.setAllCachePrepared(false);
                        }
                    }
                }
                texture.readCacheData(cacheSaveVersion, input, usableBuffer, integerByteBuffer, this, mapProcessor, x, y, leafShouldAffectBranches);
            }
            if (textureLoaded != null) {
                textureLoaded[x][y] = true;
            }
        }
    }
    
    public int getAndResetCachedTextureVersion(final int x, final int y) {
        final int result = this.cachedTextureVersions[x][y];
        this.cachedTextureVersions[x][y] = -1;
        return result;
    }
    
    public BranchLeveledRegion getParent() {
        return this.parent;
    }
    
    public boolean shouldAffectLoadingRequestFrequency() {
        return this.shouldBeProcessed();
    }
    
    protected void preCacheLoad() {
    }
    
    public void processWhenLoadedChunksExist(final int globalRegionCacheHashCode) {
    }
    
    public boolean isMetaLoaded() {
        return this.metaLoaded;
    }
    
    public void confirmMetaLoaded() {
        this.metaLoaded = true;
    }
    
    public LeveledRegion<?> getRootRegion() {
        LeveledRegion<?> result = this;
        if (this.parent != null) {
            result = (LeveledRegion<?>)this.parent.getRootRegion();
        }
        return result;
    }
    
    public void checkForUpdates(final MapProcessor mapProcessor, final boolean prevWaitingForBranchCache, final boolean[] waitingForBranchCache, final ArrayList<BranchLeveledRegion> branchRegionBuffer, final int viewedLevel, final int minViewedLeafX, final int minViewedLeafZ, final int maxViewedLeafX, final int maxViewedLeafZ) {
    }
    
    public boolean isRefreshing() {
        return false;
    }
    
    public boolean shouldAllowAnotherRegionToLoad() {
        synchronized (this) {
            if (!this.reloadHasBeenRequested() && !this.hasRemovableSourceData() && !this.isRefreshing()) {
                return true;
            }
        }
        return false;
    }
    
    public abstract boolean shouldEndProcessingAfterUpload();
    
    public abstract T createTexture(final int p0, final int p1);
    
    public abstract void putTexture(final int p0, final int p1, final T p2);
    
    public abstract T getTexture(final int p0, final int p1);
    
    protected abstract void putLeaf(final int p0, final int p1, final MapRegion p2);
    
    protected abstract boolean remove(final int p0, final int p1, final int p2);
    
    protected abstract LeveledRegion<?> get(final int p0, final int p1, final int p2);
    
    public abstract boolean loadingAnimation();
    
    public abstract boolean cleanAndCacheRequestsBlocked();
    
    public abstract boolean shouldBeProcessed();
    
    public abstract boolean isLoaded();
    
    public abstract void preCache();
    
    public abstract void postCache(final File p0, final MapSaveLoad p1, final boolean p2) throws IOException;
    
    public abstract boolean skipCaching(final int p0);
    
    public abstract File findCacheFile(final MapSaveLoad p0) throws IOException;
    
    public abstract void onCurrentDimFinish(final MapSaveLoad p0, final MapProcessor p1);
    
    public abstract void onLimiterRemoval(final MapProcessor p0);
    
    public abstract void afterLimiterRemoval(final MapProcessor p0);
    
    public String getExtraInfo() {
        return "";
    }
    
    public void updateLeafTextureVersion(final int localTextureX, final int localTextureZ, final int newVersion) {
    }
    
    public boolean hasRemovableSourceData() {
        return false;
    }
    
    public int getCaveLayer() {
        return this.caveLayer;
    }
    
    static {
        LeveledRegion.comparisonX = 0;
        LeveledRegion.comparisonZ = 0;
        LeveledRegion.comparisonLeafX = 0;
        LeveledRegion.comparisonLeafZ = 0;
    }
}
