//Decompiled by Procyon!

package xaero.map.file;

import java.io.*;

public interface MapRegionInfo
{
    boolean shouldCache();
    
    File getRegionFile();
    
    File getCacheFile();
    
    String getWorldId();
    
    String getDimId();
    
    String getMwId();
    
    int getRegionX();
    
    int getRegionZ();
    
    void setShouldCache(final boolean p0, final String p1);
    
    void setCacheFile(final File p0);
    
    boolean hasLookedForCache();
}
