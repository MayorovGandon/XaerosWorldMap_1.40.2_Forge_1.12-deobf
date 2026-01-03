//Decompiled by Procyon!

package xaero.map.gui;

import xaero.lib.client.gui.config.*;
import xaero.lib.client.gui.config.context.*;
import xaero.map.mods.*;
import xaero.map.common.config.*;
import xaero.lib.client.gui.widget.*;
import net.minecraft.client.resources.*;
import xaero.map.settings.*;
import xaero.lib.common.gui.widget.*;
import xaero.lib.common.config.util.*;
import net.minecraft.util.text.*;
import xaero.lib.client.gui.*;
import xaero.map.common.config.option.*;
import xaero.lib.common.config.option.*;
import xaero.map.config.primary.option.*;
import xaero.map.world.*;
import xaero.map.*;
import net.minecraft.client.*;
import xaero.map.controls.*;
import xaero.lib.client.controls.util.*;
import java.util.function.*;
import net.minecraft.client.gui.*;
import java.io.*;

public class GuiWorldMapSettings extends EditConfigScreen
{
    public static final ITextComponent PLAYER_TELEPORT_COMMAND_TOOLTIP;
    public static final ITextComponent MAP_TELEPORT_COMMAND_TOOLTIP;
    
    public GuiWorldMapSettings(final IEditConfigScreenContext context) {
        this(null, context);
    }
    
    public GuiWorldMapSettings(final GuiScreen parent, final IEditConfigScreenContext context) {
        this(parent, null, context);
    }
    
    public GuiWorldMapSettings(final GuiScreen parent, final GuiScreen escapeScreen, final IEditConfigScreenContext context) {
        super((ITextComponent)new TextComponentTranslation("gui.xaero_world_map_settings", new Object[0]), parent, escapeScreen, context, WorldMap.INSTANCE.getConfigs());
        final ScreenSwitchSettingEntry minimapEntry = new ScreenSwitchSettingEntry("gui.xaero_wm_minimap_settings", new BiFunction<GuiScreen, GuiScreen, GuiScreen>() {
            @Override
            public GuiScreen apply(final GuiScreen current, final GuiScreen escape) {
                return SupportMods.xaeroMinimap.getSettingsScreen(current);
            }
        }, SupportMods.minimap() ? null : new Tooltip(WorldMapConfigConstants.MINIMAP_TOOLTIP), SupportMods.minimap());
        final ScreenSwitchSettingEntry resetEntry = new ScreenSwitchSettingEntry("gui.xaero_wm_reset_config_profile_default", new BiFunction<GuiScreen, GuiScreen, GuiScreen>() {
            @Override
            public GuiScreen apply(final GuiScreen current, final GuiScreen escape) {
                return (GuiScreen)new ConfirmScreenBase(current, escape, true, (GuiYesNoCallback)new GuiYesNoCallback() {
                    public void func_73878_a(final boolean r, final int i) {
                        GuiWorldMapSettings.this.resetConfirmResult(r, (GuiScreen)GuiWorldMapSettings.this, escapeScreen);
                    }
                }, I18n.func_135052_a("gui.xaero_wm_reset_config_profile_default_message", new Object[0]), I18n.func_135052_a("gui.xaero_wm_reset_config_profile_default_message2", new Object[0]), 0);
            }
        }, null, true, false);
        final ScreenSwitchSettingEntry mapTeleportCommandEntry = new ScreenSwitchSettingEntry("gui.xaero_wm_teleport_command", new BiFunction<GuiScreen, GuiScreen, GuiScreen>() {
            @Override
            public GuiScreen apply(final GuiScreen current, final GuiScreen escape) {
                return (GuiScreen)new GuiMapTpCommand(current, escape);
            }
        }, new Tooltip(context.isClientSide() ? (ModSettings.canEditIngameSettings() ? GuiWorldMapSettings.MAP_TELEPORT_COMMAND_TOOLTIP : WorldMapConfigConstants.INGAME_TOOLTIP) : GuiConstants.SETTING_ENTRY_WRONG_CONTEXT_COMPONENT), context.isClientSide() && ModSettings.canEditIngameSettings());
        final ScreenSwitchSettingEntry playerTeleportCommandEntry = new ScreenSwitchSettingEntry("gui.xaero_wm_player_teleport_command", new BiFunction<GuiScreen, GuiScreen, GuiScreen>() {
            @Override
            public GuiScreen apply(final GuiScreen current, final GuiScreen escape) {
                return (GuiScreen)new GuiPlayerTpCommand(current, escape);
            }
        }, new Tooltip(context.isClientSide() ? (ModSettings.canEditIngameSettings() ? GuiWorldMapSettings.PLAYER_TELEPORT_COMMAND_TOOLTIP : WorldMapConfigConstants.INGAME_TOOLTIP) : GuiConstants.SETTING_ENTRY_WRONG_CONTEXT_COMPONENT), context.isClientSide() && ModSettings.canEditIngameSettings());
        final ISettingEntry ignoreHeightmapsEntry = (ISettingEntry)new CustomSettingEntry((BooleanSupplier)new BooleanSupplier() {
            @Override
            public boolean getAsBoolean() {
                return false;
            }
        }, (ITextComponent)new TextComponentTranslation("gui.xaero_wm_ignore_heightmaps", new Object[0]), context.isClientSide() ? new TooltipInfo("gui.xaero_wm_box_ignore_heightmaps") : new TooltipInfo(GuiConstants.SETTING_ENTRY_WRONG_CONTEXT_COMPONENT, false, true), false, (Supplier)new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                final WorldMapSession session = WorldMapSession.getCurrentSession();
                if (session == null) {
                    return null;
                }
                return session.getMapProcessor().getMapWorld().isIgnoreHeightmaps();
            }
        }, 0, 1, (IntFunction)new IntFunction<Boolean>() {
            @Override
            public Boolean apply(final int i) {
                return i == 1;
            }
        }, (Function)new Function<Boolean, ITextComponent>() {
            @Override
            public ITextComponent apply(final Boolean v) {
                return v ? ConfigConstants.ON : ConfigConstants.OFF;
            }
        }, (BiConsumer)new BiConsumer<Boolean, Boolean>() {
            @Override
            public void accept(final Boolean oldValue, final Boolean newValue) {
                final WorldMapSession session = WorldMapSession.getCurrentSession();
                if (session == null) {
                    return;
                }
                final MapWorld mapWorld = session.getMapProcessor().getMapWorld();
                mapWorld.setIgnoreHeightmaps(newValue);
                mapWorld.saveConfig();
                WorldMap.settings.updateRegionCacheHashCode();
            }
        }, (BooleanSupplier)new BooleanSupplier() {
            @Override
            public boolean getAsBoolean() {
                return context.isClientSide() && WorldMapSession.getCurrentSession() != null;
            }
        });
        final ISettingEntry fullReloadEntry = this.getEntryForFullReloadOption(false);
        final ISettingEntry fullResaveEntry = this.getEntryForFullReloadOption(true);
        final ISettingEntry pauseRequestsEntry = (ISettingEntry)new CustomSettingEntry((BooleanSupplier)new BooleanSupplier() {
            @Override
            public boolean getAsBoolean() {
                return false;
            }
        }, (ITextComponent)new TextComponentString("Pause Requests"), (TooltipInfo)null, false, (Supplier)new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return WorldMap.pauseRequests;
            }
        }, 0, 1, (IntFunction)new IntFunction<Boolean>() {
            @Override
            public Boolean apply(final int i) {
                return i == 1;
            }
        }, (Function)new Function<Boolean, ITextComponent>() {
            @Override
            public ITextComponent apply(final Boolean v) {
                return v ? ConfigConstants.ON : ConfigConstants.OFF;
            }
        }, (BiConsumer)new BiConsumer<Boolean, Boolean>() {
            @Override
            public void accept(final Boolean o, final Boolean n) {
                WorldMap.pauseRequests = n;
            }
        }, (BooleanSupplier)new BooleanSupplier() {
            @Override
            public boolean getAsBoolean() {
                return true;
            }
        });
        final ISettingEntry extraDebugEntry = (ISettingEntry)new CustomSettingEntry((BooleanSupplier)new BooleanSupplier() {
            @Override
            public boolean getAsBoolean() {
                return false;
            }
        }, (ITextComponent)new TextComponentString("Extra Debug"), (TooltipInfo)null, false, (Supplier)new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return WorldMap.extraDebug;
            }
        }, 0, 1, (IntFunction)new IntFunction<Boolean>() {
            @Override
            public Boolean apply(final int i) {
                return i == 1;
            }
        }, (Function)new Function<Boolean, ITextComponent>() {
            @Override
            public ITextComponent apply(final Boolean v) {
                return v ? ConfigConstants.ON : ConfigConstants.OFF;
            }
        }, (BiConsumer)new BiConsumer<Boolean, Boolean>() {
            @Override
            public void accept(final Boolean o, final Boolean n) {
                WorldMap.extraDebug = n;
            }
        }, (BooleanSupplier)new BooleanSupplier() {
            @Override
            public boolean getAsBoolean() {
                return true;
            }
        });
        this.entries = new ISettingEntry[] { this.createProfileIDEntry(), (ISettingEntry)this.optionEntry(BuiltInProfiledConfigOptions.PROFILE_NAME), (ISettingEntry)this.optionEntry((ConfigOption)WorldMapProfiledConfigOptions.LIGHTING), (ISettingEntry)this.optionEntry((ConfigOption)WorldMapProfiledConfigOptions.BLOCK_COLORS), (ISettingEntry)this.optionEntry((ConfigOption)WorldMapProfiledConfigOptions.LOAD_NEW_CHUNKS), (ISettingEntry)this.optionEntry((ConfigOption)WorldMapProfiledConfigOptions.UPDATE_CHUNKS), (ISettingEntry)this.optionEntry((ConfigOption)WorldMapProfiledConfigOptions.TERRAIN_DEPTH), (ISettingEntry)this.optionEntry((ConfigOption)WorldMapProfiledConfigOptions.TERRAIN_SLOPES), (ISettingEntry)this.optionEntry((ConfigOption)WorldMapProfiledConfigOptions.FOOTSTEPS), (ISettingEntry)this.optionEntry((ConfigOption)WorldMapProfiledConfigOptions.COORDINATES), (ISettingEntry)minimapEntry, (ISettingEntry)this.optionEntry((ConfigOption)WorldMapProfiledConfigOptions.WAYPOINTS), (ISettingEntry)this.optionEntry((ConfigOption)WorldMapProfiledConfigOptions.MINIMAP_RADAR), (ISettingEntry)this.optionEntry((ConfigOption)WorldMapProfiledConfigOptions.RENDER_WAYPOINTS), (ISettingEntry)this.optionEntry((ConfigOption)WorldMapProfiledConfigOptions.WAYPOINT_BACKGROUNDS), (ISettingEntry)this.optionEntry((ConfigOption)WorldMapProfiledConfigOptions.WAYPOINT_SCALE), (ISettingEntry)this.optionEntry((ConfigOption)WorldMapProfiledConfigOptions.BIOME_BLENDING), (ISettingEntry)this.optionEntry((ConfigOption)WorldMapProfiledConfigOptions.BIOME_COLORS_IN_VANILLA), (ISettingEntry)this.optionEntry((ConfigOption)WorldMapProfiledConfigOptions.MIN_ZOOM_LOCAL_WAYPOINTS), (ISettingEntry)this.optionEntry((ConfigOption)WorldMapProfiledConfigOptions.ADJUST_HEIGHT_FOR_SHORT_BLOCKS), (ISettingEntry)this.optionEntry((ConfigOption)WorldMapProfiledConfigOptions.FLOWERS), (ISettingEntry)this.optionEntry((ConfigOption)WorldMapProfiledConfigOptions.STAINED_GLASS), ignoreHeightmapsEntry, (ISettingEntry)this.optionEntry((ConfigOption)WorldMapProfiledConfigOptions.CAVE_MODE_ALLOWED), (ISettingEntry)this.optionEntry(WorldMapProfiledConfigOptions.CAVE_MODE_ALLOWED_DIMENSIONS), (ISettingEntry)this.optionEntry((ConfigOption)WorldMapProfiledConfigOptions.CAVE_MODE_DEPTH), (ISettingEntry)this.optionEntry((ConfigOption)WorldMapProfiledConfigOptions.AUTO_CAVE_MODE), (ISettingEntry)this.optionEntry((ConfigOption)WorldMapProfiledConfigOptions.LEGIBLE_CAVE_MAPS), (ISettingEntry)this.optionEntry((ConfigOption)WorldMapProfiledConfigOptions.CAVE_MODE_TOGGLE_TIMER), (ISettingEntry)this.optionEntry((ConfigOption)WorldMapProfiledConfigOptions.DEFAULT_CAVE_MODE_TYPE), (ISettingEntry)this.optionEntry((ConfigOption)WorldMapProfiledConfigOptions.DISPLAY_CAVE_MODE_START), (ISettingEntry)this.optionEntry((ConfigOption)WorldMapProfiledConfigOptions.WRITING_DISTANCE), (ISettingEntry)this.optionEntry((ConfigOption)WorldMapProfiledConfigOptions.ARROW), (ISettingEntry)this.optionEntry((ConfigOption)WorldMapProfiledConfigOptions.OPENING_ANIMATION), (ISettingEntry)this.optionEntry((ConfigOption)WorldMapProfiledConfigOptions.ARROW_COLOR), (ISettingEntry)this.optionEntry((ConfigOption)WorldMapProfiledConfigOptions.DISPLAY_ZOOM), (ISettingEntry)this.optionEntry((ConfigOption)WorldMapProfiledConfigOptions.DISPLAY_HOVERED_BIOME), (ISettingEntry)this.optionEntry((ConfigOption)WorldMapProfiledConfigOptions.ZOOM_BUTTONS), (ISettingEntry)this.optionEntry(WorldMapProfiledConfigOptions.DEFAULT_MAP_TELEPORT_FORMAT), (ISettingEntry)this.optionEntry(WorldMapProfiledConfigOptions.DEFAULT_MAP_TELEPORT_DIMENSION_FORMAT), (ISettingEntry)mapTeleportCommandEntry, (ISettingEntry)this.optionEntry(WorldMapProfiledConfigOptions.DEFAULT_PLAYER_TELEPORT_FORMAT), (ISettingEntry)this.optionEntry((ConfigOption)WorldMapProfiledConfigOptions.DETECT_AMBIGUOUS_Y), (ISettingEntry)playerTeleportCommandEntry, (ISettingEntry)this.optionEntry((ConfigOption)WorldMapProfiledConfigOptions.MAP_TELEPORT_ALLOWED), (ISettingEntry)this.optionEntry((ConfigOption)WorldMapProfiledConfigOptions.PARTIAL_Y_TELEPORT), (ISettingEntry)this.optionEntry(WorldMapProfiledConfigOptions.MAP_ITEM), (ISettingEntry)this.optionEntry((ConfigOption)WorldMapProfiledConfigOptions.DISPLAY_TRACKED_PLAYERS), (ISettingEntry)this.primaryOptionEntry((ConfigOption)WorldMapPrimaryClientConfigOptions.RELOAD_VIEWED), fullReloadEntry, (ISettingEntry)this.primaryOptionEntry((ConfigOption)WorldMapPrimaryClientConfigOptions.UPDATE_NOTIFICATIONS), fullResaveEntry, (ISettingEntry)this.primaryOptionEntry((ConfigOption)WorldMapPrimaryClientConfigOptions.DEBUG), (ISettingEntry)resetEntry, (ISettingEntry)this.optionEntry((ConfigOption)BuiltInProfiledConfigOptions.IGNORE_ENFORCEMENT_IF_EDITOR) };
    }
    
    private ISettingEntry getEntryForFullReloadOption(final boolean isResave) {
        return (ISettingEntry)new CustomSettingEntry((BooleanSupplier)new BooleanSupplier() {
            @Override
            public boolean getAsBoolean() {
                return false;
            }
        }, (ITextComponent)new TextComponentTranslation(isResave ? "gui.xaero_full_resave" : "gui.xaero_full_reload", new Object[0]), this.context.isClientSide() ? new TooltipInfo(isResave ? "gui.xaero_box_full_resave" : "gui.xaero_box_full_reload") : new TooltipInfo(GuiConstants.SETTING_ENTRY_WRONG_CONTEXT_COMPONENT, false, true), false, (Supplier)new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                final WorldMapSession session = WorldMapSession.getCurrentSession();
                if (session == null) {
                    return false;
                }
                final MapDimension mapDimension = session.getMapProcessor().getMapWorld().getCurrentDimension();
                final MapFullReloader reloader = (mapDimension == null) ? null : mapDimension.getFullReloader();
                return reloader != null && (!isResave || reloader.isResave());
            }
        }, 0, 1, (IntFunction)new IntFunction<Boolean>() {
            @Override
            public Boolean apply(final int i) {
                return i == 1;
            }
        }, (Function)new Function<Boolean, ITextComponent>() {
            @Override
            public ITextComponent apply(final Boolean v) {
                return v ? ConfigConstants.ON : ConfigConstants.OFF;
            }
        }, (BiConsumer)new BiConsumer<Boolean, Boolean>() {
            @Override
            public void accept(final Boolean oldValue, final Boolean newValue) {
                final WorldMapSession session = WorldMapSession.getCurrentSession();
                final MapProcessor mapProcessor = session.getMapProcessor();
                final MapDimension mapDimension = mapProcessor.getMapWorld().getCurrentDimension();
                if (mapDimension == null) {
                    return;
                }
                if (newValue && (mapDimension.getFullReloader() == null || (!mapDimension.getFullReloader().isResave() && isResave))) {
                    mapDimension.startFullMapReload(mapProcessor.getCurrentCaveLayer(), isResave, mapProcessor);
                }
                else if (!newValue) {
                    mapDimension.clearFullMapReload();
                }
                GuiWorldMapSettings.this.refresh();
            }
        }, (BooleanSupplier)new BooleanSupplier() {
            @Override
            public boolean getAsBoolean() {
                return GuiWorldMapSettings.this.context.isClientSide() && WorldMapSession.getCurrentSession() != null;
            }
        });
    }
    
    protected void resetConfirmResult(final boolean result, final GuiScreen parent, final GuiScreen escScreen) {
        if (result) {
            this.resetProfileToDefaults();
        }
        Minecraft.func_71410_x().func_147108_a(parent);
    }
    
    public void func_73866_w_() {
        super.func_73866_w_();
        final Tooltip closeSettingsTooltip = new Tooltip((ITextComponent)new TextComponentTranslation("gui.xaero_box_close_settings", new Object[] { KeyMappingUtils.getKeyName(ControlsRegister.keyOpenSettings) }));
        if (this.parent instanceof GuiMap) {
            this.func_189646_b((GuiButton)new GuiTexturedButton(0, 0, 30, 30, 113, 0, 20, 20, WorldMap.guiTextures, (Consumer)new Consumer<GuiButton>() {
                @Override
                public void accept(final GuiButton b) {
                    GuiWorldMapSettings.this.onSettingsButton(b);
                }
            }, (Supplier)new Supplier<Tooltip>() {
                @Override
                public Tooltip get() {
                    return closeSettingsTooltip;
                }
            }));
        }
    }
    
    private void onSettingsButton(final GuiButton button) {
        this.goBack();
    }
    
    protected void func_73869_a(final char typedChar, final int key) throws IOException {
        super.func_73869_a(typedChar, key);
        if (this.handledKeyboardInput) {
            return;
        }
        if ((this.context.isClientSide() && KeyMappingUtils.inputMatches(false, key, ControlsRegister.keyOpenSettings, 0)) || (!this.context.isClientSide() && KeyMappingUtils.inputMatches(false, key, ControlsRegister.keyOpenServerSettings, 0))) {
            this.onExit(this.escape);
            this.handledKeyboardInput = true;
        }
    }
    
    static {
        PLAYER_TELEPORT_COMMAND_TOOLTIP = (ITextComponent)new TextComponentTranslation("gui.xaero_wm_box_player_teleport_command", new Object[0]);
        MAP_TELEPORT_COMMAND_TOOLTIP = (ITextComponent)new TextComponentTranslation("gui.xaero_wm_box_map_teleport_command", new Object[0]);
    }
}
