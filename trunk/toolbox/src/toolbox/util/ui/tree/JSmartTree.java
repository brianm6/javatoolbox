package toolbox.util.ui.tree;

import java.awt.Graphics;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import toolbox.util.SwingUtil;
import toolbox.util.ui.AntiAliased;

/**
 * JSmartTtree adds the following behavior.
 * <p>
 * <ul>
 *   <li>Support for antialised text
 * </ul>
 * 
 * @see SmartTreeCellRender
 */
public class JSmartTree extends JTree implements AntiAliased
{
    /**
     * Antialiased flag.
     */
    private boolean antiAliased_ = SwingUtil.getDefaultAntiAlias();
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a JSmartTree.
     */
    public JSmartTree()
    {
    }


    /**
     * Creates a JSmartTree.
     * 
     * @param value
     */
    public JSmartTree(Object[] value)
    {
        super(value);
    }


    /**
     * Creates a JSmartTree.
     * 
     * @param value
     */
    public JSmartTree(Hashtable value)
    {
        super(value);
    }


    /**
     * Creates a JSmartTree.
     * 
     * @param value
     */
    public JSmartTree(Vector value)
    {
        super(value);
    }


    /**
     * Creates a JSmartTree.
     * 
     * @param newModel
     */
    public JSmartTree(TreeModel newModel)
    {
        super(newModel);
    }


    /**
     * Creates a JSmartTree.
     * 
     * @param root
     */
    public JSmartTree(TreeNode root)
    {
        super(root);
    }


    /**
     * Creates a JSmartTree.
     * 
     * @param root
     * @param asksAllowsChildren
     */
    public JSmartTree(TreeNode root, boolean asksAllowsChildren)
    {
        super(root, asksAllowsChildren);
    }

    //--------------------------------------------------------------------------
    // AntiAliased Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.ui.AntiAliased#isAntiAliased()
     */
    public boolean isAntiAliased()
    {
        return antiAliased_;
    }


    /**
     * @see toolbox.util.ui.AntiAliased#setAntiAlias(boolean)
     */
    public void setAntiAliased(boolean b)
    {
        antiAliased_ = b;
        TreeCellRenderer renderer = getCellRenderer();
        
        if (renderer != null && renderer instanceof AntiAliased)
            ((AntiAliased) renderer).setAntiAliased(b); 
    }

    //--------------------------------------------------------------------------
    // Overrides JComponent
    //--------------------------------------------------------------------------

    /**
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    public void paintComponent(Graphics gc)
    {
        SwingUtil.makeAntiAliased(gc, isAntiAliased());
        super.paintComponent(gc);
    }
}
