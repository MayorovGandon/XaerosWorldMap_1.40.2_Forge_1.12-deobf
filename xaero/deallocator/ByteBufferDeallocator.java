//Decompiled by Procyon!

package xaero.deallocator;

import java.nio.*;
import java.lang.reflect.*;
import xaero.map.*;

public class ByteBufferDeallocator
{
    private boolean usingInvokeCleanerMethod;
    private final String directBufferClassName = "java.nio.DirectByteBuffer";
    private Object theUnsafe;
    private Method invokeCleanerMethod;
    private Method directBufferCleanerMethod;
    private Method cleanerCleanMethod;
    
    public ByteBufferDeallocator() throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalArgumentException, IllegalAccessException {
        try {
            final Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            final Field theUnsafeField = unsafeClass.getDeclaredField("theUnsafe");
            theUnsafeField.setAccessible(true);
            this.theUnsafe = theUnsafeField.get(null);
            theUnsafeField.setAccessible(false);
            this.invokeCleanerMethod = unsafeClass.getDeclaredMethod("invokeCleaner", ByteBuffer.class);
            this.usingInvokeCleanerMethod = true;
        }
        catch (NoSuchMethodException | NoSuchFieldException ex2) {
            final ReflectiveOperationException ex;
            final ReflectiveOperationException nse = ex;
            final Class<?> directByteBufferClass = Class.forName("java.nio.DirectByteBuffer");
            this.directBufferCleanerMethod = directByteBufferClass.getDeclaredMethod("cleaner", (Class<?>[])new Class[0]);
            final Class<?> cleanerClass = this.directBufferCleanerMethod.getReturnType();
            if (Runnable.class.isAssignableFrom(cleanerClass)) {
                this.cleanerCleanMethod = Runnable.class.getDeclaredMethod("run", (Class<?>[])new Class[0]);
            }
            else {
                this.cleanerCleanMethod = cleanerClass.getDeclaredMethod("clean", (Class<?>[])new Class[0]);
            }
        }
    }
    
    public synchronized void deallocate(final ByteBuffer buffer, final boolean debug) {
        if (buffer == null || !buffer.isDirect()) {
            return;
        }
        if (this.usingInvokeCleanerMethod) {
            try {
                this.invokeCleanerMethod.invoke(this.theUnsafe, buffer);
            }
            catch (IllegalAccessException e) {
                this.reportException(e);
            }
            catch (IllegalArgumentException e2) {
                this.reportException(e2);
            }
            catch (InvocationTargetException e3) {
                this.reportException(e3);
            }
        }
        else {
            final boolean cleanerAccessibleBU = this.directBufferCleanerMethod.isAccessible();
            final boolean cleanAccessibleBU = this.cleanerCleanMethod.isAccessible();
            try {
                this.directBufferCleanerMethod.setAccessible(true);
                final Object cleaner = this.directBufferCleanerMethod.invoke(buffer, new Object[0]);
                if (cleaner != null) {
                    this.cleanerCleanMethod.setAccessible(true);
                    this.cleanerCleanMethod.invoke(cleaner, new Object[0]);
                }
                else if (debug) {
                    WorldMap.LOGGER.info("No cleaner to deallocate a buffer!");
                }
            }
            catch (IllegalAccessException e4) {
                this.reportException(e4);
            }
            catch (IllegalArgumentException e5) {
                this.reportException(e5);
            }
            catch (InvocationTargetException e6) {
                this.reportException(e6);
            }
            this.directBufferCleanerMethod.setAccessible(cleanerAccessibleBU);
            this.cleanerCleanMethod.setAccessible(cleanAccessibleBU);
        }
    }
    
    private void reportException(final Exception e) {
        WorldMap.LOGGER.error("Failed to deallocate a direct byte buffer: ", (Throwable)e);
    }
}
