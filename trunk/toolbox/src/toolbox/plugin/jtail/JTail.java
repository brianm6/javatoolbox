package toolbox.jtail;

import java.awt.BorderLayout;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

import org.apache.log4j.Category;
import toolbox.util.ArrayUtil;
import toolbox.util.ExceptionUtil;
import toolbox.util.SwingUtil;
import toolbox.util.file.FileStuffer;
import toolbox.util.ui.JFileExplorerAdapter;
import toolbox.util.ui.JSmartOptionPane;
import toolbox.util.ui.JSmartStatusBar;
import toolbox.util.ui.font.FontSelectionDialog;
import toolbox.util.ui.font.FontSelectionException;
import toolbox.util.ui.font.FontSelectionPanel;
import toolbox.util.ui.font.IFontDialogListener;

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
    
    
    private FileSelectionPane fileSelectionPane_;
    private JTabbedPane       tabbedPane_;
    private JSmartStatusBar   statusBar_;    
    private JMenuBar          menuBar_;    
    private Map               tailMap_;

    private boolean testMode_ = true;
        
        
    /**
     * Entry point 
     * 
     * @param  args  None recognized
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
     * 
     * @return Menu bar
     */
    protected JMenuBar createMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu   = new JMenu("File");
        fileMenu.setMnemonic('F');
        fileMenu.add(new SetFontAction());
        fileMenu.addSeparator();
        fileMenu.add(new SaveAction());
        fileMenu.add(new ExitAction());
        
        if (testMode_)
        {
            fileMenu.addSeparator();
            fileMenu.add(new CreateFileAction());
        }
            
        menuBar.add(fileMenu);
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
            logger_.debug("[tail  ]\n" + config);
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
            document.setRoot(DOC_JTAIL);                    

            logger_.debug("[save  ] Saving " + tailMap_.size() + " configurations");
            
            for (Iterator i = tailMap_.keySet().iterator(); i.hasNext(); )
            {
                TailPane tail = (TailPane)tailMap_.get(i.next());
                TailConfig config = tail.getConfiguration();
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
    private class FileSelectionListener extends JFileExplorerAdapter
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
     * Tail button listener
     */
    private class TailButtonListener implements ActionListener
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
    private class CloseButtonListener implements ActionListener
    {
        /**
         * The source is the closeButton on the tail pane. 
         * Get the tailPane from the tailMap using the button as
         * the key and then temove the tail pane from the tabbed pane
         * and them remove the tailpane from the tailmap itself.
         * 
         * @param  e  ActionEvent
         */
        public void actionPerformed(ActionEvent e)
        {
            Object closeButton = e.getSource();
            TailPane pane = (TailPane)tailMap_.get(closeButton);
            tabbedPane_.remove(pane);        
            tailMap_.remove(closeButton);
        }
    }
    
    /**
     * Action to exit the application. The configurations
     * are saved before exit.
     */
    private class ExitAction extends AbstractAction
    {
        public ExitAction()
        {
            super("Exit");
            putValue(MNEMONIC_KEY, new Integer('x'));    
            putValue(SHORT_DESCRIPTION, "Exits JTail");
            putValue(LONG_DESCRIPTION, "Saves configuraiton and exits JTail");
            putValue(ACCELERATOR_KEY, 
                KeyStroke.getKeyStroke(KeyEvent.VK_X, Event.CTRL_MASK));
        }
        
        /**
         * Saves the configuration and exits the application
         */
        public void actionPerformed(ActionEvent e)
        {
            saveConfiguration();
            System.exit(0);
        }
    }

    /**
     * Action to save configurations
     */
    private class SaveAction extends AbstractAction
    {
        public SaveAction()
        {
            super("Save");
            putValue(MNEMONIC_KEY, new Integer('S'));
            putValue(SHORT_DESCRIPTION, "Saves tail configuration");
            putValue(LONG_DESCRIPTION, "Saves tail configuration to .jtail.xml");
            putValue(ACCELERATOR_KEY, 
                KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));
        }
    
        /**
         * Saves the configuration
         */
        public void actionPerformed(ActionEvent e)
        {
            saveConfiguration();
            statusBar_.setStatus("Saved configuration");
        }
    }


    /**
     * Generates a file with intermittent output so that the file can be
     * tailed.
     */
    private class CreateFileAction extends AbstractAction
    {
        public CreateFileAction()
        {
            super("Create test file");
            putValue(MNEMONIC_KEY, new Integer('C'));
            putValue(SHORT_DESCRIPTION, "Create test file to tail");
            
            putValue(LONG_DESCRIPTION, 
                "Create a file to tail for testing purposes");
            
            putValue(ACCELERATOR_KEY, 
                KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.CTRL_MASK));
        }
    
        /**
         * Saves the configuration
         */
        public void actionPerformed(ActionEvent e)
        {
            String file = "c:\\crap.txt";
            FileStuffer stuffer = new FileStuffer(new File(file), 100);
            stuffer.start();
            statusBar_.setStatus("Created " + file + " for tailing");
        }
    }
    
    /**
     * Select Font action
     */
    private class SetFontAction extends AbstractAction 
        implements IFontDialogListener
    {
        public SetFontAction()
        {
            super("Set font ..");
            putValue(MNEMONIC_KEY, new Integer('S'));
            putValue(SHORT_DESCRIPTION, "Sets the font of the tail output");
            putValue(ACCELERATOR_KEY, 
                KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.CTRL_MASK));
        }
    
        /**
         * Saves the configuration
         */
        public void actionPerformed(ActionEvent e)
        {
            FontSelectionDialog fsd = new FontSelectionDialog(JTail.this, false);
            fsd.addFontDialogListener(this);
            SwingUtil.centerWindow(fsd);
            fsd.setVisible(true);
        }
        
        /**
         * Sets the font of the currently selected tail to the chosen font in 
         * the font selection dialog
         * 
         * @param  fontPanel  Font selection panel in the dialog box
         */
        public void applyButtonPressed(FontSelectionPanel fontPanel)
        {
            try
            {
                TailPane pane = (TailPane) tabbedPane_.getSelectedComponent();
                pane.setTailFont(fontPanel.getSelectedFont());
            }
            catch (FontSelectionException fse)
            {
                JSmartOptionPane.showExceptionMessageDialog(JTail.this, fse);
            }
        }

        /**
         *  Cancel button was pressed 
         */
        public void cancelButtonPressed(FontSelectionPanel fontPanel)
        {
            // Do nothing
        }
        
        /**
         * OK button was pressed
         */
        public void okButtonPressed(FontSelectionPanel fontPanel)
        {
            try
            {
                TailPane pane = (TailPane) tabbedPane_.getSelectedComponent();
                pane.setTailFont(fontPanel.getSelectedFont());
            }
            catch (FontSelectionException fse)
            {
                JSmartOptionPane.showExceptionMessageDialog(JTail.this, fse);
            }
        }
    }
}