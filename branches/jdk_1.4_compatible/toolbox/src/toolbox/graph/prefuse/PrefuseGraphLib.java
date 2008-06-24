package toolbox.graph.prefuse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.berkeley.guir.prefuse.graph.Node;
import edu.berkeley.guir.prefusex.layout.CircleLayout;
import edu.berkeley.guir.prefusex.layout.RandomLayout;

import toolbox.graph.Edge;
import toolbox.graph.Graph;
import toolbox.graph.GraphLib;
import toolbox.graph.GraphView;
import toolbox.graph.Vertex;

/**
 * Prefuse implementation of a {@link toolbox.graph.GraphLib}.
 */
public class PrefuseGraphLib implements GraphLib {

    // --------------------------------------------------------------------------
    // Constants
    // --------------------------------------------------------------------------

    /**
     * Reverse lookup table for graph objects.
     */
    private static final Map lookup_ = new HashMap();

    // --------------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------------

    /**
     * Creates a PrefuseGraphFactory.
     */
    public PrefuseGraphLib() {
    }

    // --------------------------------------------------------------------------
    // GraphLib Interface
    // --------------------------------------------------------------------------

    /*
     * @see toolbox.graph.GraphLib#createEdge(toolbox.graph.Vertex,
     *      toolbox.graph.Vertex)
     */
    public Edge createEdge(Vertex from, Vertex to) {
        Edge e = new PrefuseEdge(from, to);
        return e;
    }


    /*
     * @see toolbox.graph.GraphLib#createGraph()
     */
    public Graph createGraph() {
        Graph g = new PrefuseGraph();
        return g;
    }


    /*
     * @see toolbox.graph.GraphLib#createVertex(toolbox.graph.Graph,
     *      java.lang.String)
     */
    public Vertex createVertex(Graph graph, String label) {
        Vertex v = new PrefuseVertex(graph, label);
        return v;
    }

    /*
     * @see toolbox.graph.GraphLib#createView(toolbox.graph.Graph)
     */
    public GraphView createView(Graph graph) {
        GraphView view = new PrefuseGraphView(graph);
        return view;
    }


    /*
     * @see toolbox.graph.GraphLib#getLayouts(toolbox.graph.Graph)
     */
    public List getLayouts(Graph graph) {
        List layouts = new ArrayList();
        layouts.add(new PrefuseLayout("Circle", new CircleLayout()));
        layouts.add(new PrefuseLayout("Random", new RandomLayout()));
        return layouts;
    }

    // --------------------------------------------------------------------------
    // Static
    // --------------------------------------------------------------------------

    /**
     * Looks up a vertex given its delegate.
     * 
     * @param dest Node to lookup.
     * @return Vertex
     */
    public static Vertex lookupVertex(Node dest) {
        Vertex v = (Vertex) lookup_.get(dest);
        return v;
    }


    /**
     * Looks up an edge.
     * 
     * @param edge Edge to lookup.
     * @return Edge
     */
    public static Edge lookupEdge(edu.berkeley.guir.prefuse.graph.Edge edge) {
        Edge e = (Edge) lookup_.get(edge);
        return e;
    }

    // --------------------------------------------------------------------------
    // Overrides java.lang.Object
    // --------------------------------------------------------------------------

    /**
     * Overridden so name is rendered correctly in comboboxes.
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Prefuse";
    }
}