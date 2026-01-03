//Decompiled by Procyon!

package xaero.map.radar.tracker;

import xaero.map.radar.tracker.system.*;
import xaero.map.animation.*;
import java.util.*;

public class PlayerTrackerMapElement<P>
{
    private P player;
    private IPlayerTrackerSystem<P> system;
    private SlowingAnimation fadeAnim;
    private boolean renderedOnRadar;
    
    public PlayerTrackerMapElement(final P player, final IPlayerTrackerSystem<P> system) {
        this.player = player;
        this.system = system;
    }
    
    public UUID getPlayerId() {
        return this.system.getReader().getId(this.player);
    }
    
    public double getX() {
        return this.system.getReader().getX(this.player);
    }
    
    public double getY() {
        return this.system.getReader().getY(this.player);
    }
    
    public double getZ() {
        return this.system.getReader().getZ(this.player);
    }
    
    public int getDimension() {
        return this.system.getReader().getDimension(this.player);
    }
    
    public P getPlayer() {
        return this.player;
    }
    
    public void setRenderedOnRadar(final boolean renderedOnRadar) {
        this.renderedOnRadar = renderedOnRadar;
    }
    
    public boolean wasRenderedOnRadar() {
        return this.renderedOnRadar;
    }
    
    public SlowingAnimation getFadeAnim() {
        return this.fadeAnim;
    }
    
    public void setFadeAnim(final SlowingAnimation fadeAnim) {
        this.fadeAnim = fadeAnim;
    }
    
    public IPlayerTrackerSystem<P> getSystem() {
        return this.system;
    }
}
