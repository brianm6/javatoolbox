package toolbox.jsourceview;

/**
 * FileStats 
 */
public class FileStats
{
    /** Total number of lines in file **/    
    public int totalLines;
    
    /** Number of comment lines in file **/
    public int commentLines;
    
    /** Number of code lines in file **/
    public int codeLines;
    
    /** Number of blank lines in file **/
    public int blankLines;
    
    
    /**
     * Gets percentage
     * 
     * @return percentage
     */
    public int getPercent()
    {
        if(codeLines > 0 && totalLines > 0)
            return (int)
                (((float)codeLines / (float)(totalLines - blankLines)) * 100F);
        else
            return 0;
    }

    /**
     * Adds file stat to existing results
     * 
     * @param  filestats  Filestats to add
     */
    public void add(FileStats filestats)
    {
        totalLines += filestats.totalLines;
        commentLines += filestats.commentLines;
        codeLines += filestats.codeLines;
        blankLines += filestats.blankLines;
    }

    /**
     * No arg
     */
    FileStats()
    {
    }
}