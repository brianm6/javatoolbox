package toolbox.util.ui.statusbar;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;

/**
 * AbstractLayout
 */
public abstract class AbstractLayout implements LayoutManager2
{
    /** Horizonal gap between components */
    private int hgap_;
    
    /** Vertical gap between components */
    private int vgap_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates an AbstractLayout with a default horizontal and vertical gap
     * of zero.
     */
    public AbstractLayout()
    {
        this(0, 0);
    }

    /**
     * Creates an AbstractLayout.
     * 
     * @param hgap Number of pixels for the horizontal gap
     * @param vgap Number of pixels for the vertical gap
     */
    public AbstractLayout(int hgap, int vgap)
    {
        setHgap(hgap);
        setVgap(vgap);
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * @return Horizontal gap between components.
     */
    public int getHgap()
    {
        return hgap_;
    }

    /**
     * @return Vertical gap between components.
     */
    public int getVgap()
    {
        return vgap_;
    }

    /**
     * Set the horizontal gap between components.
     * 
     * @param gap The horizontal gap to be set
     */
    public void setHgap(int gap)
    {
        hgap_ = gap;
    }

    /**
     * Set the vertical gap between components.
     * 
     * @param gap The vertical gap to be set
     */
    public void setVgap(int gap)
    {
        vgap_ = gap;
    }

    //--------------------------------------------------------------------------
    // LayoutManager2 Interface
    //--------------------------------------------------------------------------

    /**
     * Adds the specified component with the specified name
     * to the layout. By default, we call the more recent
     * addLayoutComponent method with an object constraint
     * argument. The name is passed through directly.
     * @param name The name of the component
     * @param comp The component to be added
     */
    public void addLayoutComponent(String name, Component comp)
    {
        addLayoutComponent(comp, name);
    }

    /**
     * Add the specified component from the layout.
     * By default, we let the Container handle this directly.
     * @param comp The component to be added
     * @param constraints The constraints to apply when laying out.
     */
    public void addLayoutComponent(Component comp, Object constraints)
    {
    }

    /**
     * Removes the specified component from the layout.
     * By default, we let the Container handle this directly.
     * @param comp the component to be removed
     */
    public void removeLayoutComponent(Component comp)
    {
    }

    /**
     * Invalidates the layout, indicating that if the layout
     * manager has cached information it should be discarded.
     */
    public void invalidateLayout(Container target)
    {
    }

    /**
     * Returns the maximum dimensions for this layout given
     * the component in the specified target container.
     * @param target The component which needs to be laid out
     */
    public Dimension maximumLayoutSize(Container target)
    {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Returns the alignment along the x axis. This specifies how
     * the component would like to be aligned relative to other 
     * components. The value should be a number between 0 and 1
     * where 0 represents alignment along the origin, 1 is aligned
     * the furthest away from the origin, 0.5 is centered, etc.
     */
    public float getLayoutAlignmentX(Container parent)
    {
        return 0.5f;
    }

    /**
     * Returns the alignment along the y axis. This specifies how
     * the component would like to be aligned relative to other 
     * components. The value should be a number between 0 and 1
     * where 0 represents alignment along the origin, 1 is aligned
     * the furthest away from the origin, 0.5 is centered, etc.
     */

    public float getLayoutAlignmentY(Container parent)
    {
        return 0.5f;
    }

    //--------------------------------------------------------------------------
    // Overrides java.lang.Object
    //--------------------------------------------------------------------------
    
    /**
     * @return String representation of the layout manager
     */
    public String toString()
    {
        return getClass().getName() + "[hgap=" + hgap_ + ",vgap=" + vgap_ + "]";
    }
}

/*
Originally created by Claude Duguay
Copyright (c) 2000
*/
