package toolbox.graph;

/**
 * An edge connects two vertices. It can be directed (have an arrow) or
 * undirected.
 */
public interface Edge extends Delegator
{
    Vertex getSource();
    Vertex getDestination();
}