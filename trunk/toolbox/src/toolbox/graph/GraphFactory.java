package toolbox.graph;

import java.util.List;

/**
 * GraphFactory is responsible for ___.
 */
public interface GraphFactory
{
    Graph createGraph();
    
    Edge createEdge(Vertex from, Vertex to);
    
    Vertex createVertex(Graph graph, String label);
    
    GraphView createView(Graph graph);
    
    List getLayouts(Graph graph);
}
