package toolbox.util.io.filter.test;

import java.io.File;

import org.apache.log4j.Logger;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import toolbox.util.FileUtil;
import toolbox.util.RandomUtil;
import toolbox.util.io.filter.AndFilter;

/**
 * Unit test for AndFilter
 */
public class AndFilterTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(AndFilterTest.class);
     
    /** 
     * Test directory for filtering files 
     */
    private File testDir_;
        
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
        TestRunner.run(AndFilterTest.class);
    }

    //--------------------------------------------------------------------------
    // Overrides junit.framework.TestCase
    //--------------------------------------------------------------------------
    
    /** 
     * Create a temporary directory with files to use for testing
     */
    protected void setUp() throws Exception
    {
        testDir_ = new File(FileUtil.generateTempFilename());
        testDir_.mkdir();
        String base = testDir_.getAbsolutePath() + File.separator;
        FileUtil.setFileContents(base + "AndFilterTest.txt", "testing", false);
    }

    /**
     * Clean up temporary directory 
     */
    protected void tearDown() throws Exception
    {
        FileUtil.cleanDir(testDir_);
        testDir_.delete();
    }
   
    //--------------------------------------------------------------------------
    //  Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests the constructors
     */
    public void testConstructors()
    {
        logger_.info("Running testConstructors...");
        
        AndFilter filter1 = 
            new AndFilter(new MockFilter(true), new MockFilter(true));
            
        AndFilter filter2 = new AndFilter();

        assertNotNull(filter1);
        assertNotNull(filter2);        
    }
    
    /**
     * Tests accept() for (true & true)
     */
    public void testAcceptTrueTrue()
    {
        logger_.info("Running testAcceptTrueTrue...");
        
        AndFilter filter = 
            new AndFilter(new MockFilter(true), new MockFilter(true));
            
        String matches[] = testDir_.list(filter);
        
        assertEquals("One match should have been found", 1, matches.length);
    }
    
    /**
     * Tests accept() for (false & false)
     */
    public void testAcceptFalseFalse()
    {
        logger_.info("Running testAcceptFalseFalse...");
        
        AndFilter filter = 
            new AndFilter(new MockFilter(false), new MockFilter(false));
            
        String matches[] = testDir_.list(filter);
        
        assertEquals("No matches should have been found", 0, matches.length);
    }

    /**
     * Tests accept() for (true & false)
     */
    public void testAcceptTrueFalse()
    {
        logger_.info("Running testAcceptTrueFalse...");
        
        AndFilter filter = 
            new AndFilter(new MockFilter(true), new MockFilter(false));
            
        String matches[] = testDir_.list(filter);
        
        assertEquals("No matches should have been found", 0, matches.length);
    }
    
    /**
     * Tests accept() for compound expressions
     */
    public void testAcceptCompound()
    {
        logger_.info("Running testAcceptCompound...");
        
        AndFilter filter = new AndFilter();
        
        for (int i=0; i<100; i++)
            filter.addFilter(new MockFilter(RandomUtil.nextBoolean()));
        
        filter.addFilter(new MockFilter(false));
            
        String matches[] = testDir_.list(filter);
        
        assertEquals("No matches should have been found", 0, matches.length);
    }    
}