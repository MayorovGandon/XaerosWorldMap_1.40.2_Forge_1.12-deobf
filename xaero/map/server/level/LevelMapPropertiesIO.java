//Decompiled by Procyon!

package xaero.map.server.level;

import java.nio.file.*;
import java.nio.charset.*;
import java.io.*;

public class LevelMapPropertiesIO
{
    public static final String FILE_NAME = "xaeromap.txt";
    
    public void load(final Path file, final LevelMapProperties dest) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file.toFile()), "UTF8"));
            dest.read(reader);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
    
    public void save(final Path file, final LevelMapProperties dest) throws IOException {
        try (final BufferedOutputStream bufferedOutput = new BufferedOutputStream(new FileOutputStream(file.toFile()));
             final PrintWriter writer = new PrintWriter(new OutputStreamWriter(bufferedOutput, StandardCharsets.UTF_8))) {
            dest.write(writer);
        }
    }
}
