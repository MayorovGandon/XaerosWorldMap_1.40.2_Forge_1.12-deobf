//Decompiled by Procyon!

package xaero.map;

import org.lwjgl.*;
import xaero.map.misc.*;
import java.nio.*;
import org.lwjgl.opengl.*;
import xaero.map.world.*;
import xaero.map.config.util.*;
import java.util.*;
import xaero.map.region.*;

public class MapLimiter
{
    private static final int MIN_LIMIT = 53;
    private static final int DEFAULT_LIMIT = 203;
    private static final int MAX_LIMIT = 403;
    private int availableVRAM;
    private int mostRegionsAtATime;
    private IntBuffer vramBuffer;
    private int driverType;
    private ArrayList<MapDimension> workingDimList;
    
    public MapLimiter() {
        this.availableVRAM = -1;
        this.vramBuffer = BufferUtils.createByteBuffer(64).asIntBuffer();
        this.driverType = -1;
        this.workingDimList = new ArrayList<MapDimension>();
    }
    
    public int getAvailableVRAM() {
        return this.availableVRAM;
    }
    
    private void determineDriverType() {
        if (GLContext.getCapabilities().GL_NVX_gpu_memory_info) {
            this.driverType = 0;
        }
        else if (GLContext.getCapabilities().GL_ATI_meminfo) {
            this.driverType = 1;
        }
        else {
            this.driverType = 2;
        }
    }
    
    public void updateAvailableVRAM() {
        if (this.driverType == -1) {
            this.determineDriverType();
        }
        switch (this.driverType) {
            case 0: {
                BufferCompatibilityFix.clear(this.vramBuffer);
                GL11.glGetInteger(36937, this.vramBuffer);
                this.availableVRAM = this.vramBuffer.get(0);
                break;
            }
            case 1: {
                BufferCompatibilityFix.clear(this.vramBuffer);
                GL11.glGetInteger(34812, this.vramBuffer);
                this.availableVRAM = this.vramBuffer.get(0);
                break;
            }
        }
    }
    
    public int getMostRegionsAtATime() {
        return this.mostRegionsAtATime;
    }
    
    public void setMostRegionsAtATime(final int mostRegionsAtATime) {
        this.mostRegionsAtATime = mostRegionsAtATime;
    }
    
    public void applyLimit(final MapWorld mapWorld, final MapProcessor mapProcessor) {
        int limit = Math.max(this.mostRegionsAtATime, 53);
        int vramDetermined = 0;
        int loadedCount = 0;
        this.workingDimList.clear();
        mapWorld.getDimensions(this.workingDimList);
        for (final MapDimension dim : this.workingDimList) {
            loadedCount += dim.getLayeredMapRegions().loadedCount();
        }
        if (this.availableVRAM != -1) {
            if (this.availableVRAM < 204800) {
                vramDetermined = Math.min(403, loadedCount) - 6;
            }
            else {
                if (loadedCount <= 403) {
                    return;
                }
                vramDetermined = 397;
            }
        }
        else {
            vramDetermined = ((loadedCount > 203) ? 197 : loadedCount);
        }
        if (vramDetermined > limit) {
            limit = vramDetermined;
        }
        int count = 0;
        mapProcessor.pushRenderPause(false, true);
        final LeveledRegion<?> nextToLoad = (LeveledRegion<?>)mapProcessor.getMapSaveLoad().getNextToLoadByViewing();
        final int currentDimIndex = this.workingDimList.indexOf(mapWorld.getCurrentDimension());
        for (int dimCount = 0, dimTotal = this.workingDimList.size(), d = (currentDimIndex + 1) % dimTotal; dimCount < dimTotal && loadedCount > limit; ++dimCount, d = (d + 1) % dimTotal) {
            final MapDimension dimension = this.workingDimList.get(d);
            final LayeredRegionManager regions = dimension.getLayeredMapRegions();
            for (int i = 0; i < regions.loadedCount() && loadedCount > limit; ++i) {
                final LeveledRegion<?> region = regions.getLoadedRegion(i);
                if (region.isLoaded() && !region.shouldBeProcessed() && region.activeBranchUpdateReferences == 0) {
                    region.onLimiterRemoval(mapProcessor);
                    region.deleteTexturesAndBuffers();
                    mapProcessor.getMapSaveLoad().removeToCache((LeveledRegion)region);
                    region.afterLimiterRemoval(mapProcessor);
                    if (region == nextToLoad) {
                        mapProcessor.getMapSaveLoad().setNextToLoadByViewing((MapRegion)null);
                    }
                    ++count;
                    --i;
                    --loadedCount;
                }
            }
        }
        if (count > 0 && WorldMapClientConfigUtils.getDebug()) {
            WorldMap.LOGGER.info("Unloaded " + count + " world map regions!");
        }
        mapProcessor.popRenderPause(false, true);
    }
    
    public void onSessionFinalized() {
        this.workingDimList.clear();
    }
}
