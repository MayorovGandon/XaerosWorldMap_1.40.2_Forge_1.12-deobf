//Decompiled by Procyon!

package xaero.map.pool.buffer;

import xaero.map.pool.*;
import xaero.map.misc.*;
import java.nio.*;
import org.lwjgl.*;

public class PoolTextureDirectBufferUnit implements PoolUnit
{
    private ByteBuffer directBuffer;
    
    public PoolTextureDirectBufferUnit(final Object... args) {
        this.create(args);
    }
    
    public ByteBuffer getDirectBuffer() {
        return this.directBuffer;
    }
    
    public void reset() {
        BufferCompatibilityFix.clear((Buffer)this.directBuffer);
        BufferUtils.zeroBuffer(this.directBuffer);
    }
    
    @Override
    public void create(final Object... args) {
        if (this.directBuffer == null) {
            this.directBuffer = createBuffer();
        }
        else {
            BufferCompatibilityFix.clear((Buffer)this.directBuffer);
            if (args[0]) {
                BufferUtils.zeroBuffer(this.directBuffer);
            }
        }
    }
    
    public static ByteBuffer createBuffer() {
        return BufferUtils.createByteBuffer(16384);
    }
}
