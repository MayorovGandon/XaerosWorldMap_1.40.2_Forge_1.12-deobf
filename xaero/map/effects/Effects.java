//Decompiled by Procyon!

package xaero.map.effects;

import net.minecraft.potion.*;

public class Effects
{
    public static Potion NO_WORLD_MAP;
    public static Potion NO_WORLD_MAP_HARMFUL;
    public static Potion NO_CAVE_MAPS;
    public static Potion NO_CAVE_MAPS_HARMFUL;
    
    public static void init() {
        if (Effects.NO_WORLD_MAP != null) {
            return;
        }
        Effects.NO_WORLD_MAP = new NoWorldMapEffect(WorldMapEffect.EffectType.NEUTRAL);
        Effects.NO_WORLD_MAP_HARMFUL = new NoWorldMapEffect(WorldMapEffect.EffectType.HARMFUL);
        Effects.NO_CAVE_MAPS = new NoCaveMapsEffect(WorldMapEffect.EffectType.NEUTRAL);
        Effects.NO_CAVE_MAPS_HARMFUL = new NoCaveMapsEffect(WorldMapEffect.EffectType.HARMFUL);
    }
    
    static {
        Effects.NO_WORLD_MAP = null;
        Effects.NO_WORLD_MAP_HARMFUL = null;
        Effects.NO_CAVE_MAPS = null;
        Effects.NO_CAVE_MAPS_HARMFUL = null;
    }
}
