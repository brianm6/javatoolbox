package toolbox.util.ui;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.Icon;
import javax.swing.JLabel;

import toolbox.util.SwingUtil;

/**
 * JSmartLabel adds the following behavior.
 * <p>
 * <ul>
 *   <li>Support for antialised text
 * </ul>
 */
public class JSmartLabel extends JLabel implements AntiAliased
{
    /**
     * Antialiased flag
     */
    private boolean antiAliased_ = SwingUtil.getDefaultAntiAlias();
    
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
     * Creates a JSmartLabel
     * 
     * @param text Label text
     */
    public JSmartLabel(String text)
    {
        super(text);
    }

    /**
     * Creates a JSmartLabel
     * 
     * @param image Label image
     */
    public JSmartLabel(Icon image)
    {
        super(image);
    }

    /**
     * Creates a JSmartLabel
     * 
     * @param image Label image
     * @param horizontalAlignment Text alignment
     */
    public JSmartLabel(Icon image, int horizontalAlignment)
    {
        super(image, horizontalAlignment);
    }

    /**
     * Creates a JSmartLabel
     * 
     * @param text Label text
     * @param icon Label icon
     * @param horizontalAlignment Text alignment
     */
    public JSmartLabel(String text, Icon icon, int horizontalAlignment)
    {
        super(text, icon, horizontalAlignment);
    }

    /**
     * Creates a JSmartLabel
     * 
     * @param text Label text
     * @param horizontalAlignment Text alignment
     */
    public JSmartLabel(String text, int horizontalAlignment)
    {
        super(text, horizontalAlignment);
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
        SwingUtil.makeAntiAliased(gc, isAntiAliased());
        super.paintComponent(gc);
    }
}