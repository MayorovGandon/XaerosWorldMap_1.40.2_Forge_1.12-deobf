//Decompiled by Procyon!

package xaero.map;

import xaero.map.file.*;
import java.util.*;
import xaero.map.world.*;
import java.util.concurrent.*;
import xaero.map.region.*;
import net.minecraft.client.*;
import xaero.map.gui.*;

public class MapFullReloader
{
    public static final String CONVERTED_WORLD_SAVE_MW = "cm$converted";
    private final int caveLayer;
    private final boolean resave;
    private final Iterator<RegionDetection> regionDetectionIterator;
    private final Deque<RegionDetection> retryLaterDeque;
    private final MapDimension mapDimension;
    private final MapProcessor mapProcessor;
    private MapRegion lastRequestedRegion;
    
    public MapFullReloader(final int caveLayer, final boolean resave, final Iterator<RegionDetection> regionDetectionIterator, final MapDimension mapDimension, final MapProcessor mapProcessor) {
        this.caveLayer = caveLayer;
        this.resave = resave;
        this.regionDetectionIterator = regionDetectionIterator;
        this.retryLaterDeque = new LinkedBlockingDeque<RegionDetection>();
        this.mapDimension = mapDimension;
        this.mapProcessor = mapProcessor;
    }
    
    public void onRenderProcess() {
        final LeveledRegion<?> nextToLoad = (LeveledRegion<?>)this.mapProcessor.getMapSaveLoad().getNextToLoadByViewing();
        if (nextToLoad == null || nextToLoad.shouldAllowAnotherRegionToLoad()) {
            RegionDetection next;
            if (!this.regionDetectionIterator.hasNext()) {
                next = (this.retryLaterDeque.isEmpty() ? null : this.retryLaterDeque.removeFirst());
            }
            else {
                next = this.regionDetectionIterator.next();
            }
            if (next != null) {
                final MapRegion nextRegionToReload = this.mapProcessor.getLeafMapRegion(this.caveLayer, next.getRegionX(), next.getRegionZ(), true);
                if (nextRegionToReload == null) {
                    this.retryLaterDeque.add(next);
                    return;
                }
                nextRegionToReload.setHasHadTerrain();
                synchronized (nextRegionToReload) {
                    if (!nextRegionToReload.canRequestReload_unsynced()) {
                        this.retryLaterDeque.add(next);
                        return;
                    }
                    if (this.resave) {
                        nextRegionToReload.setResaving(true);
                        nextRegionToReload.setBeingWritten(true);
                    }
                    if (nextRegionToReload.getLoadState() == 2) {
                        nextRegionToReload.requestRefresh(this.mapProcessor);
                    }
                    else {
                        this.mapProcessor.getMapSaveLoad().requestLoad(nextRegionToReload, "full reload");
                    }
                    this.mapProcessor.getMapSaveLoad().setNextToLoadByViewing((LeveledRegion)nextRegionToReload);
                    this.lastRequestedRegion = nextRegionToReload;
                }
                return;
            }
        }
        if (!this.regionDetectionIterator.hasNext() && this.retryLaterDeque.isEmpty() && (this.lastRequestedRegion == null || this.lastRequestedRegion.shouldAllowAnotherRegionToLoad())) {
            this.mapDimension.clearFullMapReload();
            if (this.resave && this.mapDimension.isUsingWorldSave()) {
                this.mapDimension.addMultiworldChecked("cm$converted");
                this.mapDimension.setMultiworldName("cm$converted", "gui.xaero_converted_world_save");
                this.mapDimension.saveConfigUnsynced();
            }
            if (Minecraft.func_71410_x().field_71462_r instanceof GuiWorldMapSettings || Minecraft.func_71410_x().field_71462_r instanceof GuiMap) {
                Minecraft.func_71410_x().field_71462_r.func_146280_a(Minecraft.func_71410_x(), Minecraft.func_71410_x().field_71462_r.field_146294_l, Minecraft.func_71410_x().field_71462_r.field_146295_m);
            }
        }
    }
    
    public boolean isPartOfReload(final MapRegion region) {
        return region.getDim() == this.mapDimension && region.getCaveLayer() == this.caveLayer;
    }
    
    public boolean isResave() {
        return this.resave;
    }
}
