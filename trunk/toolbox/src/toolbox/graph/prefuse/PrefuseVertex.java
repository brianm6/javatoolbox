package toolbox.graph.prefuse;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.berkeley.guir.prefuse.graph.DefaultNode;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.Node;

import org.apache.log4j.Logger;

/**
 * Prefuse implemenation of a {@link toolbox.graph.Vertex}.
 */
public class PrefuseVertex implements toolbox.graph.Vertex
{
    private static final Logger logger_ = Logger.getLogger(PrefuseVertex.class);
 
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Prefuse vertex delegate.
     */
    private Node vertex_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a PrefuseVertex.
     * 
     * @param graph Graph to add this vertex to.
     * @param label Label to associate with this vertex. 
     */
    public PrefuseVertex(toolbox.graph.Graph graph, String label)
    {
        // Create delegate
        vertex_ = new DefaultNode();
        vertex_.setAttribute("label", label);
        
        // Add to graph
        Graph g = (Graph) graph.getDelegate();
        g.addNode(vertex_);
    }

    //--------------------------------------------------------------------------
    // Vertex Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.graph.Vertex#getEdges()
     */
    public Set getEdges()
    {
        Set result = new HashSet();
        
        for (Iterator iter = vertex_.getEdges(); iter.hasNext();)
        {
            Node node = (Node) iter.next();
            result.add(PrefuseGraphLib.lookupVertex(node));
        }
        
        return result;
    }
    
    //--------------------------------------------------------------------------
    // Delegator Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.graph.Delegator#getDelegate()
     */
    public Object getDelegate()
    {
        return vertex_;
    }
}