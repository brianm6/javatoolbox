package toolbox.jsourceview.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.log4j.Logger;

import toolbox.jsourceview.FileStats;
import toolbox.util.RandomUtil;
import toolbox.util.StringUtil;

/**
 * Unit test for FileStats.
 */
public class FileStatsTest extends TestCase
{
    private static final Logger logger_ = Logger.getLogger(FileStatsTest.class);
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Entrypoint.
     * 
     * @param args None recognized.
     */
    public static void main(String[] args)
    {
        TestRunner.run(FileStatsTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests aggregation of stats. 
     * 
     * @throws Exception on error.
     */
    public void testAdd() throws Exception
    {
        FileStats base = new FileStats();
        FileStats fs = new FileStats();
        FileStats fs2 = new FileStats();
        
        String[] methods = 
        {
            "incrementBlankLines",
            "incrementCodeLines",
            "incrementCommentLines",
            "incrementThrownOutLines", 
            "incrementTotalLines"
        };

        // Make up dummy stats
        for (int i = 0; i < methods.length; i++)
        {
            for (int j = 0, k = RandomUtil.nextInt(50); j < k; j++)
            {
                MethodUtils.invokeMethod(fs, methods[i], null);
                MethodUtils.invokeMethod(base, methods[i], null);
            }

            for (int j = 0, k = RandomUtil.nextInt(50); j < k; j++)
                MethodUtils.invokeMethod(fs2, methods[i], null);
        }
        
        // Add them together
        fs.add(fs2);
        
        logger_.debug(StringUtil.addBars(fs.toString()));
        
        assertEquals(
            base.getBlankLines() + fs2.getBlankLines(), fs.getBlankLines());
        
        assertEquals(
            base.getCodeLines() + fs2.getCodeLines(), fs.getCodeLines());

        assertEquals(
            base.getCommentLines() + fs2.getCommentLines(), 
            fs.getCommentLines());
        
        assertEquals(
            base.getThrownOutLines() + fs2.getThrownOutLines(), 
            fs.getThrownOutLines());
        
        assertEquals(
            base.getTotalLines() + fs2.getTotalLines(), fs.getTotalLines());
    }
}