package toolbox.jtail;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Event;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import nu.xom.Element;
import nu.xom.Elements;

import org.apache.log4j.Logger;

import toolbox.jtail.config.IJTailConfig;
import toolbox.jtail.config.ITailPaneConfig;
import toolbox.jtail.config.xom.JTailConfig;
import toolbox.jtail.config.xom.TailPaneConfig;
import toolbox.util.ArrayUtil;
import toolbox.util.ExceptionUtil;
import toolbox.util.FileUtil;
import toolbox.util.SwingUtil;
import toolbox.util.XOMUtil;
import toolbox.util.file.FileStuffer;
import toolbox.util.ui.JConveyorMenu;
import toolbox.util.ui.JFileExplorerAdapter;
import toolbox.util.ui.JSmartMenu;
import toolbox.util.ui.JSmartMenuItem;
import toolbox.util.ui.SmartAction;
import toolbox.util.ui.flippane.JFlipPane;
import toolbox.util.ui.font.FontChooserException;
import toolbox.util.ui.font.IFontChooserDialogListener;
import toolbox.util.ui.font.JFontChooser;
import toolbox.util.ui.font.JFontChooserDialog;
import toolbox.util.ui.plugin.IPreferenced;
import toolbox.util.ui.plugin.IStatusBar;

/**
 * JTail is a GUI front end for {@link toolbox.tail.Tail}. <p>
 * Features include:
 * <ul>
 *   <li>Tabbed window interface (one tab per tail)
 *   <li>Ability to include/exclude lines from the output based on matching a
 *       regular expression
 *   <li>Line numbering
 *   <li>Ranges of columns can be excluded with a simple cut expression
 *   <li>Multiple tails can be aggregated so that the output for all tails goes 
 *       to a single textarea
 *   <li>Fully configurable font size, color, antialiasing, etc that are 
 *       persisted between sessions
 *   <li>Remembers most recently tailed files
 *   <li>Simple file explorer interface is used to select files
 *   <li>Saves snapshot of entire configuration so that upon re-entering the 
 *       application, it looks like you never left.       
 * </ul>
 */
public class JTail extends JFrame implements IPreferenced
{
    private static final Logger logger_ = 
        Logger.getLogger(JTail.class);

    /**
     * XML: Root node for JTail preferences
     */
    private static final String NODE_JTAIL_PLUGIN = "JTailPlugin";
    
    /**
     * XML: Node that contains 0..n RecentTail nodes 
     */
    private static final String NODE_RECENT = "Recent";
    
    /**
     * XML: Node that contains all tail information to re-hydrate a given tail
     */
    private static final String NODE_RECENT_TAIL = "RecentTail";
         
    /** 
     * Menu of recently tailed files. A tail becomes 'recent' when it is closed. 
     */
    private JMenu recentMenu_;

    /** 
     * File explorer flipper that allows the user to select a file to tail 
     */
    private FileSelectionPane fileSelectionPane_;

    /** 
     * Tab panel that contains each tail as a single tab 
     */
    private JTailTabbedPane tabbedPane_;

    /** 
     * Flip pane that houses the file explorer 
     */
    private JFlipPane flipPane_;

    /** 
     * Map of each tail that is active 
     */
    private Map tailMap_;
    
    /** 
     * Reference to the workspace statusbar
     */
    private IStatusBar statusBar_;    
    
    /**
     * Puts the application into test mode. An additional menu item is added
     * to the file menu which creates a running tail for testing purposes
     */
    private boolean testMode_ = true;
    
    /** 
     * Data object that captures all known application settings/preferences.
     */ 
    private IJTailConfig jtailConfig_;            
        
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a JTail
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
     * @param statusBar Shared status bar
     */
    public void setStatusBar(IStatusBar statusBar)
    {
        statusBar_ = statusBar;
    }

    //--------------------------------------------------------------------------
    //  Private
    //--------------------------------------------------------------------------
    
    /** 
     * Initializes JTail by building the GUI and wiring the events
     */
    protected void init()
    {
        try
        {
            tailMap_  = new HashMap();
            jtailConfig_ = new JTailConfig();
            buildView();
            wireView();
            setDefaultCloseOperation(EXIT_ON_CLOSE);            
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
        JMenu fileMenu   = new JSmartMenu("File");
        fileMenu.setMnemonic('F');
        fileMenu.add(new JSmartMenuItem(new PreferencesAction()));
        fileMenu.add(new JSmartMenuItem(new SetFontAction()));
        fileMenu.add(new JSmartMenuItem(new TailSystemOutAction()));
        fileMenu.add(new JSmartMenuItem(new TailLog4JAction()));
        fileMenu.addSeparator();
                
        if (testMode_)
        {
            fileMenu.addSeparator();
            fileMenu.add(new JSmartMenuItem(new CreateFileAction()));
        }
            
        menuBar.add(fileMenu);
        
        recentMenu_ = new JConveyorMenu("Recent", 10);
        recentMenu_.add(new JSmartMenuItem(new ClearRecentAction()));
        menuBar.add(recentMenu_);
        
        return menuBar;
    }

    /**
     * Adds a tail of the given configuration to the output area
     * 
     * @param config Tail configuration
     * @throws IOException on I/O error
     */     
    protected void addTail(ITailPaneConfig config) throws IOException
    {
        TailPane tailPane = new TailPane(config, statusBar_);
            
        // Create a map of [closeButton->tailPane] so that the tail pane can be 
        // reassociated if it needs to be removed from the tabbed pane
        
        JButton closeButton = tailPane.getCloseButton();
        tailMap_.put(closeButton, tailPane);
        closeButton.addActionListener(new CloseButtonListener());
        
        // Tab config
        tabbedPane_.addTab(makeTabLabel(config), tailPane);
        
        tabbedPane_.setToolTipTextAt(
            tabbedPane_.getTabCount()-1, makeTabToolTip(config));
            
        tabbedPane_.setSelectedComponent(tailPane);
        
        statusBar_.setStatus("Added tail for " + 
            ArrayUtil.toString(config.getFilenames(), false));
            
        tailPane.addTailPaneListener(tabbedPane_);
    }
    
    /**
     * Wires event listeners
     */
    protected void wireView()
    {
        fileSelectionPane_.getFileExplorer().
            addJFileExplorerListener(new FileSelectionListener());
            
        fileSelectionPane_.getTailButton().
            addActionListener(new TailButtonListener());
            
        fileSelectionPane_.getAggregateButton().
            addActionListener(new AggregateButtonListener());
    }
    
    /**
     * Returns the currently selected TailPane 
     * 
     * @return TailPane
     */
    protected TailPane getSelectedTail()
    {
        return (TailPane)tabbedPane_.getSelectedComponent();
    }
    
    /**
     * Returns the configuration of currently selected tail in the tabbed pane
     * 
     * @return ITailPaneConfig  
     */
    protected ITailPaneConfig getSelectedConfig() throws IOException
    {
        return getSelectedTail().getConfiguration();
    }
    
    /**
     * Makes an easy to read label for the TailPane tab
     * 
     * @param config TailPane configuration
     * @return Label
     */
    public static String makeTabLabel(ITailPaneConfig config)
    {
        StringBuffer tabname = new StringBuffer();
        String[] filenames = config.getFilenames();
        tabname.append("<html><center>");
        
        for (int i=0; i<filenames.length; i++)
        {
            tabname.append(FileUtil.stripPath(filenames[i]));
            
            if (i+1 < filenames.length)
                tabname.append("<br>");
        }
        
        tabname.append("</center></html>");
        
        return tabname.toString();
    }    

    /**
     * Makes an easy to read tooltip for the TailPane tab
     * 
     * @param config TailPane configuration
     * @return String
     */
    public static String makeTabToolTip(ITailPaneConfig config)
    {
        StringBuffer tabname = new StringBuffer();
        String[] filenames = config.getFilenames();
        tabname.append("<html><center>");
        
        for (int i=0; i<filenames.length; i++)
        {
            tabname.append(filenames[i]);
            
            if (i+1 < filenames.length)
                tabname.append("<br>");
        }
        
        tabname.append("</center></html>");
        
        return tabname.toString();
    }    
    
    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------    

    /**
     * @see toolbox.util.ui.plugin.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws Exception
    {
        Element root = XOMUtil.getFirstChildElement(
            prefs, NODE_JTAIL_PLUGIN, new Element(NODE_JTAIL_PLUGIN));

        jtailConfig_.applyPrefs(root);
        
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
    
        fileSelectionPane_.getFileExplorer().applyPrefs(root);
        flipPane_.applyPrefs(root);

        Element recent = root.getFirstChildElement(NODE_RECENT);
        Elements recentTails = recent.getChildElements(NODE_RECENT_TAIL);
            
        for (int i=0, n=recentTails.size(); i<n; i++)
        {
            Element recentTail = recentTails.get(i);
            TailPaneConfig config = new TailPaneConfig();
            config.applyPrefs(recentTail);
            recentMenu_.add(new TailRecentAction(config));
        }
    }
    
    /**
     * @see toolbox.util.ui.plugin.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws Exception
    {
        Element root = new Element(NODE_JTAIL_PLUGIN);
        
        ITailPaneConfig configs[] = new ITailPaneConfig[0];
        
        for (Iterator i = tailMap_.entrySet().iterator(); i.hasNext(); )
        {
            Map.Entry entry = (Map.Entry) i.next();
            TailPane tailPane = (TailPane) entry.getValue();            
            ITailPaneConfig config = tailPane.getConfiguration();
            configs = (ITailPaneConfig[]) ArrayUtil.add(configs, config);    
        }
        
        jtailConfig_.setTailConfigs(configs);
        
        jtailConfig_.savePrefs(root);
        
        // Other preferenced components
        fileSelectionPane_.getFileExplorer().savePrefs(root);
        flipPane_.savePrefs(root);
        
        // Save recent menu
        Element recent = new Element(NODE_RECENT);
        Component[] items = recentMenu_.getMenuComponents();
       
        for (int i=0; i<items.length; i++)
        {
            JMenuItem menuItem = (JMenuItem) items[i];
            Action action = menuItem.getAction();
            
            if (action instanceof TailRecentAction) 
            {
                ITailPaneConfig config = 
                    (ITailPaneConfig) action.getValue("config");

                Element recentTail = new Element(NODE_RECENT_TAIL);
                config.savePrefs(recentTail);
                recent.appendChild(recentTail);
            }
        }
        
        root.appendChild(recent);
        XOMUtil.insertOrReplace(prefs, root);
    }

    
    //--------------------------------------------------------------------------
    //  Event Listeners
    //--------------------------------------------------------------------------
    
    /**
     * Adds a tail for a file double clicked by the user via the file explorer
     */
    class FileSelectionListener extends JFileExplorerAdapter
    {
        public void fileDoubleClicked(String file)
        {
            ITailPaneConfig defaults = jtailConfig_.getDefaultConfig();
            
            ITailPaneConfig config = new TailPaneConfig();
            config.setFilenames(new String[] { file } );
            config.setAutoScroll(defaults.isAutoScroll());
            config.setShowLineNumbers(defaults.isShowLineNumbers());
            config.setAntiAlias(defaults.isAntiAliased());
            config.setFont(defaults.getFont());
            config.setRegularExpression(defaults.getRegularExpression());
            
            try
            {
                addTail(config);
            }
            catch (IOException e)
            {
                ExceptionUtil.handleUI(e, logger_);
            }
        }
    }
    
    /**
     * Adds a tail for the currently selected file in the file explorer
     */
    class TailButtonListener extends SmartAction
    {
        TailButtonListener()
        {
            super("Tail", true, false, null);
        }
        
        public void runAction(ActionEvent e) throws Exception
        {
            ITailPaneConfig defaults = jtailConfig_.getDefaultConfig();
            ITailPaneConfig config = new TailPaneConfig();
            
            config.setFilenames(new String[] 
                {fileSelectionPane_.getFileExplorer().getFilePath() } );
                
            config.setAutoScroll(defaults.isAutoScroll());
            config.setShowLineNumbers(defaults.isShowLineNumbers());
            config.setAntiAlias(defaults.isAntiAliased());
            config.setFont(defaults.getFont());
            config.setRegularExpression(defaults.getRegularExpression());
            config.setCutExpression(defaults.getCutExpression());
            config.setAutoStart(defaults.isAutoStart());            
            addTail(config);
        }
    }

    /**
     * Aggregates a file to an existing tail
     */
    class AggregateButtonListener extends SmartAction
    {
        AggregateButtonListener()
        {
            super("Aggregate", true, false, null);    
        }

        public void runAction(ActionEvent e) throws Exception
        {
            String file = fileSelectionPane_.getFileExplorer().getFilePath();
            TailPane tailPane = getSelectedTail();
            tailPane.aggregate(file);
        }
    }

    
    /**
     * Removes a tail once the close button is clicked on the tail pane
     */
    class CloseButtonListener extends SmartAction
    {
        CloseButtonListener()
        {
            super("Close", true, false, null);
        }
        
        /**
         * The source is the closeButton on the tail pane. 
         * Get the tailPane from the tailMap using the button as
         * the key and then temove the tail pane from the tabbed pane
         * and them remove the tailpane from the tailmap itself.
         */
        public void runAction(ActionEvent e) throws Exception
        {
            Object closeButton = e.getSource();
            TailPane pane = (TailPane)tailMap_.get(closeButton);
            pane.removeTailPaneListener(tabbedPane_);
            tabbedPane_.remove(pane);        
            tailMap_.remove(closeButton);
            
            // Add the closed tail to the recent menu
            recentMenu_.add(new TailRecentAction(pane.getConfiguration()));
            
            statusBar_.setStatus("Closed " + 
                ArrayUtil.toString(pane.getConfiguration().getFilenames()));
        }
    }


    //--------------------------------------------------------------------------
    //  GUI Actions
    //--------------------------------------------------------------------------
    
    /**
     * When a file is selectect from the Recent menu, this action tails the 
     * file and removes that item from the recent menu.
     */
    class TailRecentAction extends SmartAction
    {
        private ITailPaneConfig config_;
        
        TailRecentAction(ITailPaneConfig config)
        {
            super(config.getFilenames()[0], true, false, null);
            config_ = config;
            putValue("config", config_);
        }
        
        public void runAction(ActionEvent e) throws Exception
        {
            addTail(config_);
            recentMenu_.remove((Component) e.getSource());
        }
    }

    /**
     * Clears the Recent menu
     */
    class ClearRecentAction extends AbstractAction
    {
        ClearRecentAction()
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
     * Generates a file with intermittent output so that the file can be
     * tailed for testing purposes. The file is created is $user.home
     */
    class CreateFileAction extends AbstractAction
    {
        CreateFileAction()
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
    class SetFontAction extends SmartAction 
        implements IFontChooserDialogListener
    {
        private Font lastFont_;
        private boolean lastAntiAlias_;
        
        SetFontAction()
        {
            super("Set font ..", true, false, null);
            putValue(MNEMONIC_KEY, new Integer('t'));
            
            putValue(SHORT_DESCRIPTION, 
                "Sets the font of the tail output");
                
            putValue(ACCELERATOR_KEY, 
                KeyStroke.getKeyStroke(KeyEvent.VK_T, Event.CTRL_MASK));
        }
    
        public void runAction(ActionEvent e) throws Exception
        {
            // Remember state just in case user cancels operation
            lastFont_      = getSelectedConfig().getFont();
            lastAntiAlias_ = getSelectedConfig().isAntiAliased();
            
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
                config.setAntiAlias(fontChooser.isAntiAliased());
                getSelectedTail().setConfiguration(config);
            }
            catch (FontChooserException fse)
            {
                ExceptionUtil.handleUI(fse, logger_);
            }
            catch (IOException ioe)
            {
                ExceptionUtil.handleUI(ioe, logger_);
            }
        }

        public void cancelButtonPressed(JFontChooser fontChooser)
        {
            // Restore saved state
            ITailPaneConfig config;
            try
            {
                config = getSelectedConfig();
                config.setFont(lastFont_);            
                config.setAntiAlias(lastAntiAlias_);
                getSelectedTail().setConfiguration(config);
            }
            catch (IOException e)
            {
                ExceptionUtil.handleUI(e, logger_);
            }
        }
        
        public void okButtonPressed(JFontChooser fontChooser)
        {
            try
            {
                // Use new settings
                ITailPaneConfig config = getSelectedConfig();
                config.setFont(fontChooser.getSelectedFont());
                config.setAntiAlias(fontChooser.isAntiAliased());
                getSelectedTail().setConfiguration(config);
            }
            catch (FontChooserException fse)
            {
                ExceptionUtil.handleUI(fse, logger_);
            }
            catch (IOException e)
            {
                ExceptionUtil.handleUI(e, logger_);
            }
        }
    }
    
    /**
     * Pops up the preferences dialog
     */
    class PreferencesAction extends AbstractAction 
    {
        PreferencesAction()
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
    class TailSystemOutAction extends SmartAction
    {
        TailSystemOutAction()
        {
            super("Tail System.out", true, false, null);
            putValue(MNEMONIC_KEY, new Integer('o'));
            
            putValue(ACCELERATOR_KEY, 
                KeyStroke.getKeyStroke(
                    KeyEvent.VK_O, 
                    Event.CTRL_MASK));
        }
                
        public void runAction(ActionEvent e) throws Exception
        {
            ITailPaneConfig config = new TailPaneConfig();
            config.setAntiAlias(false);
            config.setAutoScroll(true);
            config.setAutoStart(true);
            config.setCutExpression("");
            config.setFilenames(new String[] {TailPane.LOG_SYSTEM_OUT});
            config.setFont(SwingUtil.getPreferredMonoFont());
            config.setRegularExpression("");
            config.setShowLineNumbers(false);
            addTail(config);
        }
    }
    
    /**
     * Adds a tail for Log4J attached to the "toolbox" logger
     */
    class TailLog4JAction extends SmartAction
    {
        TailLog4JAction()
        {
            super("Tail Log4J", true, false, null);
            putValue(MNEMONIC_KEY, new Integer('j'));

            putValue(ACCELERATOR_KEY, 
                KeyStroke.getKeyStroke(KeyEvent.VK_J, Event.CTRL_MASK));
        }
                
        public void runAction(ActionEvent e) throws Exception
        {
            ITailPaneConfig config = new TailPaneConfig();
            config.setAntiAlias(false);
            config.setAutoScroll(true);
            config.setAutoStart(true);
            config.setCutExpression("");
            config.setFilenames(new String[] {TailPane.LOG_LOG4J});
            config.setFont(SwingUtil.getPreferredMonoFont());
            config.setRegularExpression("");
            config.setShowLineNumbers(false);
            addTail(config);
        }
    }
}   