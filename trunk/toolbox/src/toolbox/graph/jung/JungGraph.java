package toolbox.graph.jung;



import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;

/**
 * JungGraph is responsible for ___.
 */
public class JungGraph implements toolbox.graph.Graph
{
    Graph graph_;

    /**
     * Creates a JungGraph.
     * 
     */
    public JungGraph()
    {
        graph_ = new DirectedSparseGraph();
    }
    

    /**
     * @see toolbox.graph.Delegator#getDelegate()
     */
    public Object getDelegate()
    {
        return graph_;
    }

    
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
}
