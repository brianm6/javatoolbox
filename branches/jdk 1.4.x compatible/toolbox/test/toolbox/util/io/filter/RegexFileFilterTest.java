package toolbox.util.io.filter;

import java.io.File;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.log4j.Logger;
import org.apache.regexp.RESyntaxException;

/**
 * Unit test for {@link toolbox.util.io.filter.RegexFileFilter}.
 */
public class RegexFileFilterTest extends TestCase {

    // TODO: Add unit tests for match on filename only
    // TODO: Add unit tests for match on path only
    
    private static final Logger logger_ = Logger.getLogger(RegexFileFilterTest.class);

    // --------------------------------------------------------------------------
    // Constants
    // --------------------------------------------------------------------------

    /**
     * Temporary file names that unit tests will use for verification.
     */
    private static final String[] FILENAMES = new String[]{"EVENT.java",};

    // --------------------------------------------------------------------------
    // Fields
    // --------------------------------------------------------------------------

    /**
     * Test directory for filtering files.
     */
    private File testDir_;


    // --------------------------------------------------------------------------
    // Main
    // --------------------------------------------------------------------------

    /**
     * Entrypoint.
     * 
     * @param args None recognized.
     */
    public static void main(String[] args){
        TestRunner.run(RegexFileFilterTest.class);
    }


    // --------------------------------------------------------------------------
    // Overrides TestCase
    // --------------------------------------------------------------------------

    /**
     * Create a temporary directory with files to use for testing.
     */
    protected void setUp() throws Exception{
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        testDir_ = File.createTempFile("temp", "", tmpDir);
        testDir_.delete();

        assertTrue("test dir creation failed for "
            + testDir_.getCanonicalPath(), testDir_.mkdirs());

        for (int i = 0; i < FILENAMES.length; i++)
            FileUtils.writeStringToFile(new File(testDir_, FILENAMES[i]),
                "testing" + i, "UTF-8");
    }


    /**
     * Clean up temporary directory.
     */
    protected void tearDown() throws Exception{
        FileUtils.forceDelete(testDir_);
        super.tearDown();
    }


    // --------------------------------------------------------------------------
    // Case Insensetive
    // --------------------------------------------------------------------------

    public void testAccept_CaseInsensetive_NotFound() throws Exception{
        logger_.info("Running testAccept_CaseInsensetive_NotFound...");
        
        IOFileFilter filter = new RegexFileFilter("bogus", false);
        String matches[] = testDir_.list(filter);
        assertEquals("No matches should have been found", 0, matches.length);
    }


    public void testAccept_CaseInsensetive_CaseMatch() throws Exception{
        logger_.info("Running testAccept_CaseInsensetive_CaseMatch...");
        
        IOFileFilter filter = new RegexFileFilter("EVENT", false);
        String matches[] = testDir_.list(filter);
        assertEquals("One match should have been found", 1, matches.length);
        assertEquals("One match should have been found", "EVENT.java",
            matches[0]);
    }


    public void testAccept_CaseInsensetive_CaseMismatch() throws Exception{
        logger_.info("Running testAccept_CaseInsensetive_CaseMismatch...");
        
        IOFileFilter filter = new RegexFileFilter("event", false);
        String matches[] = testDir_.list(filter);
        assertEquals("One match should have been found", 1, matches.length);
        assertEquals("One match should have been found", "EVENT.java",
            matches[0]);
    }


    // --------------------------------------------------------------------------
    // Case Sensetive
    // --------------------------------------------------------------------------

    public void testAccept_CaseSensetive_NotFound() throws Exception{
        logger_.info("Running testAccept_CaseSensetive_NotFound...");
        
        IOFileFilter filter = new RegexFileFilter("bogus", true);
        String matches[] = testDir_.list(filter);
        assertEquals("No matches should have been found", 0, matches.length);
    }


    public void testAccept_CaseSensetive_ExactMatch() throws Exception{
        logger_.info("Running testAccept_CaseSensetive_ExactMatch...");
        
        IOFileFilter filter = new RegexFileFilter("EVENT", true);
        String matches[] = testDir_.list(filter);
        assertEquals("One match should have been found", 1, matches.length);
        assertEquals("One match should have been found", "EVENT.java",
            matches[0]);
    }


    public void testAccept_CaseSensetive_CaseMismatch() throws Exception{
        logger_.info("Running testAccept_CaseSensetive_CaseMismatch...");
        
        IOFileFilter filter = new RegexFileFilter("event", true);
        String matches[] = testDir_.list(filter);
        assertEquals("No matches should have been found", 0, matches.length);
    }


    // --------------------------------------------------------------------------
    // Negative Unit Tests
    // --------------------------------------------------------------------------

    /**
     * Make sure constructor blows up on invalid regular expressions.
     */
    public void testConstructor_Invalid_RegExp(){
        logger_.info("Running testConstructor_Invalid_RegExp...");

        try {
            RegexFileFilter filter = new RegexFileFilter("*", true);
            fail("Expected exception on invalid regular expression");
        }
        catch (RESyntaxException rese) {
            // Success
        }
    }
}