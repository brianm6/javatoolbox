package toolbox.plugin.pdf;

import java.awt.BorderLayout;
import java.io.File;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import nu.xom.Element;

import org.apache.log4j.Logger;

import toolbox.util.ExceptionUtil;
import toolbox.util.XOMUtil;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartSplitPane;
import toolbox.util.ui.explorer.FileExplorerAdapter;
import toolbox.util.ui.explorer.JFileExplorer;
import toolbox.util.ui.flippane.JFlipPane;
import toolbox.util.ui.layout.StackLayout;
import toolbox.workspace.IPlugin;
import toolbox.workspace.IStatusBar;
import toolbox.workspace.PluginWorkspace;

/**
 * PDF Viewer Plugin.
 */ 
public class PDFPlugin extends JPanel implements IPlugin
{
    private static final Logger logger_ = 
        Logger.getLogger(PDFPlugin.class);
    
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------

    /** 
     * Root preferences node for this plugin.
     */
    private static final String NODE_PDF_PLUGIN = "PDFPlugin";
    
    /**
     * Node for PDFViewer preferences.
     */
    private static final String NODE_PDF_VIEWER = "PDFViewer";
    
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
     * Document viewer.
     */    
    private DocumentViewer viewer_;
    
    /** 
     * File explorer used to open XML files. 
     */
    private JFileExplorer explorer_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a PDF Plugin.
     */
    public PDFPlugin()
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

        //        Vector v = new Vector();
        //        v.add(new JSmartButton("One"));
        //        v.add(new JSmartButton("Two"));
        //        v.add(new JSmartButton("Three"));
        //        
        //        JList viewerList = new JSmartList(v);
        //
        //        class ButtonCellRenderer extends JSmartButton implements ListCellRenderer
        //        {
        //            public Component getListCellRendererComponent(
        //                    JList list,
        //                    Object value,
        //                    int index,
        //                    boolean isSelected,
        //                    boolean cellHasFocus)
        //            {
        //                setText(value.toString());
        //                return this;
        //            }
        //        }
        //        
        //        viewerList.setCellRenderer( new ButtonCellRenderer());

        
        JPanel viewerList = new JPanel(new StackLayout(StackLayout.VERTICAL));
        viewerList.add("Top Wide Flush", new JSmartButton("One"));
        viewerList.add("Top Wide Flush", new JSmartButton("Two"));
        viewerList.add("Top Wide Flush", new JSmartButton("Three"));
        
        
//        viewerList.setPreferredSize(
//            new Dimension(
//                viewerList.getPreferredSize().width,
//                100));
                
        
        JSplitPane splitter = 
            new JSmartSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                explorer_,
                new JScrollPane(viewerList));
        
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
    protected void viewPDFEmbedded(File file) throws Exception
    {
        //viewPDFEmbedded(new FileInputStream(file));
        
        if (viewer_ == null)
        {
            viewer_ = new MultivalentViewer();
            //viewer_ = new AcrobatViewer();
            viewer_.startup(null);
            outputPanel_.add(BorderLayout.CENTER, viewer_.getComponent());
        }

        viewer_.view(file);
    }

    //--------------------------------------------------------------------------
    // IPlugin Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.workspace.IPlugin#getPluginName()
     */
    public String getPluginName()
    {
        return "PDF Viewer";
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
        return "Views PDF documents.";
    }

    
    /**
     * @see toolbox.workspace.IPlugin#startup(java.util.Map)
     */
    public void startup(Map params)
    {
        if (params != null)
            statusBar_= (IStatusBar) params.get(PluginWorkspace.PROP_STATUSBAR);
        
        buildView();
    }

    
    /**
     * @see toolbox.workspace.IPlugin#shutdown()
     */
    public void shutdown()
    {
        if (viewer_ != null)
            viewer_.shutdown();
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
                prefs, NODE_PDF_PLUGIN, new Element(NODE_PDF_PLUGIN));    
        
        explorer_.applyPrefs(root);
        flipPane_.applyPrefs(root);
    }

    
    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs)
    {
        Element root = new Element(NODE_PDF_PLUGIN);
        explorer_.savePrefs(root);
        flipPane_.savePrefs(root);
        XOMUtil.insertOrReplace(prefs, root);
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
         * @see toolbox.util.ui.JFileExplorerListener#fileDoubleClicked(
         *      java.lang.String)
         */
        public void fileDoubleClicked(String file)
        {
            try
            {
                viewPDFEmbedded(new File(file));
            }
            catch (Exception fnfe)
            {
                ExceptionUtil.handleUI(fnfe, logger_);
            }
        }
        
        
        /**
         * @see toolbox.util.ui.JFileExplorerAdapter#fileSelected(
         *      java.lang.String)
         */
        public void fileSelected(String file)
        {
            logger_.info("File selected: " + file);
        }
    }
}