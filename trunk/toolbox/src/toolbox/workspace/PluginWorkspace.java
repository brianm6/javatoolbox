package toolbox.util.ui.plugin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
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
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

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
 */
public class PluginWorkspace extends JFrame implements IStatusBar
{
    /** Logger */
    private static final Logger logger_ = 
        Logger.getLogger(PluginWorkspace.class);

    private static final String FILE_PREFS      = ".toolbox.properties";
    private static final String KEY_WIDTH       = "pluginframe.width";
    private static final String KEY_HEIGHT      = "pluginframe.height";
    private static final String KEY_XCOORD      = "pluginframe.xcoord";
    private static final String KEY_YCOORD      = "pluginframe.ycoord";
    private static final String KEY_PLUGINS     = "pluginframe.plugins";
    
    /**
     * Plugins are added to this tab panel in order or registration
     */
    private JTabbedPane tabbedPane_;
    
    /**
     *  Status bar at bottom of screen
     */
    private JLabel statusLabel_;
    
    /**
     *  Preferences stores as NV pairs
     */
    private Properties prefs_;
    
    /**
     * Map of plugin names -> plugins
     */
    private Map plugins_ = new SequencedHashMap();

    private String[] pluginNames_ = new String[0];
    
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
            PluginWorkspace frame = new PluginWorkspace(args);
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
     * @param  plugins  Fully qualified names of classes that implment the
     *                  IPlugin interface
     * @throws Exception on error
     */
    public PluginWorkspace(String[] plugins) throws Exception
    {
        super("Plugin Workspace");
            
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
        try
        {
            SwingUtil.setPreferredLAF();
        }
        catch (Exception e)
        {
            ExceptionUtil.handleUI(e, logger_);
        }
        
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        tabbedPane_ = new JTabbedPane();
        contentPane.add(BorderLayout.CENTER, tabbedPane_);

        statusLabel_ = new JLabel(" Howdy pardner!");
        statusLabel_.setBorder(BorderFactory.createLoweredBevelBorder());
        contentPane.add(statusLabel_, BorderLayout.SOUTH);
        
        setJMenuBar(createMenuBar());

        // Prepare the glass pane for display of a 'Please wait' message:
        JComponent glassPane = (JComponent)getGlassPane();
        GridBagLayout layout = new GridBagLayout();
        glassPane.setLayout(layout);
    
        // Create a JLabel centered in the glass pane, to be displayed
        // when the glass pane is made visible:
        JLabel waitLabel = new JLabel("Please wait...");
        waitLabel.setOpaque(true);
        waitLabel.setForeground(Color.blue);
        waitLabel.setBackground(Color.yellow);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        layout.setConstraints(waitLabel, gbc);
        glassPane.add(waitLabel);

        // Add dummy listeners to the glass pane to disable all input
        // when it is visible:
        glassPane.addKeyListener(new DummyKeyAdapter());
        glassPane.addMouseListener(new DummyMouseAdapter());
        glassPane.addMouseMotionListener(new DummyMouseMotionAdapter());
        
        addWindowListener(new CloseWindowListener());
    }

    /**
     * Loads preferences from $HOME/.pluginframe.properties
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
        logger_.debug("Saving preferences");

        // Save window location
        PropertiesUtil.setInteger(prefs_, KEY_XCOORD, getLocation().x);
        PropertiesUtil.setInteger(prefs_, KEY_YCOORD, getLocation().y);
        
        // Save window size
        PropertiesUtil.setInteger(prefs_, KEY_WIDTH, getSize().width);
        PropertiesUtil.setInteger(prefs_, KEY_HEIGHT, getSize().height); 
        
        // Save plugin prefs too
        String pluginLine = "";
        for (Iterator i = plugins_.values().iterator(); i.hasNext(); )
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
    
    /**
     * Consumes key events when the glass pane is active
     */
    private class DummyKeyAdapter extends KeyAdapter
    {
        public void keyPressed(KeyEvent ke)
        {
            ke.consume();
        }

        public void keyTyped(KeyEvent ke)
        {
            ke.consume();
        }

        public void keyReleased(KeyEvent ke)
        {
            ke.consume();
        }
    }

    /**
     * Consumes mouse events when the glass pane is active
     */
    private class DummyMouseAdapter extends MouseAdapter
    {
        public void mouseClicked(MouseEvent me)
        {
            me.consume();
        }

        public void mouseEntered(MouseEvent me)
        {
            me.consume();
        }

        public void mouseExited(MouseEvent me)
        {
            me.consume();
        }

        public void mousePressed(MouseEvent me)
        {
            me.consume();
        }

        public void mouseReleased(MouseEvent me)
        {
            me.consume();
        }
    }

    /**
     * Consumes mouse movement events when the glasspane is active
     */
    private class DummyMouseMotionAdapter extends MouseMotionAdapter
    {
        public void mouseDragged(MouseEvent me)
        {
            me.consume();
        }

        public void mouseMoved(MouseEvent me)
        {
            me.consume();
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
}    