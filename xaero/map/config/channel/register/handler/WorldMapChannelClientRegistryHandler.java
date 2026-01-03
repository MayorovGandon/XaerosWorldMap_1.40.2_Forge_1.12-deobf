//Decompiled by Procyon!

package xaero.map.config.channel.register.handler;

import xaero.lib.client.config.channel.register.handler.*;
import xaero.lib.client.config.option.*;
import xaero.map.config.primary.option.*;
import xaero.lib.client.config.option.ui.*;
import xaero.map.config.option.ui.*;
import xaero.lib.client.config.listener.*;
import xaero.map.config.listener.handler.*;
import xaero.lib.client.config.option.value.redirect.*;
import xaero.map.config.option.value.redirect.*;

public class WorldMapChannelClientRegistryHandler implements IConfigChannelClientRegistryHandler
{
    public void registerPrimaryClientOptions(final ClientConfigOptionManager manager) {
        WorldMapPrimaryClientConfigOptions.registerAll(manager);
    }
    
    public void registerConfigOptionUITypes(final ConfigOptionUITypeManager configOptionUITypeManager) {
        WorldMapConfigOptionUIRegister.registerAll(configOptionUITypeManager);
    }
    
    public void registerClientOptionChangeHandlers(final ClientConfigChangeListener registry) {
        WorldMapConfigOptionClientHandlers.registerAll(registry);
    }
    
    public void registerOptionClientRedirectors(final ClientOptionValueRedirectorManager manager) {
        WorldMapConfigOptionClientRedirectors.registerAll(manager);
    }
}
