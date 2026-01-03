//Decompiled by Procyon!

package xaero.map.misc;

import java.nio.*;

public class BufferCompatibilityFix
{
    public static Buffer clear(final Buffer buffer) {
        return buffer.clear();
    }
    
    public static Buffer flip(final Buffer buffer) {
        return buffer.flip();
    }
    
    public static Buffer position(final Buffer buffer, final int pos) {
        return buffer.position(pos);
    }
    
    public static Buffer limit(final Buffer buffer, final int limit) {
        return buffer.limit(limit);
    }
    
    public static Buffer mark(final Buffer buffer) {
        return buffer.mark();
    }
    
    public static Buffer reset(final Buffer buffer) {
        return buffer.reset();
    }
    
    public static Buffer rewind(final Buffer buffer) {
        return buffer.rewind();
    }
}
