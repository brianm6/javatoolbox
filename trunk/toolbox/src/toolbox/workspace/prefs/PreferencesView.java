package toolbox.workspace.prefs;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;

import org.apache.log4j.Logger;

import toolbox.util.SwingUtil;
import toolbox.util.ui.ImageCache;
import toolbox.util.ui.JHeaderPanel;
import toolbox.util.ui.JSmartButton;
import toolbox.util.ui.JSmartCheckBox;
import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.JSmartTextField;
import toolbox.util.ui.action.DisposeAction;
import toolbox.util.ui.tree.JSmartTree;
import toolbox.util.ui.tree.SmartTreeCellRenderer;

/**
 * Workspace preferences dialog box.
 */
public class PreferencesView extends JDialog implements ActionListener
{
    private static final Logger logger_ =
        Logger.getLogger(PreferencesView.class);

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
    
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Constructor for PreferencesView.
     *
     * @param parent Parent frame.
     */
    public PreferencesView(Frame parent)
    {
        super(parent, "Toolbox Preferences", true);
        buildView();
        pack();
        SwingUtil.centerWindow(parent, this);
    }

    //--------------------------------------------------------------------------
    // Build View
    //--------------------------------------------------------------------------

    /**
     * Constructs the user interface.
     */
    protected void buildView()
    {
        // Add to content pane
        JPanel view = new JPanel(new BorderLayout());
        
        view.add(BorderLayout.CENTER, buildCardPanel());
        view.add(BorderLayout.WEST, buildTreePanel());
        view.add(BorderLayout.SOUTH, buildButtonPanel());
        
        setContentPane(view);
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
            
            JComponent c = (JComponent) d.getUserObject();
            
            if (c != null)
            {
                System.out.println("Name = " + c.getName());
                setText(c.getName());
            }
            else
            {
                setText("NULL");
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
    
    public static void main(String[] args)
    {
        JDialog d = new PreferencesView(new Frame());
        d.setVisible(true);
        SwingUtil.centerWindow(d);
        
    }
    
    /**
     * Builds the tree panel with the preference views as children.
     * 
     * @return JPanel
     */
    protected JPanel buildTreePanel()
    {
        //cardPanel_ = new JPanel(cardLayout_ = new CardLayout());
        JPanel proxyPanel = buildProxyPanel();
        JSmartLabel lastPanel = new JSmartLabel("Last Config Panel");
        lastPanel.setName("Lastl");

        cardPanel_.add(proxyPanel, proxyPanel.getName());
        cardPanel_.add(lastPanel, lastPanel.getName());
        
        JSmartLabel rootCard = new JSmartLabel("Prefs Card Panel");
        rootCard.setName("Prefs");
        
        DefaultMutableTreeNode root_ = new DefaultMutableTreeNode(rootCard);
        DefaultTreeModel treeModel_ = new DefaultTreeModel(root_);
        tree_ = new JSmartTree(root_);
        tree_.setCellRenderer(new PrefsTreeCellRenderer());
         
        root_.add(new DefaultMutableTreeNode(proxyPanel));
        root_.add(new DefaultMutableTreeNode(lastPanel));
        
        SwingUtil.expandAll(tree_, true);
        
        JHeaderPanel p = 
            new JHeaderPanel(
                ImageCache.getIcon(ImageCache.IMAGE_CONFIG), 
                "Preferences",
                null,
                new JScrollPane(tree_));
        
        tree_.addTreeSelectionListener(new PrefsTreeSelectionListener());
        return p;
    }
    
    
    /**
     * Builds the preferences panel.
     *
     * @return Preferences panel.
     */
    protected JPanel buildProxyPanel()
    {
        // Build and wire preferences panel
        JPanel prefPanel = new JPanel(new GridBagLayout());
        prefPanel.setBorder(BorderFactory.createTitledBorder("HTTP Proxy"));

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 4, 7, 4);

        prefPanel.add(new JSmartLabel("Hostname", SwingConstants.RIGHT), gbc);

        gbc.gridx++;
        prefPanel.add(new JSmartTextField(14), gbc);

        gbc.gridy++;
        gbc.gridx--;
        prefPanel.add(
            new JSmartLabel("Port", SwingConstants.RIGHT), gbc);

        gbc.gridx++;
        prefPanel.add(new JSmartTextField(14), gbc);

//        gbc.gridx--;
//        gbc.gridy++;
//        prefPanel.add(new JSmartLabel("Filter", SwingConstants.RIGHT), gbc);
//
//        gbc.gridx++;
//        gbc.fill = GridBagConstraints.NONE;
//        prefPanel.add(new JSmartTextField(12), gbc);

        prefPanel.setName("Proxy");
        return prefPanel;
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