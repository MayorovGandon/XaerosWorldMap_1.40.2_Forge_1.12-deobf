//Decompiled by Procyon!

package xaero.map.palette;

import it.unimi.dsi.fastutil.objects.*;
import java.util.*;

public final class FastPalette<T>
{
    private final Object2IntMap<T> indexHelper;
    private final List<Element<T>> elements;
    private final int maxCountPerElement;
    
    private FastPalette(final Object2IntMap<T> indexHelper, final List<Element<T>> elements, final int maxCountPerElement) {
        this.indexHelper = indexHelper;
        this.elements = elements;
        this.maxCountPerElement = maxCountPerElement;
    }
    
    public synchronized T get(final int index) {
        if (index < 0 || index >= this.elements.size()) {
            return null;
        }
        final Element<T> element = this.elements.get(index);
        if (element == null) {
            return null;
        }
        return (T)((Element<Object>)element).getObject();
    }
    
    public synchronized int add(final T elementObject) {
        final int existing = (int)this.indexHelper.getOrDefault((Object)elementObject, (Object)(-1));
        if (existing != -1) {
            return existing;
        }
        int newIndex = this.elements.size();
        boolean add = true;
        for (int i = 0; i < this.elements.size(); ++i) {
            if (this.elements.get(i) == null) {
                newIndex = i;
                add = false;
                break;
            }
        }
        this.indexHelper.put((Object)elementObject, newIndex);
        final Element<T> element = new Element<T>((Object)elementObject);
        if (add) {
            this.elements.add(element);
        }
        else {
            this.elements.set(newIndex, element);
        }
        return newIndex;
    }
    
    public synchronized int add(final T elementObject, final int count) {
        if (count < 0 || count > this.maxCountPerElement) {
            throw new IllegalArgumentException("illegal count!");
        }
        final int index = this.add(elementObject);
        ((Element<Object>)this.elements.get(index)).count = (short)count;
        return index;
    }
    
    public synchronized int append(final T elementObject, final int count) {
        if (count < 0 || count > this.maxCountPerElement) {
            throw new IllegalArgumentException("illegal count!");
        }
        final int existing = (int)this.indexHelper.getOrDefault((Object)elementObject, (Object)(-1));
        if (existing != -1) {
            throw new IllegalArgumentException("duplicate palette element!");
        }
        final int newIndex = this.elements.size();
        this.indexHelper.put((Object)elementObject, newIndex);
        final Element<T> element = new Element<T>((Object)elementObject);
        ((Element<Object>)element).count = (short)count;
        this.elements.add(element);
        return newIndex;
    }
    
    public synchronized int getIndex(final T elementObject) {
        return (int)this.indexHelper.getOrDefault((Object)elementObject, (Object)(-1));
    }
    
    public synchronized int count(final int index, final boolean up) {
        final Element<T> element = this.elements.get(index);
        ((Element<Object>)element).count(up, this.maxCountPerElement);
        return ((Element<Object>)element).getCount();
    }
    
    public synchronized int getCount(final int index) {
        final Element<T> element = this.elements.get(index);
        return ((Element<Object>)element).getCount();
    }
    
    public synchronized void remove(final int index) {
        final Element<T> previous = this.elements.set(index, null);
        if (previous != null) {
            this.indexHelper.removeInt(((Element<Object>)previous).getObject());
        }
        if (index == this.elements.size() - 1) {
            while (!this.elements.isEmpty() && this.elements.get(this.elements.size() - 1) == null) {
                this.elements.remove(this.elements.size() - 1);
            }
        }
    }
    
    public synchronized boolean replace(final T elementObject, final T newObject) {
        final int index = (int)this.indexHelper.getOrDefault((Object)elementObject, (Object)(-1));
        return index != -1 && this.replace(index, newObject);
    }
    
    public synchronized boolean replace(final int index, final T newObject) {
        final Element<T> element = this.elements.get(index);
        final T elementObject = (T)((Element<Object>)element).getObject();
        ((Element<Object>)element).setObject(newObject);
        this.indexHelper.removeInt((Object)elementObject);
        this.indexHelper.put((Object)newObject, index);
        return true;
    }
    
    public synchronized void addNull() {
        this.elements.add(null);
    }
    
    public int getSize() {
        return this.elements.size();
    }
    
    public int getNonNullCount() {
        return this.indexHelper.size();
    }
    
    public static final class Builder<T>
    {
        private int maxCountPerElement;
        
        private Builder() {
        }
        
        public Builder<T> setDefault() {
            this.setMaxCountPerElement(0);
            return this;
        }
        
        public Builder<T> setMaxCountPerElement(final int maxCountPerElement) {
            this.maxCountPerElement = maxCountPerElement;
            return this;
        }
        
        public FastPalette<T> build() {
            if (this.maxCountPerElement == 0) {
                throw new IllegalStateException();
            }
            if (this.maxCountPerElement > 65535) {
                throw new IllegalStateException("the max count must be within 0 - 65535");
            }
            final Object2IntMap<T> indexHelper = (Object2IntMap<T>)new Object2IntOpenHashMap();
            final List<Element<T>> elements = new ArrayList<Element<T>>();
            return new FastPalette<T>(indexHelper, elements, this.maxCountPerElement, null);
        }
        
        public static <T> Builder<T> begin() {
            return new Builder<T>().setDefault();
        }
    }
    
    private static class Element<T>
    {
        private T object;
        private short count;
        
        private Element(final T elementObject) {
            this.object = elementObject;
        }
        
        private void setObject(final T elementObject) {
            this.object = elementObject;
        }
        
        private T getObject() {
            return this.object;
        }
        
        private int getCount() {
            return this.count & 0xFFFF;
        }
        
        private void count(final boolean up, final int maxCount) {
            this.count += (short)(up ? 1 : -1);
        }
    }
}
