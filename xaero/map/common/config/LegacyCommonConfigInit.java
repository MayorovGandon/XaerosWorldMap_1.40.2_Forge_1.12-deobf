//Decompiled by Procyon!

package xaero.map.common.config;

import java.nio.file.*;
import java.io.*;
import xaero.map.*;

public class LegacyCommonConfigInit
{
    public void init(final boolean dedicatedServer, final Path clientConfigDir, final String configFileName) {
        final Path configDestinationPath = clientConfigDir;
        Path configPath = configDestinationPath.resolve(configFileName);
        if (dedicatedServer && !Files.exists(configPath, new LinkOption[0])) {
            final Path oldConfigPath = new File(".").toPath().resolve(configFileName);
            if (Files.exists(oldConfigPath, new LinkOption[0])) {
                configPath = oldConfigPath;
            }
        }
        final LegacyCommonConfigIO io = WorldMap.commonConfigIO = new LegacyCommonConfigIO(configPath);
        if (Files.exists(configPath, new LinkOption[0])) {
            io.load();
        }
    }
}
