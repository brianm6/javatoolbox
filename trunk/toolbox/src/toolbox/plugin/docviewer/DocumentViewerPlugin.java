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
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import nu.xom.Element;

import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;
import toolbox.util.ClassUtil;
import toolbox.util.ExceptionUtil;
import toolbox.util.XOMUtil;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartSplitPane;
import toolbox.util.ui.SmartAction;
import toolbox.util.ui.explorer.FileExplorerAdapter;
import toolbox.util.ui.explorer.JFileExplorer;
import toolbox.util.ui.flippane.JFlipPane;
import toolbox.util.ui.layout.StackLayout;
import toolbox.workspace.IPlugin;
import toolbox.workspace.IStatusBar;
import toolbox.workspace.PluginWorkspace;

/**
 * Document Viewer Plugin.
 */ 
public class DocumentViewerPlugin extends JPanel implements IPlugin
{
    private static final Logger logger_ = 
        Logger.getLogger(DocumentViewerPlugin.class);
    
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------

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
    private JPanel viewerPane_;
    
    /**
     * List of viewers registered to show documents.
     */
    private List viewers_;
    
    /**
     * Last document viewer that was active.
     */
    private JComponent lastActive_;
    
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
        viewerPane_ = new JPanel(new StackLayout(StackLayout.VERTICAL));
        
        JSplitPane splitter = 
            new JSmartSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                explorer_,
                new JScrollPane(viewerPane_));
        
        flipPane_ = new JFlipPane(JFlipPane.LEFT);
        flipPane_.addFlipper("File Explorer", splitter);
        
        outputPanel_ = new JPanel(new BorderLayout());
        
        add(BorderLayout.CENTER, outputPanel_);
        add(BorderLayout.WEST, flipPane_);
    }
    

    /**
     * Views a PDF using an embedded java pdf viewer.
     * 
     * @param file File to view with the embedded PDF viewer
     * @throws Exception on error
     */
    protected void viewDocument(DocumentViewer dv, File f) throws Exception
    {
        if (lastActive_ != null)
            outputPanel_.remove(lastActive_);
       
        dv.view(f);
        lastActive_ = dv.getComponent();
        
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
        viewerPane_.removeAll();
        
        for (Iterator i = viewers_.iterator(); i.hasNext(); )
        {
            DocumentViewer dv = (DocumentViewer) i.next();
            
            if (dv.canView(file))
            {
                JSmartButton b = new JSmartButton(new ViewAction(dv, file));
                viewerPane_.add("Top Wide Flush", b);
            }
        }
        
        viewerPane_.validate();
        viewerPane_.repaint();
    }
    
    
    /**
     * Builds the list of document viewers by scanning the current package for
     * classes ending in the string "Viewer".
     */
    protected void buildViewerList()
    {
        String[] classes = 
            ClassUtil.getClassesInPackage(
                ClassUtil.stripClass(getClass().getName()));
       
        logger_.info("Viewers: " + ArrayUtil.toString(classes, true)); 
                
        for (int i=0; i<classes.length; i++)
        {
            String fqcn = classes[i];
            
            if (fqcn.endsWith("Viewer"))
            {
                try
                {
                    Class c = Class.forName(fqcn);
                    
                    if (!c.isInterface())
                        if (DocumentViewer.class.isAssignableFrom(c))
                        {
                            logger_.info("fcqn is a doc viewer: " + fqcn);
                            
                            DocumentViewer viewer = (DocumentViewer) c.newInstance();
                            viewer.startup(new HashMap());
                            viewers_.add(viewer);
                        }
                }
                catch (Exception e)
                {
                    logger_.warn(e);
                }
            }
        }
    }
    
    //--------------------------------------------------------------------------
    // IPlugin Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.workspace.IPlugin#startup(java.util.Map)
     */
    public void startup(Map params)
    {
        if (params != null)
            statusBar_= (IStatusBar) params.get(PluginWorkspace.PROP_STATUSBAR);
        
        viewers_ = new ArrayList();
        buildView();
        buildViewerList();
    }

    
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

    
    /**
     * @see toolbox.workspace.IPlugin#shutdown()
     */
    public void shutdown()
    {
        for (Iterator i = viewers_.iterator(); i.hasNext(); )
        {
            try
            {
                DocumentViewer dv = (DocumentViewer) i.next();
                dv.shutdown();
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
    public void savePrefs(Element prefs)
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
    class ViewAction extends SmartAction
    {
        DocumentViewer viewer_;
        File file_;
        
        public ViewAction(DocumentViewer viewer, File file)
        {
            super(viewer.getName(), true, false, null);
            viewer_ = viewer;
            file_ = file;
        }
                
        
        public void runAction(ActionEvent e) throws Exception
        {
            viewDocument(viewer_, file_);
        }
    }
    
    //--------------------------------------------------------------------------
    // FileSelectionListener
    //--------------------------------------------------------------------------
    
    /**
     * Populates file that is double clicked on in the text area.
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
                findViewersForDocument(new File(file));
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
            logger_.info("File selected: " + file);
            
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