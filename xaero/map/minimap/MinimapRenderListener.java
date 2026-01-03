//Decompiled by Procyon!

package xaero.map.minimap;

import net.minecraft.client.*;
import xaero.map.*;
import xaero.map.config.primary.option.*;
import xaero.lib.common.config.option.*;
import xaero.map.common.config.option.*;
import xaero.map.region.*;
import net.minecraft.entity.player.*;
import xaero.lib.client.config.*;
import xaero.lib.common.config.single.*;
import xaero.lib.common.config.*;
import xaero.map.misc.*;
import java.util.*;

public class MinimapRenderListener
{
    private ArrayList<MapRegion> regionBuffer;
    private boolean shouldRequestLoading;
    private boolean playerMoving;
    private int renderedCaveLayer;
    private boolean isCacheOnlyMode;
    private int globalRegionCacheHashCode;
    private boolean reloadEverything;
    private int globalVersion;
    private int globalReloadVersion;
    private int globalCaveStart;
    private int globalCaveDepth;
    private MapRegion prevRegion;
    
    public MinimapRenderListener() {
        this.regionBuffer = new ArrayList<MapRegion>();
    }
    
    public void init(final MapProcessor mapProcessor, final int flooredMapCameraX, final int flooredMapCameraZ) {
        mapProcessor.updateCaveStart();
        final EntityPlayer player = (EntityPlayer)Minecraft.func_71410_x().field_71439_g;
        final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        final SingleConfigManager<Config> primaryConfigManager = (SingleConfigManager<Config>)configManager.getPrimaryConfigManager();
        this.playerMoving = (player.field_70169_q != player.field_70165_t || player.field_70167_r != player.field_70163_u || player.field_70166_s != player.field_70161_v);
        this.renderedCaveLayer = mapProcessor.getCurrentCaveLayer();
        this.isCacheOnlyMode = mapProcessor.getMapWorld().getCurrentDimension().isCacheOnlyMode();
        this.globalRegionCacheHashCode = WorldMap.settings.getRegionCacheHashCode();
        this.reloadEverything = (boolean)primaryConfigManager.getEffective((ConfigOption)WorldMapPrimaryClientConfigOptions.RELOAD_VIEWED);
        this.globalVersion = (int)primaryConfigManager.getEffective(WorldMapPrimaryClientConfigOptions.GLOBAL_VERSION);
        this.globalReloadVersion = (int)primaryConfigManager.getEffective(WorldMapPrimaryClientConfigOptions.RELOAD_VIEWED_VERSION);
        this.globalCaveStart = mapProcessor.getMapWorld().getCurrentDimension().getLayeredMapRegions().getLayer(this.renderedCaveLayer).getCaveStart();
        this.globalCaveDepth = (int)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.CAVE_MODE_DEPTH);
        this.prevRegion = null;
        this.shouldRequestLoading = false;
        final LeveledRegion<?> nextToLoad = (LeveledRegion<?>)mapProcessor.getMapSaveLoad().getNextToLoadByViewing();
        this.shouldRequestLoading = (nextToLoad == null || nextToLoad.shouldAllowAnotherRegionToLoad());
        final int comparisonChunkX = (flooredMapCameraX >> 4) - 16;
        final int comparisonChunkZ = (flooredMapCameraZ >> 4) - 16;
        LeveledRegion.setComparison(comparisonChunkX, comparisonChunkZ, 0, comparisonChunkX, comparisonChunkZ);
    }
    
    public void beforeMinimapRender(final MapRegion region) {
        if (!this.shouldRequestLoading) {
            return;
        }
        if (region != null && region != this.prevRegion) {
            synchronized (region) {
                final int regionHashCode = region.getCacheHashCode();
                if (region.canRequestReload_unsynced() && (region.getLoadState() == 0 || ((region.getLoadState() == 4 || (region.getLoadState() == 2 && region.isBeingWritten())) && ((!this.isCacheOnlyMode && ((this.reloadEverything && region.getReloadVersion() != this.globalReloadVersion) || regionHashCode != this.globalRegionCacheHashCode || (!this.playerMoving && region.caveStartOutdated(this.globalCaveStart, this.globalCaveDepth)) || region.getVersion() != this.globalVersion || (region.getLoadState() != 2 && region.shouldCache()))) || ((region.isMetaLoaded() || region.getLoadState() != 0 || !region.hasHadTerrain()) && region.getHighlightsHash() != region.getDim().getHighlightHandler().getRegionHash(region.getRegionX(), region.getRegionZ()))))) && !this.regionBuffer.contains(region)) {
                    region.calculateSortingChunkDistance();
                    Misc.addToListOfSmallest(10, this.regionBuffer, region);
                }
            }
        }
        this.prevRegion = region;
    }
    
    public void finalize(final MapProcessor mapProcessor) {
        final int toRequest = 1;
        int counter = 0;
        final LeveledRegion<?> nextToLoad = (LeveledRegion<?>)mapProcessor.getMapSaveLoad().getNextToLoadByViewing();
        for (int i = 0; i < this.regionBuffer.size() && counter < toRequest; ++i) {
            final MapRegion region = this.regionBuffer.get(i);
            if (region != nextToLoad || this.regionBuffer.size() <= 1) {
                synchronized (region) {
                    if (region.canRequestReload_unsynced()) {
                        if (region.getLoadState() == 2) {
                            region.requestRefresh(mapProcessor);
                        }
                        else {
                            mapProcessor.getMapSaveLoad().requestLoad(region, "Minimap listener", false);
                        }
                        if (counter == 0) {
                            mapProcessor.getMapSaveLoad().setNextToLoadByViewing((LeveledRegion)region);
                        }
                        ++counter;
                        if (region.getLoadState() == 4) {
                            break;
                        }
                    }
                }
            }
        }
        this.regionBuffer.clear();
    }
    
    public int getRenderedCaveLayer() {
        return this.renderedCaveLayer;
    }
    
    public boolean shouldRequestLoading() {
        return this.shouldRequestLoading;
    }
    
    public boolean isPlayerMoving() {
        return this.playerMoving;
    }
    
    public boolean isCacheOnlyMode() {
        return this.isCacheOnlyMode;
    }
    
    public int getGlobalRegionCacheHashCode() {
        return this.globalRegionCacheHashCode;
    }
    
    public boolean isReloadEverything() {
        return this.reloadEverything;
    }
    
    public int getGlobalVersion() {
        return this.globalVersion;
    }
    
    public int getGlobalReloadVersion() {
        return this.globalReloadVersion;
    }
    
    public int getGlobalCaveStart() {
        return this.globalCaveStart;
    }
    
    public int getGlobalCaveDepth() {
        return this.globalCaveDepth;
    }
}
