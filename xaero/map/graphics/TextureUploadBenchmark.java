//Decompiled by Procyon!

package xaero.map.graphics;

import xaero.map.misc.*;
import org.lwjgl.opengl.*;
import java.util.*;

public class TextureUploadBenchmark
{
    private long[] accumulators;
    private long[] results;
    private int[] totals;
    private boolean[] finished;
    private int[] nOfElements;
    private int nOfFinished;
    private boolean allFinished;
    
    public TextureUploadBenchmark(final int... nOfElements) {
        final int nOfTypes = nOfElements.length;
        this.accumulators = new long[nOfTypes];
        this.totals = new int[nOfTypes];
        this.results = new long[nOfTypes];
        this.finished = new boolean[nOfTypes];
        this.nOfElements = nOfElements;
    }
    
    public void pre() {
        Misc.timerPre();
    }
    
    public void post(final int type) {
        GL11.glFinish();
        final int passed = Misc.timerResult();
        final long[] accumulators = this.accumulators;
        accumulators[type] += passed;
        final int[] totals = this.totals;
        ++totals[type];
        if (this.totals[type] == this.nOfElements[type]) {
            this.finish(type);
        }
    }
    
    private void finish(final int type) {
        this.results[type] = this.accumulators[type] / this.totals[type];
        this.finished[type] = true;
        ++this.nOfFinished;
        if (this.nOfFinished == this.finished.length) {
            this.allFinished = true;
        }
    }
    
    public boolean isFinished() {
        return this.allFinished;
    }
    
    public boolean isFinished(final int type) {
        return this.finished[type];
    }
    
    public long getAverage(final int type) {
        if (this.finished[type]) {
            return this.results[type];
        }
        return this.accumulators[type] / this.totals[type];
    }
    
    public String getTotalsString() {
        return Arrays.toString(this.totals);
    }
}
