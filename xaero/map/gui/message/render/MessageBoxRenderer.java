//Decompiled by Procyon!

package xaero.map.gui.message.render;

import net.minecraft.client.renderer.*;
import xaero.map.gui.message.*;
import net.minecraft.client.gui.*;
import java.util.*;

public class MessageBoxRenderer
{
    private final int OPAQUE_FOR = 5000;
    private final int FADE_FOR = 3000;
    
    public void render(final MessageBox messageBox, final FontRenderer font, final int x, final int y, final boolean rightAlign) {
        final long time = System.currentTimeMillis();
        GlStateManager.func_179094_E();
        GlStateManager.func_179109_b((float)x, (float)y, 0.0f);
        int index = 0;
        final Iterator<Message> iterator = (Iterator<Message>)messageBox.getIterator();
        while (iterator.hasNext()) {
            final Message message = iterator.next();
            final int passed = (int)(time - message.getAdditionTime());
            final float opacity = (passed < 5000) ? 1.0f : ((3000 - (passed - 5000)) / 3000.0f);
            final int alphaInt = (int)(opacity * 255.0f);
            if (alphaInt <= 3) {
                break;
            }
            final int textColor = 0xFFFFFF | alphaInt << 24;
            final int bgColor = (int)(0.5f * alphaInt) << 24;
            final int textWidth = font.func_78256_a(message.getText().func_150254_d());
            final int textX = rightAlign ? (-textWidth - 1) : 2;
            final int textY = -index * 10 - 4;
            final int bgWidth = textWidth + 3;
            Gui.func_73734_a(textX - 2, textY - 1, textX - 2 + bgWidth, textY + 9, bgColor);
            font.func_175063_a(message.getText().func_150254_d(), (float)textX, (float)textY, textColor);
            ++index;
        }
        GlStateManager.func_179121_F();
    }
}
