package toolbox.util.file.test;

import java.io.File;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.FileUtil;
import toolbox.util.file.FileComparator;

/**
 * Unit test for FileComparator.
 */
public class FileComparatorTest extends TestCase
{
    private static final Logger logger_ =
        Logger.getLogger(FileComparatorTest.class);
    
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
     * Tests the constructor for overflow condition.
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
        
        File fileA = new File(FileUtil.getTempDir(), "a");
        File fileB = new File(FileUtil.getTempDir(), "b");
            
        try
        {
            fileA.createNewFile();
            fileB.createNewFile();
            
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
        
        File fileA = FileUtil.createTempFile();
        File fileB = FileUtil.createTempFile();
        
        try
        {
            FileUtil.setFileContents(fileA, "smaller", false);
            FileUtil.setFileContents(fileB, "l a r g e r", false);
            
            FileComparator fc = new FileComparator(FileComparator.COMPARE_SIZE);
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
     * Tests file comparison by timestamp.
     * 
     * @throws Exception on error.
     */
    public void testCompareByTimestamp() throws Exception
    {
        logger_.info("Running testCompareByTimestamp...");
        
        File fileA = FileUtil.createTempFile();
        File fileB = FileUtil.createTempFile();
        
        try
        {
            FileUtil.setFileContents(fileA, "smaller", false);
            FileUtil.setFileContents(fileB, "l a r g e r", false);
            
            FileComparator fc = new FileComparator(FileComparator.COMPARE_SIZE);
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
     * Tests file comparison by contents.
     * 
     * @throws Exception on error.
     */
    public void testCompareByContents() throws Exception
    {
        logger_.info("Running testCompareByContents...");
        
        File fileA = FileUtil.createTempFile();
        File fileB = FileUtil.createTempFile();
        File fileC = FileUtil.createTempFile();
        File fileD = FileUtil.createTempFile();
        
        try
        {
            FileUtil.setFileContents(fileA, "this_file_should_match", false);
            FileUtil.setFileContents(fileB, "this_file_should_match", false);
            FileUtil.setFileContents(fileC, "yabba dabba doo,i see you!",false);
            FileUtil.setFileContents(fileD, "abcdefgijklmnopq", false);
            
            FileComparator fc = 
                new FileComparator(FileComparator.COMPARE_CONTENTS);
            
            assertTrue(fc.compare(fileA, fileA) == 0);
            assertTrue(fc.compare(fileA, fileB) == 0);
            assertTrue(fc.compare(fileB, fileA) == 0);
            assertTrue(fc.compare(fileA, fileC) < 0);
            assertTrue(fc.compare(fileA, fileD) > 0);
        }
        finally
        {
            FileUtil.delete(fileA);
            FileUtil.delete(fileB);
            FileUtil.delete(fileC);
            FileUtil.delete(fileD);
        }
    }
}