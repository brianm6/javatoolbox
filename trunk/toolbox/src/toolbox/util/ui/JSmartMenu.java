package toolbox.util.ui;

import java.awt.Graphics;

import javax.swing.Action;
import javax.swing.JMenu;

import toolbox.util.SwingUtil;

/**
 * 
 */
public class JSmartMenu extends JMenu
{
    /**
     * 
     */
    public JSmartMenu()
    {
        super();
    }

    /**
     * @param s
     */
    public JSmartMenu(String s)
    {
        super(s);
    }

    /**
     * @param s
     * @param b
     */
    public JSmartMenu(String s, boolean b)
    {
        super(s, b);
    }

    /**
     * @param a
     */
    public JSmartMenu(Action a)
    {
        super(a);
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
