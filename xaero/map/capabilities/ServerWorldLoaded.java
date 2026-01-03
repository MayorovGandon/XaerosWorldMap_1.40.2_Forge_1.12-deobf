//Decompiled by Procyon!

package xaero.map.capabilities;

import net.minecraftforge.common.capabilities.*;
import net.minecraft.util.*;
import net.minecraft.nbt.*;

public class ServerWorldLoaded
{
    public boolean loaded;
    
    public ServerWorldLoaded() {
        this.loaded = true;
    }
    
    public static class Storage implements Capability.IStorage<ServerWorldLoaded>
    {
        public NBTBase writeNBT(final Capability<ServerWorldLoaded> capability, final ServerWorldLoaded instance, final EnumFacing side) {
            return (NBTBase)new NBTTagByte((byte)(instance.loaded ? 1 : 0));
        }
        
        public void readNBT(final Capability<ServerWorldLoaded> capability, final ServerWorldLoaded instance, final EnumFacing side, final NBTBase nbt) {
            instance.loaded = (((NBTTagByte)nbt).func_150287_d() == 1);
        }
    }
}
