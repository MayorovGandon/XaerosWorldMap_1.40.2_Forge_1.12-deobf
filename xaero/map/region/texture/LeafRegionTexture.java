//Decompiled by Procyon!

package xaero.map.region.texture;

import xaero.map.pool.buffer.*;
import xaero.map.biome.*;
import xaero.map.cache.*;
import xaero.map.region.*;
import xaero.map.highlight.*;
import xaero.map.graphics.*;
import net.minecraft.client.gui.*;
import xaero.map.exception.*;
import xaero.map.*;
import xaero.map.pool.*;
import xaero.map.config.util.*;
import java.util.*;
import java.nio.*;
import org.lwjgl.opengl.*;
import java.io.*;
import xaero.map.misc.*;
import net.minecraft.client.*;

public class LeafRegionTexture extends RegionTexture<LeafRegionTexture>
{
    private MapTileChunk tileChunk;
    protected PoolTextureDirectBufferUnit highlitColorBuffer;
    
    public LeafRegionTexture(final MapTileChunk tileChunk) {
        super((LeveledRegion)tileChunk.getInRegion());
        this.tileChunk = tileChunk;
    }
    
    public void postBufferUpdate(final boolean hasLight) {
        this.colorBufferFormat = -1;
        this.colorBufferCompressed = false;
        this.bufferHasLight = hasLight;
    }
    
    @Override
    public void preUpload(final MapProcessor mapProcessor, final BiomeColorCalculator biomeColorCalculator, final OverlayManager overlayManager, final LeveledRegion<LeafRegionTexture> leveledRegion, final boolean detailedDebug, final BlockStateShortShapeCache blockStateShortShapeCache, final MapUpdateFastConfig updateConfig) {
        final MapRegion region = (MapRegion)leveledRegion;
        if (this.tileChunk.getToUpdateBuffers() && !mapProcessor.isWritingPaused()) {
            synchronized (region.writerThreadPauseSync) {
                if (!region.isWritingPaused()) {
                    this.tileChunk.updateBuffers(mapProcessor, biomeColorCalculator, overlayManager, detailedDebug, blockStateShortShapeCache, updateConfig);
                }
            }
        }
    }
    
    @Override
    public void postUpload(final MapProcessor mapProcessor, final LeveledRegion<LeafRegionTexture> leveledRegion, final boolean cleanAndCacheRequestsBlocked) {
        final MapRegion region = (MapRegion)leveledRegion;
        if (region.getLoadState() >= 2 && (region.getLoadState() == 3 || (!region.isBeingWritten() && (region.getLastVisited() == 0L || region.getTimeSinceVisit() > 1000L))) && !cleanAndCacheRequestsBlocked && !this.tileChunk.getToUpdateBuffers() && this.tileChunk.getLoadState() != 3) {
            region.setLoadState((byte)3);
            this.tileChunk.setLoadState((byte)3);
            this.tileChunk.clean(mapProcessor);
        }
    }
    
    @Override
    public boolean canUpload() {
        return this.tileChunk.getLoadState() >= 2;
    }
    
    @Override
    public boolean isUploaded() {
        return super.isUploaded() && !this.tileChunk.getToUpdateBuffers();
    }
    
    @Override
    public boolean hasSourceData() {
        return this.tileChunk.getLoadState() != 3;
    }
    
    @Override
    protected long uploadNonCache(final DimensionHighlighterHandler highlighterHandler, final TextureUploader textureUploader, final BranchTextureRenderer unused, final ScaledResolution scaledRes) {
        final PoolTextureDirectBufferUnit colorBufferToUpload = this.applyHighlights(highlighterHandler, this.colorBuffer, true);
        if (this.textureVersion == -1) {
            this.updateTextureVersion((this.bufferedTextureVersion != -1) ? (this.bufferedTextureVersion + 1) : (1 + (int)(Math.random() * 1000000.0)));
        }
        else {
            this.updateTextureVersion(this.textureVersion + 1);
        }
        if (colorBufferToUpload == null) {
            return 0L;
        }
        this.writeToUnpackPBO(0, colorBufferToUpload);
        this.textureHasLight = this.bufferHasLight;
        BufferCompatibilityFix.position((Buffer)this.colorBuffer.getDirectBuffer(), 0);
        this.colorBufferFormat = 32856;
        this.bufferedTextureVersion = this.textureVersion;
        final boolean subsequent = this.glColorTexture != -1;
        this.bindColorTexture(true);
        OpenGLException.checkGLError();
        if (this.unpackPbo[0] == 0) {
            return 0L;
        }
        long totalEstimatedTime;
        if (subsequent) {
            totalEstimatedTime = textureUploader.requestSubsequentNormal(this.glColorTexture, this.unpackPbo[0], 3553, 0, 64, 64, 0, 0L, 32993, 32821, 0, 0);
        }
        else {
            totalEstimatedTime = textureUploader.requestNormal(this.glColorTexture, this.unpackPbo[0], 3553, 0, 32856, 64, 64, 0, 0L, 32993, 32821);
        }
        final boolean toUploadImmediately = this.tileChunk.getInRegion().isBeingWritten();
        if (toUploadImmediately) {
            textureUploader.finishNewestRequestImmediately();
        }
        return totalEstimatedTime;
    }
    
    @Override
    protected PoolTextureDirectBufferUnit applyHighlights(final DimensionHighlighterHandler highlighterHandler, PoolTextureDirectBufferUnit colorBuffer, final boolean separateBuffer) {
        if (!this.tileChunk.hasHighlights()) {
            return colorBuffer;
        }
        colorBuffer = super.applyHighlights(highlighterHandler, colorBuffer, separateBuffer);
        final int startChunkX = this.tileChunk.getX() << 2;
        final int startChunkZ = this.tileChunk.getZ() << 2;
        boolean prepared = false;
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                final boolean discovered = this.getHeight(i << 4, j << 4) != -1;
                final int chunkX = startChunkX + i;
                final int chunkZ = startChunkZ + j;
                final PoolTextureDirectBufferUnit result = highlighterHandler.applyChunkHighlightColors(chunkX, chunkZ, i, j, colorBuffer, this.highlitColorBuffer, prepared, discovered, separateBuffer);
                if (result != null && separateBuffer) {
                    this.highlitColorBuffer = result;
                    prepared = true;
                }
            }
        }
        if (prepared) {
            return this.highlitColorBuffer;
        }
        return colorBuffer;
    }
    
    @Override
    public void postBufferWrite(final PoolTextureDirectBufferUnit buffer) {
        super.postBufferWrite(buffer);
        if (buffer == this.highlitColorBuffer) {
            this.highlitColorBuffer = null;
            if (!WorldMap.textureDirectBufferPool.addToPool((PoolUnit)buffer)) {
                WorldMap.bufferDeallocator.deallocate(buffer.getDirectBuffer(), WorldMapClientConfigUtils.getDebug());
            }
        }
    }
    
    @Override
    protected void updateTextureVersion(final int newVersion) {
        super.updateTextureVersion(newVersion);
        this.region.updateLeafTextureVersion(this.tileChunk.getX() & 0x7, this.tileChunk.getZ() & 0x7, newVersion);
    }
    
    @Override
    public void addDebugLines(final List<String> lines) {
        super.addDebugLines(lines);
        lines.add(this.tileChunk.getX() + " " + this.tileChunk.getZ());
        lines.add("loadState: " + this.tileChunk.getLoadState());
        lines.add(String.format("changed: %s include: %s terrain: %s highlights: %s toUpdateBuffers: %s", this.tileChunk.wasChanged(), this.tileChunk.includeInSave(), this.tileChunk.hasHadTerrain(), this.tileChunk.hasHighlights(), this.tileChunk.getToUpdateBuffers()));
    }
    
    @Override
    protected void onDownloadedBuffer(final ByteBuffer mappedPBO, final int isCompressed) {
        int length;
        if (isCompressed == 1) {
            length = GL11.glGetTexLevelParameteri(3553, 0, 34464);
        }
        else {
            length = 16384;
        }
        final ByteBuffer directBuffer = this.colorBuffer.getDirectBuffer();
        BufferCompatibilityFix.clear((Buffer)directBuffer);
        if (mappedPBO != null) {
            BufferCompatibilityFix.limit((Buffer)mappedPBO, length);
            directBuffer.put(mappedPBO);
            BufferCompatibilityFix.flip((Buffer)directBuffer);
        }
        else {
            BufferCompatibilityFix.limit((Buffer)directBuffer, length);
        }
    }
    
    @Override
    public void writeCacheMapData(final DataOutputStream output, final byte[] usableBuffer, final byte[] integerByteBuffer, final LeveledRegion<LeafRegionTexture> inRegion) throws IOException {
        super.writeCacheMapData(output, usableBuffer, integerByteBuffer, inRegion);
        this.tileChunk.writeCacheData(output, usableBuffer, integerByteBuffer, (LeveledRegion)inRegion);
    }
    
    @Override
    public void readCacheData(final int cacheSaveVersion, final DataInputStream input, final byte[] usableBuffer, final byte[] integerByteBuffer, final LeveledRegion<LeafRegionTexture> inRegion, final MapProcessor mapProcessor, final int x, final int y, final boolean leafShouldAffectBranches) throws IOException {
        super.readCacheData(cacheSaveVersion, input, usableBuffer, integerByteBuffer, inRegion, mapProcessor, x, y, leafShouldAffectBranches);
        this.tileChunk.readCacheData(cacheSaveVersion, input, usableBuffer, integerByteBuffer, mapProcessor, x, y);
        if (leafShouldAffectBranches) {
            this.colorBufferFormat = -1;
        }
        if (this.colorBuffer != null) {
            this.tileChunk.setHasHadTerrain();
        }
    }
    
    public void resetHeights() {
        Misc.clearHeightsData586(this.heightValues.getData());
        Misc.clearHeightsData586(this.topHeightValues.getData());
    }
    
    @Override
    public boolean shouldBeUsedForBranchUpdate(final int usedVersion) {
        return this.tileChunk.getLoadState() != 1 && super.shouldBeUsedForBranchUpdate(usedVersion);
    }
    
    @Override
    public boolean shouldHaveContentForBranchUpdate() {
        return this.tileChunk.getLoadState() > 0 && super.shouldHaveContentForBranchUpdate();
    }
    
    @Override
    public void deleteTexturesAndBuffers() {
        if (!Minecraft.func_71410_x().func_152345_ab()) {
            final Object o;
            synchronized (o = ((this.region.getLevel() == 3) ? this.region : this.region.getParent())) {
                synchronized (this.region) {
                    this.tileChunk.setLoadState((byte)0);
                }
            }
        }
        super.deleteTexturesAndBuffers();
    }
    
    @Override
    public void prepareBuffer() {
        super.prepareBuffer();
        this.tileChunk.setHasHadTerrain();
    }
    
    public MapTileChunk getTileChunk() {
        return this.tileChunk;
    }
    
    @Override
    public boolean shouldIncludeInCache() {
        return this.tileChunk.hasHadTerrain();
    }
    
    public void requestHighlightOnlyUpload() {
        this.resetBiomes();
        this.colorBufferCompressed = false;
        this.colorBufferFormat = 32856;
        this.bufferedTextureVersion = this.tileChunk.getInRegion().getTargetHighlightsHash();
        this.setToUpload(true);
        if (this.tileChunk.getLoadState() < 2) {
            this.tileChunk.setLoadState((byte)2);
        }
    }
}
