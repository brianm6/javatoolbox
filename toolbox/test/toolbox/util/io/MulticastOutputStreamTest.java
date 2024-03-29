package toolbox.util.io;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

/**
 * Unit test for {@link toolbox.util.io.MulticastOutputStream}.
 */
public class MulticastOutputStreamTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(MulticastOutputStreamTest.class);

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
        TestRunner.run(MulticastOutputStreamTest.class);
    }
    
    //--------------------------------------------------------------------------
    //  Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests the constructors.
     */
    public void testConstructors()
    {
        logger_.info("Running testConstructors...");
        
        MulticastOutputStream mos = 
            new MulticastOutputStream();
            
        MulticastOutputStream mos2 = 
            new MulticastOutputStream(new StringOutputStream());
            
        assertNotNull(mos);
        assertNotNull(mos2);
    }
    
    
    /**
     * Tests write(byte[]).
     * 
     * @throws Exception on error.
     */
    public void testWrite() throws Exception
    {
        logger_.info("Running testWrite...");
        
        String testString = "hello";
        
        StringOutputStream[] streams = new StringOutputStream[10];
        MulticastOutputStream mos = new MulticastOutputStream();
                
        for (int i = 0; i < streams.length; i++)
        {
            streams[i] = new StringOutputStream();
            mos.addStream(streams[i]);
        }

        mos.write(testString.getBytes());
        mos.close();

        for (int i = 0; i < streams.length; i++)
            assertEquals(testString, streams[i].getBuffer().toString());
    }

    
    /**
     * Tests write(byte[], offset, length).
     * 
     * @throws Exception on error.
     */
    public void testWrite2() throws Exception
    {
        logger_.info("Running testWrite2...");
        
        String testString = "write2";
        
        StringOutputStream[] streams = new StringOutputStream[10];
        MulticastOutputStream mos = new MulticastOutputStream();
                
        for (int i = 0; i < streams.length; i++)
        {
            streams[i] = new StringOutputStream();
            mos.addStream(streams[i]);
        }
            
        mos.write(testString.getBytes(), 0, testString.length());
        mos.close();

        for (int i = 0; i < streams.length; i++)
        {
            assertEquals(testString, streams[i].getBuffer().toString());
            mos.removeStream(streams[i]);
        }
    }

    
    /**
     * Tests write(int).
     * 
     * @throws Exception on error.
     */
    public void testWrite3() throws Exception
    {
        logger_.info("Running testWrite3...");
        
        StringOutputStream[] streams = new StringOutputStream[10];
        MulticastOutputStream mos = new MulticastOutputStream();
                
        for (int i = 0; i < streams.length; i++)
        {
            streams[i] = new StringOutputStream();
            mos.addStream(streams[i]);
        }
            
        mos.write(0);
    }
    
    
    /**
     * Tests close().
     * 
     * @throws Exception on error.
     */
    public void testClose() throws Exception
    {
        logger_.info("Running testClose...");
        
        StringOutputStream[] streams = new StringOutputStream[10];
        MulticastOutputStream mos = new MulticastOutputStream();
                
        for (int i = 0; i < streams.length; i++)
        {
            streams[i] = new StringOutputStream();
            mos.addStream(streams[i]);
        }
            
        mos.close();
    }
    
    
    /**
     * Tests flush().
     * 
     * @throws Exception on error.
     */
    public void testFlush() throws Exception
    {
        logger_.info("Running testFlush...");
        
        StringOutputStream[] streams = new StringOutputStream[10];
        MulticastOutputStream mos = new MulticastOutputStream();
                
        for (int i = 0; i < streams.length; i++)
        {
            streams[i] = new StringOutputStream();
            mos.addStream(streams[i]);
        }

        mos.write("boo".getBytes());            
        mos.flush();
    }
}