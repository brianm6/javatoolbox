package toolbox.workspace.prefs;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import com.l2fprod.common.swing.JButtonBar;

import org.apache.log4j.Logger;

import toolbox.util.SwingUtil;
import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JButtonGroup;
import toolbox.util.ui.JHeaderPanel;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartDialog;
import toolbox.util.ui.JSmartSplitPane;
import toolbox.util.ui.JSmartToggleButton;
import toolbox.util.ui.tree.SmartTreeCellRenderer;
import toolbox.workspace.IPlugin;
import toolbox.workspace.PluginWorkspace;

/**
 * The PreferencesDialog is invoked by the PluginWorkspace to allow the user 
 * to change various preferences supported by the program. Preferences are
 * grouped together on different panels and can be switched between be selecting
 * that panel's node on the configuration tree. New panels can easily be
 * added by implementing the Preferences interface and providing the 
 * necessary information.
 * 
 * @see toolbox.workspace.PluginWorkspace
 * @see toolbox.workspace.prefs.PreferencesManager
 * @see toolbox.workspace.prefs.Preferences
 */
public class PreferencesDialog2 extends JSmartDialog
{
    private static final Logger logger_ =
        Logger.getLogger(PreferencesDialog2.class);

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    private JButtonBar buttonBar_;
    private JButtonGroup buttonGroup_;
    
    /**
     * Card panel used to switch out the currently visisble Preferences
     * based on which node in the tree is selected.
     */
    private JPanel cardPanel_;

    /**
     * Layout for switching out the PreferencesViews.
     */
    private CardLayout cardLayout_;

    /**
     * Source of the PreferencesViews.
     */
    private PreferencesManager preferencesManager_;

    /**
     * Parent workspace.
     */
    private final PluginWorkspace workspace_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a PreferencesDialog.
     *
     * @param parent Parent frame.
     * @param preferencesManager PreferencesManager from the workspace.
     */
    public PreferencesDialog2(
        Frame parent,
        PreferencesManager preferencesManager)
    {
        super(parent, "Toolbox Preferences", true);
        workspace_ = (PluginWorkspace) parent;
        preferencesManager_ = preferencesManager;
        buildView();
        pack();
        SwingUtil.setSizeAsDesktopPercentage(this, 40, 40); // May have to tweak later
        SwingUtil.centerWindow(parent, this);
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Adds a PreferenceView as a node in the tree and adds it to the stack of
     * cards in the card layout.
     * 
     * @param prefs Preferences view to add to the prefs tree.
     */
    public void registerView(Preferences prefs)
    {
        logger_.debug("Registering prefs view = " + prefs.getLabel());

        JSmartToggleButton button = 
            new JSmartToggleButton(new ButtonAction(prefs.getLabel()));
        
        button.putClientProperty("prefs", prefs);
        buttonGroup_.add(button);
        buttonBar_.add(button);
        cardPanel_.add(prefs.getView(), prefs.getLabel());
    }

    
    class ButtonAction extends AbstractAction
    {
        ButtonAction(String text)
        {
            super(text);
        }
        
        public void actionPerformed(ActionEvent e)
        {
            JSmartToggleButton button = (JSmartToggleButton) e.getSource();
            Preferences prefs = (Preferences) button.getClientProperty("prefs");
            cardLayout_.show(cardPanel_, prefs.getLabel());
        }
    }
    
    //--------------------------------------------------------------------------
    // Build View
    //--------------------------------------------------------------------------

    /**
     * Constructs the user interface.
     */
    protected void buildView()
    {
        Container view = getContentPane();
        view.setLayout(new BorderLayout());
        view.add(buildButtonBar(), BorderLayout.WEST);
        view.add(buildCardPanel(), BorderLayout.CENTER);
        view.add(buildButtonPanel(), BorderLayout.SOUTH);

        buttonGroup_ = new JButtonGroup();
        
        // Non-plugin based preferences
        Preferences[] prefs = preferencesManager_.getPreferences();
        for (int i = 0; i < prefs.length; registerView(prefs[i++]));

        // Plugin based preferences
        IPlugin[] plugins = workspace_.getPluginHost().getPlugins();
        
        for (int i = 0; i < plugins.length; i++)
        {
            IPlugin plugin = plugins[i];
            Preferences p = plugin.getPreferences();
            
            if (p != null)
            {
                logger_.debug("Registering " + plugin.getPluginName());
                registerView(p);
            }
        } 
    }


    /**
     * 
     */
    protected JComponent buildCardPanel()
    {
        cardLayout_ = new CardLayout();
        cardPanel_ = new JPanel(cardLayout_);
        cardPanel_.setBorder(new EmptyBorder(10,10,10,10));
        return cardPanel_;
    }

    
    /**
     * Builds the ok/cancel/apply button panel.
     *
     * @return JPanel
     */
    protected JPanel buildButtonPanel()
    {
        JPanel p = new JPanel(new FlowLayout());
        p.add(new JSmartButton(new OKAction()));
        p.add(new JSmartButton(new ApplyAction()));
        p.add(new JSmartButton(new CancelAction()));
        return p;
    }


    /**
     * Builds the tree panel with the preference views as children.
     *
     * @return JPanel
     */
    protected JComponent buildButtonBar()
    {
        buttonBar_ = new JButtonBar(SwingConstants.VERTICAL);
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(10,10,10,10));
        p.add(buttonBar_, BorderLayout.CENTER);
        return p;
    }

    //--------------------------------------------------------------------------
    // OKAction
    //--------------------------------------------------------------------------

    /**
     * Propagates the OK event to all the views and dismisses the dialog box.
     */
    class OKAction extends AbstractAction
    {
        public OKAction()
        {
            super("OK");
        }

        
        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            Preferences[] prefs = preferencesManager_.getPreferences();
            for (int i = 0; i < prefs.length; prefs[i++].onOK());
            dispose();
        }
    }

    //--------------------------------------------------------------------------
    // ApplyAction
    //--------------------------------------------------------------------------

    /**
     * Propagates the Apply event to all the views.
     */
    class ApplyAction extends AbstractAction
    {
        public ApplyAction()
        {
            super("Apply");
        }

        
        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            Preferences[] prefs = preferencesManager_.getPreferences();
            for (int i = 0; i < prefs.length; prefs[i++].onApply());
        }
    }

    //--------------------------------------------------------------------------
    // CancelAction
    //--------------------------------------------------------------------------

    /**
     * Propagates the Cancel event to all the views and disposes of the 
     * dialog box.
     */
    class CancelAction extends AbstractAction
    {
        public CancelAction()
        {
            super("Cancel");
        }

        
        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            Preferences[] prefs = preferencesManager_.getPreferences();
            for (int i = 0; i < prefs.length; prefs[i++].onCancel());
            dispose();
        }
    }

    //--------------------------------------------------------------------------
    // PrefsTreeCellRenderer
    //--------------------------------------------------------------------------

    /**
     * Custom cell renderer for the prefs tree. The user object in each non-root
     * node is expected to be an instance of Preferences. The label of the
     * node is set to the name of the view.
     */
    class PrefsTreeCellRenderer extends SmartTreeCellRenderer
    {
        /**
         * @see javax.swing.tree.TreeCellRenderer#getTreeCellRendererComponent(
         *      javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, 
         *      int, boolean)
         */
        public Component getTreeCellRendererComponent(
            JTree tree,
            Object value,
            boolean selected,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus)
        {
            super.getTreeCellRendererComponent(
                tree,
                value,
                selected,
                expanded,
                leaf,
                row,
                hasFocus);

            DefaultMutableTreeNode d = (DefaultMutableTreeNode) value;

            if (d.getUserObject() != null &&
                d.getUserObject() instanceof Preferences)
            {
                Preferences pp = (Preferences) d.getUserObject();
                setText(pp.getLabel());
            }

            return this;
        }
    }

    //--------------------------------------------------------------------------
    // PrefsTreeSelectionListener
    //--------------------------------------------------------------------------

    /**
     * Activates PreferenceViews in the card stack based on the currently
     * selected node.
     */
    class PrefsTreeSelectionListener implements TreeSelectionListener
    {
        /**
         * @see javax.swing.event.TreeSelectionListener#valueChanged(
         *      javax.swing.event.TreeSelectionEvent)
         */
        public void valueChanged(TreeSelectionEvent e)
        {
            DefaultMutableTreeNode node =
                (DefaultMutableTreeNode) e.getPath().getLastPathComponent();

            if (node.getUserObject() != null &&
                node.getUserObject() instanceof Preferences)
            {
                Preferences pp = (Preferences) node.getUserObject();
                cardLayout_.show(cardPanel_, pp.getLabel());
            }
        }
    }
}