package toolbox.graph;

/**
 * A Layout is responsible for arranging the vertices and edges on a GraphView.
 */
public interface Layout extends Delegator {

    /**
     * Returns the name of this layout.
     * 
     * @return String
     */
    String getName();
}