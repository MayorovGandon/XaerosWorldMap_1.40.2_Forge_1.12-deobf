//Decompiled by Procyon!

package xaero.map.core.transformer;

import org.objectweb.asm.tree.*;
import java.util.*;

public class EntityPlayerTransformer extends ClassNodeTransformer
{
    public byte[] transform(final String name, final String transformedName, final byte[] basicClass) {
        this.className = "net.minecraft.entity.player.EntityPlayer";
        return super.transform(name, transformedName, basicClass);
    }
    
    protected void transformNode(final ClassNode classNode, final boolean isObfuscated) {
        final String methodName = isObfuscated ? "a" : "isWearing";
        final String methodDesc = isObfuscated ? "(Laee;)Z" : "(Lnet/minecraft/entity/player/EnumPlayerModelParts;)Z";
        for (final MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(methodName) && methodNode.desc.equals(methodDesc)) {
                break;
            }
        }
        System.out.println();
    }
}
