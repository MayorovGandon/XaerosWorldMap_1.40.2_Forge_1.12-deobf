//Decompiled by Procyon!

package xaero.map.radar.tracker;

import xaero.map.graphics.*;
import xaero.map.icon.*;
import net.minecraft.entity.player.*;
import net.minecraft.util.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.*;
import xaero.map.misc.*;

public class TrackedPlayerIconPrerenderer
{
    private ImprovedFramebuffer renderFramebuffer;
    private XaeroIconAtlas lastAtlas;
    private final PlayerTrackerIconRenderer renderer;
    
    public TrackedPlayerIconPrerenderer() {
        this.renderer = new PlayerTrackerIconRenderer();
    }
    
    public void prerender(final XaeroIcon icon, final EntityPlayer player, final int iconWidth, final ResourceLocation skinTextureLocation, final PlayerTrackerMapElement<?> mapElement, final ScaledResolution scaledRes) {
        if (this.renderFramebuffer == null) {
            this.renderFramebuffer = new ImprovedFramebuffer(icon.getTextureAtlas().getWidth(), icon.getTextureAtlas().getWidth(), false);
            GlStateManager.func_179150_h(this.renderFramebuffer.getFramebufferTexture());
            this.renderFramebuffer.setFramebufferTexture(0);
        }
        this.renderFramebuffer.func_147610_a(false);
        GlStateManager.func_179083_b(icon.getOffsetX(), icon.getOffsetY(), iconWidth, iconWidth);
        this.renderFramebuffer.setFramebufferTexture(icon.getTextureAtlas().getTextureId());
        this.renderFramebuffer.func_147611_b();
        if (this.lastAtlas != icon.getTextureAtlas()) {
            GlStateManager.func_179082_a(0.0f, 0.0f, 0.0f, 0.0f);
            GlStateManager.func_179086_m(16384);
            this.lastAtlas = icon.getTextureAtlas();
        }
        GlStateManager.func_179128_n(5889);
        GlStateManager.func_179096_D();
        GlStateManager.func_179130_a(0.0, (double)iconWidth, (double)iconWidth, 0.0, -1.0, 1000.0);
        GlStateManager.func_179128_n(5888);
        GlStateManager.func_179094_E();
        GlStateManager.func_179096_D();
        GlStateManager.func_179109_b((float)(iconWidth / 2), (float)(iconWidth / 2), 0.0f);
        GlStateManager.func_179152_a(3.0f, 3.0f, 1.0f);
        Gui.func_73734_a(-5, -5, 5, 5, -1);
        this.renderer.renderIcon(player, skinTextureLocation);
        final Minecraft mc = Minecraft.func_71410_x();
        GlStateManager.func_179128_n(5889);
        Misc.minecraftOrtho(scaledRes);
        GlStateManager.func_179128_n(5888);
        GlStateManager.func_179121_F();
        this.renderFramebuffer.func_147609_e();
        mc.func_147110_a().func_147610_a(false);
        GlStateManager.func_179083_b(0, 0, mc.field_71443_c, mc.field_71440_d);
    }
}
