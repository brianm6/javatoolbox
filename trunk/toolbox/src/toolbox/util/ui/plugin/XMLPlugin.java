package toolbox.util.ui.plugin;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;

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

        JPanel workPane = new JPanel(new BorderLayout());
        workPane.add(BorderLayout.CENTER, new JScrollPane(xmlArea_));
        
        JPanel buttonPane = new JPanel(new FlowLayout());
        buttonPane.add(new JButton(new FormatAction()));
        
        add(BorderLayout.WEST, flipPane_);
        add(BorderLayout.CENTER, workPane);
        add(BorderLayout.SOUTH, buttonPane);
        
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
            String xml = xmlArea_.getText();
            
            
            String formatted = XMLUtil.format(xml);
            
            xmlArea_.setText(formatted);
       }
    }
}