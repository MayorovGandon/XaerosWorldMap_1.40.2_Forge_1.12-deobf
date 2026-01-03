//Decompiled by Procyon!

package xaero.map.region;

import xaero.map.*;
import xaero.map.common.config.option.*;
import xaero.lib.common.config.option.*;
import xaero.lib.client.config.*;

public class MapUpdateFastConfig
{
    public final int blockColors;
    public final boolean biomeBlending;
    public final boolean biomeColorsInVanilla;
    public final int terrainSlopes;
    public final boolean terrainDepth;
    public final boolean stainedGlass;
    public final boolean legibleCaveMaps;
    public final boolean adjustHeightForShortBlocks;
    
    public MapUpdateFastConfig() {
        final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        this.blockColors = (int)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.BLOCK_COLORS);
        this.biomeBlending = (boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.BIOME_BLENDING);
        this.biomeColorsInVanilla = (boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.BIOME_COLORS_IN_VANILLA);
        this.terrainSlopes = (int)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.TERRAIN_SLOPES);
        this.terrainDepth = (boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.TERRAIN_DEPTH);
        this.stainedGlass = (boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.STAINED_GLASS);
        this.legibleCaveMaps = (boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.LEGIBLE_CAVE_MAPS);
        this.adjustHeightForShortBlocks = (boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.ADJUST_HEIGHT_FOR_SHORT_BLOCKS);
    }
}
