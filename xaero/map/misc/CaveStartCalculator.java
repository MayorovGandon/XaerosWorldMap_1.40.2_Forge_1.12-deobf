//Decompiled by Procyon!

package xaero.map.misc;

import net.minecraft.block.state.*;
import java.util.function.*;
import xaero.map.*;
import xaero.map.common.config.option.*;
import xaero.lib.common.config.option.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraft.world.chunk.*;
import net.minecraft.block.material.*;
import net.minecraft.block.*;
import net.minecraft.init.*;
import xaero.lib.client.config.*;
import net.minecraft.world.chunk.storage.*;

public class CaveStartCalculator
{
    private final BlockPos.MutableBlockPos mutableBlockPos;
    private final CachedFunction<IBlockState, Boolean> transparentCache;
    private final MapWriter mapWriter;
    
    public CaveStartCalculator(final MapWriter mapWriter) {
        this.mutableBlockPos = new BlockPos.MutableBlockPos();
        this.mapWriter = mapWriter;
        this.transparentCache = (CachedFunction<IBlockState, Boolean>)new CachedFunction((Function)new Function<IBlockState, Boolean>() {
            @Override
            public Boolean apply(final IBlockState state) {
                return mapWriter.shouldOverlay(state);
            }
        });
    }
    
    public int getCaving(final double playerX, final double playerY, final double playerZ, final World world) {
        final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        final int autoCaveModeConfig = (int)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.AUTO_CAVE_MODE);
        if (autoCaveModeConfig == 0) {
            return Integer.MAX_VALUE;
        }
        final int y = Math.max((int)playerY + 1, 0);
        final int defaultCaveStart = y + 3;
        final int defaultResult = Integer.MAX_VALUE;
        if (y > 255 || y < 0) {
            return defaultResult;
        }
        final int x = MathHelper.func_76128_c(playerX);
        final int z = MathHelper.func_76128_c(playerZ);
        final int roofRadius = (autoCaveModeConfig < 0) ? 1 : (autoCaveModeConfig - 1);
        final int roofDiameter = 1 + roofRadius * 2;
        final int startX = x - roofRadius;
        final int startZ = z - roofRadius;
        final boolean ignoringHeightmaps = this.mapWriter.getMapProcessor().getMapWorld().isIgnoreHeightmaps();
        int bottom = y;
        int top = -1;
        Chunk prevBChunk = null;
        int potentialResult = defaultCaveStart;
        for (int o = 0; o < roofDiameter; ++o) {
            int p = 0;
        Label_0163:
            while (p < roofDiameter) {
                final int currentX = startX + o;
                final int currentZ = startZ + p;
                this.mutableBlockPos.func_181079_c(currentX, y, currentZ);
                final Chunk bchunk = world.func_72964_e(currentX >> 4, currentZ >> 4);
                if (bchunk == null) {
                    return defaultResult;
                }
                final int skyLight = bchunk.func_177413_a(EnumSkyBlock.SKY, (BlockPos)this.mutableBlockPos);
                if (!ignoringHeightmaps) {
                    if (skyLight >= 15) {
                        return defaultResult;
                    }
                    final int insideX = currentX & 0xF;
                    final int insideZ = currentZ & 0xF;
                    top = bchunk.func_76611_b(insideX, insideZ);
                }
                else if (bchunk != prevBChunk) {
                    final ExtendedBlockStorage[] sections = bchunk.func_76587_i();
                    if (sections.length == 0) {
                        return defaultResult;
                    }
                    final int playerSection = y >> 4;
                    boolean foundSomething = false;
                    for (int i = playerSection; i < sections.length; ++i) {
                        final ExtendedBlockStorage searchedSection = sections[i];
                        if (searchedSection != Chunk.field_186036_a) {
                            if (!foundSomething) {
                                bottom = Math.max(bottom, i << 4);
                                foundSomething = true;
                            }
                            top = (i << 4) + 15;
                        }
                    }
                    if (!foundSomething) {
                        return defaultResult;
                    }
                    prevBChunk = bchunk;
                }
                if (top < 0) {
                    return defaultResult;
                }
                if (top > 255) {
                    top = 255;
                }
                for (int j = bottom; j <= top; ++j) {
                    this.mutableBlockPos.func_185336_p(j);
                    final IBlockState state = world.func_180495_p((BlockPos)this.mutableBlockPos);
                    if (!(state.func_177230_c() instanceof BlockAir) && state.func_185904_a().func_186274_m() != EnumPushReaction.DESTROY && state.func_185904_a() != Material.field_151584_j && !(state.func_177230_c() instanceof BlockLiquid) && !(boolean)this.transparentCache.apply((Object)state) && state.func_177230_c() != Blocks.field_180401_cv) {
                        if (o == p && o == roofRadius) {
                            potentialResult = Math.min(j, defaultCaveStart);
                        }
                        ++p;
                        continue Label_0163;
                    }
                }
                return defaultResult;
            }
        }
        return potentialResult;
    }
}
