package toolbox.util.file.test;

import java.io.File;
import java.util.Date;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.FileUtil;
import toolbox.util.RandomUtil;
import toolbox.util.file.FileComparator;

/**
 * Unit test for FileComparator.
 */
public class FileComparatorTest extends TestCase
{
    private static final Logger logger_ =
        Logger.getLogger(FileComparatorTest.class);
    
    //--------------------------------------------------------------------------
    // Fields 
    //--------------------------------------------------------------------------
    
    // Test files which are initialized and cleaned up respectively in setUp()
    // and tearDown().
    private File fileA_;
    private File fileB_;
    private File fileC_;
    private File fileD_;
    
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
        TestRunner.run(FileComparatorTest.class);
    }

    //--------------------------------------------------------------------------
    // Overrides TestCase
    //--------------------------------------------------------------------------
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        fileA_ = FileUtil.createTempFile();
        fileB_ = FileUtil.createTempFile();
        fileC_ = FileUtil.createTempFile();
        fileD_ = FileUtil.createTempFile();
    }
    
    
    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        FileUtil.delete(fileA_);
        FileUtil.delete(fileB_);
        FileUtil.delete(fileC_);
        FileUtil.delete(fileD_);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests the constructor for underflow condition.
     */
    public void testConstructorUnderFlow()
    {
        logger_.info("Running testConstructorUnderFlow...");
        
        try
        {
            new FileComparator(-1);
            fail("Construction should have failed for invalid type.");
        }
        catch (AssertionFailedError afe)
        {
            ; // Success
        }
    }

    
    /**
     * Tests the constructor for an overflow condition of the comparison 
     * criteria.
     */
    public void testConstructorOverFlow()
    {
        logger_.info("Running testConstructorOverFlow...");
        
        try
        {
            new FileComparator(999);
            fail("Construction should have failed for invalid type.");
        }
        catch (AssertionFailedError afe)
        {
            ; // Success
        }
    }
    
    
    /**
     * Tests file comparison by name.
     * 
     * @throws Exception on error.
     */
    public void testCompareByName() throws Exception
    {
        logger_.info("Running testCompareByName...");
        
        File tmpDir = FileUtil.getTempDir();
        File fileA = new File(tmpDir, "a" + RandomUtil.nextInt());
        File fileB = new File(tmpDir, "b" + RandomUtil.nextInt());
        
        try
        {
            FileComparator fc = new FileComparator(FileComparator.COMPARE_NAME);
            assertTrue(fc.compare(fileA, fileB) < 0);
            assertTrue(fc.compare(fileB, fileA) > 0);
            assertTrue(fc.compare(fileA, fileA) == 0);
        }
        finally
        {
            FileUtil.delete(fileA);
            FileUtil.delete(fileB);
        }
    }

    
    /**
     * Tests file comparison by size.
     * 
     * @throws Exception on error.
     */
    public void testCompareBySize() throws Exception
    {
        logger_.info("Running testCompareBySize...");
        
        FileUtil.setFileContents(fileA_, "smaller", false);
        FileUtil.setFileContents(fileB_, "l a r g e r", false);
        
        FileComparator fc = new FileComparator(FileComparator.COMPARE_SIZE);
        assertTrue(fc.compare(fileA_, fileB_) < 0);
        assertTrue(fc.compare(fileB_, fileA_) > 0);
        assertTrue(fc.compare(fileA_, fileA_) == 0);
    }

    
    /** 
     * Tests file comparison by timestamp.
     * 
     * @throws Exception on error.
     */
    public void testCompareByTimestamp() throws Exception
    {
        logger_.info("Running testCompareByTimestamp...");
        
        FileUtil.setFileContents(fileA_, "fileA", false);
        FileUtil.setFileContents(fileB_, "fileB", false);
        FileUtil.setFileContents(fileC_, "fileC", false);
        FileUtil.setFileContents(fileD_, "fileD", false);
        
        long d = new Date().getTime();
        fileA_.setLastModified(d - 10000);
        fileB_.setLastModified(d);
        fileC_.setLastModified(d);
        fileD_.setLastModified(d + 10000);
        
        FileComparator fc = new FileComparator(FileComparator.COMPARE_DATE);
        
        assertTrue(fc.compare(fileA_, fileB_) < 0);
        assertTrue(fc.compare(fileB_, fileB_) == 0);
        assertTrue(fc.compare(fileB_, fileC_) == 0);
        assertTrue(fc.compare(fileB_, fileD_) < 0);
        assertTrue(fc.compare(fileD_, fileB_) > 0);
    }
    
    
    /**
     * Tests file comparison by contents.
     * 
     * @throws Exception on error.
     */
    public void testCompareByContents() throws Exception
    {
        logger_.info("Running testCompareByContents...");
        
        FileUtil.setFileContents(fileA_, "this_file_should_match", false);
        FileUtil.setFileContents(fileB_, "this_file_should_match", false);
        FileUtil.setFileContents(fileC_, "yabba dabba doo,i see you!",false);
        FileUtil.setFileContents(fileD_, "abcdefgijklmnopq", false);
        
        FileComparator fc = 
            new FileComparator(FileComparator.COMPARE_CONTENTS);
        
        assertTrue(fc.compare(fileA_, fileA_) == 0);
        assertTrue(fc.compare(fileA_, fileB_) == 0);
        assertTrue(fc.compare(fileB_, fileA_) == 0);
        assertTrue(fc.compare(fileA_, fileC_) < 0);
        assertTrue(fc.compare(fileA_, fileD_) > 0);
    }

    
    /**
     * Tests file comparison by contents when one of the files is null.
     * 
     * @throws Exception on error.
     */
    public void testCompareByContentsNull() throws Exception
    {
        logger_.info("Running testCompareByContentsNull...");
        
        FileUtil.setFileContents(fileA_, "this_file_should_match", false);
        FileComparator fc = new FileComparator(FileComparator.COMPARE_CONTENTS);
        
        assertTrue(fc.compare(fileA_, null) > 0);
        assertTrue(fc.compare(null, fileA_) < 0);
    }

    
    /**
     * Tests file comparison by contents when the object is being compared to 
     * itself.
     * 
     * @throws Exception on error.
     */
    public void testCompareByContentsSelf() throws Exception
    {
        logger_.info("Running testCompareByContentsSelf...");
        
        FileUtil.setFileContents(fileA_, "fileA", false);
        FileComparator fc = new FileComparator(FileComparator.COMPARE_CONTENTS);
        
        assertTrue(fc.compare(fileA_, fileA_) == 0);
    }


    /**
     * Tests file comparison by contents when either of the files does not
     * is not a file.
     * 
     * @throws Exception on error.
     */
    public void testCompareByContentsFileNotExist() throws Exception
    {
        logger_.info("Running testCompareByContentsFileNotExist...");
        
        FileUtil.setFileContents(fileA_, "fileB", false);
        FileComparator fc = new FileComparator(FileComparator.COMPARE_CONTENTS);
        
        try
        {
            fc.compare(fileA_, fileB_);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException iae)
        {
            ; // Success
        }
        catch (Exception e)
        {
            fail("Expected IllegalArgumentException");
        }
        
        // Switch args
        
        try
        {
            fc.compare(fileB_, fileA_);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException iae)
        {
            ; // Success
        }
        catch (Exception e)
        {
            fail("Expected IllegalArgumentException");
        }
    }

    
    /**
     * Tests file comparison by contents when either of the files does not
     * exist.
     * 
     * @throws Exception on error.
     */
    public void testCompareByContentsIsNotFile() throws Exception
    {
        logger_.info("Running testCompareByContentsIsNotFile...");
        
        File dir = FileUtil.createTempDir();
        FileUtil.setFileContents(fileB_, "fileB", false);
        FileComparator fc = new FileComparator(FileComparator.COMPARE_CONTENTS);
        
        try
        {
            fc.compare(dir, fileB_);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException iae)
        {
            ; // Success
        }
        catch (Exception e)
        {
            fail("Expected IllegalArgumentException");
        }
        
        // Switch args
        
        try
        {
            fc.compare(fileB_, dir);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException iae)
        {
            ; // Success
        }
        catch (Exception e)
        {
            fail("Expected IllegalArgumentException");
        }
        
        FileUtil.removeDir(dir);
    }
}