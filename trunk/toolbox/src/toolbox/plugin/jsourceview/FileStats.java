package toolbox.jsourceview;

/**
 * FileStats represents the statistics gathered for one or more source files
 */
public class FileStats
{
    /** 
     * Total number of lines in file 
     */    
    private int totalLines_;
    
    /** 
     * Number of comment lines in file 
     */
    private int commentLines_;
    
    /** 
     * Number of code lines in file 
     */
    private int codeLines_;
    
    /** 
     * Number of blank lines in file 
     */
    private int blankLines_;
    
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
        
    /**
     * Default constructor
     */
    public FileStats()
    {
    }
    
    //--------------------------------------------------------------------------
    //  Public
    //--------------------------------------------------------------------------
    
    /**
     * @return Percentage of source code lines to total lines
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

    //--------------------------------------------------------------------------
    //  Getters/Setters
    //--------------------------------------------------------------------------
    
    /**
     * @return Number of blank lines
     */
    public int getBlankLines()
    {
        return blankLines_;
    }

    /**
     * @return Number of source code lines
     */
    public int getCodeLines()
    {
        return codeLines_;
    }

    /**
     * @return Number of comment lines
     */
    public int getCommentLines()
    {
        return commentLines_;
    }

    /**
     * @return Total number of lines
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