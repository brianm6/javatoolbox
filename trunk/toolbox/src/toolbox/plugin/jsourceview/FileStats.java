package toolbox.jsourceview;

import toolbox.util.collections.AsMap;

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
    
    /** 
     * Number of misc lines in the file. This can include import statement,
     * licences embedded as comments, etc. Basically, lines of text which don't
     * really represent in full the category to which they belong.
     */
    private int thrownOutLines_;
        
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
        
    /**
     * Creates a FileStats
     */
    public FileStats()
    {
    }
    
    //--------------------------------------------------------------------------
    //  Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns percentage of source code lines to total lines
     * 
     * @return Percent of source to total
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
     * @param filestats Filestats to add
     */
    public void add(FileStats filestats)
    {
        totalLines_     += filestats.getTotalLines();
        commentLines_   += filestats.getCommentLines();
        codeLines_      += filestats.getCodeLines();
        blankLines_     += filestats.getBlankLines();
        thrownOutLines_ += filestats.getThrownOutLines();
    }

    /**
     * Increments total lines by one
     */
    public void incrementTotalLines()
    {
        ++totalLines_;
    }

    /**
     * Increments comment lines by one
     */
    public void incrementCommentLines()
    {
        ++commentLines_;
    }

    /**
     * Increments real code lines by one
     */
    public void incrementCodeLines()
    {
        ++codeLines_;
    }

    /**
     * Increments blank lines by one
     */
    public void incrementBlankLines()
    {
        ++blankLines_;
    }
    
    /**
     * Increments the thrown out lines by one
     */
    public void incrementThrownOutLines()
    {
        ++thrownOutLines_;
    }

    //--------------------------------------------------------------------------
    //  Accessors
    //--------------------------------------------------------------------------
    
    /**
     * Returns the number of blank lines
     * 
     * @return int
     */
    public int getBlankLines()
    {
        return blankLines_;
    }

    /**
     * Return the number of source code lines
     * 
     * @return int
     */
    public int getCodeLines()
    {
        return codeLines_;
    }

    /**
     * Returns the number of comment lines
     * 
     * @return int
     */
    public int getCommentLines()
    {
        return commentLines_;
    }

    /**
     * Returns the total number of lines
     * 
     * @return int
     */
    public int getTotalLines()
    {
        return totalLines_;
    }

    /**
     * Returns the number of lines thrown out
     * 
     * @return int
     */
    public int getThrownOutLines()
    {
        return thrownOutLines_;
    }
    
    //--------------------------------------------------------------------------
    // Overrides java.lang.Object
    //--------------------------------------------------------------------------
    
    /**
     * Returns the stringified state of this object.
     * 
     * @return String
     */
    public String toString()
    {
        return AsMap.of(this).toString();
    }
}