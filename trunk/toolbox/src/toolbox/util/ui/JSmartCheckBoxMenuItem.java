package toolbox.util.ui;

import java.awt.Graphics;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;

import toolbox.util.SwingUtil;

/**
 * JSmartCheckBoxMenuItem adds the following behavior.
 * <p>
 * <ul>
 *   <li>Support for antialised text
 * </ul>
 */
public class JSmartCheckBoxMenuItem extends JCheckBoxMenuItem
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
     * Creates a JSmartCheckBoxMenuItem
     */
    public JSmartCheckBoxMenuItem()
    {
    }

    /**
     * Creates a JSmartCheckBoxMenuItem
     * 
     * @param text Checkbox text
     */
    public JSmartCheckBoxMenuItem(String text)
    {
        super(text);
    }

    /**
     * Creates a JSmartCheckBoxMenuItem
     * 
     * @param text Checkbox text
     * @param b Checked state
     */
    public JSmartCheckBoxMenuItem(String text, boolean b)
    {
        super(text, b);
    }

    /**
     * Creates a JSmartCheckedBoxMenuItem
     * 
     * @param a Action activated when the check box is toggled
     */
    public JSmartCheckBoxMenuItem(Action a)
    {
        super(a);
    }

    /**
     * Creates a JSmartCheckBoxMenuItem
     * 
     * @param icon Checkbox icon
     */
    public JSmartCheckBoxMenuItem(Icon icon)
    {
        super(icon);
    }

    /**
     * Creates a JSmartCheckBoxMenuItem
     * 
     * @param text Checkbox text
     * @param icon Checkbox icon
     */
    public JSmartCheckBoxMenuItem(String text, Icon icon)
    {
        super(text, icon);
    }

    /**
     * Creates a JSmartCheckBoxMenuItem
     * 
     * @param text Checkbox text
     * @param icon Checkbox icon
     * @param b Checked state
     */
    public JSmartCheckBoxMenuItem(String text, Icon icon, boolean b)
    {
        super(text, icon, b);
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
