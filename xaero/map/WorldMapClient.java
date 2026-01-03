//Decompiled by Procyon!

package xaero.map;

import xaero.map.region.texture.*;
import net.minecraftforge.fml.common.event.*;
import net.minecraft.client.*;

public class WorldMapClient
{
    public BranchTextureRenderer branchTextureRenderer;
    private boolean onTickDone;
    
    public void preInit(final FMLPreInitializationEvent event, final String modId) {
    }
    
    public void onTick() {
        if (this.onTickDone) {
            return;
        }
        this.onTickDone = true;
        this.branchTextureRenderer = new BranchTextureRenderer(Minecraft.func_71410_x().func_147110_a());
    }
}
