package toolbox.util.io.filter.test;

import java.io.File;

import org.apache.log4j.Logger;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import toolbox.util.FileUtil;
import toolbox.util.io.filter.NotFilter;

/**
 * Unit test for NotFilter.
 */
public class NotFilterTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(NotFilterTest.class);
     
    /** 
     * Directory for files used by the filtering tests. 
     */
    private File testDir_;
     
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
        
    /** 
     * Entrypoint.
     * 
     * @param args None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(NotFilterTest.class);
    }
    
    //--------------------------------------------------------------------------
    // Overrides junit.framework.TestCase
    //--------------------------------------------------------------------------
    
    /** 
     * Creates a temporary directory with files to use for testing.
     * 
     * @throws Exception on error
     */
    protected void setUp() throws Exception
    {
        testDir_ = FileUtil.createTempDir();
        String base = testDir_.getAbsolutePath() + File.separator;
        FileUtil.setFileContents(base + "NotFilterTest.txt", "testing", false);
        super.setUp();
    }

    
    /**
     * Cleans up the temporary directory.
     * 
     * @throws Exception on error 
     */
    protected void tearDown() throws Exception
    {
        FileUtil.removeDir(testDir_);
        super.tearDown();
    }
   
    //--------------------------------------------------------------------------
    //  Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests the constructors.
     */
    public void testConstructors()
    {
        logger_.info("Running testConstructors...");
        
        NotFilter filter1 = new NotFilter(new MockFilter(true));
        assertNotNull(filter1);
    }
    
    
    /**
     * Tests accept() for (&#33;true).
     */
    public void testAcceptNotTrue()
    {
        logger_.info("Running testAcceptNotTrue...");
        
        NotFilter filter = new NotFilter(new MockFilter(true));
            
        String matches[] = testDir_.list(filter);
        
        assertEquals("No matches should have been found", 0, matches.length);
    }
    
    
    /**
     * Tests accept() for (&#33;false)
     */
    public void testAcceptNotFalse()
    {
        logger_.info("Running testAcceptNotFalse...");
        
        NotFilter filter = 
            new NotFilter(new MockFilter(false));
            
        String matches[] = testDir_.list(filter);
        
        assertEquals("One match should have been found", 1, matches.length);
    }
}