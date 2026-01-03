//Decompiled by Procyon!

package xaero.map.message;

import java.util.function.*;
import xaero.map.server.level.*;
import xaero.map.*;

public class LevelMapPropertiesConsumer implements Consumer<LevelMapProperties>
{
    @Override
    public void accept(final LevelMapProperties t) {
        final WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
        worldmapSession.getMapProcessor().onServerLevelId(t.getId());
    }
}
