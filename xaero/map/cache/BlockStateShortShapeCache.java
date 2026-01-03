//Decompiled by Procyon!

package xaero.map.cache;

import net.minecraft.util.*;
import xaero.map.cache.placeholder.*;
import net.minecraft.client.*;
import xaero.map.*;
import java.util.concurrent.*;
import xaero.map.misc.*;
import net.minecraft.block.*;
import net.minecraft.world.*;
import net.minecraft.block.state.*;
import net.minecraft.util.math.*;

public class BlockStateShortShapeCache
{
    private IntHashMap<Boolean> shortBlockStates;
    private int lastShortChecked;
    private boolean lastShortCheckedResult;
    private PlaceholderBlockAccess placeholderBlockAccess;
    private WorldMapSession session;
    
    public BlockStateShortShapeCache(final WorldMapSession session) {
        this.lastShortChecked = -1;
        this.lastShortCheckedResult = false;
        this.session = session;
        this.shortBlockStates = (IntHashMap<Boolean>)new IntHashMap();
        this.placeholderBlockAccess = new PlaceholderBlockAccess();
    }
    
    public boolean isShort(final int state) {
        Boolean cached;
        synchronized (this.shortBlockStates) {
            if (state == this.lastShortChecked) {
                return this.lastShortCheckedResult;
            }
            cached = (Boolean)this.shortBlockStates.func_76041_a(state);
        }
        if (cached == null) {
            if (!Minecraft.func_71410_x().func_152345_ab()) {
                final CompletableFuture<Boolean> future = new CompletableFuture<Boolean>();
                final Runnable taskRunnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final Boolean value = BlockStateShortShapeCache.this.isShort(state);
                            future.complete(value);
                        }
                        catch (Throwable t) {
                            future.completeExceptionally(t);
                        }
                    }
                };
                final boolean isIOThread = Thread.currentThread() == WorldMap.mapRunnerThread;
                if (isIOThread) {
                    this.session.getMapProcessor().getRenderExecutor().enqueue(taskRunnable);
                }
                else {
                    taskRunnable.run();
                }
                Boolean result;
                try {
                    result = future.get();
                }
                catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                catch (ExecutionException e2) {
                    throw new RuntimeException(e2);
                }
                return result;
            }
            try {
                final IBlockState blockState = Misc.getStateById(state);
                if (blockState != null && !(blockState.func_177230_c() instanceof BlockAir) && !(blockState.func_177230_c() instanceof BlockLiquid)) {
                    this.placeholderBlockAccess.setPlaceholderState(blockState);
                    final AxisAlignedBB shape = blockState.func_185900_c((IBlockAccess)this.placeholderBlockAccess, BlockPos.field_177992_a);
                    cached = (shape.field_72337_e < 0.25);
                }
                else {
                    cached = false;
                }
            }
            catch (Throwable t) {
                WorldMap.LOGGER.info("(World Map) Defaulting world-dependent block state shape to not short: " + state);
                cached = false;
            }
            synchronized (this.shortBlockStates) {
                this.shortBlockStates.func_76038_a(state, (Object)cached);
                this.lastShortChecked = state;
                this.lastShortCheckedResult = cached;
            }
        }
        return cached;
    }
    
    public void reset() {
        synchronized (this.shortBlockStates) {
            this.shortBlockStates.func_76046_c();
            this.lastShortChecked = -1;
            this.lastShortCheckedResult = false;
        }
    }
}
