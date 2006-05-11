package toolbox.plugin.jtail.filter;

import java.awt.Color;
import java.util.List;

import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/**
 * Appends a line number to the beginning of a line of text.
 */
public class LineNumberDecorator extends AbstractLineFilter
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Current line number.
     */
    private int lineNumber_;
    
    /**
     * Formatting style for the line number.
     */
    private Style style_;
    
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a LineNumberDecorator.
     */
    public LineNumberDecorator()
    {
        StyleContext context = new StyleContext();
        style_ = context.new NamedStyle();
        StyleConstants.setForeground(style_, Color.green);
        StyleConstants.setBackground(style_, Color.black);
        
        reset();
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Resets the line number to one. 
     */
    public void reset()
    {
        lineNumber_ = 1;
    }
    
    //--------------------------------------------------------------------------
    //  ILineFilter Interface
    //--------------------------------------------------------------------------

    /**
     * Adds a line number to the beginning of a string.
     * 
     * @see toolbox.plugin.jtail.filter.ILineFilter#filter(java.lang.StringBuffer)
     */
    public boolean filter(StringBuffer line)
    {
        if (isEnabled())
            line.insert(0,"[" + lineNumber_++ + "] ");
        
        return true;
    }
    
    
    /*
     * @see toolbox.plugin.jtail.filter.ILineFilter#filter(java.lang.StringBuffer, java.util.List)
     */
    public boolean filter(StringBuffer line, List segments)
    {
        boolean result = filter(line);
        
        if (result && isEnabled())
        {
            StyledSegment segment = new StyledSegment(
                0, ((lineNumber_ - 1) + "").length() + 2 - 1, style_);
            
            segments.add(segment);
        }
            
        return result;
    }
}