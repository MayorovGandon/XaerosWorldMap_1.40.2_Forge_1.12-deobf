//Decompiled by Procyon!

package xaero.map.core;

import net.minecraftforge.fml.relauncher.*;
import java.util.*;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.TransformerExclusions({ "xaero.map.core.transformer", "xaero.map.core" })
public class XaeroWorldMapPlugin implements IFMLLoadingPlugin
{
    public String[] getASMTransformerClass() {
        return new String[] { "xaero.map.core.transformer.ChunkTransformer", "xaero.map.core.transformer.NetHandlerPlayClientTransformer", "xaero.map.core.transformer.EntityPlayerTransformer", "xaero.map.core.transformer.AbstractClientPlayerTransformer", "xaero.map.core.transformer.WorldClientTransformer", "xaero.map.core.transformer.EntityPlayerMPTransformer", "xaero.map.core.transformer.PlayerListTransformer", "xaero.map.core.transformer.SaveFormatTransformer", "xaero.map.core.transformer.BiomeColorHelperTransformer", "xaero.map.core.transformer.MinecraftServerTransformer", "xaero.map.core.transformer.MinecraftTransformer" };
    }
    
    public String getModContainerClass() {
        return "xaero.map.core.CoreModContainer";
    }
    
    public String getSetupClass() {
        return null;
    }
    
    public void injectData(final Map<String, Object> data) {
    }
    
    public String getAccessTransformerClass() {
        return null;
    }
}
