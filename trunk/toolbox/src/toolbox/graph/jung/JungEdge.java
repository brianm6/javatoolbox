package toolbox.graph.jung;

import edu.uci.ics.jung.graph.Edge;

/**
 * JungEdge is responsible for ___.
 */
public class JungEdge implements toolbox.graph.Edge  
{
    Edge edge_;
    
    /**
     * Creates a JungEdge.
     * 
     */
    public JungEdge()
    {
    }
    
    
    /**
     * @see toolbox.graph.Delegator#getDelegate()
     */
    public Object getDelegate()
    {
        return edge_;
    }
}
