//Decompiled by Procyon!

package xaero.map.radar.tracker.system;

import java.util.*;

public interface ITrackedPlayerReader<P>
{
    UUID getId(final P p0);
    
    double getX(final P p0);
    
    double getY(final P p0);
    
    double getZ(final P p0);
    
    int getDimension(final P p0);
}
