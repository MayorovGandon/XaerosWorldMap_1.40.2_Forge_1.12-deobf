//Decompiled by Procyon!

package xaero.map.mods.minimap.element;

import xaero.common.*;
import xaero.common.minimap.render.radar.element.*;
import xaero.map.*;
import xaero.common.minimap.element.render.*;
import java.util.function.*;
import xaero.map.common.config.option.*;
import xaero.lib.common.config.option.*;
import xaero.lib.client.config.*;
import xaero.map.element.render.*;

public class RadarRendererWrapperHelper
{
    public void createWrapper(final IXaeroMinimap modMain, final RadarRenderer radarRenderer) {
        WorldMap.mapElementRenderHandler.add((ElementRenderer)MinimapElementRendererWrapper.Builder.begin((MinimapElementRenderer)radarRenderer).setModMain(modMain).setShouldRenderSupplier((Supplier)new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
                return (Boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.MINIMAP_RADAR);
            }
        }).setOrder(100).build());
    }
}
