package toolbox.workspace.prefs;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.apache.log4j.Logger;

import toolbox.util.SwingUtil;
import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JHeaderPanel;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartSplitPane;
import toolbox.util.ui.tree.JSmartTree;
import toolbox.util.ui.tree.SmartTreeCellRenderer;

/**
 * The PreferencesDialog is spawned by the PluginWorkspace to allow the user 
 * to change various preferences supported by the program. Preferences are
 * grouped together on different panels and can be switched between be selecting
 * that panel's node on the configuration tree. New panels can easily be
 * added by implementing the PreferencesView interface and providing the 
 * necessary information.
 * 
 * @see toolbox.workspace.PluginWorkspace
 * @see toolbox.workspace.prefs.PreferencesManager
 * @see toolbox.workspace.prefs.PreferencesView
 */
public class PreferencesDialog extends JDialog
{
    private static final Logger logger_ =
        Logger.getLogger(PreferencesDialog.class);

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Preferences tree. Each node in the tree corresponds to a PreferencesView
     * which is displayed on the right side of the dialog.
     */
    private JSmartTree tree_;

    /**
     * Card panel used to switch out the currently visisble PreferencesView
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

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------

    public static void main(String[] args) throws Exception
    {
        // TODO: Remove me once i'm no longer useful for testing!
        
        JFrame frame = new JFrame();
        SwingUtil.centerWindow(frame);
        JDialog d = new PreferencesDialog(frame, new PreferencesManager());
        d.setVisible(true);
        SwingUtil.centerWindow(d);
    }

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Constructor for PreferencesView.
     *
     * @param parent Parent frame.
     * @param preferencesManager PreferencesManager from the workspace.
     */
    public PreferencesDialog(
        Frame parent,
        PreferencesManager preferencesManager)
    {
        super(parent, "Toolbox Preferences", true);
        preferencesManager_ = preferencesManager;
        buildView();
        pack();
        SwingUtil.setSizeAsPercentage(this, 20, -20); // May have to tweak later
        SwingUtil.centerWindow(parent, this);
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Adds a PreferenceView as a node in the tree and adds it to the stack of
     * cards in the card layout.
     * 
     * @param view Preferences view to add to the prefs tree.
     */
    public void registerView(PreferencesView view)
    {
        logger_.debug("Registering prefs view = " + view.getLabel());

        DefaultTreeModel model = (DefaultTreeModel) tree_.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        root.add(new DefaultMutableTreeNode(view));
        cardPanel_.add(view.getView(), view.getLabel());
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

        JSmartSplitPane splitter =
            new JSmartSplitPane(
                JSmartSplitPane.HORIZONTAL_SPLIT,
                buildTreePanel(),
                cardPanel_ = new JPanel(
                    cardLayout_ = new CardLayout()));

        view.add(BorderLayout.CENTER, splitter);
        view.add(BorderLayout.SOUTH, buildButtonPanel());

        PreferencesView[] prefs = preferencesManager_.getPreferences();
        for (int i = 0; i < prefs.length; registerView(prefs[i++]));

        SwingUtil.expandAll(tree_, true);
        tree_.setSelectionRow(1);
    }


    /**
     * Builds the ok/cancel/apply button panel.
     *-
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
    protected JPanel buildTreePanel()
    {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        DefaultTreeModel treeModel = new DefaultTreeModel(root);
        tree_ = new JSmartTree(root);
        tree_.setCellRenderer(new PrefsTreeCellRenderer());

        JHeaderPanel p =
            new JHeaderPanel(
                ImageCache.getIcon(ImageCache.IMAGE_CONFIG),
                "Preferences",
                null,
                new JScrollPane(tree_));

        tree_.addTreeSelectionListener(new PrefsTreeSelectionListener());
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
            PreferencesView[] prefs = preferencesManager_.getPreferences();
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
            PreferencesView[] prefs = preferencesManager_.getPreferences();
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
            PreferencesView[] prefs = preferencesManager_.getPreferences();
            for (int i = 0; i < prefs.length; prefs[i++].onCancel());
            dispose();
        }
    }

    //--------------------------------------------------------------------------
    // PrefsTreeCellRenderer
    //--------------------------------------------------------------------------

    /**
     * Custom cell renderer for the prefs tree. The user object in each non-root
     * node is expected to be an instance of PreferencesView. The label of the
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
                d.getUserObject() instanceof PreferencesView)
            {
                PreferencesView pp = (PreferencesView) d.getUserObject();
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
                node.getUserObject() instanceof PreferencesView)
            {
                PreferencesView pp = (PreferencesView) node.getUserObject();
                cardLayout_.show(cardPanel_, pp.getLabel());
            }
        }
    }
}