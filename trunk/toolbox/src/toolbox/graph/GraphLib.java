package toolbox.graph;

import java.util.List;

/**
 * A GraphLib represents the interface of a graphing library with the basic
 * operations to create graphs.
 */
public interface GraphLib
{
    /**
     * Returns a newly created graph.
     * 
     * @return Graph
     */
    Graph createGraph();
    
    
    /**
     * Returns a newly created edge between the two vertices and adds it to
     * the graph in which the vertices lie.
     *  
     * @param from Source vertex.
     * @param to Destination vertex.
     * @return Edge
     */
    Edge createEdge(Vertex from, Vertex to);
    
    
    /**
     * Returns a newly created Vertex with the given label and adds it to the
     * passed in Graph.
     * 
     * @param graph Graph to create the vertex in.
     * @param label Vertex label.
     * @return Vertex
     */
    Vertex createVertex(Graph graph, String label);
    
    
    /**
     * Returns a newly created GraphView associated with the passed in Graph.
     * 
     * @param graph Graph to associate the GraphView with.
     * @return GraphView
     */
    GraphView createView(Graph graph);
    
    
    /**
     * Returns a list of the layouts that the passed in Graph supports.
     * 
     * @param graph Graph to query for layouts.
     * @return List<Layout>
     */
    List getLayouts(Graph graph);
}
