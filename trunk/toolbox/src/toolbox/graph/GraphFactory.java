package toolbox.graph;

/**
 * GraphFactory is responsible for ___.
 */
public interface GraphFactory
{
    Graph createGraph();
    
    Edge createEdge(Vertex from, Vertex to);
    
    Vertex createVertex(String name);
    
}
