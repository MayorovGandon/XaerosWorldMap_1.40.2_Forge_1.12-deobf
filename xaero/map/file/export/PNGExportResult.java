//Decompiled by Procyon!

package xaero.map.file.export;

import java.nio.file.*;
import net.minecraft.util.text.*;

public class PNGExportResult
{
    private final PNGExportResultType type;
    private final Path folderToOpen;
    
    public PNGExportResult(final PNGExportResultType type, final Path folderToOpen) {
        this.type = type;
        this.folderToOpen = folderToOpen;
    }
    
    public PNGExportResultType getType() {
        return this.type;
    }
    
    public Path getFolderToOpen() {
        return this.folderToOpen;
    }
    
    public ITextComponent getMessage() {
        return this.type.getMessage();
    }
}
