package toolbox.util.ui.plugin;

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
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.jgoodies.plaf.plastic.PlasticLookAndFeel;
import com.jgoodies.plaf.plastic.PlasticTheme;

import org.apache.commons.collections.SequencedHashMap;
import org.apache.log4j.Logger;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.ParseException;
import nu.xom.Serializer;

import toolbox.log4j.SmartLogger;
import toolbox.util.ElapsedTime;
import toolbox.util.ExceptionUtil;
import toolbox.util.FileUtil;
import toolbox.util.StreamUtil;
import toolbox.util.SwingUtil;
import toolbox.util.XOMUtil;
import toolbox.util.io.StringOutputStream;
import toolbox.util.ui.ImageCache;
import toolbox.util.ui.tabbedpane.JSmartTabbedPane;
import toolbox.util.ui.tabbedpane.SmartTabbedPaneListener;

/**
 * Generic Frame that accepts pluggable GUI components that are displayed on
 * a tab panel. All pluggable GUI components must implements the IPlugin 
 * interface as a base set of functionality to be hosted by PluginWorkspace.
 */
public class PluginWorkspace extends JFrame implements IPreferenced
{
     // TODO: Plugin to configure log4j
     // TODO: Make plugins detachable
     // TODO: Make webstart enabled
     // TODO: Write log4j pattern layout that combines class name and method
     // TODO: Convert project build and layout to Maven
     // TODO: Added themes for Tiny Look and Feel
    
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
    private static final String   ATTR_SELECTED_TAB = "selectedtab";
    
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
    private Map plugins_ = new SequencedHashMap();
    
    /** 
     * Default initialization map for all plugins. Passed into IPlugin.startup() 
     */
    private Map bootstrapMap_;
    
    /**
     * Unloaded preferences
     */
    private Element unloadedPrefs_;
        
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
            
        buildView();
        loadPrefs();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        applyPrefs(prefs_);
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
        plugins_.put(plugin.getName(), plugin);

        // Init plugin
        plugin.startup(bootstrapMap_);

        // Create tab
        //JPanel pluginPanel = new JPanel(new BorderLayout());
        
        //pluginPanel.add(BorderLayout.CENTER, plugin.getComponent());
        //tabbedPane_.insertTab(plugin.getName(), null, pluginPanel, null, 0);
        //tabbedPane_.addTab(plugin.getName(), pluginPanel, ImageCache.getIcon(ImageCache.IMAGE_CROSS));
        
        tabbedPane_.addTab(
            plugin.getName(), 
            plugin.getComponent(), 
            ImageCache.getIcon(ImageCache.IMAGE_CROSS));
            
        //tabbedPane_.setSelectedIndex(0);
        
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
        plugins_.put(plugin.getName(), plugin);

        // Init plugin
        plugin.startup(bootstrapMap_);

        // Create tab
        //JPanel pluginPanel = new JPanel(new BorderLayout());
        
        //pluginPanel.add(BorderLayout.CENTER, plugin.getComponent());
        //tabbedPane_.insertTab(plugin.getName(), null, pluginPanel, null, 0);
        //tabbedPane_.addTab(plugin.getName(), pluginPanel, ImageCache.getIcon(ImageCache.IMAGE_CROSS));
        
        tabbedPane_.addTab(
            plugin.getName(), 
            plugin.getComponent(), 
            ImageCache.getIcon(ImageCache.IMAGE_CROSS));
        
        //tabbedPane_.setSelectedIndex(0);
        
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
                tabbedPane_.remove(tabbedPane_.indexOfTab(plugin.getName()));
                
            plugins_.remove(plugin.getName());
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
     * Builds the GUI
     */
    protected void buildView()
    {
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        tabbedPane_ = 
            new JSmartTabbedPane(); //ImageCache.getIcon(ImageCache.IMAGE_DELETE));
        
        tabbedPane_.addSmartTabbedPaneListener(new PluginTabbedPaneListener());
        contentPane.add(BorderLayout.CENTER, tabbedPane_);

        statusBar_ = new WorkspaceStatusBar();
        statusBar_.setStatus("Howdy pardner!");
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
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');        
        fileMenu.add(new PluginsAction());
        fileMenu.add(new SavePreferencesAction());
        fileMenu.add(createLookAndFeelMenu());
        fileMenu.add(new GarbageCollectAction());
        fileMenu.add(new ExitAction());            
        
        JMenuBar menubar = new JMenuBar();
        menubar.add(fileMenu);
        return menubar;
    }

    /**
     * Creates the look and feel menu by querying the UIManager for all 
     * installed look and feels.
     * 
     * @return Menu with all look and feels installed.
     */
    protected JMenu createLookAndFeelMenu()
    {
        lookAndFeelMenu_ = new JMenu("Look and Feel");
        
        UIManager.LookAndFeelInfo[] lookAndFeels_ = SwingUtil.getLAFs();
        ButtonGroup group = new ButtonGroup();
        
        for (int i=0; i<lookAndFeels_.length; i++)
        {
            JCheckBoxMenuItem lookAndFeelItem_ = 
                new JCheckBoxMenuItem(new SetLAFAction(lookAndFeels_[i]));
            
            group.add(lookAndFeelItem_);
            lookAndFeelMenu_.add(lookAndFeelItem_);
        }
        
        lookAndFeelMenu_.addSeparator();
        lookAndFeelMenu_.add(createThemesMenu());
        
        return lookAndFeelMenu_;
    }

    /**
     * Creates a themes menu for the plastic jgoodies.com look and feels
     * 
     * @return Menu with all the themes 
     */
    protected JMenu createThemesMenu()
    {
        JMenu menu = new JMenu("Themes");
        List themes = PlasticLookAndFeel.getInstalledThemes();
        
        for (int i=0, n=themes.size(); i<n; i++)
            menu.add(new SetThemeAction((PlasticTheme) themes.get(i)));
        
        return menu;
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

    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------

    /**
     * <pre>
     * 
     * Workspace
     *  |
     *  +--Plugin
     * 
     * </pre>
     * @see toolbox.util.ui.plugin.IPreferenced#savePrefs(nu.xom.Element)
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

        // Save look and feel
        root.addAttribute(new Attribute(
            ATTR_LAF, UIManager.getLookAndFeel().getClass().getName()));
            
        // Save currently selected tab
        root.addAttribute(
            new Attribute(ATTR_SELECTED_TAB,tabbedPane_.getSelectedIndex()+""));
                
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
            serializer.setIndent(3);
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
     * @see toolbox.util.ui.plugin.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs)
    {
        Element root = prefs.getFirstChildElement(NODE_WORKSPACE);
        
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

        if (root != null)
        {    
            // Restore look and feel
            String lafClass = XOMUtil.getStringAttribute(root, ATTR_LAF, null);
            
            if (lafClass != null)
            {
                try
                {
                    UIManager.setLookAndFeel(lafClass);
                    
                    SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            SwingUtilities.updateComponentTreeUI(
                                PluginWorkspace.this);
                        }
                    });
                }
                catch (Exception e)
                {
                    ExceptionUtil.handleUI(e, logger_);
                }   
            }
    
            // Activate the currently loaded look and feel in the menu
            String lafName = UIManager.getLookAndFeel().getName();        
            
            for (int i=0; i<lookAndFeelMenu_.getItemCount(); i++)
            {
                JMenuItem item = lookAndFeelMenu_.getItem(i);
                
                if (item instanceof JCheckBoxMenuItem)
                {
                    if (item.getText().equals(lafName))
                        item.setSelected(true);
                }
            }
            
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
                        String pluginClass = XOMUtil.getStringAttribute(
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
    // Actions
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
    
    /**
     * Adds/removes plugins to/from the plugin frame
     */
    class PluginsAction extends AbstractAction
    {
        PluginsAction()
        {
            super("Plugins ...");
            putValue(Action.MNEMONIC_KEY, new Integer('P'));
        }

        public void actionPerformed(ActionEvent ae)
        {
            JDialog dialog = new ManagePluginsDialog(PluginWorkspace.this);
            dialog.setVisible(true);    
        }
    }

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

    /**
     * Action that sets and look and feel
     */    
    class SetLAFAction extends WorkspaceAction
    {
        private UIManager.LookAndFeelInfo lafInfo_;
        
        SetLAFAction(UIManager.LookAndFeelInfo lafInfo)
        {
            super(lafInfo.getName(), false, null, null);
            putValue(Action.MNEMONIC_KEY, new Integer('L'));
            lafInfo_ = lafInfo;
        }

        public void runAction(ActionEvent e) throws Exception
        {
            UIManager.setLookAndFeel(lafInfo_.getClassName());
            SwingUtilities.updateComponentTreeUI(PluginWorkspace.this);
        }
    }

    /**
     * Action that sets the plastic theme
     */    
    class SetThemeAction extends WorkspaceAction
    {
        private PlasticTheme theme_;
        
        SetThemeAction(PlasticTheme theme)
        {
            super(theme.getName(), false, null, null);
            theme_ = theme;
        }

        public void runAction(ActionEvent e) throws Exception
        {
            PlasticLookAndFeel.setMyCurrentTheme(theme_);
            UIManager.setLookAndFeel(UIManager.getLookAndFeel());
            SwingUtilities.updateComponentTreeUI(PluginWorkspace.this);
        }
    }
    
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
}