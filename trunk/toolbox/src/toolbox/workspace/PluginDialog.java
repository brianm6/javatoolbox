package toolbox.util.ui.plugin;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import org.apache.log4j.Logger;
import org.apache.regexp.RESyntaxException;

import toolbox.findclass.FindClass;
import toolbox.findclass.FindClassResult;
import toolbox.util.ClassUtil;
import toolbox.util.ExceptionUtil;
import toolbox.util.SwingUtil;
import toolbox.util.ui.JListPopupMenu;

/**
 * Dialog that allows user to add/remove/find plugins
 */
public class ManagePluginsDialog extends JDialog
{
    /** Logger **/
    public static final Logger logger_ =
        Logger.getLogger(ManagePluginsDialog.class);
        
    private PluginWorkspace parent_;
    
    private JList activeList_;
    private JList inactiveList_;
    
    private DefaultListModel activeModel_;
    private DefaultListModel inactiveModel_;

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates dialog to add/remove plugins from the workspace
     * 
     * @param  parent  Plugin workspace
     */    
    protected ManagePluginsDialog(PluginWorkspace parent)
    {
        super(parent, "Manage Plugins", false);
        parent_ = parent;
        buildView();
        pack();
        SwingUtil.centerWindow(parent, this);
        populateActive();
    }
    
    //--------------------------------------------------------------------------
    //  Private
    //--------------------------------------------------------------------------
    
    protected void buildView()
    {
        Dimension prefSize = new Dimension(150,200);
        
        Border border = new CompoundBorder(
            BorderFactory.createEmptyBorder(0,10,10,10),                
            BorderFactory.createEtchedBorder());            
                
        // List of active plugins
        activeList_ = new JList();
        activeModel_ = new DefaultListModel();
        activeList_.setModel(activeModel_);
        new JListPopupMenu(activeList_);

        JScrollPane activeScroller = new JScrollPane(activeList_);
        activeScroller.setPreferredSize(prefSize);            
        activeScroller.setBorder(border);
        
        // List of found/removed plugins
        inactiveList_ = new JList();
        inactiveModel_ = new DefaultListModel();
        inactiveList_.setModel(inactiveModel_);
        new JListPopupMenu(inactiveList_);

        JScrollPane inactiveScroller = new JScrollPane(inactiveList_);
        inactiveScroller.setPreferredSize(prefSize);                        
        inactiveScroller.setBorder(border);
                    
        // Add/Remove buttons to move plugins between the lists
        JPanel midButtonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
       
        gbc.gridx      = 1;
        gbc.gridy      = 1;
        gbc.fill       = GridBagConstraints.HORIZONTAL;
        gbc.weightx    = 1;
        gbc.weighty    = 1;
        gbc.anchor     = GridBagConstraints.SOUTH;
        gbc.insets     = new Insets(5,0,5,0);
        midButtonPanel.add(new JButton(new AddNewPluginAction()), gbc);
        
        gbc.gridy++;
        gbc.anchor     = GridBagConstraints.NORTH;
        midButtonPanel.add(new JButton(new RemovePluginAction()), gbc);

        // Lists and buttons
        JPanel listPanel = new JPanel(new GridBagLayout());
        gbc.gridx      = 1;
        gbc.gridy      = 1;
        gbc.fill       = GridBagConstraints.BOTH;
        gbc.anchor     = GridBagConstraints.SOUTH;
        gbc.gridheight = 1;
        gbc.gridwidth  = 1;
        gbc.insets     = new Insets(0,0,0,0);
        JLabel inactiveLabel = new JLabel("Inactive Plugins");
        inactiveLabel.setHorizontalAlignment(SwingConstants.CENTER);
        inactiveLabel.setVerticalAlignment(SwingConstants.BOTTOM);
        listPanel.add(inactiveLabel, gbc);
        
        gbc.gridx  = 1; 
        gbc.gridy  = 2;
        gbc.insets = new Insets(5,0,5,0);            
        gbc.anchor = GridBagConstraints.CENTER;            
        listPanel.add(inactiveScroller, gbc);  
        
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 0.5;
        listPanel.add(midButtonPanel, gbc);    

        gbc.gridx      = 3;              
        gbc.gridy      = 1;
        gbc.weightx    = 1.0;        
        gbc.anchor     = GridBagConstraints.CENTER;
        gbc.insets     = new Insets(0,0,0,0);            
        JLabel activeLabel = new JLabel("Active Plugins");
        activeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        activeLabel.setVerticalAlignment(SwingConstants.BOTTOM);
        listPanel.add(activeLabel, gbc);

        gbc.gridx  = 3;
        gbc.gridy  = 2;
        gbc.anchor = GridBagConstraints.NORTH;                        
        gbc.insets = new Insets(5,0,5,0);            
        listPanel.add(activeScroller, gbc);
        
        // Find/Close Buttons on bottom
        JPanel buttonPanel = new JPanel(new FlowLayout());        
        buttonPanel.add(new JButton(new FindPluginsAction()));            
        buttonPanel.add(new JButton(new CloseAction()));

        // Glue everything together in the root pane
        getRootPane().setLayout(new BorderLayout());
        getRootPane().add(BorderLayout.CENTER, listPanel);
        getRootPane().add(BorderLayout.SOUTH, buttonPanel);
    }

    /**
     * Populates the active plugins list based on currently loaded plugins
     */    
    protected void populateActive()
    {
        activeModel_.clear();
        
        for (Iterator i = parent_.getPlugins().values().iterator();i.hasNext();)
        {
            IPlugin plugin = (IPlugin)i.next();
            PluginMeta meta = new PluginMeta(plugin);
            activeModel_.addElement(meta);
        }
    }
    
    /**
     * Populates the inactive plugins list based on currently available
     * plugins visible on the classpath
     */
    protected void populateInactive()
    {
        inactiveModel_.clear();
        FindClass fc = new FindClass();
        FindClassResult[] foundPlugins = new FindClassResult[0];
        
        try
        {
            // Find classes that end in "Plugin"
            foundPlugins = fc.findClass("Plugin$", true);
        }
        catch (RESyntaxException mpe)
        {
            ExceptionUtil.handleUI(mpe, logger_);
        }
        catch (IOException ioe)
        {
            ExceptionUtil.handleUI(ioe, logger_);
        }

        logger_.debug("Results: " + foundPlugins.length);

        for (int i=0; i<foundPlugins.length; i++)
        {
            String clazz     = foundPlugins[i].getClassFQN();
            String clazzName = ClassUtil.stripPackage(clazz);
                     
            logger_.debug("Inspecting " + clazzName + "...");

            boolean skip = false;
                
            // Exclude the plugins that are already loaded                
            for (Iterator it = parent_.getPlugins().values().iterator(); 
                 it.hasNext(); )
            {
                String pluginClass = it.next().getClass().getName();
                
                if (pluginClass.equals(clazz))
                {
                    skip = true;
                    break;
                }
            }
            
            // Exclude plugins that already occur in the inactive list
            for (Enumeration e=inactiveModel_.elements(); e.hasMoreElements();)
            {
                PluginMeta pm = (PluginMeta) e.nextElement();
                
                if (pm.getClassName().equals(clazz))
                {
                    skip = true;
                    break;
                }
            }

            if (!skip)
            {
                logger_.debug("Passed already loaded check : " + clazzName);
                    
                Object plugin = null;

                // Excluse plugins that can't be instantiated
                // for whatever reason
                try
                {                            
                    plugin = Class.forName(clazz).newInstance();
                    logger_.debug("Passed instantiation check: " + clazzName);
                }
                catch (Throwable t)
                {
                    logger_.debug(
                        "Failed newInstance() : " + clazzName + " " + t);
                    skip = true;
                }
                
                // Make sure plugin class implements 
                // the IPlugin interface
                if (!skip)
                {
                    if (plugin instanceof IPlugin)
                    {
                        logger_.debug("Passed instanceof check: " + clazzName);
                        
                        inactiveModel_.addElement(
                            (new PluginMeta((IPlugin)plugin)));
                    }
                }
            }
        }
    }
    
    /**
     * PluginMeta info used to populate the active/inactive lists
     */
    class PluginMeta
    {
        private String className_;
        private String pluginName_;
        private IPlugin plugin_;

        public PluginMeta(String pluginClass)
        {
            className_ = pluginClass;
        }
        
        public PluginMeta(IPlugin plugin)
        {
            plugin_ = plugin;
        }
        
        public String getClassName()
        {
            if (className_ == null && plugin_ != null)
                className_ = plugin_.getClass().getName();
                
            return className_;
        }
        
        public String getName()
        {
            return getPlugin().getName();    
        }
        
        public IPlugin getPlugin()
        {
            try
            {
                if (plugin_ == null)
                {
                    if (className_ != null)
                    {
                        plugin_ = (IPlugin) 
                            Class.forName(className_).newInstance();    
                    }
                    else
                        throw new IllegalArgumentException(
                            "Classname not provided"); 
                }
            }
            catch (Exception e)
            {
                ExceptionUtil.handleUI(e, logger_);    
            }
            
            return plugin_;
        }
        
        public String toString()
        {
            return getName();
        }
    }
    
    //----------------------------------------------------------------------
    //  Actions
    //----------------------------------------------------------------------
    
    /**
     * Adds a plugin - moves the plugin from the inactive list to the 
     * active list
     */
    class AddNewPluginAction extends AbstractAction
    {
        public AddNewPluginAction()
        {
            super("Add Plugin >>");
            putValue(Action.MNEMONIC_KEY, new Integer('A'));                
        }
           
        public void actionPerformed(ActionEvent e)
        {
            Object[] meta = inactiveList_.getSelectedValues();
            
            for (int i=0; i<meta.length; i++)
            {
                try
                {
                    parent_.registerPlugin(
                        ((PluginMeta)meta[i]).getClassName());
                }
                catch (Exception ex)
                {
                    ExceptionUtil.handleUI(ex, logger_);
                }
                
                inactiveModel_.removeElement(meta[i]);
            }
            
            populateActive();
        }
    }
    
    /**
     * Deactivates/removes a plugin and moves the plugin from the active
     * list to the inactive list
     */
    class RemovePluginAction extends AbstractAction
    {
        public RemovePluginAction()
        {
            super("<< Remove Plugin");
            putValue(Action.MNEMONIC_KEY, new Integer('R'));
        }
           
        public void actionPerformed(ActionEvent e)
        {
            try
            {
                Object meta[] = activeList_.getSelectedValues(); 
                
                for (int i=0; i<meta.length; i++)
                {
                    // Get the class name for the selected plugin name
                    parent_.deregisterPlugin(
                        ((PluginMeta)meta[i]).getClassName());
                        
                    inactiveModel_.addElement(meta[i]);
                }
            }
            catch (Exception ex)
            {
                ExceptionUtil.handleUI(ex, logger_);
            }
            finally
            {
                populateActive();
            }
        }
    }
    
    /**
     * Dismisses the dialog box
     */
    class CloseAction extends AbstractAction
    {
        public CloseAction()
        {
            super("Close");
            putValue(Action.MNEMONIC_KEY, new Integer('C'));
        }
        
        public void actionPerformed(ActionEvent e)
        {
            dispose();
        }
    }
    
    /**
     * Finds plugins on the classpath and populates the inactive plugins list
     */
    class FindPluginsAction extends AbstractAction
    {
        public FindPluginsAction()
        {
            super("Find Plugins");
            putValue(Action.MNEMONIC_KEY, new Integer('F')); 
        }
        
        public void actionPerformed(ActionEvent e)
        {
            populateInactive(); 
        }
    }
}
    
