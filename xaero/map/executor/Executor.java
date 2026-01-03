//Decompiled by Procyon!

package xaero.map.executor;

import java.util.concurrent.*;

public class Executor
{
    private final ConcurrentLinkedDeque<Runnable> tasks;
    private final Thread thread;
    
    public Executor(final String name, final Thread thread) {
        this.thread = thread;
        this.tasks = new ConcurrentLinkedDeque<Runnable>();
    }
    
    protected Thread getExecutionThread() {
        return this.thread;
    }
    
    public boolean isOnExecutionThread() {
        return Thread.currentThread() == this.thread;
    }
    
    public CompletableFuture<?> enqueue(final Runnable task) {
        final CompletableFuture<?> future = new CompletableFuture<Object>();
        this.tasks.addLast(new Runnable() {
            @Override
            public void run() {
                try {
                    task.run();
                    future.complete(null);
                }
                catch (Throwable t) {
                    future.completeExceptionally(t);
                }
            }
        });
        return future;
    }
    
    public void drainTasks() {
        if (!this.isOnExecutionThread()) {
            throw new RuntimeException("wrong thread!");
        }
        while (!this.tasks.isEmpty()) {
            this.tasks.removeFirst().run();
        }
    }
}
