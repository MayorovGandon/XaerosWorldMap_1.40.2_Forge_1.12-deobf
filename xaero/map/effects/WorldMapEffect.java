//Decompiled by Procyon!

package xaero.map.effects;

import net.minecraft.util.*;
import net.minecraft.potion.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.*;

public class WorldMapEffect extends Potion
{
    private ResourceLocation iconTexture;
    
    protected WorldMapEffect(final EffectType type, final int liquidColorIn, final String idPrefix) {
        super(type == EffectType.HARMFUL, liquidColorIn);
        if (type == EffectType.BENEFICIAL) {
            this.func_188413_j();
        }
        final ResourceLocation id = new ResourceLocation("xaeroworldmap", idPrefix + ((type == EffectType.BENEFICIAL) ? "_beneficial" : ((type == EffectType.HARMFUL) ? "_harmful" : "")));
        this.setRegistryName(id);
        this.func_76390_b("effect." + id.func_110624_b() + "." + id.func_110623_a());
        this.iconTexture = new ResourceLocation(id.func_110624_b(), "textures/mob_effect/" + id.func_110623_a() + ".png");
    }
    
    public void renderHUDEffect(final PotionEffect effect, final Gui gui, final int x, final int y, final float z, final float alpha) {
        super.renderHUDEffect(effect, gui, x, y, z, alpha);
        Minecraft.func_71410_x().func_110434_K().func_110577_a(this.iconTexture);
        Gui.func_146110_a(x + 3, y + 3, 0.0f, 0.0f, 18, 18, 18.0f, 18.0f);
    }
    
    public void renderInventoryEffect(final PotionEffect effect, final Gui gui, final int x, final int y, final float z) {
        super.renderInventoryEffect(effect, gui, x, y, z);
        this.renderHUDEffect(effect, gui, x + 3, y + 4, z, 1.0f);
    }
    
    public enum EffectType
    {
        NEUTRAL, 
        BENEFICIAL, 
        HARMFUL;
    }
}
