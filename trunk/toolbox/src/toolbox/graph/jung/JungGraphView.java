package toolbox.graph.jung;

import javax.swing.JComponent;

import toolbox.graph.GraphView;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.GraphDraw;


/**
 * JungGraphView is responsible for ___.
 */
public class JungGraphView implements GraphView
{
    GraphDraw delegate_;
    
    /**
     * Creates a JungGraphView.
     * 
     */
    public JungGraphView(toolbox.graph.Graph graph)
    {
        Graph g = (Graph) graph.getDelegate();
        delegate_ = new GraphDraw(g);
    }

    
    /**
     * @see toolbox.graph.GraphView#getComponent()
     */
    public JComponent getComponent()
    {
        return delegate_;
    }
}
