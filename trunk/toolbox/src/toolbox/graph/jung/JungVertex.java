package toolbox.graph.jung;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.StringLabeller;
import edu.uci.ics.jung.graph.decorators.StringLabeller.UniqueLabelException;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;

import org.apache.log4j.Logger;

/**
 * Jung implemenation of a {@link toolbox.graph.Vertex}.
 */
public class JungVertex implements toolbox.graph.Vertex
{
    private static final Logger logger_ = Logger.getLogger(JungVertex.class);
 
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Jung vertex delegate.
     */
    private Vertex vertex_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a JungVertex.
     * 
     * @param graph Graph to add this vertex to.
     * @param label Label to associate with this vertex. 
     */
    public JungVertex(toolbox.graph.Graph graph, String label)
    {
        vertex_ = new DirectedSparseVertex();
        Graph g = (Graph) graph.getDelegate();
        g.addVertex(vertex_);
        
        try
        {
            StringLabeller.getLabeller(g).setLabel(vertex_, label);
        }
        catch (UniqueLabelException e)
        {
            logger_.error(e);
        }
    }

    //--------------------------------------------------------------------------
    // Delegator Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.graph.Delegator#getDelegate()
     */
    public Object getDelegate()
    {
        return vertex_;
    }
}