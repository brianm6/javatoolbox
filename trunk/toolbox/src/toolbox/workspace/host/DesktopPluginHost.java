package toolbox.workspace.host;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.commons.lang.ClassUtils;
import org.apache.log4j.Logger;

import toolbox.util.ExceptionUtil;
import toolbox.util.XOMUtil;
import toolbox.util.ui.JSmartInternalFrame;
import toolbox.workspace.IPlugin;
import toolbox.workspace.IPreferenced;
import toolbox.workspace.PluginWorkspace;

/**
 * Plugin host that associates each plugin with a JInternalFrame. Multiple
 * plugins are accessible in an MDI manner because of the parent JDesktopPane. 
 */
public class DesktopPluginHost extends AbstractPluginHost 
    implements IPreferenced
{
    private static final Logger logger_ = 
        Logger.getLogger(DesktopPluginHost.class);
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Desktop.
     */
    private JDesktopPane desktop_;
    
    /**
     * Maps IPlugin -> JInternalFrame.
     */
    private BidiMap pluginToFrameMap_;
    
    /**
     * Maps JInternalFrame -> IPlugin. 
     */
    private BidiMap frameToPluginMap_;

    /**
     * Parent workspace.
     */
    private PluginWorkspace workspace_;

    
    //--------------------------------------------------------------------------
    // PluginHost Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.workspace.host.PluginHost#startup(java.util.Map)
     */
    public void startup(Map props)
    {
        super.startup(props);

        workspace_ = (PluginWorkspace) props.get(PluginWorkspace.KEY_WORKSPACE);
        desktop_ = new JDesktopPane();
        pluginToFrameMap_ = new DualHashBidiMap();
        frameToPluginMap_ = pluginToFrameMap_.inverseBidiMap();
        
        /*
        SwingUtilities.invokeLater(new Runnable() 
        {
            public void run() 
            {
                SwingUtil.cascade(desktop_);
                JInternalFrame[] jifs = desktop_.getAllFrames();
                
                for (int i = 0; i < jifs.length; i++)
                {
                    jifs[i].pack();
                    
                    Dimension d = jifs[i].getSize();
                    
                    if (d.width > desktop_.getWidth())
                        d.width = ((int) (desktop_.getWidth() * 0.9));
                                
                    if (d.height > desktop_.getHeight())
                        d.height = ((int) (desktop_.getHeight() * 0.9));

                    jifs[i].setSize(d);
                }
            }
        });
        */
    }

    
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
        JComponent c = plugin.getComponent();
        frame.getContentPane().add(c, BorderLayout.CENTER);
        desktop_.add(frame);
        
        if (frame.getSize().equals(new Dimension(0, 0)))
            frame.pack();
        
        frame.setVisible(true);
        frame.moveToFront();
        pluginToFrameMap_.put(plugin, frame);

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
                IPlugin plugin = (IPlugin) frameToPluginMap_.get(jif);
                
                //
                // Delegate removal of the plugin to the workspace since we
                // have no visibility into all the other things that need to 
                // happend (XML prefs, etc) that need to be saved at this
                // level.
                //
                
                
                try
                {
                    workspace_.deregisterPlugin(
                        plugin.getClass().getName(), true);
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
        desktop_.remove((JComponent) pluginToFrameMap_.get(plugin));
        pluginToFrameMap_.remove(plugin);
        
        super.exportPlugin(plugin);
    }    
    
    
    /**
     * @see toolbox.workspace.host.PluginHost#getComponent()
     */
    public JComponent getComponent()
    {
        return desktop_;
    }
    
    
    /**
     * @see toolbox.workspace.host.PluginHost#setSelectedPlugin(
     *      toolbox.workspace.IPlugin)
     */
    public void setSelectedPlugin(IPlugin plugin)
    {
        JInternalFrame jif = (JInternalFrame) pluginToFrameMap_.get(plugin);
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
        return (IPlugin) frameToPluginMap_.get(desktop_.getSelectedFrame());
    }
    
    
    /**
     * @see toolbox.workspace.host.PluginHost#getName()
     */
    public String getName()
    {
        return "Desktop";
    }
    
    
    /**
     * @see toolbox.workspace.host.PluginHost#shutdown()
     */
    public void shutdown()
    {
        desktop_.removeAll();
        desktop_ = null;
        
        pluginToFrameMap_.clear();
        pluginToFrameMap_ = null;
        
        super.shutdown();
    }
    
    //--------------------------------------------------------------------------
    // IPreferenced Interface
    //--------------------------------------------------------------------------
    
    
    /**
     * @see toolbox.workspace.IPreferenced#applyPrefs(nu.xom.Element)
     */
    public void applyPrefs(Element prefs) throws Exception
    {
        logger_.debug("Applying DesktopPluginHost preferences");
        
        Map class2Frame = new HashMap();
        
        //
        // Associated a plugin class with an internal frame
        //
        for (Iterator i = pluginToFrameMap_.keySet().iterator(); i.hasNext();)
        {
            IPlugin plugin = (IPlugin) i.next();
            
            class2Frame.put(
                plugin.getClass().getName(), 
                pluginToFrameMap_.get(plugin));
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
    public void savePrefs(Element prefs) throws Exception
    {
        Element root = new Element(NODE_PLUGINHOST);
        root.addAttribute(new Attribute(ATTR_CLASS, getClass().getName()));

        for (Iterator i = frameToPluginMap_.keySet().iterator(); i.hasNext();)
        {
            JSmartInternalFrame sif = (JSmartInternalFrame) i.next();
            IPlugin plugin = (IPlugin) frameToPluginMap_.get(sif);
            Element frame = new Element(NODE_FRAME);
            
            frame.addAttribute(new Attribute(
                ATTR_CLASS, plugin.getClass().getName()));
            
            sif.savePrefs(frame);
            root.appendChild(frame);
        }

        XOMUtil.insertOrReplace(prefs, root);
    }
}