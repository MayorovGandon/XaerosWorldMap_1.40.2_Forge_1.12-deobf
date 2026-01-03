//Decompiled by Procyon!

package xaero.map.common.config;

import java.nio.file.*;
import xaero.map.*;
import xaero.map.common.config.option.*;
import xaero.lib.common.config.option.*;
import com.google.common.collect.*;
import xaero.lib.common.util.*;
import java.io.*;
import xaero.lib.common.config.profile.*;
import xaero.map.common.config.primary.option.*;

public class LegacyCommonConfigIO
{
    private final Path configFilePath;
    private boolean allowCaveModeOnServer;
    private boolean allowNetherCaveModeOnServer;
    private boolean shouldEnableEveryoneTracksEveryone;
    
    public LegacyCommonConfigIO(final Path configFilePath) {
        this.configFilePath = configFilePath;
    }
    
    public void load() {
        final ConfigProfile defaultEnforcedProfile = WorldMap.INSTANCE.getConfigs().getServerConfigManager().getDefaultEnforcedProfile();
        try (final BufferedInputStream bufferedOutput = new BufferedInputStream(new FileInputStream(this.configFilePath.toFile()));
             final BufferedReader reader = new BufferedReader(new InputStreamReader(bufferedOutput))) {
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    this.readLine(line.split(":"));
                }
                if (this.allowCaveModeOnServer && this.allowNetherCaveModeOnServer) {
                    return;
                }
                if (!this.allowCaveModeOnServer && !this.allowNetherCaveModeOnServer) {
                    defaultEnforcedProfile.set((ConfigOption)WorldMapProfiledConfigOptions.CAVE_MODE_ALLOWED, (Object)false);
                    return;
                }
                if (this.allowCaveModeOnServer) {
                    defaultEnforcedProfile.set((ConfigOption)WorldMapProfiledConfigOptions.CAVE_MODE_ALLOWED_DIMENSIONS, (Object)Sets.newHashSet((Object[])new Integer[] { 0, 1 }));
                    return;
                }
                defaultEnforcedProfile.set((ConfigOption)WorldMapProfiledConfigOptions.CAVE_MODE_ALLOWED_DIMENSIONS, (Object)Sets.newHashSet((Object[])new Integer[] { -1 }));
            }
            finally {
                WorldMap.INSTANCE.getConfigs().getPrimaryCommonConfigManagerIO().save();
                WorldMap.INSTANCE.getConfigs().getServerConfigProfileIO().save(defaultEnforcedProfile);
                reader.close();
                IOUtils.tryQuickFileBackupMove(this.configFilePath, 10);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private boolean readLine(final String[] args) {
        if (args[0].equals("allowCaveModeOnServer")) {
            this.allowCaveModeOnServer = args[1].equals("true");
            return true;
        }
        if (args[0].equals("allowNetherCaveModeOnServer")) {
            this.allowNetherCaveModeOnServer = args[1].equals("true");
            return true;
        }
        if (args[0].equals("registerStatusEffects")) {
            WorldMap.INSTANCE.getConfigs().getPrimaryCommonConfigManager().getConfig().set((ConfigOption)WorldMapPrimaryCommonConfigOptions.REGISTER_EFFECTS, (Object)args[1].equals("true"));
            return true;
        }
        return args[0].equals("everyoneTracksEveryone") && args[1].equals("true") && (this.shouldEnableEveryoneTracksEveryone = true);
    }
    
    public boolean shouldEnableEveryoneTracksEveryone() {
        return this.shouldEnableEveryoneTracksEveryone;
    }
}
