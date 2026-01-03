//Decompiled by Procyon!

package xaero.map.region.texture;

import xaero.map.*;
import xaero.map.biome.*;
import xaero.map.cache.*;
import xaero.map.highlight.*;
import xaero.map.graphics.*;
import net.minecraft.client.gui.*;
import xaero.map.exception.*;
import xaero.map.region.*;
import xaero.map.misc.*;
import java.nio.*;
import java.util.*;
import java.io.*;

public class BranchRegionTexture extends RegionTexture<BranchRegionTexture>
{
    private boolean updating;
    private boolean colorAllocationRequested;
    private ChildTextureInfo topLeftInfo;
    private ChildTextureInfo topRightInfo;
    private ChildTextureInfo bottomLeftInfo;
    private ChildTextureInfo bottomRightInfo;
    private LeveledRegion<?> branchUpdateChildRegion;
    private boolean checkForUpdatesAfterDownload;
    
    public BranchRegionTexture(final LeveledRegion<BranchRegionTexture> region) {
        super(region);
        this.reset();
    }
    
    private void reset() {
        this.updating = false;
        this.colorAllocationRequested = false;
        this.topLeftInfo = new ChildTextureInfo();
        this.topRightInfo = new ChildTextureInfo();
        this.bottomLeftInfo = new ChildTextureInfo();
        this.bottomRightInfo = new ChildTextureInfo();
        this.checkForUpdatesAfterDownload = false;
    }
    
    public boolean checkForUpdates(final RegionTexture<?> topLeft, final RegionTexture<?> topRight, final RegionTexture<?> bottomLeft, final RegionTexture<?> bottomRight, final LeveledRegion<?> childRegion) {
        boolean needsUpdating = false;
        if ((topLeft != null && topLeft.glColorTexture == -1) || (topRight != null && topRight.glColorTexture == -1) || (bottomLeft != null && bottomLeft.glColorTexture == -1) || (bottomRight != null && bottomRight.glColorTexture == -1)) {
            return false;
        }
        needsUpdating = (needsUpdating || this.isChildUpdated(this.topLeftInfo, topLeft, childRegion));
        needsUpdating = (needsUpdating || this.isChildUpdated(this.topRightInfo, topRight, childRegion));
        needsUpdating = (needsUpdating || this.isChildUpdated(this.bottomLeftInfo, bottomLeft, childRegion));
        needsUpdating = (needsUpdating || this.isChildUpdated(this.bottomRightInfo, bottomRight, childRegion));
        if (needsUpdating) {
            if (this.toUpload) {
                if (this.shouldDownloadFromPBO) {
                    this.checkForUpdatesAfterDownload = true;
                    return false;
                }
                if (this.topLeftInfo.temporaryReference == topLeft && this.topRightInfo.temporaryReference == topRight && this.bottomLeftInfo.temporaryReference == bottomLeft && this.bottomRightInfo.temporaryReference == bottomRight) {
                    return false;
                }
            }
            else {
                ++childRegion.activeBranchUpdateReferences;
            }
            this.setCachePrepared(false);
            this.region.setAllCachePrepared(false);
            this.colorBufferFormat = -1;
            this.toUpload = true;
            this.updating = true;
            this.topLeftInfo.temporaryReference = topLeft;
            this.topRightInfo.temporaryReference = topRight;
            this.bottomLeftInfo.temporaryReference = bottomLeft;
            this.bottomRightInfo.temporaryReference = bottomRight;
            this.branchUpdateChildRegion = childRegion;
        }
        return needsUpdating;
    }
    
    private boolean isChildUpdated(final ChildTextureInfo info, final RegionTexture<?> texture, final LeveledRegion<?> region) {
        if (region.isLoaded()) {
            if (texture == null && info.usedTextureVersion != 0) {
                return true;
            }
            if (texture != null && texture.glColorTexture != -1 && texture.shouldBeUsedForBranchUpdate(info.usedTextureVersion)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void preUpload(final MapProcessor mapProcessor, final BiomeColorCalculator biomeColorCalculator, final OverlayManager overlayManager, final LeveledRegion<BranchRegionTexture> region, final boolean detailedDebug, final BlockStateShortShapeCache blockStateShortShapeCache, final MapUpdateFastConfig updateConfig) {
    }
    
    @Override
    public void postUpload(final MapProcessor mapProcessor, final LeveledRegion<BranchRegionTexture> leveledRegion, final boolean cleanAndCacheRequestsBlocked) {
    }
    
    @Override
    public long uploadBuffer(final DimensionHighlighterHandler highlighterHandler, final TextureUploader textureUploader, final LeveledRegion<BranchRegionTexture> inRegion, final BranchTextureRenderer branchTextureRenderer, final int x, final int y, final ScaledResolution scaledRes) throws OpenGLException, IllegalArgumentException, IllegalAccessException {
        return super.uploadBuffer(highlighterHandler, textureUploader, inRegion, branchTextureRenderer, x, y, scaledRes);
    }
    
    private void copyNonColorData(final RegionTexture<?> childTexture, final int offX, final int offZ) {
        final boolean resetting = childTexture == null;
        for (int i = 0; i < 32; ++i) {
            for (int j = 0; j < 32; ++j) {
                final int childHeight = resetting ? -1 : childTexture.getHeight(i << 1, j << 1);
                final int childTopHeight = resetting ? -1 : childTexture.getTopHeight(i << 1, j << 1);
                final int childBiome = resetting ? -1 : childTexture.getBiome(i << 1, j << 1);
                final int destX = offX | i;
                final int destZ = offZ | j;
                if (childHeight != -1) {
                    this.putHeight(destX, destZ, childHeight);
                }
                else {
                    this.removeHeight(destX, destZ);
                }
                if (childTopHeight != -1) {
                    this.putTopHeight(destX, destZ, childTopHeight);
                }
                else {
                    this.removeTopHeight(destX, destZ);
                }
                this.setBiome(destX, destZ, childBiome);
            }
        }
    }
    
    @Override
    protected long uploadNonCache(final DimensionHighlighterHandler highlighterHandler, final TextureUploader textureUploader, final BranchTextureRenderer renderer, final ScaledResolution scaledRes) {
        this.timer = 5;
        this.prepareBuffer();
        this.shouldDownloadFromPBO = true;
        if (this.updating) {
            this.bindPackPBO();
            this.unbindPackPBO();
            this.bindColorTexture(true);
            OpenGLException.checkGLError();
            final ChildTextureInfo topLeftInfo = this.topLeftInfo;
            final ChildTextureInfo topRightInfo = this.topRightInfo;
            final ChildTextureInfo bottomLeftInfo = this.bottomLeftInfo;
            final ChildTextureInfo bottomRightInfo = this.bottomRightInfo;
            final Integer topLeftColor = topLeftInfo.getColorTextureForUpdate();
            final Integer topRightColor = topRightInfo.getColorTextureForUpdate();
            final Integer bottomLeftColor = bottomLeftInfo.getColorTextureForUpdate();
            final Integer bottomRightColor = bottomRightInfo.getColorTextureForUpdate();
            final long estimatedTime = textureUploader.requestBranchUpdate(!this.colorAllocationRequested, this.glColorTexture, this.unpackPbo[0], 3553, 0, 32856, 64, 64, 0, 0L, 32993, 32821, topLeftColor, topRightColor, bottomLeftColor, bottomRightColor, renderer, this.packPbo, 0, scaledRes);
            if (topLeftColor != null) {
                this.copyNonColorData(topLeftInfo.temporaryReference, 0, 0);
            }
            if (topRightColor != null) {
                this.copyNonColorData(topRightInfo.temporaryReference, 32, 0);
            }
            if (bottomLeftColor != null) {
                this.copyNonColorData(bottomLeftInfo.temporaryReference, 0, 32);
            }
            if (bottomRightColor != null) {
                this.copyNonColorData(bottomRightInfo.temporaryReference, 32, 32);
            }
            int textureVersionSum = 0;
            final int topLeftVersion;
            textureVersionSum += (topLeftVersion = topLeftInfo.getTextureVersion());
            final int topRightVersion;
            textureVersionSum += (topRightVersion = topRightInfo.getTextureVersion());
            final int bottomLeftVersion;
            textureVersionSum += (bottomLeftVersion = bottomLeftInfo.getTextureVersion());
            final int bottomRightVersion;
            textureVersionSum += (bottomRightVersion = bottomRightInfo.getTextureVersion());
            this.updateTextureVersion(textureVersionSum);
            this.colorAllocationRequested = true;
            this.textureHasLight = (topLeftInfo.hasLight() || topRightInfo.hasLight() || bottomLeftInfo.hasLight() || bottomRightInfo.hasLight());
            final LeveledRegion<?> branchUpdateChildRegion = this.branchUpdateChildRegion;
            --branchUpdateChildRegion.activeBranchUpdateReferences;
            this.branchUpdateChildRegion = null;
            topLeftInfo.onUpdate(topLeftVersion);
            topRightInfo.onUpdate(topRightVersion);
            bottomLeftInfo.onUpdate(bottomLeftVersion);
            bottomRightInfo.onUpdate(bottomRightVersion);
            final BranchLeveledRegion branchRegion = (BranchLeveledRegion)this.region;
            branchRegion.postTextureUpdate();
            return estimatedTime;
        }
        this.bindPackPBO();
        this.unbindPackPBO();
        return textureUploader.requestBranchDownload(this.glColorTexture, 3553, this.packPbo, 0);
    }
    
    @Override
    protected void onCacheUploadRequested() {
        super.onCacheUploadRequested();
        this.colorAllocationRequested = true;
    }
    
    @Override
    protected void onDownloadedBuffer(final ByteBuffer mappedPBO, final int isCompressed) {
        final ByteBuffer directBuffer = this.colorBuffer.getDirectBuffer();
        BufferCompatibilityFix.clear((Buffer)directBuffer);
        if (mappedPBO != null) {
            directBuffer.put(mappedPBO);
            BufferCompatibilityFix.flip((Buffer)directBuffer);
        }
        else {
            BufferCompatibilityFix.limit((Buffer)directBuffer, 16384);
        }
        if (this.checkForUpdatesAfterDownload) {
            ((BranchLeveledRegion)this.region).setShouldCheckForUpdatesRecursive(true);
            this.checkForUpdatesAfterDownload = false;
        }
    }
    
    @Override
    protected void endPBODownload(final int format, final boolean compressed, final boolean success) {
        if (!success) {
            this.topLeftInfo.usedTextureVersion--;
            this.topRightInfo.usedTextureVersion--;
            this.bottomLeftInfo.usedTextureVersion--;
            this.bottomRightInfo.usedTextureVersion--;
            this.updateTextureVersion(this.topLeftInfo.usedTextureVersion + this.topRightInfo.usedTextureVersion + this.bottomLeftInfo.usedTextureVersion + this.bottomRightInfo.usedTextureVersion);
        }
        super.endPBODownload(format, compressed, success);
    }
    
    @Override
    public boolean hasSourceData() {
        return false;
    }
    
    @Override
    public void addDebugLines(final List<String> lines) {
        super.addDebugLines(lines);
        lines.add("updating: " + this.updating);
        lines.add("colorAllocationRequested: " + this.colorAllocationRequested);
        lines.add("topLeftInfo: " + this.topLeftInfo);
        lines.add("topRightInfo: " + this.topRightInfo);
        lines.add("bottomLeftInfo: " + this.bottomLeftInfo);
        lines.add("bottomRightInfo: " + this.bottomRightInfo);
    }
    
    @Override
    public void onTextureDeletion() {
        super.onTextureDeletion();
        if (this.branchUpdateChildRegion != null) {
            final LeveledRegion<?> branchUpdateChildRegion = this.branchUpdateChildRegion;
            --branchUpdateChildRegion.activeBranchUpdateReferences;
        }
        this.topLeftInfo.onParentDeletion();
        this.topRightInfo.onParentDeletion();
        this.bottomLeftInfo.onParentDeletion();
        this.bottomRightInfo.onParentDeletion();
        this.reset();
    }
    
    public void requestDownload() {
        this.toUpload = true;
        this.updating = false;
    }
    
    @Override
    public void writeCacheMapData(final DataOutputStream output, final byte[] usableBuffer, final byte[] integerByteBuffer, final LeveledRegion<BranchRegionTexture> inRegion) throws IOException {
        super.writeCacheMapData(output, usableBuffer, integerByteBuffer, inRegion);
        output.writeInt(this.topLeftInfo.usedTextureVersion);
        output.writeInt(this.topRightInfo.usedTextureVersion);
        output.writeInt(this.bottomLeftInfo.usedTextureVersion);
        output.writeInt(this.bottomRightInfo.usedTextureVersion);
    }
    
    @Override
    public void readCacheData(final int cacheSaveVersion, final DataInputStream input, final byte[] usableBuffer, final byte[] integerByteBuffer, final LeveledRegion<BranchRegionTexture> inRegion, final MapProcessor mapProcessor, final int x, final int y, final boolean leafShouldAffectBranches) throws IOException {
        super.readCacheData(cacheSaveVersion, input, usableBuffer, integerByteBuffer, inRegion, mapProcessor, x, y, leafShouldAffectBranches);
        if (cacheSaveVersion >= 15) {
            this.topLeftInfo.usedTextureVersion = input.readInt();
            this.topRightInfo.usedTextureVersion = input.readInt();
            this.bottomLeftInfo.usedTextureVersion = input.readInt();
            this.bottomRightInfo.usedTextureVersion = input.readInt();
        }
    }
    
    public class ChildTextureInfo
    {
        private int usedTextureVersion;
        private RegionTexture<?> temporaryReference;
        
        private Integer getColorTextureForUpdate() {
            if ((this.temporaryReference == null && this.usedTextureVersion == 0) || (this.temporaryReference != null && !this.temporaryReference.shouldBeUsedForBranchUpdate(this.usedTextureVersion))) {
                return null;
            }
            if (this.temporaryReference == null || !this.temporaryReference.shouldHaveContentForBranchUpdate()) {
                return -1;
            }
            return this.temporaryReference.glColorTexture;
        }
        
        private int getTextureVersion() {
            if (this.temporaryReference == null || !this.temporaryReference.shouldHaveContentForBranchUpdate()) {
                return 0;
            }
            return this.temporaryReference.textureVersion;
        }
        
        private boolean hasLight() {
            return this.temporaryReference != null && this.temporaryReference.textureHasLight && this.temporaryReference.shouldHaveContentForBranchUpdate();
        }
        
        public void onUpdate(final int newVersion) {
            this.usedTextureVersion = newVersion;
            if (this.temporaryReference != null) {
                this.temporaryReference = null;
            }
        }
        
        public void onParentDeletion() {
            if (this.temporaryReference != null) {
                this.temporaryReference = null;
            }
        }
        
        public Integer getReferenceColorTexture() {
            return (this.temporaryReference == null) ? null : Integer.valueOf(this.temporaryReference.glColorTexture);
        }
        
        @Override
        public String toString() {
            return "tv " + this.usedTextureVersion + ", ct " + this.getReferenceColorTexture();
        }
    }
}
