package toolbox.util.ui.tree;

import java.awt.Graphics;

import javax.swing.tree.DefaultTreeCellRenderer;

import toolbox.util.SwingUtil;
import toolbox.util.ui.AntiAliased;

/**
 * SmartTreeCellRenderer adds the following behavior.
 * <p>
 * <ul>
 *   <li>Support for antialised text
 * </ul>
 */
public class SmartTreeCellRenderer extends DefaultTreeCellRenderer
    implements AntiAliased
{
    /**
     * Antialiased flag
     */
    private boolean antiAliased_ = SwingUtil.getDefaultAntiAlias();

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a SmartTreeCellRenderer
     */
    public SmartTreeCellRenderer()
    {
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