package toolbox.graph.prefuse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.berkeley.guir.prefuse.graph.Node;

import toolbox.graph.Edge;
import toolbox.graph.Graph;
import toolbox.graph.GraphLib;
import toolbox.graph.GraphView;
import toolbox.graph.Vertex;

/**
 * Prefuse implemenatation of a {@link toolbox.graph.GraphLib}.
 */
public class PrefuseGraphLib implements GraphLib
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a PrefuseGraphFactory.
     */
    public PrefuseGraphLib()
    {
    }

    //--------------------------------------------------------------------------
    // GraphLib Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.graph.GraphLib#createEdge(toolbox.graph.Vertex, 
     *      toolbox.graph.Vertex)
     */
    public Edge createEdge(Vertex from, Vertex to)
    {
        Edge e = new PrefuseEdge(from, to);
        return e;
    }
    
    
    /**
     * @see toolbox.graph.GraphLib#createGraph()
     */
    public Graph createGraph()
    {
        Graph g = new PrefuseGraph();
        return g;
    }
    
    
    /**
     * @see toolbox.graph.GraphLib#createVertex(toolbox.graph.Graph, 
     *      java.lang.String)
     */
    public Vertex createVertex(Graph graph, String label)
    {
        Vertex v = new PrefuseVertex(graph, label);
        return v;
    }
    
    
    /**
     * @see toolbox.graph.GraphLib#createView(toolbox.graph.Graph)
     */
    public GraphView createView(Graph graph)
    {
        GraphView view = new PrefuseGraphView(graph);
        return view;
    }
    
    
    /**
     * @see toolbox.graph.GraphLib#getLayouts()
     */
    public List getLayouts(Graph graph)
    {
        edu.berkeley.guir.prefuse.graph.Graph g = 
            (edu.berkeley.guir.prefuse.graph.Graph) graph.getDelegate();
        
        List layouts = new ArrayList();
        
        // TODO
        //layouts.add(new PrefuseLayout(new CircleLayout(g))); 
        //layouts.add(new PrefuseLayout(new SpringLayout(g)));
        return layouts;
    }
    
    //--------------------------------------------------------------------------
    // Static
    //--------------------------------------------------------------------------
    
    /**
     * @param dest
     * @return
     */
    public static Vertex lookupVertex(Node dest)
    {
        Vertex v = (Vertex) lookup_.get(dest);
        return v;
    }

    
    /**
     * @param dest
     * @return
     */
    public static Edge lookupEdge(edu.berkeley.guir.prefuse.graph.Edge edge)
    {
        Edge e = (Edge) lookup_.get(edge);
        return e;
    }

    private static final Map lookup_ = new HashMap();
    
}