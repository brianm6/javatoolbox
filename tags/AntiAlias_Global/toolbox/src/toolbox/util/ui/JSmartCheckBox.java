package toolbox.util.ui;

import java.awt.Graphics;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBox;

import toolbox.util.SwingUtil;

/**
 * JSmartCheckBox adds the following behavior.
 * <p>
 * <ul>
 *   <li>Support for antialised text
 * </ul>
 */
public class JSmartCheckBox extends JCheckBox implements AntiAliased
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a JSmartComboBox
     */
    public JSmartCheckBox()
    {
    }

    /**
     * Creates a JSmartComboBox
     * 
     * @param text Text label
     */
    public JSmartCheckBox(String text)
    {
        super(text);
    }

    /**
     * Creates a JSmartComboBox
     * 
     * @param text Text label
     * @param selected Initial selected state
     */
    public JSmartCheckBox(String text, boolean selected)
    {
        super(text, selected);
    }

    /**
     * Creates a JSmartComboBox
     * 
     * @param a Action
     */
    public JSmartCheckBox(Action a)
    {
        super(a);
    }

    /**
     * Creates a JSmartComboBox
     * 
     * @param icon Icon
     */
    public JSmartCheckBox(Icon icon)
    {
        super(icon);
    }

    /**
     * Creates a JSmartComboBox
     * 
     * @param icon Icon
     * @param selected Initial selected state
     */
    public JSmartCheckBox(Icon icon, boolean selected)
    {
        super(icon, selected);
    }

    /**
     * Creates a JSmartComboBox
     * 
     * @param text Text label
     * @param icon Icon
     */
    public JSmartCheckBox(String text, Icon icon)
    {
        super(text, icon);
    }

    /**
     * Creates a JSmartComboBox
     * 
     * @param text Text label
     * @param icon Icon
     * @param selected Initial selected state
     */
    public JSmartCheckBox(String text, Icon icon, boolean selected)
    {
        super(text, icon, selected);
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