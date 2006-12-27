package toolbox.util.ui;

import java.awt.Graphics;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JRadioButton;

import toolbox.util.SwingUtil;

/**
 * JSmartRadioButton adds the following behavior.
 * <p>
 * <ul>
 *   <li>Support for antialised text
 * </ul>
 */
public class JSmartRadioButton extends JRadioButton implements AntiAliased
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Antialiased flag.
     */
    private boolean antiAliased_ = SwingUtil.getDefaultAntiAlias();

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a JSmartRadioButton.
     */
    public JSmartRadioButton()
    {
    }


    /**
     * Creates a JSmartRadioButton.
     * 
     * @param text Button label.
     */
    public JSmartRadioButton(String text)
    {
        super(text);
    }


    /**
     * Creates a JSmartRadioButton.
     * 
     * @param a Action activated by the button.
     */
    public JSmartRadioButton(Action a)
    {
        super(a);
    }


    /**
     * Creates a JSmartRadioButton.
     * 
     * @param icon Button icon.
     */
    public JSmartRadioButton(Icon icon)
    {
        super(icon);
    }


    /**
     * Creates a JSmartRadioButton.
     * 
     * @param text Button label.
     * @param icon Button icon.
     */
    public JSmartRadioButton(String text, Icon icon)
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
     * @see toolbox.util.ui.AntiAliased#setAntiAliased(boolean)
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