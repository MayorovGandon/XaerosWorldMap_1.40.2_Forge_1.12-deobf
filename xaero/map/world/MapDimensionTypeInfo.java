//Decompiled by Procyon!

package xaero.map.world;

public class MapDimensionTypeInfo
{
    private final String name;
    private final boolean skyLight;
    private final float ambientLight;
    private final int height;
    private final int logicalHeight;
    private final boolean nether;
    private final boolean surfaceWorld;
    private final boolean end;
    private final float noonCelestialAngle;
    private final double coordinateScale;
    private final String savePath;
    
    public MapDimensionTypeInfo(final String name, final boolean skyLight, final float ambientLight, final int height, final int logicalHeight, final boolean nether, final boolean surfaceWorld, final boolean end, final float noonCelestialAngle, final double coordinateScale, final String savePath) {
        this.name = name;
        this.skyLight = skyLight;
        this.ambientLight = ambientLight;
        this.height = height;
        this.logicalHeight = logicalHeight;
        this.nether = nether;
        this.surfaceWorld = surfaceWorld;
        this.end = end;
        this.noonCelestialAngle = noonCelestialAngle;
        this.coordinateScale = coordinateScale;
        this.savePath = savePath;
    }
    
    public String getName() {
        return this.name;
    }
    
    public boolean hasSkyLight() {
        return this.skyLight;
    }
    
    public float getAmbientLight() {
        return this.ambientLight;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public int getLogicalHeight() {
        return this.logicalHeight;
    }
    
    public String getSavePath() {
        return this.savePath;
    }
    
    public boolean isNether() {
        return this.nether;
    }
    
    public boolean isSurfaceWorld() {
        return this.surfaceWorld;
    }
    
    public boolean isEnd() {
        return this.end;
    }
    
    public float getNoonCelestialAngle() {
        return this.noonCelestialAngle;
    }
    
    public double getCoordinateScale() {
        return this.coordinateScale;
    }
    
    @Override
    public String toString() {
        return this.name + ":" + this.skyLight + "$" + this.ambientLight + "$" + this.height + "$" + this.logicalHeight + "$" + this.nether + "$" + this.surfaceWorld + "$" + this.end + "$" + this.noonCelestialAngle + "$" + this.coordinateScale + "$path$" + this.savePath;
    }
    
    public static MapDimensionTypeInfo fromString(final String name, final String s) {
        if (s == null) {
            return null;
        }
        try {
            final String[] args = s.split("\\$");
            final boolean skyLight = args[0].equals("true");
            final float ambientLight = Float.parseFloat(args[1]);
            final int height = Integer.parseInt(args[2]);
            final int logicalHeight = Integer.parseInt(args[3]);
            final boolean nether = args[4].equals("true");
            final boolean surfaceWorld = args[5].equals("true");
            final boolean end = args[6].equals("true");
            final float noonCelestialAngle = Float.parseFloat(args[7]);
            final double coordinateScale = Double.parseDouble(args[8]);
            final String savePath = s.substring(s.indexOf("$path$") + 6);
            return new MapDimensionTypeInfo(name, skyLight, ambientLight, height, logicalHeight, nether, surfaceWorld, end, noonCelestialAngle, coordinateScale, savePath);
        }
        catch (Throwable t) {
            return null;
        }
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final MapDimensionTypeInfo other = (MapDimensionTypeInfo)obj;
        if (this.ambientLight != other.ambientLight) {
            return false;
        }
        if (this.coordinateScale != other.coordinateScale) {
            return false;
        }
        if (this.end != other.end) {
            return false;
        }
        if (this.height != other.height) {
            return false;
        }
        if (this.logicalHeight != other.logicalHeight) {
            return false;
        }
        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
        }
        else if (!this.name.equals(other.name)) {
            return false;
        }
        if (this.nether != other.nether) {
            return false;
        }
        if (this.noonCelestialAngle != other.noonCelestialAngle) {
            return false;
        }
        if (this.savePath == null) {
            if (other.savePath != null) {
                return false;
            }
        }
        else if (!this.savePath.equals(other.savePath)) {
            return false;
        }
        return this.skyLight == other.skyLight && this.surfaceWorld == other.surfaceWorld;
    }
}
