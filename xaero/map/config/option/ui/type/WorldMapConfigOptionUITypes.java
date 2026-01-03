//Decompiled by Procyon!

package xaero.map.config.option.ui.type;

import xaero.lib.client.config.option.ui.type.*;
import xaero.lib.common.config.option.*;
import xaero.lib.client.gui.config.*;
import net.minecraft.client.gui.*;
import xaero.lib.common.config.*;
import xaero.map.gui.*;
import xaero.lib.client.config.option.ui.factory.*;

public class WorldMapConfigOptionUITypes
{
    public static final ConfigOptionUIType<ConfigOption<String>> DEFAULT_MAP_TP_COMMAND;
    
    static {
        DEFAULT_MAP_TP_COMMAND = ConfigOptionUIType.Builder.begin().setWidgetFactory(StandardConfigWidgetFactories.getOpenScreenFactory((ICustomOptionEditScreenFactory)new ICustomOptionEditScreenFactory<ConfigOption<String>>() {
            public GuiScreen get(final EditConfigScreen parent, final GuiScreen escape, final Config config, final Config enforced, final ConfigOption<String> option, final Runnable onChange, final boolean readOnly, final boolean includeNullValue) {
                return (GuiScreen)new EditDefaultTpCommandScreen((GuiScreen)parent, escape, config, enforced, option, includeNullValue, includeNullValue, onChange);
            }
        }, (ViewEnforcedCondition)null)).build();
    }
}
