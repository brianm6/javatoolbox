package toolbox.plugin.netmeter.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import toolbox.plugin.netmeter.Client;
import toolbox.plugin.netmeter.Server;

/**
 * Unit test for NetMeter. 
 */
public class NetMeterTest extends TestCase
{
    //--------------------------------------------------------------------------
    // Main 
    //--------------------------------------------------------------------------
    
    public static void main(String args[])
    {
        TestRunner.run(NetMeterTest.class);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    public void testNetMeter() throws Exception
    {
        Server server = new Server();
        server.start();
        
        Client client = new Client();
        client.start();
    }
}
