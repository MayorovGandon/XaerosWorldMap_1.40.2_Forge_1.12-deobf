//Decompiled by Procyon!

package xaero.map.core.transformer;

import java.util.*;
import org.objectweb.asm.tree.*;

public class PlayerListTransformer extends ClassNodeTransformer
{
    public byte[] transform(final String name, final String transformedName, final byte[] basicClass) {
        this.className = "net.minecraft.server.management.PlayerList";
        return super.transform(name, transformedName, basicClass);
    }
    
    protected void transformNode(final ClassNode classNode, final boolean isObfuscated) {
        final String methodNameWorldInfo = isObfuscated ? "b" : "updateTimeAndWeatherForPlayer";
        final String methodDescWorldInfo = isObfuscated ? "(Loq;Loo;)V" : "(Lnet/minecraft/entity/player/EntityPlayerMP;Lnet/minecraft/world/WorldServer;)V";
        for (final MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(methodNameWorldInfo) && methodNode.desc.equals(methodDescWorldInfo)) {
                this.insertWorldInfoCall(methodNode);
                break;
            }
        }
    }
    
    private void insertWorldInfoCall(final MethodNode methodNode) {
        final InsnList patchList = new InsnList();
        patchList.add((AbstractInsnNode)new VarInsnNode(25, 1));
        patchList.add((AbstractInsnNode)new MethodInsnNode(184, "xaero/map/server/core/XaeroWorldMapServerCore", "onServerWorldInfo", "(Lnet/minecraft/entity/player/EntityPlayer;)V", false));
        final AbstractInsnNode firstInsn = methodNode.instructions.get(0);
        if (firstInsn instanceof LabelNode) {
            methodNode.instructions.insert(firstInsn, patchList);
        }
        else {
            methodNode.instructions.insertBefore(firstInsn, patchList);
        }
    }
}
