/*
 * Copyright (C) Jerry Huxtable 1998-2001. All rights reserved.
 */
package toolbox.util.ui.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;

/**
 * Paragraph Layout
 */
public class ParagraphLayout extends ConstraintLayout
{
    /** TYPE */
    public static final int TYPE_MASK = 0x03;
    
    /** Horiz stretch */
    public static final int STRETCH_H_MASK = 0x04;
    
    /** Vert stretch */
    public static final int STRETCH_V_MASK = 0x08;

    /** Value */
    public static final int NEW_PARAGRAPH_VALUE = 1;
    
    /** Top value */
    public static final int NEW_PARAGRAPH_TOP_VALUE = 2;
    
    /** New line value */
    public static final int NEW_LINE_VALUE = 3;

    /** New paragraph */
    public static final Integer NEW_PARAGRAPH = new Integer(0x01);
    
    /** New paragraph top */
    public static final Integer NEW_PARAGRAPH_TOP = new Integer(0x02);
    
    /** New line */
    public static final Integer NEW_LINE = new Integer(0x03);
    
    /** Stretch horiz */
    public static final Integer STRETCH_H = new Integer(0x04);
    
    /** Stretch vert */
    public static final Integer STRETCH_V = new Integer(0x08);
    
    /** Stretch h/v */
    public static final Integer STRETCH_HV = new Integer(0x0c);
    
    /** New line stretch horiz */
    public static final Integer NEW_LINE_STRETCH_H = new Integer(0x07);
    
    /** New line stretch vert */
    public static final Integer NEW_LINE_STRETCH_V = new Integer(0x0b);
    
    /** New line stretch h/v */
    public static final Integer NEW_LINE_STRETCH_HV = new Integer(0x0f);

    private int hGapMajor_, vGapMajor_;
    private int hGapMinor_, vGapMinor_;
    private int rows_;
    private int colWidth1_;
    private int colWidth2_;

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------

    /**
     * Default Constructor
     */
    public ParagraphLayout()
    {
        this(10, 10, 12, 11, 4, 4);
    }

    /**
     * Creates a new paragraph layout
     * 
     * @param  hMargin    Horizontal margin
     * @param  vMargin    Vertical margin
     * @param  hGapMajor  Horizontal gap major
     * @param  vGapMajor  Vertical gap major
     * @param  hGapMinor  Horizontal gap minor
     * @param  vGapMinor  Vertical gap minor
     */
    public ParagraphLayout(
        int hMargin,
        int vMargin,
        int hGapMajor,
        int vGapMajor,
        int hGapMinor,
        int vGapMinor)
    {
        setHMargin(hMargin);
        setVMargin(vMargin);
        hGapMajor_ = hGapMajor;
        vGapMajor_ = vGapMajor;
        hGapMinor_ = hGapMinor;
        vGapMinor_ = vGapMinor;
    }

    //--------------------------------------------------------------------------
    //  Public
    //--------------------------------------------------------------------------

    /**
     * Measures layout
     * 
     * @param  target     Target container
     * @param  dimension  Dimension
     * @param  type       Type
     */
    public void measureLayout(Container target, Dimension dimension, int type)
    {
        int count = target.getComponentCount();
        
        if (count > 0)
        {
            Insets insets = target.getInsets();
            Dimension size = target.getSize();
            int x = 0;
            int y = 0;
            int rowHeight = 0;
            int colWidth = 0;
            int numRows = 0;
            boolean lastWasParagraph = false;

            Dimension[] sizes = new Dimension[count];

            // First pass: work out the column widths and row heights
            for (int i = 0; i < count; i++)
            {
                Component c = target.getComponent(i);
                
                if (includeComponent(c))
                {
                    Dimension d = getComponentSize(c, type);
                    int w = d.width;
                    int h = d.height;
                    sizes[i] = d;
                    Integer n = (Integer) getConstraint(c);

                    if (i == 0 || n == NEW_PARAGRAPH || n == NEW_PARAGRAPH_TOP)
                    {
                        if (i != 0)
                            y += rowHeight + vGapMajor_;
                            
                        colWidth1_ = Math.max(colWidth1_, w);
                        colWidth = 0;
                        rowHeight = 0;
                        lastWasParagraph = true;
                    }
                    else if (n == NEW_LINE || lastWasParagraph)
                    {
                        x = 0;
                        
                        if (!lastWasParagraph && i != 0)
                            y += rowHeight + vGapMinor_;
                            
                        colWidth = w;
                        colWidth2_ = Math.max(colWidth2_, colWidth);
                        
                        if (!lastWasParagraph)
                            rowHeight = 0;
                            
                        lastWasParagraph = false;
                    }
                    else
                    {
                        colWidth += w + hGapMinor_;
                        colWidth2_ = Math.max(colWidth2_, colWidth);
                        lastWasParagraph = false;
                    }
                    
                    rowHeight = Math.max(h, rowHeight);
                }
            }

            // Second pass: actually lay out the components
            if (dimension != null)
            {
                dimension.width = colWidth1_ + hGapMajor_ + colWidth2_;
                dimension.height = y + rowHeight;
            }
            else
            {
                int spareHeight     =
                    size.height     - 
                    (y + rowHeight) - 
                    insets.top      - 
                    insets.bottom   - 
                    2 * getVMargin();
                        
                x = 0;
                y = 0;
                lastWasParagraph = false;
                int start = 0;
                int rowWidth = 0;
                Integer paragraphType = NEW_PARAGRAPH;
                boolean stretchV = false;

                boolean firstLine = true;
                
                for (int i = 0; i < count; i++)
                {
                    Component c = target.getComponent(i);
                    
                    if (includeComponent(c))
                    {
                        Dimension d = sizes[i];
                        int w = d.width;
                        int h = d.height;
                        Integer n = (Integer) getConstraint(c);
                        int nv = n != null ? n.intValue() : 0;

                        if (i == 0 || 
                            n == NEW_PARAGRAPH || 
                            n == NEW_PARAGRAPH_TOP)
                        {
                            if (i != 0)
                                layoutRow(
                                    target,
                                    sizes,
                                    start,
                                    i - 1,
                                    y,
                                    rowWidth,
                                    rowHeight,
                                    firstLine,
                                    type,
                                    paragraphType);
                                    
                            stretchV = false;
                            paragraphType = n;
                            start = i;
                            firstLine = true;
                            
                            if (i != 0)
                                y += rowHeight + vGapMajor_;
                                
                            rowHeight = 0;
                            rowWidth = colWidth1_ + hGapMajor_ - hGapMinor_;
                            lastWasParagraph = true;
                        }
                        else if (n == NEW_LINE || lastWasParagraph)
                        {
                            if (!lastWasParagraph)
                            {
                                layoutRow(
                                    target,
                                    sizes,
                                    start,
                                    count - 1,
                                    y,
                                    rowWidth,
                                    rowHeight,
                                    firstLine,
                                    type,
                                    paragraphType);
                                    
                                stretchV = false;
                                start = i;
                                firstLine = false;
                                y += rowHeight + vGapMinor_;
                                rowHeight = 0;
                            }
                            
                            rowWidth += sizes[i].width + hGapMinor_;
                            lastWasParagraph = false;
                        }
                        else
                        {
                            rowWidth += sizes[i].width + hGapMinor_;
                            lastWasParagraph = false;
                        }
                        
                        if ((nv & STRETCH_V_MASK) != 0 && !stretchV)
                        {
                            stretchV = true;
                            h += spareHeight;
                        }
                        
                        rowHeight = Math.max(h, rowHeight);
                    }
                }
                
                layoutRow(
                    target,
                    sizes,
                    start,
                    count - 1,
                    y,
                    rowWidth,
                    rowHeight,
                    firstLine,
                    type,
                    paragraphType);
            }
        }
    }

    //--------------------------------------------------------------------------
    //  Protected
    //--------------------------------------------------------------------------

    protected void layoutRow(
        Container target,
        Dimension[] sizes,
        int start,
        int end,
        int y,
        int rowWidth,
        int rowHeight,
        boolean paragraph,
        int type,
        Integer paragraphType)
    {
        int x = 0;
        Insets insets = target.getInsets();
        Dimension size = target.getSize();
        
        int spareWidth =
            size.width - rowWidth - insets.left - insets.right - 2*getHMargin();

        for (int i = start; i <= end; i++)
        {
            Component c = target.getComponent(i);
            
            if (includeComponent(c))
            {
                Integer n = (Integer) getConstraint(c);
                int nv = n != null ? n.intValue() : 0;
                Dimension d = sizes[i];
                int w = d.width;
                int h = d.height;

                if ((nv & STRETCH_H_MASK) != 0)
                {
                    w += spareWidth;
                    Dimension max = getComponentSize(c, MAXIMUM);
                    Dimension min = getComponentSize(c, MINIMUM);
                    w = Math.max(min.width, Math.min(max.width, w));
                }
                
                if ((nv & STRETCH_V_MASK) != 0)
                {
                    h = rowHeight;
                    Dimension max = getComponentSize(c, MAXIMUM);
                    Dimension min = getComponentSize(c, MINIMUM);
                    h = Math.max(min.height, Math.min(max.height, h));
                }

                if (i == start)
                {
                    if (paragraph)
                        x = colWidth1_ - w;
                    else
                        x = colWidth1_ + hGapMajor_;
                }
                else if (paragraph && i == start + 1)
                {
                    x = colWidth1_ + hGapMajor_;
                }
                
                int yOffset =
                    paragraphType == NEW_PARAGRAPH_TOP ? 
                        0 : (rowHeight - h) / 2;
                        
                c.setBounds(
                    insets.left + getHMargin() + x,
                    insets.top + getVMargin() + y + yOffset,
                    w,
                    h);
                    
                x += w + hGapMinor_;
            }
        }
    }
}