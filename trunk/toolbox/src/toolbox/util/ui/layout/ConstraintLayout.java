package toolbox.util.ui.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.util.Hashtable;

/**
 * A base class for layouts which simplifies the business of building new
 * layouts with constraints.
 */
public class ConstraintLayout implements LayoutManager2
{
    protected static final int PREFERRED = 0;
    protected static final int MINIMUM = 1;
    protected static final int MAXIMUM = 2;

    protected int hMargin_ = 0;
    protected int vMargin_ = 0;
    private Hashtable constraints_;
    protected boolean includeInvisible_ = false;

    //--------------------------------------------------------------------------
    //  Public
    //--------------------------------------------------------------------------
    
    public void addLayoutComponent(String constraint, Component c)
    {
        setConstraint(c, constraint);
    }

    public void addLayoutComponent(Component c, Object constraint)
    {
        setConstraint(c, constraint);
    }

    public void removeLayoutComponent(Component c)
    {
        if (constraints_ != null)
            constraints_.remove(c);
    }

    public void setConstraint(Component c, Object constraint)
    {
        if (constraint != null)
        {
            if (constraints_ == null)
                constraints_ = new Hashtable();
            constraints_.put(c, constraint);
        }
        else if (constraints_ != null)
            constraints_.remove(c);
    }

    public Object getConstraint(Component c)
    {
        if (constraints_ != null)
            return constraints_.get(c);
        return null;
    }

    public void setIncludeInvisible(boolean includeInvisible)
    {
        this.includeInvisible_ = includeInvisible;
    }

    public boolean getIncludeInvisible()
    {
        return includeInvisible_;
    }

    protected boolean includeComponent(Component c)
    {
        return includeInvisible_ || c.isVisible();
    }

    public Dimension minimumLayoutSize(Container target)
    {
        return calcLayoutSize(target, MINIMUM);
    }

    public Dimension maximumLayoutSize(Container target)
    {
        return calcLayoutSize(target, MAXIMUM);
    }

    public Dimension preferredLayoutSize(Container target)
    {
        return calcLayoutSize(target, PREFERRED);
    }

    public Dimension calcLayoutSize(Container target, int type)
    {
        Dimension dim = new Dimension(0, 0);
        measureLayout(target, dim, type);
        Insets insets = target.getInsets();
        dim.width += insets.left + insets.right + 2 * hMargin_;
        dim.height += insets.top + insets.bottom + 2 * vMargin_;
        return dim;
    }

    public void invalidateLayout(Container target)
    {
    }

    public float getLayoutAlignmentX(Container parent)
    {
        return 0.5f;
    }

    public float getLayoutAlignmentY(Container parent)
    {
        return 0.5f;
    }

    public void layoutContainer(Container target)
    {
        measureLayout(target, null, PREFERRED);
    }

    public void measureLayout(Container target, Dimension dimension, int type)
    {
    }

    protected Dimension getComponentSize(Component c, int type)
    {
        if (type == MINIMUM)
            return c.getMinimumSize();
        if (type == MAXIMUM)
            return c.getMaximumSize();
        return c.getPreferredSize();
    }
}
