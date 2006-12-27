package toolbox.ip2hostname;

import java.io.StringReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

public class IP2HostnameReaderTest extends TestCase {

    private static final Logger logger = 
        Logger.getLogger(IP2HostnameReaderTest.class);

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------
    
    private static final String TEST_IP;
    private static final String TEST_HOSTNAME;
    
    // -------------------------------------------------------------------------
    // Static Blocks
    // -------------------------------------------------------------------------
    
    static {
        
        InetAddress yahooInet = null;
        
        try {
            yahooInet = InetAddress.getByName("www.yahoo.com");
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        }
        
        TEST_IP = yahooInet.getHostAddress();
        TEST_HOSTNAME = yahooInet.getCanonicalHostName();
        
        logger.info("IP_YAHOO = " + TEST_IP);
        logger.info("HOSTNAME_YAHOO = " + TEST_HOSTNAME);
    }

    // -------------------------------------------------------------------------
    // Main
    // -------------------------------------------------------------------------
    
    public static void main(String[] args) {
        TestRunner.run(IP2HostnameReaderTest.class);
    }

    // -------------------------------------------------------------------------
    // Unit Tests
    // -------------------------------------------------------------------------
    
    public void testEmptyString() throws Exception {
        logger.info("Running testEmptyString...");
        
        String input = "";
        IP2HostnameReader reader = new IP2HostnameReader(new StringReader(input));
        String output = reader.readLine();
        logger.debug("Output: " + output);
        assertNull(output);
    }

    public void testEmptyLine() throws Exception {
        logger.info("Running testEmptyLine...");
        
        String input = "\n";
        IP2HostnameReader reader = new IP2HostnameReader(new StringReader(input));
        String output = reader.readLine();
        logger.debug("Output: " + output);
        assertEquals("", output);
    }
    
    public void testHostnameOnly() throws Exception {
        logger.info("Running testHostnameOnly ...");
        
        String input = TEST_IP;
        IP2HostnameReader reader = new IP2HostnameReader(new StringReader(input));
        String output = reader.readLine();
        logger.debug("Output: " + output);
        assertNotSame(TEST_HOSTNAME, output);
    }
    
    public void testIPOnly() throws Exception {
        logger.info("Running testIPOnly ...");
        
        String input = "172.18.92.1";
        IP2HostnameReader reader = new IP2HostnameReader(new StringReader(input));
        String output = reader.readLine();
        logger.debug("Output: " + output);
        assertEquals(input, output);
    }

    public void testBack2BackIPs() throws Exception {
        logger.info("Running testBack2BackIPs ...");
        
        String input = TEST_IP + " " + TEST_IP + "\n";
        IP2HostnameReader reader = new IP2HostnameReader(new StringReader(input));
        String output = reader.readLine();
        logger.debug("Output: " + output);
        assertEquals(TEST_HOSTNAME + " " + TEST_HOSTNAME, output);
        reader.close();
    }
    
    public void testSameIPAndHostname() throws Exception {
        logger.info("Running testSameIPAndHostname ...");
        
        String input = TEST_IP + " " + TEST_HOSTNAME + "\n";
        IP2HostnameReader reader = new IP2HostnameReader(new StringReader(input));
        String output = reader.readLine();
        logger.debug("Output: " + output);
        assertEquals(TEST_HOSTNAME + " " + TEST_HOSTNAME, output);
        reader.close();
    }
    
    public void testMultiline() throws Exception {
        logger.info("Running testSameIPAndHostname ...");
        
        String input = TEST_IP + "\n" + TEST_IP + "\n";
        IP2HostnameReader reader = new IP2HostnameReader(new StringReader(input));
        String output1 = reader.readLine();
        String output2 = reader.readLine();
        logger.debug("Output: " + output1 + " "  + output2);
        assertEquals(TEST_HOSTNAME, output1);
        assertEquals(TEST_HOSTNAME, output2);
        reader.close();
    }
}
