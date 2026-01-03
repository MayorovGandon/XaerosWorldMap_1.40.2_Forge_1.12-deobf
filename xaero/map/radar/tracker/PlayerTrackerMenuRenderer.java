//Decompiled by Procyon!

package xaero.map.radar.tracker;

import xaero.map.element.*;
import net.minecraft.client.*;
import xaero.map.*;
import xaero.map.common.config.option.*;
import xaero.lib.common.config.option.*;
import xaero.lib.client.controls.util.*;
import xaero.lib.client.gui.widget.*;
import net.minecraft.util.text.*;
import java.util.function.*;
import xaero.map.gui.*;
import xaero.map.config.util.*;
import xaero.lib.client.config.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.network.*;
import net.minecraft.entity.player.*;
import xaero.map.element.render.*;

public final class PlayerTrackerMenuRenderer extends MapElementMenuRenderer<PlayerTrackerMapElement<?>, PlayerTrackerMenuRenderContext>
{
    private final PlayerTrackerIconRenderer iconRenderer;
    private final PlayerTrackerMapElementRenderer renderer;
    private GuiButton showPlayersButton;
    
    private PlayerTrackerMenuRenderer(final PlayerTrackerMapElementRenderer renderer, final PlayerTrackerIconRenderer iconRenderer, final PlayerTrackerMenuRenderContext context, final PlayerTrackerMapElementRenderProvider<PlayerTrackerMenuRenderContext> provider) {
        super((Object)context, (MapElementRenderProvider)provider);
        this.iconRenderer = iconRenderer;
        this.renderer = renderer;
    }
    
    public void onMapInit(final GuiMap screen, final Minecraft mc, final int width, final int height) {
        super.onMapInit(screen, mc, width, height);
        final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        final boolean trackedPlayers = (boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.DISPLAY_TRACKED_PLAYERS);
        final Tooltip showPlayersTooltip = new Tooltip((ITextComponent)new TextComponentTranslation(trackedPlayers ? "gui.xaero_box_showing_tracked_players" : "gui.xaero_box_hiding_tracked_players", new Object[] { new TextComponentString(KeyMappingUtils.getKeyName(screen.getTrackedPlayerKeyBinding())).func_150255_a(new Style().func_150238_a(TextFormatting.DARK_GREEN)) }), true);
        screen.addGuiButton(this.showPlayersButton = (GuiButton)new GuiTexturedButton(width - 173, height - 33, 20, 20, trackedPlayers ? 197 : 213, 48, 16, 16, WorldMap.guiTextures, (Consumer)new Consumer<GuiButton>() {
            @Override
            public void accept(final GuiButton b) {
                PlayerTrackerMenuRenderer.this.onShowPlayersButton(screen, width, height);
            }
        }, (Supplier)new Supplier<Tooltip>() {
            @Override
            public Tooltip get() {
                return showPlayersTooltip;
            }
        }));
        this.showPlayersButton.field_146124_l = !WorldMapClientConfigUtils.isOptionServerEnforced((ConfigOption)WorldMapProfiledConfigOptions.DISPLAY_TRACKED_PLAYERS);
    }
    
    public void onShowPlayersButton(final GuiMap screen, final int width, final int height) {
        WorldMapClientConfigUtils.tryTogglingCurrentProfileOption((ConfigOption)WorldMapProfiledConfigOptions.DISPLAY_TRACKED_PLAYERS);
        screen.func_146280_a(this.mc, width, height);
    }
    
    protected void beforeMenuRender() {
    }
    
    protected void afterMenuRender() {
    }
    
    public void renderInMenu(final PlayerTrackerMapElement<?> element, final GuiScreen gui, final int mouseX, final int mouseY, final double scale, final boolean enabled, final boolean hovered, final Minecraft mc, final boolean pressed, final int textX) {
        final PlayerTrackerMapElement<?> playerElement = element;
        final NetworkPlayerInfo info = mc.func_147114_u().func_175102_a(playerElement.getPlayerId());
        if (info != null) {
            final EntityPlayer clientPlayer = mc.field_71441_e.func_152378_a(playerElement.getPlayerId());
            this.iconRenderer.renderIcon(clientPlayer, this.renderer.getTrackedPlayerIconManager().getPlayerSkin(clientPlayer, info));
        }
    }
    
    protected void beforeFiltering() {
    }
    
    public int menuStartPos(final int height) {
        return height - 59;
    }
    
    public int menuSearchPadding() {
        return 1;
    }
    
    protected String getFilterPlaceholder() {
        return "gui.xaero_filter_players_by_name";
    }
    
    protected ElementRenderer<? super PlayerTrackerMapElement<?>, ?, ?> getRenderer(final PlayerTrackerMapElement<?> element) {
        return (ElementRenderer<? super PlayerTrackerMapElement<?>, ?, ?>)this.renderer;
    }
    
    public boolean canJumpTo(final PlayerTrackerMapElement<?> element) {
        return !this.renderer.getReader().isHidden((Object)element, this.renderer.getContext());
    }
    
    public static final class Builder
    {
        private PlayerTrackerMapElementRenderer renderer;
        
        private Builder() {
        }
        
        private Builder setDefault() {
            this.setRenderer(null);
            return this;
        }
        
        public Builder setRenderer(final PlayerTrackerMapElementRenderer renderer) {
            this.renderer = renderer;
            return this;
        }
        
        public PlayerTrackerMenuRenderer build() {
            if (this.renderer == null) {
                throw new IllegalStateException();
            }
            return new PlayerTrackerMenuRenderer(this.renderer, new PlayerTrackerIconRenderer(), new PlayerTrackerMenuRenderContext(), new PlayerTrackerMapElementRenderProvider(this.renderer.getCollector()), null);
        }
        
        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}
