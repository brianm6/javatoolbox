package toolbox.util.ui.plugin;

import java.awt.BorderLayout;
import java.awt.Container;
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
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.commons.collections.SequencedHashMap;
import org.apache.log4j.Logger;

import toolbox.util.ExceptionUtil;
import toolbox.util.FileUtil;
import toolbox.util.PropertiesUtil;
import toolbox.util.ResourceCloser;
import toolbox.util.StringUtil;
import toolbox.util.SwingUtil;

/**
 * Generic Frame that accepts pluggable GUI components that are displayed on
 * a tab panel. All pluggable GUI components must implements the IPlugin 
 * interface as a base set of functionality to be hosted by PluginWorkspace.
 * 
 * <pre>
 * 
 * TODO: Plugin to tail System.out and System.err
 * TODO: Plugin to configure log4j
 * TODO: Make plugins detachable
 * TODO: Make webstart enabled
 * 
 * </pre>
 */
public class PluginWorkspace extends JFrame implements IStatusBar
{
    /** Logger */
    private static final Logger logger_ = 
        Logger.getLogger(PluginWorkspace.class);

    private static final String FILE_PREFS  = ".toolbox.properties";
    private static final String KEY_WIDTH   = "workspace.width";
    private static final String KEY_HEIGHT  = "workspace.height";
    private static final String KEY_XCOORD  = "workspace.xcoord";
    private static final String KEY_YCOORD  = "workspace.ycoord";
    private static final String KEY_PLUGINS = "workspace.plugins";
    private static final String KEY_LAF     = "workspace.lookandfeel";
    
    /**
     * Plugins are added to this tab panel in order or registration
     */
    private JTabbedPane tabbedPane_;
    
    /**
     *  Status bar at bottom of screen
     */
    private JLabel statusLabel_;
    
    /**
     * Look and Feel Menu Items
     */
    private JMenu lafMenu_;
    
    /**
     *  Preferences stores as NV pairs
     */
    private Properties prefs_;
    
    /**
     * Map of plugin names -> plugins
     */
    private Map plugins_ = new SequencedHashMap();

    /**
     * Entrypoint 
     * 
     * @param  args  Args can be a list of one or more classnames that 
     *               implement the IPlugin interface
     */
    public static void main(String args[])
    {
        try
        {
            PluginWorkspace frame = new PluginWorkspace();
            frame.setVisible(true);
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
     * Create a new PluginWorkspace
     * 
     * @throws Exception on error
     */
    public PluginWorkspace() throws Exception
    {
        super("Toolbox");
            
        buildView();
        loadPrefs();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Nasty hack so plugins get initialized after the empty
        // workspace has been realized
        SwingUtilities.invokeLater(new PostInit());
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

        // Give plugin's its status bar
        plugin.setStatusBar(this);

        // Init plugin
        plugin.init();
        
        // Create tab
        JPanel pluginPanel = new JPanel(new BorderLayout());
        
        if (plugin.getMenuBar() != null)
            pluginPanel.add(BorderLayout.NORTH, plugin.getMenuBar());
            
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
     * Deregisters a plugin given its FQN
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
            logger_.warn("deregisterPlugin: Plugin " + pluginClass + 
                " not found.");
        }
    }

    //--------------------------------------------------------------------------
    //  Private
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

        statusLabel_ = new JLabel(" Howdy pardner!");
        statusLabel_.setBorder(BorderFactory.createLoweredBevelBorder());
        contentPane.add(statusLabel_, BorderLayout.SOUTH);
        
        setJMenuBar(createMenuBar());
        
        addWindowListener(new CloseWindowListener());
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

    //--------------------------------------------------------------------------
    // Preferences
    //--------------------------------------------------------------------------
    
    /**
     * Loads preferences from $HOME/.toolbox.properties
     */
    protected void loadPrefs()
    {
        prefs_ = new Properties();
        
        String userhome = System.getProperty("user.home");
        
        if (!userhome.endsWith(File.separator))
            userhome = userhome + File.separator;
            
        File f = new File( userhome + FILE_PREFS);
        
        if (f.exists() && f.canRead() && f.isFile())
        {
            try
            {
                prefs_.load(new FileInputStream(f));
                
                //StringWriter sw = new StringWriter();
                //prefs_.list(new PrintWriter(sw));
                //logger_.debug("Prefs:\n" + sw.toString());
                
            }
            catch (IOException ioe)
            {
                logger_.warn("loadPrefs", ioe);
            }
        }
        else
            logger_.debug("Preferences file not found.");
    }

    /**
     * Saves preferences to a properties file
     */
    protected void savePrefs()
    {
        // Save window location
        PropertiesUtil.setInteger(prefs_, KEY_XCOORD, getLocation().x);
        PropertiesUtil.setInteger(prefs_, KEY_YCOORD, getLocation().y);
        
        // Save window size
        PropertiesUtil.setInteger(prefs_, KEY_WIDTH, getSize().width);
        PropertiesUtil.setInteger(prefs_, KEY_HEIGHT, getSize().height); 

        // Save look and feel
        prefs_.setProperty(KEY_LAF, 
            UIManager.getLookAndFeel().getClass().getName());
                
        // Save plugin prefs too
        String pluginLine = "";
        
        for (Iterator i = plugins_.values().iterator(); i.hasNext();)
        {
            IPlugin plugin = (IPlugin) i.next();
            plugin.savePrefs(prefs_);
            pluginLine += plugin.getClass().getName() + ",";
        }
        
        prefs_.setProperty(KEY_PLUGINS, pluginLine);
    
        // Save to file
        String userhome = System.getProperty("user.home");
        
        userhome = FileUtil.trailWithSeparator(userhome);
            
        File f = new File( userhome + FILE_PREFS);
        FileOutputStream fos = null;

        try
        {
            fos = new FileOutputStream(f);
            prefs_.store(fos,"");
        }
        catch (IOException ioe)
        {
            ExceptionUtil.handleUI(ioe, logger_);
        }
        finally
        {
            ResourceCloser.close(fos);
        }
        
        setStatus("Saved preferences");
    }

    /**
     * Applies the preferences
     */
    protected void applyPrefs()
    {
        // Restore window location
        setLocation(
            PropertiesUtil.getInteger(prefs_, KEY_XCOORD, 0),
            PropertiesUtil.getInteger(prefs_, KEY_YCOORD, 0));
        
        // Restore window size
        setSize(
            PropertiesUtil.getInteger(prefs_, KEY_WIDTH, 800),
            PropertiesUtil.getInteger(prefs_, KEY_HEIGHT, 600)); 
            
        // Reload Plugins that were saved
        String pluginLine = prefs_.getProperty(KEY_PLUGINS,"");
        String[] plugins = StringUtil.tokenize(pluginLine, ",");

        // Restore look and feel
        String lafClass = prefs_.getProperty(KEY_LAF);
        
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
    //  IStatusBar Interface
    //--------------------------------------------------------------------------
    
    /**
     * Sets the text of the status bar
     * 
     * @param  status  Status message
     */
    public void setStatus(String status)
    {
        // registerPlugin gets called before buildView and 
        // msg generaator panel calls this when constructed
        
        if (statusLabel_ != null)
            statusLabel_.setText(status);
    }
    
    /**
     * Retrieves the status text
     * 
     * @return  Status text
     */
    public String getStatus()
    {
        return statusLabel_.getText();
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
            savePrefs();            
        }
        
        public void windowOpened(WindowEvent e)
        {
            logger_.info("windowOpened()");
        }
    }

    /**
     * Post initialization of the GUI after the frame has been realized
     */    
    class PostInit implements Runnable
    {
        public PostInit()
        {
        }
        
        public void run()
        {
            applyPrefs();
            invalidate();
            doLayout();
            repaint();
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
            savePrefs();
        }
    }

    /**
     * Action that sets and look and feel
     */    
    class SetLAFAction extends AbstractAction
    {
        private UIManager.LookAndFeelInfo lafInfo_;
        
        public SetLAFAction(UIManager.LookAndFeelInfo lafInfo)
        {
            super(lafInfo.getName());
            lafInfo_ = lafInfo;
        }
        
        public void actionPerformed(ActionEvent e)
        {
            try
            { 
                UIManager.setLookAndFeel(lafInfo_.getClassName());
                SwingUtilities.updateComponentTreeUI(PluginWorkspace.this);
            }
            catch (Exception ex)
            {
                ExceptionUtil.handleUI(ex, logger_);
            }
        }
    }
}