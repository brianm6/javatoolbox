/*
 * Copyright (C) Jerry Huxtable 1998
 */
package toolbox.util.ui.layout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;

/**
 * StackLayout
 */
public class StackLayout extends ConstraintLayout
{
    private static final Integer DEFAULT = new Integer(Direction.CENTER);

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Default Constructor
     */
    public StackLayout()
    {
    }
    
    //--------------------------------------------------------------------------
    // Static
    //--------------------------------------------------------------------------
    
    /**
     * @param  fill         Fill
     * @param  alignment    Alignment
     * @return Alignment
     */
    public static Integer alignment(int fill, int alignment)
    {
        return new Integer(fill << 8 | alignment);
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Sets constraint
     * 
     * @param  c            Component
     * @param  constraint   Constraint object
     */ 
    public void setConstraint(Component c, Object constraint)
    {
        if (constraint == null)
            constraint = DEFAULT;
        if (!(constraint instanceof Integer))
            throw new IllegalArgumentException("Constraint must be an Integer");
        super.setConstraint(c, constraint);
    }

    /**
     * Measures layout
     * 
     * @param  target       Target container
     * @param  dimension    Dimension
     * @param  type         Type
     */
    public void measureLayout(Container target, Dimension dimension, int type)
    {
        int count = target.getComponentCount();
        
        if (dimension != null)
        {
            for (int i = 0; i < count; i++)
            {
                Component m = target.getComponent(i);
                
                if (m.isVisible())
                {
                    Dimension d = getComponentSize(m, type);
                    dimension.width = Math.max(d.width, dimension.width);
                    dimension.height = Math.max(d.height, dimension.height);
                }
            }
        }
        else
        {
            Insets insets = target.getInsets();
            Dimension size = target.getSize();
            int w = size.width - (insets.left + insets.right);
            int h = size.height - (insets.top + insets.bottom);
            Rectangle cell = new Rectangle(insets.left, insets.top, w, h);
            
            for (int i = 0; i < count; i++)
            {
                Component m = target.getComponent(i);
                int n = ((Integer) getConstraint(m)).intValue();
                int alignment = n & 0xff;
                int fill = (n >> 8) & 0xff;
                
                if (m.isVisible())
                {
                    Dimension d = getComponentSize(m, type);
                    Rectangle r = new Rectangle(0, 0, d.width, d.height);
                    Alignment.alignInCell(r, cell, alignment, fill);
                    m.setBounds(r.x, r.y, r.width, r.height);
                }
            }
        }
    }
}