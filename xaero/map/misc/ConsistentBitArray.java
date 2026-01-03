//Decompiled by Procyon!

package xaero.map.misc;

import java.io.*;

public class ConsistentBitArray
{
    private int insideALong;
    private int bitsPerEntry;
    private int entries;
    private long[] data;
    private int entryMask;
    
    public ConsistentBitArray(final int bitsPerEntry, final int entries) {
        this(bitsPerEntry, entries, null);
    }
    
    public ConsistentBitArray(final int bitsPerEntry, final int entries, final long[] data) {
        if (bitsPerEntry > 32) {
            throw new RuntimeException("Entry size too big for int! " + bitsPerEntry);
        }
        this.insideALong = 64 / bitsPerEntry;
        final int longs = (entries + this.insideALong - 1) / this.insideALong;
        if (data != null) {
            if (data.length != longs) {
                throw new RuntimeException("Incorrect data length: " + data.length + " VS " + longs);
            }
            this.data = data;
        }
        else {
            this.data = new long[longs];
        }
        this.bitsPerEntry = bitsPerEntry;
        this.entries = entries;
        this.entryMask = (1 << bitsPerEntry) - 1;
    }
    
    public int get(final int index) {
        if (index >= this.entries) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        final int longIndex = index / this.insideALong;
        final int insideIndex = index % this.insideALong;
        return (int)(this.data[longIndex] >> insideIndex * this.bitsPerEntry & (long)this.entryMask);
    }
    
    public void set(final int index, final int value) {
        if (index >= this.entries) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        final int longIndex = index / this.insideALong;
        final int insideIndex = index % this.insideALong;
        final long currentLong = this.data[longIndex];
        final int offset = insideIndex * this.bitsPerEntry;
        final long shiftedMask = (long)this.entryMask << offset;
        final long shiftedValue = (long)(value & this.entryMask) << offset;
        this.data[longIndex] = ((currentLong & ~shiftedMask) | shiftedValue);
    }
    
    public void write(final DataOutputStream output) throws IOException {
        for (int i = 0; i < this.data.length; ++i) {
            output.writeLong(this.data[i]);
        }
    }
    
    public long[] getData() {
        return this.data;
    }
    
    public void setData(final long[] data) {
        this.data = data;
    }
    
    public int getBitsPerEntry() {
        return this.bitsPerEntry;
    }
}
