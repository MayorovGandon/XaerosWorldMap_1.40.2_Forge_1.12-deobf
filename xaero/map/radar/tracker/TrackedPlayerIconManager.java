//Decompiled by Procyon!

package xaero.map.radar.tracker;

import net.minecraft.util.*;
import xaero.map.icon.*;
import net.minecraft.entity.player.*;
import net.minecraft.client.network.*;
import net.minecraft.client.entity.*;
import net.minecraft.client.resources.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.*;
import java.util.*;

public final class TrackedPlayerIconManager
{
    private static final int ICON_WIDTH = 32;
    private static final int PREFERRED_ATLAS_WIDTH = 1024;
    private final TrackedPlayerIconPrerenderer prerenderer;
    private final XaeroIconAtlasManager iconAtlasManager;
    private final Map<ResourceLocation, XaeroIcon> icons;
    private final int iconWidth;
    
    private TrackedPlayerIconManager(final TrackedPlayerIconPrerenderer prerenderer, final XaeroIconAtlasManager iconAtlasManager, final Map<ResourceLocation, XaeroIcon> icons, final int iconWidth) {
        this.prerenderer = prerenderer;
        this.iconAtlasManager = iconAtlasManager;
        this.icons = icons;
        this.iconWidth = iconWidth;
    }
    
    public ResourceLocation getPlayerSkin(final EntityPlayer player, final NetworkPlayerInfo info) {
        ResourceLocation skinTextureLocation = (player instanceof AbstractClientPlayer) ? ((AbstractClientPlayer)player).func_110306_p() : info.func_178837_g();
        if (skinTextureLocation == null) {
            skinTextureLocation = DefaultPlayerSkin.func_177334_a(player.func_110124_au());
        }
        return skinTextureLocation;
    }
    
    public XaeroIcon getIcon(final EntityPlayer player, final NetworkPlayerInfo info, final PlayerTrackerMapElement<?> element, final ScaledResolution scaledRes) {
        final ResourceLocation skinTextureLocation = this.getPlayerSkin(player, info);
        XaeroIcon result = this.icons.get(skinTextureLocation);
        if (result == null) {
            this.icons.put(skinTextureLocation, result = this.iconAtlasManager.getCurrentAtlas().createIcon());
            this.prerenderer.prerender(result, player, this.iconWidth, skinTextureLocation, element, scaledRes);
        }
        return result;
    }
    
    public static final class Builder
    {
        public TrackedPlayerIconManager build() {
            final int maxTextureSize = GlStateManager.func_187397_v(3379);
            final int atlasTextureSize = Math.min(maxTextureSize, 1024) / 32 * 32;
            return new TrackedPlayerIconManager(new TrackedPlayerIconPrerenderer(), new XaeroIconAtlasManager(32, atlasTextureSize, (List)new ArrayList()), new HashMap(), 32, null);
        }
        
        public static Builder begin() {
            return new Builder();
        }
    }
}
