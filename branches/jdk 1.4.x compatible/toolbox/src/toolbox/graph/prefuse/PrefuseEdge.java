package toolbox.graph.prefuse;

import toolbox.graph.Vertex;

import edu.berkeley.guir.prefuse.graph.DefaultEdge;
import edu.berkeley.guir.prefuse.graph.Edge;
import edu.berkeley.guir.prefuse.graph.Node;

/**
 * Prefuse implementation of an {@link toolbox.graph.Edge}.
 */
public class PrefuseEdge implements toolbox.graph.Edge {

    // --------------------------------------------------------------------------
    // Fields
    // --------------------------------------------------------------------------

    /**
     * Prefuse lib version of an edge.
     */
    private Edge edge_;

    // --------------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------------

    /**
     * Creates a non-directed PrefuseEdge.
     * 
     * @param from Source vertex of this edge.
     * @param to Destination vertex of this edge.
     */
    public PrefuseEdge(toolbox.graph.Vertex from, toolbox.graph.Vertex to) {
        this(from, to, true);
    }


    /**
     * Creates a PrefuseEdge.
     * 
     * @param from Source vertex of this edge.
     * @param to Destination vertex of this edge.
     * @param directed True for directed edge, false otherwise.
     */
    public PrefuseEdge(
        toolbox.graph.Vertex from,
        toolbox.graph.Vertex to,
        boolean directed) {
        Node fromVertex = (Node) from.getDelegate();
        Node toVertex = (Node) to.getDelegate();
        edge_ = new DefaultEdge(fromVertex, toVertex, directed);
    }

    // --------------------------------------------------------------------------
    // Edge Interface
    // --------------------------------------------------------------------------

    /*
     * @see toolbox.graph.Edge#getDestination()
     */
    public Vertex getDestination() {
        return PrefuseGraphLib.lookupVertex(edge_.getSecondNode());
    }

    /*
     * @see toolbox.graph.Edge#getSource()
     */
    public Vertex getSource() {
        return PrefuseGraphLib.lookupVertex(edge_.getFirstNode());
    }

    // --------------------------------------------------------------------------
    // Delegator Interface
    // --------------------------------------------------------------------------

    /*
     * @see toolbox.graph.Delegator#getDelegate()
     */
    public Object getDelegate() {
        return edge_;
    }
}