package toolbox.util.ui.tabbedpane;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.Icon;

/**
 * Implementation of an Icon that remembers its location in coordinate space.
 * This is useful so that a mouse event can be tied to the icon's exact
 * location on the screen.
 */
class SmartTabbedPaneIcon implements Icon
{
    /**
     * X position
     */
    private int x_;

    /**
     * Y position
     */
    private int y_;

    /**
     * Icon being wrapped
     */
    private Icon icon_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /***
     * Creates a SmartTabbedPaneIcon
     * 
     * @param icon Icon to wrap
     */
    public SmartTabbedPaneIcon(Icon icon)
    {
        icon_ = icon;
    }

    //--------------------------------------------------------------------------
    // Icon Interface
    //--------------------------------------------------------------------------

    /**
     * @see javax.swing.Icon#paintIcon(
     *          java.awt.Component, java.awt.Graphics, int, int)
     */
    public void paintIcon(Component c, Graphics g, int x, int y)
    {
        x_ = x;
        y_ = y;

        if (icon_ != null)
            icon_.paintIcon(c, g, x, y);
    }

    /**
     * @see javax.swing.Icon#getIconWidth()
     */
    public int getIconWidth()
    {
        return icon_.getIconWidth();
    }

    /**
     * @see javax.swing.Icon#getIconHeight()
     */
    public int getIconHeight()
    {
        return icon_.getIconHeight();
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /***
     * Returns the bounds of the icon.
     * 
     * @return Rectangle
     */
    public Rectangle getBounds()
    {
        return new Rectangle(x_, y_, getIconWidth(), getIconHeight());
    }
}