package toolbox.jtail;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import org.apache.log4j.Category;
import toolbox.util.ArrayUtil;
import toolbox.util.ExceptionUtil;
import toolbox.util.SwingUtil;
import toolbox.util.file.FileStuffer;
import toolbox.util.ui.JFileExplorerAdapter;
import toolbox.util.ui.JSmartOptionPane;
import toolbox.util.ui.JSmartStatusBar;

import electric.xml.Document;
import electric.xml.Element;
import electric.xml.Elements;
import electric.xml.ParseException;

/**
 * GUI front end to the tail
 */
public class JTail extends JFrame
{
    /** Logger **/
    private static final Category logger_ = 
        Category.getInstance(JTail.class);

    private static final String DOC_JTAIL   = "JTail";
    private static final String CONFIG_FILE = ".jtail.xml";
    
    
    private FileSelectionPane   fileSelectionPane_;
    private JTabbedPane         tabbedPane_;
    private JSmartStatusBar     statusBar_;    
    private JMenuBar            menuBar_;    
    private Map                 tailMap_;
        
        
    static
    {
        FileStuffer stuffer = new FileStuffer(new File("c:\\crap.txt"), 100);
        stuffer.start();
    }
      
        
    /**
     * Entry point 
     */
    public static void main(String[] args)
    {
        JTail jtail = new JTail();
        jtail.setVisible(true);
    }


    /**
     * Constructor for JTail.
     */
    public JTail()
    {
        this("JTail");
    }


    /**
     * Constructor for JTail.
     * 
     * @param title
     */
    public JTail(String title)
    {
        super(title);
        init();
    }


    /** 
     * Initializes program
     */
    protected void init()
    {
        try
        {
            tailMap_ = new HashMap();
            buildView();
            addListeners();
            setDefaultCloseOperation(EXIT_ON_CLOSE);            
            applyConfiguration(loadConfiguration());
            setSize(800,400);
            SwingUtil.centerWindow(this);            
        }
        catch (Exception e)
        {
            System.err.println("Error: " + e.getMessage());
            System.err.println(ExceptionUtil.getStackTrace(e));
            System.exit(1);
        }
    }


    /**
     * Builds the GUI
     */
    protected void buildView()
    {
        getContentPane().setLayout(new BorderLayout());
        
        fileSelectionPane_ = new FileSelectionPane();
        tabbedPane_ = new JTabbedPane();

        JSplitPane rootSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            fileSelectionPane_, tabbedPane_);                

        getContentPane().add(BorderLayout.CENTER, rootSplitPane);
        
        statusBar_ = new JSmartStatusBar();
        getContentPane().add(BorderLayout.SOUTH, statusBar_);
        
        setJMenuBar(createMenuBar());
    }
    
    
    /**
     * Creates the menu bar
     */
    protected JMenuBar createMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem saveConfigMenuItem = new JMenuItem("Save configuration");
        saveConfigMenuItem.addActionListener(new MenuListener());
        
        menuBar.add(fileMenu);
        fileMenu.add(saveConfigMenuItem);
        
        return menuBar;
    }
    
    
    /**
     * Tails a file
     * 
     * @param  file   File to tail
     */     
    protected void tail(File file)
    {
        TailConfig config = new TailConfig();
        config.setFilename(file.getAbsolutePath());
        config.setAutoScroll(true);
        config.setShowLineNumbers(false);
        
        tail(config);
    }


    /**
     * Creates a tail of the given configuration
     * 
     * @param  config  Tail configuration
     */     
    protected void tail(TailConfig config)
    {
        try
        {
            logger_.debug(config);
            //File file = new File(config.getFilename());
            
            TailPane tailPane = new TailPane(config);
            
            JButton closeButton = tailPane.getCloseButton();
            
            // Create map of (closeButton, tailPane) so that the tail pane
            // can be reassociated if it needs to be removed from the
            // tabbed pane
            tailMap_.put(closeButton, tailPane);
            
            closeButton.addActionListener(new CloseButtonListener());
            
            tabbedPane_.addTab(config.getFilename(), tailPane);
            tabbedPane_.setSelectedComponent(tailPane);
        }
        catch (FileNotFoundException e)
        {
            logger_.error(e.getMessage(), e);            
            JSmartOptionPane.showExceptionMessageDialog(this, e);
        }
    }

         
    /**
     * Loads properties from $HOME/.jtail.xml
     * 
     * @throws  IOException on I/O error
     */
    protected TailConfig[] loadConfiguration() throws IOException
    {
        TailConfig[] configs = new TailConfig[0];
        
        String userHome = System.getProperty("user.home");
        String filename = userHome + File.separator + CONFIG_FILE;
     
        File xmlFile = new File(filename);
        
        if (!xmlFile.exists())
        {
            // create a new configuration
        }
        else if (!xmlFile.canRead())
        {
            JOptionPane.showMessageDialog(this, 
                "Cannot read configuration from " + filename + ". " + 
                "Using defaults.");
        }
        else if (!xmlFile.isFile())
        {
            JOptionPane.showMessageDialog(this,
                "Configuration file " + filename + " cannot be a directory. " +
                "Using Defaults.");
        }
        else
        {
            try
            {
                Document config = new Document(xmlFile);            

                // Iterate through each "tail" element, hydrate the object
                // and apply the configuration
                for (Elements tails = config.getRoot().getElements(); 
                    tails.hasMoreElements();)
                {
                    Element tail = tails.next();
                    TailConfig props = TailConfig.unmarshal(tail);                    
                    configs = (TailConfig[])ArrayUtil.addElement(configs, props);
                }
            }
            catch (ParseException pe)
            {
                JSmartOptionPane.showExceptionMessageDialog(this, pe);
            }
        }
        
        return configs;
    }    
    
    
    /**
     * Saves the current configuration of all tail instances to 
     * $HOME/.jtail.xml
     */
    protected void saveConfiguration()
    {
        String userHome = System.getProperty("user.home");
        String filename = userHome + File.separator + ".jtail.xml";
     
        File configFile = new File(filename);

        try
        {
            Document document = new Document();
                    
            for (Iterator i = tailMap_.keySet().iterator(); i.hasNext(); )
            {
                TailPane tail = (TailPane)tailMap_.get(i.next());
                TailConfig config = tail.getConfiguration();
                document.setRoot(DOC_JTAIL);
                document.getRoot().addElement(config.marshal());                
            }

            document.write(configFile);
            
            logger_.debug(document.toString());
        }
        catch (IOException ioe)
        {
            JSmartOptionPane.showExceptionMessageDialog(this, ioe);                        
        }
    }
    
    
    /**
     * Applies configurations
     */
    protected void applyConfiguration(TailConfig[] configs)
    {
        for (int i=0; i<configs.length; i++)
        {
            TailConfig config = configs[i];
            tail(config);
        }
    }    
    

    /**
     * Adds listeners
     */
    protected void addListeners()
    {
        fileSelectionPane_.getFileExplorer().
            addJFileExplorerListener(new FileSelectionListener());
            
        fileSelectionPane_.getTailButton().
            addActionListener(new TailButtonListener());
    }
    

    /**
     * Listener for the file explorer
     */
    class FileSelectionListener extends JFileExplorerAdapter
    {
        /**
         * Tail files that have been double clicked on
         * 
         * @param  file  File double clicked
         */
        public void fileDoubleClicked(String file)
        {
            tail(new File(file));
        }
    }
    
    
    /**
     * Menu Listener
     */
    class MenuListener implements ActionListener
    {
        
        public void actionPerformed(ActionEvent e)
        {
            saveConfiguration();
        }
    }
    
    
    /**
     * Tail button listener
     */
    class TailButtonListener implements ActionListener
    {
        /**
         * Tail button clicked
         * 
         * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            tail(new File(
                fileSelectionPane_.getFileExplorer().getFilePath()));
                
        }
    }
    
    
    /**
     * Tail button listener
     */
    class CloseButtonListener implements ActionListener
    {
        /**
         * Close button on TailPane clicked
         * 
         * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            TailPane pane = (TailPane)tailMap_.get(e.getSource());
            
            if (e != null)
            {
                tabbedPane_.remove(pane);        
            }                
        }
    }
}
