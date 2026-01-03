//Decompiled by Procyon!

package xaero.map.region.texture;

import net.minecraft.client.shader.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.gui.*;
import xaero.map.exception.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.*;
import xaero.map.misc.*;
import xaero.map.graphics.*;

public class BranchTextureRenderer
{
    private ImprovedFramebuffer renderFBO;
    private int glEmptyTexture;
    
    public BranchTextureRenderer(final Framebuffer defaultFramebuffer) {
        this.renderFBO = new ImprovedFramebuffer(64, 64, false);
        this.glEmptyTexture = this.renderFBO.field_147617_g;
        this.renderFBO.func_147610_a(true);
        GlStateManager.func_179118_c();
        GlStateManager.func_179084_k();
        GlStateManager.func_179082_a(0.0f, 0.0f, 0.0f, 0.0f);
        GlStateManager.func_179086_m(16384);
        GlStateManager.func_179141_d();
        GlStateManager.func_179147_l();
        this.renderFBO.func_147609_e();
        defaultFramebuffer.func_147610_a(true);
    }
    
    public void render(final int destTexture, final Integer srcTextureTopLeft, final Integer srcTextureTopRight, final Integer srcTextureBottomLeft, final Integer srcTextureBottomRight, final Framebuffer defaultFramebuffer, final boolean justAllocated, final ScaledResolution scaledRes) {
        GlStateManager.func_179144_i(0);
        this.renderFBO.func_147610_a(true);
        this.renderFBO.setFramebufferTexture(destTexture);
        OpenGLException.checkGLError();
        GlStateManager.func_179094_E();
        GlStateManager.func_179096_D();
        GlStateManager.func_179128_n(5889);
        GlStateManager.func_179096_D();
        GlStateManager.func_179130_a(0.0, 64.0, 64.0, 0.0, -1.0, 1.0);
        GlStateManager.func_179128_n(5888);
        GlStateManager.func_179118_c();
        GlStateManager.func_179084_k();
        if (justAllocated) {
            GlStateManager.func_179082_a(0.0f, 0.0f, 0.0f, 1.0f);
            GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            GlStateManager.func_179086_m(16384);
        }
        if (srcTextureTopLeft != null) {
            this.renderCorner(srcTextureTopLeft, 0, 0);
        }
        if (srcTextureTopRight != null) {
            this.renderCorner(srcTextureTopRight, 1, 0);
        }
        if (srcTextureBottomLeft != null) {
            this.renderCorner(srcTextureBottomLeft, 0, 1);
        }
        if (srcTextureBottomRight != null) {
            this.renderCorner(srcTextureBottomRight, 1, 1);
        }
        OpenGLException.checkGLError(false, "updating a map branch texture");
        GlStateManager.func_179141_d();
        GlStateManager.func_179147_l();
        GlStateManager.func_179144_i(0);
        GlStateManager.func_179121_F();
        GlStateManager.func_179128_n(5889);
        final Minecraft mc = Minecraft.func_71410_x();
        Misc.minecraftOrtho(scaledRes);
        GlStateManager.func_179128_n(5888);
        this.renderFBO.func_147609_e();
        mc.func_147110_a().func_147610_a(false);
        GlStateManager.func_179083_b(0, 0, mc.field_71443_c, mc.field_71440_d);
        OpenGLException.checkGLError();
    }
    
    private void renderCorner(final Integer srcTexture, final int cornerX, final int cornerY) {
        final int xOffset = cornerX * 32;
        final int yOffset = (1 - cornerY) * 32;
        GlStateManager.func_179144_i((srcTexture != -1) ? ((int)srcTexture) : this.glEmptyTexture);
        MapRenderHelper.renderTexturedModalRect((float)xOffset, (float)yOffset, 32.0f, 32.0f, 0, 64, 64.0f, -64.0f, 64.0f, 64.0f);
    }
}
