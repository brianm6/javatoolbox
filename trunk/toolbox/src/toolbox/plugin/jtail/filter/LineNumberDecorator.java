package toolbox.jtail.filter;

/**
 * Appends a line number to the beginning of the line.
 */
public class LineNumberDecorator extends AbstractLineFilter
{
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
        lineNumber_ = 1;
    }

    //--------------------------------------------------------------------------
    //  ILineFilter Interface
    //--------------------------------------------------------------------------

    /**
     * Adds a line number to the beginning of a string.
     * 
     * @param line String to decorate with a line number.
     * @return String decorated with a line number.
     */
    public String filter(String line)
    {
        if (!isEnabled())
            return line;
        
        if (line == null)
            return line;
                
        return "[" + lineNumber_++ + "] " + line;
    }
}
