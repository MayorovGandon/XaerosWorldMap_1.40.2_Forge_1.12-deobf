//Decompiled by Procyon!

package xaero.map.mods.gui;

import xaero.map.element.*;
import net.minecraft.client.*;
import xaero.common.minimap.waypoints.*;
import xaero.map.mods.*;
import xaero.common.gui.*;
import xaero.map.*;
import java.io.*;
import xaero.lib.client.gui.widget.dropdown.*;
import xaero.common.settings.*;
import xaero.lib.client.controls.util.*;
import xaero.map.common.config.option.*;
import xaero.lib.common.config.option.*;
import xaero.map.config.primary.option.*;
import xaero.lib.client.gui.widget.*;
import net.minecraft.util.text.*;
import xaero.common.*;
import xaero.hud.minimap.common.config.option.*;
import java.util.function.*;
import xaero.map.gui.*;
import xaero.map.config.util.*;
import xaero.lib.client.config.*;
import xaero.lib.common.config.single.*;
import xaero.lib.common.config.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.texture.*;
import xaero.map.element.render.*;

public class WaypointMenuRenderer extends MapElementMenuRenderer<Waypoint, WaypointMenuRenderContext>
{
    private final WaypointRenderer renderer;
    private GuiButton renderWaypointsButton;
    private GuiButton showDisabledButton;
    private GuiButton closeMenuWhenHoppingButton;
    private GuiButton currentMapWaypointsButton;
    private GuiButton renderAllSetsButton;
    
    public WaypointMenuRenderer(final WaypointMenuRenderContext context, final WaypointMenuRenderProvider provider, final WaypointRenderer renderer) {
        super((Object)context, (MapElementRenderProvider)provider);
        this.renderer = renderer;
    }
    
    public void onMapInit(final GuiMap screen, final Minecraft mc, final int width, final int height, final WaypointWorld waypointWorld, final IXaeroMinimap modMain, final XaeroMinimapSession minimapSession) {
        super.onMapInit(screen, mc, width, height);
        final boolean canCreate = SupportMods.xaeroMinimap.compatibilityVersion >= 6;
        final GuiWaypointSets sets = (waypointWorld != null) ? new GuiWaypointSets(canCreate, waypointWorld) : null;
        IDropDownWidgetCallback setsDropdownCallback = null;
        if (sets != null) {
            setsDropdownCallback = (IDropDownWidgetCallback)new IDropDownWidgetCallback() {
                public boolean onSelected(final DropDownWidget menu, final int selected) {
                    if (canCreate && selected == menu.size() - 1) {
                        final GuiNewSet guiNewSet = new GuiNewSet(modMain, minimapSession, (GuiScreen)screen, (GuiScreen)screen, waypointWorld);
                        Minecraft.func_71410_x().func_147108_a((GuiScreen)guiNewSet);
                        return false;
                    }
                    sets.setCurrentSet(selected);
                    waypointWorld.setCurrent(sets.getCurrentSetKey());
                    minimapSession.getWaypointsManager().updateWaypoints();
                    try {
                        modMain.getSettings().saveWaypoints(waypointWorld);
                    }
                    catch (IOException e) {
                        WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
                    }
                    return true;
                }
            };
        }
        final DropDownWidget setsDropdown = (sets == null) ? null : DropDownWidget.Builder.begin().setOptions(sets.getOptions()).setX(width - 173).setY(height - 56).setW(151).setSelected(Integer.valueOf(sets.getCurrentSet())).setCallback(setsDropdownCallback).setContainer((IDropDownContainer)screen).setOpeningUp(true).build();
        if (setsDropdown != null) {
            screen.addWidget(setsDropdown);
        }
        final ITextComponent fullWaypointMenuTooltipText = (ITextComponent)new TextComponentTranslation("gui.xaero_box_full_waypoints_menu", new Object[] { new TextComponentString(KeyMappingUtils.getKeyName(ModSettings.keyWaypoints)).func_150255_a(new Style().func_150238_a(TextFormatting.DARK_GREEN)) });
        final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        final boolean renderWaypoints = (boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.RENDER_WAYPOINTS);
        final SingleConfigManager<Config> primaryConfigManager = (SingleConfigManager<Config>)configManager.getPrimaryConfigManager();
        final boolean onlyCurrentMapWaypoints = (boolean)primaryConfigManager.getEffective((ConfigOption)WorldMapPrimaryClientConfigOptions.ONLY_CURRENT_MAP_WAYPOINTS);
        final boolean showDisabledWaypoints = (boolean)primaryConfigManager.getEffective((ConfigOption)WorldMapPrimaryClientConfigOptions.DISPLAY_DISABLED_WAYPOINTS);
        final boolean closeWaypointsWhenHopping = (boolean)primaryConfigManager.getEffective((ConfigOption)WorldMapPrimaryClientConfigOptions.CLOSE_WAYPOINTS_AFTER_HOP);
        final Tooltip fullWaypointMenuTooltip = new Tooltip(fullWaypointMenuTooltipText, true);
        final Tooltip onlyCurrentMapWaypointsTooltip = new Tooltip(onlyCurrentMapWaypoints ? "gui.xaero_box_only_current_map_waypoints" : "gui.xaero_box_waypoints_selected_by_minimap", (Style)null, true);
        final Tooltip renderingWaypointsTooltip = new Tooltip((ITextComponent)new TextComponentTranslation(renderWaypoints ? "gui.xaero_box_rendering_waypoints" : "gui.xaero_box_not_rendering_waypoints", new Object[] { new TextComponentString(KeyMappingUtils.getKeyName(ModSettings.keyToggleMapWaypoints)).func_150255_a(new Style().func_150238_a(TextFormatting.DARK_GREEN)) }), true);
        final ClientConfigManager minimapConfigManager = HudMod.INSTANCE.getHudConfigs().getClientConfigManager();
        final boolean renderAllSetsConfig = (boolean)minimapConfigManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.WAYPOINTS_ALL_SETS);
        final Tooltip renderAllSetsTooltip = new Tooltip((ITextComponent)new TextComponentTranslation(renderAllSetsConfig ? "gui.xaero_box_rendering_all_sets" : "gui.xaero_box_rendering_current_set", new Object[] { new TextComponentString(KeyMappingUtils.getKeyName(ModSettings.keyAllSets)).func_150255_a(new Style().func_150238_a(TextFormatting.DARK_GREEN)) }), true);
        final Tooltip showingDisabledTooltip = new Tooltip(showDisabledWaypoints ? "gui.xaero_box_showing_disabled" : "gui.xaero_box_hiding_disabled", (Style)null, true);
        final Tooltip closeWhenHoppingTooltip = new Tooltip(closeWaypointsWhenHopping ? "gui.xaero_box_closing_menu_when_hopping" : "gui.xaero_box_not_closing_menu_when_hopping", (Style)null, true);
        screen.addGuiButton((GuiButton)new GuiTexturedButton(width - 173, height - 20, 20, 20, 229, 0, 16, 16, WorldMap.guiTextures, (Consumer)new Consumer<GuiButton>() {
            @Override
            public void accept(final GuiButton b) {
                WaypointMenuRenderer.this.onFullMenuButton(b, screen);
            }
        }, (Supplier)new Supplier<Tooltip>() {
            @Override
            public Tooltip get() {
                return fullWaypointMenuTooltip;
            }
        }));
        screen.addGuiButton(this.currentMapWaypointsButton = (GuiButton)new GuiTexturedButton(width - 153, height - 20, 20, 20, onlyCurrentMapWaypoints ? 213 : 229, 16, 16, 16, WorldMap.guiTextures, (Consumer)new Consumer<GuiButton>() {
            @Override
            public void accept(final GuiButton b) {
                WaypointMenuRenderer.this.onCurrentMapWaypointsButton(b, screen, width, height);
            }
        }, (Supplier)new Supplier<Tooltip>() {
            @Override
            public Tooltip get() {
                return onlyCurrentMapWaypointsTooltip;
            }
        }));
        screen.addGuiButton(this.renderWaypointsButton = (GuiButton)new GuiTexturedButton(width - 133, height - 20, 20, 20, renderWaypoints ? 229 : 213, 48, 16, 16, WorldMap.guiTextures, (Consumer)new Consumer<GuiButton>() {
            @Override
            public void accept(final GuiButton b) {
                WaypointMenuRenderer.this.onRenderWaypointsButton(screen, width, height);
            }
        }, (Supplier)new Supplier<Tooltip>() {
            @Override
            public Tooltip get() {
                return renderingWaypointsTooltip;
            }
        }));
        screen.addGuiButton(this.renderAllSetsButton = (GuiButton)new GuiTexturedButton(width - 113, height - 20, 20, 20, renderAllSetsConfig ? 97 : 81, 16, 16, 16, WorldMap.guiTextures, (Consumer)new Consumer<GuiButton>() {
            @Override
            public void accept(final GuiButton b) {
                WaypointMenuRenderer.this.onRenderAllSetsButton(b, screen, width, height);
            }
        }, (Supplier)new Supplier<Tooltip>() {
            @Override
            public Tooltip get() {
                return renderAllSetsTooltip;
            }
        }));
        screen.addGuiButton(this.showDisabledButton = (GuiButton)new GuiTexturedButton(width - 93, height - 20, 20, 20, showDisabledWaypoints ? 133 : 149, 16, 16, 16, WorldMap.guiTextures, (Consumer)new Consumer<GuiButton>() {
            @Override
            public void accept(final GuiButton b) {
                WaypointMenuRenderer.this.onShowDisabledButton(b, screen, width, height);
            }
        }, (Supplier)new Supplier<Tooltip>() {
            @Override
            public Tooltip get() {
                return showingDisabledTooltip;
            }
        }));
        screen.addGuiButton(this.closeMenuWhenHoppingButton = (GuiButton)new GuiTexturedButton(width - 73, height - 20, 20, 20, closeWaypointsWhenHopping ? 181 : 197, 16, 16, 16, WorldMap.guiTextures, (Consumer)new Consumer<GuiButton>() {
            @Override
            public void accept(final GuiButton b) {
                WaypointMenuRenderer.this.onCloseMenuWhenHoppingButton(b, screen, width, height);
            }
        }, (Supplier)new Supplier<Tooltip>() {
            @Override
            public Tooltip get() {
                return closeWhenHoppingTooltip;
            }
        }));
        this.renderWaypointsButton.field_146124_l = !WorldMapClientConfigUtils.isOptionServerEnforced((ConfigOption)WorldMapProfiledConfigOptions.RENDER_WAYPOINTS);
    }
    
    public void onRenderWaypointsButton(final GuiMap screen, final int width, final int height) {
        WorldMapClientConfigUtils.tryTogglingCurrentProfileOption((ConfigOption)WorldMapProfiledConfigOptions.RENDER_WAYPOINTS);
        screen.func_146280_a(this.mc, width, height);
    }
    
    private void onFullMenuButton(final GuiButton b, final GuiMap screen) {
        SupportMods.xaeroMinimap.openWaypointsMenu(this.mc, screen);
    }
    
    private void onRenderAllSetsButton(final GuiButton b, final GuiMap screen, final int width, final int height) {
        SupportMods.xaeroMinimap.handleMinimapKeyBinding(ModSettings.keyAllSets, screen);
    }
    
    private void onShowDisabledButton(final GuiButton b, final GuiMap screen, final int width, final int height) {
        WorldMapClientConfigUtils.togglePrimaryOption((ConfigOption)WorldMapPrimaryClientConfigOptions.DISPLAY_DISABLED_WAYPOINTS);
        screen.func_146280_a(this.mc, width, height);
    }
    
    private void onCloseMenuWhenHoppingButton(final GuiButton b, final GuiMap screen, final int width, final int height) {
        WorldMapClientConfigUtils.togglePrimaryOption((ConfigOption)WorldMapPrimaryClientConfigOptions.CLOSE_WAYPOINTS_AFTER_HOP);
        screen.func_146280_a(this.mc, width, height);
    }
    
    private void onCurrentMapWaypointsButton(final GuiButton b, final GuiMap screen, final int width, final int height) {
        WorldMapClientConfigUtils.togglePrimaryOption((ConfigOption)WorldMapPrimaryClientConfigOptions.ONLY_CURRENT_MAP_WAYPOINTS);
        screen.func_146280_a(this.mc, width, height);
    }
    
    public void renderInMenu(final Waypoint element, final GuiScreen gui, final int mouseX, final int mouseY, final double scale, final boolean enabled, final boolean hovered, final Minecraft mc, final boolean pressed, final int textX) {
        final Waypoint w = element;
        final boolean disabled = w.isDisabled();
        final boolean temporary = w.isTemporary();
        final int type = w.getType();
        final int color = w.getColor();
        final String symbol = w.getSymbol();
        GlStateManager.func_179109_b(-4.0f, -4.0f, 0.0f);
        GlStateManager.func_179147_l();
        if (type == 1) {
            Gui.func_73734_a(0, 0, 9, 9, color);
            mc.func_110434_K().func_110577_a(Waypoint.minimapTextures);
            final ITextureObject texture = mc.func_110434_K().func_110581_b(Waypoint.minimapTextures);
            texture.func_174936_b(false, false);
            GlStateManager.func_179131_c(0.2431f, 0.2431f, 0.2431f, 1.0f);
            gui.func_73729_b(1, 1, 0, 78, 9, 9);
            GlStateManager.func_179131_c(0.9882f, 0.9882f, 0.9882f, 1.0f);
            gui.func_73729_b(0, 0, 0, 78, 9, 9);
            GlStateManager.func_179131_c(1.0f, 1.0f, 1.0f, 1.0f);
        }
        else {
            GlStateManager.func_179090_x();
            Gui.func_73734_a(0, 0, 9, 9, color);
        }
        GlStateManager.func_179131_c(1.0f, 1.0f, 1.0f, 1.0f);
        if (type != 1) {
            mc.field_71466_p.func_175063_a(symbol, (float)(5 - mc.field_71466_p.func_78256_a(symbol) / 2), 1.0f, -1);
        }
        GlStateManager.func_179147_l();
        int infoIconOffset = 10;
        if (disabled) {
            GlStateManager.func_179098_w();
            GlStateManager.func_179131_c(1.0f, 1.0f, 0.0f, 1.0f);
            Minecraft.func_71410_x().func_110434_K().func_110577_a(WorldMap.guiTextures);
            gui.func_73729_b(textX - 1 - infoIconOffset, 0, 173, 16, 8, 8);
            infoIconOffset += 10;
        }
        if (temporary) {
            GlStateManager.func_179098_w();
            GlStateManager.func_179131_c(1.0f, 0.0f, 0.0f, 1.0f);
            Minecraft.func_71410_x().func_110434_K().func_110577_a(WorldMap.guiTextures);
            gui.func_73729_b(textX - 1 - infoIconOffset, 0, 165, 16, 8, 8);
            infoIconOffset += 10;
        }
        GlStateManager.func_179131_c(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    public int menuStartPos(final int height) {
        return height - 59;
    }
    
    public int menuSearchPadding() {
        return 14;
    }
    
    protected String getFilterPlaceholder() {
        return "gui.xaero_filter_waypoints_by_name";
    }
    
    protected ElementRenderer<? super Waypoint, ?, ?> getRenderer(final Waypoint element) {
        return (ElementRenderer<? super Waypoint, ?, ?>)this.renderer;
    }
    
    protected void beforeFiltering() {
    }
    
    protected void beforeMenuRender() {
    }
    
    protected void afterMenuRender() {
    }
}
