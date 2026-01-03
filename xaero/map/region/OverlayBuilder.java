//Decompiled by Procyon!

package xaero.map.region;

import net.minecraft.client.renderer.texture.*;
import net.minecraft.world.*;
import xaero.map.*;
import net.minecraft.util.math.*;
import xaero.map.cache.*;
import xaero.map.biome.*;
import net.minecraft.client.*;
import xaero.map.misc.*;
import net.minecraft.block.state.*;

public class OverlayBuilder
{
    private static final int MAX_OVERLAYS = 10;
    private Overlay[] overlayBuildingSet;
    private int currentOverlayIndex;
    private OverlayManager overlayManager;
    private TextureAtlasSprite prevIcon;
    private int overlayBiome;
    
    public OverlayBuilder(final OverlayManager overlayManager) {
        this.overlayManager = overlayManager;
        this.overlayBuildingSet = new Overlay[10];
        for (int i = 0; i < this.overlayBuildingSet.length; ++i) {
            this.overlayBuildingSet[i] = new Overlay(0, 0, 0, (byte)0, false);
        }
        this.currentOverlayIndex = -1;
    }
    
    public void startBuilding() {
        this.setOverlayBiome(this.currentOverlayIndex = -1);
    }
    
    public void build(final int state, final int[] biome, final int opacity, final byte light, final World world, final MapProcessor mapProcessor, final BlockPos mutableBlockPos, int biomeId, final BlockStateColorTypeCache colorTypeCache, final BiomeInfoSupplier biomeSupplier) {
        Overlay currentOverlay = this.getCurrentOverlay();
        Overlay nextOverlay = null;
        if (this.currentOverlayIndex < this.overlayBuildingSet.length - 1) {
            nextOverlay = this.overlayBuildingSet[this.currentOverlayIndex + 1];
        }
        TextureAtlasSprite icon = null;
        boolean changed = false;
        if (currentOverlay == null || currentOverlay.getState() != state) {
            icon = Minecraft.func_71410_x().func_175602_ab().func_175023_a().func_178122_a(Misc.getStateById(state));
            changed = (icon != this.prevIcon);
        }
        if (nextOverlay != null && (currentOverlay == null || changed)) {
            final IBlockState s = Misc.getStateById(state);
            boolean glowing = false;
            try {
                glowing = mapProcessor.getMapWriter().isGlowing(s);
            }
            catch (Exception ex) {}
            if (biomeSupplier != null) {
                biomeSupplier.getBiomeInfo(colorTypeCache, world, s, mutableBlockPos, biome, biomeId);
                biomeId = biome[1];
            }
            if (this.getOverlayBiome() == -1) {
                this.setOverlayBiome(biomeId);
            }
            nextOverlay.write(state, biome[0], biome[2], light, glowing);
            currentOverlay = nextOverlay;
            ++this.currentOverlayIndex;
        }
        currentOverlay.increaseOpacity(opacity);
        if (changed) {
            this.prevIcon = icon;
        }
    }
    
    public boolean isEmpty() {
        return this.currentOverlayIndex < 0;
    }
    
    public Overlay getCurrentOverlay() {
        Overlay currentOverlay = null;
        if (this.currentOverlayIndex >= 0) {
            currentOverlay = this.overlayBuildingSet[this.currentOverlayIndex];
        }
        return currentOverlay;
    }
    
    public void finishBuilding(final MapBlock block) {
        for (int i = 0; i <= this.currentOverlayIndex; ++i) {
            final Overlay o = this.overlayBuildingSet[i];
            final Overlay original = this.overlayManager.getOriginal(o);
            if (o == original) {
                this.overlayBuildingSet[i] = new Overlay(0, 0, 0, (byte)0, false);
            }
            block.addOverlay(original);
        }
    }
    
    public int getOverlayBiome() {
        return this.overlayBiome;
    }
    
    public void setOverlayBiome(final int overlayBiome) {
        this.overlayBiome = overlayBiome;
    }
}
