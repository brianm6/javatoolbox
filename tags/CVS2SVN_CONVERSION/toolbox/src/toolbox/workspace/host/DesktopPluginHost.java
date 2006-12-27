package toolbox.workspace.host;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.commons.lang.ClassUtils;
import org.apache.log4j.Logger;

import toolbox.util.ExceptionUtil;
import toolbox.util.SwingUtil;
import toolbox.util.XOMUtil;
import toolbox.util.beans.BeanPropertyFilter;
import toolbox.util.ui.JSmartInternalFrame;
import toolbox.util.ui.JSmartMenu;
import toolbox.util.ui.JSmartMenuItem;
import toolbox.workspace.IPlugin;
import toolbox.workspace.IPreferenced;
import toolbox.workspace.PluginWorkspace;
import toolbox.workspace.PreferencedException;

/**
 * Plugin host that associates each plugin with a JInternalFrame. Multiple
 * plugins are accessible in an MDI manner because of the parent JDesktopPane.
 * 
 * @see toolbox.workspace.host.TabbedPluginHost 
 */
public class DesktopPluginHost extends AbstractPluginHost implements PluginHost, 
    IPreferenced
{
    private static final Logger logger_ = 
        Logger.getLogger(DesktopPluginHost.class);
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Desktop on which the plugins are added in separate internal frames.
     */
    private JDesktopPane desktop_;
    
    /**
     * Maps an IPlugin -> JInternalFrame.
     */
    private BidiMap pluginMap_;
    
    /**
     * Maps a JInternalFrame -> IPlugin. 
     */
    private BidiMap frameMap_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a DesktopPluginHost.
     */
    public DesktopPluginHost()
    {
        setName("Desktop");
    }
    
    //--------------------------------------------------------------------------
    // Initializable Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.util.service.Initializable#initialize(java.util.Map)
     */
    public void initialize(Map props)
    {
        super.initialize(props);
        setWorkspace((PluginWorkspace)props.get(PluginWorkspace.KEY_WORKSPACE));
        desktop_ = new JDesktopPane();
        pluginMap_ = new DualHashBidiMap();
        frameMap_ = pluginMap_.inverseBidiMap();
        addWindowMenu();
    }

    //--------------------------------------------------------------------------
    // PluginHost Interface
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.workspace.host.PluginHost#addPlugin(
     *      toolbox.workspace.IPlugin)
     */
    public void addPlugin(IPlugin plugin)
    {
        super.addPlugin(plugin);
    }

    
    /**
     * @see toolbox.workspace.host.PluginHost#removePlugin(
     *      toolbox.workspace.IPlugin)
     */
    public void removePlugin(IPlugin plugin)
    {
        super.removePlugin(plugin);
    }

    
    /**
     * @see toolbox.workspace.host.AbstractPluginHost#importPlugin(
     *      toolbox.workspace.IPlugin)
     */
    public void importPlugin(IPlugin plugin)
    {
        super.importPlugin(plugin);
        
        JSmartInternalFrame frame = new JSmartInternalFrame(
                                    plugin.getPluginName(),
                                    true,
                                    true,
                                    true,
                                    true);
        
        frame.getContentPane().setLayout(new BorderLayout());
        JComponent c = plugin.getView();
        frame.getContentPane().add(c, BorderLayout.CENTER);
        desktop_.add(frame);
        
        if (frame.getSize().equals(new Dimension(0, 0)))
            frame.pack();
        
        frame.setVisible(true);
        frame.moveToFront();
        pluginMap_.put(plugin, frame);

        if (c.getMinimumSize().height == 0 || c.getMinimumSize().width == 0)
            c.setMinimumSize(new Dimension(300, 200));
        
        //
        // When the internal frame is closed via the 'x' icon, route the event
        // to the equivalent of removing the plugin.
        //
        frame.addInternalFrameListener(new InternalFrameAdapter()
        {
            /**
             * @see javax.swing.event.InternalFrameAdapter#internalFrameClosed(
             *      javax.swing.event.InternalFrameEvent)
             */
            public void internalFrameClosed(InternalFrameEvent e)
            {
                JInternalFrame jif = e.getInternalFrame();
                IPlugin myPlugin = (IPlugin) frameMap_.get(jif);
                
                //
                // Delegate removal of the plugin to the workspace since we
                // have no visibility into all the other things that need to 
                // happend (XML prefs, etc) that need to be saved at this
                // level.
                //
                
                try
                {
                    getWorkspace().deregisterPlugin(
                        myPlugin.getClass().getName(), true);
                }
                catch (Exception e1)
                {
                    ExceptionUtil.handleUI(e1, logger_);
                }
            }
        });
    }

    
    /**
     * @see toolbox.workspace.host.AbstractPluginHost#exportPlugin(
     *      toolbox.workspace.IPlugin)
     */
    public void exportPlugin(IPlugin plugin)
    {
        desktop_.remove((JComponent) pluginMap_.get(plugin));
        pluginMap_.remove(plugin);
        
        super.exportPlugin(plugin);
    }    
    
    
    /**
     * @see toolbox.workspace.host.PluginHost#getView()
     */
    public JComponent getView()
    {
        return desktop_;
    }
    
    
    /**
     * @see toolbox.workspace.host.PluginHost#setSelectedPlugin(
     *      toolbox.workspace.IPlugin)
     */
    public void setSelectedPlugin(IPlugin plugin)
    {
        JInternalFrame jif = (JInternalFrame) pluginMap_.get(plugin);
        jif.moveToFront();
        
        try
        {
            jif.setSelected(true);
        }
        catch (PropertyVetoException e)
        {
            ExceptionUtil.handleUI(e, logger_);
        }
    }
    
    
    /**
     * @see toolbox.workspace.host.PluginHost#getSelectedPlugin()
     */
    public IPlugin getSelectedPlugin()
    {
        return (IPlugin) frameMap_.get(desktop_.getSelectedFrame());
    }
    
    //--------------------------------------------------------------------------
    // Destroyable Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.service.Destroyable#destroy()
     */
    public void destroy()
    {
        removeWindowMenu();
        desktop_.removeAll();
        desktop_ = null;
        
        pluginMap_.clear();
        pluginMap_ = null;
        
        super.destroy();
    }
    
    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws PreferencedException
    {
        logger_.debug("Applying DesktopPluginHost preferences");
        
        Map class2Frame = new HashMap();
        
        //
        // Associated a plugin class with an internal frame
        //
        for (Iterator i = pluginMap_.keySet().iterator(); i.hasNext();)
        {
            IPlugin plugin = (IPlugin) i.next();
            
            class2Frame.put(
                plugin.getClass().getName(), 
                pluginMap_.get(plugin));
        }
        
        Element root = XOMUtil.getFirstChildElement(
            prefs, NODE_PLUGINHOST, new Element(NODE_PLUGINHOST));
        
        Elements frames = root.getChildElements(NODE_FRAME);
        
        logger_.debug("Found " + frames.size() + " frames on the desktop");
        
        for (int i = 0; i < frames.size(); i++)
        {
            logger_.debug("Processing frame " + i);
            
            Element frame = frames.get(i);
            
            String pluginClass = 
                XOMUtil.getStringAttribute(frame, ATTR_CLASS, null);
            
            if (pluginClass != null)
            {
                logger_.debug("Found plugin class " + 
                    ClassUtils.getShortClassName(pluginClass));
                
                JSmartInternalFrame sif = 
                    (JSmartInternalFrame) class2Frame.get(pluginClass);
                
                if (sif != null)
                {
                    logger_.debug("Reassociated with frame! Applying prefs...");
                    sif.applyPrefs(frame);
                }
                else
                {
                    logger_.debug("Could not re-associate with a frame :(");
                }
            }
            else
            {
                logger_.debug("No plugin found");
            }
        }
    }


    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws PreferencedException
    {
        Element root = new Element(NODE_PLUGINHOST);
        root.addAttribute(new Attribute(ATTR_CLASS, getClass().getName()));

        for (Iterator i = frameMap_.keySet().iterator(); i.hasNext();)
        {
            JSmartInternalFrame sif = (JSmartInternalFrame) i.next();
            IPlugin plugin = (IPlugin) frameMap_.get(sif);
            Element frame = new Element(NODE_FRAME);
            
            frame.addAttribute(new Attribute(
                ATTR_CLASS, plugin.getClass().getName()));
            
            sif.savePrefs(frame);
            root.appendChild(frame);
        }

        XOMUtil.insertOrReplace(prefs, root);
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Adds the Window menu to the menu bar on plugin startup. 
     */
    protected void addWindowMenu()
    {
        JMenuBar menuBar = getWorkspace().getJMenuBar();
        JSmartMenu windowMenu = new JSmartMenu("Window");
        windowMenu.setName("Window");
        
        windowMenu.add(new JSmartMenuItem(new AbstractAction("Tile")
        {
            public void actionPerformed(ActionEvent e)
            {
                SwingUtil.tile(desktop_);
            }
        }));

        windowMenu.add(new JSmartMenuItem(new AbstractAction("Cascade")
        {
            public void actionPerformed(ActionEvent e)
            {
                SwingUtil.cascade(desktop_);
            }
        }));
        
        // If this is the initial plugin host, then the initialization sequence
        // has not yet created the menubar. Workaround if null...
        
        if (menuBar != null)
        {
            menuBar.add(windowMenu);
            menuBar.revalidate();
        }
        else
            logger_.debug("TODO: Fix adding Window menu to null menubar");
    }
    
    
    /**
     * Removes the Window menu from the menu bar on plugin host shutdown. 
     */
    protected void removeWindowMenu()
    {
        JMenuBar menuBar = getWorkspace().getJMenuBar();
        List results = new ArrayList();
        SwingUtil.findInstancesOf(JSmartMenu.class, menuBar, results);
        
        CollectionUtils.filter(
            results, 
            new BeanPropertyFilter("name", "Window"));
        
        if (!results.isEmpty())
        {
            menuBar.remove((JSmartMenu) results.iterator().next());
            logger_.debug("Removed window menu from menu bar");
        }
    }
}