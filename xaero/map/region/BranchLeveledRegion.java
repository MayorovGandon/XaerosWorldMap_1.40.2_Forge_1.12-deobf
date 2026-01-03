//Decompiled by Procyon!

package xaero.map.region;

import xaero.map.world.*;
import xaero.map.*;
import xaero.map.misc.*;
import java.util.*;
import xaero.map.region.texture.*;
import xaero.map.file.*;
import java.io.*;
import java.nio.file.attribute.*;
import java.nio.file.*;

public class BranchLeveledRegion extends LeveledRegion<BranchRegionTexture>
{
    public static final int CHILD_LENGTH_IN_TEXTURES = 4;
    public static final int MAX_COORD_WITHIN_CHILD = 3;
    private boolean loaded;
    private boolean freed;
    private boolean readyForUpdates;
    private BranchRegionTexture[][] textures;
    private LeveledRegion<?>[][] children;
    private boolean shouldCheckForUpdates;
    private boolean downloading;
    private long lastUpdateTime;
    private int updateCountSinceSave;
    
    public BranchLeveledRegion(final String worldId, final String dimId, final String mwId, final MapDimension dim, final int level, final int leveledX, final int leveledZ, final int caveLayer, final BranchLeveledRegion parent) {
        super(worldId, dimId, mwId, dim, level, leveledX, leveledZ, caveLayer, parent);
        this.children = (LeveledRegion<?>[][])new LeveledRegion[2][2];
        this.reset();
    }
    
    private void reset() {
        this.shouldCache = false;
        this.recacheHasBeenRequested = false;
        this.reloadHasBeenRequested = false;
        this.metaLoaded = false;
        this.loaded = false;
        this.freed = false;
        this.textures = null;
        this.downloading = false;
        this.updateCountSinceSave = 0;
        this.lastUpdateTime = 0L;
        this.readyForUpdates = false;
    }
    
    private boolean checkAndTrackRegionExistence(final MapProcessor mapProcessor, final int x, final int z) {
        final MapDimension mapDimension = mapProcessor.getMapWorld().getCurrentDimension();
        final MapLayer mapLayer = mapDimension.getLayeredMapRegions().getLayer(this.caveLayer);
        if (mapLayer.regionDetectionExists(x, z)) {
            return true;
        }
        if (mapProcessor.getMapSaveLoad().isRegionDetectionComplete() && mapDimension.getHighlightHandler().shouldApplyRegionHighlights(x, z, false)) {
            mapLayer.getRegionHighlightExistenceTracker().track(x, z);
            return true;
        }
        return false;
    }
    
    @Override
    public void checkForUpdates(final MapProcessor mapProcessor, final boolean prevWaitingForBranchCache, final boolean[] waitingForBranchCache, final ArrayList<BranchLeveledRegion> branchRegionBuffer, final int viewedLevel, final int minViewedLeafX, final int minViewedLeafZ, final int maxViewedLeafX, final int maxViewedLeafZ) {
        super.checkForUpdates(mapProcessor, prevWaitingForBranchCache, waitingForBranchCache, branchRegionBuffer, viewedLevel, minViewedLeafX, minViewedLeafZ, maxViewedLeafX, maxViewedLeafZ);
        if (!this.isLoaded()) {
            if (this.parent != null) {
                this.parent.setShouldCheckForUpdatesRecursive(true);
            }
            if (this.level == viewedLevel) {
                waitingForBranchCache[0] = true;
            }
            if (!this.recacheHasBeenRequested() && !this.reloadHasBeenRequested()) {
                this.calculateSortingDistance();
                Misc.addToListOfSmallest(10, (List)branchRegionBuffer, (Comparable)this);
            }
            return;
        }
        if (!this.readyForUpdates || prevWaitingForBranchCache) {
            if (this.parent != null) {
                this.parent.setShouldCheckForUpdatesRecursive(true);
            }
            return;
        }
        synchronized (this) {
            if (this.downloading || this.recacheHasBeenRequested) {
                if (this.parent != null) {
                    this.parent.setShouldCheckForUpdatesRecursive(true);
                }
                return;
            }
            if (!this.shouldCheckForUpdates) {
                return;
            }
            this.shouldCheckForUpdates = false;
            boolean shouldRevisitParent = false;
            boolean outdated = false;
            final int level = this.level;
            final int regionX = this.regionX;
            final int regionZ = this.regionZ;
            for (int i = 0; i < 2; ++i) {
                for (int j = 0; j < 2; ++j) {
                    LeveledRegion<?> childRegion = this.children[i][j];
                    final int globalChildRegionX = regionX << 1 | i;
                    final int globalChildRegionZ = regionZ << 1 | j;
                    final int textureOffsetX = i * 4;
                    final int textureOffsetY = j * 4;
                    boolean outdatedWithChild = false;
                    boolean outdatedWithLeaves = false;
                    boolean childRegionIsLoaded = (childRegion != null && childRegion.isLoaded()) || (childRegion == null && level == 1 && !this.checkAndTrackRegionExistence(mapProcessor, globalChildRegionX, globalChildRegionZ));
                    for (int o = 0; o < 4; ++o) {
                        for (int p = 0; p < 4; ++p) {
                            final int textureX = textureOffsetX + o;
                            final int textureY = textureOffsetY + p;
                            BranchRegionTexture texture = this.getTexture(textureX, textureY);
                            int textureVersion = 0;
                            if (texture != null) {
                                textureVersion = texture.getTextureVersion();
                                if (textureVersion == -1) {
                                    textureVersion = texture.getBufferedTextureVersion();
                                }
                            }
                            boolean leavesLoaded = true;
                            int leafTextureSum = -1;
                            final int minLeafTextureX = (regionX << 3) + textureX << level;
                            final int minLeafTextureZ = (regionZ << 3) + textureY << level;
                            final int maxLeafTextureX = minLeafTextureX + (1 << level) - 1;
                            final int maxLeafTextureZ = minLeafTextureZ + (1 << level) - 1;
                            final int minLeafRegX = minLeafTextureX >> 3;
                            final int minLeafRegZ = minLeafTextureZ >> 3;
                            final int maxLeafRegX = maxLeafTextureX >> 3;
                            final int maxLeafRegZ = maxLeafTextureZ >> 3;
                        Label_0584:
                            for (int leafRegX = minLeafRegX; leafRegX <= maxLeafRegX; ++leafRegX) {
                                for (int leafRegZ = minLeafRegZ; leafRegZ <= maxLeafRegZ; ++leafRegZ) {
                                    final MapRegion leafRegion = mapProcessor.getLeafMapRegion(this.caveLayer, leafRegX, leafRegZ, false);
                                    if (leafRegion == null && this.checkAndTrackRegionExistence(mapProcessor, leafRegX, leafRegZ)) {
                                        leavesLoaded = false;
                                        break Label_0584;
                                    }
                                    if (leafRegion != null) {
                                        synchronized (leafRegion) {
                                            if (!leafRegion.isMetaLoaded() && !leafRegion.isLoaded()) {
                                                leavesLoaded = false;
                                                break Label_0584;
                                            }
                                            if (leafTextureSum == -1) {
                                                leafTextureSum = this.leafTextureVersionSum[textureX][textureY];
                                            }
                                        }
                                    }
                                }
                            }
                            if (leavesLoaded && leafTextureSum == -1) {
                                leafTextureSum = 0;
                                if (textureVersion != 0 && level > 1) {
                                    if (childRegion == null) {
                                        final LeveledRegion<?>[] array = this.children[i];
                                        final int n = j;
                                        final LeveledRegion<?> leveledRegion = new BranchLeveledRegion(this.worldId, this.dimId, this.mwId, this.dim, level - 1, globalChildRegionX, globalChildRegionZ, this.caveLayer, this);
                                        array[n] = leveledRegion;
                                        childRegion = leveledRegion;
                                        this.dim.getLayeredMapRegions().addListRegion(childRegion);
                                        childRegionIsLoaded = false;
                                    }
                                    ((BranchLeveledRegion)childRegion).setShouldCheckForUpdatesRecursive(true);
                                }
                            }
                            if (leavesLoaded && textureVersion != leafTextureSum) {
                                outdatedWithLeaves = true;
                            }
                            if (childRegionIsLoaded) {
                                final int childTextureOffsetX = o << 1;
                                final int childTextureOffsetY = p << 1;
                                RegionTexture<?> childTopLeft = null;
                                RegionTexture<?> childTopRight = null;
                                RegionTexture<?> childBottomLeft = null;
                                RegionTexture<?> childBottomRight = null;
                                if (childRegion != null) {
                                    childTopLeft = (RegionTexture<?>)childRegion.getTexture(childTextureOffsetX, childTextureOffsetY);
                                    childTopRight = (RegionTexture<?>)childRegion.getTexture(childTextureOffsetX + 1, childTextureOffsetY);
                                    childBottomLeft = (RegionTexture<?>)childRegion.getTexture(childTextureOffsetX, childTextureOffsetY + 1);
                                    childBottomRight = (RegionTexture<?>)childRegion.getTexture(childTextureOffsetX + 1, childTextureOffsetY + 1);
                                }
                                if (childTopLeft != null || childTopRight != null || childBottomLeft != null || childBottomRight != null) {
                                    final boolean newTexture = texture == null;
                                    if (newTexture) {
                                        texture = new BranchRegionTexture(this);
                                    }
                                    if (texture.checkForUpdates(childTopLeft, childTopRight, childBottomLeft, childBottomRight, childRegion)) {
                                        outdatedWithChild = true;
                                        if (newTexture) {
                                            this.putTexture(textureX, textureY, texture);
                                        }
                                    }
                                }
                                else if (texture != null) {
                                    this.putTexture(textureX, textureY, null);
                                    texture.deleteTexturesAndBuffers();
                                    this.countTextureUpdate();
                                    outdatedWithChild = true;
                                    shouldRevisitParent = true;
                                }
                            }
                        }
                    }
                    if ((outdatedWithLeaves || outdatedWithChild) && childRegion != null) {
                        childRegion.checkForUpdates(mapProcessor, prevWaitingForBranchCache, waitingForBranchCache, branchRegionBuffer, viewedLevel, minViewedLeafX, minViewedLeafZ, maxViewedLeafX, maxViewedLeafZ);
                    }
                    if (outdatedWithChild) {
                        outdated = true;
                    }
                }
            }
            if (outdated && this.freed) {
                this.freed = false;
                mapProcessor.addToProcess((LeveledRegion)this);
            }
            if (shouldRevisitParent && this.parent != null) {
                this.parent.setShouldCheckForUpdatesRecursive(true);
            }
        }
    }
    
    @Override
    public void putTexture(final int x, final int y, final BranchRegionTexture texture) {
        this.textures[x][y] = texture;
    }
    
    @Override
    public BranchRegionTexture getTexture(final int x, final int y) {
        return this.textures[x][y];
    }
    
    @Override
    public boolean hasTextures() {
        return this.textures != null;
    }
    
    public boolean isEmpty() {
        for (int i = 0; i < this.children.length; ++i) {
            for (int j = 0; j < this.children.length; ++j) {
                if (this.children[i][j] != null) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public void preCacheLoad() {
        this.textures = new BranchRegionTexture[8][8];
        this.freed = false;
    }
    
    @Override
    protected void putLeaf(final int X, final int Z, final MapRegion leaf) {
        final int childLevel = this.level - 1;
        final int childLevelX = X >> childLevel;
        final int childLevelZ = Z >> childLevel;
        final int localChildLevelX = childLevelX & 0x1;
        final int localChildLevelZ = childLevelZ & 0x1;
        if (this.level == 1) {
            if (this.children[localChildLevelX][localChildLevelZ] == null) {
                leaf.setParent(this);
                this.children[localChildLevelX][localChildLevelZ] = leaf;
            }
            return;
        }
        LeveledRegion<?> childBranch = this.children[localChildLevelX][localChildLevelZ];
        if (childBranch == null) {
            final LeveledRegion<?>[] array = this.children[localChildLevelX];
            final int n = localChildLevelZ;
            final LeveledRegion<?> leveledRegion = new BranchLeveledRegion(leaf.getWorldId(), leaf.getDimId(), leaf.getMwId(), this.dim, childLevel, childLevelX, childLevelZ, this.caveLayer, this);
            array[n] = leveledRegion;
            childBranch = leveledRegion;
            this.dim.getLayeredMapRegions().addListRegion(childBranch);
        }
        childBranch.putLeaf(X, Z, leaf);
    }
    
    @Override
    protected LeveledRegion<?> get(final int leveledX, final int leveledZ, final int level) {
        if (this.level == level) {
            return this;
        }
        final int childLevel = this.level - 1;
        if (level > childLevel) {
            throw new RuntimeException(new IllegalArgumentException());
        }
        final int childLevelX = leveledX >> childLevel - level;
        final int childLevelZ = leveledZ >> childLevel - level;
        final int localChildLevelX = childLevelX & 0x1;
        final int localChildLevelZ = childLevelZ & 0x1;
        final LeveledRegion<?> childBranch = this.children[localChildLevelX][localChildLevelZ];
        if (childBranch == null) {
            return null;
        }
        return childBranch.get(leveledX, leveledZ, level);
    }
    
    @Override
    protected boolean remove(final int leveledX, final int leveledZ, final int level) {
        final int childLevel = this.level - 1;
        if (level > childLevel) {
            throw new RuntimeException(new IllegalArgumentException());
        }
        final int childLevelX = leveledX >> childLevel - level;
        final int childLevelZ = leveledZ >> childLevel - level;
        final int localChildLevelX = childLevelX & 0x1;
        final int localChildLevelZ = childLevelZ & 0x1;
        final LeveledRegion<?> childRegion = this.children[localChildLevelX][localChildLevelZ];
        if (level != childLevel) {
            return childRegion != null && childRegion.remove(leveledX, leveledZ, level);
        }
        if (childRegion != null) {
            this.children[localChildLevelX][localChildLevelZ] = null;
            return true;
        }
        return false;
    }
    
    @Override
    public boolean loadingAnimation() {
        return !this.loaded;
    }
    
    @Override
    public void addDebugLines(final List<String> debugLines, final MapProcessor mapProcessor, final int textureX, final int textureY) {
        super.addDebugLines(debugLines, mapProcessor, textureX, textureY);
        debugLines.add("loaded: " + this.loaded);
        debugLines.add("children: tl " + (this.children[0][0] != null) + " tr " + (this.children[1][0] != null) + " bl " + (this.children[0][1] != null) + " br " + (this.children[1][1] != null));
        debugLines.add("freed: " + this.freed + " shouldCheckForUpdates: " + this.shouldCheckForUpdates + " hasTextures: " + this.hasTextures());
        debugLines.add("updateCountSinceSave: " + this.updateCountSinceSave);
    }
    
    @Override
    public boolean shouldEndProcessingAfterUpload() {
        return this.loaded;
    }
    
    public void setLoaded(final boolean loaded) {
        this.loaded = loaded;
    }
    
    @Override
    public boolean isLoaded() {
        return this.loaded;
    }
    
    @Override
    public boolean cleanAndCacheRequestsBlocked() {
        return this.downloading || (this.updateCountSinceSave > 0 && !this.recacheHasBeenRequested);
    }
    
    @Override
    public void onProcessingEnd() {
        super.onProcessingEnd();
        this.freed = true;
        this.readyForUpdates = true;
    }
    
    @Override
    public boolean shouldBeProcessed() {
        return this.loaded && !this.freed;
    }
    
    @Override
    public void preCache() {
    }
    
    @Override
    public void postCache(final File permFile, final MapSaveLoad mapSaveLoad, final boolean successfullySaved) throws IOException {
        this.lastSaveTime = System.currentTimeMillis();
        this.updateCountSinceSave = 0;
    }
    
    @Override
    public boolean skipCaching(final int globalVersion) {
        return false;
    }
    
    @Override
    public File findCacheFile(final MapSaveLoad mapSaveLoad) throws IOException {
        final Path subFolder = mapSaveLoad.getMWSubFolder(this.worldId, this.dimId, this.mwId);
        final Path layerFolder = mapSaveLoad.getCaveLayerFolder(this.caveLayer, subFolder);
        final Path rootCacheFolder = layerFolder.resolve("cache");
        final Path levelCacheFolder = rootCacheFolder.resolve("" + this.level);
        Files.createDirectories(levelCacheFolder, (FileAttribute<?>[])new FileAttribute[0]);
        return levelCacheFolder.resolve(this.regionX + "_" + this.regionZ + ".xwmc").toFile();
    }
    
    @Override
    public void onCurrentDimFinish(final MapSaveLoad mapSaveLoad, final MapProcessor mapProcessor) {
    }
    
    @Override
    public void onLimiterRemoval(final MapProcessor mapProcessor) {
        mapProcessor.removeMapRegion((LeveledRegion)this);
    }
    
    @Override
    public void afterLimiterRemoval(final MapProcessor mapProcessor) {
        this.reset();
    }
    
    @Override
    public BranchRegionTexture createTexture(final int x, final int y) {
        return this.textures[x][y] = new BranchRegionTexture(this);
    }
    
    public void setShouldCheckForUpdatesRecursive(final boolean shouldCheckForUpdates) {
        this.shouldCheckForUpdates = shouldCheckForUpdates;
        if (this.parent != null) {
            this.parent.setShouldCheckForUpdatesRecursive(shouldCheckForUpdates);
        }
    }
    
    public void setShouldCheckForUpdatesSingle(final boolean shouldCheckForUpdates) {
        this.shouldCheckForUpdates = shouldCheckForUpdates;
    }
    
    public void startDownloadingTexturesForCache(final MapProcessor mapProcessor) {
        synchronized (this) {
            this.recacheHasBeenRequested = true;
            this.shouldCache = true;
            this.downloading = true;
        }
        boolean hasSomething = false;
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                final BranchRegionTexture regionTexture = this.textures[i][j];
                if (regionTexture != null) {
                    hasSomething = true;
                    if (!regionTexture.shouldUpload() && !regionTexture.isCachePrepared()) {
                        regionTexture.requestDownload();
                    }
                }
            }
        }
        if (this.freed) {
            this.freed = false;
            mapProcessor.addToProcess((LeveledRegion)this);
        }
        synchronized (this) {
            if (!hasSomething) {
                this.setAllCachePrepared(true);
            }
            this.downloading = false;
            this.updateCountSinceSave = 0;
        }
    }
    
    public void postTextureUpdate() {
        if (this.parent != null) {
            this.parent.setShouldCheckForUpdatesRecursive(true);
        }
        this.countTextureUpdate();
    }
    
    private void countTextureUpdate() {
        this.lastUpdateTime = System.currentTimeMillis();
        ++this.updateCountSinceSave;
    }
    
    public boolean eligibleForSaving(final long currentTime) {
        return this.updateCountSinceSave > 0 && (this.updateCountSinceSave >= 64 || currentTime - this.lastUpdateTime > 1000L);
    }
    
    @Override
    protected void onCacheLoadFailed(final boolean[][] textureLoaded) {
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                final RegionTexture<?> texture = this.getTexture(i, j);
                if (texture != null && !textureLoaded[i][j]) {
                    this.textures[i][j] = null;
                    texture.deleteTexturesAndBuffers();
                }
            }
        }
    }
}
