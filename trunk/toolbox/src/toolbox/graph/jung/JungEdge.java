package toolbox.graph.jung;


import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;

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
    public JungEdge(toolbox.graph.Vertex from, toolbox.graph.Vertex to)
    {
        Vertex fromVertex = (Vertex) from.getDelegate();
        Vertex toVertex   = (Vertex) to.getDelegate();
        edge_ = new DirectedSparseEdge(fromVertex, toVertex);
        Graph graph = (Graph) fromVertex.getGraph();
        graph.addEdge(edge_);
    }
    
    
    /**
     * @see toolbox.graph.Delegator#getDelegate()
     */
    public Object getDelegate()
    {
        return edge_;
    }
}
