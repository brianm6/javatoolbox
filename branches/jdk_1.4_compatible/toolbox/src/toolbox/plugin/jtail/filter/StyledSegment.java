package toolbox.plugin.jtail.filter;

import javax.swing.text.Style;

/**
 * StyledSegment defines following attributes for a segment of text.
 * <ul>
 *  <li>Starting index in a string
 *  <li>Ending index in a string
 *  <li>Style associated with text between the staring and ending indices.
 * </ul>
 */
public class StyledSegment
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Start index.
     */
    private int begin_;
    
    /**
     * End index.
     */
    private int end_;
    
    /**
     * Style associated with the text.
     */
    private Style style_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a StyledSegment.
     * 
     * @param begin Starting index.
     * @param end Ending index.
     * @param style Style associated with the segment of text.
     */
    public StyledSegment(int begin, int end, Style style)
    {
        setBegin(begin);
        setEnd(end);
        setStyle(style);
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns the begin.
     * 
     * @return int
     */
    public int getBegin()
    {
        return begin_;
    }


    /**
     * Sets the value of begin.
     * 
     * @param begin The begin to set.
     */
    public void setBegin(int begin)
    {
        begin_ = begin;
    }


    /**
     * Returns the end.
     * 
     * @return int
     */
    public int getEnd()
    {
        return end_;
    }


    /**
     * Sets the value of end.
     * 
     * @param end The end to set.
     */
    public void setEnd(int end)
    {
        end_ = end;
    }


    /**
     * Returns the style.
     * 
     * @return Style
     */
    public Style getStyle()
    {
        return style_;
    }


    /**
     * Sets the value of style.
     * 
     * @param style The style to set.
     */
    public void setStyle(Style style)
    {
        style_ = style;
    }
}