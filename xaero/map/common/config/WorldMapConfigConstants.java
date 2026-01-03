//Decompiled by Procyon!

package xaero.map.common.config;

import net.minecraft.util.text.*;

public class WorldMapConfigConstants
{
    public static final ITextComponent INGAME_TOOLTIP;
    public static final ITextComponent LEGACY_PLUGIN_TOOLTIP;
    public static final ITextComponent FAIRPLAY_TOOLTIP;
    public static final ITextComponent EFFECT_TOOLTIP;
    public static final ITextComponent MINIMAP_TOOLTIP;
    public static final ITextComponent OLD_MINECRAFT_TOOLTIP;
    public static ITextComponent[] BLOCK_COLORS_NAMES;
    public static ITextComponent[] TERRAIN_SLOPES_NAMES;
    public static ITextComponent SEC;
    public static ITextComponent MILLISEC;
    public static ITextComponent[] DEFAULT_CAVE_MODE_TYPE_NAMES;
    public static ITextComponent[] ARROW_COLOR_NAMES;
    public static float[][] ARROW_COLORS;
    
    static {
        INGAME_TOOLTIP = (ITextComponent)new TextComponentTranslation("gui.xaero_wm_option_requires_ingame", new Object[0]);
        LEGACY_PLUGIN_TOOLTIP = (ITextComponent)new TextComponentTranslation("gui.xaero_world_map_redirect_legacy", new Object[0]);
        FAIRPLAY_TOOLTIP = (ITextComponent)new TextComponentTranslation("gui.xaero_world_map_config_redirect_fairplay", new Object[0]);
        EFFECT_TOOLTIP = (ITextComponent)new TextComponentTranslation("gui.xaero_world_map_config_redirect_effect", new Object[0]);
        MINIMAP_TOOLTIP = (ITextComponent)new TextComponentTranslation("gui.xaero_wm_option_requires_minimap", new Object[0]);
        OLD_MINECRAFT_TOOLTIP = (ITextComponent)new TextComponentTranslation("gui.xaero_wm_old_minecraft", new Object[0]);
        WorldMapConfigConstants.BLOCK_COLORS_NAMES = new ITextComponent[] { (ITextComponent)new TextComponentTranslation("gui.xaero_accurate", new Object[0]), (ITextComponent)new TextComponentTranslation("gui.xaero_vanilla", new Object[0]) };
        WorldMapConfigConstants.TERRAIN_SLOPES_NAMES = new ITextComponent[] { (ITextComponent)new TextComponentTranslation("gui.xaero_off", new Object[0]), (ITextComponent)new TextComponentTranslation("gui.xaero_wm_slopes_legacy", new Object[0]), (ITextComponent)new TextComponentTranslation("gui.xaero_wm_slopes_default_3d", new Object[0]), (ITextComponent)new TextComponentTranslation("gui.xaero_wm_slopes_default_2d", new Object[0]) };
        WorldMapConfigConstants.SEC = (ITextComponent)new TextComponentTranslation("gui.xaero_wm_unit_s", new Object[0]);
        WorldMapConfigConstants.MILLISEC = (ITextComponent)new TextComponentTranslation("gui.xaero_wm_unit_ms", new Object[0]);
        WorldMapConfigConstants.DEFAULT_CAVE_MODE_TYPE_NAMES = new ITextComponent[] { (ITextComponent)new TextComponentTranslation("gui.xaero_off", new Object[0]), (ITextComponent)new TextComponentTranslation("gui.xaero_wm_cave_mode_type_layered", new Object[0]), (ITextComponent)new TextComponentTranslation("gui.xaero_wm_cave_mode_type_full", new Object[0]) };
        WorldMapConfigConstants.ARROW_COLOR_NAMES = new ITextComponent[] { (ITextComponent)new TextComponentTranslation("gui.xaero_wm_color_minimap", new Object[0]), (ITextComponent)new TextComponentTranslation("gui.xaero_wm_team_color", new Object[0]), (ITextComponent)new TextComponentTranslation("gui.xaero_wm_red", new Object[0]), (ITextComponent)new TextComponentTranslation("gui.xaero_wm_green", new Object[0]), (ITextComponent)new TextComponentTranslation("gui.xaero_wm_blue", new Object[0]), (ITextComponent)new TextComponentTranslation("gui.xaero_wm_yellow", new Object[0]), (ITextComponent)new TextComponentTranslation("gui.xaero_wm_purple", new Object[0]), (ITextComponent)new TextComponentTranslation("gui.xaero_wm_white", new Object[0]), (ITextComponent)new TextComponentTranslation("gui.xaero_wm_black", new Object[0]), (ITextComponent)new TextComponentTranslation("gui.xaero_wm_legacy_color", new Object[0]) };
        WorldMapConfigConstants.ARROW_COLORS = new float[][] { { 0.8f, 0.1f, 0.1f, 1.0f }, { 0.09f, 0.57f, 0.0f, 1.0f }, { 0.0f, 0.55f, 1.0f, 1.0f }, { 1.0f, 0.93f, 0.0f, 1.0f }, { 0.73f, 0.33f, 0.83f, 1.0f }, { 1.0f, 1.0f, 1.0f, 1.0f }, { 0.0f, 0.0f, 0.0f, 1.0f }, { 0.4588f, 0.0f, 0.0f, 1.0f } };
    }
}
