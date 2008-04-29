package toolbox.ip2hostname;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

public class HostnameResolverTest extends TestCase {

    private static final Logger logger = Logger.getLogger(HostnameResolverTest.class);
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args) {
        TestRunner.run(HostnameResolverTest.class);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    public void testResolve_NotCaching_NotAsync() throws Exception {
        logger.info("Running testResolve_NotCaching_NotAsync");
        
        HostnameResolver resolver = new HostnameResolver(false, false);
        String hostname = resolver.resolve("127.0.0.1");
        logger.debug("Resolved to " + hostname);
    }
    
    public void testResolve_NotCaching_Async() throws Exception {
        logger.info("Running testResolve_NotCaching_Async");
        
        try {
            HostnameResolver resolver = new HostnameResolver(false, true);
            fail("Invalid combo should have failed");
        }
        catch (IllegalArgumentException iae) {
            // Success
        }
    }

    public void testResolve_Caching_NotAsync() throws Exception {
        logger.info("Running testResolve_Caching_NotAsync");
        
        HostnameResolver resolver = new HostnameResolver(true, false);
        String hostname = resolver.resolve("127.0.0.1");
        logger.debug("First resolved to " + hostname);
        
        String hostname2 = resolver.resolve("127.0.0.1");
        logger.debug("Second resolved to " + hostname2);
    }
    
    public void testResolve_Caching_Async() throws Exception {
        logger.info("Running testResolve_Caching_Async");
        
        HostnameResolver resolver = new HostnameResolver(true, true);
        String hostname = null;
        String ip = "127.0.0.1";
        
        int cnt = 1;
        do {
            hostname = resolver.resolve("127.0.0.1");
            logger.debug("Try " + cnt++ + " resolved to " + hostname);
        } while (hostname.equals(ip));
    }
}
