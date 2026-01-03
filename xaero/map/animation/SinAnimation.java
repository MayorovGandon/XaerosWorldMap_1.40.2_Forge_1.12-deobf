//Decompiled by Procyon!

package xaero.map.animation;

public class SinAnimation extends Animation
{
    public SinAnimation(final double from, final double to, final long time) {
        super(from, to, time);
    }
    
    public double getCurrent() {
        final double passed = Math.min(1.0, (System.currentTimeMillis() - this.start) / (double)this.time);
        final double angle = 1.5707963267948966 * passed;
        return this.from + this.off * Math.sin(angle);
    }
}
