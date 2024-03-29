package toolbox.util.ui.statusbar;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;

/**
 * Layout for the statusbar.
 * <p> 
 * Originally created by Claude Duguay<br>
 * Copyright (c) 2000<br>
 */
public class StatusLayout extends AbstractLayout
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Maps a component to its constraints.
     */
    private Map table_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a StatusLayout.
     */    
    public StatusLayout()
    {
        this(0, 0);
    }


    /**
     * Creates a StatusLayout with the given gaps.
     * 
     * @param hgap Horizontal gap.
     * @param vgap Vertical gap..
     */
    public StatusLayout(int hgap, int vgap)
    {
        super(hgap, vgap);
        table_ = new HashMap();
    }

    //--------------------------------------------------------------------------
    // Overrides AbstractLayout
    //--------------------------------------------------------------------------

    /**
     * @see java.awt.LayoutManager2#addLayoutComponent(java.awt.Component, 
     *      java.lang.Object)
     */
    public void addLayoutComponent(Component comp, Object constraints)
    {
        if (!(constraints instanceof StatusArea))
            throw new IllegalArgumentException(
                "Constraint parameter must be of type StatusArea");        
        
        table_.put(comp, constraints);
    }


    /**
     * @see java.awt.LayoutManager#removeLayoutComponent(java.awt.Component)
     */
    public void removeLayoutComponent(Component comp)
    {
        table_.remove(comp);
    }


    /**
     * @see java.awt.LayoutManager#minimumLayoutSize(java.awt.Container)
     */
    public Dimension minimumLayoutSize(Container parent)
    {
        Insets insets = parent.getInsets();
        int count = parent.getComponentCount();
        int width = 0;
        int height = 0;
        
        for (int i = 0; i < count; i++)
        {
            if (i > 0)
                width += getHgap();
                
            Component child = parent.getComponent(i);
            Dimension size = child.getMinimumSize();
            width += size.width;
            
            if (size.height > height)
                height = size.height;
        }
        
        return new Dimension(
            width + insets.left + insets.right,
            height + insets.top + insets.bottom);
    }


    /**
     * @see java.awt.LayoutManager#preferredLayoutSize(java.awt.Container)
     */
    public Dimension preferredLayoutSize(Container parent)
    {
        Insets insets = parent.getInsets();
        int count = parent.getComponentCount();
        int width = 0;
        int height = 0;
        
        for (int i = 0; i < count; i++)
        {
            if (i > 0)
                width += getHgap();
                
            Component child = parent.getComponent(i);
            Dimension size = child.getPreferredSize();
            width += size.width;
            
            if (size.height > height)
                height = size.height;
        }
        
        return new Dimension(
            width + insets.left + insets.right,
            height + insets.top + insets.bottom);
    }


    /**
     * @see java.awt.LayoutManager#layoutContainer(java.awt.Container)
     */
    public void layoutContainer(Container parent)
    {
        Insets insets = parent.getInsets();

        int count = parent.getComponentCount();
        int gaps = getHgap() * ((count > 0) ? count - 1 : 0);
        // Count fixed width and relative total
        int fixedWidth = 0;
        float relativeTotal = 0;
        
        for (int i = 0; i < count; i++)
        {
            Component child = parent.getComponent(i);

            if (table_.containsKey(child))
            {
                StatusArea constraint = (StatusArea) table_.get(child);
                if (!constraint.isRelativeWidth())
                {
                    fixedWidth += (int) constraint.getRequiredWidth(child);
                }
                else
                {
                    relativeTotal += constraint.getRequiredWidth(child);
                }
            }
            else
                fixedWidth += child.getPreferredSize().width;
        }
        
        int availableWidth =
            parent.getSize().width
                - fixedWidth
                - gaps
                - (insets.left + insets.right);

        // Now do the layout
        int position = insets.left;
        int height = parent.getSize().height - (insets.top + insets.bottom);
        int width;
        
        for (int i = 0; i < count; i++)
        {
            Component child = parent.getComponent(i);
            
            if (table_.containsKey(child))
            {
                StatusArea constraint = (StatusArea) table_.get(child);
                if (!constraint.isRelativeWidth())
                {
                    width = (int) constraint.getRequiredWidth(child);
                }
                else
                {
                    float required = constraint.getRequiredWidth(child);
                    width = (int) (required / relativeTotal * availableWidth);
                }
            }
            else
            {
                width = child.getPreferredSize().width;
            }
            
            child.setBounds(position, insets.top, width, height);
            position += width + getHgap();
        }
    }
}