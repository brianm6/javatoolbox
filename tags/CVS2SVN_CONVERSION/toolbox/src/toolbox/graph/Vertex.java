package toolbox.graph;

import java.util.Set;

/**
 * A Vertex is a single node in a {@link Graph}.
 */
public interface Vertex extends Delegator {

    /**
     * Returns all edges attached to this vertex.
     * 
     * @return Set of Edges
     */
    Set getEdges();


    /**
     * Returns the text label of this vertex.
     * 
     * @return String
     */
    String getText();


    /**
     * Sets the text label for this vertex.
     * 
     * @param text Label
     */
    void setText(String text);
}
