package toolbox.workspace.prefs;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
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
import toolbox.util.ui.action.DisposeAction;
import toolbox.util.ui.plaf.LookAndFeelUtil;
import toolbox.util.ui.tree.JSmartTree;
import toolbox.util.ui.tree.SmartTreeCellRenderer;

/**
 * Workspace preferences dialog box.
 */
public class PreferencesDialog extends JDialog implements ActionListener
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
    
    JPanel cardPanel_;
    CardLayout cardLayout_;

    
    public static void main(String[] args) throws Exception
    {
        LookAndFeelUtil.setPreferredLAF();
        
        JFrame frame = new JFrame();
        SwingUtil.centerWindow(frame);
        JDialog d = new PreferencesDialog(frame);
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
    public PreferencesDialog(Frame parent)
    {
        super(parent, "Toolbox Preferences", true);
        buildView();
        pack();
        SwingUtil.centerWindow(parent, this);
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    public void registerPane(PreferencesView pane)
    {
        DefaultTreeModel model = (DefaultTreeModel) tree_.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        root.add(new DefaultMutableTreeNode(pane));
        
        cardPanel_.add(pane.getView(), pane.getLabel());
    }
    
    //--------------------------------------------------------------------------
    // Build View
    //--------------------------------------------------------------------------

    /**
     * Constructs the user interface.
     */
    protected void buildView()
    {
        JPanel view = new JPanel(new BorderLayout());
        
        JSmartSplitPane splitter = 
            new JSmartSplitPane(
                JSmartSplitPane.HORIZONTAL_SPLIT,
                buildTreePanel(),
                buildCardPanel());
        
        view.add(BorderLayout.CENTER, splitter);
        view.add(BorderLayout.SOUTH, buildButtonPanel());
        
        registerPane(new ProxyView());
        
        setContentPane(view);
        
        SwingUtil.expandAll(tree_, true);
    }

    
    protected JPanel buildCardPanel()
    {
        cardPanel_ = new JPanel(cardLayout_ = new CardLayout());
        return cardPanel_;
    }
    
    
    /**
     * Builds the ok/cancel button panel.
     * 
     * @return JPanel
     */
    protected JPanel buildButtonPanel()
    {
        // Build and wire button panel
        JPanel p = new JPanel(new FlowLayout());
        JButton okButton = new JSmartButton(ACTION_OK);
        okButton.setActionCommand(ACTION_OK);
        okButton.addActionListener(this);
        
        p.add(okButton);
        p.add(new JSmartButton(new ApplyAction()));
        p.add(new JSmartButton(new DisposeAction("Cancel", this)));
        return p;
    }
    
    class ApplyAction extends AbstractAction
    {
        public ApplyAction()
        {
            super("Apply");
        }
        
        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            System.out.println("applying!");
        }
    }
    
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
    
    class PrefsTreeSelectionListener implements TreeSelectionListener
    {
        public void valueChanged(TreeSelectionEvent e)
        {
            DefaultMutableTreeNode node = 
                (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
            
            JComponent c = (JComponent) node.getUserObject();
            cardLayout_.show(cardPanel_, c.getName());
        }
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
    // ActionListener Interface
    //--------------------------------------------------------------------------

    /**
     * @see java.awt.event.ActionListener#actionPerformed(
     *      java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
        String action = e.getActionCommand();

        if (action.equals(ACTION_OK))
        {
            dispose();
        }
        else if (action.equals(ACTION_CANCEL))
        {
            dispose();
        }
        else
        {
            logger_.warn(
                "No handler in actionPerformed() for command " + action);
        }
    }
}