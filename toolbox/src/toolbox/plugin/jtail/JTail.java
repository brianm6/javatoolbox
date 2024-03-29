package toolbox.plugin.jtail;

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
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import nu.xom.Element;
import nu.xom.Elements;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import toolbox.plugin.jtail.config.IJTailConfig;
import toolbox.plugin.jtail.config.ITailViewConfig;
import toolbox.plugin.jtail.config.xom.JTailConfig;
import toolbox.plugin.jtail.config.xom.TailViewConfig;
import toolbox.plugin.jtail.filter.DynamicFilterView;
import toolbox.util.ArrayUtil;
import toolbox.util.ExceptionUtil;
import toolbox.util.FileUtil;
import toolbox.util.FontUtil;
import toolbox.util.SwingUtil;
import toolbox.util.XOMUtil;
import toolbox.util.file.FileStuffer;
import toolbox.util.ui.JConveyorMenu;
import toolbox.util.ui.JSmartMenu;
import toolbox.util.ui.JSmartMenuItem;
import toolbox.util.ui.SmartAction;
import toolbox.util.ui.explorer.FileExplorerAdapter;
import toolbox.util.ui.explorer.JFileExplorer;
import toolbox.util.ui.flippane.JFlipPane;
import toolbox.util.ui.font.FontChooserException;
import toolbox.util.ui.font.IFontChooserDialogListener;
import toolbox.util.ui.font.JFontChooser;
import toolbox.util.ui.font.JFontChooserDialog;
import toolbox.workspace.IPreferenced;
import toolbox.workspace.IStatusBar;
import toolbox.workspace.PreferencedException;

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
public class JTail extends JPanel implements IPreferenced
{
    private static final Logger logger_ = Logger.getLogger(JTail.class);
    
    //--------------------------------------------------------------------------
    // XML Preferences
    //--------------------------------------------------------------------------

    /**
     * Root node for JTail preferences.
     */
    private static final String NODE_JTAIL_PLUGIN = "JTailPlugin";
    
    /**
     * Node that contains 0..n RecentTail nodes. 
     */
    private static final String NODE_RECENT = "Recent";
    
    /**
     * Node that contains all tail information to re-hydrate a given tail.
     */
    private static final String NODE_RECENT_TAIL = "RecentTail";

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /** 
     * Menu of recently tailed files. A tail becomes 'recent' when it is closed.
     */
    private JMenu recentMenu_;

    /** 
     * File explorer flipper that allows the user to select a file to tail. 
     */
    private FileSelectionView fileSelectionPane_;

    /** 
     * Tab panel that contains each tail as a single tab. 
     */
    private JTailTabbedPane tabbedPane_;

    /** 
     * Flip pane that houses the file explorer. 
     */
    private JFlipPane flipPane_;

    /** 
     * Map of each tail that is active. 
     */
    private Map tailMap_;
    
    /** 
     * Reference to the workspace statusbar.
     */
    private IStatusBar statusBar_;    
    
    /**
     * Puts the application into test mode. An additional menu item is added
     * to the file menu which creates a running tail for testing purposes.
     */
    private boolean testMode_ = true;
    
    /** 
     * Data object that captures all known application settings/preferences.
     */ 
    private IJTailConfig jtailConfig_;

    /**
     * Panel on flippane for creating dynamic filters. 
     */
    private DynamicFilterView dynamicFilterView_;            
        
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a JTail.
     */
    public JTail()
    {
        init();        
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Sets the status bar.
     * 
     * @param statusBar Shared status bar.
     */
    public void setStatusBar(IStatusBar statusBar)
    {
        statusBar_ = statusBar;
    }

    //--------------------------------------------------------------------------
    // Public Static
    //--------------------------------------------------------------------------
    
    /**
     * Makes an easy to read label for the TailPane tab.
     * 
     * @param config TailPane configuration.
     * @return String
     */
    public static String makeTabLabel(ITailViewConfig config)
    {
        StringBuffer tabname = new StringBuffer();
        String[] filenames = config.getFilenames();
        tabname.append("<html><center>");
        
        // 
        // For an aggregate tail, have one file name per line
        //
        
        for (int i = 0; i < filenames.length; i++)
        {
            tabname.append(FilenameUtils.getName(filenames[i]));
            
            if (i + 1 < filenames.length)
                tabname.append("<br>");
        }
        
        tabname.append("</center></html>");
        
        return tabname.toString();
    }    

    
    /**
     * Makes an easy to read tooltip for the TailPane tab.
     * 
     * @param config TailPane configuration.
     * @return String
     */
    public static String makeTabToolTip(ITailViewConfig config)
    {
        StringBuffer tabname = new StringBuffer();
        String[] filenames = config.getFilenames();
        tabname.append("<html><center>");
        
        for (int i = 0; i < filenames.length; i++)
        {
            tabname.append(filenames[i]);
            
            if (i + 1 < filenames.length)
                tabname.append("<br>");
        }
        
        tabname.append("</center></html>");
        
        return tabname.toString();
    }    
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /** 
     * Initializes JTail by building the GUI and wiring the events.
     */
    protected void init()
    {
        try
        {
            tailMap_ = new HashMap();
            jtailConfig_ = new JTailConfig();
            buildView();
            wireView();
        }
        catch (Exception e)
        {
            ExceptionUtil.handleUI(e, logger_);
        }
    }

    
    /**
     * Constructs the user interface.
     */
    protected void buildView()
    {
        setLayout(new BorderLayout());
        fileSelectionPane_ = new FileSelectionView();
        flipPane_ = new JFlipPane(JFlipPane.LEFT);
        
        flipPane_.addFlipper(
            JFileExplorer.ICON, "File Explorer", fileSelectionPane_);
        
        dynamicFilterView_ = new DynamicFilterView(this);
        
        flipPane_.addFlipper(
            DynamicFilterView.ICON, 
            DynamicFilterView.LABEL, 
            dynamicFilterView_);
        
        tabbedPane_ = new JTailTabbedPane();
        
        add(BorderLayout.WEST, flipPane_);
        add(BorderLayout.CENTER, tabbedPane_);
        add(BorderLayout.NORTH, buildMenuBar());
    }
    
    
    /**
     * Builds the menu bar.
     * 
     * @return JMenuBar
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
     * Adds a tail of the given configuration to the output area.
     * 
     * @param config Tail configuration.
     * @throws IOException on I/O error.
     */     
    protected void addTail(ITailViewConfig config) throws IOException
    {
        TailPane tailPane = new TailPane(config, statusBar_);
            
        //
        // Create a map of [closeButton->tailPane] so that the tail pane can be 
        // reassociated if it needs to be removed from the tabbed pane.
        //
        
        JButton closeButton = tailPane.getCloseButton();
        tailMap_.put(closeButton, tailPane);
        closeButton.addActionListener(new CloseButtonListener());
        
        // Tab config
        tabbedPane_.addTab(makeTabLabel(config), tailPane);
        
        tabbedPane_.setToolTipTextAt(
            tabbedPane_.getTabCount() - 1, makeTabToolTip(config));
            
        tabbedPane_.setSelectedComponent(tailPane);
        
        statusBar_.setInfo("Added tail for " + 
            ArrayUtil.toString(config.getFilenames(), false));
            
        tailPane.addTailViewListener(tabbedPane_);
    }
    
    
    /**
     * Wires event listeners.
     */
    protected void wireView()
    {
        fileSelectionPane_.getFileExplorer().
            addFileExplorerListener(new FileSelectionListener());
            
        fileSelectionPane_.getTailButton().
            addActionListener(new TailButtonListener());
            
        fileSelectionPane_.getAggregateButton().
            addActionListener(new AggregateButtonListener());
    }
    
    
    /**
     * Returns the currently selected TailPane. 
     * 
     * @return TailPane
     */
    protected TailPane getSelectedTail()
    {
        return (TailPane) tabbedPane_.getSelectedComponent();
    }
    
    
    /**
     * Returns the configuration of currently selected tail in the tabbed pane.
     * 
     * @return ITailViewConfig.
     * @throws IOException on I/O error.  
     */
    protected ITailViewConfig getSelectedConfig() throws IOException
    {
        return getSelectedTail().getConfiguration();
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
                prefs, 
                NODE_JTAIL_PLUGIN, 
                new Element(NODE_JTAIL_PLUGIN));

        jtailConfig_.applyPrefs(root);
        
        //
        // Restore tails left running since last save.
        //
        
        ITailViewConfig[] tailPaneConfigs = jtailConfig_.getTailConfigs();
        
        for (int i = 0; i < tailPaneConfigs.length; i++)
        {
            ITailViewConfig config = tailPaneConfigs[i];
            
            // Apply defaults if any
            if (config.getFont() == null)
                config.setFont(jtailConfig_.getDefaultConfig().getFont());
            
            try
            {
                addTail(config);
            }
            catch (IOException e)
            {
                throw new PreferencedException(e);
            }
        }
    
        fileSelectionPane_.getFileExplorer().applyPrefs(root);
        dynamicFilterView_.applyPrefs(root);
        flipPane_.applyPrefs(root);

        Element recent = 
            XOMUtil.getFirstChildElement(
                root, 
                NODE_RECENT, 
                new Element(NODE_RECENT));
        
        Elements recentTails = recent.getChildElements(NODE_RECENT_TAIL);
            
        for (int i = 0, n = recentTails.size(); i < n; i++)
        {
            Element recentTail = recentTails.get(i);
            TailViewConfig config = new TailViewConfig();
            config.applyPrefs(recentTail);
            recentMenu_.add(new TailRecentAction(config));
        }
    }
    
    
    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws PreferencedException
    {
        Element root = new Element(NODE_JTAIL_PLUGIN);
        
        ITailViewConfig configs[] = new ITailViewConfig[0];
        
        for (Iterator i = tailMap_.entrySet().iterator(); i.hasNext();)
        {
            try
            {
                Map.Entry entry = (Map.Entry) i.next();
                TailPane tailPane = (TailPane) entry.getValue();
                ITailViewConfig config = tailPane.getConfiguration();
                configs = (ITailViewConfig[]) ArrayUtil.add(configs, config); 
            }
            catch (Exception e)
            {
                throw new PreferencedException(e);
            }    
        }
        
        jtailConfig_.setTailConfigs(configs);
        jtailConfig_.savePrefs(root);
        
        // Other preferenced components
        fileSelectionPane_.getFileExplorer().savePrefs(root);
        flipPane_.savePrefs(root);
        dynamicFilterView_.savePrefs(root);
        
        // Save recent menu
        Element recent = new Element(NODE_RECENT);
        Component[] items = recentMenu_.getMenuComponents();
       
        for (int i = 0; i < items.length; i++)
        {
            JMenuItem menuItem = (JMenuItem) items[i];
            Action action = menuItem.getAction();
            
            if (action instanceof TailRecentAction) 
            {
                ITailViewConfig config = 
                    (ITailViewConfig) action.getValue("config");

                Element recentTail = new Element(NODE_RECENT_TAIL);
                config.savePrefs(recentTail);
                recent.appendChild(recentTail);
            }
        }
        
        root.appendChild(recent);
        XOMUtil.insertOrReplace(prefs, root);
    }
    
    //--------------------------------------------------------------------------
    // FileSelectionListener
    //--------------------------------------------------------------------------
    
    /**
     * Adds a tail for a file double clicked by the user via the file explorer.
     */
    class FileSelectionListener extends FileExplorerAdapter
    {
        /**
         * @see toolbox.util.ui.explorer.FileExplorerListener#fileDoubleClicked(
         *      java.lang.String)
         */
        public void fileDoubleClicked(String file)
        {
            ITailViewConfig defaults = jtailConfig_.getDefaultConfig();
            ITailViewConfig config = new TailViewConfig();
            
            config.setFilenames(new String[] {file});
            config.setAutoTail(defaults.isAutoTail());
            config.setShowLineNumbers(defaults.isShowLineNumbers());
            config.setAntiAliased(defaults.isAntiAliased());
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
    
    //--------------------------------------------------------------------------
    // TailButtonListener
    //--------------------------------------------------------------------------
    
    /**
     * Adds a tail for the currently selected file in the file explorer.
     */
    class TailButtonListener extends SmartAction
    {
        /**
         * Creates a TailButtonListener.
         */
        TailButtonListener()
        {
            super("Tail", true, false, null);
        }

        
        /**
         * @see toolbox.util.ui.SmartAction#runAction(
         *      java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            ITailViewConfig defaults = jtailConfig_.getDefaultConfig();
            ITailViewConfig config = new TailViewConfig();
            
            config.setFilenames(new String[] 
            {
                fileSelectionPane_.getFileExplorer().getFilePath()
            });
                
            config.setAutoTail(defaults.isAutoTail());
            config.setShowLineNumbers(defaults.isShowLineNumbers());
            config.setAntiAliased(defaults.isAntiAliased());
            config.setFont(defaults.getFont());
            config.setRegularExpression(defaults.getRegularExpression());
            config.setCutExpression(defaults.getCutExpression());
            config.setAutoStart(defaults.isAutoStart());            
            addTail(config);
        }
    }

    //--------------------------------------------------------------------------
    // AggregateButtonListener
    //--------------------------------------------------------------------------
    
    /**
     * Aggregates a file to an existing tail.
     */
    class AggregateButtonListener extends SmartAction
    {
        /**
         * Creates a AggregateButtonListener.
         */
        AggregateButtonListener()
        {
            super("Aggregate", true, false, null);    
        }

        
        /**
         * @see toolbox.util.ui.SmartAction#runAction(
         *      java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            String file = fileSelectionPane_.getFileExplorer().getFilePath();
            TailPane tailPane = getSelectedTail();
            tailPane.aggregate(file);
        }
    }

    //--------------------------------------------------------------------------
    // CloseButtonListener
    //--------------------------------------------------------------------------
    
    /**
     * Removes a tail once the close button is clicked on the tail pane.
     */
    class CloseButtonListener extends SmartAction
    {
        /**
         * Creates a CloseButtonListener.
         */
        CloseButtonListener()
        {
            super("Close", true, false, null);
        }

        
        /**
         * The source is the closeButton on the tail pane. 
         * Get the tailPane from the tailMap using the button as
         * the key and then temove the tail pane from the tabbed pane
         * and them remove the tailpane from the tailmap itself.
         * 
         * @see toolbox.util.ui.SmartAction#runAction(
         *      java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            Object closeButton = e.getSource();
            TailPane pane = (TailPane) tailMap_.get(closeButton);
            pane.removeTailViewListener(tabbedPane_);
            tabbedPane_.remove(pane);        
            tailMap_.remove(closeButton);
            
            // Add the closed tail to the recent menu
            recentMenu_.add(new TailRecentAction(pane.getConfiguration()));
            
            statusBar_.setInfo("Closed " + 
                ArrayUtil.toString(pane.getConfiguration().getFilenames()));
        }
    }


    //--------------------------------------------------------------------------
    //  TailRecentAction
    //--------------------------------------------------------------------------
    
    /**
     * When a file is selected from the Recent menu, this action tails the 
     * file and removes that item from the recent menu.
     */
    class TailRecentAction extends SmartAction
    {
        /**
         * Tail configuration.
         */
        private ITailViewConfig config_;
        
        /**
         * Creates a TailRecentAction.
         * 
         * @param config Tail configuration.
         */
        TailRecentAction(ITailViewConfig config)
        {
            super(config.getFilenames()[0], true, false, null);
            config_ = config;
            putValue("config", config_);
        }
        
        
        /**
         * @see toolbox.util.ui.SmartAction#runAction(
         *      java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            addTail(config_);
            recentMenu_.remove((Component) e.getSource());
        }
    }

    //--------------------------------------------------------------------------
    // ClearRecentAction
    //--------------------------------------------------------------------------
    
    /**
     * Clears the Recent menu.
     */
    class ClearRecentAction extends AbstractAction
    {
        /**
         * Creates a ClearRecentAction.
         */
        ClearRecentAction()
        {
            super("Clear");
        }

        
        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            Component[] items = recentMenu_.getMenuComponents();
        
            for (int i = items.length - 1; i >= 0; i--)
            {
                JMenuItem menuItem = (JMenuItem) items[i];
                if (menuItem.getAction() instanceof TailRecentAction)
                    recentMenu_.remove(menuItem);
            }
        }
    }
    
    //--------------------------------------------------------------------------
    // CreateFileAction
    //--------------------------------------------------------------------------
    
    /**
     * Generates a file with intermittent output so that the file can be
     * tailed for testing purposes. The file is created is $user.home.
     */
    class CreateFileAction extends AbstractAction
    {
        /**
         * Creates a CreateFileAction.
         */
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

        
        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            String file = FileUtil.trailWithSeparator(
                System.getProperty("user.home")) + "jtail-test.txt";
                 
            FileStuffer stuffer = new FileStuffer(new File(file), 50);
            stuffer.start();
            statusBar_.setInfo("Created " + file + " for tailing");
        }
    }
    
    //--------------------------------------------------------------------------
    // SetFontAction
    //--------------------------------------------------------------------------
    
    /**
     * Pops up a font selection dialog to change the font.
     */
    class SetFontAction extends SmartAction 
        implements IFontChooserDialogListener
    {
        /**
         * Previous font.
         */
        private Font lastFont_;
        
        /**
         * Previous antialias flag.
         */
        private boolean lastAntiAlias_;
        
        /**
         * Creates a SetFontAction.
         */
        SetFontAction()
        {
            super("Set font ..", true, false, null);
            putValue(MNEMONIC_KEY, new Integer('t'));
            
            putValue(SHORT_DESCRIPTION, 
                "Sets the font of the tail output");
                
            putValue(ACCELERATOR_KEY, 
                KeyStroke.getKeyStroke(KeyEvent.VK_T, Event.CTRL_MASK));
        }
    
        
        /**
         * @see toolbox.util.ui.SmartAction#runAction(
         *      java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            // Remember state just in case user cancels operation
            lastFont_      = getSelectedConfig().getFont();
            lastAntiAlias_ = getSelectedConfig().isAntiAliased();
            
            // Show font selection dialog with font from the current
            // tail set as the default selected font
            JFontChooserDialog fsd = 
                new JFontChooserDialog(
                    SwingUtil.getFrameAncestor(JTail.this), 
                    false, 
                    lastFont_, 
                    lastAntiAlias_);
                    
            fsd.setTitle("Select font");
            fsd.addFontDialogListener(this);
            SwingUtil.centerWindow(fsd);
            fsd.setVisible(true);
        }
        
        //----------------------------------------------------------------------
        // Interface IFontChooserDialogListener
        //----------------------------------------------------------------------
        
        /**
         * @see toolbox.util.ui.font.IFontChooserDialogListener
         *      #applyButtonPressed(toolbox.util.ui.font.JFontChooser)
         */
        public void applyButtonPressed(JFontChooser fontChooser)
        {
            try
            {
                // Apply current settings
                ITailViewConfig config = getSelectedConfig();
                config.setFont(fontChooser.getSelectedFont());
                config.setAntiAliased(fontChooser.isAntiAliased());
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

        
        /**
         * @see toolbox.util.ui.font.IFontChooserDialogListener
         *      #cancelButtonPressed(toolbox.util.ui.font.JFontChooser)
         */
        public void cancelButtonPressed(JFontChooser fontChooser)
        {
            // Restore saved state
            ITailViewConfig config;
            
            try
            {
                config = getSelectedConfig();
                config.setFont(lastFont_);            
                config.setAntiAliased(lastAntiAlias_);
                getSelectedTail().setConfiguration(config);
            }
            catch (IOException e)
            {
                ExceptionUtil.handleUI(e, logger_);
            }
        }
        
        
        /**
         * @see toolbox.util.ui.font.IFontChooserDialogListener#okButtonPressed(
         *      toolbox.util.ui.font.JFontChooser)
         */
        public void okButtonPressed(JFontChooser fontChooser)
        {
            try
            {
                // Use new settings
                ITailViewConfig config = getSelectedConfig();
                config.setFont(fontChooser.getSelectedFont());
                config.setAntiAliased(fontChooser.isAntiAliased());
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
    
    //--------------------------------------------------------------------------
    // PreferencesAction
    //--------------------------------------------------------------------------
    
    /**
     * Pops up the preferences dialog.
     */
    class PreferencesAction extends AbstractAction 
    {
        /**
         * Creates a PreferencesAction.
         */
        PreferencesAction()
        {
            super("Preferences ..");
            putValue(MNEMONIC_KEY, new Integer('P'));
            
            putValue(SHORT_DESCRIPTION, 
                "View/change the default preferences");
                
            putValue(ACCELERATOR_KEY, 
                KeyStroke.getKeyStroke(KeyEvent.VK_P, Event.CTRL_MASK));
        }

        
        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            //
            // Show font selection dialog with font from the current
            // tail set as the default selected font
            //
            PreferencesDialog pd = 
                new PreferencesDialog(
                    SwingUtil.getFrameAncestor(JTail.this), 
                    jtailConfig_);
                
            SwingUtil.centerWindow(pd);
            pd.setVisible(true);
        }
    }
    
    //--------------------------------------------------------------------------
    // TailSystemOutAction
    //--------------------------------------------------------------------------
    
    /**
     * Adds a tail of the System.out stream.
     */
    class TailSystemOutAction extends SmartAction
    {
        /**
         * Creates a TailSystemOutAction.
         */
        TailSystemOutAction()
        {
            super("Tail System.out", true, false, null);
            putValue(MNEMONIC_KEY, new Integer('o'));
            
            putValue(ACCELERATOR_KEY, 
                KeyStroke.getKeyStroke(
                    KeyEvent.VK_O, 
                    Event.CTRL_MASK));
        }

        
        /**
         * @see toolbox.util.ui.SmartAction#runAction(
         *      java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            ITailViewConfig config = new TailViewConfig();
            config.setAntiAliased(false);
            config.setAutoTail(true);
            config.setAutoStart(true);
            config.setCutExpression("");
            config.setFilenames(new String[] {TailPane.LOG_SYSTEM_OUT});
            config.setFont(FontUtil.getPreferredMonoFont());
            config.setRegularExpression("");
            config.setShowLineNumbers(false);
            addTail(config);
        }
    }
    
    //--------------------------------------------------------------------------
    // TailLog4JAction
    //--------------------------------------------------------------------------
    
    /**
     * Adds a tail for Log4J attached to the "toolbox" logger.
     */
    class TailLog4JAction extends SmartAction
    {
        /**
         * Creates a TailLog4JAction.
         */
        TailLog4JAction()
        {
            super("Tail Log4J", true, false, null);
            putValue(MNEMONIC_KEY, new Integer('j'));

            putValue(ACCELERATOR_KEY, 
                KeyStroke.getKeyStroke(KeyEvent.VK_J, Event.CTRL_MASK));
        }

        
        /**
         * @see toolbox.util.ui.SmartAction#runAction(
         *      java.awt.event.ActionEvent)
         */
        public void runAction(ActionEvent e) throws Exception
        {
            ITailViewConfig config = new TailViewConfig();
            config.setAntiAliased(false);
            config.setAutoTail(true);
            config.setAutoStart(true);
            config.setCutExpression("");
            config.setFilenames(new String[] {TailPane.LOG_LOG4J});
            config.setFont(FontUtil.getPreferredMonoFont());
            config.setRegularExpression("");
            config.setShowLineNumbers(false);
            addTail(config);
        }
    }
}   