package toolbox.util.io.test;

import java.io.File;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.FileUtil;
import toolbox.util.StringUtil;
import toolbox.util.io.ReverseFileReader;

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
    // Unit Tests : readLine()
    //--------------------------------------------------------------------------

    /**
     * Tests readLine() on a zero byte file
     * 
     * @throws Exception
     */
    public void testReadLineEmptyFile() throws Exception
    {
        logger_.info("Running testReadLineEmptyFile...");
        
        String f = FileUtil.getTempFilename();
        
        try
        {
            FileUtil.setFileContents(f, "", false);
            ReverseFileReader rfr = new ReverseFileReader(new File(f));
            assertNull(rfr.readLine());
        }
        finally
        {
            FileUtil.delete(f);
        }
    }
    
    /**
     * Tests readLine() on a 1 byte file
     * 
     * @throws Exception
     */
    public void testReadLineOneByteFile() throws Exception
    {
        logger_.info("Running testReadLineOneByteFile...");
        
        String f = FileUtil.getTempFilename();
        
        try
        {
            FileUtil.setFileContents(f, "x", false);
            ReverseFileReader rfr = new ReverseFileReader(new File(f));
            assertEquals("x", rfr.readLine());
            assertNull(rfr.readLine());
        }
        finally
        {
            FileUtil.delete(f);
        }
    }
    
    /**
     * Test readLine() on a one line file
     * 
     * @throws Exception
     */
    public void testReadLineOneLineFile() throws Exception
    {
        logger_.info("Running testReadLineOneLineFile...");
        
        String f = FileUtil.getTempFilename();
        
        try
        {
            FileUtil.setFileContents(f, "howdy doody", false);
            ReverseFileReader rfr = new ReverseFileReader(new File(f));
            
            assertEquals(StringUtil.reverse("howdy doody"), rfr.readLine());
            assertNull(rfr.readLine());            
        }
        finally
        {
            FileUtil.delete(f);
        }
    }

    /**
     * Tests readLine() on a file with multiple lines
     * 
     * @throws Exception
     */
    public void testReadLineMultiLineFile() throws Exception
    {
        logger_.info("Running testReadLineMultiLineFile...");
        
        String f = FileUtil.getTempFilename();
        FileUtil.setFileContents(f, "one\ntwo\nthree\nfour\nfive", false);
        
        try
        {
            ReverseFileReader rfr = new ReverseFileReader(new File(f));

            assertEquals(StringUtil.reverse("five"), rfr.readLine());
            assertEquals(StringUtil.reverse("four"), rfr.readLine());
            assertEquals(StringUtil.reverse("three"), rfr.readLine());
            assertEquals(StringUtil.reverse("two"), rfr.readLine());
            assertEquals(StringUtil.reverse("one"), rfr.readLine());
            assertNull(rfr.readLine());
        }
        finally
        {            
            FileUtil.delete(f);
        }
    }

    /**
     * Tests readLine() on a file with multiple lines
     * 
     * @throws Exception
     */
    public void xtestReadLineNormalMultiLineFile() throws Exception
    {
        logger_.info("Running testReadLineMultiLineFile...");
        
        String f = FileUtil.getTempFilename();
        FileUtil.setFileContents(f, "one\ntwo\nthree\nfour\nfive", false);
        
        try
        {
            ReverseFileReader rfr = new ReverseFileReader(new File(f));

            assertEquals("five", rfr.readLine());
            assertEquals("four", rfr.readLine());
            assertEquals("three", rfr.readLine());
            assertEquals("two", rfr.readLine());
            assertEquals("one", rfr.readLine());
            assertNull(rfr.readLine());
        }
        finally
        {            
            FileUtil.delete(f);
        }
    }

    //--------------------------------------------------------------------------
    // Unit Tests: read()
    //--------------------------------------------------------------------------

    /**
     * Tests read() on a zero byte file
     * 
     * @throws Exception
     */
    public void testReadEmptyFile() throws Exception
    {
        logger_.info("Running testReadEmptyFile...");
        
        String f = FileUtil.getTempFilename();
        FileUtil.setFileContents(f, "", false);
        
        try
        {
            ReverseFileReader rfr = new ReverseFileReader(new File(f));
            assertEquals(-1, rfr.read());
        }
        finally
        {
            FileUtil.delete(f);
        }
    }

    /**
     * Tests read() on a one byte file
     * 
     * @throws Exception
     */
    public void testReadOneByteFile() throws Exception
    {
        logger_.info("Running testReadOneByteFile...");
        
        String f = FileUtil.getTempFilename();
        FileUtil.setFileContents(f, "x", false);
        
        try
        {
            ReverseFileReader rfr = new ReverseFileReader(new File(f));
            assertEquals('x', rfr.read());
            assertEquals(-1, rfr.read());
        }
        finally
        {
            FileUtil.delete(f);
        }
    }

}