package toolbox.ip2hostname;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;

import java.io.StringReader;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

public class IP2HostnameReaderTest extends TestCase {

    private static final Logger logger = Logger.getLogger(IP2HostnameReaderTest.class);

    private static final String TEST_IP = "128.34.12.5";
    private static final String TEST_HOSTNAME = "www.foobar.com";
    private static final String TEST_IP_NO_HOSTNAME = "99.98.86.84";

    /** Mock resolver */
    private HostnameResolver resolver;

    // -------------------------------------------------------------------------
    // Main
    // -------------------------------------------------------------------------
    
    public static void main(String[] args) {
        TestRunner.run(IP2HostnameReaderTest.class);
    }

    // -------------------------------------------------------------------------
    // Setup
    // -------------------------------------------------------------------------
    
    protected void setUp() throws Exception {
        resolver = mock(HostnameResolver.class);
        stub(resolver.resolve(eq(TEST_IP))).toReturn(TEST_HOSTNAME);
        stub(resolver.resolve(eq(TEST_IP_NO_HOSTNAME))).toReturn(TEST_IP_NO_HOSTNAME);
    }
    
    // -------------------------------------------------------------------------
    // Unit Tests
    // -------------------------------------------------------------------------
    
    public void testEmptyString() throws Exception {
        logger.info("Running testEmptyString...");
        
        String input = "";
        IP2HostnameReader reader = new IP2HostnameReader(new StringReader(input), resolver);
        String output = reader.readLine();
        logger.debug("Output: " + output);
        assertNull(output);
    }

    public void testEmptyLine() throws Exception {
        logger.info("Running testEmptyLine...");
        
        String input = "\n";
        IP2HostnameReader reader = new IP2HostnameReader(new StringReader(input), resolver);
        String output = reader.readLine();
        logger.debug("Output: " + output);
        assertEquals("", output);
    }
    
    public void testHostnameOnly() throws Exception {
        logger.info("Running testHostnameOnly ...");
        
        String input = TEST_HOSTNAME;
        IP2HostnameReader reader = new IP2HostnameReader(new StringReader(input), resolver);
        String output = reader.readLine();
        logger.debug("Output: " + output);
        assertNotSame(TEST_HOSTNAME, output);
    }
    
    public void testIPThatDoesNotHaveAHostname() throws Exception {
        logger.info("Running testIPThatDoesNotHaveAHostname...");

        String input = TEST_IP_NO_HOSTNAME;
        IP2HostnameReader reader = new IP2HostnameReader(new StringReader(input), resolver);
        String output = reader.readLine();
        logger.debug("Output: " + output);
        assertEquals(TEST_IP_NO_HOSTNAME, output);
    }

    public void testIPOnly() throws Exception {
        logger.info("Running testIPOnly ...");
        
        String input = TEST_IP;
        IP2HostnameReader reader = new IP2HostnameReader(new StringReader(input), resolver);
        String output = reader.readLine();
        logger.debug("Output: " + output);
        assertEquals(TEST_HOSTNAME, output);
    }
    
    public void testBack2BackIPs() throws Exception {
        logger.info("Running testBack2BackIPs ...");
        
        String input = TEST_IP + " " + TEST_IP + "\n";
        IP2HostnameReader reader = new IP2HostnameReader(new StringReader(input), resolver);
        String output = reader.readLine();
        logger.debug("Output: " + output);
        assertEquals(TEST_HOSTNAME + " " + TEST_HOSTNAME, output);
        reader.close();
    }
    
    public void testSameIPAndHostname() throws Exception {
        logger.info("Running testSameIPAndHostname ...");
        
        String input = TEST_IP + " " + TEST_HOSTNAME + "\n";
        IP2HostnameReader reader = new IP2HostnameReader(new StringReader(input), resolver);
        String output = reader.readLine();
        logger.debug("Output: " + output);
        assertEquals(TEST_HOSTNAME + " " + TEST_HOSTNAME, output);
        reader.close();
    }
    
    public void testMultiline() throws Exception {
        logger.info("Running testMultiline ...");
        
        String input = TEST_IP + "\n" + TEST_IP + "\n";
        IP2HostnameReader reader = new IP2HostnameReader(new StringReader(input), resolver);
        String output1 = reader.readLine();
        String output2 = reader.readLine();
        logger.debug("Output: " + output1 + " "  + output2);
        assertEquals(TEST_HOSTNAME, output1);
        assertEquals(TEST_HOSTNAME, output2);
        reader.close();
    }
    
    public void testInsertHostnameInsteadOfReplacingIPAddress() throws Exception {
        logger.info("Running testInsertHostnameInsteadOfReplacingIPAddress ...");
        
        String input = TEST_IP;
        IP2HostnameReader reader = new IP2HostnameReader(new StringReader(input), resolver, true);
        String output = reader.readLine();
        logger.debug("Output: " + output);
        assertEquals(TEST_IP + " [" + TEST_HOSTNAME + "] ", output);
    }
}