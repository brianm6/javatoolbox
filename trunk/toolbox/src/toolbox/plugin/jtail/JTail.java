package toolbox.jtail;

import java.awt.BorderLayout;
import java.awt.Event;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

import org.apache.log4j.Category;

import toolbox.jtail.config.*;
import toolbox.jtail.exml.ConfigManager;
import toolbox.jtail.exml.TailPaneConfig;
import toolbox.util.ArrayUtil;
import toolbox.util.ExceptionUtil;
import toolbox.util.SwingUtil;
import toolbox.util.file.FileStuffer;
import toolbox.util.ui.JFileExplorerAdapter;
import toolbox.util.ui.JSmartOptionPane;
import toolbox.util.ui.JSmartStatusBar;
import toolbox.util.ui.font.FontSelectionDialog;
import toolbox.util.ui.font.FontSelectionException;
import toolbox.util.ui.font.FontSelectionPane;
import toolbox.util.ui.font.IFontDialogListener;

/**
 * GUI front end to the tail
 */
public class JTail extends JFrame
{
    /** Logger **/
    private static final Category logger_ = 
        Category.getInstance(JTail.class);

    private FileSelectionPane fileSelectionPane_;
    private JTabbedPane       tabbedPane_;
    private JSmartStatusBar   statusBar_;    
    private JMenuBar          menuBar_;    
    private Map               tailMap_;
    private boolean           testMode_ = true;

    /** Configuration manager **/
    private IConfigManager configManager_ = new ConfigManager();
    
    /** JTail configuration information **/
    private IJTailConfig jtailConfig_;            
        
        
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

    //
    //  CONSTRUCTORS
    //
    
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

    //
    //  IMPLEMENTATION
    //
    
    /** 
     * Initializes program
     */
    protected void init()
    {
        try
        {
            // Init variables
            tailMap_  = new HashMap();
            
            buildView();
            addListeners();
            setDefaultCloseOperation(EXIT_ON_CLOSE);            
            loadConfiguration();
            applyConfiguration();
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
        tabbedPane_ = new JTailTabbedPane();

        JSplitPane rootSplitPane = 
            new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                fileSelectionPane_, 
                tabbedPane_);                

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
     * Adds a tail of the given configuration to the output area
     * 
     * @param  config  Tail configuration
     */     
    protected void addTail(ITailPaneConfig config)
    {
        try
        {
            logger_.debug("[tail  ]\n" + config);
            TailPane tailPane = new TailPane(config);
 
            JButton closeButton = tailPane.getCloseButton();
            
            // Create map of (closeButton, tailPane) so that the 
            // tail pane can be reassociated if it needs to be 
            // removed from the tabbed pane
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
     * Loads properties (delegated to configuration manager)
     * 
     * @throws  IOException on I/O error
     */
    protected void loadConfiguration()
    {
        jtailConfig_ = configManager_.load();
    }    
    
    
    /**
     * Saves the current configuration of all tail instances
     * (delegated to configuration manager)
     */
    protected void saveConfiguration()
    {
        // Since each of the ITailPaneConfigs are maintained inside the 
        // TailPane itself, gather them up and update jtailConfig before
        // saving. Essentially, do the reverse of applyConfiguration(). 
        
        ITailPaneConfig configs[] = new ITailPaneConfig[0];
        
        for (Iterator i = tailMap_.entrySet().iterator(); i.hasNext(); )
        {
            Map.Entry entry = (Map.Entry) i.next();
            TailPane tailPane = (TailPane)entry.getValue();            
            ITailPaneConfig config = tailPane.getConfiguration();
            configs = (ITailPaneConfig[])ArrayUtil.addElement(configs, config);    
        }
        jtailConfig_.setTailPaneConfigs(configs);
        
        
        configManager_.save(jtailConfig_);
    }
    
    
    /**
     * Applies configurations
     * 
     * @param  configs  Tail configurations
     */
    protected void applyConfiguration()
    {
        // Window size
        if (jtailConfig_.getSize() != null)
            setSize(jtailConfig_.getSize());
        else
            setSize(800,400);
        
        // Window location    
        if (jtailConfig_.getLocation() != null)
            setLocation(jtailConfig_.getLocation());
        else
            SwingUtil.centerWindow(this);
        
        // Tails left running since last save
        ITailPaneConfig[] tailPaneConfigs = jtailConfig_.getTailPaneConfigs();
        for (int i=0; i< tailPaneConfigs.length; i++)
        {
            ITailPaneConfig config = tailPaneConfigs[i];
            
            // Apply defaults if any
            if (config.getFont() == null)
                config.setFont(getDefaultFont());
                
            addTail(config);
        }
    }    
    

    /**
     * Adds listeners
     */
    protected void addListeners()
    {
        // Intercept closing of app to save configuration
        addWindowListener(new WindowListener());
        
        fileSelectionPane_.getFileExplorer().
            addJFileExplorerListener(new FileSelectionListener());
            
        fileSelectionPane_.getTailButton().
            addActionListener(new TailButtonListener());
    }
    
    
    /**
     * @return  Currently selected tail in the tabbed pane
     */
    protected TailPane getSelectedTail()
    {
        return (TailPane)tabbedPane_.getSelectedComponent();
    }
    
    
    /**
     * @return  Default font
     */
    protected Font getDefaultFont()
    {
        return jtailConfig_.getDefaultFont();
    }


    //
    //  LISTENERS
    //

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
            // TODO: fix to use factory
            ITailPaneConfig config = new TailPaneConfig(file, true, false, 
                SwingUtil.getPreferredMonoFont(), "");
                
            addTail(config);
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
            
            ITailPaneConfig config = new TailPaneConfig(
                fileSelectionPane_.getFileExplorer().getFilePath(), 
                    true, false, SwingUtil.getPreferredMonoFont(), "");
            
            addTail(config);
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
            
            // Save the configuration since the tail is gone
            saveConfiguration();
        }
    }


    /**
     * Saves the configuration when the application is being closed
     */
    private class WindowListener extends WindowAdapter
    {
        public void windowClosing(WindowEvent e)
        {
            saveConfiguration();
        }
    }

    //
    //  ACTIONS
    //
    
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
            super("Save Settings");
            putValue(MNEMONIC_KEY, new Integer('S'));
            putValue(SHORT_DESCRIPTION, "Saves tail configuration");
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
        private Font lastFont_;
        
        public SetFontAction()
        {
            super("Set font ..");
            putValue(MNEMONIC_KEY, new Integer('t'));
            
            putValue(SHORT_DESCRIPTION, 
                "Sets the font of the tail output");
                
            putValue(ACCELERATOR_KEY, 
                KeyStroke.getKeyStroke(KeyEvent.VK_T, Event.CTRL_MASK));
        }
    
        /**
         * Pops up the Font selection dialog
         */
        public void actionPerformed(ActionEvent e)
        {
            lastFont_ = getSelectedTail().getTailFont();
            
            // Show font selection dialog with font from the current
            // tail set as the default selected font
            FontSelectionDialog fsd = 
                new FontSelectionDialog(JTail.this, false, 
                    lastFont_);
            fsd.setTitle("Select font");
            
            fsd.addFontDialogListener(this);
            SwingUtil.centerWindow(fsd);
            fsd.setVisible(true);
        }
        
        /**
         * Sets the font of the currently selected tail to the chosen 
         * font in the font selection dialog
         * 
         * @param  fontPanel  Font selection panel in the dialog box
         */
        public void applyButtonPressed(FontSelectionPane fontPanel)
        {
            try
            {
                getSelectedTail().setTailFont(
                    fontPanel.getSelectedFont());
            }
            catch (FontSelectionException fse)
            {
                JSmartOptionPane.showExceptionMessageDialog(
                    JTail.this, fse);
            }
        }

        /**
         *  Cancel button was pressed 
         */
        public void cancelButtonPressed(FontSelectionPane fontPanel)
        {
            // Restore last font
            getSelectedTail().setTailFont(lastFont_);            
        }
        
        /**
         * OK button was pressed
         */
        public void okButtonPressed(FontSelectionPane fontPanel)
        {
            try
            {
                getSelectedTail().setTailFont(
                    fontPanel.getSelectedFont());
            }
            catch (FontSelectionException fse)
            {
                JSmartOptionPane.showExceptionMessageDialog(
                    JTail.this, fse);
            }
        }
    }
}