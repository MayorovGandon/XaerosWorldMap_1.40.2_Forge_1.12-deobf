//Decompiled by Procyon!

package xaero.map.profile;

import it.unimi.dsi.fastutil.objects.*;
import java.util.function.*;
import xaero.map.*;

public class SimpleProfiler
{
    private final Object2LongMap<String> sections;
    private String currentSection;
    private long previousNanoTime;
    
    public SimpleProfiler() {
        this.sections = (Object2LongMap<String>)new Object2LongOpenHashMap();
    }
    
    public void reset() {
        this.sections.clear();
        this.currentSection = null;
    }
    
    private void addTime(final String sectionName, final long time) {
        final long current = this.sections.getLong((Object)sectionName);
        this.sections.put((Object)sectionName, current + time);
    }
    
    public void section(final String sectionName) {
        final long currentTime = System.nanoTime();
        if (this.currentSection != null) {
            final long passed = currentTime - this.previousNanoTime;
            this.addTime(this.currentSection, passed);
        }
        this.previousNanoTime = currentTime;
        this.currentSection = sectionName;
    }
    
    public void end() {
        this.section(null);
    }
    
    public void debug() {
        this.sections.forEach((BiConsumer)new BiConsumer<String, Long>() {
            @Override
            public void accept(final String sectionName, final Long time) {
                WorldMap.LOGGER.info(sectionName + " : " + time + " (" + time / 100000L / 10.0 + " ms)");
            }
        });
    }
}
