package toolbox.plugin.docviewer;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Elements;

import org.apache.commons.lang.ClassUtils;
import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;
import toolbox.util.ClassUtil;
import toolbox.util.ExceptionUtil;
import toolbox.util.FileUtil;
import toolbox.util.ResourceUtil;
import toolbox.util.XOMUtil;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.explorer.FileExplorerAdapter;
import toolbox.util.ui.explorer.JFileExplorer;
import toolbox.util.ui.flippane.JFlipPane;
import toolbox.util.ui.layout.StackLayout;
import toolbox.workspace.IPlugin;
import toolbox.workspace.IStatusBar;
import toolbox.workspace.PluginWorkspace;
import toolbox.workspace.WorkspaceAction;

/**
 * DocumentViewerPlugin is a plugin specifically targeted towards viewing 
 * different types and formats of files. Viewers must implement the 
 * DocumentViewer interface and register themselves with the plugin.
 * 
 * @see toolbox.plugin.docviewer.DocumentViewer
 */ 
public class DocumentViewerPlugin extends JPanel implements IPlugin
{
    private static final Logger logger_ = 
        Logger.getLogger(DocumentViewerPlugin.class);
    
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    /**
     * File containing hard coded list of doc viewers just in case runtime
     * introspection doesn't work under Webstart.
     * <pre>
     *  
     * &lt;docviewers&gt;
     *   &lt;docviewer name=&quot;Widget Viewer&quot; class=&quot;org.viewer.WidgetViewer&quot;/&gt;
     *   &lt;docviewer ... /&gt;
     * &lt;/docviewers&gt;
     * 
     * </pre>
     */
    private static final String FILE_DOCVIEWERS = 
        "/toolbox/plugin/docviewer/docviewers.xml";
    
    // XML Preferences nodes and attributes. 
    private static final String NODE_DOCVIEWER = "docviewer";
    private static final String     ATTR_NAME  = "name";
    private static final String     ATTR_CLASS = "class";
    
    /** 
     * Root preferences node for this plugin.
     */
    private static final String NODE_DOCVIEWER_PLUGIN = "DocumentViewerPlugin";
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /** 
     * Flip panel that houses the file explorer. 
     */
    private JFlipPane flipPane_;    
    
    /** 
     * Shared status bar with plugin host.
     */
    private IStatusBar statusBar_;
    
    /** 
     * XML output pane.
     */
    private JPanel outputPanel_;
    
    /** 
     * File explorer used to navigate to and select documents for viewing. 
     */
    private JFileExplorer explorer_;

    /**
     * Panel that contains a list of stacked buttons. One button for each
     * viewer that is capable for viewing the selecting document. 
     */
    private JPanel viewerButtons_;
    
    /**
     * List of viewers registered to show documents.
     */
    private List viewers_;
    
    /**
     * Last document viewer that was active.
     */
    private JComponent lastActive_;
    
    /**
     * Last viewer that was used to view a document with a given extension.
     */
    private Map lastViewedWith_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a DocumentViewerPlugin.
     */
    public DocumentViewerPlugin()
    {
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Builds the GUI.
     */
    protected void buildView()
    {
        setLayout(new BorderLayout());
        
        explorer_ = new JFileExplorer(false);
        explorer_.addFileExplorerListener(new FileSelectionListener());
        viewerButtons_ = new JPanel(new StackLayout(StackLayout.VERTICAL));

        JPanel splitter = new JPanel(new BorderLayout());
        splitter.add(explorer_, BorderLayout.CENTER);
        splitter.add(viewerButtons_, BorderLayout.SOUTH);
        
        flipPane_ = new JFlipPane(JFlipPane.LEFT);
        flipPane_.addFlipper(JFileExplorer.ICON, "File Explorer", splitter);
        
        outputPanel_ = new JPanel(new BorderLayout());
        
        add(BorderLayout.CENTER, outputPanel_);
        add(BorderLayout.WEST, flipPane_);
    }
    

    /**
     * Views a document.
     * 
     * @param viewer Document viewer.
     * @param file File to view with the embedded PDF viewer.
     * @throws Exception on error.
     */
    protected void viewDocument(DocumentViewer viewer, File file) 
        throws Exception
    {
        if (lastActive_ != null)
            outputPanel_.remove(lastActive_);
       
        viewer.view(file);
        lastActive_ = viewer.getComponent();
        
        lastViewedWith_.put(
            FileUtil.getExtension(file).toLowerCase(),
            viewer);
        
        outputPanel_.add(BorderLayout.CENTER, lastActive_);
        outputPanel_.validate();
        outputPanel_.repaint();
    }

    
    /**
     * Finds all valid document viewers for the given file.
     * 
     * @param file File to find a document viewer for.
     */
    protected void findViewersForDocument(File file)
    {
        viewerButtons_.removeAll();
        
        for (Iterator i = viewers_.iterator(); i.hasNext();)
        {
            DocumentViewer dv = (DocumentViewer) i.next();
            
            if (dv.canView(file))
            {
                JSmartButton b = new JSmartButton(new ViewAction(dv, file));
                viewerButtons_.add("Top Wide Flush", b);
            }
        }
        
        viewerButtons_.validate();
        viewerButtons_.repaint();
    }
    
    
    /**
     * Builds the list of document viewers by scanning the current package for
     * classes ending in the string "Viewer" and not containing the string
     * "Abstract".
     */
    protected void buildViewerList()
    {
        // The blessed list of doc viewer classes
        List classList = new ArrayList();
        
        String[] classes = ClassUtil.getClassesInPackage(
            ClassUtils.getPackageName(getClass().getName()));
       
        logger_.info("Viewers: " + ArrayUtil.toString(classes, true)); 
        
        for (int i = 0; i < classes.length; i++)
        {
            String fqcn = classes[i];
            
            if (fqcn.endsWith("Viewer") && fqcn.indexOf("Abstract") < 0)
            {
                try
                {
                    Class c = Class.forName(fqcn);
                    
                    if (!c.isInterface())
                        if (DocumentViewer.class.isAssignableFrom(c))
                            classList.add(c);
                }
                catch (Exception e)
                {
                    logger_.warn(e);
                }
            }
        }
        
        // If no viewers were found, then we're probably running as a WebStart
        // app in which case ClassUtil.getClassesInPackage() is not going to
        // work. Have to use the hardcoded list of viewers instead.
        
        if (classList.isEmpty())
        {
            try
            {
                Element root = new Builder().build(
                    ResourceUtil.getResource(FILE_DOCVIEWERS)).getRootElement();
                
                logger_.debug(XOMUtil.toXML(root));
                
                Elements docviewers = root.getChildElements(NODE_DOCVIEWER);
                
                for (int i = 0, s = docviewers.size(); i < s; i++)
                {
                    Element docviewer = docviewers.get(i);
                    String clazz = docviewer.getAttributeValue(ATTR_CLASS);
                    classList.add(Class.forName(clazz));    
                }
                
            }
            catch (Exception ex)
            {
                ExceptionUtil.handleUI(ex, logger_);
            }
        }
        
        // Take the class list and turn them into doc viewers
        
        for (int i = 0, j = classList.size(); i < j; i++)
        {
            try
            {
                Class c = (Class) classList.get(i);
                DocumentViewer viewer = (DocumentViewer) c.newInstance();
                viewer.initialize(new HashMap());
                viewers_.add(viewer);
            }
            catch (Exception e)
            {
                ExceptionUtil.handleUI(e, logger_);
            }
        }
    }
    
    //--------------------------------------------------------------------------
    // Initializable Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.util.service.Initializable#initialize(java.util.Map)
     */
    public void initialize(Map params)
    {
        if (params != null)
            statusBar_ = (IStatusBar) 
                params.get(PluginWorkspace.KEY_STATUSBAR);
        
        viewers_ = new ArrayList();
        lastViewedWith_ = new HashMap();
        buildView();
        buildViewerList();
    }

    //--------------------------------------------------------------------------
    // IPlugin Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.workspace.IPlugin#getPluginName()
     */
    public String getPluginName()
    {
        return "Document Viewer";
    }

    
    /**
     * @see toolbox.workspace.IPlugin#getComponent()
     */
    public JComponent getComponent()
    {
        return this;
    }

    
    /**
     * @see toolbox.workspace.IPlugin#getDescription()
     */
    public String getDescription()
    {
        return "Views documents.";
    }

    //--------------------------------------------------------------------------
    // Destroyable Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.service.Destroyable#destroy()
     */
    public void destroy()
    {
        for (Iterator i = viewers_.iterator(); i.hasNext();)
        {
            try
            {
                DocumentViewer dv = (DocumentViewer) i.next();
                dv.destroy();
                dv = null;
            }
            catch (Exception e)
            {
                logger_.warn(e);
            }
        }
        
        viewers_.clear();
        viewers_ = null;
    }
    
    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws Exception
    {
        Element root = 
            XOMUtil.getFirstChildElement(
                prefs, 
                NODE_DOCVIEWER_PLUGIN, 
                new Element(NODE_DOCVIEWER_PLUGIN));    
        
        explorer_.applyPrefs(root);
        flipPane_.applyPrefs(root);
    }

    
    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws Exception
    {
        Element root = new Element(NODE_DOCVIEWER_PLUGIN);
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
    class ViewAction extends WorkspaceAction
    {
        /**
         * Document viewer used to view the file.
         */
        private DocumentViewer viewer_;
        
        /**
         * File to view.
         */
        private File file_;
        
        
        /**
         * Creates a ViewAction.
         * 
         * @param viewer Document viewer.
         * @param file File to view.
         */
        public ViewAction(DocumentViewer viewer, File file)
        {
            super(viewer.getName(), true, true, getParent(), statusBar_);
            viewer_ = viewer;
            file_ = file;
        }
                
        
        /**
         * @see toolbox.util.ui.SmartAction#runAction(
         *      java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            viewDocument(viewer_, file_);
        }
    }
    
    //--------------------------------------------------------------------------
    // FileSelectionListener
    //--------------------------------------------------------------------------
    
    /**
     * Populates file that is double clicked on in the file explorer.
     */
    class FileSelectionListener extends FileExplorerAdapter
    {
        /**
         * @see toolbox.util.ui.explorer.FileExplorerListener#fileDoubleClicked(
         *      java.lang.String)
         */
        public void fileDoubleClicked(String file)
        {
            try
            {
                File f = new File(file);
                String ext = FileUtil.getExtension(f).toLowerCase();
                
                DocumentViewer viewer = 
                    (DocumentViewer) lastViewedWith_.get(ext);
                
                if (viewer != null)
                {    
                    new ViewAction(viewer, f).actionPerformed(
                        new ActionEvent(explorer_, 1, "view"));
                }
                else
                {
                    statusBar_.setInfo("You must select a viewer.");
                    findViewersForDocument(f);
                }
            }
            catch (Exception fnfe)
            {
                ExceptionUtil.handleUI(fnfe, logger_);
            }
        }
        
        
        /**
         * @see toolbox.util.ui.explorer.FileExplorerListener#fileSelected(
         *      java.lang.String)
         */
        public void fileSelected(String file)
        {
            logger_.debug("File selected: " + file);
            
            try
            {
                findViewersForDocument(new File(file));
            }
            catch (Exception fnfe)
            {
                ExceptionUtil.handleUI(fnfe, logger_);
            }
        }
    }
}