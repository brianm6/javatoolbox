package toolbox.graph.jung;

import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;


/**
 * JungVertex is responsible for ___.
 */
public class JungVertex implements toolbox.graph.Vertex
{
    Vertex vertex_;
    
    /**
     * Creates a JungVertex.
     * 
     */
    public JungVertex(String label)
    {
        vertex_ = new DirectedSparseVertex();

    }

    
    /**
     * @see toolbox.graph.Delegator#getDelegate()
     */
    public Object getDelegate()
    {
        return vertex_;
    }
    
}
