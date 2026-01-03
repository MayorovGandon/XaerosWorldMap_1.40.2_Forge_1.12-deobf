//Decompiled by Procyon!

package xaero.map.capabilities;

import net.minecraftforge.common.capabilities.*;
import java.util.concurrent.*;
import net.minecraft.util.*;

public class ServerWorldCapabilities implements ICapabilityProvider
{
    @CapabilityInject(ServerWorldLoaded.class)
    public static final Capability<ServerWorldLoaded> LOADED_CAP;
    private ServerWorldLoaded loadedCapability;
    
    public static void registerCapabilities() {
        CapabilityManager.INSTANCE.register((Class)ServerWorldLoaded.class, (Capability.IStorage)new ServerWorldLoaded.Storage(), (Callable)new Callable<ServerWorldLoaded>() {
            @Override
            public ServerWorldLoaded call() throws Exception {
                return new ServerWorldLoaded();
            }
        });
    }
    
    public ServerWorldCapabilities() {
        this.loadedCapability = new ServerWorldLoaded();
    }
    
    public <T> T getCapability(final Capability<T> cap, final EnumFacing side) {
        if (cap == ServerWorldCapabilities.LOADED_CAP) {
            return (T)this.loadedCapability;
        }
        return null;
    }
    
    public boolean hasCapability(final Capability<?> cap, final EnumFacing facing) {
        return cap == ServerWorldCapabilities.LOADED_CAP;
    }
    
    static {
        LOADED_CAP = null;
    }
}
