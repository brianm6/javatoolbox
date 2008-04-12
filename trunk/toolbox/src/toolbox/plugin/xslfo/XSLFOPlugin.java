package toolbox.plugin.xslfo;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import nu.xom.Element;

import org.apache.commons.lang.StringUtils;
import org.apache.fop.apps.Fop;
import org.apache.log4j.Logger;
import org.jedit.syntax.TextAreaDefaults;
import org.jedit.syntax.XMLTokenMarker;

import toolbox.jedit.JEditPopupMenu;
import toolbox.jedit.JEditTextArea;
import toolbox.jedit.XMLDefaults;
import toolbox.util.ArrayUtil;
import toolbox.util.ExceptionUtil;
import toolbox.util.FileUtil;
import toolbox.util.SwingUtil;
import toolbox.util.XOMUtil;
import toolbox.util.formatter.XMLFormatter;
import toolbox.util.io.StringInputStream;
import toolbox.util.service.ServiceException;
import toolbox.util.service.ServiceTransition;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartSplitPane;
import toolbox.util.ui.explorer.FileExplorerAdapter;
import toolbox.util.ui.explorer.JFileExplorer;
import toolbox.util.ui.flippane.JFlipPane;
import toolbox.workspace.AbstractPlugin;
import toolbox.workspace.IStatusBar;
import toolbox.workspace.PluginWorkspace;
import toolbox.workspace.PreferencedException;
import toolbox.workspace.WorkspaceAction;

import com.adobe.acrobat.Viewer;

/**
 * XSLFO Plugin is a simple GUI interface to edit, process and view transformed
 * XSL-FO documents.
 * <p>
 * Features:
 * <p>
 * <ul>
 *   <li>Selectable output format (PDF or Postscript)
 *   <li>Selectable viewer  (Embedded PDF viewer or launched Acrobat Viewer)
 *   <li>XML editor with syntax-hiliting
 *   <li>XML formatter
 * </ul>
 */ 
public class XSLFOPlugin extends AbstractPlugin
{
    private static final Logger logger_ = Logger.getLogger(XSLFOPlugin.class);

    //--------------------------------------------------------------------------
    // IPreferenced Constants
    //--------------------------------------------------------------------------
    
    /** 
     * Root preferences node for this plugin.
     */
    private static final String NODE_XSLFO_PLUGIN = "XSLFOPlugin";
    
    /**
     * Node for PDFViewer preferences.
     */
    private static final String NODE_PDF_VIEWER = "PDFViewer";
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * View for this plugin.
     */
    private JComponent view_;
    
    /** 
     * Shared status bar with plugin host.
     */
    private IStatusBar statusBar_;
    
    /** 
     * XML text work area. 
     */
    private JEditTextArea xmlArea_;

    /** 
     * XML output pane. 
     */
    private JPanel outputPanel_;
    
    /** 
     * Embedded PDF viewer component. 
     */
    private Viewer viewer_;    

    /** 
     * Full Path to acrobat reader executable. 
     */
    private String pdfViewerPath_;

    /** 
     * Apache XSLFO implementation = FOP. 
     */
    private FOProcessor fopProcessor_;
    
    /**
     * Subcomponents that implement IPreferenced.
     */
    private List preferenced_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates an XSLFOPlugin.
     */
    public XSLFOPlugin()
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
        
        TextAreaDefaults defaults = new XMLDefaults();
        xmlArea_ = new JEditTextArea(new XMLTokenMarker(), defaults);
        ((JEditPopupMenu) defaults.popup).setTextArea(xmlArea_);
        ((JEditPopupMenu) defaults.popup).buildView();
        
        JFileExplorer explorer = new JFileExplorer(false);
        explorer.addFileExplorerListener(new FileSelectionListener());
        
        // Flip panel that houses the file explorer. 
        JFlipPane flipPane = new JFlipPane(JFlipPane.LEFT);
        flipPane.addFlipper(JFileExplorer.ICON, "File Explorer", explorer);
        outputPanel_ = new JPanel(new BorderLayout());
        
        // Splitter between the xmlArea and the outputPanel.
        JSmartSplitPane splitPane = 
            new JSmartSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                xmlArea_,
                outputPanel_);
        
        JPanel buttonPane = new JPanel(new FlowLayout());
        buttonPane.add(new JSmartButton(new FormatAction()));
        buttonPane.add(new JSmartButton(new FOPAWTAction()));
        buttonPane.add(new JSmartButton(new FOPRenderAction()));
        buttonPane.add(new JSmartButton(new FOPLaunchAction()));
        buttonPane.add(new JSmartButton(new FOPExportToPDFAction()));
        buttonPane.add(new JSmartButton(new FOPExportToPostscriptAction()));
        
        view_.add(BorderLayout.WEST, flipPane);
        view_.add(BorderLayout.CENTER, splitPane);
        view_.add(BorderLayout.SOUTH, buttonPane);

        // Roundup all components that get tickled when saving/applying prefs 
        preferenced_ = new ArrayList();
        preferenced_.add(explorer);
        preferenced_.add(flipPane);
        preferenced_.add(xmlArea_);
        preferenced_.add(splitPane);
    }


    /**
     * Launches Adobe Acrobat Reader on the given file.
     * 
     * @param outfile File to open in the PDF viewer.
     */
    protected void viewPDFExternal(String outfile)
    {
        if (StringUtils.isBlank(pdfViewerPath_))
        {
            try
            {
                JFileChooser jfc = new JFileChooser();
                
                if (jfc.showOpenDialog(
                        SwingUtil.getFrameAncestor(getView())) == 
                            JFileChooser.APPROVE_OPTION) 
                {
                    pdfViewerPath_ = jfc.getSelectedFile().getCanonicalPath();
                }
            }
            catch (FileNotFoundException fnfe)
            {
                ExceptionUtil.handleUI(fnfe, logger_);
            }
            catch (IOException ioe)
            {
                ExceptionUtil.handleUI(ioe, logger_);
            }
        }
        
        try
        {
            logger_.info("Executing: " + pdfViewerPath_ + " " + outfile);
            Runtime.getRuntime().exec(pdfViewerPath_ + " " + outfile);
        }
        catch (Exception ex)
        {
            ExceptionUtil.handleUI(ex, logger_);
            pdfViewerPath_ = null;
        }
    }


    /**
     * Views a PDF using an embedded java pdf viewer.
     * 
     * @param file File to view with the embedded PDF viewer.
     * @throws Exception on error.
     */
    protected void viewPDFEmbedded(File file) throws Exception
    {
        viewPDFEmbedded(new FileInputStream(file));
    }


    /**
     * Views a PDF using an embedded java pdf viewer.
     * 
     * @param inputStream Stream to read PDF bytes from.
     * @throws Exception on error.
     */
    protected void viewPDFEmbedded(InputStream inputStream) throws Exception
    {
        if (viewer_ == null)
        {
            viewer_ = new Viewer();
            outputPanel_.add(BorderLayout.CENTER, viewer_);
            viewer_.activate();
        }
        
        viewer_.setDocumentInputStream(inputStream);        
        viewer_.execMenuItem("FitVisibleWidth");
    }


    /**
     * Returns the Apache FOP processor.
     * 
     * @return FOProcessor
     */
    protected FOProcessor getFOP()
    {
        if (fopProcessor_ == null)
        {
            fopProcessor_ = 
                FOProcessorFactory.create(
                    FOProcessorFactory.FO_IMPL_APACHE);
              
            fopProcessor_.initialize(new Properties());
        }
        
        return fopProcessor_;
    }

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
            statusBar_ = (IStatusBar) params.get(PluginWorkspace.KEY_STATUSBAR);
        
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
        return "XSL-FO";
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
        return "Transforms valid XSL-FO to either PDF or Postscript using " + 
               "Apache FOP.";
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
        if (viewer_ != null)
            viewer_.deactivate();
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
                prefs, NODE_XSLFO_PLUGIN, new Element(NODE_XSLFO_PLUGIN));    
        
        ArrayUtil.invoke(preferenced_.toArray(), "applyPrefs", new Object[] {root});
            
        pdfViewerPath_ = XOMUtil.getString(
            root.getFirstChildElement(NODE_PDF_VIEWER), null);
    }


    /**
     * Saves preferences in following XML structure.
     * <pre>
     * 
     * Parent
     * |
     * +--XSLFOPlugin
     *    |
     *    +--PDFViewer
     *    |
     *    +--JFileExplorer
     *    |
     *    +--JFlipPane
     *    |
     *    '--JEditTextArea 
     * 
     * </pre>
     * 
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws PreferencedException
    {
        Element root = new Element(NODE_XSLFO_PLUGIN);
        ArrayUtil.invoke(preferenced_.toArray(), "savePrefs", new Object[] {root});
        
        if (!StringUtils.isBlank(pdfViewerPath_))
        {
            Element pdf = new Element(NODE_PDF_VIEWER);
            pdf.appendChild(pdfViewerPath_);
            root.appendChild(pdf);
        }
        
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
         * @see toolbox.util.ui.explorer.FileExplorerListener#fileDoubleClicked(
         *      java.lang.String)
         */
        public void fileDoubleClicked(String file)
        {
            try
            {
                xmlArea_.setText(FileUtil.getFileContents(file));
                xmlArea_.setCaretPosition(0);
            }
            catch (FileNotFoundException fnfe)
            {
                ExceptionUtil.handleUI(fnfe, logger_);
            }
            catch (IOException ioe)
            {
                ExceptionUtil.handleUI(ioe, logger_);
            }
        }
    }
    
    //--------------------------------------------------------------------------
    // FormatAction
    //--------------------------------------------------------------------------
    
    /**
     * Formats the XML with correct indentation and spacing.
     */
    class FormatAction extends WorkspaceAction 
    {
        /**
         * Creates a FormatAction. 
         */
        FormatAction()
        {
            super("Format", false, null, null);
        }
    
        
        /**
         * @see toolbox.util.ui.SmartAction#runAction(
         *      java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            xmlArea_.setText(new XMLFormatter().format(xmlArea_.getText()));
            xmlArea_.setCaretPosition(0);
        }
    }

    //--------------------------------------------------------------------------
    // FOPAWTAction
    //--------------------------------------------------------------------------
    
    /**
     * Launches FOP AWT viewer.
     */
    class FOPAWTAction extends WorkspaceAction
    {
        /**
         * Creates a FOPAWTAction. 
         */
        FOPAWTAction()
        {
            super("Launch with FOP AWT", false, null, null);
        }
        
        
        /**
         * @see toolbox.util.ui.SmartAction#runAction(
         *      java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            String xml = xmlArea_.getText();
            String foFile  = FileUtil.createTempFilename() + ".xml";
            FileUtil.setFileContents(foFile, xml, false);
            Fop.main(new String[] {foFile, "-awt"});
        }
    }
    
    //--------------------------------------------------------------------------
    // FOPRenderAction
    //--------------------------------------------------------------------------
    
    /**
     * Renders the XSLFO and views using the internal PDF viewer.
     */
    class FOPRenderAction extends WorkspaceAction
    {
        /**
         * Creates a FOPRenderAction. 
         */
        FOPRenderAction()
        {
            super("Render with FOP", true, getView(), statusBar_);
        }
        
        
        /**
         * @see toolbox.util.ui.SmartAction#runAction(
         *      java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            StringInputStream input = new StringInputStream(xmlArea_.getText());
            getFOP().renderPDF(input, output);
            viewPDFEmbedded(new ByteArrayInputStream(output.toByteArray()));
        }
    }

    //--------------------------------------------------------------------------
    // FOPLaunchAction
    //--------------------------------------------------------------------------
    
    /**
     * Uses FOP formatter and views externally as a PDF.
     */
    class FOPLaunchAction extends WorkspaceAction
    {
        /**
         * Creates a FOPLaunchAction. 
         */
        FOPLaunchAction()
        {
            super("Launch with FOP", true, getView(), statusBar_);
        }
        
        
        /**
         * @see toolbox.util.ui.SmartAction#runAction(
         *      java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            StringInputStream input = new StringInputStream(xmlArea_.getText());
            
            getFOP().renderPDF(input, output);
            byte[] pdfBytes = output.toByteArray();
            
            String pdfFile = FileUtil.createTempFilename() + ".pdf";
            FileUtil.setFileContents(pdfFile, pdfBytes, false);
            viewPDFExternal(pdfFile);
        }
    }    
    
    //--------------------------------------------------------------------------
    // FOPExportToPDFAction
    //--------------------------------------------------------------------------
    
    /**
     * Saves generated PDF to file.
     */
    class FOPExportToPDFAction extends WorkspaceAction
    {
        private File lastDir_;
        
        /**
         * Creates a FOPExportToPDFAction. 
         */
        FOPExportToPDFAction()
        {
            super("Export to PDF..", false, null, null);
        }
        
        
        /**
         * @see toolbox.util.ui.SmartAction#runAction(
         *      java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            JFileChooser chooser = null;
            
            if (lastDir_ == null)
                chooser = new JFileChooser();
            else
                chooser = new JFileChooser(lastDir_);

            if (chooser.showSaveDialog(null) ==  JFileChooser.APPROVE_OPTION) 
            {
                String saveFile = chooser.getSelectedFile().getCanonicalPath();
                
                logger_.debug("Save file=" + saveFile);
                
                FileOutputStream output = new FileOutputStream(saveFile);
                StringInputStream i = new StringInputStream(xmlArea_.getText());
                getFOP().renderPDF(i, output);
                output.close();
            }
            
            lastDir_ = chooser.getCurrentDirectory();
        }
    }
    
    //--------------------------------------------------------------------------
    // FOPExportToPostcriptAction
    //--------------------------------------------------------------------------
    
    /**
     * Exports XSL-FO to a Postscript file.
     */
    class FOPExportToPostscriptAction extends WorkspaceAction
    {
        private File lastDir_;
        
        /**
         * Creates a FOPExportToPostscriptAction. 
         */
        FOPExportToPostscriptAction()
        {
            super("Export to Postscript..", false, null, null);
        }
        
        
        /**
         * @see toolbox.util.ui.SmartAction#runAction(
         *      java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            JFileChooser chooser = null;
            
            if (lastDir_ == null)
                chooser = new JFileChooser();
            else
                chooser = new JFileChooser(lastDir_);

            if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) 
            {
                String saveFile = 
                    chooser.getSelectedFile().getCanonicalPath();
                
                logger_.debug("Save file=" + saveFile);
                
                FileOutputStream fos = new FileOutputStream(saveFile);
                
                getFOP().renderPostscript(
                    new StringInputStream(xmlArea_.getText()), fos);
            }
            
            lastDir_ = chooser.getCurrentDirectory();
        }
    }
}