package toolbox.util.ui;

import java.awt.Graphics;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

import toolbox.util.SwingUtil;

/**
 * 
 */
public class JSmartButton extends JButton
{
    private boolean antialiased_ = SwingUtil.isAntiAliasGlobal();
    
    /**
     * 
     */
    public JSmartButton()
    {
        super();
    }

    /**
     * @param text
     */
    public JSmartButton(String text)
    {
        super(text);
    }

    /**
     * @param a
     */
    public JSmartButton(Action a)
    {
        super(a);
    }

    /**
     * @param icon
     */
    public JSmartButton(Icon icon)
    {
        super(icon);
    }

    /**
     * @param text
     * @param icon
     */
    public JSmartButton(String text, Icon icon)
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
        return antialiased_;
    }

    /**
     * @see toolbox.util.ui.AntiAliased#setAntiAlias(boolean)
     */
    public void setAntiAlias(boolean b)
    {
        antialiased_ = b;
    }
    
    //--------------------------------------------------------------------------
    // Overrides JComponent
    //--------------------------------------------------------------------------

    /**
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    public void paintComponent(Graphics gc)
    {
        SwingUtil.setAntiAlias(gc, antialiased_);
        super.paintComponent(gc);
    }

}
