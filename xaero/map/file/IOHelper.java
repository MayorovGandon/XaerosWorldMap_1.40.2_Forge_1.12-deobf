//Decompiled by Procyon!

package xaero.map.file;

import java.io.*;

public class IOHelper
{
    public static void readToBuffer(final byte[] buffer, final int count, final DataInputStream input) throws IOException {
        int readCount;
        for (int currentTotal = 0; currentTotal < count; currentTotal += readCount) {
            readCount = input.read(buffer, currentTotal, count - currentTotal);
            if (readCount == -1) {
                throw new EOFException();
            }
        }
    }
}
