package toolbox.util.ui;

import java.awt.Graphics;

import javax.swing.JPopupMenu;

import toolbox.util.SwingUtil;

/**
 * JSmartPopupMenu adds the following behavior.
 * <p>
 * <ul>
 *   <li>Support for antialised text
 * </ul>
 */
public class JSmartPopupMenu extends JPopupMenu implements AntiAliased
{
    /**
     * Antialiased flag
     */
    private boolean antiAliased_ = SwingUtil.getDefaultAntiAlias();

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a JSmartPopupMenu 
     */
    public JSmartPopupMenu()
    {
    }

    /**
     * Creates a JSmartPopupMenu
     * 
     * @param label Menu label
     */
    public JSmartPopupMenu(String label)
    {
        super(label);
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
