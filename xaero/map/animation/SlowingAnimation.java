//Decompiled by Procyon!

package xaero.map.animation;

public class SlowingAnimation extends Animation
{
    public static final double animationThing = 16.666666666666668;
    private double dest;
    private double zero;
    private double factor;
    
    public SlowingAnimation(final double from, final double to, final double factor, final double zero) {
        super(from, to, 0L);
        this.dest = to;
        this.zero = zero;
        this.factor = factor;
    }
    
    public double getCurrent() {
        final double times = (System.currentTimeMillis() - this.start) / 16.666666666666668;
        final double currentOff = this.off * Math.pow(this.factor, times);
        return this.dest - ((Math.abs(currentOff) <= this.zero) ? 0.0 : currentOff);
    }
    
    public double getDestination() {
        return this.dest;
    }
}
