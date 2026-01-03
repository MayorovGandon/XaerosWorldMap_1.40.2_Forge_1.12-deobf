//Decompiled by Procyon!

package xaero.map.message.tracker;

import xaero.lib.common.packet.*;
import java.util.*;
import net.minecraft.network.*;
import java.io.*;
import net.minecraft.nbt.*;
import java.util.function.*;
import xaero.map.*;

public class ClientboundTrackedPlayerPacket extends XaeroPacket
{
    private boolean remove;
    private UUID id;
    private double x;
    private double y;
    private double z;
    private int dimension;
    
    public ClientboundTrackedPlayerPacket(final boolean remove, final UUID id, final double x, final double y, final double z, final int dimension) {
        this.remove = remove;
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimension = dimension;
    }
    
    public ClientboundTrackedPlayerPacket() {
    }
    
    public void read(final PacketBuffer buffer) {
        NBTTagCompound nbt;
        try {
            nbt = buffer.func_150793_b();
        }
        catch (IOException e) {
            return;
        }
        this.remove = nbt.func_74767_n("r");
        this.id = nbt.func_186857_a("i");
        if (!this.remove) {
            this.x = nbt.func_74769_h("x");
            this.y = nbt.func_74769_h("y");
            this.z = nbt.func_74769_h("z");
            this.dimension = nbt.func_74762_e("d");
        }
    }
    
    public void write(final PacketBuffer buffer) {
        final NBTTagCompound nbt = new NBTTagCompound();
        nbt.func_74757_a("r", this.remove);
        nbt.func_186854_a("i", this.id);
        if (!this.remove) {
            nbt.func_74780_a("x", this.x);
            nbt.func_74780_a("y", this.y);
            nbt.func_74780_a("z", this.z);
            nbt.func_74768_a("d", this.dimension);
        }
        buffer.func_150786_a(nbt);
    }
    
    public static class Handler implements Consumer<ClientboundTrackedPlayerPacket>
    {
        @Override
        public void accept(final ClientboundTrackedPlayerPacket t) {
            final WorldMapSession session = WorldMapSession.getCurrentSession();
            if (session == null) {
                return;
            }
            if (t.remove) {
                session.getMapProcessor().getClientSyncedTrackedPlayerManager().remove(t.id);
                return;
            }
            session.getMapProcessor().getClientSyncedTrackedPlayerManager().update(t.id, t.x, t.y, t.z, t.dimension);
        }
    }
}
