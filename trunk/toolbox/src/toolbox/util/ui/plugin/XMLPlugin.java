package toolbox.util.ui.plugin;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import com.adobe.acrobat.Viewer;

import org.apache.fop.apps.Fop;
import org.apache.log4j.Logger;

import toolbox.util.ExceptionUtil;
import toolbox.util.FileUtil;
import toolbox.util.SwingUtil;
import toolbox.util.XMLUtil;
import toolbox.util.ui.JFileExplorer;
import toolbox.util.ui.JFileExplorerAdapter;
import toolbox.util.ui.JFlipPane;
import toolbox.util.ui.JSmartTextArea;

/**
 * Simple SQL query panel
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
    
    private IStatusBar statusBar_;
    
    private JSmartTextArea xmlArea_;
    
    private JPanel outputPanel_;
    
    private Viewer viewer_;    

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Constructor for XMLPlugin.
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
     * Preferences action
     */
    private class FormatAction extends AbstractAction 
    {
        public FormatAction()
        {
            super("Format");
        }
    
        /**
         * Pops up the Font selection dialog
         */
        public void actionPerformed(ActionEvent e)
        {
            try
            {
                String xml = xmlArea_.getText();
                String formatted = XMLUtil.format(xml);
                xmlArea_.setText(formatted);
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
                FileUtil.setFileContents("tmp_fop.xml", xml, false);
                Fop.main(new String[] { "tmp_fop.xml", "-awt"});
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
                String xml = xmlArea_.getText();
                FileUtil.setFileContents("tmp_fop.xml", xml, false);
                Fop.main(new String[] { "tmp_fop.xml", "tmp_fop.pdf"});
                
                if (viewer_ == null)
                {
                    viewer_ = new Viewer();
                    outputPanel_.add(BorderLayout.CENTER, viewer_);
                    viewer_.activate();
                    
                }
                
                viewer_.setDocumentInputStream(new FileInputStream("tmp_fop.pdf"));
                
                //Runtime.getRuntime().exec(
                //    "C:\\Program Files\\Adobe\\Acrobat 4.0\\Reader\\AcroRd32.exe tmp_fop.pdf");

            }
            catch (Exception ioe)
            {
                ExceptionUtil.handleUI(ioe, logger_);
            }
        }
    }
}