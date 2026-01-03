//Decompiled by Procyon!

package xaero.map.common.config.channel.register.handler;

import xaero.lib.common.config.channel.register.handler.*;
import xaero.lib.common.config.option.*;
import xaero.map.common.config.primary.option.*;
import xaero.map.common.config.option.*;
import xaero.lib.common.config.server.listener.*;
import xaero.map.common.config.listener.handler.*;
import xaero.lib.common.config.listener.handler.*;
import xaero.lib.common.config.option.value.redirect.*;
import xaero.map.common.config.option.value.redirect.*;

public class WorldMapChannelCommonRegistryHandler implements IConfigChannelCommonRegistryHandler
{
    public void registerPrimaryCommonOptions(final ConfigOptionManager manager) {
        WorldMapPrimaryCommonConfigOptions.registerAll(manager);
    }
    
    public void registerProfiledOptions(final ConfigOptionManager manager) {
        WorldMapProfiledConfigOptions.registerAll(manager);
    }
    
    public void registerServerOptionChangeHandlers(final ServerConfigChangeListener registry) {
        WorldMapConfigOptionServerHandlers.registerAll((HandlerBasedConfigChangeListener)registry);
    }
    
    public void registerOptionServerRedirectors(final OptionValueRedirectorManager manager) {
        WorldMapConfigOptionServerRedirectors.registerAll(manager);
    }
}
