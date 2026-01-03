//Decompiled by Procyon!

package xaero.map.server.radar.tracker;

import java.util.*;
import net.minecraft.entity.player.*;

public class SyncedTrackedPlayer
{
    private final UUID id;
    private double x;
    private double y;
    private double z;
    private int dimension;
    
    public SyncedTrackedPlayer(final UUID id, final double x, final double y, final double z, final int dimension) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimension = dimension;
    }
    
    public SyncedTrackedPlayer setPos(final double x, final double y, final double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }
    
    public SyncedTrackedPlayer setDimension(final int dimension) {
        this.dimension = dimension;
        return this;
    }
    
    public UUID getId() {
        return this.id;
    }
    
    public double getX() {
        return this.x;
    }
    
    public double getY() {
        return this.y;
    }
    
    public double getZ() {
        return this.z;
    }
    
    public int getDimension() {
        return this.dimension;
    }
    
    public boolean matchesEnough(final EntityPlayer player, final double maxAxisDistance) {
        return Math.abs(player.field_70165_t - this.x) <= maxAxisDistance && Math.abs(player.field_70163_u - this.y) <= maxAxisDistance && Math.abs(player.field_70161_v - this.z) <= maxAxisDistance && player.field_70170_p.field_73011_w.getDimension() == this.dimension;
    }
    
    public void update(final EntityPlayer player) {
        this.setPos(player.field_70165_t, player.field_70163_u, player.field_70161_v).setDimension(player.field_70170_p.field_73011_w.getDimension());
    }
    
    public void copyFrom(final SyncedTrackedPlayer trackedPlayer) {
        this.setPos(trackedPlayer.getX(), trackedPlayer.getY(), trackedPlayer.getZ()).setDimension(trackedPlayer.getDimension());
    }
}
