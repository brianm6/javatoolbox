package toolbox.util.ui.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.Hashtable;

/**
 * StackLayout is a LayoutManager that arranges components in a vertical (or
 * horizontal) strip aligning them at right, left or centered, and/or filling
 * them to take up any extra vertical or horizontal space. Arrangement tags are
 * provided by using the add(tag,component) form to add components to the
 * container.
 * 
 * The tag consists of one or more of the following, with the two forms
 * applying to horizontal or vertical dimension.
 * 
 * <pre>
 * Positioning:
 *  "Center"                : centered horizontally & vertically (the default)
 *  "Left"    or "Top"      : pushed at the left|top edge.
 *  "Right"   or "Bottom"   : pushed against the right|top edge
 * 
 * Sizing:
 *  "Wide"    or "Tall"     : filled to use available space.
 *  "Wide*#"  or "Tall*#"   : filled but weighted by the number #.
 *  "Fill" (or "Fill*#")    : filled in both directions.
 *  "Width=#" or "Height=#" : given explicit width|height
 * 
 * Margins:
 *  "Flush"                 : margins are not added around this component.
 * </pre>
 * 
 * By default, a component is centered in both directions. The available space
 * along the orientation is divided between the filled components. A common
 * idiom is to build a complicated panel out of, say, a vertical stack of
 * horizontal stacks (both using StackLayout). In that case, it would usually
 * be good to add the horizontal panels using the tag "Wide Flush", so that
 * spacing comes out evenly.
 * <p>
 * 
 * Much of what can be done with GridBagLayout can be achieved by combining a
 * set of subpanels using StackLayout, but typically more concisely. On the
 * other hand, with StackLayout there is less compile time checking of the
 * layout.
 * 
 * @author Bruce R. Miller (bruce.miller@nist.gov)
 * @author Contribution of the National Institute of Standards and Technology,
 * @author not subject to copyright.
 */

public class StackLayout implements LayoutManager
{
    //--------------------------------------------------------------------------
    // Constants 
    //--------------------------------------------------------------------------
    
    /** 
     * The orientation constant for horizontal layouts. 
     */
    public static final int HORIZONTAL = 0;
    
    /** 
     * The orientation constant for vertical layouts. 
     */
    public static final int VERTICAL = 1;

    // Layout codes 
    static final int CENTER = 0x00;
    static final int FRONT = 0x01;
    static final int BACK = 0x02;
    static final int FILL = 0x04;
    static final int ABS = 0x08;
    static final int FLUSH = 0x10;
    static final int POSMASK = 0x03;
    static final int SIZEMASK = 0x0C;
    
    //--------------------------------------------------------------------------
    // Fields 
    //--------------------------------------------------------------------------
    
    private int orientation_ = HORIZONTAL;
    private int margin_ = 2;
    private Hashtable codes_ = new Hashtable();
    private int defaultCode_[] = {CENTER, CENTER, 0, 0};
    
    //--------------------------------------------------------------------------
    // Constructors 
    //--------------------------------------------------------------------------
    
    /** 
     * Create a horizontal StackLayout. 
     */
    public StackLayout()
    {
    }

    /** 
     * Create a StackLayout with the given orientation.
     * 
     * @param orientation Orientation. 
     */
    public StackLayout(int orientation)
    {
        orientation_ = orientation;
    }

    /**
     * Create a StackLayout with the given orientation and space.
     * 
     * @param orientation Orientation.
     * @param margin Margin.
     */
    public StackLayout(int orientation, int margin)
    {
        orientation_ = orientation;
        margin_ = margin;
    }

    //--------------------------------------------------------------------------
    // Public 
    //--------------------------------------------------------------------------
    
    /** 
     * Add the specified component to the layout, parsing the layout tag.
     * 
     * @see java.awt.LayoutManager#addLayoutComponent(java.lang.String, 
     *      java.awt.Component) 
     */
    public void addLayoutComponent(String tag, Component comp)
    {
        tag = tag.toUpperCase().trim();
        int hcode = CENTER, vcode = CENTER, harg = 0, varg = 0;
        int i, l = tag.length(), n;
        
        for (i = 0; i < l;)
        {
            if (tag.startsWith("CENTER", i))
            {
                i += 6;
            }
            else if (tag.startsWith("LEFT", i))
            {
                i += 4;
                hcode |= FRONT;
            }
            else if (tag.startsWith("TOP", i))
            {
                i += 3;
                vcode |= FRONT;
            }
            else if (tag.startsWith("RIGHT", i))
            {
                i += 5;
                hcode |= BACK;
            }
            else if (tag.startsWith("BOTTOM", i))
            {
                i += 6;
                vcode |= BACK;
            }
            else if (tag.startsWith("WIDE", i))
            {
                i += 4;
                hcode |= FILL;
                
                if (tag.startsWith("*", i))
                {
                    i++;
                    n = countDigits(tag, i);
                    harg = parseArg(tag, i, n);
                    i += n;
                }
                else
                    harg = 1;
            }
            else if (tag.startsWith("TALL", i))
            {
                i += 4;
                vcode |= FILL;
                
                if (tag.startsWith("*", i))
                {
                    i++;
                    n = countDigits(tag, i);
                    varg = parseArg(tag, i, n);
                    i += n;
                }
                else
                    varg = 1;
            }
            else if (tag.startsWith("FILL", i))
            {
                i += 4;
                hcode |= FILL;
                vcode |= FILL;
                
                if (tag.startsWith("*", i))
                {
                    i++;
                    n = countDigits(tag, i);
                    harg = varg = parseArg(tag, i, n);
                    i += n;
                }
                else
                    harg = varg = 1;
            }
            else if (tag.startsWith("WIDTH", i))
            {
                i += 5;
                hcode |= ABS;
                
                if (tag.startsWith("=", i))
                {
                    i++;
                    n = countDigits(tag, i);
                    harg = parseArg(tag, i, n);
                    i += n;
                }
                else
                {
                    harg = -1;
                    break;
                }
            }
            else if (tag.startsWith("HEIGHT", i))
            {
                i += 6;
                vcode |= ABS;
                
                if (tag.startsWith("=", i))
                {
                    i++;
                    n = countDigits(tag, i);
                    varg = parseArg(tag, i, n);
                    i += n;
                }
                else
                {
                    varg = -1;
                    break;
                }
            }
            else if (tag.startsWith("FLUSH", i))
            {
                i += 5;
                hcode |= FLUSH;
                vcode |= FLUSH;
            }
            else
            {
                harg = -1;
                break;
            }
            
            for (; (i < l) && Character.isWhitespace(tag.charAt(i)); i++);
            // skip whitesp.
        }
        
        if ((harg == -1) || (varg == -1))
            System.out.println("StackLayout: can't understand \"" + tag + "\"");
        else
        {
            int codes[] = {hcode, vcode, harg, varg};
            codes_.put(comp, codes);
        }
    }

    
    /** 
     * Lays out the specified container.
     * 
     * @param parent Parent container. 
     */
    public void layoutContainer(Container parent)
    {
        int along = orientation_;
        int across = (orientation_ + 1) % 2;
        int n = parent.getComponentCount();
        
        Insets in = parent.getInsets();
        Dimension sz = parent.getSize();
        
        int width = sz.width - in.left - in.right;
        int height = sz.height - in.top - in.bottom;
        
        // total running Length
        int length = (orientation_ == HORIZONTAL ? width : height);
        
        // sideways Depth.
        int depth = (orientation_ == HORIZONTAL ? height : width);

        // First pass: find visible components, record min. sizes,
        // find out how much leftover space there is.
        int nFills = 0;
        int nRubber = 0;
        int sum = 0;
        int prev = FRONT;
        
        int codes[][] = new int[n][];
        int sizes[][] = new int[n][2];
        
        for (int i = 0; i < n; i++)
        {
            // determine # of fills & remaining space.
            Component comp = parent.getComponent(i);

            if (comp.isVisible())
            {
                Dimension d = comp.getMinimumSize();
                int code[] = getCode(comp);
                int size[] = sizes[i];
                
                codes[i] = code;
                size[0] = d.width;
                size[1] = d.height;
                
                int l = size[along];
                int c = code[along];
                
                switch (c & SIZEMASK)
                {
                    case FILL :
                        nFills += code[along + 2];
                        break;
                        
                    case ABS :
                        sum += code[along + 2];
                        break;
                        
                    default :
                        sum += l;
                        break;
                }
                
                switch (c & POSMASK)
                {
                    case CENTER :
                        nRubber++;
                        break;
                        
                    case BACK :
                        if (prev != BACK)
                            nRubber++;
                        break;
                }
                
                if ((c & FLUSH) == 0)
                    sum += 2 * margin_;
                
                prev = (c & POSMASK);
            }
        }
        
        if (prev == CENTER)
            nRubber++;
        
        // Divide up the leftover space among filled components (if any)
        // else as filler between centered or justified components.
        int rubber =
            ((nFills != 0)
                || (nRubber == 0) ? 0 : Math.max(0, (length - sum) / nRubber)),
            fill = (nFills == 0 ? 0 : Math.max(0, (length - sum) / nFills));

        // Second pass: layout the components. running pos.
        int r = (orientation_ == HORIZONTAL ? in.left : in.top);
        
        // side pos.
        int s0 = (orientation_ == HORIZONTAL ? in.top : in.left); 
        int s, l, d, m;
        
        prev = FRONT;
        
        for (int i = 0; i < n; i++)
        {
            int code[] = codes[i];
            int size[] = sizes[i];

            if (code != null)
            {
                int c = code[along];
                int ca = code[across];
                
                m = ((c & FLUSH) == 0 ? margin_ : 0);
                r += m;
                s = s0 + m;
                l = size[along];
                d = size[across];

                switch (c & SIZEMASK)
                {
                    case FILL :
                        if (fill > 0)
                            l = fill * code[along + 2];
                        break;
                        
                    case ABS :
                        l = code[along + 2];
                        break;
                }

                switch (c & POSMASK)
                {
                    case CENTER :
                        r += rubber;
                        break;
                        
                    case BACK :
                        if (prev != BACK)
                            r += rubber;
                        break;
                }

                prev = (c & POSMASK);

                switch (ca & SIZEMASK)
                {
                    case FILL :
                        d = depth - 2 * m;
                        break;
                        
                    case ABS :
                        d = code[across + 2];
                        break;
                }

                switch (ca & POSMASK)
                {
                    case BACK :
                        s += depth - d;
                        break;
                        
                    case CENTER :
                        s += (depth - d) / 2;
                        break;
                }

                Component comp = parent.getComponent(i);

                if (orientation_ == HORIZONTAL)
                    comp.setBounds(r, s, l, d);
                else
                    comp.setBounds(s, r, d, l);

                r += l + m;
            }
        }
    }    

    
    /** 
     * Remove the specified component from the layout.
     * 
     * @param comp Component. 
     */
    public void removeLayoutComponent(Component comp)
    {
        codes_.remove(comp);
    }
    
    
    /** 
     * Calculate the minimum size dimensions for the specififed container.
     * 
     * @param parent Parent.
     * @return Dimension. 
     */
    public Dimension minimumLayoutSize(Container parent)
    {
        return computeLayoutSize(parent, false);
    }

    
    /**
     * Calculate the preferred size dimensions for the specififed container.
     * 
     * @param parent Parent.
     * @return Dimension. 
     */
    public Dimension preferredLayoutSize(Container parent)
    {
        return computeLayoutSize(parent, true);
    }
    
    //--------------------------------------------------------------------------
    // Package 
    //--------------------------------------------------------------------------
    
    /**
     * Counts digits.
     * 
     * @param tag Tag.
     * @param i Dunno.
     * @return int
     */
    int countDigits(String tag, int i)
    {
        int j, l = tag.length();
        for (j = i; (j < l) && Character.isDigit(tag.charAt(j)); j++);
        return j - i;
    }

    
    /**
     * Parses arguments. 
     * 
     * @param tag Tag
     * @param i Dunno
     * @param n Dunno
     * @return int
     */
    int parseArg(String tag, int i, int n)
    {
        int num = -1;
        try
        {
            num = Integer.parseInt(tag.substring(i, i + n));
        }
        catch (Exception e)
        {
            ; // Ignore
        }
        return num;
    }
    

    /**
     * Gets code.
     * 
     * @param comp Component.
     * @return int[]
     */
    int[] getCode(Component comp)
    {
        int code[] = (int[]) codes_.get(comp);
        return (code == null ? defaultCode_ : code);
    }

    
    /**
     * Stretches component.
     * 
     * @param comp Component.
     * @return boolean.
     */
    boolean stretches(Component comp)
    {
        int c[] = getCode(comp);
        return c[orientation_] == FILL;
    }

    
    /**
     * Computes the layout size.
     * 
     * @param parent Parent container.
     * @param preferred Preferred. 
     * @return Dimension
     */
    Dimension computeLayoutSize(Container parent, boolean preferred)
    {
        Insets in = parent.getInsets();
        int inW = in.left + in.right; 
        int inH = in.top + in.bottom;
        int n = parent.getComponentCount();
        
        if (orientation_ == HORIZONTAL)
        {
            int maxH = 0;
            int totW = 0;
            int m;
            
            for (int i = 0; i < n; i++)
            {
                Component comp = parent.getComponent(i);
                
                if (comp.isVisible())
                {
                    int code = getCode(comp)[orientation_];
                    m = ((code & FLUSH) == 0 ? margin_ : 0);
                    
                    Dimension d =
                        (preferred
                            && ((code & SIZEMASK) == FILL)
                                ? comp.getPreferredSize()
                                : comp.getMinimumSize());
                    
                    maxH = Math.max(maxH, d.height + 2 * m);
                    totW += d.width + 2 * m;
                }
            }
            
            return new Dimension(totW + inW, maxH + inH);
        }
        else
        {
            int maxW = 0;
            int totH = 0;
            int m;
            
            for (int i = 0; i < n; i++)
            {
                Component comp = parent.getComponent(i);
                
                if (comp.isVisible())
                {
                    int code = getCode(comp)[orientation_];
                    m = ((code & FLUSH) == 0 ? margin_ : 0);
                    
                    Dimension d =
                        (preferred
                            && ((code & SIZEMASK) == FILL)
                                ? comp.getPreferredSize()
                                : comp.getMinimumSize());
                    
                    maxW = Math.max(maxW, d.width + 2 * m);
                    totH += d.height + 2 * m;
                }
            }
            
            return new Dimension(maxW + inW, totH + inH);
        }
    }
}