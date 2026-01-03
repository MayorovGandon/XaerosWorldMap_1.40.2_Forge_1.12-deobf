//Decompiled by Procyon!

package xaero.map.radar.tracker;

import xaero.map.element.*;
import net.minecraft.client.*;
import net.minecraft.client.renderer.*;
import xaero.map.animation.*;
import net.minecraft.client.network.*;
import net.minecraft.entity.player.*;
import xaero.map.icon.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.gui.*;
import xaero.map.element.render.*;
import net.minecraft.util.math.*;
import net.minecraft.client.shader.*;
import xaero.map.world.*;
import xaero.map.*;
import xaero.map.common.config.option.*;
import xaero.lib.common.config.option.*;
import xaero.lib.client.config.*;

public final class PlayerTrackerMapElementRenderer extends MapElementRenderer<PlayerTrackerMapElement<?>, PlayerTrackerMapElementRenderContext, PlayerTrackerMapElementRenderer>
{
    private final PlayerTrackerMapElementCollector elementCollector;
    private TrackedPlayerIconManager trackedPlayerIconManager;
    private ElementRenderInfo compatibleRenderInfo;
    
    private PlayerTrackerMapElementRenderer(final PlayerTrackerMapElementCollector elementCollector, final PlayerTrackerMapElementRenderContext context, final PlayerTrackerMapElementRenderProvider<PlayerTrackerMapElementRenderContext> provider, final PlayerTrackerMapElementReader reader) {
        super((Object)context, (MapElementRenderProvider)provider, (MapElementReader)reader);
        this.elementCollector = elementCollector;
    }
    
    public TrackedPlayerIconManager getTrackedPlayerIconManager() {
        return this.trackedPlayerIconManager;
    }
    
    public void preRender(final ElementRenderInfo renderInfo, final boolean shadow) {
        final Minecraft mc = Minecraft.func_71410_x();
        final WorldMapSession mapSession = WorldMapSession.getCurrentSession();
        final MapProcessor mapProcessor = mapSession.getMapProcessor();
        ((PlayerTrackerMapElementRenderContext)this.context).mapDimId = mapProcessor.getMapWorld().getCurrentDimensionId();
        ((PlayerTrackerMapElementRenderContext)this.context).mapDimDiv = mapProcessor.getMapWorld().getCurrentDimension().calculateDimDiv(mc.field_71441_e.field_73011_w);
    }
    
    public void postRender(final ElementRenderInfo renderInfo, final boolean shadow) {
        if (!shadow) {
            this.elementCollector.resetRenderedOnRadarFlags();
        }
    }
    
    public void renderElementShadow(final PlayerTrackerMapElement<?> element, final boolean hovered, final float optionalScale, final double partialX, final double partialY, final ElementRenderInfo renderInfo) {
    }
    
    public boolean renderElement(final PlayerTrackerMapElement<?> e, final boolean hovered, final double optionalDepth, final float optionalScale, final double partialX, final double partialY, final ElementRenderInfo renderInfo) {
        final NetworkPlayerInfo info = Minecraft.func_71410_x().func_147114_u().func_175102_a(e.getPlayerId());
        if (info != null) {
            final Minecraft mc = Minecraft.func_71410_x();
            final FontRenderer fontRenderer = mc.field_71466_p;
            final EntityPlayer clientPlayer = mc.field_71441_e.func_152378_a(e.getPlayerId());
            GlStateManager.func_179094_E();
            final double fadeDest = hovered ? 1.0 : 0.0;
            final boolean firstTime = e.getFadeAnim() == null;
            if (firstTime || e.getFadeAnim().getDestination() != fadeDest) {
                e.setFadeAnim(new SlowingAnimation((e.getFadeAnim() == null) ? 0.0 : e.getFadeAnim().getCurrent(), fadeDest, 0.8, 0.001));
            }
            final float alpha = (float)e.getFadeAnim().getCurrent();
            if (!e.wasRenderedOnRadar() || alpha > 0.0f) {
                GlStateManager.func_179131_c(1.0f, 1.0f, 1.0f, 1.0f);
                if (alpha > 0.0f) {
                    GlStateManager.func_179094_E();
                    GlStateManager.func_179152_a(2.0f, 2.0f, 1.0f);
                    final String name = info.func_178845_a().getName();
                    final int nameWidth = fontRenderer.func_78256_a(name);
                    Gui.func_73734_a(-8 - nameWidth - 2, -6, -7, 6, (int)(alpha * 119.0f) << 24);
                    GlStateManager.func_179147_l();
                    final int textAlphaComponent = (int)(alpha * 255.0f);
                    if (textAlphaComponent > 3) {
                        final int tc = 0xFFFFFF | textAlphaComponent << 24;
                        fontRenderer.func_175063_a(name, (float)(-8 - nameWidth), -4.0f, tc);
                    }
                    GlStateManager.func_179121_F();
                    GlStateManager.func_179131_c(1.0f, 1.0f, 1.0f, 1.0f);
                }
                GlStateManager.func_179137_b(partialX, partialY, 0.0);
                GlStateManager.func_179152_a((2.0f + alpha) / 3.0f, (2.0f + alpha) / 3.0f, 1.0f);
                final XaeroIcon icon = this.getTrackedPlayerIconManager().getIcon(clientPlayer, info, e, renderInfo.scaledResolution);
                final XaeroIconAtlas atlas = icon.getTextureAtlas();
                GlStateManager.func_179144_i(atlas.getTextureId());
                Gui.func_152125_a(-15, -15, (float)(icon.getOffsetX() + 1), (float)(icon.getOffsetY() + 31), 30, -30, 30, 30, (float)atlas.getWidth(), (float)atlas.getWidth());
            }
            GlStateManager.func_179121_F();
        }
        return false;
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
    public void renderElementPre(final int location, final PlayerTrackerMapElement<?> element, final boolean hovered, final Minecraft mc, final double cameraX, final double cameraZ, final double mouseX, final double mouseZ, final float brightness, final double scale, final double screenSizeBasedScale, final TextureManager textureManager, final FontRenderer fontRenderer, final float optionalScale, final double partialX, final double partialY, final boolean cave, final float partialTicks, final ScaledResolution scaledRes) {
        this.renderElementShadow(element, hovered, optionalScale, partialX, partialY, null);
    }
    
    @Deprecated
    public boolean renderElement(final int location, final PlayerTrackerMapElement<?> element, final boolean hovered, final Minecraft mc, final double cameraX, final double cameraZ, final double mouseX, final double mouseZ, final float brightness, final double scale, final double screenSizeBasedScale, final TextureManager textureManager, final FontRenderer fontRenderer, final int elementIndex, final double optionalDepth, final float optionalScale, final double partialX, final double partialY, final boolean cave, final float partialTicks, final ScaledResolution scaledRes) {
        if (this.compatibleRenderInfo == null) {
            final MapProcessor mapProcessor = WorldMapSession.getCurrentSession().getMapProcessor();
            final MapDimension mapDimension = mapProcessor.getMapWorld().getCurrentDimension();
            final double mapDimScale = mapDimension.calculateDimScale();
            this.compatibleRenderInfo = new ElementRenderInfo(ElementRenderLocation.fromIndex(location), mc.func_175606_aa(), (EntityPlayer)mc.field_71439_g, new Vec3d(cameraX, -1.0, cameraZ), mouseX, mouseZ, scale, cave, partialTicks, brightness, screenSizeBasedScale, (Framebuffer)null, scaledRes, mapDimScale, mapDimension.getDimId());
        }
        return this.renderElement(element, hovered, optionalDepth, optionalScale, partialX, partialY, this.compatibleRenderInfo);
    }
    
    public boolean shouldRender(final int location, final boolean shadow) {
        final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        return (boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.DISPLAY_TRACKED_PLAYERS);
    }
    
    public int getOrder() {
        return 200;
    }
    
    public PlayerTrackerMapElementCollector getCollector() {
        return this.elementCollector;
    }
    
    public void update(final Minecraft mc) {
        if (this.trackedPlayerIconManager == null) {
            this.trackedPlayerIconManager = TrackedPlayerIconManager.Builder.begin().build();
        }
        this.elementCollector.update(mc);
    }
    
    public static final class Builder
    {
        private Builder() {
        }
        
        private Builder setDefault() {
            return this;
        }
        
        public PlayerTrackerMapElementRenderer build() {
            final PlayerTrackerMapElementCollector collector = new PlayerTrackerMapElementCollector(WorldMap.playerTrackerSystemManager, (Runnable)new Runnable() {
                @Override
                public void run() {
                    WorldMap.trackedPlayerMenuRenderer.updateFilteredList();
                }
            });
            return new PlayerTrackerMapElementRenderer(collector, new PlayerTrackerMapElementRenderContext(), new PlayerTrackerMapElementRenderProvider(collector), new PlayerTrackerMapElementReader(), null);
        }
        
        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}
