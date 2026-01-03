//Decompiled by Procyon!

package xaero.map.graphics;

import net.minecraft.client.shader.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.*;
import java.nio.*;
import org.lwjgl.opengl.*;

public class ImprovedFramebuffer extends Framebuffer
{
    private int type;
    public int field_147617_g;
    private int depthBuffer;
    private boolean superConstructorWorks;
    private static final int GL_FB_INCOMPLETE_ATTACHMENT = 36054;
    private static final int GL_FB_INCOMPLETE_MISS_ATTACH = 36055;
    private static final int GL_FB_INCOMPLETE_DRAW_BUFFER = 36059;
    private static final int GL_FB_INCOMPLETE_READ_BUFFER = 36060;
    
    public ImprovedFramebuffer(final int width, final int height, final boolean useDepthIn) {
        super(width, height, useDepthIn);
        if (!this.superConstructorWorks) {
            this.func_147613_a(width, height);
        }
    }
    
    public void func_147613_a(final int width, final int height) {
        this.superConstructorWorks = true;
        GlStateManager.func_179126_j();
        if (this.field_147616_f >= 0) {
            this.func_147608_a();
        }
        this.func_147605_b(width, height);
        bindFramebuffer(this.type, 36160, 0);
    }
    
    public void func_147605_b(final int width, final int height) {
        this.field_147621_c = width;
        this.field_147618_d = height;
        this.field_147622_a = width;
        this.field_147620_b = height;
        this.field_147616_f = this.genFrameBuffers();
        if (this.field_147616_f == -1) {
            this.func_147614_f();
            return;
        }
        this.field_147617_g = TextureUtil.func_110996_a();
        if (this.field_147617_g == -1) {
            this.func_147614_f();
            return;
        }
        if (this.field_147619_e) {
            this.depthBuffer = this.genRenderbuffers();
            if (this.depthBuffer == -1) {
                this.func_147614_f();
                return;
            }
        }
        this.func_147607_a(9728);
        GlStateManager.func_179144_i(this.field_147617_g);
        GlStateManager.func_187419_a(3553, 0, 32856, this.field_147622_a, this.field_147620_b, 0, 6408, 5121, (IntBuffer)null);
        bindFramebuffer(this.type, 36160, this.field_147616_f);
        framebufferTexture2D(this.type, 36160, 36064, 3553, this.field_147617_g, 0);
        if (this.field_147619_e) {
            bindRenderbuffer(this.type, 36161, this.depthBuffer);
            if (!this.isStencilEnabled()) {
                renderbufferStorage(this.type, 36161, 33190, this.field_147622_a, this.field_147620_b);
                framebufferRenderbuffer(this.type, 36160, 36096, 36161, this.depthBuffer);
            }
            else {
                renderbufferStorage(this.type, 36161, 35056, this.field_147622_a, this.field_147620_b);
                framebufferRenderbuffer(this.type, 36160, 36096, 36161, this.depthBuffer);
                framebufferRenderbuffer(this.type, 36160, 36128, 36161, this.depthBuffer);
            }
        }
        this.func_147611_b();
        this.func_147614_f();
        this.func_147606_d();
    }
    
    private int genFrameBuffers() {
        int fbo = -1;
        this.type = -1;
        if (GLContext.getCapabilities().OpenGL30) {
            fbo = GL30.glGenFramebuffers();
            this.type = 0;
        }
        else if (GLContext.getCapabilities().GL_ARB_framebuffer_object) {
            fbo = ARBFramebufferObject.glGenFramebuffers();
            this.type = 1;
        }
        else if (GLContext.getCapabilities().GL_EXT_framebuffer_object) {
            fbo = EXTFramebufferObject.glGenFramebuffersEXT();
            this.type = 2;
        }
        return fbo;
    }
    
    public int genRenderbuffers() {
        int rbo = -1;
        switch (this.type) {
            case 0: {
                rbo = GL30.glGenRenderbuffers();
                break;
            }
            case 1: {
                rbo = ARBFramebufferObject.glGenRenderbuffers();
                break;
            }
            case 2: {
                rbo = EXTFramebufferObject.glGenRenderbuffersEXT();
                break;
            }
        }
        return rbo;
    }
    
    public void func_147608_a() {
        this.func_147606_d();
        this.func_147609_e();
        if (this.depthBuffer > -1) {
            this.deleteRenderbuffers(this.depthBuffer);
            this.depthBuffer = -1;
        }
        if (this.field_147617_g > -1) {
            TextureUtil.func_147942_a(this.field_147617_g);
            this.field_147617_g = -1;
        }
        if (this.field_147616_f > -1) {
            bindFramebuffer(this.type, 36160, 0);
            this.deleteFramebuffers(this.field_147616_f);
            this.field_147616_f = -1;
        }
    }
    
    private void deleteFramebuffers(final int framebufferIn) {
        switch (this.type) {
            case 0: {
                GL30.glDeleteFramebuffers(framebufferIn);
                break;
            }
            case 1: {
                ARBFramebufferObject.glDeleteFramebuffers(framebufferIn);
                break;
            }
            case 2: {
                EXTFramebufferObject.glDeleteFramebuffersEXT(framebufferIn);
                break;
            }
        }
    }
    
    private void deleteRenderbuffers(final int renderbuffer) {
        switch (this.type) {
            case 0: {
                GL30.glDeleteRenderbuffers(renderbuffer);
                break;
            }
            case 1: {
                ARBFramebufferObject.glDeleteRenderbuffers(renderbuffer);
                break;
            }
            case 2: {
                EXTFramebufferObject.glDeleteRenderbuffersEXT(renderbuffer);
                break;
            }
        }
    }
    
    public void func_147611_b() {
        final int i = this.checkFramebufferStatus(36160);
        if (i == 36053) {
            return;
        }
        if (i == 36054) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");
        }
        if (i == 36055) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");
        }
        if (i == 36059) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER");
        }
        if (i == 36060) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER");
        }
        throw new RuntimeException("glCheckFramebufferStatus returned unknown status:" + i);
    }
    
    private int checkFramebufferStatus(final int target) {
        switch (this.type) {
            case 0: {
                return GL30.glCheckFramebufferStatus(target);
            }
            case 1: {
                return ARBFramebufferObject.glCheckFramebufferStatus(target);
            }
            case 2: {
                return EXTFramebufferObject.glCheckFramebufferStatusEXT(target);
            }
            default: {
                return -1;
            }
        }
    }
    
    public static void bindFramebuffer(final int type, final int target, int framebufferIn) {
        if (framebufferIn == -1) {
            framebufferIn = 0;
        }
        switch (type) {
            case 0: {
                GL30.glBindFramebuffer(target, framebufferIn);
                break;
            }
            case 1: {
                ARBFramebufferObject.glBindFramebuffer(target, framebufferIn);
                break;
            }
            case 2: {
                EXTFramebufferObject.glBindFramebufferEXT(target, framebufferIn);
                break;
            }
        }
    }
    
    public static void framebufferTexture2D(final int type, final int target, final int attachment, final int textarget, final int texture, final int level) {
        switch (type) {
            case 0: {
                GL30.glFramebufferTexture2D(target, attachment, textarget, texture, level);
                break;
            }
            case 1: {
                ARBFramebufferObject.glFramebufferTexture2D(target, attachment, textarget, texture, level);
                break;
            }
            case 2: {
                EXTFramebufferObject.glFramebufferTexture2DEXT(target, attachment, textarget, texture, level);
                break;
            }
        }
    }
    
    public static void bindRenderbuffer(final int type, final int target, final int renderbuffer) {
        switch (type) {
            case 0: {
                GL30.glBindRenderbuffer(target, renderbuffer);
                break;
            }
            case 1: {
                ARBFramebufferObject.glBindRenderbuffer(target, renderbuffer);
                break;
            }
            case 2: {
                EXTFramebufferObject.glBindRenderbufferEXT(target, renderbuffer);
                break;
            }
        }
    }
    
    public static void renderbufferStorage(final int type, final int target, final int internalFormat, final int width, final int height) {
        switch (type) {
            case 0: {
                GL30.glRenderbufferStorage(target, internalFormat, width, height);
                break;
            }
            case 1: {
                ARBFramebufferObject.glRenderbufferStorage(target, internalFormat, width, height);
                break;
            }
            case 2: {
                EXTFramebufferObject.glRenderbufferStorageEXT(target, internalFormat, width, height);
                break;
            }
        }
    }
    
    public static void framebufferRenderbuffer(final int type, final int target, final int attachment, final int renderBufferTarget, final int renderBuffer) {
        switch (type) {
            case 0: {
                GL30.glFramebufferRenderbuffer(target, attachment, renderBufferTarget, renderBuffer);
                break;
            }
            case 1: {
                ARBFramebufferObject.glFramebufferRenderbuffer(target, attachment, renderBufferTarget, renderBuffer);
                break;
            }
            case 2: {
                EXTFramebufferObject.glFramebufferRenderbufferEXT(target, attachment, renderBufferTarget, renderBuffer);
                break;
            }
        }
    }
    
    public void func_147610_a(final boolean p_147610_1_) {
        bindFramebuffer(this.type, 36160, this.field_147616_f);
        if (p_147610_1_) {
            GlStateManager.func_179083_b(0, 0, this.field_147621_c, this.field_147618_d);
        }
    }
    
    public void func_147609_e() {
        bindFramebuffer(this.type, 36160, 0);
    }
    
    public void func_147612_c() {
        GlStateManager.func_179144_i(this.field_147617_g);
    }
    
    public void func_147606_d() {
        GlStateManager.func_179144_i(0);
    }
    
    public void func_147607_a(final int framebufferFilterIn) {
        this.field_147623_j = framebufferFilterIn;
        GlStateManager.func_179144_i(this.field_147617_g);
        GlStateManager.func_187421_b(3553, 10241, framebufferFilterIn);
        GlStateManager.func_187421_b(3553, 10240, framebufferFilterIn);
        GlStateManager.func_187421_b(3553, 10242, 10496);
        GlStateManager.func_187421_b(3553, 10243, 10496);
        GlStateManager.func_179144_i(0);
    }
    
    public int getFramebufferTexture() {
        return this.field_147617_g;
    }
    
    public void setFramebufferTexture(final int textureId) {
        if (textureId != this.field_147617_g && (this.field_147617_g = textureId) != 0) {
            framebufferTexture2D(this.type, 36160, 36064, 3553, this.field_147617_g, 0);
        }
    }
    
    public void generateMipmaps() {
        switch (this.type) {
            case 0: {
                GL30.glGenerateMipmap(3553);
                break;
            }
            case 1: {
                ARBFramebufferObject.glGenerateMipmap(3553);
                break;
            }
            case 2: {
                EXTFramebufferObject.glGenerateMipmapEXT(3553);
                break;
            }
        }
    }
    
    public int getType() {
        return this.type;
    }
}
