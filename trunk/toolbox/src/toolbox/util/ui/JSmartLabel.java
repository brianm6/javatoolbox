package toolbox.util.ui;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.Icon;
import javax.swing.JLabel;

import toolbox.util.SwingUtil;

/**
 * JSmartLabel 
 */
public class JSmartLabel extends JLabel implements AntiAliased
{
    private boolean antialiased_ = SwingUtil.isAntiAliasGlobal();

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a JSmartLabel 
     */
    public JSmartLabel()
    {
    }
    
    /**
     * @param text
     */
    public JSmartLabel(String text)
    {
        super(text);
    }

    /**
     * @param image
     */
    public JSmartLabel(Icon image)
    {
        super(image);
    }

    /**
     * @param image
     * @param horizontalAlignment
     */
    public JSmartLabel(Icon image, int horizontalAlignment)
    {
        super(image, horizontalAlignment);
    }

    /**
     * @param text
     * @param icon
     * @param horizontalAlignment
     */
    public JSmartLabel(String text, Icon icon, int horizontalAlignment)
    {
        super(text, icon, horizontalAlignment);
    }

    /**
     * @param text
     * @param horizontalAlignment
     */
    public JSmartLabel(String text, int horizontalAlignment)
    {
        super(text, horizontalAlignment);
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
    // ImageObserver Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see java.awt.image.ImageObserver#imageUpdate(
     *      java.awt.Image, int, int, int, int, int)
     */
    public boolean imageUpdate(
        Image img, int infoflags, 
        int x, int y, 
        int width, int height)
    {
        repaint();
        return true;
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