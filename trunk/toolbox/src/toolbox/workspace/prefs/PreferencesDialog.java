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
 * Workspace preferences dialog box.
 */
public class PreferencesDialog extends JDialog
{
    private static final Logger logger_ =
        Logger.getLogger(PreferencesDialog.class);

    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------

    private static final String ACTION_OK     = "OK";
    private static final String ACTION_CANCEL = "Cancel";


    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Preferences tree.
     */
    private JSmartTree tree_;
    
    private JPanel cardPanel_;
    private CardLayout cardLayout_;

    private PreferencesManager preferencesManager_;

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args) throws Exception
    {
        //LookAndFeelUtil.setPreferredLAF();
        
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
     */
    public PreferencesDialog(
        Frame parent, 
        PreferencesManager preferencesManager)
    {
        super(parent, "Toolbox Preferences", true);
        preferencesManager_ = preferencesManager;
        buildView();
        pack();
        SwingUtil.centerWindow(parent, this);
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    public void registerView(PreferencesView view)
    {
        logger_.debug("Registering prefs view = " + view.getLabel());
        
        DefaultTreeModel model = (DefaultTreeModel) tree_.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        root.add(new DefaultMutableTreeNode(view));
        
        // Since the view is created by the PlugWorkspace during initialization,
        // the look and feel may have changed so we have to update it manually.
        
        // SwingUtilities.updateComponentTreeUI(view.getView());
        
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
        //setContentPane(view);
        
        PreferencesView[] prefs = preferencesManager_.getPreferences();
        for (int i = 0; i < prefs.length; registerView(prefs[i++]));
        
        SwingUtil.expandAll(tree_, true);
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
            System.out.println("ok!");
            PreferencesView[] prefs = preferencesManager_.getPreferences();
            for (int i = 0; i < prefs.length; prefs[i++].onOK());
            dispose();
        }
    }
    
    //--------------------------------------------------------------------------
    // ApplyAction
    //--------------------------------------------------------------------------
    
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
            System.out.println("applying!");
            PreferencesView[] prefs = preferencesManager_.getPreferences();
            for (int i = 0; i < prefs.length; prefs[i++].onApply());
        }
    }

    //--------------------------------------------------------------------------
    // CancelAction
    //--------------------------------------------------------------------------
    
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
            System.out.println("cancel");
            PreferencesView[] prefs = preferencesManager_.getPreferences();
            for (int i = 0; i < prefs.length; prefs[i++].onCancel());
            dispose();
        }
    }
    
    //--------------------------------------------------------------------------
    // PrefsTreeCellRenderer
    //--------------------------------------------------------------------------
    
    class PrefsTreeCellRenderer extends SmartTreeCellRenderer
    {
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
    
    class PrefsTreeSelectionListener implements TreeSelectionListener
    {
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