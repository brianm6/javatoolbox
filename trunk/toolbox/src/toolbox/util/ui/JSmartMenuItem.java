package toolbox.util.ui;

import java.awt.Graphics;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenuItem;

import org.apache.log4j.Logger;

import toolbox.util.SwingUtil;

/**
 * JSmartMenuItem adds the following behavior.
 * <p>
 * <ul>
 *   <li>Support for antialised text
 * </ul>
 */
public class JSmartMenuItem extends JMenuItem implements AntiAliased
{
    private static final Logger logger_ =
        Logger.getLogger(JSmartMenuItem.class);
        
    /**
     * Antialiased flag
     */
    private boolean antiAliased_ = SwingUtil.getDefaultAntiAlias();

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a JSmartMenuItem
     */
    public JSmartMenuItem()
    {
    }

    /**
     * Creates a JSmartMenuItem
     * 
     * @param text Menu item text
     */
    public JSmartMenuItem(String text)
    {
        super(text);
    }

    /**
     * Creates a JSmartMenuItem
     * 
     * @param text Menu item text
     * @param mnemonic Text mnemonic
     */
    public JSmartMenuItem(String text, int mnemonic)
    {
        super(text, mnemonic);
    }

    /**
     * Creates a JSmartMenuItem
     * 
     * @param a Action activated by the selection of this menu item
     */
    public JSmartMenuItem(Action a)
    {
        super(a);
    }

    /**
     * Creates a JSmartMenuItem
     * 
     * @param icon Menu item icon
     */
    public JSmartMenuItem(Icon icon)
    {
        super(icon);
    }

    /**
     * Creates a JSmartMenuItem
     * 
     * @param text Menu item text
     * @param icon Menu item icon
     */
    public JSmartMenuItem(String text, Icon icon)
    {
        super(text, icon);
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
        logger_.debug("AA set to " + b + " on menuItem " + getText());
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
