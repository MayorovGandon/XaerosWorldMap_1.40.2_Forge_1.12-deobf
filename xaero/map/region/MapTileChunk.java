//Decompiled by Procyon!

package xaero.map.region;

import xaero.map.region.texture.*;
import xaero.map.biome.*;
import xaero.map.cache.*;
import net.minecraft.client.*;
import xaero.map.*;
import net.minecraft.world.*;
import net.minecraft.util.math.*;
import xaero.map.world.*;
import java.nio.*;
import xaero.map.pool.*;
import xaero.map.common.config.option.*;
import xaero.lib.common.config.option.*;
import java.io.*;
import xaero.map.file.*;

public class MapTileChunk
{
    public static final int SIDE_LENGTH = 4;
    private MapRegion inRegion;
    private byte loadState;
    private int X;
    private int Z;
    private MapTile[][] tiles;
    private byte[][] tileGridsCache;
    private LeafRegionTexture leafTexture;
    private boolean toUpdateBuffers;
    private boolean changed;
    private boolean includeInSave;
    private boolean hasHadTerrain;
    private boolean hasHighlights;
    private boolean hasHighlightsIfUndiscovered;
    
    public MapTileChunk(final MapRegion r, final int x, final int z) {
        this.loadState = 0;
        this.tiles = new MapTile[4][4];
        this.tileGridsCache = new byte[this.tiles.length][this.tiles.length];
        this.X = x;
        this.Z = z;
        this.inRegion = r;
        this.leafTexture = this.createLeafTexture();
    }
    
    protected LeafRegionTexture createLeafTexture() {
        return new LeafRegionTexture(this);
    }
    
    public void updateBuffers(final MapProcessor mapProcessor, final BiomeColorCalculator biomeColorCalculator, final OverlayManager overlayManager, final boolean detailedDebug, final BlockStateShortShapeCache blockStateShortShapeCache, final MapUpdateFastConfig config) {
        if (!Minecraft.func_71410_x().func_152345_ab()) {
            throw new RuntimeException("Wrong thread!");
        }
        if (detailedDebug) {
            WorldMap.LOGGER.info("Updating buffers: " + this.X + " " + this.Z + " " + this.loadState);
        }
        final World world = (World)mapProcessor.getWorld();
        final LeafRegionTexture leafTexture = this.getLeafTexture();
        leafTexture.resetTimer();
        synchronized (this.inRegion) {
            leafTexture.setCachePrepared(false);
            leafTexture.setShouldDownloadFromPBO(false);
            this.inRegion.setAllCachePrepared(false);
        }
        leafTexture.prepareBuffer();
        final int[] result = this.inRegion.getPixelResultBuffer();
        boolean hasLight = false;
        final BlockPos.MutableBlockPos mutableGlobalPos = this.inRegion.getMutableGlobalPos();
        final MapTileChunk prevTileChunk = this.getNeighbourTileChunk(0, -1, mapProcessor, false);
        final MapTileChunk prevTileChunkDiagonal = this.getNeighbourTileChunk(-1, -1, mapProcessor, false);
        final MapTileChunk prevTileChunkHorisontal = this.getNeighbourTileChunk(-1, 0, mapProcessor, false);
        final MapDimension dim = mapProcessor.getMapWorld().getCurrentDimension();
        final float shadowR = dim.getShadowR();
        final float shadowG = dim.getShadowG();
        final float shadowB = dim.getShadowB();
        final ByteBuffer colorBuffer = leafTexture.getDirectColorBuffer();
        mapProcessor.getBiomeColorCalculator().prepare(config.biomeBlending);
        for (int o = 0; o < this.tiles.length; ++o) {
            final int offX = o * 16;
            for (int p = 0; p < this.tiles.length; ++p) {
                final MapTile tile = this.tiles[o][p];
                if (tile != null) {
                    if (tile.isLoaded()) {
                        final int caveStart = tile.getWrittenCaveStart();
                        final int caveDepth = tile.getWrittenCaveDepth();
                        final int offZ = p * 16;
                        for (int z = 0; z < 16; ++z) {
                            final int pixelZ = offZ + z;
                            for (int x = 0; x < 16; ++x) {
                                final int pixelX = offX + x;
                                final int effectiveHeight = leafTexture.getHeight(pixelX, pixelZ);
                                final int effectiveTopHeight = leafTexture.getTopHeight(pixelX, pixelZ);
                                tile.getBlock(x, z).getPixelColour(result, mapProcessor.getMapWriter(), world, dim, this, prevTileChunk, prevTileChunkDiagonal, prevTileChunkHorisontal, tile, x, z, caveStart, caveDepth, mutableGlobalPos, shadowR, shadowG, shadowB, biomeColorCalculator, mapProcessor, overlayManager, effectiveHeight, effectiveTopHeight, blockStateShortShapeCache, config);
                                this.putColour(pixelX, pixelZ, result[0], result[1], result[2], result[3], colorBuffer, 64);
                                if (result[3] != 0) {
                                    hasLight = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        leafTexture.postBufferUpdate(hasLight);
        this.toUpdateBuffers = false;
        leafTexture.setToUpload(true);
    }
    
    public void putColour(final int x, final int y, final int red, final int green, final int blue, final int alpha, final ByteBuffer buffer, final int size) {
        final int pos = (y * size + x) * 4;
        buffer.putInt(pos, blue << 24 | green << 16 | red << 8 | alpha);
    }
    
    public MapTileChunk getNeighbourTileChunk(final int directionX, final int directionZ, final MapProcessor mapProcessor, final boolean crossRegion) {
        final int maxCoord = 7;
        final int chunkXInsideRegion = this.X & maxCoord;
        final int chunkZInsideRegion = this.Z & maxCoord;
        MapTileChunk prevTileChunk = null;
        int chunkXInsideRegionPrev = chunkXInsideRegion + directionX;
        int chunkZInsideRegionPrev = chunkZInsideRegion + directionZ;
        int regDirectionX = 0;
        int regDirectionZ = 0;
        if (chunkXInsideRegionPrev < 0 || chunkXInsideRegionPrev > maxCoord) {
            regDirectionX = directionX;
            chunkXInsideRegionPrev &= maxCoord;
        }
        if (chunkZInsideRegionPrev < 0 || chunkZInsideRegionPrev > maxCoord) {
            regDirectionZ = directionZ;
            chunkZInsideRegionPrev &= maxCoord;
        }
        MapRegion prevTileChunkSrc;
        if (regDirectionX != 0 || regDirectionZ != 0) {
            prevTileChunkSrc = (crossRegion ? mapProcessor.getLeafMapRegion(this.inRegion.getCaveLayer(), this.inRegion.getRegionX() + regDirectionX, this.inRegion.getRegionZ() + regDirectionZ, false) : null);
        }
        else {
            prevTileChunkSrc = this.inRegion;
        }
        if (prevTileChunkSrc != null) {
            prevTileChunk = prevTileChunkSrc.getChunk(chunkXInsideRegionPrev, chunkZInsideRegionPrev);
        }
        return prevTileChunk;
    }
    
    public void clean(final MapProcessor mapProcessor) {
        for (int o = 0; o < 4; ++o) {
            for (int p = 0; p < 4; ++p) {
                final MapTile tile = this.tiles[o][p];
                if (tile != null) {
                    mapProcessor.getTilePool().addToPool((PoolUnit)tile);
                    this.tiles[o][p] = null;
                }
            }
        }
        this.toUpdateBuffers = false;
        this.includeInSave = false;
    }
    
    public int getX() {
        return this.X;
    }
    
    public int getZ() {
        return this.Z;
    }
    
    public byte[][] getTileGridsCache() {
        return this.tileGridsCache;
    }
    
    public int getLoadState() {
        return this.loadState;
    }
    
    public void setLoadState(final byte loadState) {
        this.loadState = loadState;
    }
    
    public MapTile getTile(final int x, final int z) {
        return this.tiles[x][z];
    }
    
    public void setTile(final int x, final int z, final MapTile tile, final BlockStateShortShapeCache blockStateShortShapeCache) {
        final LeafRegionTexture leafTexture = this.leafTexture;
        if (tile != null) {
            final boolean tileWasLoadedWithTopHeightValues = tile.getWorldInterpretationVersion() > 0;
            this.includeInSave = true;
            final boolean adjustHeightForCarpetLikeBlocks = (boolean)WorldMap.INSTANCE.getConfigs().getClientConfigManager().getEffective((ConfigOption)WorldMapProfiledConfigOptions.ADJUST_HEIGHT_FOR_SHORT_BLOCKS);
            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    final int destX = x * 16 + i;
                    final int destZ = z * 16 + j;
                    final MapBlock mapBlock = tile.getBlock(i, j);
                    final boolean subtractOneFromHeight = adjustHeightForCarpetLikeBlocks && blockStateShortShapeCache.isShort(mapBlock.getState());
                    leafTexture.putHeight(destX, destZ, mapBlock.getEffectiveHeight(subtractOneFromHeight));
                    if (mapBlock.getState() == -1 || (mapBlock.getState() == 0 && mapBlock.getNumberOfOverlays() == 0) || (!tileWasLoadedWithTopHeightValues && mapBlock.getState() != 0 && mapBlock.getNumberOfOverlays() > 0)) {
                        leafTexture.removeTopHeight(destX, destZ);
                    }
                    else {
                        leafTexture.putTopHeight(destX, destZ, mapBlock.getEffectiveTopHeight(subtractOneFromHeight));
                    }
                    leafTexture.setBiome(destX, destZ, mapBlock.getBiome());
                }
            }
        }
        else if (this.tiles[x][z] != null) {
            for (int k = 0; k < 16; ++k) {
                for (int l = 0; l < 16; ++l) {
                    final int destX2 = x * 16 + k;
                    final int destZ2 = z * 16 + l;
                    leafTexture.removeHeight(destX2, destZ2);
                    leafTexture.removeTopHeight(destX2, destZ2);
                    leafTexture.setBiome(destX2, destZ2, -1);
                }
            }
        }
        this.tiles[x][z] = tile;
    }
    
    public MapRegion getInRegion() {
        return this.inRegion;
    }
    
    public boolean wasChanged() {
        return this.changed;
    }
    
    public void setChanged(final boolean changed) {
        this.changed = changed;
    }
    
    public int getTimer() {
        return this.leafTexture.getTimer();
    }
    
    public void decTimer() {
        this.leafTexture.decTimer();
    }
    
    public boolean includeInSave() {
        return this.includeInSave;
    }
    
    public void unincludeInSave() {
        this.includeInSave = false;
    }
    
    public void resetHeights() {
        this.leafTexture.resetHeights();
    }
    
    public boolean getToUpdateBuffers() {
        return this.toUpdateBuffers;
    }
    
    public void setToUpdateBuffers(final boolean toUpdateBuffers) {
        this.toUpdateBuffers = toUpdateBuffers;
    }
    
    @Deprecated
    public int getGlColorTexture() {
        return this.leafTexture.getGlColorTexture();
    }
    
    public LeafRegionTexture getLeafTexture() {
        return this.leafTexture;
    }
    
    public void writeCacheData(final DataOutputStream output, final byte[] usableBuffer, final byte[] integerByteBuffer, final LeveledRegion<LeafRegionTexture> inRegion2) throws IOException {
    }
    
    public void readCacheData(final int cacheSaveVersion, final DataInputStream input, final byte[] usableBuffer, final byte[] integerByteBuffer, final MapProcessor mapProcessor, final int x, final int y) throws IOException {
        if (cacheSaveVersion == 4) {
            final boolean hasBottomHeightValues = input.read() == 1;
            if (hasBottomHeightValues) {
                input.readByte();
                final byte[] bottomHeights = new byte[64];
                IOHelper.readToBuffer(bottomHeights, 64, input);
                final LeafRegionTexture leafTexture = this.leafTexture;
                for (int i = 0; i < 64; ++i) {
                    leafTexture.putHeight(i, 63, bottomHeights[i]);
                }
            }
        }
        else if (cacheSaveVersion >= 5 && cacheSaveVersion < 13) {
            input.readInt();
            final byte[] heights = new byte[64];
            final LeafRegionTexture leafTexture2 = this.leafTexture;
            for (int hx = 0; hx < 64; ++hx) {
                IOHelper.readToBuffer(heights, 64, input);
                for (int hz = 0; hz < 64; ++hz) {
                    leafTexture2.putHeight(hx, hz, heights[hz]);
                }
            }
        }
        if (cacheSaveVersion >= 4 && cacheSaveVersion < 10 && (this.Z & 0x7) == 0x0) {
            input.readByte();
        }
        this.loadState = 2;
    }
    
    @Override
    public String toString() {
        return this.getX() + " " + this.getZ();
    }
    
    public boolean hasHadTerrain() {
        return this.hasHadTerrain;
    }
    
    public void setHasHadTerrain() {
        this.hasHadTerrain = true;
        this.inRegion.setHasHadTerrain();
    }
    
    public void unsetHasHadTerrain() {
        this.hasHadTerrain = false;
    }
    
    public boolean hasHighlights() {
        return this.hasHighlights;
    }
    
    public void setHasHighlights(final boolean hasHighlights) {
        this.hasHighlights = hasHighlights;
    }
    
    public boolean hasHighlightsIfUndiscovered() {
        return this.hasHighlightsIfUndiscovered;
    }
    
    public void setHasHighlightsIfUndiscovered(final boolean hasHighlightsIfUndiscovered) {
        this.hasHighlightsIfUndiscovered = hasHighlightsIfUndiscovered;
    }
}
