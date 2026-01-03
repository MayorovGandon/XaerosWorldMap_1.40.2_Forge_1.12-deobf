//Decompiled by Procyon!

package xaero.map.config.option.ui;

import xaero.lib.client.config.option.ui.*;
import xaero.map.config.primary.option.*;
import xaero.lib.client.config.option.ui.type.*;
import xaero.lib.common.config.option.*;
import xaero.map.common.config.option.*;
import xaero.map.config.option.ui.type.*;

public class WorldMapConfigOptionUIRegister
{
    public static void registerAll(final ConfigOptionUITypeManager manager) {
        manager.registerUIType((ConfigOption)WorldMapPrimaryClientConfigOptions.UPDATE_NOTIFICATIONS, BuiltInConfigOptionUITypes.TOGGLE);
        manager.registerUIType((ConfigOption)WorldMapProfiledConfigOptions.LIGHTING, BuiltInConfigOptionUITypes.TOGGLE);
        manager.registerUIType((ConfigOption)WorldMapProfiledConfigOptions.BLOCK_COLORS, BuiltInConfigOptionUITypes.INT_INDEXED_BUTTON);
        manager.registerUIType((ConfigOption)WorldMapProfiledConfigOptions.LOAD_NEW_CHUNKS, BuiltInConfigOptionUITypes.TOGGLE);
        manager.registerUIType((ConfigOption)WorldMapProfiledConfigOptions.UPDATE_CHUNKS, BuiltInConfigOptionUITypes.TOGGLE);
        manager.registerUIType((ConfigOption)WorldMapProfiledConfigOptions.TERRAIN_DEPTH, BuiltInConfigOptionUITypes.TOGGLE);
        manager.registerUIType((ConfigOption)WorldMapProfiledConfigOptions.TERRAIN_SLOPES, BuiltInConfigOptionUITypes.INT_INDEXED_BUTTON);
        manager.registerUIType((ConfigOption)WorldMapProfiledConfigOptions.FOOTSTEPS, BuiltInConfigOptionUITypes.TOGGLE);
        manager.registerUIType((ConfigOption)WorldMapProfiledConfigOptions.COORDINATES, BuiltInConfigOptionUITypes.TOGGLE);
        manager.registerUIType((ConfigOption)WorldMapProfiledConfigOptions.WAYPOINTS, BuiltInConfigOptionUITypes.TOGGLE);
        manager.registerUIType((ConfigOption)WorldMapProfiledConfigOptions.WAYPOINT_BACKGROUNDS, BuiltInConfigOptionUITypes.TOGGLE);
        manager.registerUIType((ConfigOption)WorldMapProfiledConfigOptions.WAYPOINT_SCALE, BuiltInConfigOptionUITypes.DOUBLE_INDEXED_SLIDER);
        manager.registerUIType((ConfigOption)WorldMapProfiledConfigOptions.BIOME_BLENDING, BuiltInConfigOptionUITypes.TOGGLE);
        manager.registerUIType((ConfigOption)WorldMapProfiledConfigOptions.BIOME_COLORS_IN_VANILLA, BuiltInConfigOptionUITypes.TOGGLE);
        manager.registerUIType((ConfigOption)WorldMapProfiledConfigOptions.MIN_ZOOM_LOCAL_WAYPOINTS, BuiltInConfigOptionUITypes.DOUBLE_INDEXED_SLIDER);
        manager.registerUIType((ConfigOption)WorldMapProfiledConfigOptions.ADJUST_HEIGHT_FOR_SHORT_BLOCKS, BuiltInConfigOptionUITypes.TOGGLE);
        manager.registerUIType((ConfigOption)WorldMapProfiledConfigOptions.FLOWERS, BuiltInConfigOptionUITypes.TOGGLE);
        manager.registerUIType((ConfigOption)WorldMapProfiledConfigOptions.STAINED_GLASS, BuiltInConfigOptionUITypes.TOGGLE);
        manager.registerUIType((ConfigOption)WorldMapProfiledConfigOptions.CAVE_MODE_ALLOWED, BuiltInConfigOptionUITypes.TOGGLE);
        manager.registerUIType(WorldMapProfiledConfigOptions.CAVE_MODE_ALLOWED_DIMENSIONS, BuiltInConfigOptionUITypes.getStringEdit());
        manager.registerUIType((ConfigOption)WorldMapProfiledConfigOptions.CAVE_MODE_DEPTH, BuiltInConfigOptionUITypes.INT_INDEXED_SLIDER);
        manager.registerUIType((ConfigOption)WorldMapProfiledConfigOptions.AUTO_CAVE_MODE, BuiltInConfigOptionUITypes.INT_INDEXED_BUTTON);
        manager.registerUIType((ConfigOption)WorldMapProfiledConfigOptions.LEGIBLE_CAVE_MAPS, BuiltInConfigOptionUITypes.TOGGLE);
        manager.registerUIType((ConfigOption)WorldMapProfiledConfigOptions.CAVE_MODE_TOGGLE_TIMER, BuiltInConfigOptionUITypes.DOUBLE_INDEXED_SLIDER);
        manager.registerUIType((ConfigOption)WorldMapProfiledConfigOptions.DEFAULT_CAVE_MODE_TYPE, BuiltInConfigOptionUITypes.INT_INDEXED_BUTTON);
        manager.registerUIType((ConfigOption)WorldMapProfiledConfigOptions.DISPLAY_CAVE_MODE_START, BuiltInConfigOptionUITypes.TOGGLE);
        manager.registerUIType((ConfigOption)WorldMapProfiledConfigOptions.WRITING_DISTANCE, BuiltInConfigOptionUITypes.INT_INDEXED_SLIDER);
        manager.registerUIType((ConfigOption)WorldMapProfiledConfigOptions.ARROW, BuiltInConfigOptionUITypes.TOGGLE);
        manager.registerUIType((ConfigOption)WorldMapProfiledConfigOptions.OPENING_ANIMATION, BuiltInConfigOptionUITypes.TOGGLE);
        manager.registerUIType((ConfigOption)WorldMapProfiledConfigOptions.ARROW_COLOR, BuiltInConfigOptionUITypes.INT_INDEXED_BUTTON);
        manager.registerUIType((ConfigOption)WorldMapProfiledConfigOptions.DISPLAY_ZOOM, BuiltInConfigOptionUITypes.TOGGLE);
        manager.registerUIType((ConfigOption)WorldMapProfiledConfigOptions.DISPLAY_HOVERED_BIOME, BuiltInConfigOptionUITypes.TOGGLE);
        manager.registerUIType((ConfigOption)WorldMapProfiledConfigOptions.ZOOM_BUTTONS, BuiltInConfigOptionUITypes.TOGGLE);
        manager.registerUIType(WorldMapProfiledConfigOptions.DEFAULT_MAP_TELEPORT_FORMAT, WorldMapConfigOptionUITypes.DEFAULT_MAP_TP_COMMAND);
        manager.registerUIType(WorldMapProfiledConfigOptions.DEFAULT_MAP_TELEPORT_DIMENSION_FORMAT, WorldMapConfigOptionUITypes.DEFAULT_MAP_TP_COMMAND);
        manager.registerUIType(WorldMapProfiledConfigOptions.DEFAULT_PLAYER_TELEPORT_FORMAT, WorldMapConfigOptionUITypes.DEFAULT_MAP_TP_COMMAND);
        manager.registerUIType((ConfigOption)WorldMapProfiledConfigOptions.DETECT_AMBIGUOUS_Y, BuiltInConfigOptionUITypes.TOGGLE);
        manager.registerUIType((ConfigOption)WorldMapProfiledConfigOptions.MAP_TELEPORT_ALLOWED, BuiltInConfigOptionUITypes.TOGGLE);
        manager.registerUIType((ConfigOption)WorldMapProfiledConfigOptions.PARTIAL_Y_TELEPORT, BuiltInConfigOptionUITypes.TOGGLE);
        manager.registerUIType((ConfigOption)WorldMapProfiledConfigOptions.OPAC_CLAIMS, BuiltInConfigOptionUITypes.TOGGLE);
        manager.registerUIType((ConfigOption)WorldMapProfiledConfigOptions.OPAC_CLAIMS_BORDER_OPACITY, BuiltInConfigOptionUITypes.INT_INDEXED_SLIDER);
        manager.registerUIType((ConfigOption)WorldMapProfiledConfigOptions.OPAC_CLAIMS_FILL_OPACITY, BuiltInConfigOptionUITypes.INT_INDEXED_SLIDER);
        manager.registerUIType((ConfigOption)WorldMapPrimaryClientConfigOptions.RELOAD_VIEWED, BuiltInConfigOptionUITypes.TOGGLE);
        manager.registerUIType((ConfigOption)WorldMapPrimaryClientConfigOptions.DEBUG, BuiltInConfigOptionUITypes.TOGGLE);
        manager.registerUIType(WorldMapProfiledConfigOptions.MAP_ITEM, BuiltInConfigOptionUITypes.STRING_STRING_EDIT);
        manager.registerUIType((ConfigOption)WorldMapPrimaryClientConfigOptions.EXPORT_MULTIPLE_IMAGES, BuiltInConfigOptionUITypes.TOGGLE);
        manager.registerUIType((ConfigOption)WorldMapPrimaryClientConfigOptions.EXPORT_HIGHLIGHTS, BuiltInConfigOptionUITypes.TOGGLE);
        manager.registerUIType((ConfigOption)WorldMapPrimaryClientConfigOptions.NIGHT_EXPORT, BuiltInConfigOptionUITypes.TOGGLE);
        manager.registerUIType((ConfigOption)WorldMapPrimaryClientConfigOptions.EXPORT_SCALE_DOWN_SQUARE, BuiltInConfigOptionUITypes.INT_INDEXED_SLIDER);
        manager.registerUIType((ConfigOption)WorldMapProfiledConfigOptions.MINIMAP_RADAR, BuiltInConfigOptionUITypes.TOGGLE);
        manager.registerUIType((ConfigOption)WorldMapProfiledConfigOptions.RENDER_WAYPOINTS, BuiltInConfigOptionUITypes.TOGGLE);
        manager.registerUIType((ConfigOption)WorldMapProfiledConfigOptions.DISPLAY_TRACKED_PLAYERS, BuiltInConfigOptionUITypes.TOGGLE);
    }
}
