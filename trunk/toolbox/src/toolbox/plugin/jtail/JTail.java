package toolbox.jtail;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Event;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;

import toolbox.jtail.config.IConfigManager;
import toolbox.jtail.config.IJTailConfig;
import toolbox.jtail.config.ITailPaneConfig;
import toolbox.jtail.config.tinyxml.ConfigManager;
import toolbox.jtail.config.tinyxml.TailPaneConfig;
import toolbox.util.ArrayUtil;
import toolbox.util.ExceptionUtil;
import toolbox.util.FileUtil;
import toolbox.util.PropertiesUtil;
import toolbox.util.SwingUtil;
import toolbox.util.file.FileStuffer;
import toolbox.util.ui.JFileExplorerAdapter;
import toolbox.util.ui.flippane.JFlipPane;
import toolbox.util.ui.font.FontChooserException;
import toolbox.util.ui.font.IFontChooserDialogListener;
import toolbox.util.ui.font.JFontChooser;
import toolbox.util.ui.font.JFontChooserDialog;
import toolbox.util.ui.plugin.IStatusBar;
import toolbox.util.xml.XMLParser;

/**
 * JTail is a GUI front end for {@link toolbox.tail.Tail}.
 */
public class JTail extends JFrame
{
    /*
     * TODO: Add configurable "aggregator" for existing tails 
     */

    private static final Logger logger_ = 
        Logger.getLogger(JTail.class);
         
    private int lastNumRecent_;

    /** Presents list of recently tailed files */
    private JMenu recentMenu_;

    /** File explorer flipper that allows the user to select a file to tail */
    private FileSelectionPane fileSelectionPane_;
    
    /** Tab panel that contains each tail as a single tab */
    private JTabbedPane tabbedPane_;
    
    /** Flip panel that houses the file explorer */
    private JFlipPane flipPane_;    

    /** Map of each tail that is active */
    private Map tailMap_;
    
    /** 
     * Status bar at the bottom of the screen that shows the status of various 
     * activities
     */
    private IStatusBar statusBar_;    
    
    /**
     * Puts the application into test mode. An additional menu item is added
     * to the file menu which creates a running tail for testing purposes
     */
    private boolean testMode_ = true;
    
    /** 
     * Configuration manager which oversees the reading and saving of 
     * application specific preferences. This includes the current active
     * tails and also window size/position, etc.
     */
    private IConfigManager configManager_ = new ConfigManager();
    
    /** 
     * Data object that captures all known application settings/preferences. 
     * The ConfigManager is responsible for saving/loading this object between
     * application instances.
     */
    private IJTailConfig jtailConfig_;            
        
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Entry point 
     * 
     * @param  args  None recognized
     * @throws Exception on error
     */
    public static void main(String[] args) throws Exception
    {
        SwingUtil.setPreferredLAF();        
        JTail jtail = new JTail();
        jtail.setVisible(true);
        jtail.applyConfiguration(null);
    }

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Default constructor
     */
    public JTail()
    {
        super("JTail");
        init();        
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Sets the status bar
     * 
     * @param  statusBar  Shared status bar
     */
    public void setStatusBar(IStatusBar statusBar)
    {
        statusBar_ = statusBar;
    }

    //--------------------------------------------------------------------------
    //  Private
    //--------------------------------------------------------------------------
    
    /** 
     * Initializes JTail: builds the GUI, wires the events, and loads the
     * configuration
     */
    protected void init()
    {
        try
        {
            // Init variables
            tailMap_  = new HashMap();
            
            buildView();
            wireView();
            setDefaultCloseOperation(EXIT_ON_CLOSE);            
            loadConfiguration();
        }
        catch (Exception e)
        {
            ExceptionUtil.handleUI(e, logger_);
        }
    }

    /**
     * Builds the GUI
     */
    protected void buildView()
    {
        getContentPane().setLayout(new BorderLayout());
        
        fileSelectionPane_ = new FileSelectionPane();
        
        flipPane_ = new JFlipPane(JFlipPane.LEFT);
        flipPane_.addFlipper("File Explorer", fileSelectionPane_);
        tabbedPane_ = new JTailTabbedPane();

        getContentPane().add(BorderLayout.WEST, flipPane_);
        getContentPane().add(BorderLayout.CENTER, tabbedPane_);
        
        getContentPane().add(BorderLayout.NORTH, buildMenuBar());
    }
    
    /**
     * Builds the menu bar
     * 
     * @return Menu bar
     */
    protected JMenuBar buildMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu   = new JMenu("File");
        fileMenu.setMnemonic('F');
        fileMenu.add(new PreferencesAction());
        fileMenu.add(new SetFontAction());
        fileMenu.add(new TailSystemOutAction());
        fileMenu.add(new TailLog4JAction());
        fileMenu.addSeparator();
        fileMenu.add(new SaveAction());
        
        if (testMode_)
        {
            fileMenu.addSeparator();
            fileMenu.add(new CreateFileAction());
        }
            
        menuBar.add(fileMenu);
        
        recentMenu_ = new JMenu("Recent");
        recentMenu_.add(new ClearRecentAction());
        menuBar.add(recentMenu_);
        
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
            logger_.debug(config);
            TailPane tailPane = new TailPane(config, statusBar_);
 
            JButton closeButton = tailPane.getCloseButton();
            
            // Create map of (closeButton, tailPane) so that the 
            // tail pane can be reassociated if it needs to be 
            // removed from the tabbed pane
            tailMap_.put(closeButton, tailPane);
            
            closeButton.addActionListener(new CloseButtonListener());
            
            tabbedPane_.addTab(
                FileUtil.stripPath(config.getFilename()), tailPane);
                
            tabbedPane_.setToolTipTextAt(
                tabbedPane_.getTabCount()-1, config.getFilename());
            
                
            tabbedPane_.setSelectedComponent(tailPane);
            
            statusBar_.setStatus("Added tail for " + config.getFilename());
        }
        catch (IOException e)
        {
            ExceptionUtil.handleUI(e, logger_);
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
     * Adds listeners
     */
    protected void wireView()
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
     * @return  Configuration of currently selected tail in the tabbed pane
     */
    protected ITailPaneConfig getSelectedConfig()
    {
        return getSelectedTail().getConfiguration();
    }
    
    //--------------------------------------------------------------------------
    // Preferences Support
    //--------------------------------------------------------------------------    

    /**
     * Saves the current configuration of all tail instances (delegated to 
     * configuration manager).
     * 
     * @param  props  Used to save file explorer and flip pane properties
     */
    protected void saveConfiguration(Properties props)
    {
        try
        {
            // Size and location
            jtailConfig_.setSize(getSize());
            jtailConfig_.setLocation(getLocation());
            
            // Last selected directory
            String path = fileSelectionPane_.getFileExplorer().getCurrentPath();
            jtailConfig_.setDirectory(path);
            
            // Since each of the ITailPaneConfigs are maintained inside the 
            // TailPane itself, gather them up and update jtailConfig before
            // saving. Essentially, do the reverse of applyConfiguration(). 
            
            ITailPaneConfig configs[] = new ITailPaneConfig[0];
            
            for (Iterator i = tailMap_.entrySet().iterator(); i.hasNext(); )
            {
                Map.Entry entry = (Map.Entry) i.next();
                TailPane tailPane = (TailPane)entry.getValue();            
                ITailPaneConfig config = tailPane.getConfiguration();
                configs = (ITailPaneConfig[])ArrayUtil.add(configs, config);    
            }
            
            jtailConfig_.setTailConfigs(configs);
            
            configManager_.save(jtailConfig_);
            
            // Save properties that are separate from 
            // IJTailConfig/ITailPaneConfig
            if (props != null)
            {
                fileSelectionPane_.getFileExplorer().savePrefs(props, "jtail");
                flipPane_.savePrefs(props, "jtail");
            }
            
            // Save recent menu
            Component[] items = recentMenu_.getMenuComponents();
            
            // Sub 1 for the unaccounted "Clear" menuitem
            PropertiesUtil.setInteger(
                props, "jtail.recent.num", items.length - 1); 
            
            int cnt = 0;
            
            // Clear out old properties since the number we're now persisting
            // may have changed and that will leave dangling properties
            for (int i=0; i<lastNumRecent_-1; i++)
                props.remove("jtail.recent.tail." + i++);
                
            
            for (int i=0; i<items.length; i++)
            {
                logger_.debug(
                    "Menuitem " + i + " = " + items[i].getClass().getName());
                
                JMenuItem menuItem = (JMenuItem) items[i];
                
                Action a = (Action) menuItem.getAction();
                
                if (a instanceof TailRecentAction) 
                {
                    TailRecentAction action = (TailRecentAction) menuItem.getAction();
                    
                    ITailPaneConfig config = 
                        (ITailPaneConfig) action.getValue("config");
                        
                    props.setProperty(
                        "jtail.recent.tail." + cnt++, config.toString());
                }
            }
            
            lastNumRecent_ = cnt;
        }
        catch (Throwable t)
        {
            ExceptionUtil.handleUI(t, logger_);
        }
    }
    
    /**
     * Applies configurations
     * 
     * @param  props  File explorer and flip pane properties read from here
     */
    protected void applyConfiguration(Properties props)
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
        
        // Last selected directory
        if (jtailConfig_.getDirectory() != null)
        {
            fileSelectionPane_.getFileExplorer().selectFolder(
                jtailConfig_.getDirectory());
        }
        
        // Tails left running since last save
        ITailPaneConfig[] tailPaneConfigs = jtailConfig_.getTailConfigs();
        
        for (int i=0; i< tailPaneConfigs.length; i++)
        {
            ITailPaneConfig config = tailPaneConfigs[i];
            
            // Apply defaults if any
            if (config.getFont() == null)
                config.setFont(jtailConfig_.getDefaultConfig().getFont());
            
            addTail(config);
        }
        
        // Apply properties that are separate from IJTailConfig/ITailPaneConfig
        if (props != null)
        {
            fileSelectionPane_.getFileExplorer().applyPrefs(props, "jtail");
            flipPane_.applyPrefs(props, "jtail");
        }
        
        int numRecent = PropertiesUtil.getInteger(props, "jtail.recent.num", 0);

        XMLParser parser = new XMLParser();

        for (int i=0; i<numRecent; i++)
        {
            String xmlConfig = props.getProperty("jtail.recent.tail." + i);
            TailPaneConfig config = null;
            
            try
            {
                logger_.debug("tailnum=" + i + " xml=" + xmlConfig);
                
                config = TailPaneConfig.unmarshal(
                    parser.parseXML(new StringReader(xmlConfig)));

                recentMenu_.add(new TailRecentAction(config));
            }
            catch (IOException ioe)
            {
                
                ExceptionUtil.handleUI(ioe, logger_);
            }
        }        
    }    
    
    //--------------------------------------------------------------------------
    //  Event Listeners
    //--------------------------------------------------------------------------
    
    /**
     * Adds a tail for a file double clicked by the user via the file explorer
     */
    private class FileSelectionListener extends JFileExplorerAdapter
    {
        public void fileDoubleClicked(String file)
        {
            ITailPaneConfig defaults = jtailConfig_.getDefaultConfig();
            
            ITailPaneConfig config = configManager_.createTailPaneConfig();
            config.setFilename(file);
            config.setAutoScroll(defaults.isAutoScroll());
            config.setShowLineNumbers(defaults.isShowLineNumbers());
            config.setAntiAlias(defaults.isAntiAlias());
            config.setFont(defaults.getFont());
            config.setRegularExpression(defaults.getRegularExpression());
            
            addTail(config);
        }
    }
    
    /**
     * Adds a tail for the currently selected file in the file explorer
     */
    private class TailButtonListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            ITailPaneConfig defaults = jtailConfig_.getDefaultConfig();
            ITailPaneConfig config = configManager_.createTailPaneConfig();
            
            config.setFilename(
                fileSelectionPane_.getFileExplorer().getFilePath());
                
            config.setAutoScroll(defaults.isAutoScroll());
            config.setShowLineNumbers(defaults.isShowLineNumbers());
            config.setAntiAlias(defaults.isAntiAlias());
            config.setFont(defaults.getFont());
            config.setRegularExpression(defaults.getRegularExpression());
            config.setCutExpression(defaults.getCutExpression());
            config.setAutoStart(defaults.isAutoStart());            
            addTail(config);
        }
    }
    
    /**
     * Removes a tail once the close button is clicked on the tail pane
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
            
            // Add the closed tail to the recent menu
            recentMenu_.add(new TailRecentAction(pane.getConfiguration()));
            
            statusBar_.setStatus(
                "Closed " + pane.getConfiguration().getFilename());
        }
    }

    /**
     * Saves the configuration when the application is being closed
     */
    private class WindowListener extends WindowAdapter
    {
        public void windowClosing(WindowEvent e)
        {
            saveConfiguration(null);
        }
    }

    //--------------------------------------------------------------------------
    //  GUI Actions
    //--------------------------------------------------------------------------
    
    /**
     * When a file is selectect from the Recent menu, this action tails the 
     * file and removes that item from the recent menu.
     */
    private class TailRecentAction extends AbstractAction
    {
        private ITailPaneConfig config_;
        
        public TailRecentAction(ITailPaneConfig config)
        {
            super(config.getFilename());
            config_ = config;
            putValue("config", config_);
        }
        
        public void actionPerformed(ActionEvent e)
        {
            addTail(config_);
            recentMenu_.remove((Component) e.getSource());
        }
    }

    /**
     * Clears the Recent menu
     */
    private class ClearRecentAction extends AbstractAction
    {
        public ClearRecentAction()
        {
            super("Clear");
        }
        
        public void actionPerformed(ActionEvent e)
        {
            Component[] items = recentMenu_.getMenuComponents();
        
            for (int i=items.length-1; i >= 0; i--)
            {
                JMenuItem menuItem = (JMenuItem) items[i];
                if (menuItem.getAction() instanceof TailRecentAction)
                    recentMenu_.remove(menuItem);
            }
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
    
        public void actionPerformed(ActionEvent e)
        {
            saveConfiguration(null);
            statusBar_.setStatus("Saved configuration");
        }
    }
    
    /**
     * Generates a file with intermittent output so that the file can be
     * tailed for testing purposes. The file is created is $user.home
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
    
        public void actionPerformed(ActionEvent e)
        {
            String file = FileUtil.trailWithSeparator(
                System.getProperty("user.home")) + "jtail.test.txt";
                 
            FileStuffer stuffer = new FileStuffer(new File(file), 100);
            stuffer.start();
            statusBar_.setStatus("Created " + file + " for tailing");
        }
    }
    
    /**
     * Pops up a font selection dialog to change the font
     */
    private class SetFontAction extends AbstractAction 
        implements IFontChooserDialogListener
    {
        private Font lastFont_;
        private boolean lastAntiAlias_;
        
        public SetFontAction()
        {
            super("Set font ..");
            putValue(MNEMONIC_KEY, new Integer('t'));
            
            putValue(SHORT_DESCRIPTION, 
                "Sets the font of the tail output");
                
            putValue(ACCELERATOR_KEY, 
                KeyStroke.getKeyStroke(KeyEvent.VK_T, Event.CTRL_MASK));
        }
    
        public void actionPerformed(ActionEvent e)
        {
            // Remember state just in case user cancels operation
            lastFont_      = getSelectedConfig().getFont();
            lastAntiAlias_ = getSelectedConfig().isAntiAlias();
            
            // Show font selection dialog with font from the current
            // tail set as the default selected font
            JFontChooserDialog fsd = new JFontChooserDialog(
                JTail.this, false, lastFont_, lastAntiAlias_);
                    
            fsd.setTitle("Select font");
            
            fsd.addFontDialogListener(this);
            SwingUtil.centerWindow(fsd);
            fsd.setVisible(true);
        }
        
        //----------------------------------------------------------------------
        // Interface IFontChooserDialogListener
        //----------------------------------------------------------------------
        
        public void applyButtonPressed(JFontChooser fontChooser)
        {
            try
            {
                // Apply current settings
                ITailPaneConfig config = getSelectedConfig();
                config.setFont(fontChooser.getSelectedFont());
                config.setAntiAlias(fontChooser.isAntiAlias());
                getSelectedTail().setConfiguration(config);
            }
            catch (FontChooserException fse)
            {
                ExceptionUtil.handleUI(fse, logger_);
            }
        }

        public void cancelButtonPressed(JFontChooser fontChooser)
        {
            // Restore saved state
            ITailPaneConfig config = getSelectedConfig();
            config.setFont(lastFont_);            
            config.setAntiAlias(lastAntiAlias_);
            getSelectedTail().setConfiguration(config);
        }
        
        public void okButtonPressed(JFontChooser fontChooser)
        {
            try
            {
                // Use new settings
                ITailPaneConfig config = getSelectedConfig();
                config.setFont(fontChooser.getSelectedFont());
                config.setAntiAlias(fontChooser.isAntiAlias());
                getSelectedTail().setConfiguration(config);
            }
            catch (FontChooserException fse)
            {
                ExceptionUtil.handleUI(fse, logger_);
            }
        }
    }
    
    /**
     * Pops up the preferences dialog
     */
    private class PreferencesAction extends AbstractAction 
    {
        public PreferencesAction()
        {
            super("Preferences ..");
            putValue(MNEMONIC_KEY, new Integer('P'));
            
            putValue(SHORT_DESCRIPTION, 
                "View/change the default preferences");
                
            putValue(ACCELERATOR_KEY, 
                KeyStroke.getKeyStroke(KeyEvent.VK_P, Event.CTRL_MASK));
        }
    
        public void actionPerformed(ActionEvent e)
        {
            // Show font selection dialog with font from the current
            // tail set as the default selected font
            PreferencesDialog pd = 
                new PreferencesDialog(JTail.this, jtailConfig_);
                
            SwingUtil.centerWindow(pd);
            pd.setVisible(true);
        }
    }
    
    /**
     * Adds a tail of the System.out stream
     */
    private class TailSystemOutAction extends AbstractAction
    {
        public TailSystemOutAction()
        {
            super("Tail System.out");
            putValue(MNEMONIC_KEY, new Integer('o'));
            
            putValue(ACCELERATOR_KEY, 
                KeyStroke.getKeyStroke(
                    KeyEvent.VK_O, 
                    Event.CTRL_MASK));
        }
                
        public void actionPerformed(ActionEvent e)
        {
            ITailPaneConfig config = configManager_.createTailPaneConfig();
            config.setAntiAlias(false);
            config.setAutoScroll(true);
            config.setAutoStart(true);
            config.setCutExpression("");
            config.setFilename(TailPane.LOG_SYSTEM_OUT);
            config.setFont(SwingUtil.getPreferredMonoFont());
            config.setRegularExpression("");
            config.setShowLineNumbers(false);
            addTail(config);
        }
    }
    
    /**
     * Adds a tail for Log4J attached to the "toolbox" logger
     */
    private class TailLog4JAction extends AbstractAction
    {
        public TailLog4JAction()
        {
            super("Tail Log4J");
            putValue(MNEMONIC_KEY, new Integer('j'));

            putValue(ACCELERATOR_KEY, 
                KeyStroke.getKeyStroke(KeyEvent.VK_J, Event.CTRL_MASK));
        }
                
        public void actionPerformed(ActionEvent e)
        {
            ITailPaneConfig config = configManager_.createTailPaneConfig();
            config.setAntiAlias(false);
            config.setAutoScroll(true);
            config.setAutoStart(true);
            config.setCutExpression("");
            config.setFilename(TailPane.LOG_LOG4J);
            config.setFont(SwingUtil.getPreferredMonoFont());
            config.setRegularExpression("");
            config.setShowLineNumbers(false);
            addTail(config);
        }
    }
}   