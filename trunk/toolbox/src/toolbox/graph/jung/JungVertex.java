package toolbox.graph.jung;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.StringLabeller;
import edu.uci.ics.jung.graph.decorators.StringLabeller.UniqueLabelException;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;

import org.apache.log4j.Logger;


/**
 * JungVertex is responsible for ___.
 */
public class JungVertex implements toolbox.graph.Vertex
{
    private static final Logger logger_ = Logger.getLogger(JungVertex.class);
    
    Vertex vertex_;
    
    /**
     * Creates a JungVertex.
     * 
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

    
    /**
     * @see toolbox.graph.Delegator#getDelegate()
     */
    public Object getDelegate()
    {
        return vertex_;
    }
    
}
