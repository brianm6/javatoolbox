package toolbox.util.ui;

import java.awt.Graphics;

import javax.swing.DefaultListCellRenderer;

import toolbox.util.SwingUtil;

/**
 * SmartListCellRenderer adds the following behavior.
 * <p>
 * <ul>
 *   <li>Support for antialised text
 * </ul>
 */
public class SmartListCellRenderer extends DefaultListCellRenderer
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a SmartListCellRenderer 
     */
    public SmartListCellRenderer()
    {
    }
    
    //--------------------------------------------------------------------------
    // AntiAliased Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.ui.AntiAliased#isAntiAlias()
     */
    public boolean isAntiAliased()
    {
        return SwingUtil.isAntiAliased();
    }

    /**
     * @see toolbox.util.ui.AntiAliased#setAntiAlias(boolean)
     */
    public void setAntiAliased(boolean b)
    {
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
