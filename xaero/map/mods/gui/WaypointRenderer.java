//Decompiled by Procyon!

package xaero.map.mods.gui;

import xaero.map.mods.*;
import xaero.map.element.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.*;
import xaero.map.common.config.option.*;
import xaero.lib.common.config.option.*;
import xaero.lib.client.config.*;
import xaero.map.animation.*;
import xaero.map.graphics.*;
import net.minecraft.client.renderer.texture.*;
import xaero.map.icon.*;
import xaero.map.gui.*;
import xaero.map.config.primary.option.*;
import xaero.lib.common.config.single.*;
import xaero.lib.common.config.*;
import net.minecraft.client.gui.*;
import xaero.map.element.render.*;
import net.minecraft.util.math.*;
import net.minecraft.entity.player.*;
import net.minecraft.client.shader.*;
import xaero.map.*;
import xaero.map.world.*;

public final class WaypointRenderer extends MapElementRenderer<Waypoint, WaypointRenderContext, WaypointRenderer>
{
    private final SupportXaeroMinimap minimap;
    private final WaypointSymbolCreator symbolCreator;
    private ElementRenderInfo compatibleRenderInfo;
    
    private WaypointRenderer(final WaypointRenderContext context, final WaypointRenderProvider provider, final WaypointReader reader, final SupportXaeroMinimap minimap, final WaypointSymbolCreator symbolCreator) {
        super((Object)context, (MapElementRenderProvider)provider, (MapElementReader)reader);
        this.minimap = minimap;
        this.symbolCreator = symbolCreator;
    }
    
    public WaypointSymbolCreator getSymbolCreator() {
        return this.symbolCreator;
    }
    
    public void renderElementShadow(final Waypoint w, final boolean hovered, final float optionalScale, final double partialX, final double partialY, final ElementRenderInfo renderInfo) {
        GlStateManager.func_179137_b(partialX, partialY, 0.0);
        GlStateManager.func_179152_a(optionalScale * ((WaypointRenderContext)this.context).worldmapWaypointsScale, optionalScale * ((WaypointRenderContext)this.context).worldmapWaypointsScale, 1.0f);
        final float visibilityAlpha = w.isDisabled() ? 0.3f : 1.0f;
        GlStateManager.func_179109_b(-14.0f, -41.0f, 0.0f);
        GlStateManager.func_179131_c(0.0f, 0.0f, 0.0f, renderInfo.brightness * visibilityAlpha / ((WaypointRenderContext)this.context).worldmapWaypointsScale);
        Minecraft.func_71410_x().field_71462_r.func_73729_b(0, 19, 0, 117, 41, 22);
        GlStateManager.func_179131_c(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    public boolean shouldRender(final int location, final boolean shadow) {
        final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        final boolean waypointBackgroundsConfig = (boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.WAYPOINT_BACKGROUNDS);
        final boolean renderWaypoints = (boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.RENDER_WAYPOINTS);
        return renderWaypoints && (!shadow || waypointBackgroundsConfig);
    }
    
    public boolean renderElement(final Waypoint w, final boolean hovered, final double optionalDepth, final float optionalScale, final double partialX, final double partialY, final ElementRenderInfo renderInfo) {
        final boolean renderBackground = hovered || ((WaypointRenderContext)this.context).waypointBackgrounds;
        GlStateManager.func_179137_b(partialX, partialY, 0.0);
        GlStateManager.func_179152_a(optionalScale * ((WaypointRenderContext)this.context).worldmapWaypointsScale, optionalScale * ((WaypointRenderContext)this.context).worldmapWaypointsScale, 1.0f);
        GlStateManager.func_179094_E();
        final float visibilityAlpha = w.isDisabled() ? 0.3f : 1.0f;
        final int color = w.getColor();
        final String symbol = w.getSymbol();
        final int type = w.getType();
        final float red = (color >> 16 & 0xFF) / 255.0f;
        final float green = (color >> 8 & 0xFF) / 255.0f;
        final float blue = (color & 0xFF) / 255.0f;
        int flagU = 35;
        final int flagV = 34;
        int flagW = 30;
        final int flagH = 43;
        if (symbol.length() > 1) {
            flagU += 35;
            flagW += 13;
        }
        if (w.isTemporary()) {
            flagU += 83;
        }
        GlStateManager.func_179109_b(-flagW / 2.0f, (float)(-flagH + 1), 0.0f);
        GlStateManager.func_187428_a(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        if (renderBackground) {
            final TextureManager textureManager = Minecraft.func_71410_x().func_110434_K();
            textureManager.func_110577_a(WorldMap.guiTextures);
            GlStateManager.func_179131_c(red * visibilityAlpha, green * visibilityAlpha, blue * visibilityAlpha, visibilityAlpha);
            Gui.func_146110_a(0, 0, (float)flagU, (float)flagV, flagW, flagH, 256.0f, 256.0f);
        }
        GlStateManager.func_179121_F();
        final float oldDestAlpha = w.getDestAlpha();
        if (hovered) {
            w.setDestAlpha(255.0f);
        }
        else {
            w.setDestAlpha(0.0f);
        }
        if (oldDestAlpha != w.getDestAlpha()) {
            w.setAlphaAnim(new SlowingAnimation((double)w.getAlpha(), (double)w.getDestAlpha(), 0.8, 1.0));
        }
        if (w.getAlphaAnim() != null) {
            w.setAlpha((float)w.getAlphaAnim().getCurrent());
        }
        final float alpha = w.getAlpha();
        XaeroIcon symbolIcon = null;
        int symbolVerticalOffset = 0;
        int symbolWidth = 0;
        final FontRenderer fontRenderer = Minecraft.func_71410_x().field_71466_p;
        final int stringWidth = fontRenderer.func_78256_a(symbol);
        final int symbolFrameWidth = (stringWidth / 2 > 4) ? 62 : 32;
        if (type != 1 && alpha < 200.0f) {
            symbolVerticalOffset = 5;
            symbolWidth = (stringWidth - 1) * 3;
            symbolIcon = this.symbolCreator.getSymbolTexture(symbol, renderInfo.scaledResolution);
        }
        else if (type == 1) {
            symbolVerticalOffset = 3;
            symbolWidth = 27;
            symbolIcon = this.symbolCreator.getDeathSymbolTexture(renderInfo.scaledResolution);
        }
        if (symbolIcon != null) {
            GlStateManager.func_179094_E();
            GlStateManager.func_179109_b(-1.0f - symbolWidth / 2.0f, (float)(62 + (renderBackground ? (-43 + symbolVerticalOffset - 1) : -12)), 0.0f);
            GlStateManager.func_179152_a(1.0f, -1.0f, 1.0f);
            GlStateManager.func_179144_i(symbolIcon.getTextureAtlas().getTextureId());
            GlStateManager.func_179131_c(visibilityAlpha, visibilityAlpha, visibilityAlpha, visibilityAlpha);
            Gui.func_146110_a(0, 0, (float)(symbolIcon.getOffsetX() + 1), (float)(symbolIcon.getOffsetY() + 1), symbolFrameWidth, 62, (float)symbolIcon.getTextureAtlas().getWidth(), (float)symbolIcon.getTextureAtlas().getWidth());
            GlStateManager.func_179121_F();
        }
        if ((int)alpha > 0) {
            final int tc = (int)alpha << 24 | 0xFFFFFF;
            final String name = w.getName();
            final int len = fontRenderer.func_78256_a(name);
            GlStateManager.func_179109_b(0.0f, (float)(renderBackground ? -38 : -11), 0.0f);
            GlStateManager.func_179152_a(3.0f, 3.0f, 1.0f);
            final int bgLen = Math.max(len + 2, 10);
            Gui.func_73734_a(-bgLen / 2, -1, bgLen / 2, 9, MapRenderHelper.getColorInt(red, green, blue, alpha / 255.0f));
            Gui.func_73734_a(-bgLen / 2, -1, bgLen / 2, 8, MapRenderHelper.getColorInt(0.0f, 0.0f, 0.0f, alpha / 255.0f * 200.0f / 255.0f));
            if ((int)alpha > 3) {
                GlStateManager.func_179109_b(0.0f, 0.0f, 1.0f);
                GlStateManager.func_179147_l();
                fontRenderer.func_78276_b(name, -(len - 1) / 2, 0, tc);
            }
        }
        GlStateManager.func_179131_c(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.func_179147_l();
        GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        return false;
    }
    
    public void preRender(final ElementRenderInfo renderInfo, final boolean shadow) {
        ((WaypointRenderContext)this.context).deathpoints = this.minimap.getDeathpoints();
        final Minecraft mc = Minecraft.func_71410_x();
        ((WaypointRenderContext)this.context).userScale = ((mc.field_71462_r != null && mc.field_71462_r instanceof GuiMap) ? ((GuiMap)mc.field_71462_r).getUserScale() : 1.0);
        final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        ((WaypointRenderContext)this.context).waypointBackgrounds = (boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.WAYPOINT_BACKGROUNDS);
        ((WaypointRenderContext)this.context).minZoomForLocalWaypoints = (double)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.MIN_ZOOM_LOCAL_WAYPOINTS);
        final SingleConfigManager<Config> primaryConfigManager = (SingleConfigManager<Config>)configManager.getPrimaryConfigManager();
        ((WaypointRenderContext)this.context).showDisabledWaypoints = (boolean)primaryConfigManager.getEffective((ConfigOption)WorldMapPrimaryClientConfigOptions.DISPLAY_DISABLED_WAYPOINTS);
    }
    
    public void postRender(final ElementRenderInfo renderInfo, final boolean shadow) {
    }
    
    @Deprecated
    public void beforeRender(final int location, final Minecraft mc, final double cameraX, final double cameraZ, final double mouseX, final double mouseZ, final float brightness, final double scale, final double screenSizeBasedScale, final TextureManager textureManager, final FontRenderer fontRenderer, final ScaledResolution scaledRes, final boolean pre) {
        this.preRender(null, pre);
    }
    
    @Deprecated
    public void afterRender(final int location, final Minecraft mc, final double cameraX, final double cameraZ, final double mouseX, final double mouseZ, final float brightness, final double scale, final double screenSizeBasedScale, final TextureManager textureManager, final FontRenderer fontRenderer, final ScaledResolution scaledRes, final boolean pre) {
        this.postRender(null, pre);
        this.compatibleRenderInfo = null;
    }
    
    @Deprecated
    public void renderElementPre(final int location, final Waypoint element, final boolean hovered, final Minecraft mc, final double cameraX, final double cameraZ, final double mouseX, final double mouseZ, final float brightness, final double scale, final double screenSizeBasedScale, final TextureManager textureManager, final FontRenderer fontRenderer, final float optionalScale, final double partialX, final double partialY, final boolean cave, final float partialTicks, final ScaledResolution scaledRes) {
        if (this.compatibleRenderInfo == null) {
            final MapProcessor mapProcessor = WorldMapSession.getCurrentSession().getMapProcessor();
            final MapDimension mapDimension = mapProcessor.getMapWorld().getCurrentDimension();
            final double mapDimScale = mapDimension.calculateDimScale();
            this.compatibleRenderInfo = new ElementRenderInfo(ElementRenderLocation.fromIndex(location), mc.func_175606_aa(), (EntityPlayer)mc.field_71439_g, new Vec3d(cameraX, -1.0, cameraZ), mouseX, mouseZ, scale, cave, partialTicks, brightness, screenSizeBasedScale, (Framebuffer)null, scaledRes, mapDimScale, mapDimension.getDimId());
        }
        this.renderElementShadow(element, hovered, optionalScale, partialX, partialY, this.compatibleRenderInfo);
    }
    
    @Deprecated
    public boolean renderElement(final int location, final Waypoint element, final boolean hovered, final Minecraft mc, final double cameraX, final double cameraZ, final double mouseX, final double mouseZ, final float brightness, final double scale, final double screenSizeBasedScale, final TextureManager textureManager, final FontRenderer fontRenderer, final int elementIndex, final double optionalDepth, final float optionalScale, final double partialX, final double partialY, final boolean cave, final float partialTicks, final ScaledResolution scaledRes) {
        if (this.compatibleRenderInfo == null) {
            final MapProcessor mapProcessor = WorldMapSession.getCurrentSession().getMapProcessor();
            final MapDimension mapDimension = mapProcessor.getMapWorld().getCurrentDimension();
            final double mapDimScale = mapDimension.calculateDimScale();
            this.compatibleRenderInfo = new ElementRenderInfo(ElementRenderLocation.fromIndex(location), mc.func_175606_aa(), (EntityPlayer)mc.field_71439_g, new Vec3d(cameraX, -1.0, cameraZ), mouseX, mouseZ, scale, cave, partialTicks, brightness, screenSizeBasedScale, (Framebuffer)null, scaledRes, mapDimScale, mapDimension.getDimId());
        }
        return this.renderElement(element, hovered, optionalDepth, optionalScale, partialX, partialY, this.compatibleRenderInfo);
    }
    
    public int getOrder() {
        return 200;
    }
    
    public boolean shouldBeDimScaled() {
        return false;
    }
    
    public static final class Builder
    {
        private SupportXaeroMinimap minimap;
        private WaypointSymbolCreator symbolCreator;
        
        private Builder() {
        }
        
        private Builder setDefault() {
            this.setMinimap(null);
            this.setSymbolCreator(null);
            return this;
        }
        
        public Builder setMinimap(final SupportXaeroMinimap minimap) {
            this.minimap = minimap;
            return this;
        }
        
        public Builder setSymbolCreator(final WaypointSymbolCreator symbolCreator) {
            this.symbolCreator = symbolCreator;
            return this;
        }
        
        public WaypointRenderer build() {
            if (this.minimap == null || this.symbolCreator == null) {
                throw new IllegalStateException();
            }
            return new WaypointRenderer(new WaypointRenderContext(), new WaypointRenderProvider(this.minimap), new WaypointReader(), this.minimap, this.symbolCreator, null);
        }
        
        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}
