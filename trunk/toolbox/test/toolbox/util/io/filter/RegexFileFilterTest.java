package toolbox.util.io.filter;

import java.io.File;
import java.util.Arrays;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.commons.collections.SetUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import toolbox.util.FileUtil;

/**
 * Unit test for {@link toolbox.util.io.filter.RegexFilter}.
 */
public class RegexFileFilterTest extends TestCase
{
    private static final Logger logger_ =
        Logger.getLogger(RegexFileFilterTest.class);

    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------

    /**
     * Temporary file names that unit tests will use for verification.
     */
    private static final String[] FILENAMES = new String[] {
        "file.txt", 
        "widget.java",
        "b2b.xml",
        "EVENT.java",
        "proxy.JAVA"
    };

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /** 
     * Test directory for filtering files. 
     */
    private File testDir_;
    
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
        TestRunner.run(RegexFileFilterTest.class);
    }

    //--------------------------------------------------------------------------
    // Overrides TestCase
    //--------------------------------------------------------------------------
    
    /** 
     * Create a temporary directory with files to use for testing.
     * 
     * @throws Exception on error.
     */
    protected void setUp() throws Exception
    {
        testDir_ = FileUtil.createTempDir();
        
        for (int i = 0; i < FILENAMES.length; i++)
            FileUtils.writeStringToFile(
                new File(testDir_, FILENAMES[i]), "testing" + i, "UTF-8");
    }

    
    /**
     * Clean up temporary directory.
     * 
     * @throws Exception on error. 
     */
    protected void tearDown() throws Exception
    {
        FileUtils.forceDelete(testDir_);
        super.tearDown();
    }
   
    //--------------------------------------------------------------------------
    // MATCH_FILE and Case Insensetive 
    //--------------------------------------------------------------------------
    
    /**
     * Tests accept() for not matching any files.
     * 
     * @throws Exception on error.
     */
    public void testAccept_FileOnly_CaseInsensetive_ZeroFiles() throws Exception
    {
        logger_.info("Running testAccept_FileOnly_CaseInsensetive_Zero...");
        
        IOFileFilter filter = 
            new RegexFileFilter("xyz", RegexFileFilter.MATCH_FILE, false);
        
        String matches[] = testDir_.list(filter);
        assertEquals("No matches should have been found", 0, matches.length);
    } 

    
    /**
     * Tests accept() for not matching one file.
     * 
     * @throws Exception on error.
     */
    public void testAccept_FileOnly_CaseInsensetive_OneFile() throws Exception
    {
        logger_.info("Running testAccept_FileOnly_CaseInsensetive_OneFile...");
                
        IOFileFilter filter = 
            new RegexFileFilter("^b2b", RegexFileFilter.MATCH_FILE, false);
        
        String matches[] = testDir_.list(filter);
        assertEquals("One match should have been found", 1, matches.length);
        assertEquals("One match should have been found", "b2b.xml", matches[0]);
    } 
    
    
    /**
     * Tests accept() for matching many files.
     * 
     * @throws Exception on error.
     */
    public void testAccept_FileOnly_CaseInsensetive_ManyFiles() throws Exception
    {
        logger_.info("Running testAccept_FileOnly_CaseInsensetive_ManyFiles...");
        
        IOFileFilter filter = 
            new RegexFileFilter("java$", RegexFileFilter.MATCH_FILE, false);
        
        String matches[] = testDir_.list(filter);
        assertEquals("Two matches should have been found", 3, matches.length);
        assertTrue(ArrayUtils.contains(matches, "widget.java"));
        assertTrue(ArrayUtils.contains(matches, "EVENT.java"));
        assertTrue(ArrayUtils.contains(matches, "proxy.JAVA"));
    }

    
    /**
     * Tests accept() for matching all files in a directory.
     * 
     * @throws Exception on error.
     */
    public void testAccept_FileOnly_CaseInsensetive_AllFiles() throws Exception
    {
        logger_.info("Running testAccept_FileOnly_CaseInsensetive_AllFiles...");
        
        IOFileFilter filter = 
            new RegexFileFilter(".*", RegexFileFilter.MATCH_FILE, false);
        
        String matches[] = testDir_.list(filter);
        assertEquals(FILENAMES.length, matches.length);
        
        assertTrue(SetUtils.isEqualSet(
            Arrays.asList(FILENAMES),
            Arrays.asList(matches)));
    }
    
    //--------------------------------------------------------------------------
    // MATCH_FILE and Case Sensetive 
    //--------------------------------------------------------------------------
    
    /**
     * Tests accept() for not matching any files.
     * 
     * @throws Exception on error.
     */
    public void testAccept_FileOnly_CaseSensetive_ZeroFiles() throws Exception
    {
        logger_.info("Running testAccept_FileOnly_CaseSensetive_ZeroFiles...");
        
        IOFileFilter filter = 
            new RegexFileFilter("xyz", RegexFileFilter.MATCH_FILE, true);
        
        String matches[] = testDir_.list(filter);
        assertEquals("No matches should have been found", 0, matches.length);
    } 

    
    /**
     * Tests accept() for not matching one file.
     * 
     * @throws Exception on error.
     */
    public void testAccept_FileOnly_CaseSensetive_OneFile() throws Exception
    {
        logger_.info("Running testAccept_FileOnly_CaseSensetive_OneFile...");
                
        IOFileFilter filter = 
            new RegexFileFilter("^b2b", RegexFileFilter.MATCH_FILE, true);
        
        String matches[] = testDir_.list(filter);
        assertEquals("One match should have been found", 1, matches.length);
        assertEquals("One match should have been found", "b2b.xml", matches[0]);
    } 
    
    
    /**
     * Tests accept() for matching many files.
     * 
     * @throws Exception on error.
     */
    public void testAccept_FileOnly_CaseSensetive_ManyFiles() throws Exception
    {
        logger_.info("Running testAccept_FileOnly_CaseSensetive_ManyFiles...");
        
        IOFileFilter filter = 
            new RegexFileFilter("java$", RegexFileFilter.MATCH_FILE, true);
        
        String matches[] = testDir_.list(filter);
        assertEquals("Two matches should have been found", 2, matches.length);
        assertTrue(ArrayUtils.contains(matches, "widget.java"));
        assertTrue(ArrayUtils.contains(matches, "EVENT.java"));
    }
    
    
    /**
     * Tests accept() for matching all files in a directory.
     * 
     * @throws Exception on error.
     */
    public void testAccept_FileOnly_CaseSensetive_AllFiles() throws Exception
    {
        logger_.info("Running testAccept_FileOnly_CaseSensetive_AllFiles...");
        
        IOFileFilter filter = 
            new RegexFileFilter(".*", RegexFileFilter.MATCH_FILE, true);
        
        String matches[] = testDir_.list(filter);
        assertEquals(FILENAMES.length, matches.length);
        
        assertTrue(SetUtils.isEqualSet(
            Arrays.asList(FILENAMES),
            Arrays.asList(matches)));
    }

    //--------------------------------------------------------------------------
    // MATCH_PATH and Case Insensetive 
    //--------------------------------------------------------------------------
    
    /**
     * Tests accept() for not matching any files.
     * 
     * @throws Exception on error.
     */
    public void testAccept_PathOnly_CaseInsensetive_ZeroFiles() throws Exception
    {
        logger_.info("Running testAccept_PathOnly_CaseInsensetive_ZeroFiles...");
        
        IOFileFilter filter = 
            new RegexFileFilter("xyz", RegexFileFilter.MATCH_PATH, false);
        
        String matches[] = testDir_.list(filter);
        assertEquals("No matches should have been found", 0, matches.length);
    } 

    
    /**
     * Tests accept() for not matching one file.
     * 
     * @throws Exception on error.
     */
    public void testAccept_PathOnly_CaseInsensetive_OneFile() throws Exception
    {
        logger_.info("Running testAccept_PathOnly_CaseInsensetive_OneFile...");
        
        // testDir
        //   |
        //   +----ApCiO
        //          |
        //          +---abc.txt
        
        File newDir = new File(testDir_, "ApCiO");
        newDir.mkdir();
        
        FileUtils.writeStringToFile(
            new File(newDir, "abc.txt"), "testing", "UTF-8");
        
        IOFileFilter filter = 
            new RegexFileFilter(".*apcio.*", RegexFileFilter.MATCH_PATH, false);
        
        String matches[] = newDir.list(filter);
        assertEquals("One match should have been found", 1, matches.length);
        assertEquals("One match should have been found", "abc.txt", matches[0]);
    } 
    
    
    /**
     * Tests accept() for matching many files.
     * 
     * @throws Exception on error.
     */
    public void testAccept_PathOnly_CaseInsensetive_ManyFiles() throws Exception
    {
        logger_.info("Running testAccept_FileOnly_CaseInsensetive_ManyFiles...");

        
        // testDir
        //   |
        //   +----ApCiO
        //          |
        //          +---abc.txt
        
        File newDir = new File(testDir_, "ApCiO");
        newDir.mkdir();
        
        FileUtils.writeStringToFile(
            new File(newDir, "abc.txt"), "testing", "UTF-8");
        
        IOFileFilter filter = 
            new RegexFileFilter(".*apcio.*", RegexFileFilter.MATCH_PATH, false);
        
        String matches[] = newDir.list(filter);
        assertEquals("One match should have been found", 1, matches.length);
        assertEquals("One match should have been found", "abc.txt", matches[0]);
        
        
        IOFileFilter filter = 
            new RegexFileFilter("java$", RegexFileFilter.MATCH_FILE, false);
        
        String matches[] = testDir_.list(filter);
        assertEquals("Two matches should have been found", 3, matches.length);
        assertTrue(ArrayUtils.contains(matches, "widget.java"));
        assertTrue(ArrayUtils.contains(matches, "EVENT.java"));
        assertTrue(ArrayUtils.contains(matches, "proxy.JAVA"));
    }

    
    /**
     * Tests accept() for matching all files in a directory.
     * 
     * @throws Exception on error.
     */
    public void testAccept_FileOnly_CaseInsensetive_AllFiles() throws Exception
    {
        logger_.info("Running testAccept_FileOnly_CaseInsensetive_AllFiles...");
        
        IOFileFilter filter = 
            new RegexFileFilter(".*", RegexFileFilter.MATCH_FILE, false);
        
        String matches[] = testDir_.list(filter);
        assertEquals(FILENAMES.length, matches.length);
        
        assertTrue(SetUtils.isEqualSet(
            Arrays.asList(FILENAMES),
            Arrays.asList(matches)));
    }
    
    ///////////////////////////////////////////////////////////////////////////
    
    /**
     * Tests accept() for case sensetivity.
     * 
     * @throws Exception on error.
     */
//    public void testAcceptMatchesCase() throws Exception
//    {
//        logger_.info("Running testAcceptMatchesCase...");
//        
//        // Match found        
//        IOFileFilter filter = new RegexFileFilter("^b2b", true);
//        String matches[] = testDir_.list(filter);
//        assertEquals("One match should have been found", 1, matches.length);
//        assertEquals("One match should have been found", "b2b.xml", matches[0]);
//        
//        // Match not found
//        filter = new RegexFileFilter("^B2B", true);
//        matches = testDir_.list(filter);
//        assertEquals("No matches should have been found", 0, matches.length);
//    } 
}