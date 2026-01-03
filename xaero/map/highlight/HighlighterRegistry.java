//Decompiled by Procyon!

package xaero.map.highlight;

import java.util.*;

public class HighlighterRegistry
{
    private List<AbstractHighlighter> highlighters;
    
    public HighlighterRegistry() {
        this.highlighters = new ArrayList<AbstractHighlighter>();
    }
    
    public void register(final AbstractHighlighter highlighter) {
        this.highlighters.add(highlighter);
    }
    
    public void end() {
        this.highlighters = Collections.unmodifiableList((List<? extends AbstractHighlighter>)this.highlighters);
    }
    
    public List<AbstractHighlighter> getHighlighters() {
        return this.highlighters;
    }
}
