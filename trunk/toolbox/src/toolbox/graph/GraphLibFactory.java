package toolbox.graph;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * GraphLibFactory is responsible for creating knowing implementations of the
 * GraphLib interface.
 */
public final class GraphLibFactory {

    private static final Logger logger_ = 
        Logger.getLogger(GraphLibFactory.class);

    // -------------------------------------------------------------------------
    // GraphLib Type Constants
    // -------------------------------------------------------------------------

    /**
     * Java Universal Network Graph (JUNG) library at 
     * <a href="http://jung.sf.net"/>.
     */
    public static final String TYPE_JUNG = "toolbox.graph.jung.JungGraphLib";

    /**
     * Prefuse java graphing library at <a href="http://prefuse.sf.net"/>.
     */
    public static final String TYPE_PREFUSE = "toolbox.graph.prefuse.PrefuseGraphLib";

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Prevent construction of this static singleton.
     */
    private GraphLibFactory() {
    }

    // -------------------------------------------------------------------------
    // Public
    // -------------------------------------------------------------------------

    /**
     * Returns a a newly created GraphLib based on the given type.
     * 
     * @param type Type of GraphLib implementation.
     * @see #TYPE_JUNG
     * @see #TYPE_PREFUSE
     * @return GraphLib
     */
    public static GraphLib create(String type) {
        GraphLib lib = null;

        try {
            lib = (GraphLib) Class.forName(type).newInstance();
        }
        catch (Exception e) {
            logger_.error(e);
        }
        return lib;
    }


    /**
     * Returns a list of all known GraphLib implementations in no particular
     * order.
     * 
     * @return List<GraphLib>
     */
    public static List createAll() {
        List result = new ArrayList();
        result.add(create(TYPE_JUNG));
        result.add(create(TYPE_PREFUSE));
        return result;
    }
}