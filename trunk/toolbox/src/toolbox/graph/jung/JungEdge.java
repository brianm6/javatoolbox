package toolbox.graph.jung;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;

/**
 * Jung implementation of an {@link toolbox.graph.Edge}.
 */
public class JungEdge implements toolbox.graph.Edge  
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Jung lib version of an edge.
     */
    private Edge edge_;
 
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a JungEdge.
     * 
     * @param from Source vertex of this edge.
     * @param to Destination vertext of this edge.
     */
    public JungEdge(toolbox.graph.Vertex from, toolbox.graph.Vertex to)
    {
        Vertex fromVertex = (Vertex) from.getDelegate();
        Vertex toVertex   = (Vertex) to.getDelegate();
        edge_ = new DirectedSparseEdge(fromVertex, toVertex);
        Graph graph = (Graph) fromVertex.getGraph();
        graph.addEdge(edge_);
    }
    
    //--------------------------------------------------------------------------
    // Delegator Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.graph.Delegator#getDelegate()
     */
    public Object getDelegate()
    {
        return edge_;
    }
}