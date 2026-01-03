//Decompiled by Procyon!

package xaero.map.message.basic;

import xaero.lib.common.packet.*;
import net.minecraft.network.*;
import xaero.map.*;
import java.util.function.*;
import net.minecraft.entity.player.*;
import xaero.map.server.player.*;

public class HandshakePacket extends XaeroPacket
{
    public static final int NETWORK_COMPATIBILITY = 3;
    private int networkVersion;
    
    public HandshakePacket(final int networkVersion) {
        this.networkVersion = networkVersion;
    }
    
    public HandshakePacket() {
        this(3);
    }
    
    protected void write(final PacketBuffer buf) {
        buf.writeInt(this.networkVersion);
    }
    
    protected void read(final PacketBuffer buf) {
        this.networkVersion = buf.readInt();
    }
    
    public static class ClientHandler implements Consumer<HandshakePacket>
    {
        @Override
        public void accept(final HandshakePacket message) {
            final WorldMapSession session = WorldMapSession.getCurrentSession();
            if (session == null) {
                return;
            }
            session.getMapProcessor().setServerModNetworkVersion(message.networkVersion);
            WorldMap.messageHandler.sendToServer((XaeroPacket)new HandshakePacket());
        }
    }
    
    public static class ServerHandler implements BiConsumer<HandshakePacket, EntityPlayerMP>
    {
        @Override
        public void accept(final HandshakePacket message, final EntityPlayerMP player) {
            final ServerPlayerData playerData = ServerPlayerData.get(player);
            playerData.setClientModNetworkVersion(message.networkVersion);
        }
    }
}
