package toolbox.util.ui.statusbar;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;

/**
 * AbstractLayout used for the status bar.
 * <p>
 * Originally created by Claude Duguay<br>
 * Copyright (c) 2000<br>
 */
public abstract class AbstractLayout implements LayoutManager2
{
    /** 
     * Horizonal gap between components. 
     */
    private int hgap_;
    
    /** 
     * Vertical gap between components.
     */
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
     * Returns the horizontal gap between components.
     * 
     * @return int
     */
    public int getHgap()
    {
        return hgap_;
    }


    /**
     * Returns the vertical gap between components.
     * 
     * @return int
     */
    public int getVgap()
    {
        return vgap_;
    }


    /**
     * Set the horizontal gap between components.
     * 
     * @param gap Horizontal gap to be set
     */
    public void setHgap(int gap)
    {
        hgap_ = gap;
    }


    /**
     * Set the vertical gap between components.
     * 
     * @param gap Vertical gap to be set
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
     * 
     * @param name Name of the component
     * @param comp Component to be added
     */
    public void addLayoutComponent(String name, Component comp)
    {
        addLayoutComponent(comp, name);
    }


    /**
     * Add the specified component from the layout.
     * By default, we let the Container handle this directly.
     * 
     * @param comp Component to be added
     * @param constraints Constraints to apply when laying out.
     */
    public void addLayoutComponent(Component comp, Object constraints)
    {
    }

    /**
     * Removes the specified component from the layout.
     * By default, we let the Container handle this directly.
     * 
     * @param comp Component to be removed
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
     * 
     * @param target The component which needs to be laid out
     * @return Dimension
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
     * 
     * @param parent Parent container 
     * @return float
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
     * 
     * @param parent Parent container
     * @return float
     */
    public float getLayoutAlignmentY(Container parent)
    {
        return 0.5f;
    }

    //--------------------------------------------------------------------------
    // Overrides java.lang.Object
    //--------------------------------------------------------------------------
    
    /**
     * Returns the string representation of the layout manager.
     * 
     * @return String
     */
    public String toString()
    {
        return getClass().getName() + "[hgap=" + hgap_ + ",vgap=" + vgap_ + "]";
    }
}