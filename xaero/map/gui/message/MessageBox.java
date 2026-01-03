//Decompiled by Procyon!

package xaero.map.gui.message;

import xaero.lib.common.util.*;
import java.util.*;
import net.minecraft.util.text.*;

public class MessageBox
{
    private final List<Message> messages;
    private final int width;
    private final int capacity;
    
    private MessageBox(final List<Message> messages, final int width, final int capacity) {
        this.messages = messages;
        this.width = width;
        this.capacity = capacity;
    }
    
    private void addMessageLine(final ITextComponent text) {
        final Message msg = new Message(text, System.currentTimeMillis());
        this.messages.add(0, msg);
        if (this.messages.size() > this.capacity) {
            this.messages.remove(this.messages.size() - 1);
        }
    }
    
    public void addMessage(final ITextComponent text) {
        final List<ITextComponent> splitDest = new ArrayList<ITextComponent>();
        TextSplitter.splitTextIntoLines((List)splitDest, this.width, this.width, text, (StringBuilder)null);
        for (final ITextComponent line : splitDest) {
            this.addMessageLine(line);
        }
    }
    
    public void addMessageWithSource(final ITextComponent source, final ITextComponent text) {
        final ITextComponent fullText = (ITextComponent)new TextComponentString("<");
        fullText.func_150253_a().add(source);
        fullText.func_150253_a().add(new TextComponentString("> "));
        fullText.func_150253_a().add(text);
        this.addMessage(fullText);
    }
    
    public int getCapacity() {
        return this.capacity;
    }
    
    public Iterator<Message> getIterator() {
        return this.messages.iterator();
    }
    
    public static class Builder
    {
        private int width;
        private int capacity;
        
        private Builder() {
        }
        
        public Builder setDefault() {
            this.setWidth(250);
            this.setCapacity(5);
            return this;
        }
        
        public Builder setWidth(final int width) {
            this.width = width;
            return this;
        }
        
        public Builder setCapacity(final int capacity) {
            this.capacity = capacity;
            return this;
        }
        
        public MessageBox build() {
            return new MessageBox(new ArrayList(this.capacity), this.width, this.capacity, null);
        }
        
        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}
