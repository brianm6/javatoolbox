package toolbox.util.ui.statusbar;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;

/**
 * AbstractLayout used for the status bar.
 * 
 * <code>
 * Originally created by Claude Duguay
 * Copyright (c) 2000
 * <code>
 */
public abstract class AbstractLayout implements LayoutManager2
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /** 
     * Horizonal gap in pixels between components. 
     */
    private int hgap_;
    
    /** 
     * Vertical gap in pixels between components.
     */
    private int vgap_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates an AbstractLayout with a default horizontal and vertical gap of
     * zero.
     */
    public AbstractLayout()
    {
        this(0, 0);
    }


    /**
     * Creates an AbstractLayout.
     * 
     * @param hgap Number of pixels for the horizontal gap.
     * @param vgap Number of pixels for the vertical gap.
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
     * Sets the horizontal gap between components.
     * 
     * @param gap Horizontal gap to be set.
     */
    public void setHgap(int gap)
    {
        hgap_ = gap;
    }


    /**
     * Sets the vertical gap between components.
     * 
     * @param gap Vertical gap to be set.
     */
    public void setVgap(int gap)
    {
        vgap_ = gap;
    }

    //--------------------------------------------------------------------------
    // LayoutManager2 Interface
    //--------------------------------------------------------------------------

    /**
     * @see java.awt.LayoutManager#addLayoutComponent(java.lang.String,
     *      java.awt.Component)
     */
    public void addLayoutComponent(String name, Component comp)
    {
        addLayoutComponent(comp, name);
    }


    /**
     * @see java.awt.LayoutManager2#addLayoutComponent(java.awt.Component,
     *      java.lang.Object)
     */
    public void addLayoutComponent(Component comp, Object constraints)
    {
    }

    
    /**
     * @see java.awt.LayoutManager#removeLayoutComponent(java.awt.Component)
     */
    public void removeLayoutComponent(Component comp)
    {
    }


    /**
     * @see java.awt.LayoutManager2#invalidateLayout(java.awt.Container)
     */
    public void invalidateLayout(Container target)
    {
    }


    /**
     * @see java.awt.LayoutManager2#maximumLayoutSize(java.awt.Container)
     */
    public Dimension maximumLayoutSize(Container target)
    {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }


    /**
     * @see java.awt.LayoutManager2#getLayoutAlignmentX(java.awt.Container)
     */
    public float getLayoutAlignmentX(Container parent)
    {
        return 0.5f;
    }


    /**
     * @see java.awt.LayoutManager2#getLayoutAlignmentY(java.awt.Container)
     */
    public float getLayoutAlignmentY(Container parent)
    {
        return 0.5f;
    }

    //--------------------------------------------------------------------------
    // Overrides java.lang.Object
    //--------------------------------------------------------------------------
    
    /**
     * Returns a debug friendly string representation of the layout manager.
     * 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return getClass().getName() + "[hgap=" + hgap_ + ",vgap=" + vgap_ + "]";
    }
}