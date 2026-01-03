//Decompiled by Procyon!

package xaero.map.core.transformer;

import org.objectweb.asm.tree.*;
import java.util.*;

public class AbstractClientPlayerTransformer extends ClassNodeTransformer
{
    @Override
    public byte[] transform(final String name, final String transformedName, final byte[] basicClass) {
        this.className = "net.minecraft.client.entity.AbstractClientPlayer";
        return super.transform(name, transformedName, basicClass);
    }
    
    @Override
    protected void transformNode(final ClassNode classNode, final boolean isObfuscated) {
        final String methodName = isObfuscated ? "q" : "getLocationCape";
        final String methodDesc = isObfuscated ? "()Lnf;" : "()Lnet/minecraft/util/ResourceLocation;";
        for (final MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(methodName) && methodNode.desc.equals(methodDesc)) {
                break;
            }
        }
        System.out.println();
    }
}
