package toolbox.util.ui.plugin;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import com.adobe.acrobat.Viewer;

import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.fop.apps.Driver;
import org.apache.fop.apps.Fop;
import org.apache.fop.messaging.MessageHandler;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;

import toolbox.util.ExceptionUtil;
import toolbox.util.FileUtil;
import toolbox.util.SwingUtil;
import toolbox.util.XMLUtil;
import toolbox.util.io.StringInputStream;
import toolbox.util.ui.JFileExplorer;
import toolbox.util.ui.JFileExplorerAdapter;
import toolbox.util.ui.JFlipPane;
import toolbox.util.ui.JSmartTextArea;

/**
 * XML Plugin 
 * 
 * <pre>
 * 
 * - formats XML
 * - transforms XSL-FO to PDF and views with embedded PDF viewer
 * - transforms XSL-FO and renders output directly to a GUI
 * 
 * </pre>
 */ 
public class XMLPlugin extends JPanel implements IPlugin
{
    /** Logger */
    private static final Logger logger_ = 
        Logger.getLogger(XMLPlugin.class);

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
    private JSmartTextArea xmlArea_;
    
    /**
     * XML output pane
     */
    private JPanel outputPanel_;
    
    /**
     * Embedded PDF viewer component
     */
    private Viewer viewer_;    

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Default Constructor
     */
    public XMLPlugin()
    {
        buildView();
    }


    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    //--------------------------------------------------------------------------
    // Private
    //--------------------------------------------------------------------------
    
    /**
     * Builds the GUI
     */
    protected void buildView()
    {
        xmlArea_ = new JSmartTextArea();
        xmlArea_.setFont(SwingUtil.getPreferredMonoFont());
        
        setLayout(new BorderLayout());
        JFileExplorer fileExplorer = new JFileExplorer(false);      
        fileExplorer.addJFileExplorerListener(new FileSelectionListener());
                
        flipPane_ = new JFlipPane(JFlipPane.LEFT);
        flipPane_.addFlipper("File Explorer", fileExplorer);

        outputPanel_ = new JPanel(new BorderLayout());
        
        JSplitPane splitPane = 
            new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(xmlArea_),
                outputPanel_);
        
        JPanel buttonPane = new JPanel(new FlowLayout());
        buttonPane.add(new JButton(new FormatAction()));
        buttonPane.add(new JButton(new FOPAWTAction()));
        buttonPane.add(new JButton(new FOPPDFAction()));
        
        add(BorderLayout.WEST, flipPane_);
        add(BorderLayout.CENTER, splitPane);
        add(BorderLayout.SOUTH, buttonPane);
        
    }


    //--------------------------------------------------------------------------
    //  IPlugin Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.ui.plugin.IPlugin#getName()
     */
    public String getName()
    {
        return "XML";
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
        
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#savePrefs(Properties)
     */
    public void savePrefs(Properties prefs)
    {
        
    }

    /**
     * @see toolbox.util.ui.plugin.IPlugin#applyPrefs(Properties)
     */
    public void applyPrefs(Properties prefs)
    {
        
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
        
    }    
    
    
    //--------------------------------------------------------------------------
    //  Inner Classes
    //--------------------------------------------------------------------------
    
    /**
     * Listener for the file explorer
     */
    private class FileSelectionListener extends JFileExplorerAdapter
    {
        /**
         * Tail files that have been double clicked on
         * 
         * @param  file  File double clicked
         */
        public void fileDoubleClicked(String file)
        {
            try
            {
                xmlArea_.setText(FileUtil.getFileContents(file));
            }
            catch (FileNotFoundException e)
            {
                logger_.error(e);
            }
            catch (IOException e)
            {
                logger_.error(e);
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
            super("FOP AWT");
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
     * Launches FOP PDF viewer
     */
    private class FOPPDFAction extends AbstractAction
    {
        public FOPPDFAction()
        {
            super("FOP PDF");
        }
        
        public void actionPerformed(ActionEvent e)
        {
            try
            {
                Driver driver = new Driver();

                org.apache.avalon.framework.logger.Logger logger = 
                    new ConsoleLogger(ConsoleLogger.LEVEL_INFO);
                    
                driver.setLogger(logger);
                MessageHandler.setScreenLogger(logger);
                driver.setRenderer(Driver.RENDER_PDF);
                
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                
                try
                {
                    driver.setOutputStream(out);

                    InputStream in = new StringInputStream(xmlArea_.getText());
                    
                    try
                    {
                        driver.setInputSource(new InputSource(in));
                        driver.run();
                    }
                    finally
                    {
                        in.close();
                    }
                }
                finally
                {
                    out.close();
                }
               
                if (viewer_ == null)
                {
                    viewer_ = new Viewer();
                    outputPanel_.add(BorderLayout.CENTER, viewer_);
                    viewer_.activate();
                }
                
                viewer_.setDocumentInputStream(
                    new ByteArrayInputStream(out.toByteArray()));
                    
                // TODO: max width the doc
            }
            catch (Exception ioe)
            {
                ExceptionUtil.handleUI(ioe, logger_);
            }
        }
    }
}