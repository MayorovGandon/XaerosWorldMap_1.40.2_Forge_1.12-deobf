//Decompiled by Procyon!

package xaero.map.gui;

import java.util.*;
import xaero.map.gui.dropdown.rightclick.*;

public interface IRightClickableElement
{
    ArrayList<RightClickOption> getRightClickOptions();
    
    boolean isRightClickValid();
    
    int getRightClickTitleBackgroundColor();
}
