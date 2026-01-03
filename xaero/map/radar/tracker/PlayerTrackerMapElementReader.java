//Decompiled by Procyon!

package xaero.map.radar.tracker;

import xaero.map.element.*;
import net.minecraft.client.*;
import net.minecraft.client.network.*;
import xaero.map.gui.*;
import java.util.*;
import xaero.map.gui.dropdown.rightclick.*;
import net.minecraft.client.gui.*;
import xaero.map.common.config.option.*;
import xaero.lib.common.config.option.*;
import xaero.lib.client.config.*;
import xaero.map.*;
import xaero.map.element.render.*;

public class PlayerTrackerMapElementReader extends MapElementReader<PlayerTrackerMapElement<?>, PlayerTrackerMapElementRenderContext, PlayerTrackerMapElementRenderer>
{
    public boolean isHidden(final PlayerTrackerMapElement<?> element, final PlayerTrackerMapElementRenderContext context) {
        return Minecraft.func_71410_x().field_71441_e.field_73011_w.getDimension() != element.getDimension() && context.mapDimId != element.getDimension();
    }
    
    public double getRenderX(final PlayerTrackerMapElement<?> element, final PlayerTrackerMapElementRenderContext context, final float partialTicks) {
        if (Minecraft.func_71410_x().field_71441_e.field_73011_w.getDimension() != element.getDimension()) {
            return element.getX() * context.mapDimDiv;
        }
        return element.getX();
    }
    
    public double getRenderZ(final PlayerTrackerMapElement<?> element, final PlayerTrackerMapElementRenderContext context, final float partialTicks) {
        if (Minecraft.func_71410_x().field_71441_e.field_73011_w.getDimension() != element.getDimension()) {
            return element.getZ() * context.mapDimDiv;
        }
        return element.getZ();
    }
    
    public int getInteractionBoxLeft(final PlayerTrackerMapElement<?> element, final PlayerTrackerMapElementRenderContext context, final float partialTicks) {
        return -16;
    }
    
    public int getInteractionBoxRight(final PlayerTrackerMapElement<?> element, final PlayerTrackerMapElementRenderContext context, final float partialTicks) {
        return 16;
    }
    
    public int getInteractionBoxTop(final PlayerTrackerMapElement<?> element, final PlayerTrackerMapElementRenderContext context, final float partialTicks) {
        return -16;
    }
    
    public int getInteractionBoxBottom(final PlayerTrackerMapElement<?> element, final PlayerTrackerMapElementRenderContext context, final float partialTicks) {
        return 16;
    }
    
    public int getRenderBoxLeft(final PlayerTrackerMapElement<?> element, final PlayerTrackerMapElementRenderContext context, final float partialTicks) {
        return -20;
    }
    
    public int getRenderBoxRight(final PlayerTrackerMapElement<?> element, final PlayerTrackerMapElementRenderContext context, final float partialTicks) {
        return 20;
    }
    
    public int getRenderBoxTop(final PlayerTrackerMapElement<?> element, final PlayerTrackerMapElementRenderContext context, final float partialTicks) {
        return -20;
    }
    
    public int getRenderBoxBottom(final PlayerTrackerMapElement<?> element, final PlayerTrackerMapElementRenderContext context, final float partialTicks) {
        return 20;
    }
    
    public int getLeftSideLength(final PlayerTrackerMapElement<?> element, final Minecraft mc) {
        final NetworkPlayerInfo info = Minecraft.func_71410_x().func_147114_u().func_175102_a(element.getPlayerId());
        if (info == null) {
            return 9;
        }
        return 9 + mc.field_71466_p.func_78256_a(info.func_178845_a().getName());
    }
    
    public String getMenuName(final PlayerTrackerMapElement<?> element) {
        final NetworkPlayerInfo info = Minecraft.func_71410_x().func_147114_u().func_175102_a(element.getPlayerId());
        if (info == null) {
            return element.getPlayerId() + "";
        }
        return info.func_178845_a().getName();
    }
    
    public String getFilterName(final PlayerTrackerMapElement<?> element) {
        return this.getMenuName(element);
    }
    
    public int getMenuTextFillLeftPadding(final PlayerTrackerMapElement<?> element) {
        return 0;
    }
    
    public int getRightClickTitleBackgroundColor(final PlayerTrackerMapElement<?> element) {
        return -11184641;
    }
    
    public boolean shouldScaleBoxWithOptionalScale() {
        return true;
    }
    
    public boolean isRightClickValid(final PlayerTrackerMapElement<?> element) {
        return WorldMap.trackedPlayerRenderer.getCollector().playerExists(element.getPlayerId());
    }
    
    public ArrayList<RightClickOption> getRightClickOptions(final PlayerTrackerMapElement<?> element, final IRightClickableElement target) {
        final ArrayList<RightClickOption> rightClickOptions = new ArrayList<RightClickOption>();
        rightClickOptions.add(new RightClickOption(this.getMenuName(element), rightClickOptions.size(), target) {
            public void onAction(final GuiScreen screen) {
            }
        });
        rightClickOptions.add(new RightClickOption("", rightClickOptions.size(), target) {
            public String getName() {
                final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
                if (!(boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.COORDINATES)) {
                    return "hidden";
                }
                return String.format("X: %d, Y: %s, Z: %d", (int)Math.floor(element.getX()), (int)Math.floor(element.getY()), (int)Math.floor(element.getZ()));
            }
            
            public void onAction(final GuiScreen screen) {
            }
        });
        rightClickOptions.add(new RightClickOption("gui.xaero_right_click_player_teleport", rightClickOptions.size(), target) {
            public void onAction(final GuiScreen screen) {
                final WorldMapSession session = WorldMapSession.getCurrentSession();
                new PlayerTeleporter().teleportToPlayer(screen, session.getMapProcessor().getMapWorld(), element);
            }
        }.setNameFormatArgs(new Object[] { "T" }));
        return rightClickOptions;
    }
    
    @Deprecated
    public boolean isInteractable(final int location, final PlayerTrackerMapElement<?> element) {
        return this.isInteractable(ElementRenderLocation.fromIndex(location), element);
    }
    
    public boolean isInteractable(final ElementRenderLocation location, final PlayerTrackerMapElement<?> element) {
        return true;
    }
}
