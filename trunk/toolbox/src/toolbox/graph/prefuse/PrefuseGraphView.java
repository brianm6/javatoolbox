package toolbox.graph.prefuse;

import javax.swing.JComponent;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.action.RepaintAction;
import edu.berkeley.guir.prefuse.action.filter.GraphFilter;
import edu.berkeley.guir.prefuse.activity.ActionList;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefusex.controls.DragControl;
import edu.berkeley.guir.prefusex.layout.RandomLayout;

import toolbox.graph.GraphView;

/**
 * Prefuse implementation of a {@link toolbox.graph.GraphView}.
 */
public class PrefuseGraphView implements GraphView
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Prefuse version of a GraphView.
     */
    private Display delegate_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a PrefuseGraphView.
     * 
     * @param graph Graph to associate with this view.
     */
    public PrefuseGraphView(toolbox.graph.Graph graph)
    {
        Graph g = (Graph) graph.getDelegate();
        ItemRegistry registry = new ItemRegistry(g);
        
        delegate_ = new Display(registry);
        delegate_.addControlListener(new DragControl());
        delegate_.setHighQuality(true);
        
        // create a new action list that
        // (a) filters visual representations from the original graph
        // (b) performs a random layout of graph nodes
        // (c) calls repaint on displays so that we can see the result
        ActionList actions = new ActionList(registry);
        actions = new ActionList(registry);
        actions.add(new GraphFilter());
        actions.add(new RandomLayout());
        actions.add(new RepaintAction());
        actions.runNow();
    }

    //--------------------------------------------------------------------------
    // GraphView Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.graph.GraphView#getComponent()
     */
    public JComponent getComponent()
    {
        return delegate_;
    }
    
    
    /**
     * @see toolbox.graph.GraphView#setLayout(toolbox.graph.Layout)
     */
    public void setLayout(toolbox.graph.Layout layout)
    {
        // TODO
    }
}
