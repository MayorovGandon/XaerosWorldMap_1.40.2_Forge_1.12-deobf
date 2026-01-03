//Decompiled by Procyon!

package xaero.map.entity.util;

import net.minecraft.entity.*;
import net.minecraft.util.math.*;

public class EntityUtil
{
    public static double getEntityX(final Entity e, final float partial) {
        final double xOld = (e.field_70173_aa > 0) ? e.field_70142_S : e.field_70165_t;
        return xOld + (e.field_70165_t - xOld) * partial;
    }
    
    public static double getEntityY(final Entity e, final float partial) {
        final double yOld = (e.field_70173_aa > 0) ? e.field_70137_T : e.field_70163_u;
        return yOld + (e.field_70163_u - yOld) * partial;
    }
    
    public static double getEntityZ(final Entity e, final float partial) {
        final double zOld = (e.field_70173_aa > 0) ? e.field_70136_U : e.field_70161_v;
        return zOld + (e.field_70161_v - zOld) * partial;
    }
    
    public static Vec3d getEntityPos(final Entity e, final float partial) {
        return new Vec3d(getEntityX(e, partial), getEntityY(e, partial), getEntityZ(e, partial));
    }
}
