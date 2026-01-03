//Decompiled by Procyon!

package xaero.map.server;

import xaero.map.server.events.*;
import net.minecraftforge.fml.common.event.*;
import xaero.map.*;
import net.minecraftforge.common.*;

public class WorldMapServer
{
    private ServerEvents serverEvents;
    
    public void load(final FMLInitializationEvent event) {
        WorldMap.LOGGER.info("Loading Xaero's World Map - Stage 1/2 (Server)");
        this.serverEvents = new ServerEvents(this);
        MinecraftForge.EVENT_BUS.register((Object)this.serverEvents);
    }
    
    public void loadLater() {
        WorldMap.LOGGER.info("Loading Xaero's World Map - Stage 2/2 (Server)");
    }
    
    public ServerEvents getServerEvents() {
        return this.serverEvents;
    }
}
