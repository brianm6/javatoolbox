package toolbox.graph.jung;

import edu.uci.ics.jung.visualization.Layout;

/**
 * JungLayout is responsible for _____.
 */
public class JungLayout implements toolbox.graph.Layout
{
    private Layout delegate_;
    
    /**
     * Creates a JungLayout.
     * 
     * 
     */
    public JungLayout(Layout layout)
    {
        delegate_ = layout;
    }
    
    
    /**
     * @see toolbox.graph.Delegator#getDelegate()
     */
    public Object getDelegate()
    {
        return delegate_;
    }
}
