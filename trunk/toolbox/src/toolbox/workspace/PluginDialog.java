package toolbox.util.ui.plugin;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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
import toolbox.util.collections.ObjectComparator;
import toolbox.util.ui.JListPopupMenu;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.list.JSmartList;

/**
 * Dialog that allows user to add/remove/find plugins
 */
public class ManagePluginsDialog extends JDialog
{
    public static final Logger logger_ =
        Logger.getLogger(ManagePluginsDialog.class);
    
    /**
     * Button to remove a plugin from the workspace
     */
    private JButton removeButton_;
    
    /**
     * Button to add a plugin to the workspace
     */
    private JButton addButton_;
    
    /**
     * Workspace that is the parent of this dialog box
     */
    private PluginWorkspace parent_;
    
    /**
     * List of active (loaded) plugins in the workspace
     */
    private JList activeList_;
    
    /**
     * List of inactive plugins that can be added to the workspace 
     */
    private JList inactiveList_;
    
    /**
     * List model for the list of active plugins
     */
    private DefaultListModel activeModel_;
    
    /**
     * List model for the list of inactive plugins
     */
    private DefaultListModel inactiveModel_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates dialog to add/remove plugins from the workspace
     * 
     * @param parent Plugin workspace
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
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Builds the view of the GUI
     */
    protected void buildView()
    {
        Dimension prefSize = new Dimension(150,200);
        
        Border border = new CompoundBorder(
            BorderFactory.createEmptyBorder(0,10,10,10),                
            BorderFactory.createEtchedBorder());            
                
        // List of active plugins
        activeList_ = new JSmartList();
        activeModel_ = new DefaultListModel();
        activeList_.setModel(activeModel_);
        new JListPopupMenu(activeList_);

        JScrollPane activeScroller = new JScrollPane(activeList_);
        activeScroller.setPreferredSize(prefSize);            
        activeScroller.setBorder(border);
        
        // List of found/removed plugins
        inactiveList_ = new JSmartList();
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
        midButtonPanel.add(
            addButton_ = new JSmartButton(new AddNewPluginAction()), gbc);
        
        gbc.gridy++;
        gbc.anchor     = GridBagConstraints.NORTH;
        midButtonPanel.add(
            removeButton_ = new JSmartButton(new RemovePluginAction()), gbc);

        // Lists and buttons
        JPanel listPanel = new JPanel(new GridBagLayout());
        gbc.gridx      = 1;
        gbc.gridy      = 1;
        gbc.fill       = GridBagConstraints.BOTH;
        gbc.anchor     = GridBagConstraints.SOUTH;
        gbc.gridheight = 1;
        gbc.gridwidth  = 1;
        gbc.insets     = new Insets(0,0,0,0);
        JLabel inactiveLabel = new JSmartLabel("Inactive Plugins");
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
        JLabel activeLabel = new JSmartLabel("Active Plugins");
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
        buttonPanel.add(new JSmartButton(new FindPluginsAction()));            
        buttonPanel.add(new JSmartButton(new CloseAction()));

        // Glue everything together in the root pane
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(BorderLayout.CENTER, listPanel);
        getContentPane().add(BorderLayout.SOUTH, buttonPanel);
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
        
        removeButton_.setEnabled(activeModel_.size() > 0);
        addButton_.setEnabled(inactiveModel_.size() > 0);
        activeList_.setSelectedIndex(0);
    }
    
    /**
     * Populates the inactive plugins list based on currently available
     * plugins visible on the classpath
     */
    protected void populateInactive()
    {
        inactiveModel_.clear();
        FindClass fc = new FindClass();
        FindClassResult[] candidatePlugins = new FindClassResult[0];
        List legitPlugins = new ArrayList();
                
        try
        {
            // Find classes that end in "Plugin"
            candidatePlugins = fc.findClass("Plugin$", true);
        }
        catch (RESyntaxException mpe)
        {
            ExceptionUtil.handleUI(mpe, logger_);
        }
        catch (IOException ioe)
        {
            ExceptionUtil.handleUI(ioe, logger_);
        }

        logger_.debug("Results: " + candidatePlugins.length);

        for (int i=0; i<candidatePlugins.length; i++)
        {
            String clazz     = candidatePlugins[i].getClassFQN();
            String clazzOnly = ClassUtil.stripPackage(clazz);
                     
            logger_.debug(clazzOnly + " : Inspecting...");

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
            for (int j=0, n=legitPlugins.size(); j<n; j++)
            {
                PluginMeta pm = (PluginMeta) legitPlugins.get(j);
                
                if (pm.getClassName().equals(clazz))
                {
                    logger_.debug(clazzOnly + " : Excluding duplicate class");
                    skip = true;
                    break;
                }
            }

            if (!skip)
            {
                logger_.debug(clazzOnly + " : Passed already loaded check");
                    
                Object plugin = null;

                // Excluse plugins that can't be instantiated for whatever 
                // reason
                try
                {                            
                    plugin = Class.forName(clazz).newInstance();
                    logger_.debug(clazzOnly + " : Passed instantiation check");
                }
                catch (Throwable t)
                {
                    logger_.debug(clazzOnly + " : Failed newInstance() : " +  
                        t.getMessage());
                        
                    skip = true;
                }
                
                // Make sure plugin class implements the IPlugin interface
                if (!skip)
                {
                    if (plugin instanceof IPlugin)
                    {
                        logger_.debug(clazzOnly + " : We have a plugin!!!");
                        legitPlugins.add(new PluginMeta((IPlugin) plugin));
                    }
                }
            }
        }
    
        Collections.sort(legitPlugins, new ObjectComparator("name"));

        for (int i=0; i<legitPlugins.size(); i++)
            inactiveModel_.addElement(legitPlugins.get(i));
        
        removeButton_.setEnabled(activeModel_.size() > 0);
        addButton_.setEnabled(inactiveModel_.size() > 0);
    }
    
    //--------------------------------------------------------------------------
    // Inner Classes 
    //--------------------------------------------------------------------------
    
    /**
     * PluginMeta info used to populate the active/inactive lists
     */
    public class PluginMeta
    {
        private String className_;
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
     * Adds a plugin - moves the plugin from the inactive list to the active 
     * list
     */
    class AddNewPluginAction extends AbstractAction
    {
        AddNewPluginAction()
        {
            super("Add Plugin >>");
            putValue(Action.MNEMONIC_KEY, new Integer('A'));                
        }
           
        public void actionPerformed(ActionEvent e)
        {
            int sizeBefore = inactiveModel_.size();
            int[] selectedIdx = inactiveList_.getSelectedIndices();
            Object[] selected = inactiveList_.getSelectedValues();
            
            for (int i=0; i<selected.length; i++)
            {
                try
                {
                    parent_.registerPlugin(
                        ((PluginMeta)selected[i]).getClassName());
                        
                    inactiveModel_.removeElement(selected[i]);                        
                }
                catch (Exception ex)
                {
                    ExceptionUtil.handleUI(ex, logger_);
                }
            }
            
            populateActive();

            // Inactive list selection
            if (selectedIdx[selected.length-1] == sizeBefore - 1)
                inactiveList_.setSelectedIndex(sizeBefore - selected.length -1);
            else
                inactiveList_.setSelectedIndex(selectedIdx[0]);
            
            // Active list selection
            int[] selectedActive = new int[selected.length];
            
            for (int i=0, n = activeModel_.size() - selected.length; 
                i<selected.length; 
                    i++, n++)
            {
                selectedActive[i] = n;                
            }
            
            activeList_.setSelectedIndices(selectedActive);
        }
    }
    
    /**
     * Deactivates/removes a plugin and moves the plugin from the active list 
     * to the inactive list
     */
    class RemovePluginAction extends AbstractAction
    {
        RemovePluginAction()
        {
            super("<< Remove Plugin");
            putValue(Action.MNEMONIC_KEY, new Integer('R'));
        }
           
        public void actionPerformed(ActionEvent e)
        {
            int sizeBefore = activeModel_.size();
            int[] selectedIdx = activeList_.getSelectedIndices();
            Object[] selected = activeList_.getSelectedValues(); 
            
            try
            {
                // Deregister selected plugins
                for (int i=0; i<selected.length; i++)
                {
                    parent_.deregisterPlugin(
                        ((PluginMeta)selected[i]).getClassName(), true);
                        
                    inactiveModel_.addElement(selected[i]);                        
                }
            }
            catch (Exception ex)
            {
                ExceptionUtil.handleUI(ex, logger_);
            }
            
            populateActive();

            // Active list selection
            if (selectedIdx[selected.length-1] == sizeBefore-1)
                activeList_.setSelectedIndex(sizeBefore - selected.length - 1);
            else
                activeList_.setSelectedIndex(selectedIdx[0]);
                    
            // Inactive list selection
            int[] selectedInactive = new int[selected.length];
            
            for (int i=0, n = inactiveModel_.size() - selected.length; 
                i<selected.length; 
                    i++, n++)
            {
                selectedInactive[i] = n;                
            }
            
            inactiveList_.setSelectedIndices(selectedInactive);
        }
    }
    
    /**
     * Dismisses the dialog box
     */
    class CloseAction extends AbstractAction
    {
        CloseAction()
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
    class FindPluginsAction extends WorkspaceAction
    {
        FindPluginsAction()
        {
            super("Find Plugins", 
                  true, 
                  ManagePluginsDialog.this, 
                  parent_.getStatusBar());
                
            putValue(Action.MNEMONIC_KEY, new Integer('F')); 
        }
        
        public void runAction(ActionEvent e)
        {
            populateInactive(); 
        }
    }
}