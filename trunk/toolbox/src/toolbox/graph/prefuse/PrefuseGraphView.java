package toolbox.graph.prefuse;

import javax.swing.JComponent;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.action.Action;
import edu.berkeley.guir.prefuse.action.RepaintAction;
import edu.berkeley.guir.prefuse.action.filter.GraphFilter;
import edu.berkeley.guir.prefuse.activity.ActionList;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.render.DefaultEdgeRenderer;
import edu.berkeley.guir.prefuse.render.DefaultRendererFactory;
import edu.berkeley.guir.prefuse.render.ShapeRenderer;
import edu.berkeley.guir.prefuse.render.TextImageItemRenderer;
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
    
    private ItemRegistry registry_;

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
        registry_ = new ItemRegistry(g);
        
        // set up a renderer such that nodes show text labels
        TextImageItemRenderer nodeRenderer = new TextImageItemRenderer();
        nodeRenderer.setRoundedCorner(5,5);
        nodeRenderer.setVerticalPadding(2);
        nodeRenderer.setHorizontalPadding(2);
        nodeRenderer.setRenderType(ShapeRenderer.RENDER_TYPE_DRAW_AND_FILL);
        
        DefaultEdgeRenderer edgeRenderer = new DefaultEdgeRenderer();
        edgeRenderer.setEdgeType(DefaultEdgeRenderer.EDGE_TYPE_CURVE);
        edgeRenderer.setRenderType(DefaultEdgeRenderer.RENDER_TYPE_DRAW);
        edgeRenderer.setWeightType(DefaultEdgeRenderer.WEIGHT_TYPE_LINEAR);
        
        // create a new renderer factory and associate it 
        // with the item registry
        registry_.setRendererFactory(new DefaultRendererFactory(
                nodeRenderer,
                edgeRenderer,
                null));
        
        delegate_ = new Display(registry_);
        delegate_.addControlListener(new DragControl());
        delegate_.setHighQuality(true);
        
        // create a new action list that
        // (a) filters visual representations from the original graph
        // (b) performs a random layout of graph nodes
        // (c) calls repaint on displays so that we can see the result
        
        ActionList actions = new ActionList(registry_);
        actions.add(new GraphFilter());
        //actions.add(new CircleLayout());
        //actions.add(new FruchtermanReingoldLayout());
        //actions.add(new ForceDirectedLayout(true));
        //actions.add(new GridLayout(5,5));
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
        ActionList actions = new ActionList(registry_);
        actions.add(new GraphFilter());
        actions.add((Action) layout.getDelegate());
        actions.add(new RepaintAction());
        actions.runNow();
    }
}
