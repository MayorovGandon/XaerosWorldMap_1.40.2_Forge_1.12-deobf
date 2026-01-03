//Decompiled by Procyon!

package xaero.map.pool;

import xaero.map.graphics.*;
import xaero.map.region.texture.*;
import net.minecraft.client.gui.*;

public abstract class TextureUploadPool<T extends TextureUpload> extends MapPool<T>
{
    public TextureUploadPool(final int maxSize) {
        super(maxSize);
    }
    
    public static class Normal extends TextureUploadPool<TextureUpload.Normal>
    {
        public Normal(final int maxSize) {
            super(maxSize);
        }
        
        protected TextureUpload.Normal construct(final Object... args) {
            return new TextureUpload.Normal(args);
        }
        
        public TextureUpload.Normal get(final int glTexture, final int glPbo, final int target, final int level, final int internalFormat, final int width, final int height, final int border, final long pixels_buffer_offset, final int format, final int type) {
            return (TextureUpload.Normal)super.get(new Object[] { glTexture, glPbo, target, level, internalFormat, width, height, border, pixels_buffer_offset, format, type });
        }
    }
    
    public static class SubsequentNormal extends TextureUploadPool<TextureUpload.SubsequentNormal>
    {
        public SubsequentNormal(final int maxSize) {
            super(maxSize);
        }
        
        protected TextureUpload.SubsequentNormal construct(final Object... args) {
            return new TextureUpload.SubsequentNormal(args);
        }
        
        public TextureUpload.SubsequentNormal get(final int glTexture, final int glPbo, final int target, final int level, final int width, final int height, final int border, final long pixels_buffer_offset, final int format, final int type, final int xOffset, final int yOffset) {
            return (TextureUpload.SubsequentNormal)super.get(new Object[] { glTexture, glPbo, target, level, -1, width, height, border, pixels_buffer_offset, format, type, xOffset, yOffset });
        }
    }
    
    public static class Compressed extends TextureUploadPool<TextureUpload.Compressed>
    {
        public Compressed(final int maxSize) {
            super(maxSize);
        }
        
        protected TextureUpload.Compressed construct(final Object... args) {
            return new TextureUpload.Compressed(args);
        }
        
        public TextureUpload.Compressed get(final int glTexture, final int glPbo, final int target, final int level, final int internalFormat, final int width, final int height, final int border, final long pixels_buffer_offset, final int dataSize) {
            return (TextureUpload.Compressed)super.get(new Object[] { glTexture, glPbo, target, level, internalFormat, width, height, border, pixels_buffer_offset, dataSize });
        }
    }
    
    public static class BranchUpdate extends TextureUploadPool<TextureUpload.BranchUpdate>
    {
        protected boolean allocate;
        
        public BranchUpdate(final int maxSize, final boolean allocate) {
            super(maxSize);
            this.allocate = allocate;
        }
        
        protected TextureUpload.BranchUpdate construct(final Object... args) {
            return new TextureUpload.BranchUpdate(args);
        }
        
        public TextureUpload.BranchUpdate get(final int glTexture, final int glPbo, final int target, final int level, final int internalFormat, final int width, final int height, final int border, final long pixels_buffer_offset, final int format, final int type, final Integer srcTextureTopLeft, final Integer srcTextureTopRight, final Integer srcTextureBottomLeft, final Integer srcTextureBottomRight, final BranchTextureRenderer renderer, final int glPackPbo, final int pboOffset, final ScaledResolution scaledRes) {
            return (TextureUpload.BranchUpdate)super.get(new Object[] { glTexture, glPbo, target, level, internalFormat, width, height, border, pixels_buffer_offset, format, type, this.allocate, srcTextureTopLeft, srcTextureTopRight, srcTextureBottomLeft, srcTextureBottomRight, renderer, glPackPbo, pboOffset, scaledRes });
        }
    }
    
    public static class BranchDownload extends TextureUploadPool<TextureUpload.BranchDownload>
    {
        public BranchDownload(final int maxSize) {
            super(maxSize);
        }
        
        protected TextureUpload.BranchDownload construct(final Object... args) {
            return new TextureUpload.BranchDownload(args);
        }
        
        public TextureUpload.BranchDownload get(final int glTexture, final int target, final int glPackPbo, final int pboOffset) {
            return (TextureUpload.BranchDownload)super.get(new Object[] { glTexture, 0, target, 0, 0, 0, 0, 0, 0L, glPackPbo, pboOffset });
        }
    }
}
