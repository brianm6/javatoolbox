package toolbox.util.io.test;

import java.io.File;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.FileUtil;
import toolbox.util.StreamUtil;
import toolbox.util.StringUtil;
import toolbox.util.io.ReverseFileReader;

/**
 * Unit test for ReverseFileReader.
 */
public class ReverseFileReaderTest extends TestCase
{
    private static final Logger logger_ =
        Logger.getLogger(ReverseFileReaderTest.class);
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Temp file initialized and reset in for each test.
     */
    private File file_;
    
    /**
     * Reverse file reader attached to the temp file that is initialized and
     * reset for each test.
     */
    private ReverseFileReader reader_;
 
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
        TestRunner.run(ReverseFileReaderTest.class);
    }

    //--------------------------------------------------------------------------
    // Overrides TestCase
    //--------------------------------------------------------------------------
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        file_ = FileUtil.createTempFile();
        reader_ = null;
    }
    
    
    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        StreamUtil.close(reader_);   
        FileUtil.delete(file_);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests : readLine()
    //--------------------------------------------------------------------------

    /**
     * Tests readLine() on a zero byte file.
     * 
     * @throws Exception on error
     */
    public void testReadLineEmptyFile() throws Exception
    {
        logger_.info("Running testReadLineEmptyFile...");
        
        FileUtil.setFileContents(file_, "", false);
        reader_ = new ReverseFileReader(file_);
        assertNull(reader_.readLine());
    }
    
    
    /**
     * Tests readLine() on a 1 byte file.
     * 
     * @throws Exception on error
     */
    public void testReadLineOneByteFile() throws Exception
    {
        logger_.info("Running testReadLineOneByteFile...");
        
        FileUtil.setFileContents(file_, "a", false);
        reader_ = new ReverseFileReader(file_);
        assertEquals("a", reader_.readLine());
        assertNull(reader_.readLine());
    }
    
    
    /**
     * Test readLine() on a one line file.
     * 
     * @throws Exception on error
     */
    public void testReadLineOneLineFile() throws Exception
    {
        logger_.info("Running testReadLineOneLineFile...");
        
        FileUtil.setFileContents(file_, "howdy doody", false);
        reader_ = new ReverseFileReader(file_);
        
        assertEquals(StringUtil.reverse("howdy doody"), reader_.readLine());
        assertNull(reader_.readLine());            
    }

    
    /**
     * Tests readLine() on a file with multiple lines.
     * 
     * @throws Exception on error
     */
    public void testReadLineMultiLineFile() throws Exception
    {
        logger_.info("Running testReadLineMultiLineFile...");
        
        FileUtil.setFileContents(file_, "one\ntwo\nthree\nfour\nfive", false);
        
        reader_ = new ReverseFileReader(file_);

        assertEquals(StringUtil.reverse("five"), reader_.readLine());
        assertEquals(StringUtil.reverse("four"), reader_.readLine());
        assertEquals(StringUtil.reverse("three"), reader_.readLine());
        assertEquals(StringUtil.reverse("two"), reader_.readLine());
        assertEquals(StringUtil.reverse("one"), reader_.readLine());
        assertNull(reader_.readLine());
    }

    
    /**
     * Tests readLine() on a file with multiple lines.
     * 
     * @throws Exception on error
     */
    public void xtestReadLineNormalMultiLineFile() throws Exception
    {
        logger_.info("Running testReadLineMultiLineFile...");
        
        FileUtil.setFileContents(file_, "one\ntwo\nthree\nfour\nfive", false);
        reader_ = new ReverseFileReader(file_);

        assertEquals("five", reader_.readLine());
        assertEquals("four", reader_.readLine());
        assertEquals("three", reader_.readLine());
        assertEquals("two", reader_.readLine());
        assertEquals("one", reader_.readLine());
        assertNull(reader_.readLine());
    }

    //--------------------------------------------------------------------------
    // Unit Tests: read()
    //--------------------------------------------------------------------------

    /**
     * Tests read() on a zero byte file.
     * 
     * @throws Exception on error
     */
    public void testReadEmptyFile() throws Exception
    {
        logger_.info("Running testReadEmptyFile...");
        
        FileUtil.setFileContents(file_, "", false);
        reader_ = new ReverseFileReader(file_);
        assertEquals(-1, reader_.read());
    }
           
    
    /**
     * Tests read() on a one byte file.
     * 
     * @throws Exception on error
     */
    public void testReadOneByteFile() throws Exception
    {
        logger_.info("Running testReadOneByteFile...");
        
        FileUtil.setFileContents(file_, "b", false);
        reader_ = new ReverseFileReader(file_);
        assertEquals('b', reader_.read());
        assertEquals(-1, reader_.read());
    }
}