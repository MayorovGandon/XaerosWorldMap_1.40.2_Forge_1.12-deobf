//Decompiled by Procyon!

package xaero.map.gui;

import net.minecraftforge.fml.client.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import xaero.lib.client.gui.config.context.*;
import java.util.*;

public class ConfigGuiFactory implements IModGuiFactory
{
    public void initialize(final Minecraft minecraftInstance) {
    }
    
    public boolean hasConfigGui() {
        return true;
    }
    
    public GuiScreen createConfigGui(final GuiScreen parentScreen) {
        return (GuiScreen)new GuiWorldMapSettings(parentScreen, BuiltInEditConfigScreenContexts.CLIENT);
    }
    
    public Set<IModGuiFactory.RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }
}
