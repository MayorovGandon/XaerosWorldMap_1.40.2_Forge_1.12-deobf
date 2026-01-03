//Decompiled by Procyon!

package xaero.map.pool.buffer;

import xaero.map.pool.*;

public class TextureDirectBufferPool extends MapPool<PoolTextureDirectBufferUnit>
{
    public TextureDirectBufferPool() {
        super(4096);
    }
    
    @Override
    protected PoolTextureDirectBufferUnit construct(final Object... args) {
        return new PoolTextureDirectBufferUnit(args);
    }
    
    public PoolTextureDirectBufferUnit get(final boolean zeroFillIfReused) {
        return super.get(zeroFillIfReused);
    }
}
