package toolbox.plugin.netmeter.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import toolbox.plugin.netmeter.Client;
import toolbox.plugin.netmeter.Server;
import toolbox.util.ThreadUtil;

/**
 * Unit test for NetMeter. 
 */
public class NetMeterTest extends TestCase
{
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
        Server server = new Server();
        server.start();
        
        Client client = new Client();
        client.start();
        
        ThreadUtil.sleep(10000);
        
        client.stop();
        server.stop();
    }
}