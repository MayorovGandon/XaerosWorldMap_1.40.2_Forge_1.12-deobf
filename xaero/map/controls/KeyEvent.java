//Decompiled by Procyon!

package xaero.map.controls;

import net.minecraft.client.settings.*;

public class KeyEvent
{
    private KeyBinding kb;
    private boolean tickEnd;
    private boolean isRepeat;
    private boolean keyDown;
    
    public KeyEvent(final KeyBinding kb, final boolean tickEnd, final boolean isRepeat, final boolean keyDown) {
        this.kb = kb;
        this.tickEnd = tickEnd;
        this.isRepeat = isRepeat;
        this.keyDown = keyDown;
    }
    
    public KeyBinding getKb() {
        return this.kb;
    }
    
    public boolean isTickEnd() {
        return this.tickEnd;
    }
    
    public boolean isRepeat() {
        return this.isRepeat;
    }
    
    public boolean isKeyDown() {
        return this.keyDown;
    }
}
