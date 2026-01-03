//Decompiled by Procyon!

package xaero.map.mods.gui;

import xaero.map.element.*;
import xaero.map.element.render.*;
import net.minecraft.client.*;
import java.util.*;
import xaero.map.gui.dropdown.rightclick.*;
import net.minecraft.client.gui.*;
import xaero.map.mods.*;
import xaero.map.gui.*;
import xaero.map.*;
import xaero.map.common.config.option.*;
import xaero.lib.common.config.option.*;
import xaero.lib.client.config.*;

public class WaypointReader extends MapElementReader<Waypoint, WaypointRenderContext, WaypointRenderer>
{
    public boolean waypointIsGood(final Waypoint w, final WaypointRenderContext context) {
        return ((w.getType() != 1 && w.getType() != 2) || context.deathpoints) && (w.isGlobal() || context.userScale >= context.minZoomForLocalWaypoints);
    }
    
    public boolean isHidden(final Waypoint element, final WaypointRenderContext context) {
        return !this.waypointIsGood(element, context) || (!context.showDisabledWaypoints && element.isDisabled());
    }
    
    @Deprecated
    public boolean isInteractable(final int location, final Waypoint element) {
        return this.isInteractable(ElementRenderLocation.fromIndex(location), element);
    }
    
    public boolean isInteractable(final ElementRenderLocation location, final Waypoint element) {
        return true;
    }
    
    @Deprecated
    public float getBoxScale(final int location, final Waypoint element, final WaypointRenderContext context) {
        return this.getBoxScale(ElementRenderLocation.fromIndex(location), element, context);
    }
    
    public float getBoxScale(final ElementRenderLocation location, final Waypoint element, final WaypointRenderContext context) {
        return context.worldmapWaypointsScale;
    }
    
    public double getRenderX(final Waypoint element, final WaypointRenderContext context, final float partialTicks) {
        return element.getRenderX();
    }
    
    public double getRenderZ(final Waypoint element, final WaypointRenderContext context, final float partialTicks) {
        return element.getRenderZ();
    }
    
    public int getInteractionBoxLeft(final Waypoint element, final WaypointRenderContext context, final float partialTicks) {
        return -this.getInteractionBoxRight(element, context, partialTicks);
    }
    
    public int getInteractionBoxRight(final Waypoint element, final WaypointRenderContext context, final float partialTicks) {
        return (element.getSymbol().length() > 1) ? 21 : 14;
    }
    
    public int getInteractionBoxTop(final Waypoint element, final WaypointRenderContext context, final float partialTicks) {
        return context.waypointBackgrounds ? -41 : -12;
    }
    
    public int getInteractionBoxBottom(final Waypoint element, final WaypointRenderContext context, final float partialTicks) {
        return context.waypointBackgrounds ? 0 : 12;
    }
    
    public int getLeftSideLength(final Waypoint element, final Minecraft mc) {
        return 9 + element.getCachedNameLength();
    }
    
    public String getMenuName(final Waypoint element) {
        String name = element.getName();
        if (element.isGlobal()) {
            name = "* " + name;
        }
        return name;
    }
    
    public int getMenuTextFillLeftPadding(final Waypoint element) {
        return (element.isDisabled() ? 11 : 0) + (element.isTemporary() ? 10 : 0);
    }
    
    public String getFilterName(final Waypoint element) {
        return this.getMenuName(element) + " " + element.getSymbol();
    }
    
    public ArrayList<RightClickOption> getRightClickOptions(final Waypoint element, final IRightClickableElement target) {
        final ArrayList<RightClickOption> rightClickOptions = new ArrayList<RightClickOption>();
        rightClickOptions.add(new RightClickOption(element.getName(), rightClickOptions.size(), target) {
            public void onAction(final GuiScreen screen) {
                SupportMods.xaeroMinimap.openWaypoint((GuiMap)screen, element);
            }
        });
        final ClientConfigManager configManager = WorldMap.INSTANCE.getConfigs().getClientConfigManager();
        if ((boolean)configManager.getEffective((ConfigOption)WorldMapProfiledConfigOptions.COORDINATES) && !SupportMods.xaeroMinimap.hidingWaypointCoordinates()) {
            rightClickOptions.add(new RightClickOption(String.format("X: %d, Y: %s, Z: %d", element.getX(), element.isyIncluded() ? ("" + element.getY()) : "~", element.getZ()), rightClickOptions.size(), target) {
                public void onAction(final GuiScreen screen) {
                    SupportMods.xaeroMinimap.openWaypoint((GuiMap)screen, element);
                }
            });
        }
        rightClickOptions.add(new RightClickOption("gui.xaero_right_click_waypoint_edit", rightClickOptions.size(), target) {
            public void onAction(final GuiScreen screen) {
                SupportMods.xaeroMinimap.openWaypoint((GuiMap)screen, element);
            }
        }.setNameFormatArgs(new Object[] { "E" }));
        rightClickOptions.add(new RightClickOption("gui.xaero_right_click_waypoint_teleport", rightClickOptions.size(), target) {
            public void onAction(final GuiScreen screen) {
                SupportMods.xaeroMinimap.teleportToWaypoint(screen, element);
            }
            
            public boolean isActive() {
                return SupportMods.xaeroMinimap.canTeleport(SupportMods.xaeroMinimap.getWaypointWorld());
            }
        }.setNameFormatArgs(new Object[] { "T" }));
        rightClickOptions.add(new RightClickOption("gui.xaero_right_click_waypoint_share", rightClickOptions.size(), target) {
            public void onAction(final GuiScreen screen) {
                SupportMods.xaeroMinimap.shareWaypoint(element, (GuiMap)screen, SupportMods.xaeroMinimap.getWaypointWorld());
            }
        });
        rightClickOptions.add(new RightClickOption("", rightClickOptions.size(), target) {
            public String getName() {
                return element.isTemporary() ? "gui.xaero_right_click_waypoint_restore" : (element.isDisabled() ? "gui.xaero_right_click_waypoint_enable" : "gui.xaero_right_click_waypoint_disable");
            }
            
            public void onAction(final GuiScreen screen) {
                if (element.isTemporary()) {
                    SupportMods.xaeroMinimap.toggleTemporaryWaypoint(element);
                }
                else {
                    SupportMods.xaeroMinimap.disableWaypoint(element);
                }
            }
        }.setNameFormatArgs(new Object[] { "H" }));
        rightClickOptions.add(new RightClickOption("", rightClickOptions.size(), target) {
            public String getName() {
                return element.isTemporary() ? "gui.xaero_right_click_waypoint_delete_confirm" : "gui.xaero_right_click_waypoint_delete";
            }
            
            public void onAction(final GuiScreen screen) {
                if (element.isTemporary()) {
                    SupportMods.xaeroMinimap.deleteWaypoint(element);
                }
                else {
                    SupportMods.xaeroMinimap.toggleTemporaryWaypoint(element);
                }
            }
        }.setNameFormatArgs(new Object[] { "DEL" }));
        return rightClickOptions;
    }
    
    public boolean isRightClickValid(final Waypoint element) {
        return SupportMods.xaeroMinimap.waypointExists(element);
    }
    
    public int getRightClickTitleBackgroundColor(final Waypoint element) {
        return element.getColor();
    }
    
    public boolean shouldScaleBoxWithOptionalScale() {
        return true;
    }
    
    public int getRenderBoxLeft(final Waypoint element, final WaypointRenderContext context, final float partialTicks) {
        final int left = this.getInteractionBoxLeft(element, context, partialTicks);
        if (element.getAlpha() <= 0.0f) {
            return left;
        }
        return Math.min(left, -element.getCachedNameLength() * 3 / 2);
    }
    
    public int getRenderBoxRight(final Waypoint element, final WaypointRenderContext context, final float partialTicks) {
        final int right = this.getInteractionBoxRight(element, context, partialTicks) + 12;
        if (element.getAlpha() <= 0.0f) {
            return right;
        }
        return Math.max(right, element.getCachedNameLength() * 3 / 2);
    }
    
    public int getRenderBoxTop(final Waypoint element, final WaypointRenderContext context, final float partialTicks) {
        return this.getInteractionBoxTop(element, context, partialTicks);
    }
    
    public int getRenderBoxBottom(final Waypoint element, final WaypointRenderContext context, final float partialTicks) {
        return this.getInteractionBoxBottom(element, context, partialTicks);
    }
}
