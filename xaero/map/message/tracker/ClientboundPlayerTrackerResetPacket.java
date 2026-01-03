//Decompiled by Procyon!

package xaero.map.message.tracker;

import xaero.lib.common.packet.*;
import net.minecraft.network.*;
import java.util.function.*;
import xaero.map.*;

public class ClientboundPlayerTrackerResetPacket extends XaeroPacket
{
    protected void read(final PacketBuffer buf) {
    }
    
    protected void write(final PacketBuffer buf) {
    }
    
    public static class Handler implements Consumer<ClientboundPlayerTrackerResetPacket>
    {
        @Override
        public void accept(final ClientboundPlayerTrackerResetPacket t) {
            final WorldMapSession session = WorldMapSession.getCurrentSession();
            if (session == null) {
                return;
            }
            session.getMapProcessor().getClientSyncedTrackedPlayerManager().reset();
        }
    }
}
