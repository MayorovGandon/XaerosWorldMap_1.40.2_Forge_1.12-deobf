//Decompiled by Procyon!

package xaero.map.common.config.primary.option;

import xaero.lib.common.config.option.*;
import java.util.*;

public class WorldMapPrimaryCommonConfigOptions
{
    private static final List<ConfigOption<?>> ALL;
    public static final BooleanConfigOption REGISTER_EFFECTS;
    
    public static void registerAll(final ConfigOptionManager manager) {
        for (final ConfigOption<?> option : WorldMapPrimaryCommonConfigOptions.ALL) {
            manager.register((ConfigOption)option);
        }
    }
    
    static {
        ALL = new ArrayList<ConfigOption<?>>();
        REGISTER_EFFECTS = ((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)BooleanConfigOption.Builder.begin().setId("register_status_effects")).setDefaultValue((Object)true)).build((List)WorldMapPrimaryCommonConfigOptions.ALL);
    }
}
