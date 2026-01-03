//Decompiled by Procyon!

package xaero.map.graphics;

import xaero.map.pool.*;
import java.util.*;
import xaero.map.region.texture.*;
import net.minecraft.client.gui.*;
import org.lwjgl.opengl.*;

public class TextureUploader
{
    public static final int NORMAL = 0;
    public static final int NORMALDOWNLOAD = 1;
    public static final int COMPRESSED = 2;
    public static final int BRANCHUPDATE = 3;
    public static final int BRANCHUPDATE_ALLOCATE = 4;
    public static final int BRANCHDOWNLOAD = 5;
    public static final int SUBSEQUENT_NORMAL = 6;
    private static final int DEFAULT_NORMAL_TIME = 1000000;
    private static final int DEFAULT_COMPRESSED_TIME = 1000000;
    private static final int DEFAULT_BRANCHUPDATED_TIME = 3000000;
    private static final int DEFAULT_BRANCHUPDATE_ALLOCATE_TIME = 4000000;
    private static final int DEFAULT_BRANCHDOWNLOAD_TIME = 1000000;
    private static final int DEFAULT_SUBSEQUENT_NORMAL_TIME = 1000000;
    private List<TextureUpload> textureUploadRequests;
    private TextureUploadBenchmark textureUploadBenchmark;
    private final TextureUploadPool.Normal normalTextureUploadPool;
    private final TextureUploadPool.Compressed compressedTextureUploadPool;
    private final TextureUploadPool.BranchUpdate branchUpdatePool;
    private final TextureUploadPool.BranchUpdate branchUpdateAllocatePool;
    private final TextureUploadPool.BranchDownload branchDownloadPool;
    private final TextureUploadPool.SubsequentNormal subsequentNormalTextureUploadPool;
    
    public TextureUploader(final TextureUploadPool.Normal normalTextureUploadPool, final TextureUploadPool.Compressed compressedTextureUploadPool, final TextureUploadPool.BranchUpdate branchUpdatePool, final TextureUploadPool.BranchUpdate branchUpdateAllocatePool, final TextureUploadPool.BranchDownload branchDownloadPool, final TextureUploadPool.SubsequentNormal subsequentNormalTextureUploadPool, final TextureUploadBenchmark textureUploadBenchmark) {
        this.textureUploadRequests = new ArrayList<TextureUpload>();
        this.normalTextureUploadPool = normalTextureUploadPool;
        this.compressedTextureUploadPool = compressedTextureUploadPool;
        this.textureUploadBenchmark = textureUploadBenchmark;
        this.branchUpdatePool = branchUpdatePool;
        this.branchUpdateAllocatePool = branchUpdateAllocatePool;
        this.branchDownloadPool = branchDownloadPool;
        this.subsequentNormalTextureUploadPool = subsequentNormalTextureUploadPool;
    }
    
    public long requestUpload(final TextureUpload upload) {
        this.textureUploadRequests.add(upload);
        if (upload.getUploadType() == 0) {
            return this.textureUploadBenchmark.isFinished(0) ? Math.min(this.textureUploadBenchmark.getAverage(0), 1000000L) : 1000000L;
        }
        if (upload.getUploadType() == 2) {
            return this.textureUploadBenchmark.isFinished(2) ? Math.min(this.textureUploadBenchmark.getAverage(2), 1000000L) : 1000000L;
        }
        if (upload.getUploadType() == 3) {
            return this.textureUploadBenchmark.isFinished(3) ? Math.min(this.textureUploadBenchmark.getAverage(3), 3000000L) : 3000000L;
        }
        if (upload.getUploadType() == 4) {
            return this.textureUploadBenchmark.isFinished(4) ? Math.min(this.textureUploadBenchmark.getAverage(4), 4000000L) : 4000000L;
        }
        if (upload.getUploadType() == 5) {
            return this.textureUploadBenchmark.isFinished(5) ? Math.min(this.textureUploadBenchmark.getAverage(5), 1000000L) : 1000000L;
        }
        if (upload.getUploadType() == 6) {
            return this.textureUploadBenchmark.isFinished(6) ? Math.min(this.textureUploadBenchmark.getAverage(6), 1000000L) : 1000000L;
        }
        return 0L;
    }
    
    public long requestNormal(final int glTexture, final int glPbo, final int target, final int level, final int internalFormat, final int width, final int height, final int border, final long pixels_buffer_offset, final int format, final int type) {
        final TextureUpload upload = (TextureUpload)this.normalTextureUploadPool.get(glTexture, glPbo, target, level, internalFormat, width, height, border, pixels_buffer_offset, format, type);
        return this.requestUpload(upload);
    }
    
    public long requestSubsequentNormal(final int glTexture, final int glPbo, final int target, final int level, final int width, final int height, final int border, final long pixels_buffer_offset, final int format, final int type, final int xOffset, final int yOffset) {
        final TextureUpload upload = (TextureUpload)this.subsequentNormalTextureUploadPool.get(glTexture, glPbo, target, level, width, height, border, pixels_buffer_offset, format, type, xOffset, yOffset);
        return this.requestUpload(upload);
    }
    
    public long requestCompressed(final int glTexture, final int glPbo, final int target, final int level, final int internalFormat, final int width, final int height, final int border, final long pixels_buffer_offset, final int dataSize) {
        final TextureUpload upload = (TextureUpload)this.compressedTextureUploadPool.get(glTexture, glPbo, target, level, internalFormat, width, height, border, pixels_buffer_offset, dataSize);
        return this.requestUpload(upload);
    }
    
    public long requestBranchUpdate(final boolean allocate, final int glTexture, final int glPbo, final int target, final int level, final int internalFormat, final int width, final int height, final int border, final long pixels_buffer_offset, final int format, final int type, final Integer srcTextureTopLeft, final Integer srcTextureTopRight, final Integer srcTextureBottomLeft, final Integer srcTextureBottomRight, final BranchTextureRenderer renderer, final int glPackPbo, final int pboOffset, final ScaledResolution scaledRes) {
        TextureUpload upload;
        if (!allocate) {
            upload = (TextureUpload)this.branchUpdatePool.get(glTexture, glPbo, target, level, internalFormat, width, height, border, pixels_buffer_offset, format, type, srcTextureTopLeft, srcTextureTopRight, srcTextureBottomLeft, srcTextureBottomRight, renderer, glPackPbo, pboOffset, scaledRes);
        }
        else {
            upload = (TextureUpload)this.branchUpdateAllocatePool.get(glTexture, glPbo, target, level, internalFormat, width, height, border, pixels_buffer_offset, format, type, srcTextureTopLeft, srcTextureTopRight, srcTextureBottomLeft, srcTextureBottomRight, renderer, glPackPbo, pboOffset, scaledRes);
        }
        return this.requestUpload(upload);
    }
    
    public long requestBranchDownload(final int glTexture, final int target, final int glPackPbo, final int pboOffset) {
        final TextureUpload upload = (TextureUpload)this.branchDownloadPool.get(glTexture, target, glPackPbo, pboOffset);
        return this.requestUpload(upload);
    }
    
    public void finishNewestRequestImmediately() {
        final TextureUpload newestRequest = this.textureUploadRequests.remove(this.textureUploadRequests.size() - 1);
        newestRequest.run();
        this.addToPool(newestRequest);
    }
    
    public void uploadTextures() {
        if (!this.textureUploadRequests.isEmpty()) {
            boolean prepared = false;
            for (int i = 0; i < this.textureUploadRequests.size(); ++i) {
                final TextureUpload tu = this.textureUploadRequests.get(i);
                final int type = tu.getUploadType();
                if (!this.textureUploadBenchmark.isFinished(type)) {
                    if (!prepared) {
                        GL11.glFinish();
                        prepared = true;
                    }
                    this.textureUploadBenchmark.pre();
                }
                tu.run();
                if (!this.textureUploadBenchmark.isFinished(type)) {
                    this.textureUploadBenchmark.post(type);
                    prepared = true;
                }
                this.addToPool(tu);
            }
            this.textureUploadRequests.clear();
        }
    }
    
    private void addToPool(final TextureUpload tu) {
        switch (tu.getUploadType()) {
            case 0: {
                this.normalTextureUploadPool.addToPool((TextureUpload.Normal)tu);
                break;
            }
            case 2: {
                this.compressedTextureUploadPool.addToPool((TextureUpload.Compressed)tu);
                break;
            }
            case 3: {
                this.branchUpdatePool.addToPool((TextureUpload.BranchUpdate)tu);
                break;
            }
            case 4: {
                this.branchUpdateAllocatePool.addToPool((TextureUpload.BranchUpdate)tu);
                break;
            }
            case 5: {
                this.branchDownloadPool.addToPool((TextureUpload.BranchDownload)tu);
                break;
            }
            case 6: {
                this.subsequentNormalTextureUploadPool.addToPool((TextureUpload.SubsequentNormal)tu);
                break;
            }
        }
    }
}
