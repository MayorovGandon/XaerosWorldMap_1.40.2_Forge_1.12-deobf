//Decompiled by Procyon!

package xaero.map.common.config.option;

import xaero.lib.common.config.option.*;
import xaero.map.common.config.*;
import java.util.function.*;
import java.util.*;
import xaero.lib.common.config.option.value.type.*;
import xaero.lib.common.config.util.*;
import net.minecraft.util.text.*;

public class WorldMapProfiledConfigOptions
{
    private static final List<ConfigOption<?>> ALL;
    public static final BooleanConfigOption LIGHTING;
    public static final RangeConfigOption BLOCK_COLORS;
    public static final BooleanConfigOption LOAD_NEW_CHUNKS;
    public static final BooleanConfigOption UPDATE_CHUNKS;
    public static final BooleanConfigOption TERRAIN_DEPTH;
    public static final RangeConfigOption TERRAIN_SLOPES;
    public static final BooleanConfigOption FOOTSTEPS;
    public static final BooleanConfigOption COORDINATES;
    public static final BooleanConfigOption WAYPOINTS;
    public static final BooleanConfigOption RENDER_WAYPOINTS;
    public static final BooleanConfigOption WAYPOINT_BACKGROUNDS;
    public static final SteppedConfigOption WAYPOINT_SCALE;
    public static final BooleanConfigOption BIOME_BLENDING;
    public static final BooleanConfigOption BIOME_COLORS_IN_VANILLA;
    public static final SteppedConfigOption MIN_ZOOM_LOCAL_WAYPOINTS;
    public static final BooleanConfigOption ADJUST_HEIGHT_FOR_SHORT_BLOCKS;
    public static final BooleanConfigOption FLOWERS;
    public static final BooleanConfigOption STAINED_GLASS;
    public static final BooleanConfigOption CAVE_MODE_ALLOWED;
    public static final ConfigOption<Set<Integer>> CAVE_MODE_ALLOWED_DIMENSIONS;
    public static final RangeConfigOption CAVE_MODE_DEPTH;
    public static final BooleanConfigOption LEGIBLE_CAVE_MAPS;
    public static final RangeConfigOption AUTO_CAVE_MODE;
    public static final SteppedConfigOption CAVE_MODE_TOGGLE_TIMER;
    public static final RangeConfigOption DEFAULT_CAVE_MODE_TYPE;
    public static final BooleanConfigOption DISPLAY_CAVE_MODE_START;
    public static final RangeConfigOption WRITING_DISTANCE;
    public static final BooleanConfigOption ARROW;
    public static final BooleanConfigOption OPENING_ANIMATION;
    public static final RangeConfigOption ARROW_COLOR;
    public static final BooleanConfigOption DISPLAY_ZOOM;
    public static final BooleanConfigOption DISPLAY_HOVERED_BIOME;
    public static final BooleanConfigOption ZOOM_BUTTONS;
    public static final BooleanConfigOption DETECT_AMBIGUOUS_Y;
    public static final ConfigOption<String> DEFAULT_MAP_TELEPORT_FORMAT;
    public static final ConfigOption<String> DEFAULT_MAP_TELEPORT_DIMENSION_FORMAT;
    public static final ConfigOption<String> DEFAULT_PLAYER_TELEPORT_FORMAT;
    public static final BooleanConfigOption MAP_TELEPORT_ALLOWED;
    public static final BooleanConfigOption PARTIAL_Y_TELEPORT;
    public static final BooleanConfigOption OPAC_CLAIMS;
    public static final RangeConfigOption OPAC_CLAIMS_BORDER_OPACITY;
    public static final RangeConfigOption OPAC_CLAIMS_FILL_OPACITY;
    public static final ConfigOption<String> MAP_ITEM;
    public static final BooleanConfigOption MINIMAP_RADAR;
    public static final BooleanConfigOption DISPLAY_TRACKED_PLAYERS;
    
    public static void registerAll(final ConfigOptionManager manager) {
        for (final ConfigOption<?> option : WorldMapProfiledConfigOptions.ALL) {
            manager.register((ConfigOption)option);
        }
    }
    
    static {
        ALL = new ArrayList<ConfigOption<?>>();
        LIGHTING = ((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)BooleanConfigOption.Builder.begin().setId("lighting")).setDefaultValue((Object)true)).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_lighting", new Object[0]))).build((List)WorldMapProfiledConfigOptions.ALL);
        BLOCK_COLORS = ((RangeConfigOption.Builder)((RangeConfigOption.Builder)((RangeConfigOption.Builder)((RangeConfigOption.Builder)((RangeConfigOption.Builder)((RangeConfigOption.Builder)RangeConfigOption.Builder.begin().setId("block_colors")).setDefaultValue((Object)0)).setMinIndex(0)).setMaxIndex(WorldMapConfigConstants.BLOCK_COLORS_NAMES.length - 1)).setDisplayGetter((BiFunction)new BiFunction<ConfigOption<Integer>, Integer, ITextComponent>() {
            @Override
            public ITextComponent apply(final ConfigOption<Integer> o, final Integer v) {
                return WorldMapConfigConstants.BLOCK_COLORS_NAMES[v];
            }
        })).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_block_colours", new Object[0]))).build((List)WorldMapProfiledConfigOptions.ALL);
        LOAD_NEW_CHUNKS = ((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)BooleanConfigOption.Builder.begin().setId("load_new_chunks")).setDefaultValue((Object)true)).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_load_chunks", new Object[0]))).build((List)WorldMapProfiledConfigOptions.ALL);
        UPDATE_CHUNKS = ((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)BooleanConfigOption.Builder.begin().setId("update_chunks")).setDefaultValue((Object)true)).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_update_chunks", new Object[0]))).build((List)WorldMapProfiledConfigOptions.ALL);
        TERRAIN_DEPTH = ((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)BooleanConfigOption.Builder.begin().setId("terrain_depth")).setDefaultValue((Object)true)).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_terrain_depth", new Object[0]))).build((List)WorldMapProfiledConfigOptions.ALL);
        TERRAIN_SLOPES = ((RangeConfigOption.Builder)((RangeConfigOption.Builder)((RangeConfigOption.Builder)((RangeConfigOption.Builder)((RangeConfigOption.Builder)((RangeConfigOption.Builder)RangeConfigOption.Builder.begin().setId("terrain_slopes")).setDefaultValue((Object)2)).setMinIndex(0)).setMaxIndex(WorldMapConfigConstants.TERRAIN_SLOPES_NAMES.length - 1)).setDisplayGetter((BiFunction)new BiFunction<ConfigOption<Integer>, Integer, ITextComponent>() {
            @Override
            public ITextComponent apply(final ConfigOption<Integer> o, final Integer v) {
                return WorldMapConfigConstants.TERRAIN_SLOPES_NAMES[v];
            }
        })).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_terrain_slopes", new Object[0]))).build((List)WorldMapProfiledConfigOptions.ALL);
        FOOTSTEPS = ((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)BooleanConfigOption.Builder.begin().setId("footsteps")).setDefaultValue((Object)true)).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_footsteps", new Object[0]))).build((List)WorldMapProfiledConfigOptions.ALL);
        COORDINATES = ((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)BooleanConfigOption.Builder.begin().setId("display_coordinates")).setDefaultValue((Object)true)).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_wm_coordinates", new Object[0]))).build((List)WorldMapProfiledConfigOptions.ALL);
        WAYPOINTS = ((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)BooleanConfigOption.Builder.begin().setId("waypoints")).setDefaultValue((Object)true)).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_worldmap_waypoints", new Object[0]))).build((List)WorldMapProfiledConfigOptions.ALL);
        RENDER_WAYPOINTS = ((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)BooleanConfigOption.Builder.begin().setId("render_waypoints")).setDefaultValue((Object)true)).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_render_worldmap_waypoints", new Object[0]))).build((List)WorldMapProfiledConfigOptions.ALL);
        WAYPOINT_BACKGROUNDS = ((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)BooleanConfigOption.Builder.begin().setId("waypoint_backgrounds")).setDefaultValue((Object)true)).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_waypoint_backgrounds", new Object[0]))).build((List)WorldMapProfiledConfigOptions.ALL);
        WAYPOINT_SCALE = ((SteppedConfigOption.Builder)((SteppedConfigOption.Builder)((SteppedConfigOption.Builder)SteppedConfigOption.Builder.begin().setId("waypoint_scale")).setDefaultValue((Object)1.0)).setMinValue(0.5).setMaxValue(5.0).setStep(0.5).setRangeValidator(true).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_wm_waypoint_scale", new Object[0]))).build((List)WorldMapProfiledConfigOptions.ALL);
        BIOME_BLENDING = ((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)BooleanConfigOption.Builder.begin().setId("biome_blending")).setDefaultValue((Object)true)).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_wm_biome_blending", new Object[0]))).setTooltip((ITextComponent)new TextComponentTranslation("gui.xaero_wm_box_biome_blending", new Object[0]))).build((List)WorldMapProfiledConfigOptions.ALL);
        BIOME_COLORS_IN_VANILLA = ((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)BooleanConfigOption.Builder.begin().setId("biome_colors_in_vanilla")).setDefaultValue((Object)false)).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_biome_colors", new Object[0]))).build((List)WorldMapProfiledConfigOptions.ALL);
        MIN_ZOOM_LOCAL_WAYPOINTS = ((SteppedConfigOption.Builder)((SteppedConfigOption.Builder)((SteppedConfigOption.Builder)SteppedConfigOption.Builder.begin().setId("minimum_zoom_for_local_waypoints")).setDefaultValue((Object)0.0)).setMinValue(0.0).setMaxValue(3.0).setStep(0.01).setRangeValidator(true).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_wm_min_zoom_local_waypoints", new Object[0]))).build((List)WorldMapProfiledConfigOptions.ALL);
        ADJUST_HEIGHT_FOR_SHORT_BLOCKS = ((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)BooleanConfigOption.Builder.begin().setId("adjust_height_for_short_blocks")).setDefaultValue((Object)true)).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_wm_adjust_height_for_carpetlike_blocks", new Object[0]))).setTooltip((ITextComponent)new TextComponentTranslation("gui.xaero_wm_box_adjust_height_for_carpetlike_blocks", new Object[0]))).build((List)WorldMapProfiledConfigOptions.ALL);
        FLOWERS = ((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)BooleanConfigOption.Builder.begin().setId("display_flowers")).setDefaultValue((Object)true)).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_flowers", new Object[0]))).build((List)WorldMapProfiledConfigOptions.ALL);
        STAINED_GLASS = ((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)BooleanConfigOption.Builder.begin().setId("display_stained_glass")).setDefaultValue((Object)true)).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_wm_display_stained_glass", new Object[0]))).build((List)WorldMapProfiledConfigOptions.ALL);
        CAVE_MODE_ALLOWED = ((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)BooleanConfigOption.Builder.begin().setId("cave_mode_allowed")).setDefaultValue((Object)true)).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_wm_cave_mode_allowed", new Object[0]))).setTooltip((ITextComponent)new TextComponentTranslation("gui.xaero_wm_box_cave_mode_allowed", new Object[0]))).build((List)WorldMapProfiledConfigOptions.ALL);
        CAVE_MODE_ALLOWED_DIMENSIONS = ((ConfigOption.FinalBuilder)((ConfigOption.FinalBuilder)((ConfigOption.FinalBuilder)((ConfigOption.FinalBuilder)((ConfigOption.FinalBuilder)((ConfigOption.FinalBuilder)ConfigOption.FinalBuilder.begin().setId("cave_mode_allowed_dimensions")).setDefaultValue((Object)Collections.unmodifiableSet((Set<?>)new LinkedHashSet<Object>()))).setValueType(CollectionConfigValueType.Builder.begin().setElementValueType(BuiltInConfigValueTypes.INTEGER).setIoCodecSeparator(Character.valueOf(',')).build())).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_wm_cave_mode_allowed_dimensions", new Object[0]))).setTooltip((ITextComponent)new TextComponentTranslation("gui.xaero_wm_box_cave_mode_allowed_dimensions", new Object[0]))).setOverridable(false)).build((List)WorldMapProfiledConfigOptions.ALL);
        CAVE_MODE_DEPTH = ((RangeConfigOption.Builder)((RangeConfigOption.Builder)((RangeConfigOption.Builder)((RangeConfigOption.Builder)((RangeConfigOption.Builder)RangeConfigOption.Builder.begin().setId("cave_mode_depth")).setDefaultValue((Object)30)).setMinIndex(1)).setMaxIndex(64)).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_wm_cave_mode_depth", new Object[0]))).build((List)WorldMapProfiledConfigOptions.ALL);
        LEGIBLE_CAVE_MAPS = ((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)BooleanConfigOption.Builder.begin().setId("legible_cave_maps")).setDefaultValue((Object)false)).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_wm_legible_cave_maps", new Object[0]))).setTooltip((ITextComponent)new TextComponentTranslation("gui.xaero_wm_box_legible_cave_maps", new Object[0]))).build((List)WorldMapProfiledConfigOptions.ALL);
        AUTO_CAVE_MODE = ((RangeConfigOption.Builder)((RangeConfigOption.Builder)((RangeConfigOption.Builder)((RangeConfigOption.Builder)((RangeConfigOption.Builder)((RangeConfigOption.Builder)((RangeConfigOption.Builder)RangeConfigOption.Builder.begin().setId("auto_cave_mode")).setDefaultValue((Object)(-1))).setMinIndex(-1)).setMaxIndex(3)).setDisplayGetter((BiFunction)new BiFunction<ConfigOption<Integer>, Integer, ITextComponent>() {
            @Override
            public ITextComponent apply(final ConfigOption<Integer> o, final Integer v) {
                if (v == 0) {
                    return (ITextComponent)new TextComponentTranslation("gui.xaero_off", new Object[0]);
                }
                if (v < 0) {
                    return (ITextComponent)new TextComponentTranslation("gui.xaero_auto_cave_mode_minimap", new Object[0]);
                }
                final int roofSideSize = v * 2 - 1;
                return (ITextComponent)new TextComponentTranslation("gui.xaero_wm_ceiling", new Object[] { roofSideSize + "x" + roofSideSize });
            }
        })).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_auto_cave_mode", new Object[0]))).setTooltip((ITextComponent)new TextComponentTranslation("gui.xaero_box_auto_cave_mode", new Object[0]))).build((List)WorldMapProfiledConfigOptions.ALL);
        CAVE_MODE_TOGGLE_TIMER = ((SteppedConfigOption.Builder)((SteppedConfigOption.Builder)((SteppedConfigOption.Builder)((SteppedConfigOption.Builder)((SteppedConfigOption.Builder)SteppedConfigOption.Builder.begin().setId("cave_mode_toggle_timer")).setDefaultValue((Object)1.0)).setMinValue(0.0).setMaxValue(10.0).setStep(0.1).setDisplayGetter((BiFunction)new BiFunction<ConfigOption<Double>, Double, ITextComponent>() {
            @Override
            public ITextComponent apply(final ConfigOption<Double> o, final Double v) {
                return ConfigUtils.getDisplayForSimpleNumber((ConfigOption)o, (Number)v, WorldMapConfigConstants.SEC);
            }
        })).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_wm_cave_mode_toggle_timer", new Object[0]))).setTooltip((ITextComponent)new TextComponentTranslation("gui.xaero_wm_box_cave_mode_toggle_timer", new Object[0]))).build((List)WorldMapProfiledConfigOptions.ALL);
        DEFAULT_CAVE_MODE_TYPE = ((RangeConfigOption.Builder)((RangeConfigOption.Builder)((RangeConfigOption.Builder)((RangeConfigOption.Builder)((RangeConfigOption.Builder)((RangeConfigOption.Builder)((RangeConfigOption.Builder)RangeConfigOption.Builder.begin().setId("default_cave_mode_type")).setDefaultValue((Object)1)).setMinIndex(0)).setMaxIndex(WorldMapConfigConstants.DEFAULT_CAVE_MODE_TYPE_NAMES.length - 1)).setDisplayGetter((BiFunction)new BiFunction<ConfigOption<Integer>, Integer, ITextComponent>() {
            @Override
            public ITextComponent apply(final ConfigOption<Integer> o, final Integer v) {
                return WorldMapConfigConstants.DEFAULT_CAVE_MODE_TYPE_NAMES[v];
            }
        })).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_wm_default_cave_mode_type", new Object[0]))).setTooltip((ITextComponent)new TextComponentTranslation("gui.xaero_wm_box_default_cave_mode_type", new Object[0]))).build((List)WorldMapProfiledConfigOptions.ALL);
        DISPLAY_CAVE_MODE_START = ((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)BooleanConfigOption.Builder.begin().setId("display_cave_mode_start")).setDefaultValue((Object)true)).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_wm_display_cave_mode_start", new Object[0]))).build((List)WorldMapProfiledConfigOptions.ALL);
        WRITING_DISTANCE = ((RangeConfigOption.Builder)((RangeConfigOption.Builder)((RangeConfigOption.Builder)((RangeConfigOption.Builder)((RangeConfigOption.Builder)((RangeConfigOption.Builder)((RangeConfigOption.Builder)RangeConfigOption.Builder.begin().setId("map_writing_distance")).setDefaultValue((Object)(-1))).setMinIndex(-1)).setMaxIndex(32)).setDisplayGetter((BiFunction)new BiFunction<ConfigOption<Integer>, Integer, ITextComponent>() {
            @Override
            public ITextComponent apply(final ConfigOption<Integer> o, final Integer v) {
                if (v < 0) {
                    return (ITextComponent)new TextComponentTranslation("gui.xaero_map_writing_distance_unlimited", new Object[0]);
                }
                return (ITextComponent)new TextComponentString(v + "");
            }
        })).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_map_writing_distance", new Object[0]))).setTooltip((ITextComponent)new TextComponentTranslation("gui.xaero_box_map_writing_distance", new Object[0]))).build((List)WorldMapProfiledConfigOptions.ALL);
        ARROW = ((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)BooleanConfigOption.Builder.begin().setId("display_player_as_arrow")).setDefaultValue((Object)true)).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_render_arrow", new Object[0]))).build((List)WorldMapProfiledConfigOptions.ALL);
        OPENING_ANIMATION = ((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)BooleanConfigOption.Builder.begin().setId("opening_animation")).setDefaultValue((Object)true)).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_open_map_animation", new Object[0]))).build((List)WorldMapProfiledConfigOptions.ALL);
        ARROW_COLOR = ((RangeConfigOption.Builder)((RangeConfigOption.Builder)((RangeConfigOption.Builder)((RangeConfigOption.Builder)((RangeConfigOption.Builder)((RangeConfigOption.Builder)((RangeConfigOption.Builder)RangeConfigOption.Builder.begin().setId("arrow_color")).setDefaultValue((Object)(-2))).setMinIndex(-2)).setMaxIndex(WorldMapConfigConstants.ARROW_COLORS.length - 1)).setDisplayGetter((BiFunction)new BiFunction<ConfigOption<Integer>, Integer, ITextComponent>() {
            @Override
            public ITextComponent apply(final ConfigOption<Integer> o, final Integer v) {
                return WorldMapConfigConstants.ARROW_COLOR_NAMES[v + 2];
            }
        })).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_wm_arrow_colour", new Object[0]))).setTooltip((ITextComponent)new TextComponentTranslation("gui.xaero_wm_box_arrow_color", new Object[0]))).build((List)WorldMapProfiledConfigOptions.ALL);
        DISPLAY_ZOOM = ((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)BooleanConfigOption.Builder.begin().setId("display_zoom")).setDefaultValue((Object)true)).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_display_zoom", new Object[0]))).build((List)WorldMapProfiledConfigOptions.ALL);
        DISPLAY_HOVERED_BIOME = ((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)BooleanConfigOption.Builder.begin().setId("display_hovered_biome")).setDefaultValue((Object)true)).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_wm_hovered_biome", new Object[0]))).build((List)WorldMapProfiledConfigOptions.ALL);
        ZOOM_BUTTONS = ((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)BooleanConfigOption.Builder.begin().setId("zoom_buttons")).setDefaultValue((Object)true)).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_zoom_buttons", new Object[0]))).build((List)WorldMapProfiledConfigOptions.ALL);
        DETECT_AMBIGUOUS_Y = ((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)BooleanConfigOption.Builder.begin().setId("detect_ambiguous_y")).setDefaultValue((Object)true)).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_wm_detect_ambiguous_y", new Object[0]))).setTooltip((ITextComponent)new TextComponentTranslation("gui.xaero_wm_box_detect_ambiguous_y", new Object[0]))).build((List)WorldMapProfiledConfigOptions.ALL);
        DEFAULT_MAP_TELEPORT_FORMAT = ((ConfigOption.FinalBuilder)((ConfigOption.FinalBuilder)((ConfigOption.FinalBuilder)((ConfigOption.FinalBuilder)ConfigOption.FinalBuilder.begin().setId("default_map_teleport_command_format")).setDefaultValue((Object)"/tp @s {x} {y} {z}")).setValueType(BuiltInConfigValueTypes.getString(500))).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_wm_default_teleport_command", new Object[0]))).build((List)WorldMapProfiledConfigOptions.ALL);
        DEFAULT_MAP_TELEPORT_DIMENSION_FORMAT = ((ConfigOption.FinalBuilder)((ConfigOption.FinalBuilder)((ConfigOption.FinalBuilder)((ConfigOption.FinalBuilder)ConfigOption.FinalBuilder.begin().setId("default_map_teleport_command_dimension_format")).setDefaultValue((Object)"/execute as @s in {d} run tp {x} {y} {z}")).setValueType(BuiltInConfigValueTypes.getString(500))).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_wm_default_teleport_command_dimension", new Object[0]))).build((List)WorldMapProfiledConfigOptions.ALL);
        DEFAULT_PLAYER_TELEPORT_FORMAT = ((ConfigOption.FinalBuilder)((ConfigOption.FinalBuilder)((ConfigOption.FinalBuilder)((ConfigOption.FinalBuilder)ConfigOption.FinalBuilder.begin().setId("default_player_teleport_command_format")).setDefaultValue((Object)"/tp @s {name}")).setValueType(BuiltInConfigValueTypes.getString(500))).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_wm_default_player_teleport_command", new Object[0]))).build((List)WorldMapProfiledConfigOptions.ALL);
        MAP_TELEPORT_ALLOWED = ((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)BooleanConfigOption.Builder.begin().setId("map_teleport_allowed")).setDefaultValue((Object)true)).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_wm_teleport_allowed", new Object[0]))).setTooltip((ITextComponent)new TextComponentTranslation("gui.xaero_wm_teleport_allowed_tooltip", new Object[0]))).build((List)WorldMapProfiledConfigOptions.ALL);
        PARTIAL_Y_TELEPORT = ((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)BooleanConfigOption.Builder.begin().setId("partial_y_teleport")).setDefaultValue((Object)true)).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_wm_partial_y_teleportation", new Object[0]))).setTooltip((ITextComponent)new TextComponentTranslation("gui.xaero_wm_box_partial_y_teleportation", new Object[0]))).build((List)WorldMapProfiledConfigOptions.ALL);
        OPAC_CLAIMS = ((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)BooleanConfigOption.Builder.begin().setId("display_opac_claims")).setDefaultValue((Object)true)).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_wm_pac_claims", new Object[0]))).setTooltip((ITextComponent)new TextComponentTranslation("gui.xaero_wm_box_pac_claims", new Object[0]))).build((List)WorldMapProfiledConfigOptions.ALL);
        OPAC_CLAIMS_BORDER_OPACITY = ((RangeConfigOption.Builder)((RangeConfigOption.Builder)((RangeConfigOption.Builder)((RangeConfigOption.Builder)((RangeConfigOption.Builder)((RangeConfigOption.Builder)RangeConfigOption.Builder.begin().setId("opac_claims_border_opacity")).setDefaultValue((Object)80)).setMinIndex(1)).setMaxIndex(100)).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_wm_pac_claims_border_opacity", new Object[0]))).setTooltip((ITextComponent)new TextComponentTranslation("gui.xaero_wm_box_pac_claims_border_opacity", new Object[0]))).build((List)WorldMapProfiledConfigOptions.ALL);
        OPAC_CLAIMS_FILL_OPACITY = ((RangeConfigOption.Builder)((RangeConfigOption.Builder)((RangeConfigOption.Builder)((RangeConfigOption.Builder)((RangeConfigOption.Builder)((RangeConfigOption.Builder)RangeConfigOption.Builder.begin().setId("opac_claims_fill_opacity")).setDefaultValue((Object)46)).setMinIndex(1)).setMaxIndex(100)).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_wm_pac_claims_fill_opacity", new Object[0]))).setTooltip((ITextComponent)new TextComponentTranslation("gui.xaero_wm_box_pac_claims_fill_opacity", new Object[0]))).build((List)WorldMapProfiledConfigOptions.ALL);
        MAP_ITEM = ((ConfigOption.FinalBuilder)((ConfigOption.FinalBuilder)((ConfigOption.FinalBuilder)((ConfigOption.FinalBuilder)((ConfigOption.FinalBuilder)((ConfigOption.FinalBuilder)ConfigOption.FinalBuilder.begin().setId("map_item")).setDefaultValue((Object)"-")).setValueType(BuiltInConfigValueTypes.getString(BuiltInConfigValueTypes.RESOURCE_LOCATION.getIoCodec().getMaxStringLength()))).setDisplayGetter((BiFunction)new BiFunction<ConfigOption<String>, String, ITextComponent>() {
            @Override
            public ITextComponent apply(final ConfigOption<String> o, final String v) {
                return ConfigUtils.getDisplayForString((ConfigOption)o, v);
            }
        })).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_map_item", new Object[0]))).setTooltip((ITextComponent)new TextComponentTranslation("gui.xaero_box_map_item", new Object[0]))).build((List)WorldMapProfiledConfigOptions.ALL);
        MINIMAP_RADAR = ((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)BooleanConfigOption.Builder.begin().setId("display_minimap_radar")).setDefaultValue((Object)true)).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_wm_display_minimap_radar", new Object[0]))).setTooltip((ITextComponent)new TextComponentTranslation("gui.xaero_wm_box_display_minimap_radar", new Object[0]))).build((List)WorldMapProfiledConfigOptions.ALL);
        DISPLAY_TRACKED_PLAYERS = ((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)BooleanConfigOption.Builder.begin().setId("display_tracked_players")).setDefaultValue((Object)true)).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_wm_display_tracked_players", new Object[0]))).build((List)WorldMapProfiledConfigOptions.ALL);
    }
}
