//Decompiled by Procyon!

package xaero.map.core;

import java.util.*;
import com.google.common.eventbus.*;
import net.minecraftforge.fml.common.*;

public class CoreModContainer extends DummyModContainer
{
    public CoreModContainer() {
        super(new ModMetadata());
        final ModMetadata meta = this.getMetadata();
        meta.modId = "xaeroworldmap_core";
        meta.name = "XaeroWorldMapCore";
        meta.description = "Required by Xaero's World Map.";
        meta.version = "1.12.2-1.0";
        meta.authorList = Arrays.asList("Xaero");
    }
    
    public boolean registerBus(final EventBus bus, final LoadController controller) {
        bus.register((Object)this);
        return true;
    }
}
