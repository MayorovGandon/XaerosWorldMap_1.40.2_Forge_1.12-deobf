//Decompiled by Procyon!

package xaero.map.graphics;

import xaero.map.pool.*;
import net.minecraft.client.renderer.*;
import xaero.map.exception.*;
import org.lwjgl.opengl.*;
import xaero.map.region.texture.*;
import net.minecraft.client.gui.*;
import java.nio.*;
import net.minecraft.client.*;

public abstract class TextureUpload implements PoolUnit
{
    protected int glTexture;
    private int glUnpackPbo;
    private int target;
    private int level;
    private int internalFormat;
    private int width;
    private int height;
    private int border;
    private long pixels_buffer_offset;
    private int uploadType;
    
    @Override
    public void create(final Object... args) {
        this.glTexture = (int)args[0];
        this.glUnpackPbo = (int)args[1];
        this.target = (int)args[2];
        this.level = (int)args[3];
        this.internalFormat = (int)args[4];
        this.width = (int)args[5];
        this.height = (int)args[6];
        this.border = (int)args[7];
        this.pixels_buffer_offset = (long)args[8];
    }
    
    public void run() {
        GlStateManager.func_179144_i(this.glTexture);
        OpenGLException.checkGLError(false, "preparing to upload a map texture");
        PixelBuffers.glBindBuffer(35052, this.glUnpackPbo);
        Util.checkGLError();
        this.upload();
        Util.checkGLError();
        PixelBuffers.glBindBuffer(35052, 0);
        GlStateManager.func_179144_i(0);
        Util.checkGLError();
    }
    
    abstract void upload();
    
    public int getUploadType() {
        return this.uploadType;
    }
    
    public static class Normal extends TextureUpload
    {
        private int format;
        private int type;
        
        public Normal(final int uploadType) {
            this.uploadType = uploadType;
        }
        
        public Normal(final Object... args) {
            this(0);
            this.create(args);
        }
        
        @Override
        void upload() {
            GL11.glHint(34031, 4354);
            OpenGLException.checkGLError();
            GL11.glTexImage2D(this.target, this.level, this.internalFormat, this.width, this.height, this.border, this.format, this.type, this.pixels_buffer_offset);
            OpenGLException.checkGLError(false, "uploading a map texture");
        }
        
        @Override
        public void create(final Object... args) {
            super.create(args);
            this.format = (int)args[9];
            this.type = (int)args[10];
        }
    }
    
    public static class SubsequentNormal extends TextureUpload
    {
        private int format;
        private int type;
        private int xOffset;
        private int yOffset;
        
        public SubsequentNormal(final int uploadType) {
            this.uploadType = uploadType;
        }
        
        public SubsequentNormal(final Object... args) {
            this(6);
            this.create(args);
        }
        
        @Override
        void upload() throws OpenGLException {
            GL11.glTexSubImage2D(this.target, this.level, this.xOffset, this.yOffset, this.width, this.height, this.format, this.type, this.pixels_buffer_offset);
            OpenGLException.checkGLError();
        }
        
        @Override
        public void create(final Object... args) {
            super.create(args);
            this.format = (int)args[9];
            this.type = (int)args[10];
            this.xOffset = (int)args[11];
            this.yOffset = (int)args[12];
        }
    }
    
    public static class Compressed extends TextureUpload
    {
        private int dataSize;
        
        public Compressed(final Object... args) {
            this.create(args);
            this.uploadType = 2;
        }
        
        @Override
        void upload() {
            GL13.glCompressedTexImage2D(this.target, this.level, this.internalFormat, this.width, this.height, this.border, this.dataSize, this.pixels_buffer_offset);
        }
        
        @Override
        public void create(final Object... args) {
            super.create(args);
            this.dataSize = (int)args[9];
        }
    }
    
    public static class BranchUpdate extends TextureUpload
    {
        private int format;
        private int type;
        private boolean allocate;
        private Integer srcTextureTopLeft;
        private Integer srcTextureTopRight;
        private Integer srcTextureBottomLeft;
        private Integer srcTextureBottomRight;
        private BranchTextureRenderer renderer;
        private int glPackPbo;
        private int pboOffset;
        private ScaledResolution scaledRes;
        
        public BranchUpdate(final int uploadType) {
            this.uploadType = uploadType;
        }
        
        public BranchUpdate(final Object... args) {
            this(args[11] ? 4 : 3);
            this.create(args);
        }
        
        @Override
        void upload() throws OpenGLException {
            if (this.allocate) {
                GL11.glTexImage2D(this.target, this.level, this.internalFormat, this.width, this.height, 0, this.format, this.type, (ByteBuffer)null);
                OpenGLException.checkGLError();
            }
            this.renderer.render(this.glTexture, this.srcTextureTopLeft, this.srcTextureTopRight, this.srcTextureBottomLeft, this.srcTextureBottomRight, Minecraft.func_71410_x().func_147110_a(), this.allocate, this.scaledRes);
            GlStateManager.func_179144_i(this.glTexture);
            PixelBuffers.glBindBuffer(35051, this.glPackPbo);
            if (this.glPackPbo == 0) {
                return;
            }
            final int target = this.target;
            GL11.glGetTexImage(target, 0, 32993, 32821, (long)this.pboOffset);
            PixelBuffers.glBindBuffer(35051, 0);
            OpenGLException.checkGLError();
        }
        
        @Override
        public void create(final Object... args) {
            super.create(args);
            this.format = (int)args[9];
            this.type = (int)args[10];
            this.allocate = (boolean)args[11];
            this.srcTextureTopLeft = (Integer)args[12];
            this.srcTextureTopRight = (Integer)args[13];
            this.srcTextureBottomLeft = (Integer)args[14];
            this.srcTextureBottomRight = (Integer)args[15];
            this.renderer = (BranchTextureRenderer)args[16];
            this.glPackPbo = (int)args[17];
            this.pboOffset = (int)args[18];
            this.scaledRes = (ScaledResolution)args[19];
        }
    }
    
    public static class BranchDownload extends TextureUpload
    {
        private int glPackPbo;
        private int pboOffset;
        
        public BranchDownload(final int uploadType) {
            this.uploadType = uploadType;
        }
        
        public BranchDownload(final Object... args) {
            this(5);
            this.create(args);
        }
        
        @Override
        void upload() throws OpenGLException {
            if (this.glPackPbo == 0) {
                return;
            }
            PixelBuffers.glBindBuffer(35051, this.glPackPbo);
            final int target = this.target;
            GL11.glGetTexImage(target, 0, 32993, 32821, (long)this.pboOffset);
            PixelBuffers.glBindBuffer(35051, 0);
            OpenGLException.checkGLError();
        }
        
        @Override
        public void create(final Object... args) {
            super.create(args);
            this.glPackPbo = (int)args[9];
            this.pboOffset = (int)args[10];
        }
    }
}
