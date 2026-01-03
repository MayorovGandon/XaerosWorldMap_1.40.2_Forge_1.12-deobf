//Decompiled by Procyon!

package xaero.map.misc;

import javax.crypto.*;
import xaero.lib.*;
import xaero.lib.common.config.primary.option.*;
import xaero.lib.common.config.option.*;
import xaero.lib.patreon.*;
import xaero.lib.client.online.decrypt.*;
import java.io.*;
import xaero.map.*;
import xaero.map.config.primary.option.*;
import java.net.*;
import java.util.*;
import java.security.spec.*;
import java.security.*;

public class Internet
{
    public static Cipher cipher;
    
    public static void checkModVersion() {
        if (!(boolean)XaeroLib.INSTANCE.getLibConfigChannel().getPrimaryCommonConfigManager().getEffective((ConfigOption)LibPrimaryCommonConfigOptions.ALLOW_INTERNET)) {
            return;
        }
        final int keyVersion = Patreon.getKEY_VERSION2();
        String s = "http://data.chocolateminecraft.com/Versions_" + keyVersion + "/WorldMap" + ((keyVersion >= 4) ? ".dat" : ".txt");
        s = s.replaceAll(" ", "%20");
        try {
            if (Internet.cipher == null) {
                throw new Exception("Cipher instance is null!");
            }
            final URL url = new URL(s);
            final URLConnection conn = url.openConnection();
            conn.setReadTimeout(900);
            conn.setConnectTimeout(900);
            if (conn.getContentLengthLong() > 524288L) {
                throw new IOException("Input too long to trust!");
            }
            final BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream)new DecryptInputStream(conn.getInputStream(), Internet.cipher), "UTF8"));
            WorldMap.isOutdated = true;
            final boolean updateNotificationConfig = (boolean)WorldMap.INSTANCE.getConfigs().getPrimaryClientConfigManager().getEffective((ConfigOption)WorldMapPrimaryClientConfigOptions.UPDATE_NOTIFICATIONS);
            final int ignoredUpdateConfig = (int)WorldMap.INSTANCE.getConfigs().getPrimaryClientConfigManager().getEffective(WorldMapPrimaryClientConfigOptions.IGNORED_UPDATE);
            String line = reader.readLine();
            if (line != null) {
                WorldMap.newestUpdateID = Integer.parseInt(line);
                if (!updateNotificationConfig || WorldMap.newestUpdateID == ignoredUpdateConfig) {
                    WorldMap.isOutdated = false;
                    reader.close();
                    return;
                }
            }
            boolean versionFound = false;
            final String[] current = WorldMap.versionID.split("_");
            while ((line = reader.readLine()) != null) {
                if (line.equals(WorldMap.versionID)) {
                    WorldMap.isOutdated = false;
                    break;
                }
                if (!Patreon.getHasAutoUpdates()) {
                    continue;
                }
                if (versionFound) {
                    if (line.startsWith("meta;")) {
                        final String[] metadata = line.substring(5).split(";");
                        WorldMap.latestVersionMD5 = metadata[0];
                    }
                    versionFound = false;
                }
                if (!line.startsWith(current[0] + "_")) {
                    continue;
                }
                final String[] args = line.split("_");
                if (args.length != current.length) {
                    continue;
                }
                boolean sameType = true;
                if (current.length > 2) {
                    for (int i = 2; i < current.length && sameType; ++i) {
                        if (!args[i].equals(current[i])) {
                            sameType = false;
                        }
                    }
                }
                if (!sameType) {
                    continue;
                }
                WorldMap.latestVersion = args[1];
                versionFound = true;
            }
            reader.close();
        }
        catch (IOException ioe) {
            WorldMap.LOGGER.error("io exception while checking versions: {}", (Object)ioe.getMessage());
            WorldMap.isOutdated = false;
        }
        catch (Throwable e) {
            WorldMap.LOGGER.error("suppressed exception", e);
            WorldMap.isOutdated = false;
        }
    }
    
    static {
        Internet.cipher = null;
        try {
            Internet.cipher = Cipher.getInstance("RSA");
            final KeyFactory factory = KeyFactory.getInstance("RSA");
            final byte[] byteKey = Base64.getDecoder().decode(Patreon.getPublicKeyString2().getBytes());
            final X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
            final PublicKey publicKey = factory.generatePublic(X509publicKey);
            Internet.cipher.init(2, publicKey);
        }
        catch (Exception e) {
            Internet.cipher = null;
            WorldMap.LOGGER.error("suppressed exception", (Throwable)e);
        }
    }
}
