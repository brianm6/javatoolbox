package toolbox.graph.jung;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uci.ics.jung.visualization.FRLayout;
import edu.uci.ics.jung.visualization.ISOMLayout;
import edu.uci.ics.jung.visualization.SpringLayout;
import edu.uci.ics.jung.visualization.contrib.CircleLayout;
import edu.uci.ics.jung.visualization.contrib.DAGLayout;
import edu.uci.ics.jung.visualization.contrib.KKLayout;

import toolbox.graph.Edge;
import toolbox.graph.Graph;
import toolbox.graph.GraphLib;
import toolbox.graph.GraphView;
import toolbox.graph.Vertex;

/**
 * Jung implementation of a {@link toolbox.graph.GraphLib}.
 */
public class JungGraphLib implements GraphLib {

    // --------------------------------------------------------------------------
    // Constants
    // --------------------------------------------------------------------------

    /**
     * Lookup table for delegates.
     */
    private static final Map lookup_ = new HashMap();

    // --------------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------------

    /**
     * Creates a JungGraphLib.
     */
    public JungGraphLib() {
    }

    // --------------------------------------------------------------------------
    // GraphLib Interface
    // --------------------------------------------------------------------------

    /*
     * @see toolbox.graph.GraphLib#createEdge(toolbox.graph.Vertex,
     *      toolbox.graph.Vertex)
     */
    public Edge createEdge(Vertex from, Vertex to) {
        Edge e = new JungEdge(from, to);
        lookup_.put(e.getDelegate(), e);
        return e;
    }


    /*
     * @see toolbox.graph.GraphLib#createGraph()
     */
    public Graph createGraph() {
        Graph g = new JungGraph();
        return g;
    }


    /*
     * @see toolbox.graph.GraphLib#createVertex(toolbox.graph.Graph,
     *      java.lang.String)
     */
    public Vertex createVertex(Graph graph, String label) {
        Vertex v = new JungVertex(graph, label);
        lookup_.put(v.getDelegate(), v);
        return v;
    }

    /*
     * @see toolbox.graph.GraphLib#createView(toolbox.graph.Graph)
     */
    public GraphView createView(Graph graph) {
        GraphView view = new JungGraphView(graph);
        return view;
    }


    /*
     * @see toolbox.graph.GraphLib#getLayouts(toolbox.graph.Graph)
     */
    public List getLayouts(Graph graph) {
        edu.uci.ics.jung.graph.Graph g = 
            (edu.uci.ics.jung.graph.Graph) graph.getDelegate();

        List layouts = new ArrayList();
        layouts.add(new JungLayout("Circle", new CircleLayout(g)));
        layouts.add(new JungLayout("Spring", new SpringLayout(g)));
        layouts.add(new JungLayout("FR", new FRLayout(g)));

        DAGLayout dag = new DAGLayout(g);
        dag.setForceMultiplier(1);
        dag.setRepulsionRange(100);
        dag.setStretch(1);

        layouts.add(new JungLayout("DAG", dag));
        layouts.add(new JungLayout("KK", new KKLayout(g)));
        layouts.add(new JungLayout("ISOM", new ISOMLayout(g)));
        return layouts;
    }

    // --------------------------------------------------------------------------
    // Static
    // --------------------------------------------------------------------------

    /**
     * Looks up a vertex.
     * 
     * @param dest Destination vertex.
     * @return Vertex
     */
    public static Vertex lookupVertex(edu.uci.ics.jung.graph.Vertex dest) {
        Vertex v = (Vertex) lookup_.get(dest);
        return v;
    }


    /**
     * Returns the edge for the specified jung graph edge.
     * 
     * @param edge Edge to lookup.
     * @return Edge
     */
    public static Edge lookupEdge(edu.uci.ics.jung.graph.Edge edge) {
        Edge e = (Edge) lookup_.get(edge);
        return e;
    }

    // --------------------------------------------------------------------------
    // Overrides java.lang.Object
    // --------------------------------------------------------------------------

    /*
     * Overridden so name is rendered correctly in comboboxes.
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Jung";
    }
}