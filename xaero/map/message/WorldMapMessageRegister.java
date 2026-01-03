//Decompiled by Procyon!

package xaero.map.message;

import xaero.lib.common.packet.*;
import xaero.map.server.level.*;
import java.util.function.*;
import xaero.map.message.tracker.*;
import xaero.map.message.basic.*;

public class WorldMapMessageRegister
{
    public void register(final IPacketHandler messageHandler) {
        messageHandler.register(0, (Class)LevelMapProperties.class, (BiConsumer)null, (Consumer)new LevelMapPropertiesConsumer());
        messageHandler.register(1, (Class)HandshakePacket.class, (BiConsumer)new HandshakePacket.ServerHandler(), (Consumer)new HandshakePacket.ClientHandler());
        messageHandler.register(2, (Class)ClientboundTrackedPlayerPacket.class, (BiConsumer)null, (Consumer)new ClientboundTrackedPlayerPacket.Handler());
        messageHandler.register(3, (Class)ClientboundPlayerTrackerResetPacket.class, (BiConsumer)null, (Consumer)new ClientboundPlayerTrackerResetPacket.Handler());
        messageHandler.register(4, (Class)ClientboundRulesPacket.class, (BiConsumer)null, (Consumer)new ClientboundRulesPacket.ClientHandler());
    }
}
