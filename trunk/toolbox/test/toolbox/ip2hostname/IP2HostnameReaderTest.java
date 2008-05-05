package toolbox.ip2hostname;

import java.io.StringReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

public class IP2HostnameReaderTest extends TestCase {

    private static final Logger logger = Logger.getLogger(IP2HostnameReaderTest.class);

    // -------------------------------------------------------------------------
    // Static Fields
    // -------------------------------------------------------------------------
    
    private static String TEST_IP;
    private static String TEST_HOSTNAME;
    private static boolean skipTest = false;
    
    // -------------------------------------------------------------------------
    // Static Block
    // -------------------------------------------------------------------------
    
    static {
        
        InetAddress googleInet = null;
        
        try {
            googleInet = InetAddress.getByName("www.google.com");
            TEST_IP = googleInet.getHostAddress();
            TEST_HOSTNAME = googleInet.getCanonicalHostName();
            logger.info("IP_GOOGLE = " + TEST_IP);
            logger.info("HOSTNAME_GOOGLE = " + TEST_HOSTNAME);
            skipTest = false;
        }
        catch (UnknownHostException e) {
        	skipTest = true;
        }
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
        
        if (skipTest)
        	return;
        
        String input = "";
        IP2HostnameReader reader = new IP2HostnameReader(new StringReader(input), new DnsHostnameResolver());
        String output = reader.readLine();
        logger.debug("Output: " + output);
        assertNull(output);
    }

    public void testEmptyLine() throws Exception {
        logger.info("Running testEmptyLine...");
        
        if (skipTest)
        	return;
        
        String input = "\n";
        IP2HostnameReader reader = new IP2HostnameReader(new StringReader(input), new DnsHostnameResolver());
        String output = reader.readLine();
        logger.debug("Output: " + output);
        assertEquals("", output);
    }
    
    public void testHostnameOnly() throws Exception {
        logger.info("Running testHostnameOnly ...");
        
        if (skipTest)
        	return;
        
        String input = TEST_IP;
        IP2HostnameReader reader = new IP2HostnameReader(new StringReader(input), new DnsHostnameResolver());
        String output = reader.readLine();
        logger.debug("Output: " + output);
        assertNotSame(TEST_HOSTNAME, output);
    }
    
    public void testIPOnly() throws Exception {
        logger.info("Running testIPOnly ...");

        if (skipTest)
        	return;
        
        String input = "172.18.92.1";
        IP2HostnameReader reader = new IP2HostnameReader(new StringReader(input), new DnsHostnameResolver());
        String output = reader.readLine();
        logger.debug("Output: " + output);
        assertEquals(input, output);
    }

    public void testBack2BackIPs() throws Exception {
        logger.info("Running testBack2BackIPs ...");
        
        if (skipTest)
        	return;
        
        String input = TEST_IP + " " + TEST_IP + "\n";
        IP2HostnameReader reader = new IP2HostnameReader(new StringReader(input), new DnsHostnameResolver());
        String output = reader.readLine();
        logger.debug("Output: " + output);
        assertEquals(TEST_HOSTNAME + " " + TEST_HOSTNAME, output);
        reader.close();
    }
    
    public void testSameIPAndHostname() throws Exception {
        logger.info("Running testSameIPAndHostname ...");
        
        if (skipTest)
        	return;
        String input = TEST_IP + " " + TEST_HOSTNAME + "\n";
        IP2HostnameReader reader = new IP2HostnameReader(new StringReader(input), new DnsHostnameResolver());
        String output = reader.readLine();
        logger.debug("Output: " + output);
        assertEquals(TEST_HOSTNAME + " " + TEST_HOSTNAME, output);
        reader.close();
    }
    
    public void testMultiline() throws Exception {
        logger.info("Running testSameIPAndHostname ...");
    
        if (skipTest)
        	return;
        
        String input = TEST_IP + "\n" + TEST_IP + "\n";
        IP2HostnameReader reader = new IP2HostnameReader(new StringReader(input), new DnsHostnameResolver());
        String output1 = reader.readLine();
        String output2 = reader.readLine();
        logger.debug("Output: " + output1 + " "  + output2);
        assertEquals(TEST_HOSTNAME, output1);
        assertEquals(TEST_HOSTNAME, output2);
        reader.close();
    }
}