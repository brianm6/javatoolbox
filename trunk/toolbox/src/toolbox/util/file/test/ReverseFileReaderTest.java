package toolbox.util.file.test;

import java.io.File;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.FileUtil;
import toolbox.util.file.ReverseFileReader;

/**
 * Unit Test for ReverseFileReader
 */
public class ReverseFileReaderTest extends TestCase
{
    private static final Logger logger_ =
        Logger.getLogger(ReverseFileReaderTest.class);
        
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
        TestRunner.run(ReverseFileReaderTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------

    /**
     * Tests reading reverse from a zero byte file
     * 
     * @throws Exception
     */
    public void testReadPreviousLineEmptyFile() throws Exception
    {
        logger_.info("Running testReadPreviousLineEmptyFile...");
        
        String f = FileUtil.getTempFilename();
        
        try
        {
            FileUtil.setFileContents(f, "", false);
            ReverseFileReader rfr = new ReverseFileReader(new File(f));
            assertNull(rfr.readPreviousLine());
        }
        finally
        {
            FileUtil.delete(f);
        }
    }
    
    /**
     * Tests reading reverse from a 1 byte file
     * 
     * @throws Exception
     */
    public void testReadPreviousLineOneByteFile() throws Exception
    {
        logger_.info("Running testReadPreviousLineOneByteFile...");
        
        String f = FileUtil.getTempFilename();
        
        try
        {
            FileUtil.setFileContents(f, "x", false);
            ReverseFileReader rfr = new ReverseFileReader(new File(f));
            assertEquals("x", rfr.readPreviousLine());
            assertNull(rfr.readPreviousLine());
        }
        finally
        {
            FileUtil.delete(f);
        }
    }
    
    /**
     * Tests reading reverse from a file with only one line
     * 
     * @throws Exception
     */
    public void testReadPreviousLineOneLineFile() throws Exception
    {
        logger_.info("Running testReadPreviousLineOneLineFile...");
        
        String f = FileUtil.getTempFilename();
        
        try
        {
            FileUtil.setFileContents(f, "howdy doody", false);
            ReverseFileReader rfr = new ReverseFileReader(new File(f));
            assertEquals("howdy doody", rfr.readPreviousLine());
            assertNull(rfr.readPreviousLine());            
        }
        finally
        {
            FileUtil.delete(f);
        }
    }

    /**
     * Tests reading reverse from a file with multiple lines
     * 
     * @throws Exception
     */
    public void testReadPreviousLineMultiLineFile() throws Exception
    {
        logger_.info("Running testReadPreviousLineMultiLineFile...");
        
        String f = FileUtil.getTempFilename();
        FileUtil.setFileContents(f, "one\ntwo\nthree\nfour\nfive", false);
        
        try
        {
            ReverseFileReader rfr = new ReverseFileReader(new File(f));

            assertEquals("five", rfr.readPreviousLine());
            assertEquals("four", rfr.readPreviousLine());
            assertEquals("three", rfr.readPreviousLine());
            assertEquals("two", rfr.readPreviousLine());
            assertEquals("one", rfr.readPreviousLine());
            assertNull(rfr.readPreviousLine());
        }
        finally
        {            
            FileUtil.delete(f);
        }
    }

}