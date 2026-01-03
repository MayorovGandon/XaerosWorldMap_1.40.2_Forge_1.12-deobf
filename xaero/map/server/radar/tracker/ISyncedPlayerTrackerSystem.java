//Decompiled by Procyon!

package xaero.map.server.radar.tracker;

import net.minecraft.entity.player.*;

public interface ISyncedPlayerTrackerSystem
{
    int getTrackingLevel(final EntityPlayer p0, final EntityPlayer p1);
    
    boolean isPartySystem();
}
