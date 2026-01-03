//Decompiled by Procyon!

package xaero.map.mods.gui;

import net.minecraft.util.*;
import net.minecraft.client.*;
import xaero.map.graphics.*;
import xaero.map.icon.*;
import xaero.map.exception.*;
import net.minecraft.client.renderer.*;
import java.util.*;
import net.minecraft.client.gui.*;
import xaero.map.misc.*;
import net.minecraft.client.renderer.texture.*;

public class WaypointSymbolCreator
{
    private static final int PREFERRED_ATLAS_WIDTH = 1024;
    private static final int ICON_WIDTH = 64;
    public static final ResourceLocation minimapTextures;
    public static final int white = -1;
    private Minecraft mc;
    private XaeroIcon deathSymbolTexture;
    private final Map<String, XaeroIcon> charSymbols;
    private XaeroIconAtlasManager iconManager;
    private ImprovedFramebuffer atlasRenderFramebuffer;
    private XaeroIconAtlas lastAtlas;
    
    public WaypointSymbolCreator() {
        this.mc = Minecraft.func_71410_x();
        this.charSymbols = new HashMap<String, XaeroIcon>();
    }
    
    public XaeroIcon getDeathSymbolTexture(final ScaledResolution scaledRes) {
        if (this.deathSymbolTexture == null) {
            this.createDeathSymbolTexture(scaledRes);
        }
        return this.deathSymbolTexture;
    }
    
    private void createDeathSymbolTexture(final ScaledResolution scaledRes) {
        this.deathSymbolTexture = this.createCharSymbol(true, null, scaledRes);
    }
    
    public XaeroIcon getSymbolTexture(final String c, final ScaledResolution scaledRes) {
        XaeroIcon icon;
        synchronized (this.charSymbols) {
            icon = this.charSymbols.get(c);
        }
        if (icon == null) {
            icon = this.createCharSymbol(false, c, scaledRes);
        }
        return icon;
    }
    
    private XaeroIcon createCharSymbol(final boolean death, final String c, final ScaledResolution scaledRes) {
        if (this.iconManager == null) {
            OpenGLException.checkGLError();
            final int maxTextureSize = GlStateManager.func_187397_v(3379);
            OpenGLException.checkGLError();
            final int atlasTextureSize = Math.min(maxTextureSize, 1024) / 64 * 64;
            this.atlasRenderFramebuffer = new ImprovedFramebuffer(atlasTextureSize, atlasTextureSize, false);
            OpenGLException.checkGLError();
            GlStateManager.func_179150_h(this.atlasRenderFramebuffer.getFramebufferTexture());
            OpenGLException.checkGLError();
            this.atlasRenderFramebuffer.setFramebufferTexture(0);
            this.iconManager = new XaeroIconAtlasManager(64, atlasTextureSize, (List)new ArrayList());
        }
        final XaeroIconAtlas atlas = this.iconManager.getCurrentAtlas();
        final XaeroIcon icon = atlas.createIcon();
        this.atlasRenderFramebuffer.func_147610_a(false);
        GlStateManager.func_179083_b(icon.getOffsetX(), icon.getOffsetY(), 64, 64);
        this.atlasRenderFramebuffer.setFramebufferTexture(atlas.getTextureId());
        this.atlasRenderFramebuffer.func_147611_b();
        if (this.lastAtlas != atlas) {
            GlStateManager.func_179082_a(0.0f, 0.0f, 0.0f, 0.0f);
            GlStateManager.func_179086_m(16384);
            this.lastAtlas = atlas;
        }
        GlStateManager.func_179128_n(5889);
        GlStateManager.func_179096_D();
        GlStateManager.func_179130_a(0.0, 64.0, 64.0, 0.0, -1.0, 1000.0);
        GlStateManager.func_179128_n(5888);
        GlStateManager.func_179094_E();
        GlStateManager.func_179096_D();
        GlStateManager.func_179109_b(2.0f, 2.0f, 0.0f);
        if (!death) {
            GlStateManager.func_179152_a(3.0f, 3.0f, 1.0f);
            this.mc.field_71466_p.func_175063_a(c, 0.0f, 0.0f, -1);
        }
        else {
            GlStateManager.func_179152_a(3.0f, 3.0f, 1.0f);
            GlStateManager.func_179141_d();
            this.mc.func_110434_K().func_110577_a(WaypointSymbolCreator.minimapTextures);
            final ITextureObject texture = this.mc.func_110434_K().func_110581_b(WaypointSymbolCreator.minimapTextures);
            texture.func_174936_b(false, false);
            GlStateManager.func_179131_c(0.2431f, 0.2431f, 0.2431f, 1.0f);
            Gui.func_146110_a(1, 1, 0.0f, 78.0f, 9, 9, 256.0f, 256.0f);
            GlStateManager.func_179131_c(0.9882f, 0.9882f, 0.9882f, 1.0f);
            Gui.func_146110_a(0, 0, 0.0f, 78.0f, 9, 9, 256.0f, 256.0f);
            GlStateManager.func_179131_c(1.0f, 1.0f, 1.0f, 1.0f);
        }
        GlStateManager.func_179128_n(5889);
        Misc.minecraftOrtho(scaledRes);
        GlStateManager.func_179128_n(5888);
        GlStateManager.func_179121_F();
        this.atlasRenderFramebuffer.func_147609_e();
        this.mc.func_147110_a().func_147610_a(false);
        GlStateManager.func_179083_b(0, 0, this.mc.field_71443_c, this.mc.field_71440_d);
        if (death) {
            this.deathSymbolTexture = icon;
        }
        else {
            synchronized (this.charSymbols) {
                this.charSymbols.put(c, icon);
            }
        }
        return icon;
    }
    
    public void resetChars() {
        synchronized (this.charSymbols) {
            this.charSymbols.clear();
        }
        this.lastAtlas = null;
        this.deathSymbolTexture = null;
        if (this.iconManager != null) {
            this.iconManager.clearAtlases();
            this.atlasRenderFramebuffer.setFramebufferTexture(0);
        }
    }
    
    static {
        minimapTextures = new ResourceLocation("xaerobetterpvp", "gui/guis.png");
    }
}
