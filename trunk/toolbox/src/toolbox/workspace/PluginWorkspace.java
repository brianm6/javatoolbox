package toolbox.util.ui.plugin;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.commons.collections.SequencedHashMap;
import org.apache.log4j.Logger;

import toolbox.log4j.SmartLogger;
import toolbox.util.ElapsedTime;
import toolbox.util.ExceptionUtil;
import toolbox.util.FileUtil;
import toolbox.util.PropertiesUtil;
import toolbox.util.StreamUtil;
import toolbox.util.StringUtil;
import toolbox.util.SwingUtil;
import toolbox.util.ui.ImageCache;

/**
 * Generic Frame that accepts pluggable GUI components that are displayed on
 * a tab panel. All pluggable GUI components must implements the IPlugin 
 * interface as a base set of functionality to be hosted by PluginWorkspace.
 */
public class PluginWorkspace extends JFrame implements IPreferenced
{
    /*
     * TODO: Plugin to configure log4j
     * TODO: Make plugins detachable
     * TODO: Make webstart enabled
     * TODO: Write log4j pattern layout that combines class name and method
     * TODO: Abstraction for concrete regular expression engine implementation
     * TODO: Convert project build and layout to Maven
     * TODO: Use Quilt for JUnit Test coverage instead of Clover
     * TODO: Add support for selecting Plastic Look & Feel color themes
     */
        
    private static final Logger logger_ = 
        Logger.getLogger(PluginWorkspace.class);

    private static final String FILE_PREFS    = ".toolbox.properties";
    private static final String PROP_MAXXED   = "workspace.maximized";
    private static final String PROP_WIDTH    = "workspace.width";
    private static final String PROP_HEIGHT   = "workspace.height";
    private static final String PROP_XCOORD   = "workspace.xcoord";
    private static final String PROP_YCOORD   = "workspace.ycoord";
    private static final String PROP_LAF      = "workspace.lookandfeel";
    private static final String PROP_LOADED   = "workspace.plugins.loaded";
    private static final String PROP_SELECTED = "workspace.plugins.selected";
    
    /** Plugins are added to this tab panel in order or registration */
    private JTabbedPane tabbedPane_;
    
    /** Status bar at bottom of screen */
    private IStatusBar statusBar_;
    
    /** Look and Feel Menu Items */
    private JMenu lafMenu_;
    
    /** Preferences stored as NV pairs */
    private Properties prefs_;
    
    /** Map of plugin names -> plugins */
    private Map plugins_ = new SequencedHashMap();

    //--------------------------------------------------------------------------
    // Main 
    //--------------------------------------------------------------------------
    
    /**
     * Entrypoint 
     * 
     * @param  args  None recognized
     */
    public static void main(String args[])
    {
        try
        {
            PluginWorkspace frame = new PluginWorkspace();
            //frame.setVisible(true);
        }
        catch(Exception e)
        {
            ExceptionUtil.handleUI(e, logger_);
        }
    }

    //--------------------------------------------------------------------------
    //  Constructors
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
    //  Public
    //--------------------------------------------------------------------------

    /**
     * Registers a plugin with the GUI. Must be called prior buildView()
     * 
     * @param  plugin  Plugin to add to the GUI
     */
    public void registerPlugin(IPlugin plugin)
    {
        // Add to registry    
        plugins_.put(plugin.getName(), plugin);

        // Init plugin
        plugin.init();

        // Give plugin's its status bar
        plugin.setStatusBar(statusBar_);
        
        // Create tab
        JPanel pluginPanel = new JPanel(new BorderLayout());
        
        pluginPanel.add(BorderLayout.CENTER, plugin.getComponent());
        tabbedPane_.insertTab(plugin.getName(), null, pluginPanel, null, 0);
        tabbedPane_.setSelectedIndex(0);
        
        // Restore preferences
        plugin.applyPrefs(prefs_);
    }

    /**
     * Registeres a plugin given its FQN
     * 
     * @param  pluginClass  Name of plugin class that implements the IPlugin 
     *                      interface
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
     * Deregisters a plugin given its fully qualified name
     * 
     * @param   pluginClass  Class name of plugin to remove
     * @throws  Exception on error
     */
    public void deregisterPlugin(String pluginClass) throws Exception
    {
        // Make sure plugin is registered
        if (hasPlugin(pluginClass))
        {
            IPlugin plugin = getPluginByClass(pluginClass);
            tabbedPane_.remove(tabbedPane_.indexOfTab(plugin.getName()));
            plugin.setStatusBar(null);
            plugins_.remove(plugin.getName());
            plugin.shutdown();
        }
        else
        {
            logger_.warn("Plugin " + pluginClass + " was not found.");
        }
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

        tabbedPane_ = new JTabbedPane();
        contentPane.add(BorderLayout.CENTER, tabbedPane_);

        statusBar_ = new WorkspaceStatusBar();
        statusBar_.setStatus("Howdy pardner!");
        contentPane.add(BorderLayout.SOUTH, (Component) statusBar_);
        
        setJMenuBar(createMenuBar());
        
        addWindowListener(new CloseWindowListener());
        
        setIconImage(
            ImageCache.getImage("/toolbox/util/ui/images/Toolbox.gif"));
    }

    /**
     * Creates and configures the menu bar
     * 
     * @return  JMenuBar
     */
    protected JMenuBar createMenuBar()
    {
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');        
        fileMenu.add(new PluginsAction());

        lafMenu_ = new JMenu("Look and Feel");
        
        UIManager.LookAndFeelInfo[] lafs = SwingUtil.getLAFs();
        ButtonGroup group = new ButtonGroup();
        
        for (int i=0; i<lafs.length; i++)
        {
            JCheckBoxMenuItem lafItem = 
                new JCheckBoxMenuItem(new SetLAFAction(lafs[i]));
            
            group.add(lafItem);
            lafMenu_.add(lafItem);
        }
        
        fileMenu.add(new SavePreferencesAction());
        fileMenu.add(lafMenu_);
        fileMenu.add(new GarbageCollectAction());
        fileMenu.add(new ExitAction());            
        
        JMenuBar menubar = new JMenuBar();
        menubar.add(fileMenu);
        return menubar;
    }

    /**
     * Determines if a plugin is active given its FQN
     * 
     * @param  pluginClass  FQN of plugin class
     * @return True if plugin is registered, false otherwise
     */
    protected boolean hasPlugin(String pluginClass) 
    {
        for (Iterator i = plugins_.values().iterator(); i.hasNext();)
        {
            if (i.next().getClass().getName().equals(pluginClass))
                return true;
        }
        
        return false;
    }
    
    /**
     * @return Plugin for a given class
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
     * Loads preferences from $HOME/.toolbox.properties
     */
    protected void loadPrefs()
    {
        prefs_ = new Properties();
        
        String userhome = System.getProperty("user.home");
        
        userhome = FileUtil.trailWithSeparator(userhome);
            
        File f = new File( userhome + FILE_PREFS);
        
        if (f.exists() && f.canRead() && f.isFile())
        {
            try
            {
                prefs_.load(new FileInputStream(f));
                SmartLogger.debug(logger_, PropertiesUtil.toString(prefs_));
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

    public void savePrefs(Properties prefs)
    {
        boolean maxxed = 
            (getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH;
            
        PropertiesUtil.setBoolean(prefs, PROP_MAXXED, maxxed);
        
        if (!maxxed)
        {
            // Save window location
            PropertiesUtil.setInteger(prefs, PROP_XCOORD, getLocation().x);
            PropertiesUtil.setInteger(prefs, PROP_YCOORD, getLocation().y);
        
            // Save window size
            PropertiesUtil.setInteger(prefs, PROP_WIDTH, getSize().width);
            PropertiesUtil.setInteger(prefs, PROP_HEIGHT, getSize().height); 
        }
        else
        {
            prefs.remove(PROP_XCOORD);
            prefs.remove(PROP_YCOORD);
            prefs.remove(PROP_WIDTH);
            prefs.remove(PROP_HEIGHT);
        }

        // Save look and feel
        prefs.setProperty(PROP_LAF, 
            UIManager.getLookAndFeel().getClass().getName());
                
        // Save plugin prefs too
        String pluginLine = "";
        
        for (Iterator i = plugins_.values().iterator(); i.hasNext();)
        {
            IPlugin plugin = (IPlugin) i.next();
            plugin.savePrefs(prefs);
            pluginLine += plugin.getClass().getName() + ",";
        }
        
        prefs.setProperty(PROP_LOADED , pluginLine);
        
        // Save currently selected tab
        PropertiesUtil.setInteger(prefs, PROP_SELECTED, 
            tabbedPane_.getSelectedIndex());
            
        // Save to file
        String userhome = System.getProperty("user.home");
        
        userhome = FileUtil.trailWithSeparator(userhome);
            
        File f = new File( userhome + FILE_PREFS);
        FileOutputStream fos = null;

        try
        {
            fos = new FileOutputStream(f);
            prefs.store(fos,"");
        }
        catch (IOException ioe)
        {
            ExceptionUtil.handleUI(ioe, logger_);
        }
        finally
        {
            StreamUtil.close(fos);
        }
        
        SmartLogger.debug(logger_, PropertiesUtil.toString(prefs));
        
        statusBar_.setStatus("Saved preferences");
    }

    /**
     * @see toolbox.util.ui.plugin.IPreferenced#applyPrefs(java.util.Properties)
     */
    public void applyPrefs(Properties prefs)
    {
        boolean maxxed = PropertiesUtil.getBoolean(prefs, PROP_MAXXED, false);
        
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
            PropertiesUtil.getInteger(prefs, PROP_XCOORD, 0),
            PropertiesUtil.getInteger(prefs, PROP_YCOORD, 0));
        
        // Restore window size
        setSize(
            PropertiesUtil.getInteger(prefs, PROP_WIDTH, 800),
            PropertiesUtil.getInteger(prefs, PROP_HEIGHT, 600));
            
        // Reload Plugins that were saved
        String pluginLine = prefs.getProperty(PROP_LOADED ,"");
        String[] plugins = StringUtil.tokenize(pluginLine, ",");

        // Restore look and feel
        String lafClass = prefs.getProperty(PROP_LAF);
        
        if (lafClass != null)
        {
            try
            {
                UIManager.setLookAndFeel(lafClass);
                SwingUtilities.updateComponentTreeUI(this);
            }
            catch (Exception e)
            {
                ExceptionUtil.handleUI(e, logger_);
            }   
        }

        // Activate the currently loaded look and feel in the menu
        String lafName = UIManager.getLookAndFeel().getName();        
        
        for (int i=0; i<lafMenu_.getItemCount(); i++)
        {
            JMenuItem item = lafMenu_.getItem(i);
            
            if (item instanceof JCheckBoxMenuItem)
            {
                if (item.getText().equals(lafName))
                    item.setSelected(true);
            }
        }
        
        // Register plugins. Don't let failures stop process
        for (int i=0; i<plugins.length; i++) 
        {
            try
            {
                registerPlugin(plugins[i]);       
            }
            catch (Throwable t)
            {
                ExceptionUtil.handleUI(t, logger_);
            }
        }
        
        // Restore last selected tab
        tabbedPane_.setSelectedIndex(
            PropertiesUtil.getInteger(prefs, PROP_SELECTED,-1));
    }       

    //--------------------------------------------------------------------------
    //  Package Protected
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
    //  Inner Classes
    //--------------------------------------------------------------------------

    /**
     * Saves preferences when the application is closed
     */
    private class CloseWindowListener extends WindowAdapter
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
     * Post initialization of the GUI after the frame has been realized
     */    
    class PostInit implements Runnable
    {
        public void run()
        {
            applyPrefs(prefs_);
            //invalidate();
            //doLayout();
            //repaint();
        }        
    }
    
    //--------------------------------------------------------------------------
    //  Actions
    //--------------------------------------------------------------------------

    /**
     * Exits the appication
     */
    class ExitAction extends AbstractAction
    {
        ExitAction()
        {
            super("Exit");
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
    class SavePreferencesAction extends AbstractAction
    {
        public SavePreferencesAction()
        {
            super("Save prefs");
        }
        
        public void actionPerformed(ActionEvent e)
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
        
        public SetLAFAction(UIManager.LookAndFeelInfo lafInfo)
        {
            super(lafInfo.getName(), false, null, null);
            lafInfo_ = lafInfo;
        }

        public void runAction(ActionEvent e) throws Exception
        {
            UIManager.setLookAndFeel(lafInfo_.getClassName());
            SwingUtilities.updateComponentTreeUI(PluginWorkspace.this);
        }
    }
    
    /**
     * Triggers garbage collection
     */
    class GarbageCollectAction extends WorkspaceAction
    {
        public GarbageCollectAction()
        {
            super("Run GC", false, null, null);
            
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