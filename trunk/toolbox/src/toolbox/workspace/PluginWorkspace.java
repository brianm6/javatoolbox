package toolbox.workspace;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.ParseException;
import nu.xom.Serializer;

import org.apache.commons.collections.SequencedHashMap;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import toolbox.log4j.SmartLogger;
import toolbox.util.ElapsedTime;
import toolbox.util.ExceptionUtil;
import toolbox.util.FileUtil;
import toolbox.util.StreamUtil;
import toolbox.util.SwingUtil;
import toolbox.util.XOMUtil;
import toolbox.util.io.StringOutputStream;
import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JSmartCheckBoxMenuItem;
import toolbox.util.ui.JSmartMenu;
import toolbox.util.ui.JSmartMenuItem;
import toolbox.util.ui.tabbedpane.JSmartTabbedPane;
import toolbox.util.ui.tabbedpane.SmartTabbedPaneListener;
import toolbox.workspace.lookandfeel.LookAndFeelException;
import toolbox.workspace.lookandfeel.LookAndFeelManager;

/**
 * Generic Frame that accepts pluggable GUI components that are displayed on
 * a tab panel. All pluggable GUI components must implements the IPlugin 
 * interface as a base set of functionality to be hosted by PluginWorkspace.
 */
public class PluginWorkspace extends JFrame implements IPreferenced
{
    // TODO: Make plugins detachable
    // TODO: Write log4j pattern layout that combines class name and method
    
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
        
    private static final Logger logger_ = 
        Logger.getLogger(PluginWorkspace.class);
    
    private static final String NODE_WORKSPACE      = "Workspace";
    private static final String   ATTR_MAXXED       = "maximized";
    private static final String   ATTR_WIDTH        = "width";
    private static final String   ATTR_HEIGHT       = "height";
    private static final String   ATTR_XCOORD       = "xcoord";
    private static final String   ATTR_YCOORD       = "ycoord";
    private static final String   ATTR_LAF          = "lookandfeel";
    private static final String   ATTR_LAF_THEME    = "lookandfeel.theme";
    private static final String   ATTR_SELECTED_TAB = "selectedtab";
    private static final String   ATTR_SMOOTH_FONTS = "smoothfonts";
    private static final String   ATTR_LOG_LEVEL    = "loglevel";
    
    private static final String   NODE_PLUGIN       = "Plugin";
    private static final String     ATTR_CLASS      = "class";
    private static final String     ATTR_LOADED     = "loaded";
    
    /**
     * This is a wrapper for NODE_WORKSPACE so we can treat it as an 
     * arbitrary element and not the document root.
     */
    private static final String NODE_ROOT = "Root";

    /**
     * Name of file that application and plugin preferences are stored in
     */
    private static final String FILE_PREFS = ".toolbox.xml";
    
    /**
     * Plugin property used to identify a reference to the workspace's shared
     * statusbar
     */    
    public static final String PROP_STATUSBAR = "workspace.statusbar";
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
     
    /** 
     * Plugins are added to this tab panel in order or registration 
     */
    private JSmartTabbedPane tabbedPane_;
    
    /** 
     * Status bar at bottom of screen 
     */
    private IStatusBar statusBar_;
    
    /** 
     * Look and Feel Menu Items 
     */
    private JMenu lookAndFeelMenu_;
    
    /** 
     * Preferences stored as XML 
     */
    private Element prefs_;
    
    /** 
     * Map of (pluginName:String, pluginInstance:IPlugin) 
     */
    private Map plugins_;
    
    /** 
     * Default initialization map for all plugins. Passed into IPlugin.startup() 
     */
    private Map bootstrapMap_;
    
    /**
     * Unloaded preferences
     */
    private Element unloadedPrefs_;
        
    /**
     * Smooth fonts check box
     */
    private JCheckBoxMenuItem smoothFontsCheckBoxItem_;
    
    /**
     * Delegate for all things look and feel related.
     */
    private LookAndFeelManager lafManager_;

    /**
     * Maps Log Level -> JCheckBoxMenuItem
     */
    private Map levelMap_;
    
    //--------------------------------------------------------------------------
    // Main 
    //--------------------------------------------------------------------------
    
    /**
     * Starts up the workspace 
     * 
     * @param args None recognized
     */
    public static void main(String args[])
    {
        JFrame.setDefaultLookAndFeelDecorated(false);    // to decorate frames
        JDialog.setDefaultLookAndFeelDecorated(false);   // to decorate dialogs

        SwingUtil.getLAFs();

        try
        {
            new PluginWorkspace();
        }
        catch(Exception e)
        {
            ExceptionUtil.handleUI(e, logger_);
        }
    }

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a PluginWorkspace
     * 
     * @throws Exception on error
     */
    public PluginWorkspace() throws Exception
    {
        super("Toolbox");
        init();
        loadPrefs();
        setLAF(prefs_);
        buildView();
        applyPrefs(prefs_);        
        setVisible(true);
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Registers a plugin with the GUI. Must be called prior buildView()
     * 
     * @param plugin Plugin to add to the GUI
     * @throws Exception on error
     */
    public void registerPlugin(IPlugin plugin) throws Exception
    {
        // Add to registry    
        plugins_.put(plugin.getPluginName(), plugin);

        // Init plugin
        plugin.startup(bootstrapMap_);

        // Create tab
        tabbedPane_.addTab(
            plugin.getPluginName(), 
            plugin.getComponent(), 
            ImageCache.getIcon(ImageCache.IMAGE_CROSS));
            
        // Restore unloaded preferences if they exist
        Element workspaceNode = 
            prefs_.getFirstChildElement(NODE_WORKSPACE);
        
        Elements pluginWrappers = 
            unloadedPrefs_.getChildElements(NODE_PLUGIN);
            
        Element pluginWrapper = null;
                
        for (int i=0; i<pluginWrappers.size(); i++)
        {
            pluginWrapper = pluginWrappers.get(i);
            
            if (pluginWrapper.getAttributeValue(ATTR_CLASS).equals(
                plugin.getClass().getName()))
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
     * Registers a plugin with the GUI. Must be called prior buildView()
     * 
     * @param plugin Plugin to add to the GUI
     * @param prefs Plugin preferences DOM
     * @throws Exception on error
     */
    public void registerPlugin(IPlugin plugin, Element prefs) throws Exception
    {
        // Add to registry    
        plugins_.put(plugin.getPluginName(), plugin);

        // Init plugin
        plugin.startup(bootstrapMap_);

        // Create tab
        tabbedPane_.addTab(
            plugin.getPluginName(), 
            plugin.getComponent(), 
            ImageCache.getIcon(ImageCache.IMAGE_CROSS));
        
        // Restore preferences but first see if there is a set of unloaded 
        // prefs for this plugin hanging around
        plugin.applyPrefs(prefs);
    }

    /**
     * Registers a plugin given its FQN
     * 
     * @param pluginClass Name of plugin class that implements the IPlugin 
     *        interface
     * @throws Exception on instantiation error
     */
    public void registerPlugin(String pluginClass) throws Exception
    {
        // Make sure this plugin hasn't already been loaded
        for (Iterator i = plugins_.values().iterator(); i.hasNext(); )
            if (i.next().getClass().getName().equals(pluginClass))
                return;                
        
        IPlugin plugin = (IPlugin) Class.forName(pluginClass).newInstance();
        registerPlugin(plugin);
    }

    /**
     * Registers a plugin given its FQN and preferences 
     * 
     * @param pluginClass Name of plugin class that implements the IPlugin 
     *        interface
     * @param prefs Plugin preferences DOM
     * @throws Exception on instantiation error
     */
    public void registerPlugin(String pluginClass, Element prefs) 
        throws Exception
    {
        // Make sure this plugin hasn't already been loaded
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
     * Deregisters a plugin given its fully qualified name
     * 
     * @param pluginClass Class name of plugin to remove
     * @throws Exception on error
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
            
            if (removeTab)
                tabbedPane_.remove(tabbedPane_.indexOfTab(plugin.getPluginName()));
                
            plugins_.remove(plugin.getPluginName());
            plugin.shutdown();
        }
        else
        {
            logger_.warn("Plugin " + pluginClass + " was not found.");
        }
    }

    //--------------------------------------------------------------------------
    // Package
    //--------------------------------------------------------------------------

    /**
     * Returns the workspace status bar
     * 
     * @return Status bar
     */
    IStatusBar getStatusBar()
    {
        return statusBar_;
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /**
     * Initializes the plugin workspace
     */
    protected void init()
    {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        plugins_ = new SequencedHashMap(); 
        lafManager_ = new LookAndFeelManager();
        levelMap_ = new HashMap();
    }
    
    /**
     * Builds the GUI
     */
    protected void buildView()
    {
        tabbedPane_ = new JSmartTabbedPane();
        tabbedPane_.addSmartTabbedPaneListener(new PluginTabbedPaneListener());

        statusBar_ = new WorkspaceStatusBar();
        statusBar_.setStatus("Howdy pardner!");
        
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(BorderLayout.CENTER, tabbedPane_);
        contentPane.add(BorderLayout.SOUTH, (Component) statusBar_);
        
        bootstrapMap_ = new HashMap(1);
        bootstrapMap_.put(PROP_STATUSBAR, statusBar_);
        
        setJMenuBar(createMenuBar());
        
        addWindowListener(new CloseWindowListener());
        
        setIconImage(ImageCache.getImage(ImageCache.IMAGE_TOOLBOX));
    }

    /**
     * Creates and configures the menu bar
     * 
     * @return JMenuBar
     */
    protected JMenuBar createMenuBar()
    {
        JMenuBar menubar = new JMenuBar();
        menubar.add(createFileMenu());
        menubar.add(lafManager_.createLookAndFeelMenu());
        menubar.add(createPreferencesMenu());
        menubar.add(createLoggingMenu());
        return menubar;
    }

    /**
     * Creates the File menu 
     * 
     * @return JMenu
     */
    protected JMenu createFileMenu()
    {
        JMenu fileMenu = new JSmartMenu("File");
        fileMenu.setMnemonic('F');        
        fileMenu.add(new JSmartMenuItem(new PluginsAction()));
        fileMenu.add(new JSmartMenuItem(new GarbageCollectAction()));
        fileMenu.add(new JSmartMenuItem(new ExitAction()));
        return fileMenu;            
    }


    /**
     * Creates the preferences menu
     * 
     * @return JMenu 
     */
    protected JMenu createPreferencesMenu()
    {
        JMenu menu = new JSmartMenu("Preferences");
        menu.setMnemonic('P');

        menu.add(new JSmartMenuItem(new SavePreferencesAction()));

        smoothFontsCheckBoxItem_ = 
            new JSmartCheckBoxMenuItem(new AntiAliasAction());

        menu.add(smoothFontsCheckBoxItem_);
        return menu;
    }

	/**
	 * Creates the Logging menu 
	 * 
	 * @return JMenu
	 */
	protected JMenu createLoggingMenu()
	{
		JMenu fileMenu = new JSmartMenu("Logging");
		fileMenu.setMnemonic('L');
		
		ButtonGroup group = new ButtonGroup();
		
		Level[] levels = new Level[]
		{
            Level.ALL, 
            Level.DEBUG, 
            Level.INFO, 
            Level.ERROR, 
            Level.FATAL, 
            Level.OFF
		};
		
		for (int i=0; i<levels.length; i++)
		{
		    JCheckBoxMenuItem cbmi =
		    	new JSmartCheckBoxMenuItem(new SetLogLevelAction(levels[i]));
		    
		    levelMap_.put(levels[i], cbmi);
			group.add(cbmi);
			fileMenu.add(cbmi);
		}
		
		return fileMenu;            
	}

    /**
     * Determines if a plugin is active given its FQN
     * 
     * @param pluginClass FQN of plugin class
     * @return True if plugin is registered, false otherwise
     */
    protected boolean hasPlugin(String pluginClass) 
    {
        for (Iterator i = plugins_.values().iterator(); i.hasNext();)
            if (i.next().getClass().getName().equals(pluginClass))
                return true;
        
        return false;
    }
    
    /**
     * Returns a plugin given its class name
     * 
     * @param pluginClass FQCN of the plugin
     * @return IPlugin
     */
    protected IPlugin getPluginByClass(String pluginClass)
    {
        for (Iterator i = plugins_.values().iterator(); i.hasNext(); )
        {
            IPlugin plugin = (IPlugin) i.next();
            if (plugin.getClass().getName().equals(pluginClass))
                return plugin;
        }        
        
        return null;
    }

    /**
     * Loads the workspace and plugin preferences from $user.home/.toolbox.xml
     */
    protected void loadPrefs()
    {
        prefs_ = new Element(NODE_ROOT);
        unloadedPrefs_ = new Element(NODE_ROOT);
        
        String userhome = System.getProperty("user.home");
        
        userhome = FileUtil.trailWithSeparator(userhome);
            
        File f = new File( userhome + FILE_PREFS);
        
        if (f.exists() && f.canRead() && f.isFile())
        {
            try
            {
                Document doc = new Builder().build(f);
                Node root = doc.getRootElement().copy();
                prefs_.appendChild(root);
                SmartLogger.debug(logger_, prefs_.toXML());
            }
            catch (ParseException pe)
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
            logger_.info("Preferences file " + FILE_PREFS + 
                " is either non-existant, unreadable, or not a file.");
        }
    }

    /**
     * Sets only the LookAndFeel based on the loaded preferences
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
            lafManager_.setLookAndFeel(root);
        }
        catch (LookAndFeelException lfe)
        {
            ExceptionUtil.handleUI(lfe, logger_);
        }
    }       

    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws Exception
    {
        Element root = new Element(NODE_WORKSPACE);
        
        boolean maxxed = 
            (getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH;
            
        root.addAttribute(new Attribute(ATTR_MAXXED, maxxed+""));
        
        if (!maxxed)
        {
            // Save window location
            root.addAttribute(new Attribute(ATTR_XCOORD, getLocation().x+""));
            root.addAttribute(new Attribute(ATTR_YCOORD, getLocation().y+""));
        
            // Save window size
            root.addAttribute(new Attribute(ATTR_WIDTH, getSize().width+""));
            root.addAttribute(new Attribute(ATTR_HEIGHT, getSize().height+"")); 
        }

        // Save currently selected tab
        root.addAttribute(
            new Attribute(ATTR_SELECTED_TAB,tabbedPane_.getSelectedIndex()+""));

        // Save smooth fonts flag
        root.addAttribute(
            new Attribute(
                ATTR_SMOOTH_FONTS, 
                smoothFontsCheckBoxItem_.isSelected()+""));
        
        // Save log level
        root.addAttribute(new Attribute(
            ATTR_LOG_LEVEL, Logger.getLogger("toolbox").getLevel().toString()));
        
        // Save look and feel
        lafManager_.savePrefs(root);
        
        // Save loaded plugin prefs
        for (Iterator i = plugins_.values().iterator(); i.hasNext();)
        {
            IPlugin plugin = (IPlugin) i.next();
            Element pluginNode = new Element(NODE_PLUGIN);
            
            pluginNode.addAttribute(
                new Attribute(ATTR_CLASS, plugin.getClass().getName()));
                
            pluginNode.addAttribute(new Attribute(ATTR_LOADED, "true"));
                
            plugin.savePrefs(pluginNode);
            root.appendChild(pluginNode);
        }


        // Save unloaded plugin prefs
        Elements unloaded = unloadedPrefs_.getChildElements(NODE_PLUGIN);
        
        logger_.debug(unloaded.size() + " unloaded plugins to save");
        
        for (int i=0; i<unloaded.size(); i++)
            root.appendChild(unloaded.get(i).copy());            

            
        // Save to file
        String userhome = System.getProperty("user.home");
        userhome = FileUtil.trailWithSeparator(userhome);
            
        FileWriter writer = null;
        String xml = null;
        
        try
        {
            writer = new FileWriter(userhome + FILE_PREFS);
            StringOutputStream sos = new StringOutputStream();
            Serializer serializer = new Serializer(sos);
            serializer.setIndent(2);
            serializer.setLineSeparator("\n");
            //serializer.setMaxLength(80);
            serializer.write(new Document(root));
            xml = sos.toString();
            writer.write(xml);
            
            //writer.write(new Serializer();
            
        }
        catch (IOException ioe)
        {
            ExceptionUtil.handleUI(ioe, logger_);
        }
        finally
        {
            StreamUtil.close(writer);
        }
        
        SmartLogger.debug(logger_, xml);
        statusBar_.setStatus("Saved preferences");
    }

    /**
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs)
    {
        Element root = prefs.getFirstChildElement(NODE_WORKSPACE);

        smoothFontsCheckBoxItem_.setSelected(
            XOMUtil.getBooleanAttribute(root, ATTR_SMOOTH_FONTS, false));
        
        new AntiAliasAction().actionPerformed(
            new ActionEvent(smoothFontsCheckBoxItem_, -1, null));
        
        boolean maxxed = XOMUtil.getBooleanAttribute(root, ATTR_MAXXED, false);
        
        // Frame has to be visible before it can be maximized so just queue
        // this bad boy up on the event queue 
        if (maxxed)
        {
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    setExtendedState(Frame.MAXIMIZED_BOTH);
                }
            });
            
        }
        
        // Set size/loc regardless of maximized state since this will be used
        // as the restored state
        
        // Restore window location
        setLocation(
            XOMUtil.getIntegerAttribute(root, ATTR_XCOORD, 0),
            XOMUtil.getIntegerAttribute(root, ATTR_YCOORD, 0));
        
        // Restore window size
        setSize(
            XOMUtil.getIntegerAttribute(root, ATTR_WIDTH, 800),
            XOMUtil.getIntegerAttribute(root, ATTR_HEIGHT, 600));

		Level level = 
			Level.toLevel(
		        XOMUtil.getStringAttribute(
		            root, ATTR_LOG_LEVEL, 
		                Logger.getLogger("toolbox").getLevel().toString()));

		new SetLogLevelAction(level).actionPerformed(
		    new ActionEvent(this, 1, ""));
		
		JCheckBoxMenuItem cbmi = (JCheckBoxMenuItem) levelMap_.get(level);
		cbmi.setSelected(true);
		
        lafManager_.selectOnMenu();
        
        if (root != null)
        {    
            // Iterate over the list of plugins. If the plugin has the 'loaded'
            // attribute then register() it otherwise add to the unloadedPrefs_
            // DOM for later use.
            
            Elements plugins = root.getChildElements(NODE_PLUGIN);
            
            for (int i=0; i<plugins.size(); i++) 
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

            // Restore last selected tab
            tabbedPane_.setSelectedIndex(
                XOMUtil.getIntegerAttribute(root, ATTR_SELECTED_TAB, -1));
        }
        else
        {
            logger_.warn(
                "Root preferences object is empty.We're starting from scratch");
        }
    }       

    //--------------------------------------------------------------------------
    // Package
    //--------------------------------------------------------------------------
    
    /**
     * Returns the plugins.
     * 
     * @return Map of plugins
     */
    Map getPlugins()
    {
        return plugins_;
    }
    
    //--------------------------------------------------------------------------
    // Listeners
    //--------------------------------------------------------------------------

    /**
     * Saves preferences when the application is closed
     */
    class CloseWindowListener extends WindowAdapter
    {
        public void windowClosing(WindowEvent e)
        {
            try
            {    
                savePrefs(prefs_);
            }
            catch (Throwable t)
            {
                ExceptionUtil.handleUI(t, logger_);
            }
        }
    }

    /**
     * Listens for tab closing events, and deregisters the plugin for that 
     * given tab.
     */
    class PluginTabbedPaneListener implements SmartTabbedPaneListener
    {
        /**
         * @see toolbox.util.ui.tabbedpane.SmartTabbedPaneListener#tabClosing(
         *      toolbox.util.ui.tabbedpane.JSmartTabbedPane, int)
         */
        public void tabClosing(JSmartTabbedPane tabbedPane, int tabIndex)
        {
            String name = tabbedPane.getTitleAt(tabIndex);
            IPlugin plugin = (IPlugin) plugins_.get(name);
            String clazz = plugin.getClass().getName(); 
            
            try
            {
                deregisterPlugin(clazz, false);
            }
            catch (Exception e)
            {
                ExceptionUtil.handleUI(e, logger_);
            }
        }
    }

    //--------------------------------------------------------------------------
    // ExitAction
    //--------------------------------------------------------------------------

    /**
     * Exits the appication
     */
    class ExitAction extends AbstractAction
    {
        ExitAction()
        {
            super("Exit");
            putValue(Action.MNEMONIC_KEY, new Integer('X'));
        }

        public void actionPerformed(ActionEvent ae)
        {
            new CloseWindowListener().windowClosing(
                new WindowEvent(PluginWorkspace.this, 0));
                
            setVisible(false);
            dispose();
            logger_.debug("Goodbye!");
            System.exit(0);
        }
    }

	//--------------------------------------------------------------------------
	// PluginsAction
	//--------------------------------------------------------------------------

    /**
     * Adds/removes plugins to/from the plugin frame
     */
    class PluginsAction extends AbstractAction
    {
        PluginsAction()
        {
            super("Plugins..");
            putValue(Action.MNEMONIC_KEY, new Integer('P'));
        }

        public void actionPerformed(ActionEvent ae)
        {
            JDialog dialog = new ManagePluginsDialog(PluginWorkspace.this);
            dialog.setVisible(true);    
        }
    }

	//--------------------------------------------------------------------------
	// SavePreferencesAction
	//--------------------------------------------------------------------------

    /**
     * Saves the preferences for the workspaces in addition to all the
     * active plugins.
     */
    class SavePreferencesAction extends WorkspaceAction
    {
        SavePreferencesAction()
        {
            super("Save prefs", false, null, null);
            putValue(Action.MNEMONIC_KEY, new Integer('S'));
            
            putValue(Action.SMALL_ICON, 
                ImageCache.getIcon(ImageCache.IMAGE_SAVE));
        }
        
        public void runAction(ActionEvent e) throws Exception
        {
            savePrefs(prefs_);
        }
    }

	//--------------------------------------------------------------------------
	// GarbageCollectAction
	//--------------------------------------------------------------------------

    /**
     * Triggers garbage collection
     */
    class GarbageCollectAction extends WorkspaceAction
    {
        GarbageCollectAction()
        {
            super("Run GC", false, null, null);
            putValue(Action.MNEMONIC_KEY, new Integer('G'));
            
        }
        
        public void runAction(ActionEvent e) throws Exception
        {
            long freeMem  = Runtime.getRuntime().freeMemory();
            long totalMem = Runtime.getRuntime().totalMemory();
            long maxMem   = Runtime.getRuntime().maxMemory();
            long beforeUsedMem  = (totalMem - freeMem)/1000;
            
            ElapsedTime time = new ElapsedTime();
            System.gc();
            time.setEndTime();
            
            freeMem  = Runtime.getRuntime().freeMemory();
            totalMem = Runtime.getRuntime().totalMemory();
            maxMem   = Runtime.getRuntime().maxMemory();
            long afterUsedMem  = (totalMem - freeMem)/1000;

            statusBar_.setStatus("" +
                "<html>" + "<font color='black'>" +
                  "Finished GC in " + time + ".   " +
                  "Used Before: " + beforeUsedMem + "K   " +
                  "After: "       + afterUsedMem  + "K   " +
                  "Freed:<b>"     + (beforeUsedMem - afterUsedMem) + "K</b>   "+ 
                  "Total:     "   + totalMem/1000 + "K   " +
                  "Max: "         + maxMem/1000   + "K   " +
                  "</font>" +
                "</html>");
        }
    }

	//--------------------------------------------------------------------------
	// AntiAliasAction
	//--------------------------------------------------------------------------

    /**
     * Toggles smooth fonts  
     */
    class AntiAliasAction extends AbstractAction
    {
        AntiAliasAction()
        {
            super("Smooth Fonts");
        }

        public void actionPerformed(ActionEvent e)
        {
            JCheckBoxMenuItem cb = (JCheckBoxMenuItem) e.getSource();
            boolean b = cb.isSelected();
            SwingUtil.setDefaultAntiAlias(b);
            Component[] comps = PluginWorkspace.this.getComponents();
            for (int i=0; i<comps.length; 
                SwingUtil.setAntiAliased(comps[i++], b));
            SwingUtil.setAntiAliased(PluginWorkspace.this.getJMenuBar(), b);
            PluginWorkspace.this.repaint();
        }
    }

	//--------------------------------------------------------------------------
	// SetLogLevelAction
	//--------------------------------------------------------------------------

	/**
	 * Action to set the logging level
	 */
	class SetLogLevelAction extends AbstractAction
	{
		private Level level_;
	    
		/**
		 * Creates a SetLogLevelAction
		 * 
		 * @param level Logging level to activate
		 */
		SetLogLevelAction(Level level)
		{
		    super(level.toString());
			level_ = level;
		}
	    
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(
		 *      java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0)
		{
		    Logger logger = Logger.getLogger("toolbox");
		    logger.setLevel(level_);
		}
	}
}