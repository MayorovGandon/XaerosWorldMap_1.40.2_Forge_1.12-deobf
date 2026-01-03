//Decompiled by Procyon!

package xaero.map.util.linked;

import java.util.*;
import javax.annotation.*;
import java.util.stream.*;
import com.google.common.collect.*;

public class LinkedChain<V extends ILinkedChainNode<V>> implements Iterable<V>
{
    private boolean destroyed;
    private V head;
    
    public void add(final V element) {
        if (this.destroyed) {
            throw new RuntimeException(new IllegalAccessException("Trying to use a destroyed chain!"));
        }
        if (element.isDestroyed()) {
            throw new IllegalArgumentException("Trying to reintroduce a removed chain element!");
        }
        if (this.head != null) {
            element.setNext((ILinkedChainNode)this.head);
            this.head.setPrevious((ILinkedChainNode)element);
        }
        this.head = element;
    }
    
    public void remove(final V element) {
        if (this.destroyed) {
            throw new RuntimeException(new IllegalAccessException("Trying to use a cleared chain!"));
        }
        if (element.isDestroyed()) {
            return;
        }
        final V prev = (V)element.getPrevious();
        final V next = (V)element.getNext();
        if (prev != null) {
            prev.setNext((ILinkedChainNode)next);
        }
        if (next != null) {
            next.setPrevious((ILinkedChainNode)prev);
        }
        if (element == this.head) {
            this.head = next;
        }
        element.onDestroyed();
    }
    
    public void destroy() {
        this.head = null;
        this.destroyed = true;
    }
    
    public void reset() {
        this.head = null;
        this.destroyed = false;
    }
    
    @Nonnull
    @Override
    public Iterator<V> iterator() {
        return new Iterator<V>() {
            private V next = LinkedChain.this.head;
            
            private V reachValidNext() {
                if (LinkedChain.this.destroyed) {
                    return this.next = null;
                }
                while (this.next != null && this.next.isDestroyed()) {
                    this.next = (V)this.next.getNext();
                }
                return this.next;
            }
            
            @Override
            public boolean hasNext() {
                return this.reachValidNext() != null;
            }
            
            @Nullable
            @Override
            public V next() {
                final V result = this.reachValidNext();
                if (result != null) {
                    this.next = (V)result.getNext();
                }
                return result;
            }
        };
    }
    
    @Nonnull
    public Stream<V> stream() {
        return (Stream<V>)Streams.stream((Iterable)this);
    }
}
