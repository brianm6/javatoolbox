package toolbox.util.io;

import java.io.File;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import toolbox.util.FileUtil;

/**
 * Unit test for {@link toolbox.util.io.ReverseFileReader}.
 */
public class ReverseFileReaderTest extends TestCase
{
    private static final Logger logger_ =
        Logger.getLogger(ReverseFileReaderTest.class);
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Temp file initialized and reset for each test.
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
        IOUtils.closeQuietly(reader_);   
        FileUtil.deleteQuietly(file_);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests : readLine()
    //--------------------------------------------------------------------------

    /**
     * Tests readLine() on a zero byte file.
     * 
     * @throws Exception on error.
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
     * @throws Exception on error.
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
     * @throws Exception on error.
     */
    public void testReadLineOneLineFile() throws Exception
    {
        logger_.info("Running testReadLineOneLineFile...");
        
        FileUtil.setFileContents(file_, "howdy doody", false);
        reader_ = new ReverseFileReader(file_);
        
        assertEquals(StringUtils.reverse("howdy doody"), reader_.readLine());
        assertNull(reader_.readLine());            
    }

    
    /**
     * Tests readLine() on a file with multiple lines.
     * 
     * @throws Exception on error.
     */
    public void testReadLineMultiLineFile() throws Exception
    {
        logger_.info("Running testReadLineMultiLineFile...");
        
        FileUtil.setFileContents(file_, "one\ntwo\nthree\nfour\nfive", false);
        
        reader_ = new ReverseFileReader(file_);

        assertEquals(StringUtils.reverse("five"), reader_.readLine());
        assertEquals(StringUtils.reverse("four"), reader_.readLine());
        assertEquals(StringUtils.reverse("three"), reader_.readLine());
        assertEquals(StringUtils.reverse("two"), reader_.readLine());
        assertEquals(StringUtils.reverse("one"), reader_.readLine());
        assertNull(reader_.readLine());
    }

    //--------------------------------------------------------------------------
    // Unit Tests : readLineNormal()
    //--------------------------------------------------------------------------

    /**
     * Tests readLineNormal() on a zero byte file.
     * 
     * @throws Exception on error.
     */
    public void testReadLineNormalEmptyFile() throws Exception
    {
        logger_.info("Running testReadLineNormalEmptyFile...");
        
        FileUtil.setFileContents(file_, "", false);
        reader_ = new ReverseFileReader(file_);
        assertNull(reader_.readLineNormal());
    }

    
    /**
     * Tests readLineNormal() on a 1 byte file.
     * 
     * @throws Exception on error.
     */
    public void testReadLineNormalOneByteFile() throws Exception
    {
        logger_.info("Running testReadLineNormalOneByteFile...");
        
        FileUtil.setFileContents(file_, "a", false);
        reader_ = new ReverseFileReader(file_);
        assertEquals("a", reader_.readLineNormal());
        assertNull(reader_.readLineNormal());
    }
    
    
    /**
     * Test readLineNormal() on a one line file.
     * 
     * @throws Exception on error.
     */
    public void testReadLineNormalOneLineFile() throws Exception
    {
        logger_.info("Running testReadLineNormalOneLineFile...");
        
        FileUtil.setFileContents(file_, "howdy doody", false);
        reader_ = new ReverseFileReader(file_);
        assertEquals("howdy doody", reader_.readLineNormal());
        assertNull(reader_.readLine());            
    }

    
    /**
     * Tests readLineNormal() on a file with multiple lines.
     * 
     * @throws Exception on error.
     */
    public void testReadLineNormalMultiLineFile() throws Exception
    {
        logger_.info("Running testReadLineNormalMultiLineFile...");
        
        FileUtil.setFileContents(file_, "one\ntwo\nthree\nfour\nfive", false);
        reader_ = new ReverseFileReader(file_);

        assertEquals("five", reader_.readLineNormal());
        assertEquals("four", reader_.readLineNormal());
        assertEquals("three", reader_.readLineNormal());
        assertEquals("two", reader_.readLineNormal());
        assertEquals("one", reader_.readLineNormal());
        assertNull(reader_.readLine());
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests: read()
    //--------------------------------------------------------------------------

    /**
     * Tests read() on a zero byte file.
     * 
     * @throws Exception on error.
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
     * @throws Exception on error.
     */
    public void testReadOneByteFile() throws Exception
    {
        logger_.info("Running testReadOneByteFile...");
        
        FileUtil.setFileContents(file_, "b", false);
        reader_ = new ReverseFileReader(file_);
        assertEquals('b', reader_.read());
        assertEquals(-1, reader_.read());
    }
    
    
    /**
     * Tests read(char[])
     * 
     * @throws Exception on error.
     */
    public void testReadCharArray() throws Exception
    {
        logger_.info("Running testReadCharArray...");
        
        String input = "abcdefg";
        String expected = StringUtils.reverse(input);
        FileUtil.setFileContents(file_, input, false);
        reader_ = new ReverseFileReader(file_);
        char[] output = new char[input.length()];
        assertEquals(input.length(), reader_.read(output));
        assertEquals(expected, new String(output));
    }
    

    /**
     * Tests read(char[]) for buffer larger than the available data.
     * 
     * @throws Exception on error.
     */
    public void testReadCharArrayUnderFlow() throws Exception
    {
        logger_.info("Running testReadCharArrayUnderFlow...");
        
        String input = "abcdefg";
        String expected = StringUtils.reverse(input);
        FileUtil.setFileContents(file_, input, false);
        reader_ = new ReverseFileReader(file_);
        
        // Alloc array and space out
        char[] output = new char[100];
        for (int i = 0; i < output.length; output[i++] = ' ');
        
        // Reader should overwrite 
        assertEquals(input.length(), reader_.read(output));
        
        // Trim (nuke spaces) and we should have expected
        assertEquals(expected, new String(output).trim());
    }

    
    /**
     * Tests read(char[]) for file bigger than the buffer.
     * 
     * @throws Exception on error.
     */
    public void testReadCharArrayOverFlow() throws Exception
    {
        logger_.info("Running testReadCharArrayOverFlow...");
        
        String input = "abcdefg";
        FileUtil.setFileContents(file_, input, false);
        reader_ = new ReverseFileReader(file_);
        
        char[] chunk1 = new char[3];
        char[] chunk2 = new char[4];
        
        assertEquals(3, reader_.read(chunk1));
        assertEquals("gfe", new String(chunk1));
        
        assertEquals(4, reader_.read(chunk2));
        assertEquals("dcba", new String(chunk2));
    }
    

    /**
     * Tests read(char[], int off, int len)
     * 
     * @throws Exception on error.
     */
    public void testReadCharArrayOffsetLength() throws Exception
    {
        logger_.info("Running testReadCharArrayOffsetLength...");
        
        String input = "abcdefg";
        String expected = StringUtils.reverse(input);
        FileUtil.setFileContents(file_, input, false);
        reader_ = new ReverseFileReader(file_);
        char[] output = new char[input.length()];
        assertEquals(input.length(), reader_.read(output, 0, input.length()));
        assertEquals(expected, new String(output));
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests: skip()
    //--------------------------------------------------------------------------

    /**
     * Tests skip() for a negative value.
     * 
     * @throws Exception on error.
     */
    public void testSkipNegative() throws Exception
    {
        logger_.info("Running testSkipNegative...");
        
        FileUtil.setFileContents(file_, "xyz", false);
        reader_ = new ReverseFileReader(file_);
        
        try
        {
            reader_.skip(-34);
            fail("Cskip must be positive");
        }
        catch (IllegalArgumentException iae)
        {
            ; // Success
        }
    }

    
    /**
     * Tests skip() on a zero byte file.
     * 
     * @throws Exception on error.
     */
    public void testSkipEmptyFile() throws Exception
    {
        logger_.info("Running testSkipEmptyFile...");
        
        FileUtil.setFileContents(file_, "", false);
        reader_ = new ReverseFileReader(file_);
        assertEquals(0, reader_.skip(100));
    }
    

    /**
     * Tests skip() on a 1 byte file.
     * 
     * @throws Exception on error.
     */
    public void testSkipOneByteFile() throws Exception
    {
        logger_.info("Running testSkipOneByteFile...");
        
        FileUtil.setFileContents(file_, "a", false);
        reader_ = new ReverseFileReader(file_);
        assertEquals(1, reader_.skip(1));
        assertEquals(0, reader_.skip(999));
    }

    
    /**
     * Tests skip() past beginning on 1 byte file.
     * 
     * @throws Exception on error.
     */
    public void testSkipOneByteFilePastBeginning() throws Exception
    {
        logger_.info("Running testSkipOneByteFilePastBeginning...");
        
        FileUtil.setFileContents(file_, "a", false);
        reader_ = new ReverseFileReader(file_);
        assertEquals(1, reader_.skip(999));
    }

    
    /**
     * Tests skip() on a single line.
     * 
     * @throws Exception on error.
     */
    public void testSkipOneLineFile() throws Exception
    {
        logger_.info("Running testSkipOneLineFile...");
        
        FileUtil.setFileContents(file_, " 0 1 2 3 4 5 6", false);
        reader_ = new ReverseFileReader(file_);
        
        int ch;
        StringBuffer sb = new StringBuffer();
        
        while ((ch = reader_.read()) != -1)
        {
            sb.append((char) ch);
            reader_.skip(1);
        }
        
        assertEquals("6543210", sb.toString());
    }

    
    /**
     * Tests skip() on a multiline file.
     * 
     * @throws Exception on error.
     */
    public void testSkipMultiLineFile() throws Exception
    {
        logger_.info("Running testSkipMultiLineFile...");
        
        FileUtil.setFileContents(file_, "..0..1..2\n.3..4..5\n.6", false);
        reader_ = new ReverseFileReader(file_);
        
        int ch;
        StringBuffer sb = new StringBuffer();
        
        while ((ch = reader_.read()) != -1)
        {
            sb.append((char) ch);
            reader_.skip(2);
        }
        
        assertEquals("6543210", sb.toString());
    }
}