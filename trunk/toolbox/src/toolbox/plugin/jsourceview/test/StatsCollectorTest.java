package toolbox.jsourceview.test;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.jsourceview.FileStats;
import toolbox.jsourceview.StatsCollector;
import toolbox.util.ResourceUtil;

/**
 * Unit test for StatsCollector
 */
public class StatsCollectorTest extends TestCase
{
    private static final Logger logger_ =
        Logger.getLogger(StatsCollectorTest.class);
        
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Entrypoint
     * 
     * @param  args  None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(StatsCollectorTest.class);
    }

    /**
     * Tests getStats() on an empty file
     * 
     * @throws Exception
     */
    public void testGetStatsEmptyFile() throws Exception
    {
        logger_.info("Running testGetStatsEmptyFile...");
        
        StringReader reader = new StringReader("");
        FileStats stats = new StatsCollector().getStats(reader);
        
        assertEquals(0, stats.getBlankLines());
        assertEquals(0, stats.getCodeLines());
        assertEquals(0, stats.getCommentLines());
        assertEquals(0, stats.getThrownOutLines());
        assertEquals(0, stats.getTotalLines());
        assertEquals(0, stats.getPercent());
    }

    /**
     * Tests getStats() on a small java file
     * 
     * @throws Exception
     */
    public void testGetStats() throws Exception
    {
        logger_.info("Running testGetStats...");
        
        Reader reader = new InputStreamReader(
            ResourceUtil.getResource(
            "/toolbox/jsourceview/test/StatsCollectorTest_testGetStatus.txt"));
            
        FileStats stats = new StatsCollector().getStats(reader);
        
        logger_.info("\n" + stats);
        
        assertEquals(6, stats.getBlankLines());
        assertEquals(7, stats.getCodeLines());
        assertEquals(20, stats.getCommentLines());
        assertEquals(9, stats.getThrownOutLines());
        assertEquals(42, stats.getTotalLines());
        assertEquals(19, stats.getPercent());
    }
}