package toolbox.jsourceview;

public class FileStats
{

    public int getPercent()
    {
        if(codeLines > 0 && totalLines > 0)
            return (int)(((float)codeLines / (float)(totalLines - blankLines)) * 100F);
        else
            return 0;
    }

    public void add(FileStats filestats)
    {
        totalLines += filestats.totalLines;
        commentLines += filestats.commentLines;
        codeLines += filestats.codeLines;
        blankLines += filestats.blankLines;
    }

    FileStats()
    {
    }

    int totalLines;
    int commentLines;
    int codeLines;
    int blankLines;
}