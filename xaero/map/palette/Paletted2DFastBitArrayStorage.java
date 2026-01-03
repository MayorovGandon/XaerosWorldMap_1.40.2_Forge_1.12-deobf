//Decompiled by Procyon!

package xaero.map.palette;

import xaero.map.misc.*;

public class Paletted2DFastBitArrayStorage<T>
{
    private final FastPalette<T> palette;
    private final int width;
    private final int height;
    private ConsistentBitArray data;
    private final T defaultValue;
    private int defaultValueCount;
    
    private Paletted2DFastBitArrayStorage(final FastPalette<T> palette, final T defaultValue, final int width, final int height, final int defaultValueCount, final ConsistentBitArray data) {
        this.palette = palette;
        this.defaultValue = defaultValue;
        this.width = width;
        this.height = height;
        this.data = data;
        this.defaultValueCount = defaultValueCount;
    }
    
    private void checkRange(final int x, final int y) {
        if (x < 0 || y < 0 || x >= this.width || y >= this.height) {
            throw new IllegalArgumentException("out of bounds! (x: " + x + "; y: " + y + ") (w: " + this.width + "; h: " + this.height + ")");
        }
    }
    
    private int getIndex(final int x, final int y) {
        return y * this.width + x;
    }
    
    public synchronized T get(final int x, final int y) {
        this.checkRange(x, y);
        final int index = this.getIndex(x, y);
        final int paletteIndex = this.data.get(index);
        if (paletteIndex == 0) {
            return this.defaultValue;
        }
        return (T)this.palette.get(paletteIndex - 1);
    }
    
    public synchronized void set(final int x, final int y, final T object) {
        this.checkRange(x, y);
        final int index = this.getIndex(x, y);
        final int currentPaletteIndex = this.data.get(index);
        int newPaletteIndex = 0;
        if (currentPaletteIndex > 0) {
            newPaletteIndex = this.palette.getIndex((Object)object) + 1;
            if (newPaletteIndex == currentPaletteIndex) {
                return;
            }
            final int replacedObjectCount = this.palette.count(currentPaletteIndex - 1, false);
            if (replacedObjectCount == 0) {
                this.palette.remove(currentPaletteIndex - 1);
            }
        }
        else {
            --this.defaultValueCount;
        }
        if (object != this.defaultValue) {
            if (newPaletteIndex == 0) {
                newPaletteIndex = this.palette.add((Object)object) + 1;
            }
            this.palette.count(newPaletteIndex - 1, true);
        }
        else {
            ++this.defaultValueCount;
        }
        this.data.set(index, newPaletteIndex);
    }
    
    public boolean contains(final T object) {
        return this.palette.getIndex((Object)object) != -1;
    }
    
    public int getPaletteSize() {
        return this.palette.getSize();
    }
    
    public int getPaletteNonNullCount() {
        return this.palette.getNonNullCount();
    }
    
    public T getPaletteElement(final int index) {
        return (T)this.palette.get(index);
    }
    
    public int getPaletteElementCount(final int index) {
        return this.palette.getCount(index);
    }
    
    public int getDefaultValueCount() {
        return this.defaultValueCount;
    }
    
    public static final class Builder<T>
    {
        private int width;
        private int height;
        private int maxPaletteElements;
        private T defaultValue;
        private FastPalette<T> palette;
        private ConsistentBitArray data;
        private int defaultValueCount;
        
        private Builder() {
        }
        
        public Builder<T> setDefault() {
            this.setWidth(0);
            this.setHeight(0);
            this.setDefaultValue(null);
            this.setMaxPaletteElements(0);
            this.setPalette(null);
            this.setData(null);
            this.setDefaultValueCount(Integer.MIN_VALUE);
            return this;
        }
        
        public Builder<T> setWidth(final int width) {
            this.width = width;
            return this;
        }
        
        public Builder<T> setHeight(final int height) {
            this.height = height;
            return this;
        }
        
        public Builder<T> setMaxPaletteElements(final int maxPaletteElements) {
            this.maxPaletteElements = maxPaletteElements;
            return this;
        }
        
        public Builder<T> setDefaultValue(final T defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }
        
        public Builder<T> setPalette(final FastPalette<T> palette) {
            this.palette = palette;
            return this;
        }
        
        public Builder<T> setData(final ConsistentBitArray data) {
            this.data = data;
            return this;
        }
        
        public Builder<T> setDefaultValueCount(final int defaultValueCount) {
            this.defaultValueCount = defaultValueCount;
            return this;
        }
        
        public Paletted2DFastBitArrayStorage<T> build() {
            if (this.width == 0 || this.height == 0 || this.maxPaletteElements == 0) {
                throw new IllegalStateException();
            }
            if (this.palette == null) {
                this.palette = (FastPalette<T>)FastPalette.Builder.begin().setMaxCountPerElement(this.width * this.height).build();
            }
            final int bitsPerEntry = (int)Math.ceil(Math.log(this.maxPaletteElements + 1) / Math.log(2.0));
            if (this.data == null) {
                this.data = new ConsistentBitArray(bitsPerEntry, this.width * this.height);
            }
            if (this.data.getBitsPerEntry() != bitsPerEntry) {
                throw new IllegalStateException();
            }
            if (this.defaultValueCount == Integer.MIN_VALUE) {
                this.defaultValueCount = this.width * this.height;
            }
            if (this.defaultValueCount < 0) {
                throw new IllegalStateException();
            }
            return new Paletted2DFastBitArrayStorage<T>(this.palette, this.defaultValue, this.width, this.height, this.defaultValueCount, this.data, null);
        }
        
        public static <T> Builder<T> begin() {
            return new Builder<T>().setDefault();
        }
    }
}
