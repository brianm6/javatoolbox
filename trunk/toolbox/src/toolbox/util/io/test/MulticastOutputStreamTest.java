package toolbox.util.io.test;

import java.io.OutputStream;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.io.MulticastOutputStream;
import toolbox.util.io.StringOutputStream;

/**
 * Unit test for MulticastOutputStream
 */
public class MulticastOutputStreamTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(MulticastOutputStreamTest.class);
        
    /**
     * Entrypoint
     * 
     * @param  args None
     */
    public static void main(String[] args)
    {
        TestRunner.run(MulticastOutputStreamTest.class);
    }
    
    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
        
    /**
     * Constructor for MulticastOutputStreamTest.
     * 
     * @param  arg0  Test name
     */
    public MulticastOutputStreamTest(String arg0)
    {
        super(arg0);
    }
    
    //--------------------------------------------------------------------------
    //  Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests write(byte[])
     * 
     * @throws Exception on error
     */
    public void testWrite() throws Exception
    {
        logger_.info("Running testWrite...");
        
        String testString = "hello";
        
        StringOutputStream[] streams = new StringOutputStream[10];
        MulticastOutputStream mos = new MulticastOutputStream();
                
        for(int i=0; i<streams.length; i++)
        {
            streams[i] = new StringOutputStream();
            mos.addStream(streams[i]);
        }
            
        mos.write(testString.getBytes());

        for (int i=0; i<streams.length; i++)
            assertEquals(testString, streams[i].getBuffer().toString());
    }

    /**
     * Tests write(byte[], offset, length)
     * 
     * @throws Exception on error
     */
    public void testWrite2() throws Exception
    {
        logger_.info("Running testWrite2...");
        
        String testString = "write2";
        
        StringOutputStream[] streams = new StringOutputStream[10];
        MulticastOutputStream mos = new MulticastOutputStream();
                
        for(int i=0; i<streams.length; i++)
        {
            streams[i] = new StringOutputStream();
            mos.addStream(streams[i]);
        }
            
        mos.write(testString.getBytes(), 0, testString.length());

        for (int i=0; i<streams.length; i++)
        {
            assertEquals(testString, streams[i].getBuffer().toString());
            mos.removeStream(streams[i]);
        }
    }
    
    /**
     * Tests close()
     * 
     * @throws Exception on error
     */
    public void testClose() throws Exception
    {
        logger_.info("Running testClose...");
        
        String testString = "hello";
        
        StringOutputStream[] streams = new StringOutputStream[10];
        MulticastOutputStream mos = new MulticastOutputStream();
                
        for(int i=0; i<streams.length; i++)
        {
            streams[i] = new StringOutputStream();
            mos.addStream(streams[i]);
        }
            
        mos.close();
    }
    
    /**
     * Tests flush()
     * 
     * @throws Exception on error
     */
    public void testFlush() throws Exception
    {
        logger_.info("Running testFlush...");
        
        String testString = "hello";
        
        StringOutputStream[] streams = new StringOutputStream[10];
        MulticastOutputStream mos = new MulticastOutputStream();
                
        for(int i=0; i<streams.length; i++)
        {
            streams[i] = new StringOutputStream();
            mos.addStream(streams[i]);
        }

        mos.write("boo".getBytes());            
        mos.flush();
    }
}
