package toolbox.graph;

/**
 * A Graph is a model for the representation of arbitrary graphs.
 */
public interface Graph extends Delegator {

    /**
     * Adds a vertex to this graph.
     * 
     * @param vertex Vertex to add.
     */
    void addVertex(Vertex vertex);


    /**
     * Adds an edge to this graph.
     * 
     * @param edge Edge to add.
     */
    void addEdge(Edge edge);
}
