package toolbox.graph;

import java.util.Set;

/**
 * A Vertex is a single node in a graph.
 */
public interface Vertex extends Delegator
{
    Set getEdges();
}
