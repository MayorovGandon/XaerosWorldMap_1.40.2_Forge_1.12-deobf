//Decompiled by Procyon!

package xaero.map.config.listener.handler;

import xaero.lib.common.config.*;
import net.minecraft.client.*;
import xaero.map.*;
import net.minecraft.entity.player.*;
import net.minecraft.world.*;
import xaero.map.config.primary.option.*;
import xaero.lib.common.config.option.*;
import xaero.lib.client.config.listener.*;
import xaero.map.common.config.option.*;
import java.util.function.*;

public class WorldMapConfigOptionClientHandlers
{
    private static void handleBlockColors(final Config config) {
        WorldMap.settings.updateRegionCacheHashCode();
    }
    
    private static void handleTerrainDepth(final Config config) {
        WorldMap.settings.updateRegionCacheHashCode();
    }
    
    private static void handleTerrainSlopes(final Config config) {
        WorldMap.settings.updateRegionCacheHashCode();
    }
    
    private static void handleBiomeBlending(final Config config) {
        WorldMap.settings.updateRegionCacheHashCode();
    }
    
    private static void handleBiomesInVanilla(final Config config) {
        WorldMap.settings.updateRegionCacheHashCode();
    }
    
    private static void handleAdjustShortBlockHeight(final Config config) {
        WorldMap.settings.updateRegionCacheHashCode();
    }
    
    private static void handleStainedGlass(final Config config) {
        WorldMap.settings.updateRegionCacheHashCode();
    }
    
    private static void handleLegibleCaveMaps(final Config config) {
        WorldMap.settings.updateRegionCacheHashCode();
    }
    
    private static void handleFlowers(final Config config) {
        final Minecraft mc = Minecraft.func_71410_x();
        if (mc.field_71441_e == null || mc.field_71439_g == null) {
            return;
        }
        final WorldMapSession session = WorldMapSession.getCurrentSession();
        if (session != null) {
            session.getMapProcessor().getMapWriter().setDirtyInWriteDistance((EntityPlayer)mc.field_71439_g, (World)mc.field_71441_e);
        }
    }
    
    private static void clearAllCachedHighlightHashes() {
        final WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
        if (worldmapSession == null) {
            return;
        }
        synchronized (worldmapSession.getMapProcessor().uiSync) {
            worldmapSession.getMapProcessor().getMapWorld().clearAllCachedHighlightHashes();
        }
    }
    
    private static void handleOpacClaims(final Config config) {
        clearAllCachedHighlightHashes();
    }
    
    private static void handleOpacClaimFillOpacity(final Config config) {
        clearAllCachedHighlightHashes();
    }
    
    private static void handleOpacClaimBorderOpacity(final Config config) {
        clearAllCachedHighlightHashes();
    }
    
    private static void handleReloadViewed(final Config config) {
        if (config.get((ConfigOption)WorldMapPrimaryClientConfigOptions.RELOAD_VIEWED)) {
            config.set((ConfigOption)WorldMapPrimaryClientConfigOptions.RELOAD_VIEWED_VERSION, (Object)((int)config.get((ConfigOption)WorldMapPrimaryClientConfigOptions.RELOAD_VIEWED_VERSION) + 1));
        }
    }
    
    private static void handleMapItem(final Config config) {
        final WorldMapSession session = WorldMapSession.getCurrentSession();
        if (session != null) {
            session.getMapProcessor().updateMapItem();
        }
    }
    
    public static void registerAll(final ClientConfigChangeListener registry) {
        registry.register((ConfigOption)WorldMapProfiledConfigOptions.BLOCK_COLORS, (Consumer)new Consumer<Config>() {
            @Override
            public void accept(final Config config) {
                handleBlockColors(config);
            }
        });
        registry.register((ConfigOption)WorldMapProfiledConfigOptions.TERRAIN_DEPTH, (Consumer)new Consumer<Config>() {
            @Override
            public void accept(final Config config) {
                handleTerrainDepth(config);
            }
        });
        registry.register((ConfigOption)WorldMapProfiledConfigOptions.TERRAIN_SLOPES, (Consumer)new Consumer<Config>() {
            @Override
            public void accept(final Config config) {
                handleTerrainSlopes(config);
            }
        });
        registry.register((ConfigOption)WorldMapProfiledConfigOptions.BIOME_BLENDING, (Consumer)new Consumer<Config>() {
            @Override
            public void accept(final Config config) {
                handleBiomeBlending(config);
            }
        });
        registry.register((ConfigOption)WorldMapProfiledConfigOptions.BIOME_COLORS_IN_VANILLA, (Consumer)new Consumer<Config>() {
            @Override
            public void accept(final Config config) {
                handleBiomesInVanilla(config);
            }
        });
        registry.register((ConfigOption)WorldMapProfiledConfigOptions.ADJUST_HEIGHT_FOR_SHORT_BLOCKS, (Consumer)new Consumer<Config>() {
            @Override
            public void accept(final Config config) {
                handleAdjustShortBlockHeight(config);
            }
        });
        registry.register((ConfigOption)WorldMapProfiledConfigOptions.STAINED_GLASS, (Consumer)new Consumer<Config>() {
            @Override
            public void accept(final Config config) {
                handleStainedGlass(config);
            }
        });
        registry.register((ConfigOption)WorldMapProfiledConfigOptions.LEGIBLE_CAVE_MAPS, (Consumer)new Consumer<Config>() {
            @Override
            public void accept(final Config config) {
                handleLegibleCaveMaps(config);
            }
        });
        registry.register((ConfigOption)WorldMapProfiledConfigOptions.FLOWERS, (Consumer)new Consumer<Config>() {
            @Override
            public void accept(final Config config) {
                handleFlowers(config);
            }
        });
        registry.register((ConfigOption)WorldMapProfiledConfigOptions.OPAC_CLAIMS, (Consumer)new Consumer<Config>() {
            @Override
            public void accept(final Config config) {
                handleOpacClaims(config);
            }
        });
        registry.register((ConfigOption)WorldMapProfiledConfigOptions.OPAC_CLAIMS_FILL_OPACITY, (Consumer)new Consumer<Config>() {
            @Override
            public void accept(final Config config) {
                handleOpacClaimFillOpacity(config);
            }
        });
        registry.register((ConfigOption)WorldMapProfiledConfigOptions.OPAC_CLAIMS_BORDER_OPACITY, (Consumer)new Consumer<Config>() {
            @Override
            public void accept(final Config config) {
                handleOpacClaimBorderOpacity(config);
            }
        });
        registry.register((ConfigOption)WorldMapPrimaryClientConfigOptions.RELOAD_VIEWED, (Consumer)new Consumer<Config>() {
            @Override
            public void accept(final Config config) {
                handleReloadViewed(config);
            }
        });
        registry.register(WorldMapProfiledConfigOptions.MAP_ITEM, (Consumer)new Consumer<Config>() {
            @Override
            public void accept(final Config config) {
                handleMapItem(config);
            }
        });
    }
}
