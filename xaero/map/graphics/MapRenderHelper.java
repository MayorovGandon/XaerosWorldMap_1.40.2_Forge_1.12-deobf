//Decompiled by Procyon!

package xaero.map.graphics;

import net.minecraft.client.renderer.vertex.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.*;
import net.minecraft.util.text.*;

public class MapRenderHelper
{
    public static void renderTexturedModalRect(final float x, final float y, final float width, final float height, final int textureX, final int textureY, final float textureW, final float textureH, final float fullTextureWidth, final float fullTextureHeight) {
        final Tessellator tessellator = Tessellator.func_178181_a();
        final BufferBuilder vertexBuffer = tessellator.func_178180_c();
        vertexBuffer.func_181668_a(7, DefaultVertexFormats.field_181707_g);
        final float normalizedTextureX = textureX / fullTextureWidth;
        final float normalizedTextureY = textureY / fullTextureHeight;
        final float normalizedTextureX2 = (textureX + textureW) / fullTextureWidth;
        final float normalizedTextureY2 = (textureY + textureH) / fullTextureHeight;
        vertexBuffer.func_181662_b((double)(x + 0.0f), (double)(y + height), 0.0).func_187315_a((double)normalizedTextureX, (double)normalizedTextureY2).func_181675_d();
        vertexBuffer.func_181662_b((double)(x + width), (double)(y + height), 0.0).func_187315_a((double)normalizedTextureX2, (double)normalizedTextureY2).func_181675_d();
        vertexBuffer.func_181662_b((double)(x + width), (double)(y + 0.0f), 0.0).func_187315_a((double)normalizedTextureX2, (double)normalizedTextureY).func_181675_d();
        vertexBuffer.func_181662_b((double)(x + 0.0f), (double)(y + 0.0f), 0.0).func_187315_a((double)normalizedTextureX, (double)normalizedTextureY).func_181675_d();
        tessellator.func_78381_a();
    }
    
    public static void renderDynamicHighlight(final int flooredCameraX, final int flooredCameraZ, final int leftX, final int rightX, final int topZ, final int bottomZ, final float sideR, final float sideG, final float sideB, final float sideA, final float centerR, final float centerG, final float centerB, final float centerA) {
        final int sideColor = getColorInt(sideR, sideG, sideB, sideA);
        final int centerColor = getColorInt(centerR, centerG, centerB, centerA);
        Gui.func_73734_a(leftX - 1 - flooredCameraX, topZ - 1 - flooredCameraZ, leftX - flooredCameraX, bottomZ + 1 - flooredCameraZ, sideColor);
        Gui.func_73734_a(leftX - flooredCameraX, topZ - 1 - flooredCameraZ, rightX - flooredCameraX, topZ - flooredCameraZ, sideColor);
        Gui.func_73734_a(rightX - flooredCameraX, topZ - 1 - flooredCameraZ, rightX + 1 - flooredCameraX, bottomZ + 1 - flooredCameraZ, sideColor);
        Gui.func_73734_a(leftX - flooredCameraX, bottomZ - flooredCameraZ, rightX - flooredCameraX, bottomZ + 1 - flooredCameraZ, sideColor);
        Gui.func_73734_a(leftX - flooredCameraX, topZ - flooredCameraZ, rightX - flooredCameraX, bottomZ - flooredCameraZ, centerColor);
    }
    
    public static int getColorInt(final float r, final float g, final float b, final float a) {
        final int ri = (int)(r * 255.0f);
        final int gi = (int)(g * 255.0f);
        final int bi = (int)(b * 255.0f);
        final int ai = (int)(a * 255.0f);
        return ai << 24 | ri << 16 | gi << 8 | bi;
    }
    
    public static void drawCenteredStringWithBackground(final FontRenderer font, final String string, final int x, final int y, final int color, final float bgRed, final float bgGreen, final float bgBlue, final float bgAlpha) {
        final int stringWidth = font.func_78256_a(string);
        drawStringWithBackground(font, string, x - stringWidth / 2, y, color, bgRed, bgGreen, bgBlue, bgAlpha);
    }
    
    public static void drawStringWithBackground(final FontRenderer font, final String string, final int x, final int y, final int color, final float bgRed, final float bgGreen, final float bgBlue, final float bgAlpha) {
        final int stringWidth = font.func_78256_a(string);
        GlStateManager.func_179109_b(0.0f, 0.0f, -1.0f);
        Gui.func_73734_a(x - 1, y - 1, x + stringWidth + 1, y + 9, getColorInt(bgRed, bgGreen, bgBlue, bgAlpha));
        GlStateManager.func_179109_b(0.0f, 0.0f, 1.0f);
        font.func_175063_a(string, (float)x, (float)y, color);
    }
    
    public static void drawCenteredStringWithBackground(final FontRenderer font, final ITextComponent text, final int x, final int y, final int color, final float bgRed, final float bgGreen, final float bgBlue, final float bgAlpha) {
        drawCenteredStringWithBackground(font, text.func_150254_d(), x, y, color, bgRed, bgGreen, bgBlue, bgAlpha);
    }
    
    public static void drawStringWithBackground(final FontRenderer font, final ITextComponent text, final int x, final int y, final int color, final float bgRed, final float bgGreen, final float bgBlue, final float bgAlpha) {
        drawStringWithBackground(font, text.func_150254_d(), x, y, color, bgRed, bgGreen, bgBlue, bgAlpha);
    }
}
