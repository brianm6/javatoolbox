package toolbox.jsourceview;

/**
 * FileStats represents the statistics gathered for one or more source files
 */
public class FileStats
{
    /** Total number of lines in file **/    
    private int totalLines_;
    
    /** Number of comment lines in file **/
    private int commentLines_;
    
    /** Number of code lines in file **/
    private int codeLines_;
    
    /** Number of blank lines in file **/
    private int blankLines_;
    
    
    /**
     * Constructor
     */
    public FileStats()
    {
    }
    
    
    /**
     * Gets percentage of source code lines to total lines
     * 
     * @return percentage
     */
    public int getPercent()
    {
        if (codeLines_ > 0 && totalLines_ > 0)
            return (int)
                (( (float) codeLines_ / (float)
                    (totalLines_ - blankLines_)) * 100F);
        else
            return 0;
    }


    /**
     * Adds file stat to existing statistics
     * 
     * @param  filestats  Filestats to add
     */
    public void add(FileStats filestats)
    {
        totalLines_   += filestats.getTotalLines();
        commentLines_ += filestats.getCommentLines();
        codeLines_    += filestats.getCodeLines();
        blankLines_   += filestats.getBlankLines();
    }
    
    
    /**
     * Returns the blankLines.
     * 
     * @return int
     */
    public int getBlankLines()
    {
        return blankLines_;
    }


    /**
     * Returns the codeLines.
     * 
     * @return int
     */
    public int getCodeLines()
    {
        return codeLines_;
    }


    /**
     * Returns the commentLines.
     * 
     * @return int
     */
    public int getCommentLines()
    {
        return commentLines_;
    }


    /**
     * Returns the totalLines.
     * 
     * @return int
     */
    public int getTotalLines()
    {
        return totalLines_;
    }


    /**
     * Sets the blankLines.
     * 
     * @param blankLines The blankLines to set
     */
    public void setBlankLines(int blankLines)
    {
        blankLines_ = blankLines;
    }


    /**
     * Sets the codeLines.
     * 
     * @param codeLines The codeLines to set
     */
    public void setCodeLines(int codeLines)
    {
        codeLines_ = codeLines;
    }


    /**
     * Sets the commentLines.
     * 
     * @param commentLines The commentLines to set
     */
    public void setCommentLines(int commentLines)
    {
        commentLines_ = commentLines;
    }


    /**
     * Sets the totalLines.
     * 
     * @param totalLines The totalLines to set
     */
    public void setTotalLines(int totalLines)
    {
        totalLines_ = totalLines;
    }
}