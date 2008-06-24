package toolbox.util.ui.statusbar;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.Border;

/**
 * Bevel Border with a variable thickness.
 * <p>
 * Originally created by Claude Duguay<br>
 * Copyright (c) 2000<br>
 */
public class DynamicBevelBorder implements Border
{
    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    /** 
     * Raised bevel type.
     */
    public static final int RAISED = 0;
    
    /**
     * Lowered bebel type.
     */
    public static final int LOWERED = 1;

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /** 
     * Type of bevel. 
     */
    private int type_;
    
    /** 
     * Thickness of the bevel border in pixels. 
     */
    private int thickness_;
    
    /** 
     * Hightlight color.
     */
    private Color highlight_;
    
    /** 
     * Shadow color.
     */
    private Color shadow_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a raised DynamicBevelBorder with a thickness of 1.
     */
    public DynamicBevelBorder()
    {
        this(RAISED, 1, null, null);
    }


    /**
     * Creates a DynamicBevelBorder of the given type (RAISED or LOWERED).
     * 
     * @param type Bevel type.
     */
    public DynamicBevelBorder(int type)
    {
        this(type, 1, null, null);
    }


    /**
     * Creates a DynamicBevelBorder of the given type (RAISED or LOWERED) and
     * thickness.
     * 
     * @param type Bevel type.
     * @param thickness Border thickness in pixels.
     */
    public DynamicBevelBorder(int type, int thickness)
    {
        this(type, thickness, null, null);
    }


    /**
     * Creates a DynamicBevelBorder with the given options.
     * 
     * @param type Bevel type.
     * @param thickness Border thickiness in pixels.
     * @param highlight Highlight color.
     * @param shadow Shadow color.
     */
    public DynamicBevelBorder(int type, int thickness, Color highlight, 
        Color shadow)
    {
        type_      = type;
        thickness_ = thickness;
        highlight_ = highlight;
        shadow_    = shadow;
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Determines the hightlight color for a given component.
     * 
     * @param c Component being painted.
     * @return Highlight color. If not specified, it is derived from the 
     *         component.
     */
    public Color getHighlightColor(Component c)
    {
        if (highlight_ == null)
            highlight_ = c.getBackground().brighter();
        return highlight_;
    }


    /**
     * Determines the shadow color for a given component.
     * 
     * @param c Component being painted.
     * @return Shadow color. If not specified, it is derived from the component.
     */
    public Color getShadowColor(Component c)
    {
        if (shadow_ == null)
            shadow_ = c.getBackground().darker();
        return shadow_;
    }

    //--------------------------------------------------------------------------
    // Border Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see javax.swing.border.Border#isBorderOpaque()
     */
    public boolean isBorderOpaque()
    {
        return true;
    }
    
    
    /**
     * @see javax.swing.border.Border#getBorderInsets(java.awt.Component)
     */
    public Insets getBorderInsets(Component component)
    {
        return new Insets(thickness_, thickness_, thickness_, thickness_);
    }


    /**
     * @see javax.swing.border.Border#paintBorder(
     *      java.awt.Component, java.awt.Graphics, int, int, int, int)
     */
    public void paintBorder(
        Component c,
        Graphics g,
        int x,
        int y,
        int w,
        int h)
    {
        Color hi = (type_ == RAISED ? getHighlightColor(c) : getShadowColor(c));
        Color lo = (type_ == RAISED ? getShadowColor(c) : getHighlightColor(c));

        for (int i = thickness_ - 1; i >= 0; i--)
        {
            g.setColor(hi);
            g.drawLine(x + i, y + i, x + w - i - 1, y + i);
            g.drawLine(x + i, y + i, x + i, x + h - i - 1);

            g.setColor(lo);
            g.drawLine(x + w - i - 1, y + i, x + w - i - 1, y + h - i - 1);
            g.drawLine(x + i, y + h - i - 1, x + w - i - 1, y + h - i - 1);
        }
    }
}