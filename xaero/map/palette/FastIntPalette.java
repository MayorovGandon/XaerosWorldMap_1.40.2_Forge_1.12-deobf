//Decompiled by Procyon!

package xaero.map.palette;

import it.unimi.dsi.fastutil.ints.*;
import java.util.*;

public final class FastIntPalette
{
    private final Int2IntMap indexHelper;
    private final List<Element> elements;
    private final int maxCountPerElement;
    
    private FastIntPalette(final Int2IntMap indexHelper, final List<Element> elements, final int maxCountPerElement) {
        this.indexHelper = indexHelper;
        this.elements = elements;
        this.maxCountPerElement = maxCountPerElement;
    }
    
    public synchronized int get(final int index, final int defaultValue) {
        if (index < 0 || index >= this.elements.size()) {
            return defaultValue;
        }
        final Element element = this.elements.get(index);
        if (element == null) {
            return defaultValue;
        }
        return element.getValue();
    }
    
    public synchronized int add(final int elementValue) {
        final int existing = (int)this.indexHelper.getOrDefault((Object)elementValue, (Object)(-1));
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
        this.indexHelper.put(elementValue, newIndex);
        final Element element = new Element(elementValue);
        if (add) {
            this.elements.add(element);
        }
        else {
            this.elements.set(newIndex, element);
        }
        return newIndex;
    }
    
    public synchronized int add(final int elementValue, final int count) {
        if (count < 0 || count > this.maxCountPerElement) {
            throw new IllegalArgumentException("illegal count!");
        }
        final int index = this.add(elementValue);
        this.elements.get(index).count = (short)count;
        return index;
    }
    
    public synchronized int append(final int elementValue, final int count) {
        if (count < 0 || count > this.maxCountPerElement) {
            throw new IllegalArgumentException("illegal count!");
        }
        final int existing = (int)this.indexHelper.getOrDefault((Object)elementValue, (Object)(-1));
        if (existing != -1) {
            throw new IllegalArgumentException("duplicate palette element!");
        }
        final int newIndex = this.elements.size();
        this.indexHelper.put(elementValue, newIndex);
        final Element element = new Element(elementValue);
        element.count = (short)count;
        this.elements.add(element);
        return newIndex;
    }
    
    public synchronized int getIndex(final int elementValue) {
        return (int)this.indexHelper.getOrDefault((Object)elementValue, (Object)(-1));
    }
    
    public synchronized int count(final int index, final boolean up) {
        final Element element = this.elements.get(index);
        element.count(up, this.maxCountPerElement);
        return element.getCount();
    }
    
    public synchronized int getCount(final int index) {
        final Element element = this.elements.get(index);
        return element.getCount();
    }
    
    public synchronized void remove(final int index) {
        final Element previous = this.elements.set(index, null);
        if (previous != null) {
            this.indexHelper.remove(previous.getValue());
        }
        if (index == this.elements.size() - 1) {
            while (!this.elements.isEmpty() && this.elements.get(this.elements.size() - 1) == null) {
                this.elements.remove(this.elements.size() - 1);
            }
        }
    }
    
    public synchronized boolean replace(final int elementValue, final int newValue) {
        final int index = (int)this.indexHelper.getOrDefault((Object)elementValue, (Object)(-1));
        return index != -1 && this.replaceAtIndex(index, newValue);
    }
    
    public synchronized boolean replaceAtIndex(final int index, final int newValue) {
        final Element element = this.elements.get(index);
        final int elementValue = element.getValue();
        element.setValue(newValue);
        this.indexHelper.remove(elementValue);
        this.indexHelper.put(newValue, index);
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
    
    public static final class Builder
    {
        private int maxCountPerElement;
        
        private Builder() {
        }
        
        public Builder setDefault() {
            this.setMaxCountPerElement(0);
            return this;
        }
        
        public Builder setMaxCountPerElement(final int maxCountPerElement) {
            this.maxCountPerElement = maxCountPerElement;
            return this;
        }
        
        public FastIntPalette build() {
            if (this.maxCountPerElement == 0) {
                throw new IllegalStateException();
            }
            if (this.maxCountPerElement > 65535) {
                throw new IllegalStateException("the max count must be within 0 - 65535");
            }
            final Int2IntMap indexHelper = (Int2IntMap)new Int2IntOpenHashMap();
            final List<Element> elements = new ArrayList<Element>();
            return new FastIntPalette(indexHelper, elements, this.maxCountPerElement, null);
        }
        
        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
    
    private static class Element
    {
        private int value;
        private short count;
        
        private Element(final int elementValue) {
            this.value = elementValue;
        }
        
        private void setValue(final int elementValue) {
            this.value = elementValue;
        }
        
        private int getValue() {
            return this.value;
        }
        
        private int getCount() {
            return this.count & 0xFFFF;
        }
        
        private void count(final boolean up, final int maxCount) {
            if ((up && this.count == maxCount) || (!up && this.count == 0)) {
                throw new IllegalStateException();
            }
            this.count += (short)(up ? 1 : -1);
        }
    }
}
