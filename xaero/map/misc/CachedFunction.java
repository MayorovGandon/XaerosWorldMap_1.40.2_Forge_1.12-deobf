//Decompiled by Procyon!

package xaero.map.misc;

import java.util.*;
import java.util.function.*;

public class CachedFunction<F, T>
{
    private final HashMap<F, T> cache;
    private F prevFrom;
    private T prevTo;
    private Function<F, T> function;
    
    public CachedFunction(final Function<F, T> function) {
        this.cache = new HashMap<F, T>();
        this.function = function;
    }
    
    public T apply(final F from) {
        if (this.prevFrom == from && from != null) {
            return this.prevTo;
        }
        T cached = this.cache.get(from);
        if (cached == null) {
            cached = this.function.apply(from);
            this.cache.put(from, cached);
        }
        this.prevFrom = from;
        return this.prevTo = cached;
    }
}
