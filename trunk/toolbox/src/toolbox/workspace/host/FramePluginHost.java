package toolbox.workspace.host;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;

import nu.xom.Element;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.log4j.Logger;

import toolbox.util.ExceptionUtil;
import toolbox.util.SwingUtil;
import toolbox.util.beans.BeanPropertyFilter;
import toolbox.util.ui.JSmartFrame;
import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.JSmartMenu;
import toolbox.util.ui.JSmartMenuItem;
import toolbox.workspace.IPlugin;
import toolbox.workspace.IPreferenced;
import toolbox.workspace.PluginWorkspace;

/**
 * Plugin host that associates each plugin with a JFrame.
 * 
 * @see toolbox.workspace.host.TabbedPluginHost 
 * @see toolbox.workspace.host.DesktopPluginHost 
 */
public class FramePluginHost extends AbstractPluginHost implements PluginHost, 
    IPreferenced
{
    private static final Logger logger_ = 
        Logger.getLogger(FramePluginHost.class);
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Maps an IPlugin -> JFrame.
     */
    private BidiMap pluginMap_;
    
    /**
     * Maps a JFrame -> IPlugin. 
     */
    private BidiMap frameMap_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a FramePluginHost.
     */
    public FramePluginHost()
    {
        setName("Frame");
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
        
        JSmartFrame frame = new JSmartFrame(plugin.getPluginName());
        frame.getContentPane().setLayout(new BorderLayout());
        JComponent c = plugin.getView();
        frame.getContentPane().add(c, BorderLayout.CENTER);
        
        if (frame.getSize().equals(new Dimension(0, 0)))
            frame.pack();
        
        frame.setVisible(true);
        frame.toFront();
        pluginMap_.put(plugin, frame);

        if (c.getMinimumSize().height == 0 || c.getMinimumSize().width == 0)
            c.setMinimumSize(new Dimension(300, 200));
        
        //
        // When the internal frame is closed via the 'x' icon, route the event
        // to the equivalent of removing the plugin.
        //
        frame.addWindowListener(new WindowAdapter()
        {
            /**
             * @see java.awt.event.WindowAdapter#windowClosed(
             *      java.awt.event.WindowEvent)
             */
            public void windowClosed(WindowEvent e)
            {
                JFrame jif = (JFrame) e.getWindow();
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
        JFrame frame = (JFrame) pluginMap_.get(plugin);
        frame.dispose();
        pluginMap_.remove(plugin);
        super.exportPlugin(plugin);
    }    
    
    
    /**
     * @see toolbox.workspace.host.PluginHost#getComponent()
     */
    public JComponent getView()
    {
        return new JSmartLabel("FramePluginHost");
    }
    
    
    /**
     * @see toolbox.workspace.host.PluginHost#setSelectedPlugin(
     *      toolbox.workspace.IPlugin)
     */
    public void setSelectedPlugin(IPlugin plugin)
    {
        JFrame jif = (JFrame) pluginMap_.get(plugin);
        jif.toFront();
        
//        try
//        {
//            jif.setSelected(true);
//        }
//        catch (PropertyVetoException e)
//        {
//            ExceptionUtil.handleUI(e, logger_);
//        }
    }
    
    
    /**
     * @see toolbox.workspace.host.PluginHost#getSelectedPlugin()
     */
    public IPlugin getSelectedPlugin()
    {
        Frame[] frames = JFrame.getFrames();
        
        for (int i = 0; i < frames.length; i++)
            if (frames[i].isFocused())
                return (IPlugin) frameMap_.get(frames[i]);
        
        // TODO: Fix me
        return null;
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
    public void applyPrefs(Element prefs) throws Exception
    {
//        logger_.debug("Applying FramePluginHost preferences");
//        
//        Map class2Frame = new HashMap();
//        
//        //
//        // Associated a plugin class with an internal frame
//        //
//        for (Iterator i = pluginMap_.keySet().iterator(); i.hasNext();)
//        {
//            IPlugin plugin = (IPlugin) i.next();
//            
//            class2Frame.put(
//                plugin.getClass().getName(), 
//                pluginMap_.get(plugin));
//        }
//        
//        Element root = XOMUtil.getFirstChildElement(
//            prefs, NODE_PLUGINHOST, new Element(NODE_PLUGINHOST));
//        
//        Elements frames = root.getChildElements(NODE_FRAME);
//        
//        logger_.debug("Found " + frames.size() + " frames on the desktop");
//        
//        for (int i = 0; i < frames.size(); i++)
//        {
//            logger_.debug("Processing frame " + i);
//            
//            Element frame = frames.get(i);
//            
//            String pluginClass = 
//                XOMUtil.getStringAttribute(frame, ATTR_CLASS, null);
//            
//            if (pluginClass != null)
//            {
//                logger_.debug("Found plugin class " + 
//                    ClassUtils.getShortClassName(pluginClass));
//                
//                JSmartInternalFrame sif = 
//                    (JSmartInternalFrame) class2Frame.get(pluginClass);
//                
//                if (sif != null)
//                {
//                    logger_.debug("Reassociated with frame! Applying prefs...");
//                    sif.applyPrefs(frame);
//                }
//                else
//                {
//                    logger_.debug("Could not re-associate with a frame :(");
//                }
//            }
//            else
//            {
//                logger_.debug("No plugin found");
//            }
//        }
    }


    /**
     * @see toolbox.workspace.IPreferenced#savePrefs(nu.xom.Element)
     */
    public void savePrefs(Element prefs) throws Exception
    {
//        Element root = new Element(NODE_PLUGINHOST);
//        root.addAttribute(new Attribute(ATTR_CLASS, getClass().getName()));
//
//        for (Iterator i = frameMap_.keySet().iterator(); i.hasNext();)
//        {
//            JSmartInternalFrame sif = (JSmartInternalFrame) i.next();
//            IPlugin plugin = (IPlugin) frameMap_.get(sif);
//            Element frame = new Element(NODE_FRAME);
//            
//            frame.addAttribute(new Attribute(
//                ATTR_CLASS, plugin.getClass().getName()));
//            
//            sif.savePrefs(frame);
//            root.appendChild(frame);
//        }
//
//        XOMUtil.insertOrReplace(prefs, root);
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
                //SwingUtil.tile(desktop_);
            }
        }));

        windowMenu.add(new JSmartMenuItem(new AbstractAction("Cascade")
        {
            public void actionPerformed(ActionEvent e)
            {
                //SwingUtil.cascade(desktop_);
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