package toolbox.util.ui.statusbar;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JPanel;

import toolbox.util.SwingUtil;

/**
 * JStatusBar
 */
public class JStatusBar extends JPanel
{
    /** 
     * Statusbar component that size is adjusted relative to the contents and
     * space availability on the status bar
     */
    public static final boolean RELATIVE = true;
    
    /**
     * Statusbar component thats size will remain fixed regardless of the 
     * contraints of the other status bar components around it.
     */
    public static final boolean FIXED = false;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a JStatusBar
     */
    public JStatusBar()
    {
        setLayout(new StatusLayout(0, 0));
        //setBorder(new DynamicBevelBorder(DynamicBevelBorder.LOWERED, 1));
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    public void addStatusComponent(JComponent component)
    {
        add(wrapBorder(component), new StatusConstraint());
    }

    public void addStatusComponent(JComponent component, int width)
    {
        add(wrapBorder(component), new StatusConstraint(width));
    }

    public void addStatusComponent(JComponent component, boolean relative)
    {
        add(wrapBorder(component), new StatusConstraint(relative));
    }

    public void addStatusComponent(JComponent component, boolean relative, 
        float width)
    {
        add(wrapBorder(component), new StatusConstraint(relative, width));
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /**
     * Wraps a component with a border if one does not already exist.
     * 
     * @param  c  Component to wrap with a border
     * @return Component wrapped with border (not necessarily the input 
     *         component).
     */        
    protected Component wrapBorder(JComponent c)
    {
        DynamicBevelBorder border = 
            new DynamicBevelBorder(DynamicBevelBorder.LOWERED, 1);
        
        if (c.getBorder() != null)
            c = SwingUtil.wrapTight(c);
        
        c.setBorder(border);
        return c;
    }
}

/*
Originally created by Claude Duguay
Copyright (c) 2000
*/
