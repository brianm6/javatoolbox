package toolbox.util.ui;

import java.awt.Graphics;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenuItem;

import toolbox.util.SwingUtil;

/**
 * 
 */
public class JSmartMenuItem extends JMenuItem implements AntiAliased
{

    /**
     * 
     */
    public JSmartMenuItem()
    {
        super();
    }

    /**
     * @param text
     */
    public JSmartMenuItem(String text)
    {
        super(text);
    }

    /**
     * @param text
     * @param mnemonic
     */
    public JSmartMenuItem(String text, int mnemonic)
    {
        super(text, mnemonic);
    }

    /**
     * @param a
     */
    public JSmartMenuItem(Action a)
    {
        super(a);
    }

    /**
     * @param icon
     */
    public JSmartMenuItem(Icon icon)
    {
        super(icon);
    }

    /**
     * @param text
     * @param icon
     */
    public JSmartMenuItem(String text, Icon icon)
    {
        super(text, icon);
    }

    //--------------------------------------------------------------------------
    // AntiAliased Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.ui.AntiAliased#isAntiAlias()
     */
    public boolean isAntiAlias()
    {
        return SwingUtil.isAntiAliasGlobal();
    }

    /**
     * @see toolbox.util.ui.AntiAliased#setAntiAlias(boolean)
     */
    public void setAntiAlias(boolean b)
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
        SwingUtil.setAntiAlias(gc, isAntiAlias());
        super.paintComponent(gc);
    }
}
