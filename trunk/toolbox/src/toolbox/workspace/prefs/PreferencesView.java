package toolbox.workspace.prefs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingConstants;
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
        
        view.add(BorderLayout.WEST, buildTreePanel());
        view.add(BorderLayout.CENTER, buildPreferencesPanel());
        view.add(BorderLayout.SOUTH, buildButtonPanel());
        
        setContentPane(view);
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
        p.add(new JSmartButton(new DisposeAction("Cancel", this)));
        return p;
    }
    
    class MyTreeCellRenderer extends SmartTreeCellRenderer
    {
        public Component getTreeCellRendererComponent(JTree tree,
            Object value, boolean selected, boolean expanded, boolean leaf,
            int row, boolean hasFocus)
        {
            super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            
            DefaultMutableTreeNode d = (DefaultMutableTreeNode) value;
            
            if (leaf)
            {
                JComponent c = (JComponent) d.getUserObject();
                System.out.println("Name = " + c.getName());
                setText(c.getName());
            }
            
            return this; 
            
        }
    };
    
    
    /**
     * Builds the tree panel with the preference views as children.
     * 
     * @return JPanel
     */
    protected JPanel buildTreePanel()
    {
        
        DefaultMutableTreeNode root_ = new DefaultMutableTreeNode();
        DefaultTreeModel treeModel_ = new DefaultTreeModel(root_);
        tree_ = new JSmartTree(root_);
        tree_.setCellRenderer(new DefaultTreeCellRenderer());
        
        root_.add(new DefaultMutableTreeNode(buildPreferencesPanel()));
        JSmartLabel jl = new JSmartLabel("Peekaboo");
        jl.setName("Label");
        root_.add(new DefaultMutableTreeNode(jl));
        
        JHeaderPanel p = 
            new JHeaderPanel(
                ImageCache.getIcon(ImageCache.IMAGE_CONFIG), 
                "Preferences",
                null,
                new JScrollPane(tree_));
        
        
        return p;
    }
    
    
    /**
     * Builds the preferences panel.
     *
     * @return Preferences panel.
     */
    protected JPanel buildPreferencesPanel()
    {
        // Build and wire preferences panel
        JPanel prefPanel = new JPanel(new GridBagLayout());
        prefPanel.setBorder(BorderFactory.createTitledBorder("Defaults"));

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 4, 7, 4);

        prefPanel.add(new JSmartLabel("AutoScroll", SwingConstants.RIGHT), gbc);

        gbc.gridx++;
        prefPanel.add(new JSmartCheckBox(), gbc);

        gbc.gridy++;
        gbc.gridx--;
        prefPanel.add(
            new JSmartLabel("Show Line Numbers", SwingConstants.RIGHT), gbc);

        gbc.gridx++;
        prefPanel.add(new JSmartCheckBox(), gbc);

        gbc.gridx--;
        gbc.gridy++;
        prefPanel.add(new JSmartLabel("Filter", SwingConstants.RIGHT), gbc);

        gbc.gridx++;
        gbc.fill = GridBagConstraints.NONE;
        prefPanel.add(new JSmartTextField(12), gbc);

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