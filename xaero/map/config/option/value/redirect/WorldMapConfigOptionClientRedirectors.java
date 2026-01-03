//Decompiled by Procyon!

package xaero.map.config.option.value.redirect;

import xaero.lib.client.config.option.value.redirect.*;
import xaero.map.common.config.option.*;
import xaero.lib.common.config.channel.*;
import xaero.map.effects.*;
import xaero.map.misc.*;
import xaero.map.config.util.*;
import java.util.function.*;
import net.minecraft.client.gui.*;
import xaero.lib.common.config.util.*;
import net.minecraft.util.text.*;
import xaero.map.common.config.*;
import xaero.lib.common.config.option.*;
import xaero.map.mods.*;

public class WorldMapConfigOptionClientRedirectors
{
    public static void registerAll(final ClientOptionValueRedirectorManager manager) {
        manager.register((ConfigOption)WorldMapProfiledConfigOptions.CAVE_MODE_ALLOWED, (Supplier)new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return false;
            }
        }, (Predicate)new Predicate<ConfigChannel>() {
            @Override
            public boolean test(final ConfigChannel channel) {
                return Misc.hasEffect(Effects.NO_CAVE_MAPS) || Misc.hasEffect(Effects.NO_CAVE_MAPS_HARMFUL) || WorldMapClientConfigUtils.isFairPlay() || WorldMapClientConfigUtils.isCaveModeDisabledLegacy();
            }
        }, (Function)new Function<GuiScreen, GuiScreen>() {
            @Override
            public GuiScreen apply(final GuiScreen current) {
                return null;
            }
        }, ConfigConstants.OFF, (Supplier)new Supplier<ITextComponent>() {
            @Override
            public ITextComponent get() {
                return (Misc.hasEffect(Effects.NO_CAVE_MAPS) || Misc.hasEffect(Effects.NO_CAVE_MAPS_HARMFUL)) ? WorldMapConfigConstants.EFFECT_TOOLTIP : (WorldMapClientConfigUtils.isFairPlay() ? WorldMapConfigConstants.FAIRPLAY_TOOLTIP : WorldMapConfigConstants.LEGACY_PLUGIN_TOOLTIP);
            }
        });
        manager.register((ConfigOption)WorldMapProfiledConfigOptions.WAYPOINTS, (Supplier)new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return false;
            }
        }, (Predicate)new Predicate<ConfigChannel>() {
            @Override
            public boolean test(final ConfigChannel channel) {
                return !SupportMods.minimap();
            }
        }, (Function)new Function<GuiScreen, GuiScreen>() {
            @Override
            public GuiScreen apply(final GuiScreen current) {
                return null;
            }
        }, ConfigConstants.OFF, (Supplier)new Supplier<ITextComponent>() {
            @Override
            public ITextComponent get() {
                return WorldMapConfigConstants.MINIMAP_TOOLTIP;
            }
        });
        manager.register((ConfigOption)WorldMapProfiledConfigOptions.WAYPOINT_BACKGROUNDS, (Supplier)new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return false;
            }
        }, (Predicate)new Predicate<ConfigChannel>() {
            @Override
            public boolean test(final ConfigChannel channel) {
                return !SupportMods.minimap();
            }
        }, (Function)new Function<GuiScreen, GuiScreen>() {
            @Override
            public GuiScreen apply(final GuiScreen current) {
                return null;
            }
        }, ConfigConstants.OFF, (Supplier)new Supplier<ITextComponent>() {
            @Override
            public ITextComponent get() {
                return WorldMapConfigConstants.MINIMAP_TOOLTIP;
            }
        });
        manager.register((ConfigOption)WorldMapProfiledConfigOptions.WAYPOINT_SCALE, (Supplier)new Supplier<Double>() {
            @Override
            public Double get() {
                return 1.0;
            }
        }, (Predicate)new Predicate<ConfigChannel>() {
            @Override
            public boolean test(final ConfigChannel channel) {
                return !SupportMods.minimap();
            }
        }, (Function)new Function<GuiScreen, GuiScreen>() {
            @Override
            public GuiScreen apply(final GuiScreen current) {
                return null;
            }
        }, ConfigConstants.OFF, (Supplier)new Supplier<ITextComponent>() {
            @Override
            public ITextComponent get() {
                return WorldMapConfigConstants.MINIMAP_TOOLTIP;
            }
        });
        manager.register((ConfigOption)WorldMapProfiledConfigOptions.MIN_ZOOM_LOCAL_WAYPOINTS, (Supplier)new Supplier<Double>() {
            @Override
            public Double get() {
                return 0.0;
            }
        }, (Predicate)new Predicate<ConfigChannel>() {
            @Override
            public boolean test(final ConfigChannel channel) {
                return !SupportMods.minimap();
            }
        }, (Function)new Function<GuiScreen, GuiScreen>() {
            @Override
            public GuiScreen apply(final GuiScreen current) {
                return null;
            }
        }, ConfigConstants.OFF, (Supplier)new Supplier<ITextComponent>() {
            @Override
            public ITextComponent get() {
                return WorldMapConfigConstants.MINIMAP_TOOLTIP;
            }
        });
        manager.register((ConfigOption)WorldMapProfiledConfigOptions.MINIMAP_RADAR, (Supplier)new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return false;
            }
        }, (Predicate)new Predicate<ConfigChannel>() {
            @Override
            public boolean test(final ConfigChannel channel) {
                return !SupportMods.minimap();
            }
        }, (Function)new Function<GuiScreen, GuiScreen>() {
            @Override
            public GuiScreen apply(final GuiScreen current) {
                return null;
            }
        }, ConfigConstants.OFF, (Supplier)new Supplier<ITextComponent>() {
            @Override
            public ITextComponent get() {
                return WorldMapConfigConstants.MINIMAP_TOOLTIP;
            }
        });
        manager.register((ConfigOption)WorldMapProfiledConfigOptions.RENDER_WAYPOINTS, (Supplier)new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return false;
            }
        }, (Predicate)new Predicate<ConfigChannel>() {
            @Override
            public boolean test(final ConfigChannel channel) {
                return !SupportMods.minimap();
            }
        }, (Function)new Function<GuiScreen, GuiScreen>() {
            @Override
            public GuiScreen apply(final GuiScreen current) {
                return null;
            }
        }, ConfigConstants.OFF, (Supplier)new Supplier<ITextComponent>() {
            @Override
            public ITextComponent get() {
                return WorldMapConfigConstants.MINIMAP_TOOLTIP;
            }
        });
    }
}
