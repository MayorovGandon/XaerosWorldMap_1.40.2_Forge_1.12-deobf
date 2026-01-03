//Decompiled by Procyon!

package xaero.map.file.export;

import net.minecraft.util.text.*;

public enum PNGExportResultType
{
    NOT_PREPARED((ITextComponent)new TextComponentTranslation("gui.xaero_png_result_not_prepared", new Object[] { new Style().func_150238_a(TextFormatting.RED) }), false), 
    EMPTY((ITextComponent)new TextComponentTranslation("gui.xaero_png_result_empty", new Object[] { new Style().func_150238_a(TextFormatting.RED) }), false), 
    TOO_BIG((ITextComponent)new TextComponentTranslation("gui.xaero_png_result_too_big", new Object[] { new Style().func_150238_a(TextFormatting.RED) }), false), 
    IMAGE_TOO_BIG((ITextComponent)new TextComponentTranslation("gui.xaero_png_result_image_too_big", new Object[] { new Style().func_150238_a(TextFormatting.RED) }), false), 
    OUT_OF_MEMORY((ITextComponent)new TextComponentTranslation("gui.xaero_png_result_out_of_memory", new Object[] { new Style().func_150238_a(TextFormatting.RED) }), false), 
    BAD_FBO((ITextComponent)new TextComponentTranslation("gui.xaero_png_result_bad_fbo", new Object[] { new Style().func_150238_a(TextFormatting.RED) }), false), 
    IO_EXCEPTION((ITextComponent)new TextComponentTranslation("gui.xaero_png_result_io_exception", new Object[] { new Style().func_150238_a(TextFormatting.RED) }), false), 
    SUCCESS((ITextComponent)new TextComponentTranslation("gui.xaero_png_result_success", new Object[] { new Style().func_150238_a(TextFormatting.GREEN) }), true);
    
    private final ITextComponent message;
    private final boolean success;
    
    private PNGExportResultType(final ITextComponent message, final boolean success) {
        this.message = message;
        this.success = success;
    }
    
    public ITextComponent getMessage() {
        return this.message;
    }
    
    public boolean isSuccess() {
        return this.success;
    }
}
