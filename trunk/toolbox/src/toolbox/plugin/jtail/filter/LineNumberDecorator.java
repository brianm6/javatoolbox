package toolbox.plugin.jtail.filter;

/**
 * Appends a line number to the beginning of the line.
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
    
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a LineNumberDecorator.
     */
    public LineNumberDecorator()
    {
        reset();
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Resets the number number to one. 
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
     * @see toolbox.plugin.jtail.filter.ILineFilter#filter(
     *      java.lang.StringBuffer)
     */
    public boolean filter(StringBuffer line)
    {
        if (isEnabled())
            line.insert(0,"[" + lineNumber_++ + "] ");
        
        return true;
    }
}