package toolbox.util.ui;

import java.awt.Graphics;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

import toolbox.util.SwingUtil;

/**
 * JSmartButton adds the following behavior.
 * <p>
 * <ul>
 *  <li>Support for antialised text
 * </ul>
 * 
 * @see toolbox.util.SwingUtil
 */
public class JSmartButton extends JButton implements AntiAliased
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
     * Creates a JSmartButton.
     */
    public JSmartButton()
    {
    }


    /**
     * Creates a JSmartButton.
     * 
     * @param text Button label.
     */
    public JSmartButton(String text)
    {
        super(text);
    }


    /**
     * Creates a JSmartButton.
     * 
     * @param a Action activated by the button.
     */
    public JSmartButton(Action a)
    {
        super(a);
    }


    /**
     * Creates a JSmartButton.
     * 
     * @param icon Button icon.
     */
    public JSmartButton(Icon icon)
    {
        super(icon);
    }


    /**
     * Creates a JSmartButton.
     * 
     * @param text Button label.
     * @param icon Button icon.
     */
    public JSmartButton(String text, Icon icon)
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
     * Activates antialiasing on the Graphics if enabled before delegating 
     * painting to the super classes implementation. 
     * 
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    public void paintComponent(Graphics gc)
    {
        SwingUtil.makeAntiAliased(gc, isAntiAliased());
        super.paintComponent(gc);
    }
}