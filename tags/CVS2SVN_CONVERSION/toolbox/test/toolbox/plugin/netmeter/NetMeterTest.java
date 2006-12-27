package toolbox.plugin.netmeter;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;

import toolbox.util.ThreadUtil;

/**
 * Unit test for {@link toolbox.plugin.netmeter.Client} and 
 * {@link toolbox.plugin.netmeter.Server}. 
 */
public class NetMeterTest extends TestCase
{
    private static final Logger logger_ = Logger.getLogger(NetMeterTest.class);
    
    //--------------------------------------------------------------------------
    // Main 
    //--------------------------------------------------------------------------
    
    /**
     * Entrypoint. 
     * 
     * @param args None recognized.
     */
    public static void main(String args[])
    {
        TestRunner.run(NetMeterTest.class);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Simple test of non-UI Client connecting to a non-UI server.
     * 
     * @throws Exception on error.
     */
    public void testNetMeter() throws Exception
    {
        logger_.info("Running testNetMeter...");
        
        Server server = new Server();
        server.initialize(MapUtils.EMPTY_MAP);
        server.start();
        
        Client client = new Client();
        client.initialize(MapUtils.EMPTY_MAP);
        client.start();
        
        ThreadUtil.sleep(10000);
        
        client.stop();
        server.stop();
    }
}