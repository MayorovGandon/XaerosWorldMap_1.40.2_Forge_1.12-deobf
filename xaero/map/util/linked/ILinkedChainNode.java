//Decompiled by Procyon!

package xaero.map.util.linked;

public interface ILinkedChainNode<V extends ILinkedChainNode<V>>
{
    void setNext(final V p0);
    
    void setPrevious(final V p0);
    
    V getNext();
    
    V getPrevious();
    
    boolean isDestroyed();
    
    void onDestroyed();
}
