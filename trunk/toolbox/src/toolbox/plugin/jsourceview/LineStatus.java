package toolbox.jsourceview;

/**
 * LineStatus
 */
public class LineStatus
{
    /** is the line part of a comment **/
    private boolean inComment_;
    
    /** should the line be counted **/
    private boolean countLine_;


    /**
     * Constuctor 
     */
    public LineStatus()
    {
        inComment_ = false;
        countLine_ = false;
    }


    /**
     * Mutator for in comment flag
     * 
     * @param  inComment  True if the line is in a comment, false otherwise
     */    
    public void setInComment(boolean inComment)
    {
        inComment_ = inComment;    
    }
    

    /**
     * @return  Is line in comment
     */    
    public boolean getInComment()
    {
        return inComment_;
    }
    
    /**
     * Mutator for countLine flag
     * 
     * @param  countLine  Should the line be counted
     */
    public void setCountLine(boolean countLine)
    {
        countLine_ = countLine;
    }
    
    
    /**
     * @return Count the line flag
     */
    public boolean getCountLine()
    {
        return countLine_;
    }
}