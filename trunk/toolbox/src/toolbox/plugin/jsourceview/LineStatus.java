package toolbox.jsourceview;

/**
 * LineStatus represents the current status of a line of source while it is 
 * being parsed.
 */
public class LineStatus
{
    /** 
     * Is the line part of a comment?
     */
    private boolean inComment_;
    
    /** 
     * Should the line be counted?
     */
    private boolean countLine_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a LineStatus. 
     */
    public LineStatus()
    {
        inComment_ = false;
        countLine_ = false;
    }

    //--------------------------------------------------------------------------
    // Accessors/Mutators
    //--------------------------------------------------------------------------
    
    /**
     * Mutator for in comment flag.
     * 
     * @param inComment True if the line is in a comment, false otherwise
     */    
    public void setInComment(boolean inComment)
    {
        inComment_ = inComment;    
    }

    
    /**
     * Returns true if the line is in a comment, false otherwise.
     * 
     * @return Is line in a comment
     */    
    public boolean getInComment()
    {
        return inComment_;
    }
    
    
    /**
     * Mutator for countLine flag.
     * 
     * @param countLine Should the line be counted
     */
    public void setCountLine(boolean countLine)
    {
        countLine_ = countLine;
    }
    
    
    /**
     * Returns true if the line is considered a real line of code, false
     * otherwise.
     * 
     * @return Count the line flag
     */
    public boolean isRealCode()
    {
        return countLine_;
    }
}