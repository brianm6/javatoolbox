package toolbox.util.io.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.io.CountingOutputStream;
import toolbox.util.io.StringOutputStream;

/**
 * Unit test for CountingOutputStream
 */
public class CountingOutputStreamTest extends TestCase
{
    private static final Logger logger_ =
        Logger.getLogger(CountingOutputStreamTest.class);
        
    /**
     * Entrypoint   
     * 
     * @param  args  None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(CountingOutputStreamTest.class);
    }
    
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Constructor for CountingOutputStreamTest
     * 
     * @param  arg0  Name
     */
    public CountingOutputStreamTest(String arg0)
    {
        super(arg0);
    }

    //--------------------------------------------------------------------------
    //  Unit Tests
    //--------------------------------------------------------------------------
       
    /**
     * Tests the constructors
     */
    public void testConstructors()
    {
        logger_.info("Running testConstructors...");
        
        CountingOutputStream cos = 
            new CountingOutputStream(new StringOutputStream());
        
        assertNotNull(cos);
        assertEquals(0, cos.getCount());
    }
        
    /**
     * Tests the write(byte) method
     * 
     * @throws Exception on error
     */
    public void testWriteByte() throws Exception
    {
        logger_.info("Running testWriteByte...");
        
        StringOutputStream sos = new StringOutputStream();
        CountingOutputStream cos = new CountingOutputStream(sos);

        assertEquals(0, cos.getCount());        
        cos.write("x".getBytes()[0]);
        cos.flush();
        assertEquals("strings don't match", "x", sos.toString());
        assertEquals(1, cos.getCount());
    }
    
    /**
     * Tests the write(byte[]) method
     * 
     * @throws Exception on error
     */
    public void testWriteByteArray() throws Exception
    {
        logger_.info("Running testWriteByteArray...");

        StringOutputStream sos = new StringOutputStream();       
        CountingOutputStream cos = new CountingOutputStream(sos);

        assertEquals(0, cos.getCount());
        cos.write("hello".getBytes());
        cos.flush();
        assertEquals("strings don't match", "hello", sos.toString());
        assertEquals("hello".length(), cos.getCount());
    }
    
    /**
     * Tests the write(byte[], begin, len) method
     * 
     * @throws Exception on error
     */
    public void testWriteByteArraySubset() throws Exception
    {
        logger_.info("Running testWriteByteArraySubset...");

        StringOutputStream sos = new StringOutputStream();       
        CountingOutputStream cos = new CountingOutputStream(sos);

        assertEquals(0, cos.getCount());
        cos.write("hello".getBytes(), 2, 2);
        cos.flush();
        assertEquals("strings don't match", "hello", sos.toString());
        assertEquals("hello".length(), cos.getCount());
    }
}