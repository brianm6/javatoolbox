package toolbox.util.ui.statusbar;

import java.awt.Component;

/**
 * StatusConstraint
 */
public class StatusConstraint implements StatusArea
{
    /** Flag for relative layout */
    private boolean relative_;
    
    /** Requiremd width (implies fixed width) */
    private float width_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a StatusConstraint with the defaults of FIXED and a width of 1
     */
    public StatusConstraint()
    {
        this(JStatusBar.FIXED, 1);
    }

    /**
     * Creates a StatusConstraint with the given width
     * 
     * @param  width  Width of the constraint
     */
    public StatusConstraint(int width)
    {
        this(JStatusBar.RELATIVE, width);
    }

    /**
     * Creates a StatusConstraint setting the relative flag
     * 
     * @param  relative  True if constraint is to be variable width, false
     *                   otherwise.
     */
    public StatusConstraint(boolean relative)
    {
        this(relative, 1);
    }

    /**
     * Creates a StatusConstraint with the given width and relative setting
     * 
     * @param  relative  True if the constraint is to be variable width
     * @param  width     Width of the constraint
     */
    public StatusConstraint(boolean relative, float width)
    {
        relative_ = relative;
        width_    = width;
    }

    //--------------------------------------------------------------------------
    // StatusArea Interface
    //--------------------------------------------------------------------------
    
    public boolean isRelativeWidth()
    {
        return relative_;
    }

    public float getRequiredWidth(Component component)
    {
        if (relative_ || width_ != 1)
            return width_;
            
        return component.getPreferredSize().width;
    }
}

/*
Originally created by Claude Duguay
Copyright (c) 2000
*/

