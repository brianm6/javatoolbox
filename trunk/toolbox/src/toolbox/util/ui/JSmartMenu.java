package toolbox.util.ui;

import java.awt.Graphics;

import javax.swing.Action;
import javax.swing.JMenu;

import toolbox.util.SwingUtil;

/**
 * JSmartMenu adds the following behavior.
 * <p>
 * <ul>
 *   <li>Support for antialised text
 * </ul>
 */
public class JSmartMenu extends JMenu implements AntiAliased
{
    /**
     * Antialiased flag.
     */
    private boolean antiAliased_ = SwingUtil.getDefaultAntiAlias();

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a JSmartMenu.
     */
    public JSmartMenu()
    {
    }


    /**
     * Creates a JSmartMenu.
     * 
     * @param s Menu text
     */
    public JSmartMenu(String s)
    {
        super(s);
    }


    /**
     * Creates a JSmartMenu.
     * 
     * @param s Menu text
     * @param b Can the menu be torn off  
     */
    public JSmartMenu(String s, boolean b)
    {
        super(s, b);
    }


    /**
     * Creates a JSmartMenu.
     * 
     * @param a Action activated by the selection of this menu
     */
    public JSmartMenu(Action a)
    {
        super(a);
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
