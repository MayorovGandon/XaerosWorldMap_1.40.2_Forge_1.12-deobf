//Decompiled by Procyon!

package xaero.map.core.transformer;

import org.objectweb.asm.tree.*;
import java.util.*;

public class NetHandlerPlayClientTransformer extends ClassNodeTransformer
{
    public byte[] transform(final String name, final String transformedName, final byte[] basicClass) {
        this.className = "net.minecraft.client.network.NetHandlerPlayClient";
        return super.transform(name, transformedName, basicClass);
    }
    
    protected void transformNode(final ClassNode classNode, final boolean isObfuscated) {
        final String methodNameHandleMultiBlockChange = isObfuscated ? "a" : "handleMultiBlockChange";
        final String methodNameHandleChunkData = isObfuscated ? "a" : "handleChunkData";
        final String methodNameHandleBlockChange = isObfuscated ? "a" : "handleBlockChange";
        final String methodNameHandleJoinGame = isObfuscated ? "a" : "handleJoinGame";
        final String methodDescHandleMultiBlockChange = isObfuscated ? "(Lio;)V" : "(Lnet/minecraft/network/play/server/SPacketMultiBlockChange;)V";
        final String methodDescHandleChunkData = isObfuscated ? "(Lje;)V" : "(Lnet/minecraft/network/play/server/SPacketChunkData;)V";
        final String methodDescHandleBlockChange = isObfuscated ? "(Lij;)V" : "(Lnet/minecraft/network/play/server/SPacketBlockChange;)V";
        final String methodDescHandleJoinGame = isObfuscated ? "(Ljh;)V" : "(Lnet/minecraft/network/play/server/SPacketJoinGame;)V";
        final String methodNameCleanup = isObfuscated ? "b" : "cleanup";
        boolean multiBlockChangeRedirected = false;
        boolean chunkDataRedirected = false;
        boolean blockChangeRedirected = false;
        boolean joinGameTransformed = false;
        boolean cleanupTransformed = false;
        for (final MethodNode mn : classNode.methods) {
            if (mn.name.equals(methodNameHandleMultiBlockChange) && mn.desc.equals(methodDescHandleMultiBlockChange)) {
                this.clientPacketRedirectTransform(mn, new MethodInsnNode(184, "xaero/map/core/XaeroWorldMapCore", "onMultiBlockChange", methodDescHandleMultiBlockChange, false), isObfuscated);
                multiBlockChangeRedirected = true;
            }
            else if (mn.name.equals(methodNameHandleChunkData) && mn.desc.equals(methodDescHandleChunkData)) {
                this.clientPacketRedirectTransform(mn, new MethodInsnNode(184, "xaero/map/core/XaeroWorldMapCore", "onChunkData", methodDescHandleChunkData, false), isObfuscated);
                chunkDataRedirected = true;
            }
            else if (mn.name.equals(methodNameHandleBlockChange) && mn.desc.equals(methodDescHandleBlockChange)) {
                this.clientPacketRedirectTransform(mn, new MethodInsnNode(184, "xaero/map/core/XaeroWorldMapCore", "onBlockChange", methodDescHandleBlockChange, false), isObfuscated);
                blockChangeRedirected = true;
            }
            else if (mn.name.equals(methodNameHandleJoinGame) && mn.desc.equals(methodDescHandleJoinGame)) {
                this.clientPacketRedirectTransformCustomDouble(mn, new MethodInsnNode(184, "xaero/map/core/XaeroWorldMapCore", "onPlayNetHandler", "(Lnet/minecraft/client/network/NetHandlerPlayClient;Lnet/minecraft/network/play/server/SPacketJoinGame;)V", false), isObfuscated, 0, 1);
                joinGameTransformed = true;
            }
            else if (mn.name.equals(methodNameCleanup) && mn.desc.equals("()V")) {
                final InsnList patchList = new InsnList();
                patchList.add((AbstractInsnNode)new VarInsnNode(25, 0));
                patchList.add((AbstractInsnNode)new MethodInsnNode(184, "xaero/map/core/XaeroWorldMapCore", "onPlayNetHandlerCleanup", "(Lnet/minecraft/client/network/NetHandlerPlayClient;)V", false));
                final AbstractInsnNode firstInsn = mn.instructions.get(0);
                if (firstInsn instanceof LabelNode) {
                    mn.instructions.insert(firstInsn, patchList);
                }
                else {
                    mn.instructions.insertBefore(firstInsn, patchList);
                }
                cleanupTransformed = true;
            }
            if (multiBlockChangeRedirected && chunkDataRedirected && blockChangeRedirected && joinGameTransformed && cleanupTransformed) {
                break;
            }
        }
        classNode.interfaces.add("xaero/map/core/IWorldMapClientPlayNetHandler");
        classNode.fields.add(new FieldNode(2, "xaero_worldmapSession", "Lxaero/map/WorldMapSession;", (String)null, (Object)null));
        this.addGetter(classNode, "xaero_worldmapSession", "Lxaero/map/WorldMapSession;");
        this.addSetter(classNode, "xaero_worldmapSession", "Lxaero/map/WorldMapSession;");
    }
    
    private void clientPacketRedirectTransform(final MethodNode methodNode, final MethodInsnNode methodInsnNode, final boolean isObfuscated) {
        this.clientPacketRedirectTransformCustom(methodNode, methodInsnNode, isObfuscated, 1);
    }
    
    private void clientPacketRedirectTransformCustom(final MethodNode methodNode, final MethodInsnNode methodInsnNode, final boolean isObfuscated, final int localVariable) {
        final InsnList patchList = new InsnList();
        patchList.add((AbstractInsnNode)new VarInsnNode(25, localVariable));
        patchList.add((AbstractInsnNode)methodInsnNode);
        this.clientPacketRedirectTransformCustomPatch(methodNode, isObfuscated, patchList);
    }
    
    private void clientPacketRedirectTransformCustomDouble(final MethodNode methodNode, final MethodInsnNode methodInsnNode, final boolean isObfuscated, final int localVariable, final int localVariable2) {
        final InsnList patchList = new InsnList();
        patchList.add((AbstractInsnNode)new VarInsnNode(25, localVariable));
        patchList.add((AbstractInsnNode)new VarInsnNode(25, localVariable2));
        patchList.add((AbstractInsnNode)methodInsnNode);
        this.clientPacketRedirectTransformCustomPatch(methodNode, isObfuscated, patchList);
    }
    
    private void clientPacketRedirectTransformCustomPatch(final MethodNode methodNode, final boolean isObfuscated, final InsnList patchList) {
        final InsnList instructions = methodNode.instructions;
        for (int i = 0; i < instructions.size(); ++i) {
            final AbstractInsnNode insn = instructions.get(i);
            if (insn.getOpcode() == 184) {
                final MethodInsnNode methodInsn = (MethodInsnNode)insn;
                if (isObfuscated) {
                    if (!methodInsn.owner.equals("hv") || !methodInsn.name.equals("a")) {
                        continue;
                    }
                }
                else if (!methodInsn.owner.equals("net/minecraft/network/PacketThreadUtil") || !methodInsn.name.equals("checkThreadAndEnqueue")) {
                    continue;
                }
                instructions.insert(insn, patchList);
                break;
            }
        }
    }
}
