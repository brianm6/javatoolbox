package toolbox.graph.jung;

import javax.swing.JComponent;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.GraphDraw;
import edu.uci.ics.jung.visualization.Layout;
import edu.uci.ics.jung.visualization.graphdraw.SettableRenderer;
import scratch.scott.AestheticSpringVisualizer;

import toolbox.graph.GraphView;
import toolbox.util.ui.Colors;


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
        
        //Layout layout = new DAGLayout(g);
        //Layout layout = new SpringLayout(g);
        //Layout layout = new FadingVertexLayout(10, new SpringLayout(graph_));
        //Layout layout = new FastScalableMDS(graph_);
        //Layout layout = new FRLayout(g);
        //Layout layout = new CircleLayout(g);
        //Layout layout = new ISOMLayout(graph_);
        //Layout layout = new KKLayout(graph_);
        //Layout layout = new KKLayoutInt(graph_);
        Layout layout = new AestheticSpringVisualizer(g);
        
        //layout.initialize(new Dimension(400,400));
        delegate_.setGraphLayout(layout);
        
        
        //delegate_.setVertexBGColor(Color.gray);
        //delegate_.setVertexForegroundColor(Color.white);
        
        
        // Renderer ------------------------------------------------------------
        
//      graphDraw_.setRenderer(
//      new FadeRenderer(
//          StringLabeller.getLabeller(graph_), 
//          new FadingVertexLayout(20, layout)));
  
        //graphDraw_.setRenderer(new BasicRenderer());
        //graphDraw_.setRenderer(new PluggableRenderer());
        //SettableRenderer sr = (SettableRenderer) graphDraw_.getRenderer();

        SettableRenderer renderer = (SettableRenderer) delegate_.getRenderer();
        renderer.setLightDrawing(false);
        //renderer.setEdgeThickness(2);
        renderer.setVertexBGColor(Colors.dark_orange);
        //srenderer.setVertexForegroundColor(Colors.whitesmoke);
        //delegate_.setBackground(Colors.light_steel_blue);
        //sr.setLightDrawing(false);
        renderer.setEdgeColor(Colors.black);
        
    }

    
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
        Layout l = (Layout) layout.getDelegate();
        delegate_.setGraphLayout(l);
    }

}
