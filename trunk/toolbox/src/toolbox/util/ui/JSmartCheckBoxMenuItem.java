package toolbox.util.ui;

import java.awt.Graphics;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;

import toolbox.util.SwingUtil;

/**
 * 
 */
public class JSmartCheckBoxMenuItem extends JCheckBoxMenuItem
{

    /**
     * 
     */
    public JSmartCheckBoxMenuItem()
    {
        super();
    }

    /**
     * @param text
     */
    public JSmartCheckBoxMenuItem(String text)
    {
        super(text);
    }

    /**
     * @param text
     * @param b
     */
    public JSmartCheckBoxMenuItem(String text, boolean b)
    {
        super(text, b);
    }

    /**
     * @param a
     */
    public JSmartCheckBoxMenuItem(Action a)
    {
        super(a);
    }

    /**
     * @param icon
     */
    public JSmartCheckBoxMenuItem(Icon icon)
    {
        super(icon);
    }

    /**
     * @param text
     * @param icon
     */
    public JSmartCheckBoxMenuItem(String text, Icon icon)
    {
        super(text, icon);
    }

    /**
     * @param text
     * @param icon
     * @param b
     */
    public JSmartCheckBoxMenuItem(String text, Icon icon, boolean b)
    {
        super(text, icon, b);
    }

    //--------------------------------------------------------------------------
    // AntiAliased Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.ui.AntiAliased#isAntiAlias()
     */
    public boolean isAntiAlias()
    {
        return SwingUtil.isAntiAliased();
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
        SwingUtil.makeAntiAliased(gc, isAntiAlias());
        super.paintComponent(gc);
    }

}
