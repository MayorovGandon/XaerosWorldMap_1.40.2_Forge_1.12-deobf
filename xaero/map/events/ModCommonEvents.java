//Decompiled by Procyon!

package xaero.map.events;

import net.minecraftforge.event.*;
import net.minecraft.potion.*;
import xaero.map.*;
import xaero.map.common.config.primary.option.*;
import xaero.lib.common.config.option.*;
import xaero.map.effects.*;
import net.minecraftforge.registries.*;
import xaero.lib.common.config.single.*;
import xaero.lib.common.config.*;
import net.minecraftforge.fml.common.eventhandler.*;

public class ModCommonEvents
{
    @SubscribeEvent
    public void handleRegisterEffectsEvent(final RegistryEvent.Register<Potion> event) {
        final SingleConfigManager<Config> primaryCommonConfig = (SingleConfigManager<Config>)WorldMap.INSTANCE.getConfigs().getPrimaryCommonConfigManager();
        final boolean shouldRegisterEffects = (boolean)primaryCommonConfig.getEffective((ConfigOption)WorldMapPrimaryCommonConfigOptions.REGISTER_EFFECTS);
        if (!shouldRegisterEffects) {
            return;
        }
        Effects.init();
        event.getRegistry().register((IForgeRegistryEntry)Effects.NO_WORLD_MAP);
        event.getRegistry().register((IForgeRegistryEntry)Effects.NO_WORLD_MAP_HARMFUL);
        event.getRegistry().register((IForgeRegistryEntry)Effects.NO_CAVE_MAPS);
        event.getRegistry().register((IForgeRegistryEntry)Effects.NO_CAVE_MAPS_HARMFUL);
    }
}
