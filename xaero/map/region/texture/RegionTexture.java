//Decompiled by Procyon!

package xaero.map.region.texture;

import xaero.map.pool.buffer.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.renderer.*;
import xaero.map.highlight.*;
import net.minecraft.client.gui.*;
import xaero.map.exception.*;
import xaero.map.graphics.*;
import xaero.map.pool.*;
import xaero.map.config.util.*;
import xaero.map.misc.*;
import java.nio.*;
import java.io.*;
import xaero.map.*;
import xaero.map.file.*;
import xaero.map.palette.*;
import java.util.*;
import xaero.map.biome.*;
import xaero.map.cache.*;
import xaero.map.region.*;

public abstract class RegionTexture<T extends RegionTexture<T>>
{
    public static final int PBO_UNPACK_LENGTH = 16384;
    public static final int PBO_PACK_LENGTH = 16384;
    private static final long[] ONE_BIOME_PALETTE_DATA;
    protected int textureVersion;
    protected int glColorTexture;
    protected boolean textureHasLight;
    protected PoolTextureDirectBufferUnit colorBuffer;
    protected boolean bufferHasLight;
    protected int colorBufferFormat;
    protected boolean colorBufferCompressed;
    protected int bufferedTextureVersion;
    protected int packPbo;
    protected int[] unpackPbo;
    protected boolean shouldDownloadFromPBO;
    protected int timer;
    private boolean cachePrepared;
    protected boolean toUpload;
    protected LeveledRegion<T> region;
    protected ConsistentBitArray heightValues;
    protected ConsistentBitArray topHeightValues;
    protected RegionTextureBiomes biomes;
    
    public RegionTexture(final LeveledRegion<T> region) {
        this.glColorTexture = -1;
        this.unpackPbo = new int[2];
        this.colorBufferFormat = -1;
        this.region = region;
        final int n = -1;
        this.textureVersion = n;
        this.bufferedTextureVersion = n;
        this.heightValues = new ConsistentBitArray(9, 4096);
        this.topHeightValues = new ConsistentBitArray(9, 4096);
    }
    
    private void setupTextureParameters() {
        GL11.glTexParameteri(3553, 33084, 0);
        GL11.glTexParameteri(3553, 33085, 0);
        GL11.glTexParameterf(3553, 33082, 0.0f);
        GL11.glTexParameterf(3553, 33083, 1.0f);
        GL11.glTexParameterf(3553, 34049, 0.0f);
        GL11.glTexParameteri(3553, 10240, 9728);
        GL11.glTexParameteri(3553, 10241, 9729);
        GL11.glTexParameteri(3553, 10242, 33071);
        GL11.glTexParameteri(3553, 10243, 33071);
    }
    
    public void prepareBuffer() {
        if (this.colorBuffer != null) {
            this.colorBuffer.reset();
        }
        else {
            this.colorBuffer = WorldMap.textureDirectBufferPool.get(true);
        }
    }
    
    @Deprecated
    public int bindColorTexture(final boolean create, final int magFilter) {
        final int texture = this.bindColorTexture(create);
        if (texture != -1) {
            GL11.glTexParameteri(3553, 10240, magFilter);
        }
        return texture;
    }
    
    public int bindColorTexture(final boolean create) {
        boolean result = false;
        int texture = this.glColorTexture;
        if (texture == -1) {
            if (!create) {
                return -1;
            }
            final int glGenTextures = GL11.glGenTextures();
            this.glColorTexture = glGenTextures;
            texture = glGenTextures;
            result = true;
        }
        GlStateManager.func_179144_i(texture);
        if (result) {
            this.setupTextureParameters();
        }
        return texture;
    }
    
    public long uploadBuffer(final DimensionHighlighterHandler highlighterHandler, final TextureUploader textureUploader, final LeveledRegion<T> inRegion, final BranchTextureRenderer branchTextureRenderer, final int x, final int y, final ScaledResolution scaledRes) throws OpenGLException, IllegalArgumentException, IllegalAccessException {
        final long result = this.uploadBufferHelper(highlighterHandler, textureUploader, inRegion, branchTextureRenderer, scaledRes);
        if (!this.shouldDownloadFromPBO()) {
            this.setToUpload(false);
            if (this.getColorBufferFormat() == -1) {
                this.deleteColorBuffer();
            }
            else {
                this.setCachePrepared(true);
            }
        }
        return result;
    }
    
    public void postBufferWrite(final PoolTextureDirectBufferUnit buffer) {
    }
    
    private long uploadBufferHelper(final DimensionHighlighterHandler highlighterHandler, final TextureUploader textureUploader, final LeveledRegion<T> inRegion, final BranchTextureRenderer branchTextureRenderer, final ScaledResolution scaledRes) throws OpenGLException, IllegalArgumentException, IllegalAccessException {
        return this.uploadBufferHelper(highlighterHandler, textureUploader, inRegion, branchTextureRenderer, scaledRes, false);
    }
    
    private long uploadBufferHelper(final DimensionHighlighterHandler highlighterHandler, final TextureUploader textureUploader, final LeveledRegion<T> inRegion, final BranchTextureRenderer branchTextureRenderer, final ScaledResolution scaledRes, final boolean retrying) throws OpenGLException, IllegalArgumentException, IllegalAccessException {
        if (this.colorBufferFormat != -1) {
            final boolean isCompressed = this.colorBufferCompressed;
            PoolTextureDirectBufferUnit colorBufferToUpload = this.colorBuffer;
            if (!isCompressed) {
                colorBufferToUpload = this.applyHighlights(highlighterHandler, colorBufferToUpload, true);
            }
            this.updateTextureVersion(this.bufferedTextureVersion);
            if (colorBufferToUpload == null) {
                return 0L;
            }
            final int length = colorBufferToUpload.getDirectBuffer().remaining();
            this.writeToUnpackPBO(0, colorBufferToUpload);
            final int internalFormat = this.colorBufferFormat;
            this.textureHasLight = this.bufferHasLight;
            this.colorBufferCompressed = false;
            this.colorBufferFormat = -1;
            this.bufferedTextureVersion = -1;
            final boolean subsequent = this.glColorTexture != -1;
            this.bindColorTexture(true);
            OpenGLException.checkGLError();
            long totalEstimatedTime = 0L;
            if (this.unpackPbo[0] == 0) {
                return 0L;
            }
            if (isCompressed) {
                totalEstimatedTime = textureUploader.requestCompressed(this.glColorTexture, this.unpackPbo[0], 3553, 0, internalFormat, 64, 64, 0, 0L, length);
            }
            else if (subsequent) {
                totalEstimatedTime = textureUploader.requestSubsequentNormal(this.glColorTexture, this.unpackPbo[0], 3553, 0, 64, 64, 0, 0L, 32993, 32821, 0, 0);
            }
            else {
                totalEstimatedTime = textureUploader.requestNormal(this.glColorTexture, this.unpackPbo[0], 3553, 0, internalFormat, 64, 64, 0, 0L, 32993, 32821);
            }
            this.onCacheUploadRequested();
            return totalEstimatedTime;
        }
        else {
            if (!this.shouldDownloadFromPBO) {
                return this.uploadNonCache(highlighterHandler, textureUploader, branchTextureRenderer, scaledRes);
            }
            final int glTexture = this.glColorTexture;
            GlStateManager.func_179144_i(glTexture);
            final int isCompressed2 = 0;
            int length;
            if (isCompressed2 == 1) {
                length = GL11.glGetTexLevelParameteri(3553, 0, 34464);
            }
            else {
                length = 16384;
            }
            OpenGLException.checkGLError();
            this.bindPackPBO();
            if (this.packPbo == 0) {
                this.onDownloadedBuffer(null, 0);
                this.endPBODownload(32856, false, false);
                return 0L;
            }
            final ByteBuffer mappedPBO = PixelBuffers.glMapBuffer(35051, 35000, (long)length, (ByteBuffer)null);
            if (mappedPBO != null) {
                OpenGLException.checkGLError();
                this.onDownloadedBuffer(mappedPBO, isCompressed2);
                PixelBuffers.glUnmapBuffer(35051);
                OpenGLException.checkGLError();
                this.unbindPackPBO();
                OpenGLException.checkGLError();
                final int format = GL11.glGetTexLevelParameteri(3553, 0, 4099);
                OpenGLException.checkGLError();
                this.endPBODownload(format, isCompressed2 == 1, true);
                return 0L;
            }
            this.unbindPackPBO();
            WorldMap.LOGGER.warn("Failed to map PBO {} {} (uploadBufferHelper).", (Object)this.packPbo, (Object)retrying);
            PixelBuffers.glDeleteBuffers(this.packPbo);
            int error;
            while ((error = GL11.glGetError()) != 0) {
                WorldMap.LOGGER.warn("OpenGL error (uploadBufferHelper): " + error);
            }
            this.packPbo = 0;
            if (retrying) {
                this.onDownloadedBuffer(null, 0);
                this.endPBODownload(32856, false, false);
                return 0L;
            }
            return this.uploadBufferHelper(highlighterHandler, textureUploader, inRegion, branchTextureRenderer, scaledRes, true);
        }
    }
    
    protected PoolTextureDirectBufferUnit applyHighlights(final DimensionHighlighterHandler highlighterHandler, final PoolTextureDirectBufferUnit colorBuffer, final boolean separateBuffer) {
        return colorBuffer;
    }
    
    protected abstract void onDownloadedBuffer(final ByteBuffer p0, final int p1);
    
    protected void endPBODownload(final int format, final boolean compressed, final boolean success) {
        this.bufferHasLight = this.textureHasLight;
        this.colorBufferFormat = format;
        this.colorBufferCompressed = compressed;
        this.shouldDownloadFromPBO = false;
        this.bufferedTextureVersion = this.textureVersion;
        if (format == -1) {
            throw new RuntimeException("Invalid texture internal format returned by the driver.");
        }
    }
    
    protected void bindPackPBO() {
        boolean created = false;
        if (this.packPbo == 0) {
            this.packPbo = PixelBuffers.glGenBuffers();
            created = (this.packPbo != 0);
        }
        PixelBuffers.glBindBuffer(35051, this.packPbo);
        if (created) {
            PixelBuffers.glBufferData(35051, 16384L, 35041);
            OpenGLException.checkGLError();
        }
    }
    
    private void bindUnpackPBO(final int index) {
        boolean created = false;
        if (this.unpackPbo[index] == 0) {
            this.unpackPbo[index] = PixelBuffers.glGenBuffers();
            created = (this.unpackPbo[index] != 0);
        }
        PixelBuffers.glBindBuffer(35052, this.unpackPbo[index]);
        if (created) {
            PixelBuffers.glBufferData(35052, 16384L, 35040);
            OpenGLException.checkGLError();
        }
    }
    
    protected void unbindPackPBO() {
        PixelBuffers.glBindBuffer(35051, 0);
    }
    
    private void unbindUnpackPBO() {
        PixelBuffers.glBindBuffer(35052, 0);
    }
    
    protected void writeToUnpackPBO(final int pboIndex, final PoolTextureDirectBufferUnit buffer) throws OpenGLException {
        this.writeToUnpackPBO(pboIndex, buffer, false);
    }
    
    private void writeToUnpackPBO(final int pboIndex, final PoolTextureDirectBufferUnit buffer, final boolean retrying) throws OpenGLException {
        this.bindUnpackPBO(pboIndex);
        if (this.unpackPbo[pboIndex] == 0) {
            this.postBufferWrite(buffer);
            return;
        }
        final ByteBuffer mappedPBO = PixelBuffers.glMapBuffer(35052, 35001, 16384L, (ByteBuffer)null);
        if (mappedPBO == null) {
            this.unbindUnpackPBO();
            WorldMap.LOGGER.warn("Failed to map PBO {} {} (writeToUnpackPBO).", (Object)this.unpackPbo[pboIndex], (Object)retrying);
            PixelBuffers.glDeleteBuffers(this.unpackPbo[pboIndex]);
            this.unpackPbo[pboIndex] = 0;
            int error;
            while ((error = GL11.glGetError()) != 0) {
                WorldMap.LOGGER.warn("OpenGL error (writeToUnpackPBO): " + error);
            }
            if (!retrying) {
                this.writeToUnpackPBO(pboIndex, buffer, true);
            }
            return;
        }
        OpenGLException.checkGLError();
        mappedPBO.put(buffer.getDirectBuffer());
        PixelBuffers.glUnmapBuffer(35052);
        this.unbindUnpackPBO();
        this.postBufferWrite(buffer);
    }
    
    public void deleteColorBuffer() {
        if (this.colorBuffer != null) {
            if (!WorldMap.textureDirectBufferPool.addToPool((PoolUnit)this.colorBuffer)) {
                WorldMap.bufferDeallocator.deallocate(this.colorBuffer.getDirectBuffer(), WorldMapClientConfigUtils.getDebug());
            }
            this.colorBuffer = null;
        }
        this.colorBufferFormat = -1;
        this.bufferedTextureVersion = -1;
    }
    
    public void deletePBOs() {
        if (this.packPbo > 0) {
            WorldMap.glObjectDeleter.requestBufferToDelete(this.packPbo);
        }
        this.packPbo = 0;
        for (int i = 0; i < this.unpackPbo.length; ++i) {
            if (this.unpackPbo[i] > 0) {
                WorldMap.glObjectDeleter.requestBufferToDelete(this.unpackPbo[i]);
                this.unpackPbo[i] = 0;
            }
        }
    }
    
    public void writeCacheMapData(final DataOutputStream output, final byte[] usableBuffer, final byte[] integerByteBuffer, final LeveledRegion<T> inRegion) throws IOException {
        output.write(this.colorBufferCompressed ? 1 : 0);
        output.writeInt(this.colorBufferFormat);
        final ByteBuffer directBuffer = this.colorBuffer.getDirectBuffer();
        final int length = directBuffer.remaining();
        output.writeInt(length);
        directBuffer.get(usableBuffer, 0, length);
        BufferCompatibilityFix.position((Buffer)directBuffer, 0);
        output.write(usableBuffer, 0, length);
        output.writeBoolean(this.bufferHasLight);
        final long[] heightData = this.heightValues.getData();
        for (int i = 0; i < heightData.length; ++i) {
            output.writeLong(heightData[i]);
        }
        final long[] topHeightData = this.topHeightValues.getData();
        for (int j = 0; j < topHeightData.length; ++j) {
            output.writeLong(topHeightData[j]);
        }
        this.saveBiomeIndexStorage(output);
    }
    
    public void readCacheData(final int cacheSaveVersion, final DataInputStream input, final byte[] usableBuffer, final byte[] integerByteBuffer, final LeveledRegion<T> inRegion, final MapProcessor mapProcessor, final int x, final int y, final boolean leafShouldAffectBranches) throws IOException {
        if (cacheSaveVersion < 7 || (cacheSaveVersion >= 9 && cacheSaveVersion <= 11)) {
            this.bufferedTextureVersion = 1;
        }
        else {
            this.bufferedTextureVersion = inRegion.getAndResetCachedTextureVersion(x, y);
        }
        if (cacheSaveVersion == 6) {
            input.readInt();
        }
        for (int lightLevelsInCache = (cacheSaveVersion < 3) ? 4 : 1, i = 0; i < lightLevelsInCache; ++i) {
            if (i == 0) {
                this.colorBufferCompressed = true;
                if (cacheSaveVersion > 1) {
                    this.colorBufferCompressed = (input.read() == 1);
                }
                this.colorBufferFormat = input.readInt();
            }
            else {
                if (cacheSaveVersion > 1) {
                    input.read();
                }
                input.readInt();
            }
            final int length = input.readInt();
            IOHelper.readToBuffer(usableBuffer, length, input);
            if (i == 0) {
                if (inRegion.getLevel() == 0 && length == 16384 && this.colorBufferCompressed) {
                    if (this.colorBuffer == null) {
                        this.colorBuffer = WorldMap.textureDirectBufferPool.get(true);
                    }
                    this.colorBufferCompressed = false;
                    this.colorBufferFormat = 32856;
                    inRegion.setShouldCache(true, "broken texture compression fix");
                    BufferCompatibilityFix.limit((Buffer)this.colorBuffer.getDirectBuffer(), 16384);
                }
                else {
                    if (this.colorBuffer == null) {
                        this.colorBuffer = WorldMap.textureDirectBufferPool.get(false);
                    }
                    final ByteBuffer directBuffer = this.colorBuffer.getDirectBuffer();
                    directBuffer.put(usableBuffer, 0, length);
                    BufferCompatibilityFix.flip((Buffer)directBuffer);
                }
            }
        }
        if (cacheSaveVersion >= 14) {
            this.bufferHasLight = input.readBoolean();
        }
        else if (cacheSaveVersion > 2) {
            final int lightLength = input.readInt();
            if (lightLength > 0) {
                IOHelper.readToBuffer(usableBuffer, lightLength, input);
            }
            this.bufferHasLight = false;
        }
        if (cacheSaveVersion >= 13) {
            final long[] heightData = new long[586];
            for (int j = 0; j < heightData.length; ++j) {
                heightData[j] = input.readLong();
            }
            this.heightValues.setData(heightData);
            if (cacheSaveVersion >= 17) {
                final long[] topHeightData = new long[586];
                for (int k = 0; k < topHeightData.length; ++k) {
                    topHeightData[k] = input.readLong();
                }
                this.topHeightValues.setData(topHeightData);
            }
            else {
                final long[] copyFrom = this.heightValues.getData();
                final long[] topHeightData2 = new long[this.topHeightValues.getData().length];
                System.arraycopy(copyFrom, 0, topHeightData2, 0, copyFrom.length);
                this.topHeightValues.setData(topHeightData2);
            }
            this.loadBiomeIndexStorage(input, cacheSaveVersion);
            if (cacheSaveVersion == 16) {
                for (int j = 0; j < 64; ++j) {
                    input.readLong();
                }
            }
        }
        this.toUpload = true;
    }
    
    private void saveBiomeIndexStorage(final DataOutputStream output) throws IOException {
        final Paletted2DFastBitArrayIntStorage biomeIndexStorage = (this.biomes == null) ? null : this.biomes.getBiomeIndexStorage();
        final int paletteSize = (biomeIndexStorage == null) ? 0 : biomeIndexStorage.getPaletteSize();
        if (paletteSize > 0) {
            if (biomeIndexStorage.getPaletteNonNullCount() > 1 || biomeIndexStorage.getDefaultValueCount() != 0) {
                output.writeInt(paletteSize);
                for (int i = 0; i < paletteSize; ++i) {
                    final int paletteElement = biomeIndexStorage.getPaletteElement(i);
                    output.writeInt(paletteElement);
                    if (paletteElement != -1) {
                        output.writeShort(biomeIndexStorage.getPaletteElementCount(i));
                    }
                }
                output.write(1);
                biomeIndexStorage.writeData(output);
            }
            else {
                final int paletteElement2 = biomeIndexStorage.getPaletteElement(paletteSize - 1);
                final int paletteElementCount = biomeIndexStorage.getPaletteElementCount(paletteSize - 1);
                output.writeInt(1);
                output.writeInt(paletteElement2);
                output.writeShort(paletteElementCount);
                output.write(0);
            }
        }
        else {
            output.writeInt(0);
        }
    }
    
    private void loadBiomeIndexStorage(final DataInputStream input, final int cacheSaveVersion) throws IOException {
        if (cacheSaveVersion >= 19) {
            final int paletteSize = input.readInt();
            if (paletteSize > 0) {
                int defaultValueCount = 4096;
                final FastIntPalette fastIntPalette = FastIntPalette.Builder.begin().setMaxCountPerElement(4096).build();
                for (int i = 0; i < paletteSize; ++i) {
                    final int paletteElementValue = input.readInt();
                    if (paletteElementValue == -1) {
                        fastIntPalette.addNull();
                    }
                    else {
                        final int count = input.readShort() & 0xFFFF;
                        fastIntPalette.append(paletteElementValue, count);
                        defaultValueCount -= count;
                    }
                }
                final long[] data = new long[1024];
                if (cacheSaveVersion == 19 || input.read() == 1) {
                    for (int j = 0; j < data.length; ++j) {
                        data[j] = input.readLong();
                    }
                }
                else {
                    System.arraycopy(RegionTexture.ONE_BIOME_PALETTE_DATA, 0, data, 0, data.length);
                }
                final ConsistentBitArray dataStorage = new ConsistentBitArray(13, 4096, data);
                final Paletted2DFastBitArrayIntStorage biomeIndexStorage = Paletted2DFastBitArrayIntStorage.Builder.begin().setPalette(fastIntPalette).setData(dataStorage).setWidth(64).setHeight(64).setDefaultValueCount(defaultValueCount).setMaxPaletteElements(4096).build();
                this.biomes = new RegionTextureBiomes(biomeIndexStorage);
            }
        }
    }
    
    public void deleteTexturesAndBuffers() {
        final int textureToDelete = this.getGlColorTexture();
        this.glColorTexture = -1;
        if (textureToDelete != -1) {
            WorldMap.glObjectDeleter.requestTextureDeletion(textureToDelete);
        }
        this.onTextureDeletion();
        if (this.getColorBuffer() != null) {
            this.deleteColorBuffer();
        }
        this.deletePBOs();
    }
    
    public PoolTextureDirectBufferUnit getColorBuffer() {
        return this.colorBuffer;
    }
    
    public ByteBuffer getDirectColorBuffer() {
        return (this.colorBuffer == null) ? null : this.colorBuffer.getDirectBuffer();
    }
    
    public void setShouldDownloadFromPBO(final boolean shouldDownloadFromPBO) {
        this.shouldDownloadFromPBO = shouldDownloadFromPBO;
    }
    
    public int getColorBufferFormat() {
        return this.colorBufferFormat;
    }
    
    public boolean isColorBufferCompressed() {
        return this.colorBufferCompressed;
    }
    
    public boolean shouldDownloadFromPBO() {
        return this.shouldDownloadFromPBO;
    }
    
    public int getTimer() {
        return this.timer;
    }
    
    public void decTimer() {
        --this.timer;
    }
    
    public void resetTimer() {
        this.timer = 0;
    }
    
    public final int getGlColorTexture() {
        return this.glColorTexture;
    }
    
    public void onTextureDeletion() {
        this.updateTextureVersion(0);
    }
    
    public boolean shouldUpload() {
        return this.toUpload;
    }
    
    public void setToUpload(final boolean value) {
        this.toUpload = value;
    }
    
    public boolean isCachePrepared() {
        return this.cachePrepared;
    }
    
    public void setCachePrepared(final boolean cachePrepared) {
        this.cachePrepared = cachePrepared;
    }
    
    public boolean canUpload() {
        return true;
    }
    
    public boolean isUploaded() {
        return !this.shouldUpload();
    }
    
    public int getTextureVersion() {
        return this.textureVersion;
    }
    
    public int getBufferedTextureVersion() {
        return this.bufferedTextureVersion;
    }
    
    public void setBufferedTextureVersion(final int bufferedTextureVersion) {
        this.bufferedTextureVersion = bufferedTextureVersion;
    }
    
    public LeveledRegion<T> getRegion() {
        return this.region;
    }
    
    protected void updateTextureVersion(final int newVersion) {
        this.textureVersion = newVersion;
    }
    
    public int getHeight(final int x, final int z) {
        final int index = (z << 6) + x;
        final int value = this.heightValues.get(index);
        if (value >> 8 == 0) {
            return -1;
        }
        return value & 0xFF;
    }
    
    public void putHeight(final int x, final int z, final int height) {
        final int index = (z << 6) + x;
        final int value = 0x100 | (height & 0xFF);
        this.heightValues.set(index, value);
    }
    
    public void removeHeight(final int x, final int z) {
        final int index = (z << 6) + x;
        this.heightValues.set(index, 0);
    }
    
    public int getTopHeight(final int x, final int z) {
        final int index = (z << 6) + x;
        final int value = this.topHeightValues.get(index);
        if (value >> 8 == 0) {
            return -1;
        }
        return value & 0xFF;
    }
    
    public void putTopHeight(final int x, final int z, final int height) {
        final int index = (z << 6) + x;
        final int value = 0x100 | (height & 0xFF);
        this.topHeightValues.set(index, value);
    }
    
    public void removeTopHeight(final int x, final int z) {
        final int index = (z << 6) + x;
        this.topHeightValues.set(index, 0);
    }
    
    public void ensureBiomeIndexStorage() {
        if (this.biomes == null) {
            final Paletted2DFastBitArrayIntStorage biomeIndexStorage = Paletted2DFastBitArrayIntStorage.Builder.begin().setMaxPaletteElements(4096).setDefaultValue(-1).setWidth(64).setHeight(64).build();
            this.biomes = new RegionTextureBiomes(biomeIndexStorage);
        }
    }
    
    public int getBiome(final int x, final int z) {
        final RegionTextureBiomes biomes = this.biomes;
        if (biomes == null) {
            return -1;
        }
        return biomes.getBiomeIndexStorage().get(x, z);
    }
    
    public void setBiome(final int x, final int z, final int biome) {
        this.ensureBiomeIndexStorage();
        final Paletted2DFastBitArrayIntStorage biomeIndexStorage = this.biomes.getBiomeIndexStorage();
        final int currentBiome = biomeIndexStorage.get(x, z);
        if (biome == currentBiome) {
            return;
        }
        try {
            biomeIndexStorage.set(x, z, biome);
        }
        catch (Throwable t) {
            WorldMap.LOGGER.error("weird biomes " + this.region + " pixel x:" + x + " z:" + z + " " + currentBiome + " " + biome, t);
            for (int i = 0; i < 8; ++i) {
                for (int j = 0; j < 8; ++j) {
                    if (this.region.getTexture(i, j) == this) {
                        WorldMap.LOGGER.info("texture " + i + " " + j);
                    }
                }
            }
            WorldMap.LOGGER.error(biomeIndexStorage.getBiomePaletteDebug());
            final int[] realCounts = new int[biomeIndexStorage.getPaletteSize()];
            for (int p = 0; p < 64; ++p) {
                String line = "";
                for (int o = 0; o < 64; ++o) {
                    final int rawIndex = biomeIndexStorage.getRaw(o, p) - 1;
                    line = line + " " + rawIndex;
                    if (rawIndex >= 0 && rawIndex < realCounts.length) {
                        final int[] array = realCounts;
                        final int n = rawIndex;
                        ++array[n];
                    }
                }
                WorldMap.LOGGER.error(line);
            }
            WorldMap.LOGGER.error("real counts: " + Arrays.toString(realCounts));
            WorldMap.LOGGER.error("suppressed exception", t);
            this.region.setShouldCache(true, "broken cache biome data");
            if (this.region.getLevel() > 0) {
                this.textureVersion = new Random().nextInt();
                ((BranchLeveledRegion)this.region).setShouldCheckForUpdatesRecursive(true);
            }
            else {
                ((MapRegion)this.region).setCacheHashCode(0);
            }
            this.biomes = null;
        }
    }
    
    public boolean getTextureHasLight() {
        return this.textureHasLight;
    }
    
    public void addDebugLines(final List<String> debugLines) {
        debugLines.add("shouldUpload: " + this.shouldUpload() + " timer: " + this.getTimer());
        debugLines.add(String.format("buffer exists: %s", this.getColorBuffer() != null));
        debugLines.add("glColorTexture: " + this.getGlColorTexture() + " textureHasLight: " + this.textureHasLight);
        debugLines.add("cachePrepared: " + this.isCachePrepared());
        debugLines.add("textureVersion: " + this.textureVersion);
        debugLines.add("colorBufferFormat: " + this.colorBufferFormat);
        if (this.biomes != null) {
            debugLines.add(this.biomes.getBiomeIndexStorage().getBiomePaletteDebug());
        }
    }
    
    protected void onCacheUploadRequested() {
    }
    
    public boolean shouldBeUsedForBranchUpdate(final int usedVersion) {
        return (this.shouldHaveContentForBranchUpdate() ? this.textureVersion : 0) != usedVersion;
    }
    
    public boolean shouldHaveContentForBranchUpdate() {
        return true;
    }
    
    public boolean shouldIncludeInCache() {
        return true;
    }
    
    public RegionTextureBiomes getBiomes() {
        return this.biomes;
    }
    
    public void resetBiomes() {
        this.biomes = null;
    }
    
    public abstract boolean hasSourceData();
    
    public abstract void preUpload(final MapProcessor p0, final BiomeColorCalculator p1, final OverlayManager p2, final LeveledRegion<T> p3, final boolean p4, final BlockStateShortShapeCache p5, final MapUpdateFastConfig p6);
    
    public abstract void postUpload(final MapProcessor p0, final LeveledRegion<T> p1, final boolean p2);
    
    protected abstract long uploadNonCache(final DimensionHighlighterHandler p0, final TextureUploader p1, final BranchTextureRenderer p2, final ScaledResolution p3);
    
    public boolean getBufferHasLight() {
        return this.bufferHasLight;
    }
    
    static {
        final ConsistentBitArray dataStorage = new ConsistentBitArray(13, 4096);
        for (int i = 0; i < 4096; ++i) {
            dataStorage.set(i, 1);
        }
        ONE_BIOME_PALETTE_DATA = dataStorage.getData();
    }
}
