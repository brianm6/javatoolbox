package toolbox.workspace;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.UIManager;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.ParsingException;
import nu.xom.Serializer;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import toolbox.log4j.LoggingMenu;
import toolbox.log4j.SmartLogger;
import toolbox.util.ExceptionUtil;
import toolbox.util.FileUtil;
import toolbox.util.XOMUtil;
import toolbox.util.io.StringOutputStream;
import toolbox.util.service.ServiceException;
import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JSmartCheckBoxMenuItem;
import toolbox.util.ui.JSmartFrame;
import toolbox.util.ui.JSmartMenu;
import toolbox.util.ui.JSmartMenuItem;
import toolbox.util.ui.plaf.LookAndFeelUtil;
import toolbox.workspace.action.AboutAction;
import toolbox.workspace.action.ExitAction;
import toolbox.workspace.action.GarbageCollectAction;
import toolbox.workspace.action.PluginsAction;
import toolbox.workspace.action.SavePreferencesAction;
import toolbox.workspace.action.SmoothFontsAction;
import toolbox.workspace.host.PluginHost;
import toolbox.workspace.host.PluginHostManager;
import toolbox.workspace.prefs.PreferencesDialog;
import toolbox.workspace.prefs.PreferencesManager;

/**
 * Generic Frame that accepts pluggable GUI components that are displayed on a
 * tab panel. All pluggable GUI components must implements the IPlugin
 * interface as a base set of functionality to be hosted by PluginWorkspace.
 */
public class PluginWorkspace extends JSmartFrame implements IPreferenced
{
    private static final Logger logger_ = 
        Logger.getLogger(PluginWorkspace.class);

    //--------------------------------------------------------------------------
    // IPreferenced Constants
    //--------------------------------------------------------------------------

    // Workspace preferences nodes and attributes.
    private static final String NODE_WORKSPACE      = "Workspace";
    private static final String   ATTR_SMOOTH_FONTS =   "smoothfonts";
    private static final String   ATTR_LOG_LEVEL    =   "loglevel";
    private static final String   ATTR_DECORATIONS  =   "decorations";
    private static final String   ATTR_PLUGINHOST   =   "pluginhost";


    // Plugin preferences nodes and attributes.
    private static final String   NODE_PLUGIN       = "Plugin";
    private static final String     ATTR_CLASS      =   "class";
    private static final String     ATTR_LOADED     =   "loaded";

    /**
     * This is a wrapper for NODE_WORKSPACE so we can treat it as an arbitrary
     * element and not the document root.
     */
    private static final String NODE_ROOT = "Root";

    //--------------------------------------------------------------------------
    // Default Constants
    //--------------------------------------------------------------------------

    /**
     * Default name and location of the file that application and plugin 
     * preferences are saved to which is .toolbox.xml in the users home
     * directory.
     */
    private static final String DEFAULT_PREFS_FILE = 
        FileUtil.trailWithSeparator(
            System.getProperty("user.home")) + ".toolbox.xml";

    //--------------------------------------------------------------------------
    // Key Constants
    //--------------------------------------------------------------------------
    
    /**
     * Key that references the workspace's statusbar. This is passed to plugins 
     * via IPlugin.initialize(Map) on startup.
     */
    public static final String KEY_STATUSBAR = "workspace.statusbar";

    /**
     * Key that refererences the workspace itself. This is passed to plugins
     * via IPlugin.initialize(Map) on startup.
     */
    public static final String KEY_WORKSPACE = "workspace.self";

    //--------------------------------------------------------------------------
    // UI Component Name Constants
    //--------------------------------------------------------------------------
    
    // Used by the Jemmy Unit Tests
    public static final String LABEL_PREFERENCES_MENU = "Preferences";
    public static final String LABEL_FILE_MENU        = "File";
    public static final String LABEL_EXIT_MENUITEM    = "Exit";
    public static final String TITLE_TOOLBOX          = "Toolbox";

    //--------------------------------------------------------------------------
    // UI Fields
    //--------------------------------------------------------------------------

    /**
     * Status bar at the bottom of the window. Houses the progress bar, memory
     * monitor, and garbage collection invoker.
     */
    private IStatusBar statusBar_;

    /**
     * Log4J specific logging menu.
     */
    private LoggingMenu logMenu_;

    /**
     * Smooth fonts check box.
     */
    private JCheckBoxMenuItem smoothFontsCheckBoxItem_;

    /**
     * Use Look and Feel frame and dialog border decorations check box.
     */
    private JCheckBoxMenuItem decorationsCheckBoxItem_;

    //--------------------------------------------------------------------------
    // Preferences Fields
    //--------------------------------------------------------------------------

    /**
     * File to which workspace and plugin preferences are persisted. Any
     * component which implements the IPreferenced interface is persisted here.
     */
    private String prefsFile_;
    
    /**
     * Global set of preferences in the structure of a live XML DOM tree.
     */
    private Element prefs_;

    /**
     * Once a plugin is unloaded, its prefs would be thrown away unless we save
     * them here to reuse again the next time the plugin is activated.
     */
    private Element unloadedPrefs_;

    /**
     * Manages workspace preferences.
     */
    private PreferencesManager preferencesManager_;

    //--------------------------------------------------------------------------
    // Plugin Fields
    //--------------------------------------------------------------------------

    /**
     * Manages the various plugin host implementations (tabbed and desktop at
     * the moment).
     */
    private PluginHostManager pluginHostManager_;

    /**
     * Initialization map for plugins passed via IPlugin.startup(Map). See
     * Key Constants.
     */
    private Map initMap_;

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------

    /**
     * Starts up the workspace.
     *
     * @param args None recognized.
     */
    public static void main(String args[])
    {
        // Workaround for annoying WebStart dlg box asking for disk in drive a:
        System.setSecurityManager(null);
        
        // Decoration flag has to be set before the first JFrame is instantiated
        String prefsFile = (args.length == 0) ? DEFAULT_PREFS_FILE : args[0];
        boolean decorate = shouldDecorate(prefsFile);
        JFrame.setDefaultLookAndFeelDecorated(decorate);
        JDialog.setDefaultLookAndFeelDecorated(decorate);
        Toolkit.getDefaultToolkit().setDynamicLayout(true);
        
        try
        {
            new PluginWorkspace(prefsFile);
        }
        catch (Exception e)
        {
            ExceptionUtil.handleUI(e, logger_);
        }
    }

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a PluginWorkspace with the default preferences file.
     *
     * @see #DEFAULT_PREFS_FILE
     * @throws Exception on error.
     */
    public PluginWorkspace() throws Exception
    {
        this(DEFAULT_PREFS_FILE);
    }

    
    /**
     * Creates a PluginWorkspace with the given file used for preferences.
     *
     * @param prefsFile Preferences file.
     * @throws Exception on error.
     */
    public PluginWorkspace(String prefsFile) throws Exception
    {
        super(TITLE_TOOLBOX);
        setPrefsFile(prefsFile);
        initialize(MapUtils.EMPTY_MAP);
        loadPrefs();
        buildView();
        setLAF(prefs_);
        applyPrefs(prefs_);
        setVisible(true);
    }
    
    //--------------------------------------------------------------------------
    // Plugin Management
    //--------------------------------------------------------------------------

    /**
     * Returns the currently installed plugin host.
     *
     * @return PluginHost
     */
    public PluginHost getPluginHost()
    {
        return pluginHostManager_.getPluginHost();
    }

    
    /**
     * Registers a plugin with the PluginHostManager. Must be called prior to
     * buildView().
     *
     * @param plugin Plugin to register.
     * @throws Exception on error.
     */
    public void registerPlugin(IPlugin plugin) throws Exception
    {
        getPluginHost().addPlugin(plugin);

        //
        // Restore unloaded preferences if they exist
        //

        Element workspaceNode = prefs_.getFirstChildElement(NODE_WORKSPACE);
        Elements pluginWrappers = unloadedPrefs_.getChildElements(NODE_PLUGIN);
        Element pluginWrapper = null;

        for (int i = 0, n = pluginWrappers.size(); i < n; i++)
        {
            pluginWrapper = pluginWrappers.get(i);

            if (pluginWrapper
                    .getAttributeValue(ATTR_CLASS)
                    .equals(plugin.getClass().getName()))
            {
                pluginWrapper.detach();
                workspaceNode.appendChild(pluginWrapper);
                break;
            }
        }

        if (pluginWrapper != null)
            pluginWrapper.addAttribute(new Attribute(ATTR_LOADED, "true"));

        plugin.applyPrefs(pluginWrapper);
    }


    /**
     * Registers a plugin with the PluginHostManager along with an existing set
     * of preferences. Must be called prior to buildView().
     *
     * @param plugin Plugin to register.
     * @param prefs Plugin preferences DOM.
     * @throws Exception on error.
     */
    public void registerPlugin(IPlugin plugin, Element prefs) throws Exception
    {
        getPluginHost().addPlugin(plugin);

        //
        // Restore preferences but first see if there is a set of unloaded
        // prefs for this plugin hanging around
        //

        plugin.applyPrefs(prefs);
    }


    /**
     * Registers a plugin given its FQCN.
     *
     * @param pluginClass Name of class that implements the IPlugin interface.
     * @throws Exception on instantiation error.
     */
    public void registerPlugin(String pluginClass) throws Exception
    {
        if (!pluginHostManager_.getPluginHost().hasPlugin(pluginClass))
        {
            IPlugin plugin = (IPlugin) Class.forName(pluginClass).newInstance();
            registerPlugin(plugin);
        }
        else
        {
            logger_.warn("Plugin " + pluginClass + " already loaded.");
        }
    }


    /**
     * Registers a plugin given its FQCN and preferences.
     *
     * @param pluginClass Name of class that implements the IPlugin interface.
     * @param prefs Plugin preferences DOM.
     * @throws Exception on instantiation error.
     */
    public void registerPlugin(String pluginClass, Element prefs)
        throws Exception
    {
        //
        // Make sure this plugin hasn't already been loaded
        //

        if (!hasPlugin(pluginClass))
        {
            IPlugin plugin = (IPlugin) Class.forName(pluginClass).newInstance();
            registerPlugin(plugin, prefs);
        }
        else
        {
            logger_.warn("Plugin " + pluginClass + "has already been loaded.");
        }
    }


    /**
     * Deregisters a plugin given its FQCN.
     *
     * @param pluginClass Class name of plugin to remove.
     * @param removeTab Set to true to also remove the tab the plugin was in.
     * @throws Exception on error.
     */
    public void deregisterPlugin(String pluginClass, boolean removeTab)
        throws Exception
    {
        if (hasPlugin(pluginClass))
        {
            IPlugin plugin = getPluginByClass(pluginClass);
            Element pluginNode = new Element(NODE_PLUGIN);
            pluginNode.addAttribute(new Attribute(ATTR_CLASS, pluginClass));
            pluginNode.addAttribute(new Attribute(ATTR_LOADED, "false"));
            plugin.savePrefs(pluginNode);
            unloadedPrefs_.appendChild(pluginNode);
            getPluginHost().removePlugin(plugin);
        }
        else
        {
            logger_.warn("Plugin " + pluginClass + " was not found.");
        }
    }

    
    /**
     * Returns the root of the preferences DOM. Access to the preferences exists
     * primarily so that the PluginHosts can read/write their prefs into the
     * DOM tree.
     *
     * @return Element
     */
    public Element getPreferences()
    {
        return prefs_.getFirstChildElement(NODE_WORKSPACE);
    }

    
    /**
     * Returns the shared workspace status bar.
     *
     * @return Status bar.
     */
    public IStatusBar getStatusBar()
    {
        return statusBar_;
    }
    
    //--------------------------------------------------------------------------
    // Package
    //--------------------------------------------------------------------------

    /**
     * Returns the preferences manager.
     *
     * @return PreferencesManager
     */
    PreferencesManager getPreferencesManager()
    {
        return preferencesManager_;
    }

    //--------------------------------------------------------------------------
    // Static Protected
    //--------------------------------------------------------------------------
    
    /**
     * Loads the preferences specifically to read the look and feel decorations
     * flag. Returns true if look and feel decorations should be used, false
     * otherwise.
     * 
     * @param prefsFile Preferences file.
     * @return boolean
     */
    protected static boolean shouldDecorate(String prefsFile)
    {
        boolean result = false;
        File file = new File(prefsFile);

        try
        {
            result = XOMUtil.getBooleanAttribute(
                new Builder().build(file).getRootElement(),
                ATTR_DECORATIONS,
                false);
        }
        catch (Exception e)
        {
            logger_.warn(e);
        }

        logger_.debug("shouldDecorate = " + result);
        return result;
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /**
     * Initializes the plugin workspace.
     */
    public void initialize(Map config) throws IllegalStateException,
        ServiceException
    {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pluginHostManager_ = new PluginHostManager(this);
        preferencesManager_ = new PreferencesManager();

        //
        // Have to build log menu here cuz buildView() requires it to set
        // the initial value of the console logger checkbox state.
        //
        logMenu_ = new LoggingMenu();
    }

    
    /**
     * Constructs the user interface.
     *
     * @throws PluginException on plugin error.
     */
    protected void buildView() throws PluginException
    {
        statusBar_ = new WorkspaceStatusBar();
        statusBar_.setInfo("Howdy pardner!");

        initMap_ = new HashMap(2);
        initMap_.put(KEY_STATUSBAR, statusBar_);
        initMap_.put(KEY_WORKSPACE, this);

        //
        // The plugin host needs to be set before calling applyPrefs() because
        // of dependencies
        //
        pluginHostManager_.setPluginHost(
            XOMUtil.getStringAttribute(
                XOMUtil.getFirstChildElement(
                    prefs_,
                    NODE_WORKSPACE,
                    new Element(NODE_WORKSPACE)),
                ATTR_PLUGINHOST,
                PluginHostManager.PLUGIN_HOST_TABBED),
            initMap_);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        contentPane.add(
            pluginHostManager_.getPluginRecepticle(),
            BorderLayout.CENTER);

        contentPane.add(
            (Component) statusBar_,
            BorderLayout.SOUTH);

        setJMenuBar(createMenuBar());
        addWindowListener(new CloseWindowListener());
        setIconImage(ImageCache.getImage(ImageCache.IMAGE_TOOLBOX));
    }


    /**
     * Creates and configures the menu bar.
     *
     * @return JMenuBar
     */
    protected JMenuBar createMenuBar()
    {
        JMenuBar menubar = new JMenuBar();
        menubar.add(createFileMenu());
        menubar.add(LookAndFeelUtil.createLookAndFeelMenu());
        menubar.add(createPreferencesMenu());
        menubar.add(new PluginMenu(this));
        menubar.add(logMenu_);
        menubar.add(createHelpMenu());
        return menubar;
    }


    /**
     * Creates the File menu.
     *
     * @return JMenu
     */
    protected JMenu createFileMenu()
    {
        JMenu fileMenu = new JSmartMenu(LABEL_FILE_MENU);
        fileMenu.setMnemonic('F');
        fileMenu.add(new JSmartMenuItem(new PluginsAction(this)));
        fileMenu.add(new JSmartMenuItem(new GarbageCollectAction(this)));
        fileMenu.add(new JSmartMenuItem(new ExitAction(this)));
        return fileMenu;
    }

    
    /**
     * Creates the Help menu.
     *
     * @return JMenu
     */
    protected JMenu createHelpMenu()
    {
        JMenu helpMenu = new JSmartMenu("Help");
        helpMenu.setMnemonic('H');
        helpMenu.add(new JSmartMenuItem(new AboutAction(this)));
        return helpMenu;
    }

    
    /**
     * Creates the preferences menu.
     *
     * @return JMenu
     */
    protected JMenu createPreferencesMenu()
    {
        JMenu menu = new JSmartMenu(LABEL_PREFERENCES_MENU);
        menu.setMnemonic('P');
        menu.add(new JSmartMenuItem(new SavePreferencesAction(this)));

        smoothFontsCheckBoxItem_ = 
            new JSmartCheckBoxMenuItem(
                new SmoothFontsAction(this));

        decorationsCheckBoxItem_ =
            new JSmartCheckBoxMenuItem(
                new UseDecorationsAction());

        menu.add(smoothFontsCheckBoxItem_);
        menu.add(decorationsCheckBoxItem_);
        menu.add(pluginHostManager_.createMenu());

        menu.add(new JSmartMenuItem(new AbstractAction(LABEL_PREFERENCES_MENU)
        {
            public void actionPerformed(ActionEvent e)
            {
                new PreferencesDialog(
                    PluginWorkspace.this,
                    preferencesManager_).setVisible(true);
            }
        }));

        return menu;
    }


    /**
     * Determines if a plugin is active given its FQN.
     *
     * @param pluginClass FQN of plugin class.
     * @return True if plugin is registered, false otherwise.
     */
    protected boolean hasPlugin(String pluginClass)
    {
        return getPluginHost().hasPlugin(pluginClass);
    }


    /**
     * Returns a plugin given its class name.
     *
     * @param pluginClass FQCN of the plugin.
     * @return IPlugin
     */
    protected IPlugin getPluginByClass(String pluginClass)
    {
        return getPluginHost().getPlugin(pluginClass);
    }


    /**
     * Loads the workspace and plugin preferences from the preferences file 
     * into a DOM tree accessible via XOM.
     */
    protected void loadPrefs()
    {
        prefs_ = new Element(NODE_ROOT);
        unloadedPrefs_ = new Element(NODE_ROOT);
        File f = new File(getPrefsFile());

        if (f.exists() && f.canRead() && f.isFile())
        {
            try
            {
                Document doc = new Builder().build(f);
                Node root = doc.getRootElement().copy();
                prefs_.appendChild(root);
                SmartLogger.debug(logger_, prefs_.toXML());
            }
            catch (ParsingException pe)
            {
                ExceptionUtil.handleUI(pe, logger_);
            }
            catch (IOException ioe)
            {
                ExceptionUtil.handleUI(ioe, logger_);
            }
        }
        else
        {
            logger_.warn(
                "Preferences file " 
                + getPrefsFile() 
                + " is either non-existant, unreadable, or not a file.");
        }
    }


    /**
     * Sets only the LookAndFeel based on the loaded preferences. The buck stops
     * here if the look and feel should fail to load.
     *
     * @param prefs DOM representing the saved preferences.
     */
    protected void setLAF(Element prefs)
    {
        Element root =
            XOMUtil.getFirstChildElement(
                prefs, NODE_WORKSPACE, new Element(NODE_WORKSPACE));

        try
        {
            LookAndFeelUtil.setLookAndFeel(root);
        }
        catch (Exception e)
        {
            // Don't let look and feel failure stop initialization
            ExceptionUtil.handleUI(e, logger_);
        }
    }

    
    /**
     * Returns the absolute name of the preferences file.
     * 
     * @return String
     */
    public String getPrefsFile()
    {
        return prefsFile_;
    }


    /**
     * Sets the absolute name of the preferences file.
     * 
     * @param prefsFile Preferences file. 
     */
    public void setPrefsFile(String prefsFile)
    {
        prefsFile_ = prefsFile;
    }
    
    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------

    /**
     * Save preferences of all contained instances of IPreferenced.
     * 
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws Exception
    {
        Element root = new Element(NODE_WORKSPACE);

        // Let superclass handle saving frame size and location
        super.savePrefs(root);
        
        //
        // Save currently selected tab
        //

        // Fix me!!!
        //root.addAttribute(
        //    new Attribute(ATTR_SELECTED_TAB,
        //                  tabbedPane_.getSelectedIndex()+""));

        //
        // Save the pluginhost class
        //
        root.addAttribute(
            new Attribute(
                ATTR_PLUGINHOST,
                getPluginHost().getClass().getName()));

        //
        // Save smooth fonts flag
        //
        root.addAttribute(
            new Attribute(
                ATTR_SMOOTH_FONTS,
                smoothFontsCheckBoxItem_.isSelected() + ""));

        //
        // Save user decorations flag
        //
        root.addAttribute(
            new Attribute(
                ATTR_DECORATIONS,
                decorationsCheckBoxItem_.isSelected() + ""));

        //
        // Save log level
        //
        root.addAttribute(new Attribute(
            ATTR_LOG_LEVEL, Logger.getRootLogger().getLevel().toString()));

        //
        // Save look and feel
        //
        LookAndFeelUtil.savePrefs(root);

        //
        // Save prefs managed by the PreferencesManager
        //
        preferencesManager_.savePrefs(root);

        //
        // Save loaded plugin prefs
        //
        for (int i = 0; i < getPluginHost().getPlugins().length; i++)
        {
            IPlugin plugin = getPluginHost().getPlugins()[i];
            Element pluginNode = new Element(NODE_PLUGIN);

            pluginNode.addAttribute(
                new Attribute(ATTR_CLASS, plugin.getClass().getName()));

            pluginNode.addAttribute(new Attribute(ATTR_LOADED, "true"));

            plugin.savePrefs(pluginNode);
            root.appendChild(pluginNode);
        }

        //
        // Save unloaded plugin prefs
        //
        Elements unloaded = unloadedPrefs_.getChildElements(NODE_PLUGIN);

        logger_.debug(unloaded.size() + " unloaded plugins to save");

        for (int i = 0; i < unloaded.size(); i++)
            root.appendChild(unloaded.get(i).copy());


        getPluginHost().savePrefs(root);

        //
        // Save prefs to the designated preferences file.
        //
        FileWriter writer = null;

        try
        {
            writer = new FileWriter(getPrefsFile());
            StringOutputStream sos = new StringOutputStream();
            Serializer serializer = new Serializer(sos);
            serializer.setIndent(2);
            serializer.setLineSeparator("\n");
            serializer.write(new Document(root));
            String xml = sos.toString();
            SmartLogger.debug(logger_, xml);
            writer.write(xml);
            statusBar_.setInfo("Saved preferences to " + getPrefsFile());
        }
        catch (IOException ioe)
        {
            statusBar_.setError(
                "Failed to save preferences to " 
                + getPrefsFile());
            
            ExceptionUtil.handleUI(ioe, logger_);
        }
        finally
        {
            IOUtils.closeQuietly(writer);
        }
    }


    /**
     * Apply preferences of all contained instances of IPreferenced.
     * 
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws Exception
    {
        Element root = prefs.getFirstChildElement(NODE_WORKSPACE);

        //
        // Restore smooth fonts
        //
        smoothFontsCheckBoxItem_.setSelected(
            XOMUtil.getBooleanAttribute(root, ATTR_SMOOTH_FONTS, false));

        new SmoothFontsAction(this).actionPerformed(
            new ActionEvent(smoothFontsCheckBoxItem_, -1, null));

        //
        // Restore use window decorations
        //
        decorationsCheckBoxItem_.setSelected(
            XOMUtil.getBooleanAttribute(root, ATTR_DECORATIONS, false));

        new UseDecorationsAction().actionPerformed(
            new ActionEvent(decorationsCheckBoxItem_, -1, null));

        // Let superclass impl handle frame size, location, maximized
        super.applyPrefs(root);
        
        //
        // Restore log level even if it conflicts with log4j.xml
        //
        Level level =
            Level.toLevel(
                XOMUtil.getStringAttribute(
                    root, ATTR_LOG_LEVEL,
                        Logger.getRootLogger().getLevel().toString()));

        logMenu_.setLogLevel(level);

        //
        // Restore prefs stored by the Preferences dialog
        //
        preferencesManager_.applyPrefs(root);

        if (root != null)
        {
            //
            // Iterate over the list of plugins. If the plugin has the 'loaded'
            // attribute then register() it otherwise add to the unloadedPrefs_
            // DOM for later use.
            //
            Elements plugins = root.getChildElements(NODE_PLUGIN);

            for (int i = 0; i < plugins.size(); i++)
            {
                Element pluginNode = plugins.get(i);

                if (XOMUtil.getBooleanAttribute(pluginNode, ATTR_LOADED, false))
                {
                    try
                    {
                        String pluginClass =
                            XOMUtil.getStringAttribute(
                                pluginNode, ATTR_CLASS, "");

                        registerPlugin(pluginClass, pluginNode);
                    }
                    catch (Throwable t)
                    {
                        ExceptionUtil.handleUI(t, logger_);
                    }
                }
                else
                {
                    unloadedPrefs_.appendChild(pluginNode.copy());
                }
            }

            //
            // Restore last selected tab
            //

            //
            // TODO: Move to applyPrefs of TabbedPluginHost
            //
            //tabbedPane_.setSelectedIndex(
            //    XOMUtil.getIntegerAttribute(root, ATTR_SELECTED_TAB, -1));

            //
            // When the plugin host is set for the first time, applying its
            // preferences has to happen at the tail end of the initialization
            // process.
            //
            try
            {
                getPluginHost().applyPrefs(root);
            }
            catch (Exception e)
            {
                ExceptionUtil.handleUI(e, logger_);
            }
        }
        else
        {
            logger_.warn(
                "Root preferences object is empty.We're starting from scratch");
        }
    }

    //--------------------------------------------------------------------------
    // CloseWindowListener
    //--------------------------------------------------------------------------

    /**
     * Saves preferences when the application is closed.
     */
    class CloseWindowListener extends WindowAdapter
    {
        /**
         * @see java.awt.event.WindowListener#windowClosing(
         *      java.awt.event.WindowEvent)
         */
        public void windowClosing(WindowEvent e)
        {
            try
            {
                (new SavePreferencesAction(PluginWorkspace.this)).runAction();
            }
            catch (Throwable t)
            {
                ExceptionUtil.handleUI(t, PluginWorkspace.logger_);
            }
        }
    }
    
    //--------------------------------------------------------------------------
    // UseDecorationsAction
    //--------------------------------------------------------------------------
    
    /**
     * Toggles using the Look and Feel frame and dialog border decorations.
     */
    class UseDecorationsAction extends AbstractAction
    {
        /**
         * Creates a UseDecorationsAction.
         */
        UseDecorationsAction()
        {
            super("Use Decorations");
        }


        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            if (UIManager.getLookAndFeel().getSupportsWindowDecorations())
            {
                boolean useDecorations = decorationsCheckBoxItem_.isSelected();

                if (!isDisplayable())
                {
                    logger_.debug(
                        "Frame is not displayed yet! Setting decorated to " +
                        useDecorations);

                    //JFrame.setDefaultLookAndFeelDecorated(!useDecorations);
                    setUndecorated(useDecorations);
                }
                else
                {
                    logger_.debug("Frame is displayable..doing nuttin!");
                }

                //JDialog.setDefaultLookAndFeelDecorated(useDecorations);
            }
            else
            {
                statusBar_.setWarning("The current look and feel does not " +
                    "support window decorations.");
            }
        }
    }
}