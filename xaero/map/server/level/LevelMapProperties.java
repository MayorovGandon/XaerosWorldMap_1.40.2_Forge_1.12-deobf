//Decompiled by Procyon!

package xaero.map.server.level;

import xaero.lib.common.packet.*;
import java.util.*;
import java.io.*;
import net.minecraft.network.*;

public class LevelMapProperties extends XaeroPacket
{
    private int id;
    private boolean usable;
    
    public LevelMapProperties() {
        this.id = new Random().nextInt();
        this.usable = true;
    }
    
    public void write(final PrintWriter writer) {
        writer.print("id:" + this.id);
    }
    
    public void read(final BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            final String[] args = line.split(":");
            if (args[0].equals("id")) {
                try {
                    this.id = Integer.parseInt(args[1]);
                }
                catch (NumberFormatException ex) {}
            }
        }
    }
    
    public boolean isUsable() {
        return this.usable;
    }
    
    public void setUsable(final boolean usable) {
        this.usable = usable;
    }
    
    public int getId() {
        return this.id;
    }
    
    public void read(final PacketBuffer buf) {
        this.id = buf.readInt();
    }
    
    public void write(final PacketBuffer buf) {
        buf.writeInt(this.id);
    }
}
