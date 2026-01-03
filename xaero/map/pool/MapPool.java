//Decompiled by Procyon!

package xaero.map.pool;

import java.util.*;

public abstract class MapPool<T extends PoolUnit>
{
    private int maxSize;
    private List<T> units;
    
    public MapPool(final int maxSize) {
        this.maxSize = maxSize;
        this.units = new ArrayList<T>();
    }
    
    protected T get(final Object... args) {
        T unit = null;
        synchronized (this.units) {
            if (!this.units.isEmpty()) {
                unit = this.takeFromPool();
            }
        }
        if (unit == null) {
            return this.construct(args);
        }
        unit.create(args);
        return unit;
    }
    
    public boolean addToPool(final T unit) {
        synchronized (this.units) {
            if (this.units.size() < this.maxSize) {
                this.units.add(unit);
                return true;
            }
        }
        return false;
    }
    
    private T takeFromPool() {
        return this.units.remove(this.units.size() - 1);
    }
    
    public int size() {
        return this.units.size();
    }
    
    protected abstract T construct(final Object... p0);
}
