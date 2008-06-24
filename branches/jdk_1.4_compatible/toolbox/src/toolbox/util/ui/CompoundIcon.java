
package toolbox.util.ui;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.SwingConstants;

import org.apache.commons.lang.Validate;

/**
 * CompoundIcon creates a new icon from two existing icons given a horizontal
 * or vertical orientation.
 * 
 * <pre class="snippet">
 * 
 *   +---+---+
 *   | 1 | 2 |   Join horizontally
 *   +---+---+
 *   
 *   
 *   +---+
 *   | 1 |
 *   +---+       Join vertically
 *   | 2 |
 *   +---+
 *
 * </pre>
 */
public class CompoundIcon implements Icon
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Join the icons next to each other or one on top of the other.
     * 
     * @see SwingConstants#HORIZONTAL
     * @see SwingConstants#VERTICAL
     */
    private int orientation_;
    
    /**
     * First icon.
     */
    private Icon icon1_;
    
    /**
     * Second icon.
     */
    private Icon icon2_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a CompoundIcon using horizontal orientation.
     * 
     * @param icon1 First icon.
     * @param icon2 Second icon.
     */
    public CompoundIcon(Icon icon1, Icon icon2)
    {
        this(icon1, icon2, SwingConstants.HORIZONTAL);
    }
    
    
    /**
     * Creates a CompoundIcon.
     * 
     * @param icon1 First icon.
     * @param icon2 Second icon.
     * @param orientation Horizontal or vertically glue the icons together.
     */
    public CompoundIcon(Icon icon1, Icon icon2, int orientation)
    {
        icon1_ = icon1;
        icon2_ = icon2;
        orientation_ = orientation;
        
        Validate.isTrue(
            orientation_ == SwingConstants.HORIZONTAL ||
            orientation_ == SwingConstants.VERTICAL,
            "Orientation must be either SwingConstants.[HORIZONTAL|VERTICAL]");
    }

    //--------------------------------------------------------------------------
    // Icon Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see javax.swing.Icon#getIconHeight()
     */
    public int getIconHeight()
    {
        int height = 0;
        
        switch (orientation_)
        {
            case SwingConstants.HORIZONTAL:
                height = Math.max(
                    icon1_.getIconHeight(), icon2_.getIconHeight());
                break;
            
            case SwingConstants.VERTICAL:
                height = icon1_.getIconHeight() + icon2_.getIconHeight();
                break;
        }
        
        return height;
    }


    /**
     * @see javax.swing.Icon#getIconWidth()
     */
    public int getIconWidth()
    {
        int width = 0;
        
        switch (orientation_)
        {
            case SwingConstants.HORIZONTAL:
                width = icon1_.getIconWidth() + icon2_.getIconWidth();
                break;
            
            case SwingConstants.VERTICAL:
                width = Math.max(icon1_.getIconWidth(), icon2_.getIconWidth());
                break;
        }
        
        return width;
    }


    /**
     * @see javax.swing.Icon#paintIcon(java.awt.Component, java.awt.Graphics, 
     *      int, int)
     */
    public void paintIcon(Component c, Graphics g, int x, int y)
    {
        switch (orientation_)
        {
            case SwingConstants.HORIZONTAL:
                
                int h = getIconHeight();
                icon1_.paintIcon(c, g, x, y + (h - icon1_.getIconHeight()) / 2);
                
                icon2_.paintIcon(
                    c, g, 
                    x + icon1_.getIconWidth(), 
                    y + (h - icon2_.getIconHeight()) / 2);
                
                break;
            
            case SwingConstants.VERTICAL:
                
                int w = getIconWidth();
                icon1_.paintIcon(c, g, x + (w - icon1_.getIconWidth()) / 2, y);
                
                icon2_.paintIcon(
                    c, g, 
                    x + (w - icon2_.getIconWidth()) / 2 , 
                    y + icon1_.getIconHeight());
                
                break;
        }
    }
}