//Decompiled by Procyon!

package xaero.map;

public class CrashHandler
{
    private Throwable crashedBy;
    
    public void checkForCrashes() throws RuntimeException {
        if (this.crashedBy != null) {
            final Throwable crash = this.crashedBy;
            this.crashedBy = null;
            throw new RuntimeException("Xaero's World Map (" + WorldMap.versionID + ") has crashed! Please report here: bit.ly/XaeroWMIssues", crash);
        }
    }
    
    public Throwable getCrashedBy() {
        return this.crashedBy;
    }
    
    public void setCrashedBy(final Throwable crashedBy) {
        if (this.crashedBy == null) {
            this.crashedBy = crashedBy;
        }
    }
}
