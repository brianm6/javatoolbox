package toolbox.util.io.filter.test;

import java.io.File;
import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.FileUtil;
import toolbox.util.RandomUtil;
import toolbox.util.io.filter.OrFilter;

/**
 * Unit test for OrFilter
 */
public class OrFilterTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(OrFilterTest.class);
     
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
     * @param args None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(OrFilterTest.class);
    }

    //--------------------------------------------------------------------------
    // Overrides TestCase
    //--------------------------------------------------------------------------
    
    /** 
     * Create a temporary directory with files to use for testing
     * 
     * @throws Exception on error
     */
    protected void setUp() throws Exception
    {
        testDir_ = new File(FileUtil.generateTempFilename());
        testDir_.mkdir();
        String base = testDir_.getAbsolutePath() + File.separator;
        FileUtil.setFileContents(base + "OrFilterTest.txt", "testing", false);
        super.setUp();
    }

    /**
     * Clean up temporary directory
     * 
     * @throws Exception on error 
     */
    protected void tearDown() throws Exception
    {
        FileUtil.cleanDir(testDir_);
        testDir_.delete();
        super.tearDown();
    }
   
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests the constructors
     */
    public void testConstructors()
    {
        logger_.info("Running testConstructors...");
        
        OrFilter filter1 = 
            new OrFilter(new MockFilter(true), new MockFilter(true));
            
        OrFilter filter2 = new OrFilter();

        assertNotNull(filter1);
        assertNotNull(filter2);        
    }
    
    /**
     * Tests accept() for (true || true)
     */
    public void testAcceptTrueTrue()
    {
        logger_.info("Running testAcceptTrueTrue...");
        
        OrFilter filter = 
            new OrFilter(new MockFilter(true), new MockFilter(true));
            
        String matches[] = testDir_.list(filter);
        
        assertEquals("One match should have been found", 1, matches.length);
    }
    
    /**
     * Tests accept() for (false | false)
     */
    public void testAcceptFalseFalse()
    {
        logger_.info("Running testAcceptFalseFalse...");
        
        OrFilter filter = 
            new OrFilter(new MockFilter(false), new MockFilter(false));
            
        String matches[] = testDir_.list(filter);
        
        assertEquals("No matches should have been found", 0, matches.length);
    }

    /**
     * Tests accept() for (true | false)
     */
    public void testAcceptTrueFalse()
    {
        logger_.info("Running testAcceptTrueFalse...");
        
        OrFilter filter = 
            new OrFilter(new MockFilter(true), new MockFilter(false));
            
        String matches[] = testDir_.list(filter);
        
        assertEquals("One match should have been found", 1, matches.length);
    }
    
    /**
     * Tests accept() for compound expressions
     */
    public void testAcceptCompound()
    {
        logger_.info("Running testAcceptCompound...");
        
        OrFilter filter = new OrFilter();
        
        for (int i=0; i<100; i++)
            filter.addFilter(new MockFilter(RandomUtil.nextBoolean()));
        
        filter.addFilter(new MockFilter(true));
            
        String matches[] = testDir_.list(filter);
        
        assertEquals("One match should have been found", 1, matches.length);
    }    
}