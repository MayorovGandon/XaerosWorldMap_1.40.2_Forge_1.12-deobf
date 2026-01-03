//Decompiled by Procyon!

package xaero.map.mcworld;

import net.minecraft.client.*;
import net.minecraft.client.multiplayer.*;

public class WorldMapClientWorldDataHelper
{
    public static WorldMapClientWorldData getCurrentWorldData() {
        return getWorldData(Minecraft.func_71410_x().field_71441_e);
    }
    
    public static synchronized WorldMapClientWorldData getWorldData(final WorldClient clientWorld) {
        if (clientWorld == null) {
            return null;
        }
        final IWorldMapClientWorld inter = (IWorldMapClientWorld)clientWorld;
        WorldMapClientWorldData worldmapWorldData = inter.getXaero_worldmapData();
        if (worldmapWorldData == null) {
            inter.setXaero_worldmapData(worldmapWorldData = new WorldMapClientWorldData(clientWorld));
        }
        return worldmapWorldData;
    }
}
