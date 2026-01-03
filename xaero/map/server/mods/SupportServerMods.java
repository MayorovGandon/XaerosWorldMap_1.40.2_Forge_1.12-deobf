//Decompiled by Procyon!

package xaero.map.server.mods;

import xaero.map.server.mods.ftbteams.*;

public class SupportServerMods
{
    private static SupportFTBTeamsServer ftbTeams;
    private static SupportMinimapServer minimap;
    
    public static void check() {
        try {
            Class.forName("com.feed_the_beast.ftblib.lib.data.FTBLibAPI");
            SupportServerMods.ftbTeams = new SupportFTBTeamsServer();
        }
        catch (ClassNotFoundException ex) {}
        try {
            Class.forName("xaero.common.XaeroMinimapSession");
            SupportServerMods.minimap = new SupportMinimapServer();
        }
        catch (ClassNotFoundException ex2) {}
    }
    
    public static boolean hasFtbTeams() {
        return SupportServerMods.ftbTeams != null;
    }
    
    public static SupportFTBTeamsServer getFtbTeams() {
        return SupportServerMods.ftbTeams;
    }
    
    public static boolean hasMinimap() {
        return SupportServerMods.minimap != null;
    }
    
    public static SupportMinimapServer getMinimap() {
        return SupportServerMods.minimap;
    }
}
