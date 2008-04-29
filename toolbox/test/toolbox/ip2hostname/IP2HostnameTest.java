package toolbox.ip2hostname;

import org.apache.log4j.Logger;

import junit.framework.TestCase;
import junit.textui.TestRunner;

public class IP2HostnameTest extends TestCase {

    private static final Logger logger = Logger.getLogger(IP2HostnameTest.class);
    
    // -------------------------------------------------------------------------
    // Main
    // -------------------------------------------------------------------------
    
    public static void main(String[] args) {
        TestRunner.run(IP2HostnameTest.class);
    }
    
    // -------------------------------------------------------------------------
    // Unit Tests
    // -------------------------------------------------------------------------
    
    public void testPrintUsage() {
        logger.info("Running testPrintUsage ...");
        IP2Hostname.main(new String[] {"-help"} );
    }
}