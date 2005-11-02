package toolbox.graph;

/**
 * An Edge connects two Vertices. It can be directed (have an arrow) or
 * undirected.
 */
public interface Edge extends Delegator {

    /**
     * Returns the source vertex.
     * 
     * @return Vertex
     */
    Vertex getSource();


    /**
     * Returns the destination vertex.
     * 
     * @return Vertex
     */
    Vertex getDestination();
}