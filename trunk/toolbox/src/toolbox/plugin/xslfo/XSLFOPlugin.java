package toolbox.util.xslfo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.adobe.acrobat.Viewer;

import org.apache.fop.apps.Fop;
import org.apache.log4j.Logger;

import org.jedit.syntax.SyntaxStyle;
import org.jedit.syntax.TextAreaDefaults;
import org.jedit.syntax.Token;
import org.jedit.syntax.XMLTokenMarker;

import toolbox.jedit.JEditTextArea;
import toolbox.jedit.JEditTextAreaPopupMenu;
import toolbox.util.ClassUtil;
import toolbox.util.ExceptionUtil;
import toolbox.util.FileUtil;
import toolbox.util.StringUtil;
import toolbox.util.SwingUtil;
import toolbox.util.XMLUtil;
import toolbox.util.ui.JFileExplorer;
import toolbox.util.ui.JFileExplorerAdapter;
import toolbox.util.ui.JFlipPane;
import toolbox.util.ui.plugin.IPlugin;
import toolbox.util.ui.plugin.IStatusBar;

/**
 * XSL-FO Plugin
 * 
 * <pre>
 * 
 * o formats XML
 * o transforms XSL-FO to PDF and views with embedded PDF viewer
 * o transforms XSL-FO and renders output directly to a GUI
 * 
 * </pre>
 */ 
public class XSLFOPlugin extends JPanel implements IPlugin
{
    /** Logger */
    private static final Logger logger_ = 
        Logger.getLogger(XSLFOPlugin.class);

    /** Prefix passed to sub components when asked to save their preferences */
    private static final String PREFS_PREFIX =  
        ClassUtil.stripPackage(XSLFOPlugin.class.getName()).toLowerCase();
        
    /**
     * Flip panel that houses the file explorer 
     */
    private JFlipPane flipPane_;    
    
    /**
     * Shared status bar with plugin host
     */
    private IStatusBar statusBar_;
    
    /**
     * XML text work area
     */
    private JEditTextArea xmlArea_;

    /**
     * Default settings for XML text area
     */
    private TextAreaDefaults defaults_;    
    
    /**
     * XML output pane
     */
    private JPanel outputPanel_;
    
    /**
     * Embedded PDF viewer component
     */
    private Viewer viewer_;    

    /**
     * File explorer to load xml from files directly
     */
    private JFileExplorer explorer_;

    /**
     * Full Path to acrobat reader executable
     */
    private String acrobatPath_;

    /**
     * Apache FOP 
     */
    private FOProcessor fopProcessor_;
    
    /**
     * RenderX XEP
     */
    private FOProcessor xepProcessor_;

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------

    /**
     * Default Constructor
     */
    public XSLFOPlugin()
    {
    }

    //--------------------------------------------------------------------------
    // Private
    //--------------------------------------------------------------------------

    /**
     * Customizes the configuration of the text area
     */
    protected void initTextArea()
    {
        defaults_ = TextAreaDefaults.getDefaults();
        
        defaults_.editable = true;
        defaults_.caretVisible = true;
        defaults_.caretBlinks = false;
        defaults_.blockCaret = true;
        //defaults.electricScroll=int??
        
        //public int cols;
        defaults_.rows = 5;
        defaults_.styles = getSyntaxStyles();
        //public Color caretColor;
        //public Color selectionColor;
        //public Color lineHighlightColor;
        //public boolean lineHighlight;
        //public Color bracketHighlightColor;
        //public boolean bracketHighlight;
        //public Color eolMarkerColor;
        defaults_.eolMarkers=false;
        defaults_.paintInvalid=false;
        defaults_.popup = new JEditTextAreaPopupMenu();
    }

    /**
     * Customizes the colors used for syntax hiliting the xml
     * 
     * @return  Syntax styles
     */
    protected static SyntaxStyle[] getSyntaxStyles()
    {
        SyntaxStyle[] styles = new SyntaxStyle[Token.ID_COUNT];

        styles[Token.COMMENT1] =
            new SyntaxStyle(Color.red.darker(), false, false);
            
        styles[Token.COMMENT2] =
            new SyntaxStyle(new Color(0x990033), false, false);
            
        styles[Token.KEYWORD1] =
            new SyntaxStyle(Color.blue.darker(), false, false);
            
        styles[Token.KEYWORD2] =
            new SyntaxStyle(Color.blue.darker(), false, false);
            
        styles[Token.KEYWORD3] =
            new SyntaxStyle(new Color(0x009600), false, false);
            
        styles[Token.LITERAL1] =
            new SyntaxStyle(Color.green.darker() /*new Color(0x650099)*/
                , false, false);
                
        styles[Token.LITERAL2] =
            new SyntaxStyle(new Color(0x650099), false, false);
            
        styles[Token.LABEL] =
            new SyntaxStyle(new Color(0x990033), false, false);
            
        styles[Token.OPERATOR] =
            new SyntaxStyle(Color.blue.darker(), false, false);
            
        styles[Token.INVALID] = 
            new SyntaxStyle(Color.red, false, false);

        return styles;
    }
    
    /**
     * Builds the GUI
     */
    protected void buildView()
    {
        initTextArea();
        xmlArea_ = new JEditTextArea(defaults_);
        ((JEditTextAreaPopupMenu) defaults_.popup).setTextArea(xmlArea_);
        ((JEditTextAreaPopupMenu) defaults_.popup).buildView();
        
        xmlArea_.setTokenMarker(new XMLTokenMarker());
        
        setLayout(new BorderLayout());
        explorer_ = new JFileExplorer(false);
        explorer_.addJFileExplorerListener(new FileSelectionListener());
                
        flipPane_ = new JFlipPane(JFlipPane.LEFT);
        flipPane_.addFlipper("File Explorer", explorer_);

        outputPanel_ = new JPanel(new BorderLayout());
        
        JSplitPane splitPane = 
            new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                xmlArea_,
                outputPanel_);
        
        JPanel buttonPane = new JPanel(new FlowLayout());
        buttonPane.add(new JButton(new FormatAction()));
        buttonPane.add(new JButton(new FOPAWTAction()));
        buttonPane.add(new JButton(new FOPRenderAction()));
        buttonPane.add(new JButton(new FOPLaunchAction()));
        buttonPane.add(new JButton(new FOPExportToPDFAction()));
        buttonPane.add(new JButton(new XEPRenderAction()));
        buttonPane.add(new JButton(new XEPLaunchAction()));
        
        add(BorderLayout.WEST, flipPane_);
        add(BorderLayout.CENTER, splitPane);
        add(BorderLayout.SOUTH, buttonPane);
    }

    /**
     * Launches Adobe Acrobat Reader on the given file
     * 
     * @param outfile
     */
    private void viewPDFExternal(String outfile) throws IOException
    {
        if (StringUtil.isNullOrEmpty(acrobatPath_))
        {
            try
            {
                JFileChooser jfc = new JFileChooser();
                
                if (jfc.showOpenDialog(
                        SwingUtil.getFrameAncestor(this)) == 
                            JFileChooser.APPROVE_OPTION) 
                {
                    acrobatPath_ = jfc.getSelectedFile().getCanonicalPath();
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
            logger_.info("Executing: " + acrobatPath_ + " " + outfile);
            Runtime.getRuntime().exec(acrobatPath_ + " " + outfile);
        }
        catch (Exception ex)
        {
            ExceptionUtil.handleUI(ex, logger_);
            acrobatPath_ = null;
        }
    }

    /**
     * Views a PDF using an embedded java pdf viewer
     * 
     * @param file
     */
    private void viewPDFEmbedded(File file) throws Exception
    {
        viewPDFEmbedded(new FileInputStream(file));
    }

    /**
     * Views a PDF using an embedded java pdf viewer
     * 
     * @param inputStream
     * @throws Exception
     */
    private void viewPDFEmbedded(InputStream inputStream) throws Exception
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
     * @return  Apache FOP processor
     */
    protected FOProcessor getFOP()
    {
        if (fopProcessor_ == null)
        {
            fopProcessor_ = FOProcessorFactory.createProcessor(
                FOProcessorFactory.FO_IMPL_APACHE);
              
            fopProcessor_.initialize();
        }
        
        return fopProcessor_;
    }

    /**
     * @return  RenderX XEP processor
     */
    protected FOProcessor getXEP()
    {
        if (xepProcessor_ == null)
        {
            xepProcessor_ = FOProcessorFactory.createProcessor(
                FOProcessorFactory.FO_IMPL_RENDERX);
              
            xepProcessor_.initialize();
        }
        
        return xepProcessor_;
    }

    //--------------------------------------------------------------------------
    //  IPlugin Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.ui.plugin.IPlugin#getName()
     */
    public String getName()
    {
        return "XSL-FO";
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#getComponent()
     */
    public Component getComponent()
    {
        return this;
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#getMenuBar()
     */
    public JMenuBar getMenuBar()
    {
        return null;
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#init()
     */
    public void init()
    {
        buildView();
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#savePrefs(Properties)
     */
    public void savePrefs(Properties prefs)
    {
        explorer_.savePrefs(prefs, PREFS_PREFIX);
        flipPane_.savePrefs(prefs, PREFS_PREFIX);
        
        if (!StringUtil.isNullOrEmpty(acrobatPath_))
            prefs.setProperty("xslfoplugin.acrobat.path", acrobatPath_);
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#applyPrefs(Properties)
     */
    public void applyPrefs(Properties prefs)
    {
        explorer_.applyPrefs(prefs, PREFS_PREFIX);
        flipPane_.applyPrefs(prefs, PREFS_PREFIX);
        
        acrobatPath_ = prefs.getProperty("xslfoplugin.acrobat.path", null);
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#setStatusBar(IStatusBar)
     */
    public void setStatusBar(IStatusBar statusBar)
    {
        statusBar_ = statusBar;
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#shutdown()
     */
    public void shutdown()
    {
        if (viewer_ != null)
            viewer_.deactivate();
    }    
    
    //--------------------------------------------------------------------------
    //  Inner Classes
    //--------------------------------------------------------------------------
    
    /**
     * Populates file that is double clicked on in the text area
     */
    private class FileSelectionListener extends JFileExplorerAdapter
    {
        public void fileDoubleClicked(String file)
        {
            try
            {
                xmlArea_.setText(FileUtil.getFileContents(file));
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
    //  Actions
    //--------------------------------------------------------------------------
    
    /**
     * Formats the XML with correct indentation and spacing
     */
    private class FormatAction extends AbstractAction 
    {
        public FormatAction()
        {
            super("Format");
        }
    
        public void actionPerformed(ActionEvent e)
        {
            try
            {
                xmlArea_.setText(XMLUtil.format(xmlArea_.getText()));
            }
            catch (Exception ee)
            {
                ExceptionUtil.handleUI(ee, logger_);
            }
        }
    }
    
    /**
     * Launches FOP AWT viewer
     */
    private class FOPAWTAction extends AbstractAction
    {
        public FOPAWTAction()
        {
            super("Launch with FOP AWT");
        }
        
        public void actionPerformed(ActionEvent e)
        {
            try
            {
                String xml = xmlArea_.getText();
                String foFile  = FileUtil.getTempFilename() + ".xml";
                FileUtil.setFileContents(foFile, xml, false);
                Fop.main(new String[] { foFile, "-awt"});
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
    
    /**
     * Renders the XSLFO and views using the internal PDF viewer
     */
    private class FOPRenderAction extends AbstractAction
    {
        public FOPRenderAction()
        {
            super("Render with FOP");
        }
        
        public void actionPerformed(ActionEvent e)
        {
            try
            {
                byte[] pdfBytes = getFOP().renderPDF(xmlArea_.getText());
                viewPDFEmbedded(new ByteArrayInputStream(pdfBytes));
            }
            catch (Exception ioe)
            {
                ExceptionUtil.handleUI(ioe, logger_);
            }
        }
    }

    /**
     * Uses FOP formatter and views externally as a PDF
     */
    private class FOPLaunchAction extends AbstractAction
    {
        public FOPLaunchAction()
        {
            super("Launch with FOP");
        }
        
        public void actionPerformed(ActionEvent e)
        {
            try
            {
                byte[] pdfBytes = getFOP().renderPDF(xmlArea_.getText());
                String pdfFile = FileUtil.getTempFilename() + ".pdf";
                FileUtil.setFileContents(pdfFile, pdfBytes, false);
                viewPDFExternal(pdfFile);
            }
            catch (Exception ioe)
            {
                ExceptionUtil.handleUI(ioe, logger_);
            }
        }
    }    
    
    /**
     * Launches FOP PDF viewer
     */
    private class FOPExportToPDFAction extends AbstractAction
    {
        private File lastDir_;
        
        public FOPExportToPDFAction()
        {
            super("Export to PDF..");
        }
        
        public void actionPerformed(ActionEvent e)
        {
            try
            {
                JFileChooser chooser = null;
                
                if (lastDir_ == null)
                    chooser = new JFileChooser();
                else
                    chooser = new JFileChooser(lastDir_);

                if (chooser.showSaveDialog(null) == 
                    JFileChooser.APPROVE_OPTION) 
                {
                    String saveFile = 
                        chooser.getSelectedFile().getCanonicalPath();
                    
                    logger_.debug("Save file=" + saveFile);
                    
                    FileOutputStream bos = new FileOutputStream(saveFile);
                    bos.write(getFOP().renderPDF(xmlArea_.getText()));
                    bos.close();
                }
                
                lastDir_ = chooser.getCurrentDirectory();
            }
            catch (Exception ioe)
            {
                ExceptionUtil.handleUI(ioe, logger_);
            }
        }
    }
    
    /**
     * Uses XEP formatter and views as PDF
     */
    private class XEPRenderAction extends AbstractAction
    {
        public XEPRenderAction()
        {
            super("Render with XEP");
        }
        
        public void actionPerformed(ActionEvent e)
        {
            try
            {
                byte[] pdfBytes = getXEP().renderPDF(xmlArea_.getText());
                viewPDFEmbedded(new ByteArrayInputStream(pdfBytes));
            }
            catch (Exception ioe)
            {
                ExceptionUtil.handleUI(ioe, logger_);
            }
        }
    }
    
    /**
     * Uses XEP formatter and views externally as a PDF
     */
    private class XEPLaunchAction extends AbstractAction
    {
        public XEPLaunchAction()
        {
            super("Launch with XEP");
        }
        
        public void actionPerformed(ActionEvent e)
        {
            try
            {
                byte[] pdfBytes = getXEP().renderPDF(xmlArea_.getText());
                String pdfFile = FileUtil.getTempFilename() + ".pdf";
                FileUtil.setFileContents(pdfFile, pdfBytes, false);
                viewPDFExternal(pdfFile);
            }
            catch (Exception ioe)
            {
                ExceptionUtil.handleUI(ioe, logger_);
            }
        }
    }    
}