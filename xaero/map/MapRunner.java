//Decompiled by Procyon!

package xaero.map;

import java.util.*;
import xaero.map.task.*;

public class MapRunner implements Runnable
{
    private boolean stopped;
    private ArrayList<MapRunnerTask> tasks;
    
    public MapRunner() {
        this.tasks = new ArrayList<MapRunnerTask>();
    }
    
    @Override
    public void run() {
        while (!this.stopped) {
            final WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
            if (worldmapSession != null && worldmapSession.isUsable()) {
                final MapProcessor mapProcessor = worldmapSession.getMapProcessor();
                mapProcessor.run(this);
            }
            else {
                this.doTasks(null);
            }
            try {
                Thread.sleep(100L);
            }
            catch (InterruptedException ex) {}
        }
    }
    
    public void doTasks(final MapProcessor mapProcessor) {
        while (!this.tasks.isEmpty()) {
            final MapRunnerTask task;
            synchronized (this.tasks) {
                if (this.tasks.isEmpty()) {
                    break;
                }
                task = this.tasks.remove(0);
            }
            task.run(mapProcessor);
        }
    }
    
    public void addTask(final MapRunnerTask task) {
        synchronized (this.tasks) {
            this.tasks.add(task);
        }
    }
    
    public void stop() {
        this.stopped = true;
    }
}
