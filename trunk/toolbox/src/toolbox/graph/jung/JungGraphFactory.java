package toolbox.graph.jung;

import toolbox.graph.Edge;
import toolbox.graph.Graph;
import toolbox.graph.GraphFactory;
import toolbox.graph.Vertex;

/**
 * JungGraphFactory is responsible for ___.
 */
public class JungGraphFactory implements GraphFactory
{
    /**
     * Creates a JungGraphFactory.
     * 
     */
    public JungGraphFactory()
    {
    }

    /**
     * @see toolbox.graph.GraphFactory#createEdge(toolbox.graph.Vertex, toolbox.graph.Vertex)
     */
    public Edge createEdge(Vertex from, Vertex to)
    {
        Edge e = new JungEdge();
        return e;
    }
    
    
    /**
     * @see toolbox.graph.GraphFactory#createGraph()
     */
    public Graph createGraph()
    {
        Graph g = new JungGraph();
        return g;
    }
    
    
    /**
     * @see toolbox.graph.GraphFactory#createVertex(java.lang.String)
     */
    public Vertex createVertex(String label)
    {
        Vertex v = new JungVertex(label);
        return v;
    }
}
