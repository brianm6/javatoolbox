package toolbox.plugin.netmeter;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

/**
 * Unit test for Client.
 * 
 * @see toolbox.plugin.netmeter.Client
 */
public class ClientTest extends TestCase
{
    private static final Logger logger_ = Logger.getLogger(ClientTest.class);
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args)
    {
        TestRunner.run(ClientTest.class);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    public void testInvalidTransitions() throws Exception
    {
        logger_.info("Running testInvalidTransitions...");
        
        Client c = new Client();
        
        try
        {
            c.start();
        }
        catch (IllegalStateException ise)
        {
            logger_.debug("SUCCESS: " + ise);
        }
        
        try
        {
            c.stop();
        }
        catch (IllegalStateException ise)
        {
            logger_.debug("SUCCESS: " + ise);
        }
        
        try
        {
            c.destroy();
        }
        catch (IllegalStateException ise)
        {
            logger_.debug("SUCCESS: " + ise);
        }
        
        try
        {
            c.suspend();
        }
        catch (IllegalStateException ise)
        {
            logger_.debug("SUCCESS: " + ise);
        }
    }
}
