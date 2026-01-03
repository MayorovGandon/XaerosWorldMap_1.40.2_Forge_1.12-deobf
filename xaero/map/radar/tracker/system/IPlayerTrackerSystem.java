//Decompiled by Procyon!

package xaero.map.radar.tracker.system;

import java.util.*;

public interface IPlayerTrackerSystem<P>
{
    ITrackedPlayerReader<P> getReader();
    
    Iterator<P> getTrackedPlayerIterator();
}
