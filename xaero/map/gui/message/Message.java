//Decompiled by Procyon!

package xaero.map.gui.message;

import net.minecraft.util.text.*;

public class Message
{
    private final ITextComponent text;
    private final long additionTime;
    
    public Message(final ITextComponent text, final long additionTime) {
        this.text = text;
        this.additionTime = additionTime;
    }
    
    public ITextComponent getText() {
        return this.text;
    }
    
    public long getAdditionTime() {
        return this.additionTime;
    }
}
