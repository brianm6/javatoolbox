package toolbox.graph.jung;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.StringLabeller;
import edu.uci.ics.jung.graph.decorators.StringLabeller.UniqueLabelException;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;

import org.apache.log4j.Logger;

/**
 * Jung implemenation of a {@link toolbox.graph.Vertex}.
 */
public class JungVertex implements toolbox.graph.Vertex
{
    private static final Logger logger_ = Logger.getLogger(JungVertex.class);
 
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Jung vertex delegate.
     */
    private Vertex vertex_;
    
    /**
     * Graph associated with this vertex.
     */
    private Graph graph_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a JungVertex.
     * 
     * @param graph Graph to add this vertex to.
     * @param label Label to associate with this vertex. 
     */
    public JungVertex(toolbox.graph.Graph graph, String label)
    {
        vertex_ = new DirectedSparseVertex();
        graph_ = (Graph) graph.getDelegate();
        setText(label);
    }

    //--------------------------------------------------------------------------
    // Vertex Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.graph.Vertex#getEdges()
     */
    public Set getEdges()
    {
        Set edges = vertex_.getIncidentEdges();
        Set result = new HashSet();
        
        for (Iterator iter = edges.iterator(); iter.hasNext();)
        {
            Vertex element = (Vertex) iter.next();
            result.add(JungGraphLib.lookupVertex(element));
        }
        
        return result;
    }
    
    
    /**
     * @see toolbox.graph.Vertex#getText()
     */
    public String getText()
    {
        return StringLabeller.getLabeller(graph_).getLabel(vertex_);
    }
    
    
    /**
     * @see toolbox.graph.Vertex#setText(java.lang.String)
     */
    public void setText(String text)
    {
        boolean remove = false;
        
        if (!graph_.getVertices().contains(vertex_))
        {
            // Temporarily add to graph so that StringLabeller will work
            graph_.addVertex(vertex_);
            remove = true;
        }
        
        try
        {
            StringLabeller.getLabeller(graph_).setLabel(vertex_, text);
        }
        catch (UniqueLabelException e)
        {
            logger_.error(e);
        }

        if (remove)
            graph_.removeVertex(vertex_);
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