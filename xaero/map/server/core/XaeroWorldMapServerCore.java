//Decompiled by Procyon!

package xaero.map.server.core;

import xaero.map.*;
import net.minecraft.entity.player.*;

public class XaeroWorldMapServerCore
{
    public static void onServerWorldInfo(final EntityPlayer player) {
        if (!WorldMap.loaded) {
            return;
        }
        WorldMap.commonEvents.onPlayerWorldJoin((EntityPlayerMP)player);
    }
}
