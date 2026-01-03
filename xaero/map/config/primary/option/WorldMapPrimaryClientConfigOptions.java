//Decompiled by Procyon!

package xaero.map.config.primary.option;

import xaero.lib.common.config.option.*;
import xaero.lib.client.config.option.*;
import java.util.*;
import xaero.lib.common.config.option.value.type.*;
import java.util.function.*;
import xaero.lib.common.config.util.*;
import net.minecraft.util.text.*;

public class WorldMapPrimaryClientConfigOptions
{
    private static final List<ConfigOption<?>> ALL;
    public static final ConfigOption<Integer> IGNORED_UPDATE;
    public static final BooleanConfigOption UPDATE_NOTIFICATIONS;
    public static final BooleanConfigOption RELOAD_VIEWED;
    public static final ConfigOption<Integer> RELOAD_VIEWED_VERSION;
    public static final BooleanConfigOption DEBUG;
    public static final ConfigOption<Integer> CAVE_MODE_START;
    public static final BooleanConfigOption EXPORT_MULTIPLE_IMAGES;
    public static final BooleanConfigOption NIGHT_EXPORT;
    public static final RangeConfigOption EXPORT_SCALE_DOWN_SQUARE;
    public static final BooleanConfigOption EXPORT_HIGHLIGHTS;
    public static final BooleanConfigOption DIFFERENTIATE_BY_SERVER_ADDRESS;
    public static final BooleanConfigOption DISPLAY_DISABLED_WAYPOINTS;
    public static final BooleanConfigOption CLOSE_WAYPOINTS_AFTER_HOP;
    public static final BooleanConfigOption ONLY_CURRENT_MAP_WAYPOINTS;
    public static final ConfigOption<Integer> GLOBAL_VERSION;
    
    public static void registerAll(final ClientConfigOptionManager manager) {
        for (final ConfigOption<?> option : WorldMapPrimaryClientConfigOptions.ALL) {
            manager.register((ConfigOption)option);
        }
    }
    
    static {
        ALL = new ArrayList<ConfigOption<?>>();
        IGNORED_UPDATE = ((ConfigOption.FinalBuilder)((ConfigOption.FinalBuilder)((ConfigOption.FinalBuilder)((ConfigOption.FinalBuilder)ConfigOption.FinalBuilder.begin().setId("ignored_update")).setDefaultValue((Object)0)).setValueType(BuiltInConfigValueTypes.INTEGER)).setDisplayGetter((BiFunction)new BiFunction<ConfigOption<Integer>, Integer, ITextComponent>() {
            @Override
            public ITextComponent apply(final ConfigOption<Integer> o, final Integer v) {
                return ConfigUtils.getDisplayForSimpleNumber((ConfigOption)o, (Number)v);
            }
        })).build((List)WorldMapPrimaryClientConfigOptions.ALL);
        UPDATE_NOTIFICATIONS = ((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)BooleanConfigOption.Builder.begin().setId("update_notifications")).setDefaultValue((Object)true)).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_wm_update_notification", new Object[0]))).build((List)WorldMapPrimaryClientConfigOptions.ALL);
        RELOAD_VIEWED = ((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)BooleanConfigOption.Builder.begin().setId("reload_viewed")).setDefaultValue((Object)false)).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_reload_viewed_regions", new Object[0]))).setTooltip((ITextComponent)new TextComponentTranslation("gui.xaero_box_reload_viewed_regions", new Object[0]))).build((List)WorldMapPrimaryClientConfigOptions.ALL);
        RELOAD_VIEWED_VERSION = ((ConfigOption.FinalBuilder)((ConfigOption.FinalBuilder)((ConfigOption.FinalBuilder)ConfigOption.FinalBuilder.begin().setId("reload_viewed_version")).setValueType(BuiltInConfigValueTypes.INTEGER)).setDefaultValue((Object)0)).build((List)WorldMapPrimaryClientConfigOptions.ALL);
        DEBUG = ((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)BooleanConfigOption.Builder.begin().setId("debug")).setDefaultValue((Object)false)).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_debug", new Object[0]))).build((List)WorldMapPrimaryClientConfigOptions.ALL);
        CAVE_MODE_START = ((ConfigOption.FinalBuilder)((ConfigOption.FinalBuilder)((ConfigOption.FinalBuilder)((ConfigOption.FinalBuilder)ConfigOption.FinalBuilder.begin().setId("cave_mode_start_y")).setValueType(BuiltInConfigValueTypes.INTEGER)).setDefaultValue((Object)Integer.MAX_VALUE)).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_wm_cave_mode_start", new Object[0]))).build((List)WorldMapPrimaryClientConfigOptions.ALL);
        EXPORT_MULTIPLE_IMAGES = ((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)BooleanConfigOption.Builder.begin().setId("png_export_in_multiple_images")).setDefaultValue((Object)false)).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_export_option_multiple_images", new Object[0]))).setTooltip((ITextComponent)new TextComponentTranslation("gui.xaero_box_export_option_multiple_images", new Object[0]))).build((List)WorldMapPrimaryClientConfigOptions.ALL);
        NIGHT_EXPORT = ((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)BooleanConfigOption.Builder.begin().setId("png_export_night")).setDefaultValue((Object)false)).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_export_option_nighttime", new Object[0]))).setTooltip((ITextComponent)new TextComponentTranslation("gui.xaero_box_export_option_nighttime", new Object[0]))).build((List)WorldMapPrimaryClientConfigOptions.ALL);
        EXPORT_SCALE_DOWN_SQUARE = ((RangeConfigOption.Builder)((RangeConfigOption.Builder)((RangeConfigOption.Builder)((RangeConfigOption.Builder)((RangeConfigOption.Builder)((RangeConfigOption.Builder)((RangeConfigOption.Builder)RangeConfigOption.Builder.begin().setId("png_export_scale_down_square")).setDefaultValue((Object)20)).setMinIndex(0)).setMaxIndex(90)).setDisplayGetter((BiFunction)new BiFunction<ConfigOption<Integer>, Integer, ITextComponent>() {
            @Override
            public ITextComponent apply(final ConfigOption<Integer> o, final Integer v) {
                if (v <= 0) {
                    return (ITextComponent)new TextComponentTranslation("gui.xaero_export_option_scale_down_square_unscaled", new Object[0]);
                }
                return (ITextComponent)new TextComponentTranslation("gui.xaero_export_option_scale_down_square_value", new Object[] { v });
            }
        })).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_export_option_scale_down_square", new Object[0]))).setTooltip((ITextComponent)new TextComponentTranslation("gui.xaero_box_export_option_scale_down_square", new Object[0]))).build((List)WorldMapPrimaryClientConfigOptions.ALL);
        EXPORT_HIGHLIGHTS = ((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)BooleanConfigOption.Builder.begin().setId("png_export_highlights")).setDefaultValue((Object)false)).setDisplayName((ITextComponent)new TextComponentTranslation("gui.xaero_export_option_highlights", new Object[0]))).setTooltip((ITextComponent)new TextComponentTranslation("gui.xaero_box_export_option_highlights", new Object[0]))).build((List)WorldMapPrimaryClientConfigOptions.ALL);
        DIFFERENTIATE_BY_SERVER_ADDRESS = ((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)BooleanConfigOption.Builder.begin().setId("differentiate_by_server_address")).setDefaultValue((Object)true)).build((List)WorldMapPrimaryClientConfigOptions.ALL);
        DISPLAY_DISABLED_WAYPOINTS = ((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)BooleanConfigOption.Builder.begin().setId("display_disabled_waypoints")).setDefaultValue((Object)false)).build((List)WorldMapPrimaryClientConfigOptions.ALL);
        CLOSE_WAYPOINTS_AFTER_HOP = ((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)BooleanConfigOption.Builder.begin().setId("close_waypoints_after_hopping")).setDefaultValue((Object)true)).build((List)WorldMapPrimaryClientConfigOptions.ALL);
        ONLY_CURRENT_MAP_WAYPOINTS = ((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)BooleanConfigOption.Builder.begin().setId("only_display_current_map_waypoints")).setDefaultValue((Object)false)).build((List)WorldMapPrimaryClientConfigOptions.ALL);
        GLOBAL_VERSION = ((ConfigOption.FinalBuilder)((ConfigOption.FinalBuilder)((ConfigOption.FinalBuilder)ConfigOption.FinalBuilder.begin().setId("global_version")).setValueType(BuiltInConfigValueTypes.INTEGER)).setDefaultValue((Object)1)).build((List)WorldMapPrimaryClientConfigOptions.ALL);
    }
}
