package toolbox.graph.jung;

import javax.swing.JComponent;

import org.apache.log4j.Logger;

import toolbox.graph.GraphView;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.decorators.StringLabeller;
import edu.uci.ics.jung.visualization.FRLayout;
import edu.uci.ics.jung.visualization.Layout;
import edu.uci.ics.jung.visualization.PluggableRenderer;
import edu.uci.ics.jung.visualization.VisualizationViewer;

/**
 * Jung implementation of a {@link toolbox.graph.GraphView}.
 */
public class JungGraphView implements GraphView {

    private static final Logger logger_ = Logger.getLogger(JungGraphView.class);

    // --------------------------------------------------------------------------
    // Fields
    // --------------------------------------------------------------------------

    /**
     * Jung version of a GraphView.
     */
    private VisualizationViewer delegate_;

    // --------------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------------

    /**
     * Creates a JungGraphView.
     * 
     * @param graph Graph to associate with this view.
     */
    public JungGraphView(toolbox.graph.Graph graph) {

        Graph g = (Graph) graph.getDelegate();

        PluggableRenderer renderer = new PluggableRenderer();
        renderer.setVertexStringer(StringLabeller.getLabeller(g));

        // Layout layout = new SpringLayout(g);
        Layout layout = new FRLayout(g);

        delegate_ = new VisualizationViewer(layout, renderer);




        // Layout layout = new DAGLayout(g);
        // Layout layout = new SpringLayout(g);
        // Layout layout = new FadingVertexLayout(10, new SpringLayout(graph_));
        // Layout layout = new FastScalableMDS(graph_);
        // Layout layout = new CircleLayout(g);
        // Layout layout = new ISOMLayout(graph_);
        // Layout layout = new KKLayout(graph_);
        // Layout layout = new KKLayoutInt(graph_);
        // Layout layout = new AestheticSpringVisualizer(g);

        // SpringLayout layout = new SpringLayout(g);
        // layout.setRepulsionRange(200);

        // layout.initialize(new Dimension(400,400));
        // delegate_.setGraphLayout(layout);

        // delegate_.setVertexBGColor(Color.gray);
        // delegate_.setVertexForegroundColor(Color.white);


        // Renderer ------------------------------------------------------------

        // graphDraw_.setRenderer(
        // new FadeRenderer(
        // StringLabeller.getLabeller(graph_),
        // new FadingVertexLayout(20, layout)));

        // graphDraw_.setRenderer(new BasicRenderer());
        // graphDraw_.setRenderer(new PluggableRenderer());
        // SettableRenderer sr = (SettableRenderer) graphDraw_.getRenderer();

        // SettableRenderer renderer = (SettableRenderer)
        // delegate_.getRenderer();
        // PluggableRenderer renderer = (PluggableRenderer)
        // delegate_.getRenderer();
        // renderer.setLightDrawing(false);
        // renderer.setEdgeThickness(2);
        // renderer.setVertexBGColor(Colors.dark_orange);
        // srenderer.setVertexForegroundColor(Colors.whitesmoke);
        // delegate_.setBackground(Colors.light_steel_blue);
        // sr.setLightDrawing(false);
        // renderer.setEdgeColor(Colors.black);
    }

    // --------------------------------------------------------------------------
    // GraphView Interface
    // --------------------------------------------------------------------------

    /*
     * @see toolbox.graph.GraphView#getComponent()
     */
    public JComponent getComponent() {

        return delegate_;
    }


    /*
     * @see toolbox.graph.GraphView#setLayout(toolbox.graph.Layout)
     */
    public void setLayout(toolbox.graph.Layout layout) {

        Layout l = (Layout) layout.getDelegate();
        delegate_.setGraphLayout(l);

        delegate_.restart();

        // SettableRenderer renderer = (SettableRenderer)
        // delegate_.getRenderer();
        // renderer.setLightDrawing(false);
        // //renderer.setEdgeThickness(2);
        // renderer.setVertexBGColor(Colors.dark_orange);
        // //srenderer.setVertexForegroundColor(Colors.whitesmoke);
        // //delegate_.setBackground(Colors.light_steel_blue);
        // //sr.setLightDrawing(false);
        // renderer.setEdgeColor(Colors.black);

        // delegate_.repaint();
        // delegate_.resetRenderer();
        // delegate_.getVisualizationViewer().stop();

        // delegate_.stop();
        //        
        // while (delegate_.isVisRunnerRunning())
        // {
        // logger_.debug("Waiting for vis runner to stop...");
        // ThreadUtil.sleep(1000);
        // }
        //        
        // delegate_.restart();
    }
}
