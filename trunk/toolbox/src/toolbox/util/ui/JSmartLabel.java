package toolbox.util.ui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.geom.RoundRectangle2D;

import javax.swing.Icon;
import javax.swing.JLabel;

import toolbox.util.SwingUtil;

/**
 * JSmartLabel adds the following behavior.
 * <p>
 * <ul>
 *   <li>Antialiased text
 *   <li>Background gradient
 * </ul>
 */
public class JSmartLabel extends JLabel implements AntiAliased
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------

    /**
     * Antialiased flag.
     */
    private boolean antiAliased_ = SwingUtil.getDefaultAntiAlias();
    
    /**
     * Flag to turn the gradient on/off.
     */
    private boolean gradient_;

    /**
     * Gradient start color.
     */
    private Color startColor_;
    
    /**
     * Gradient end color.
     */
    private Color endColor_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a JSmartLabel. 
     */
    public JSmartLabel()
    {
    }

    
    /**
     * Creates a JSmartLabel.
     * 
     * @param text Label text.
     */
    public JSmartLabel(String text)
    {
        super(text);
    }


    /**
     * Creates a JSmartLabel.
     * 
     * @param image Label image.
     */
    public JSmartLabel(Icon image)
    {
        super(image);
    }


    /**
     * Creates a JSmartLabel.
     * 
     * @param image Label image.
     * @param horizontalAlignment Text alignment.
     */
    public JSmartLabel(Icon image, int horizontalAlignment)
    {
        super(image, horizontalAlignment);
    }


    /**
     * Creates a JSmartLabel.
     * 
     * @param text Label text.
     * @param icon Label icon.
     * @param horizontalAlignment Text alignment.
     */
    public JSmartLabel(String text, Icon icon, int horizontalAlignment)
    {
        super(text, icon, horizontalAlignment);
    }


    /**
     * Creates a JSmartLabel.
     * 
     * @param text Label text.
     * @param horizontalAlignment Text alignment.
     */
    public JSmartLabel(String text, int horizontalAlignment)
    {
        super(text, horizontalAlignment);
    }

    
    /**
     * Creates a JSmartLabel with a gradient.
     *
     * @param text Label text.
     * @param start Gradient start color.
     * @param end Gradient end color.
     */
    public JSmartLabel(String text, Color start, Color end) 
    {
        super(text);
        gradient_ = true;
        startColor_ = start;
        endColor_ = end;
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Turns the gradient on/off.
     * 
     * @param b True to turn on the gradient, false to turn it off.
     */
    public void setGradient(boolean b)
    {
        gradient_ = b;
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
        Image img, 
        int infoflags, 
        int x, 
        int y, 
        int width, 
        int height)
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
    
    
    /**
     * @see java.awt.Component#paint(java.awt.Graphics)
     */
    public void paint(Graphics g) 
    {
        if (gradient_)
        {    
            Insets insets = new Insets(0, 0, 0, 0);
            g.setColor(startColor_);
            Graphics2D g2d = (Graphics2D) g;
        
            GradientPaint redtowhite =
                new GradientPaint(
                    insets.left, 
                    insets.top,
                    startColor_,  
                    getWidth(),
                    insets.top,
                    endColor_); 
                
            g2d.setPaint(redtowhite);
        
            g2d.fill(
                new RoundRectangle2D.Double(
                    insets.left, 
                    insets.top, 
                    getWidth(), 
                    getHeight(), 
                    1, 
                    1));
        }
        super.paint(g);
    }
}