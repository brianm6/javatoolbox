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

    private int hMargin_ = 0;
    private int vMargin_ = 0;
    private Hashtable constraints_;
    private boolean includeInvisible_ = false;

    //--------------------------------------------------------------------------
    //  Public
    //--------------------------------------------------------------------------

    /**
     * Adds a component to the layout
     * 
     * @param  constraint  String constraint
     * @param  c           Component to add
     */    
    public void addLayoutComponent(String constraint, Component c)
    {
        setConstraint(c, constraint);
    }

    /**
     * Adds a component to the layout
     * 
     * @param  c           Component to add
     * @param  constraint  Object constraint
     */    
    public void addLayoutComponent(Component c, Object constraint)
    {
        setConstraint(c, constraint);
    }

    /**
     * Removes a component from the layout
     * 
     * @param  c   Component to remove
     */    
    public void removeLayoutComponent(Component c)
    {
        if (constraints_ != null)
            constraints_.remove(c);
    }

    /**
     * Sets a constraint on a component
     * 
     * @param  c           Component to set constraint on
     * @param  constraint  Object constraint
     */    
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

    /**
     * Gets a constraint for a component
     * 
     * @param  c    Component to add
     * @return Object constraint
     */    
    public Object getConstraint(Component c)
    {
        if (constraints_ != null)
            return constraints_.get(c);
            
        return null;
    }

    /**
     * Mutator for the include visible flag
     * 
     * @param  includeInvisible  Flag
     */    
    public void setIncludeInvisible(boolean includeInvisible)
    {
        includeInvisible_ = includeInvisible;
    }

    /**
     * Accessor for the include invisible flag
     * 
     * @return  True if includeVisible, false otherwise
     */    
    public boolean getIncludeInvisible()
    {
        return includeInvisible_;
    }

    /**
     * @param   target  Container in question
     * @return  Minimum layout size for the given target
     */
    public Dimension minimumLayoutSize(Container target)
    {
        return calcLayoutSize(target, MINIMUM);
    }

    /**
     * @param   target  Container in question
     * @return  Maximum layout size for the given target
     */
    public Dimension maximumLayoutSize(Container target)
    {
        return calcLayoutSize(target, MAXIMUM);
    }

    /**
     * @param   target  Container in question
     * @return  Preferred layout size for the given target
     */
    public Dimension preferredLayoutSize(Container target)
    {
        return calcLayoutSize(target, PREFERRED);
    }

    /**
     * @param   target  Container in question
     * @param   type    Type
     * @return  Layout size for the given target
     */
    public Dimension calcLayoutSize(Container target, int type)
    {
        Dimension dim = new Dimension(0, 0);
        measureLayout(target, dim, type);
        Insets insets = target.getInsets();
        dim.width += insets.left + insets.right + 2 * hMargin_;
        dim.height += insets.top + insets.bottom + 2 * vMargin_;
        return dim;
    }

    /**
     * Invalidates layout - NOOP
     * 
     * @param  target  Target container
     */
    public void invalidateLayout(Container target)
    {
    }

    /**
     * Returns X layout alignment
     * 
     * @param  parent  Parent container
     * @return Alignment
     */
    public float getLayoutAlignmentX(Container parent)
    {
        return 0.5f;
    }

    /**
     * Returns Y layout alignment
     * 
     * @param  parent  Parent container
     * @return Alignment
     */
    public float getLayoutAlignmentY(Container parent)
    {
        return 0.5f;
    }

    /**
     * Lays out container
     * 
     * @param  target  Target container
     */
    public void layoutContainer(Container target)
    {
        measureLayout(target, null, PREFERRED);
    }

    /**
     * Measures the layout
     * 
     * @param  target       Target container
     * @param  dimension    Dimension
     * @param  type         Type
     */
    public void measureLayout(Container target, Dimension dimension, int type)
    {
    }

    /**
     * Returns the hMargin.
     * 
     * @return int
     */
    public int getHMargin()
    {
        return hMargin_;
    }

    /**
     * Returns the vMargin.
     * 
     * @return int
     */
    public int getVMargin()
    {
        return vMargin_;
    }

    /**
     * Sets the hMargin.
     * 
     * @param hMargin The hMargin to set
     */
    public void setHMargin(int hMargin)
    {
        hMargin_ = hMargin;
    }

    /**
     * Sets the vMargin.
     * 
     * @param vMargin The vMargin to set
     */
    public void setVMargin(int vMargin)
    {
        vMargin_ = vMargin;
    }

    //--------------------------------------------------------------------------
    // Private
    //--------------------------------------------------------------------------
    
    /**
     * Includes a component
     * 
     * @param  c           Component to include
     * @return True if the component is visible or include invisible flag is
     *         true, false otherwise
     */    
    protected boolean includeComponent(Component c)
    {
        return includeInvisible_ || c.isVisible();
    }

    /**
     * @param  c    Component
     * @param  type Type
     * @return Component size
     */
    protected Dimension getComponentSize(Component c, int type)
    {
        if (type == MINIMUM)
            return c.getMinimumSize();
            
        if (type == MAXIMUM)
            return c.getMaximumSize();
            
        return c.getPreferredSize();
    }
}
