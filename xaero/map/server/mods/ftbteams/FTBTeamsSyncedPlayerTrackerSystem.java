//Decompiled by Procyon!

package xaero.map.server.mods.ftbteams;

import xaero.map.server.radar.tracker.*;
import net.minecraft.entity.player.*;
import com.feed_the_beast.ftblib.lib.*;
import com.feed_the_beast.ftblib.lib.data.*;

public class FTBTeamsSyncedPlayerTrackerSystem implements ISyncedPlayerTrackerSystem
{
    @Override
    public int getTrackingLevel(final EntityPlayer tracker, final EntityPlayer tracked) {
        if (FTBLibAPI.arePlayersInSameTeam(tracker.func_110124_au(), tracked.func_110124_au())) {
            return 2;
        }
        final ForgePlayer trackerPlayer = Universe.get().getPlayer(tracker.func_110124_au());
        if (!trackerPlayer.hasTeam()) {
            return 0;
        }
        final ForgePlayer trackedPlayer = Universe.get().getPlayer(tracked.func_110124_au());
        if (!trackedPlayer.hasTeam()) {
            return 0;
        }
        if (trackerPlayer.team.getHighestStatus(trackedPlayer) == EnumTeamStatus.ALLY && trackedPlayer.team.getHighestStatus(trackerPlayer) == EnumTeamStatus.ALLY) {
            return 1;
        }
        return 0;
    }
    
    @Override
    public boolean isPartySystem() {
        return true;
    }
}
