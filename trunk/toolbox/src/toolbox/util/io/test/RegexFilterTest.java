package toolbox.util.io.test;

import java.io.File;

import org.apache.regexp.RESyntaxException;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import toolbox.util.FileUtil;
import toolbox.util.io.RegexFilter;

/**
 * Unit test for RegexFilter
 */
public class RegexFilterTest extends TestCase
{
    /** Test directory for filtering files **/
    private File testDir_;
    
    
    /** 
     * Entrypoint
     * 
     * @param  args  None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(RegexFilterTest.class);
    }


    /** 
     * Create a temporary directory with files to use for testing
     */
    protected void setUp() throws Exception
    {
        testDir_ = new File(FileUtil.getTempFilename());
        testDir_.mkdir();
        String base = testDir_.getAbsolutePath() + File.separator;
        
        FileUtil.setFileContents(base + "file.txt", "testing", false);
        FileUtil.setFileContents(base + "widget.java", "testing", false);
        FileUtil.setFileContents(base + "b2b.xml", "testing", false);
        FileUtil.setFileContents(base + "EVENT.java", "testing", false);
        super.setUp();
    }


    /**
     * Clean up temporary directory 
     */
    protected void tearDown() throws Exception
    {
        FileUtil.cleanDir(testDir_);
        testDir_.delete();
        super.tearDown();
    }

    
    /**
     * Constructor for RegexFilterTest.
     * 
     * @param arg0  Name
     */
    public RegexFilterTest(String arg0)
    {
        super(arg0);
    }
    
    
    /**
     * Tests accept() for not matching any files
     */
    public void testAcceptMatchesZero() throws Exception
    {
        RegexFilter filter = new RegexFilter("xyz", false);
        String matches[] = testDir_.list(filter);
        assertEquals("No matches should have been found", 0, matches.length);
    } 
    

    /**
     * Tests accept() for not matching one file
     */
    public void testAcceptMatchesOne() throws Exception
    {
        RegexFilter filter = new RegexFilter("^b2b", false);
        String matches[] = testDir_.list(filter);
        assertEquals("One match should have been found", 1, matches.length);
        assertEquals("One match should have been found", "b2b.xml", matches[0]);
    } 
    
    
    /**
     * Tests accept() for matching many files
     */
    public void testAcceptMatchesMany() throws Exception
    {
        RegexFilter filter = new RegexFilter("java$", false);
        String matches[] = testDir_.list(filter);
        
        assertEquals("Two matches should have been found", 2, matches.length);
        
        for(int i=0; i<matches.length; i++)
            assertTrue("java file should have been found", 
                matches[i].endsWith("java"));
    } 
}