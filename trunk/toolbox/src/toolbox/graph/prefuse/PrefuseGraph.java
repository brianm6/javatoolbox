package toolbox.graph.prefuse;

import edu.berkeley.guir.prefuse.graph.DefaultGraph;
import edu.berkeley.guir.prefuse.graph.Edge;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.Node;

/**
 * Prefuse implemenation of a {@link toolbox.graph.Graph}.
 */
public class PrefuseGraph implements toolbox.graph.Graph
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Prefuse version of a graph.
     */
    private Graph graph_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a PrefuseGraph.
     */
    public PrefuseGraph()
    {
        this(true);
    }

    
    /**
     * Creates a PrefuseGraph.
     * 
     * @param directed True for directed, false otherwise.
     */
    public PrefuseGraph(boolean directed)
    {
        graph_ = new DefaultGraph(directed);
    }

    //--------------------------------------------------------------------------
    // toolbox.graph.Graph Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.graph.Graph#addVertex(toolbox.graph.Vertex)
     */
    public void addVertex(toolbox.graph.Vertex vertex)
    {
        graph_.addNode((Node) vertex.getDelegate());
    }

    
    /**
     * @see toolbox.graph.Graph#addEdge(toolbox.graph.Edge)
     */
    public void addEdge(toolbox.graph.Edge edge)
    {
        graph_.addEdge((Edge) edge.getDelegate());
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