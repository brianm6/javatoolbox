package toolbox.graph.jung;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;

/**
 * Jung implemenation of a {@link toolbox.graph.Graph}.
 */
public class JungGraph implements toolbox.graph.Graph
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Jung version of a graph.
     */
    private Graph graph_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a JungGraph.
     */
    public JungGraph()
    {
        graph_ = new DirectedSparseGraph();
    }

    //--------------------------------------------------------------------------
    // toolbox.graph.Graph Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.graph.Graph#addVertex(toolbox.graph.Vertex)
     */
    public void addVertex(toolbox.graph.Vertex vertex)
    {
        Vertex v = (Vertex) vertex.getDelegate();
        graph_.addVertex(v);
    }

    
    /**
     * @see toolbox.graph.Graph#addEdge(toolbox.graph.Edge)
     */
    public void addEdge(toolbox.graph.Edge edge)
    {
        Edge e = (Edge) edge.getDelegate();
        graph_.addEdge(e);
    }
    
    //--------------------------------------------------------------------------
    // Delegator Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.graph.Delegator#getDelegate()
     */
    public Object getDelegate()
    {
        return graph_;
    }
}
