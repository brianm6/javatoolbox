package toolbox.plugin.jardeps;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

import nu.xom.Element;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import toolbox.graph.Edge;
import toolbox.graph.Graph;
import toolbox.graph.GraphFactory;
import toolbox.graph.GraphView;
import toolbox.graph.Vertex;
import toolbox.graph.jung.JungGraphFactory;
import toolbox.util.FileUtil;
import toolbox.util.XOMUtil;
import toolbox.util.service.ServiceException;
import toolbox.util.service.ServiceTransition;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.explorer.JFileExplorer;
import toolbox.util.ui.flippane.JFlipPane;
import toolbox.workspace.AbstractPlugin;
import toolbox.workspace.IStatusBar;
import toolbox.workspace.PluginWorkspace;
import toolbox.workspace.PreferencedException;
import toolbox.workspace.WorkspaceAction;

/**
 * Visualizes jar file dependencies. 
 */ 
public class JarDepsPlugin extends AbstractPlugin
{
    private static final Logger logger_ = 
        Logger.getLogger(JarDepsPlugin.class);
    
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    /** 
     * Root preferences node for this plugin.
     */
    private static final String NODE_JARDEPS_PLUGIN = "JarDepsPlugin2";
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * View for this plugin.
     */
    private JComponent view_;
    
    /** 
     * Flip panel that houses the file explorer. 
     */
    private JFlipPane flipPane_;    
    
    /** 
     * Shared status bar with plugin host.
     */
    private IStatusBar statusBar_;
    
    /** 
     * Work area.
     */
    private JPanel workArea_;
    
    /** 
     * File explorer used to navigate to and select documents for viewing. 
     */
    private JFileExplorer explorer_;

    private GraphView graphView_;
    private Graph graph_;
    private GraphFactory graphFactory_ = new JungGraphFactory();

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a JarDepsPlugin.
     */
    public JarDepsPlugin()
    {
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Constructs the user interface.
     */
    protected void buildView()
    {
        view_ = new JPanel(new BorderLayout());
        explorer_ = new JFileExplorer(false);
        flipPane_ = new JFlipPane(JFlipPane.LEFT);
        flipPane_.addFlipper(JFileExplorer.ICON, "File Explorer", explorer_);
        workArea_ = new JPanel(new BorderLayout());
        view_.add(BorderLayout.CENTER, workArea_);
        view_.add(BorderLayout.WEST, flipPane_);
        view_.add(BorderLayout.SOUTH, new JSmartButton(new ViewJarDepsAction()));
    }
    

    /**
     * Views a jar dependencies.
     * 
     * @param files List of jar, directories to view dependencies for.
     */
    protected void viewDependencies(List files) 
    {
        String cp = "\\lib\\junit.jar:\\lib\\commons-io.jar:\\lib\\commons-lang.jar:\\lib\\commons-beanutils.jar:\\lib\\commons-math.jar:\\lib\\commons-dbcp.jar:\\lib\\commons-logging.jar:\\lib\\log4j.jar:\\lib\\hsqldb.jar:\\lib\\acrobat.jar:";
        cp = cp + "\\lib\\ant.jar:\\lib\\batik.jar:\\lib\\bsh.jar:\\lib\\classworlds.jar:\\lib\\commons-cli.jar:\\lib\\commons-codec.jar:\\lib\\commons-collections.jar:\\lib\\commons-net.jar:\\lib\\commons-pool.jar:\\lib\\cvslib.jar:\\lib\\fop.jar:\\lib\\forms.jar:\\lib\\hamsam.jar:\\lib\\jakarta-oro.jar:\\lib\\jakarta-regexp.jar:\\lib\\jalopy.jar:\\lib\\janino.jar:\\lib\\javassist.jar:\\lib\\jaxen.jar:\\lib\\jcommon.jar:\\lib\\jdom.jar:\\lib\\jemmy.jar:\\lib\\jfreechart.jar:\\lib\\jode.jar:\\lib\\jsap.jar:\\lib\\jtidy.jar:\\lib\\junit-addons.jar:\\lib\\looks.jar:\\lib\\matra.jar:\\lib\\multivalent.jar:\\lib\\pollo.jar:\\lib\\qdox.jar:\\lib\\saxon.jar:\\lib\\statcvs.jar:\\lib\\util-concurrent.jar:\\lib\\xep.jar:\\lib\\xercesimpl.jar:\\lib\\xml-apis.jar:\\lib\\xom.jar:\\lib\\xt.jar:\\lib\\webwindow.jar";
        
        try
        {
//            Main.main(new String[] {
//                "--find",
//                "--level=jar",
//                cp
//            });
            
            StringWriter sw = new StringWriter();
            
//            Main m = new Main();
//            m.setLevel(DepHandler.LEVEL_JAR);
//            m.find(cp, cp, sw);
//            
//            logger_.info(sw.toString());
            
//            LineNumberReader lnr = 
//                new LineNumberReader(new StringReader(sw.toString()));

            LineNumberReader lnr = 
                new LineNumberReader(new StringReader(                 
            "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\commons-beanutils.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\commons-collections.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\commons-beanutils.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\commons-logging.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\commons-dbcp.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\commons-pool.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\commons-dbcp.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\commons-collections.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\commons-dbcp.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\xml-apis.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\commons-logging.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\log4j.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\log4j.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\xml-apis.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\ant.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\xml-apis.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\ant.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\multivalent.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\batik.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\xml-apis.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\commons-cli.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\commons-lang.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\commons-net.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\jakarta-oro.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\commons-pool.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\commons-collections.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\fop.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\xml-apis.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\fop.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\batik.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\fop.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\ant.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\jalopy.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\log4j.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\jalopy.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\jakarta-oro.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\jalopy.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\jdom.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\janino.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\ant.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\jaxen.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\xml-apis.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\jaxen.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\jdom.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\jcommon.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\xml-apis.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\jdom.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\xml-apis.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\jfreechart.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\jcommon.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\jfreechart.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\xml-apis.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\jsap.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\ant.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\jtidy.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\xml-apis.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\jtidy.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\ant.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\jtidy.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\xercesimpl.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\junit-addons.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\junit.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\junit-addons.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\jdom.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\junit-addons.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\jaxen.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\junit-addons.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\ant.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\pollo.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\xercesimpl.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\pollo.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\xml-apis.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\pollo.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\log4j.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\pollo.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\ant.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\pollo.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\jaxen.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\pollo.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\commons-lang.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\qdox.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\ant.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\qdox.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\junit.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\saxon.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\xml-apis.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\statcvs.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\ant.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\statcvs.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\jfreechart.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\statcvs.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\jcommon.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\xep.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\xml-apis.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\xep.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\junit.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\xep.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\xt.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\xercesimpl.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\xml-apis.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\xom.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\xml-apis.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\xom.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\xercesimpl.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\xom.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\junit.jar\n"
            + "C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\xt.jar -> C:\\workspaces\\workspace-toolbox\\toolbox\\lib\\xml-apis.jar\n"));
            
            String line = null;
            
            
            Map nodes = new HashMap();
            
            graph_ = graphFactory_.createGraph();
            
            while ( (line = lnr.readLine()) !=  null)
            {
                String[] tokens = StringUtils.split(line);
                Validate.isTrue(tokens.length == 3);
                
                String fromLabel = FileUtil.stripPath(tokens[0]);
                String toLabel = FileUtil.stripPath(tokens[2]);
                
                Vertex fromVertex = null;
                Vertex toVertex = null;
                
                if (!nodes.containsKey(fromLabel))
                {
                    fromVertex = graphFactory_.createVertex(graph_, fromLabel);
                    nodes.put(fromLabel, fromVertex);
                }
                else
                {
                    fromVertex = (Vertex) nodes.get(fromLabel);
                }
                    
                
                if (!nodes.containsKey(toLabel))
                {
                    toVertex = graphFactory_.createVertex(graph_, toLabel);
                    nodes.put(toLabel, toVertex);
                }
                else
                {
                    toVertex = (Vertex) nodes.get(toLabel);
                }
                
                Edge edge = graphFactory_.createEdge(fromVertex, toVertex);
            }
            
            graphView_ = graphFactory_.createView(graph_);
        }
        catch (Exception e)
        {
            logger_.error(e);
        }
    }
    
    
    private void showGraph()
    {
        //graphView_ = new GraphDraw(graph_);
        //graphView_.showStatus();
    
        
//        graphDraw_.setRenderer(
//            new FadeRenderer(
//                StringLabeller.getLabeller(graph_), 
//                new FadingVertexLayout(20, layout)));
        
        //graphDraw_.setRenderer(new BasicRenderer());
        
        //graphDraw_.setRenderer(new PluggableRenderer());
                
        //SettableRenderer sr = (SettableRenderer) graphDraw_.getRenderer();
//        SettableRenderer sr = 
//            new SettableRenderer(
//                StringLabeller.getLabeller(graph_),
//                new EdgeStringer()
//                {
//                    /**
//                     * @see edu.uci.ics.jung.graph.decorators.EdgeStringer#getLabel(edu.uci.ics.jung.graph.Edge)
//                     */
//                    public String getLabel(Edge e)
//                    {
//                        return "depends on";
//                    }
//                });
//        
//        graphView_.setRenderer(sr);
//                
//        sr.setLightDrawing(false);
//        sr.setEdgeColor(Color.BLUE);
        
        
        //sr.setEdgeThickness(2);
        
        //Layout layout = new DAGLayout(graph_);
        //Layout layout = new SpringLayout(graph_);
        //Layout layout = new FadingVertexLayout(10, new SpringLayout(graph_));
        //Layout layout = new FastScalableMDS(graph_);
        //Layout layout = new FRLayout(graph_);
        //Layout layout = new CircleLayout(graph_);
        //Layout layout = new ISOMLayout(graph_);
        //Layout layout = new KKLayout(graph_);
        //Layout layout = new KKLayoutInt(graph_);
        //Layout layout = new AestheticSpringVisualizer(graph_);
        
        //graphView_.setGraphLayout(layout);
        //graphView_.setVertexBGColor(Color.gray);
        //graphView_.setVertexForegroundColor(Color.white);
        
        //SpringLayout sl = (SpringLayout) graphDraw_.getGraphLayout();
        //sl.setRepulsionRange(200);
        //sl.setForceMultiplier(23);
        //sl.setStretch(21);
        //sl.update();
        //sl.restart();
        
        //gd.stop();
        //gd.restartLayout();
        
        //workArea_.add(BorderLayout.CENTER, new JScrollPane(graphView_));
    }

//    public Graph getGraph() throws IOException {
//        Graph g = new DirectedSparseGraph();
//        return g;
//    }
    
    
    
    /**
     * 
     */
//    private Vertex addVertex(String label) //throws UniqueLabelException
//    {
//        logger_.debug("Adding vertex " + label);
//        
//        Vertex vertex = graphFactory_.createVertex(graph_, label); 
//        //graph_.addVertex(vertex);
//        //StringLabeller.getLabeller(graph_).setLabel(vertex, label);
//        return vertex;
//    }
//    
//    private void addEdge(Vertex from, Vertex to)
//    {
//        logger_.debug("Adding edge " + from + " ---> " + to);
//        
//        Edge edge = graphFactory_.createEdge(from, to);
//        graph_.addEdge(edge);
//    }
    
    //--------------------------------------------------------------------------
    // Initializable Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.util.service.Initializable#initialize(java.util.Map)
     */
    public void initialize(Map params) throws ServiceException
    {
        checkTransition(ServiceTransition.INITIALIZE);
        
        if (params != null)
            statusBar_ = (IStatusBar) 
                params.get(PluginWorkspace.KEY_STATUSBAR);
        
        buildView();
        transition(ServiceTransition.INITIALIZE);
    }

    //--------------------------------------------------------------------------
    // IPlugin Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.workspace.IPlugin#getPluginName()
     */
    public String getPluginName()
    {
        return "Jar Deps 2";
    }

    
    /**
     * @see toolbox.workspace.IPlugin#getView()
     */
    public JComponent getView()
    {
        return view_;
    }

    
    /**
     * @see toolbox.workspace.IPlugin#getDescription()
     */
    public String getDescription()
    {
        return "Visualizes jar dependencies.";
    }

    //--------------------------------------------------------------------------
    // Destroyable Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.service.Destroyable#destroy()
     */
    public void destroy() throws ServiceException
    {
        checkTransition(ServiceTransition.DESTROY);
        transition(ServiceTransition.DESTROY);
    }
    
    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws PreferencedException
    {
        Element root = 
            XOMUtil.getFirstChildElement(
                prefs, 
                NODE_JARDEPS_PLUGIN, 
                new Element(NODE_JARDEPS_PLUGIN));    
        
        explorer_.applyPrefs(root);
        flipPane_.applyPrefs(root);
    }

    
    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws PreferencedException
    {
        Element root = new Element(NODE_JARDEPS_PLUGIN);
        explorer_.savePrefs(root);
        flipPane_.savePrefs(root);
        XOMUtil.insertOrReplace(prefs, root);
    }

    //--------------------------------------------------------------------------
    // ViewAction
    //--------------------------------------------------------------------------
    
    /**
     * Action to view a document.
     */
    class ViewJarDepsAction extends WorkspaceAction
    {
        public ViewJarDepsAction()
        {
            super("View Jar Dependencies", true, true, view_, statusBar_);
        }
                
        
        public void runAction(ActionEvent e) throws Exception
        {
            viewDependencies(Arrays.asList(
                explorer_.getFileList().getSelectedValues()));
            
            workArea_.add(BorderLayout.CENTER, graphView_.getComponent());
        }
    }
}