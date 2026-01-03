//Decompiled by Procyon!

package xaero.map.message.basic;

import xaero.lib.common.packet.*;
import net.minecraft.network.*;
import java.io.*;
import net.minecraft.nbt.*;
import java.util.function.*;
import xaero.map.mcworld.*;

public class ClientboundRulesPacket extends XaeroPacket
{
    public boolean allowCaveModeOnServer;
    public boolean allowNetherCaveModeOnServer;
    
    public ClientboundRulesPacket(final boolean allowCaveModeOnServer, final boolean allowNetherCaveModeOnServer) {
        this.allowCaveModeOnServer = allowCaveModeOnServer;
        this.allowNetherCaveModeOnServer = allowNetherCaveModeOnServer;
    }
    
    public ClientboundRulesPacket() {
    }
    
    protected void read(final PacketBuffer buf) {
        NBTTagCompound nbt;
        try {
            nbt = buf.func_150793_b();
        }
        catch (IOException e) {
            return;
        }
        this.allowCaveModeOnServer = nbt.func_74767_n("cm");
        this.allowNetherCaveModeOnServer = nbt.func_74767_n("ncm");
    }
    
    protected void write(final PacketBuffer buf) {
        final NBTTagCompound nbt = new NBTTagCompound();
        nbt.func_74757_a("cm", this.allowCaveModeOnServer);
        nbt.func_74757_a("ncm", this.allowNetherCaveModeOnServer);
        buf.func_150786_a(nbt);
    }
    
    public static class ClientHandler implements Consumer<ClientboundRulesPacket>
    {
        @Override
        public void accept(final ClientboundRulesPacket message) {
            WorldMapClientWorldDataHelper.getCurrentWorldData().setSyncedRules(message);
        }
    }
}
