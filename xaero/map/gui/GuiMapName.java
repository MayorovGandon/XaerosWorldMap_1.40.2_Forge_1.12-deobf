//Decompiled by Procyon!

package xaero.map.gui;

import xaero.lib.client.gui.*;
import xaero.map.world.*;
import net.minecraft.util.text.*;
import net.minecraft.client.resources.*;
import xaero.lib.client.gui.widget.*;
import net.minecraft.client.gui.*;
import org.lwjgl.input.*;
import java.nio.file.attribute.*;
import xaero.map.*;
import java.io.*;
import java.nio.file.*;

public class GuiMapName extends ScreenBase
{
    protected String screenTitle;
    private GuiTextField nameTextField;
    private MapDimension mapDimension;
    private String editingMWId;
    private String currentNameFieldContent;
    private MapProcessor mapProcessor;
    
    public GuiMapName(final MapProcessor mapProcessor, final GuiScreen par1GuiScreen, final GuiScreen escape, final MapDimension mapDimension, final String editingMWId) {
        super(par1GuiScreen, escape, (ITextComponent)new TextComponentTranslation("gui.xaero_map_name", new Object[0]));
        this.mapDimension = mapDimension;
        this.editingMWId = editingMWId;
        this.currentNameFieldContent = ((editingMWId == null) ? "" : mapDimension.getMultiworldName(editingMWId));
        this.mapProcessor = mapProcessor;
        this.canSkipWorldRender = true;
    }
    
    public void func_73866_w_() {
        super.func_73866_w_();
        this.screenTitle = I18n.func_135052_a("gui.xaero_map_name", new Object[0]);
        if (this.nameTextField != null) {
            this.currentNameFieldContent = this.nameTextField.func_146179_b();
        }
        (this.nameTextField = new GuiTextField(0, this.field_146289_q, this.field_146294_l / 2 - 100, 60, 200, 20)).func_146180_a(this.currentNameFieldContent);
        this.nameTextField.func_146195_b(true);
        this.func_189646_b((GuiButton)new MySmallButton(200, this.field_146294_l / 2 - 155, this.field_146295_m / 6 + 168, I18n.func_135052_a("gui.xaero_confirm", new Object[0])));
        this.func_189646_b((GuiButton)new MySmallButton(201, this.field_146294_l / 2 + 5, this.field_146295_m / 6 + 168, I18n.func_135052_a("gui.xaero_cancel", new Object[0])));
        Keyboard.enableRepeatEvents(true);
        this.updateConfirmButton();
    }
    
    protected void func_146284_a(final GuiButton button) throws IOException {
        if (button.field_146124_l) {
            if (button.field_146127_k == 200) {
                if (this.canConfirm()) {
                    synchronized (this.mapProcessor.uiSync) {
                        if (this.mapProcessor.getMapWorld() == this.mapDimension.getMapWorld()) {
                            final String unfilteredName = this.nameTextField.func_146179_b();
                            String mwIdFixed;
                            if (this.editingMWId == null) {
                                String mwId = unfilteredName.toLowerCase().replaceAll("[^a-z0-9]+", "");
                                if (mwId.isEmpty()) {
                                    mwId = "map";
                                }
                                mwId = "cm$" + mwId;
                                boolean mwAdded = false;
                                mwIdFixed = mwId;
                                int fix = 1;
                                while (!mwAdded) {
                                    mwAdded = this.mapDimension.addMultiworldChecked(mwIdFixed);
                                    if (!mwAdded) {
                                        ++fix;
                                        mwIdFixed = mwId + fix;
                                    }
                                }
                                final Path dimensionFolderPath = this.mapDimension.getMainFolderPath();
                                final Path multiworldFolderPath = dimensionFolderPath.resolve(mwIdFixed);
                                try {
                                    Files.createDirectories(multiworldFolderPath, (FileAttribute<?>[])new FileAttribute[0]);
                                }
                                catch (IOException e) {
                                    WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
                                }
                                this.mapDimension.setMultiworldUnsynced(mwIdFixed);
                            }
                            else {
                                mwIdFixed = this.editingMWId;
                            }
                            this.mapDimension.setMultiworldName(mwIdFixed, unfilteredName);
                            this.mapDimension.saveConfigUnsynced();
                            this.goBack();
                        }
                    }
                }
            }
            else if (button.field_146127_k == 201) {
                this.goBack();
            }
        }
    }
    
    public void func_146281_b() {
        Keyboard.enableRepeatEvents(false);
    }
    
    private boolean canConfirm() {
        return this.nameTextField.func_146179_b().length() > 0;
    }
    
    private void updateConfirmButton() {
        this.field_146292_n.get(0).field_146124_l = this.canConfirm();
    }
    
    protected void func_73869_a(final char par1, final int par2) throws IOException {
        super.func_73869_a(par1, par2);
        if (this.nameTextField.func_146206_l()) {
            this.nameTextField.func_146201_a(par1, par2);
            this.updateConfirmButton();
        }
        if ((par2 == 28 || par2 == 156) && this.canConfirm()) {
            this.func_146284_a(this.field_146292_n.get(0));
        }
    }
    
    public void func_73876_c() {
        this.updateConfirmButton();
        this.nameTextField.func_146178_a();
    }
    
    public void func_73863_a(final int par1, final int par2, final float par3) {
        this.renderEscapeScreen(par1, par2, par3);
        this.func_146276_q_();
        this.func_73732_a(this.field_146289_q, this.screenTitle, this.field_146294_l / 2, 20, 16777215);
        this.nameTextField.func_146194_f();
        super.func_73863_a(par1, par2, par3);
    }
    
    protected void func_73864_a(final int par1, final int par2, final int par3) throws IOException {
        super.func_73864_a(par1, par2, par3);
        this.nameTextField.func_146192_a(par1, par2, par3);
    }
}
