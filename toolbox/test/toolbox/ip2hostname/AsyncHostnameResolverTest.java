package toolbox.ip2hostname;

import org.apache.log4j.Logger;

import toolbox.util.ThreadUtil;

import junit.framework.TestCase;

public class AsyncHostnameResolverTest extends TestCase {

    
    private static final Logger log = 
        Logger.getLogger(AsyncHostnameResolverTest.class);

    private static final String IP_ADDR = "72.14.207.99";
    
    public void testResolveIPAddressToHostnameSuccessful() throws Exception {
        log.info("Running testResolveIPAddressToHostnameSuccessful...");
        HostnameResolver resolver = new AsyncHostnameResolver();
        String hostname = resolver.resolve(IP_ADDR);
        assertEquals(IP_ADDR, hostname);
        
        boolean asyncResolveCompleted = false;
        int limit = 1000;
        int cnt = 0;
        while (!asyncResolveCompleted && cnt++ < limit) {
            ThreadUtil.sleep(1);
            hostname = resolver.resolve(IP_ADDR);
            log.debug(hostname);
            asyncResolveCompleted = !hostname.equals(IP_ADDR);
        }
        assertFalse(IP_ADDR.equals(hostname));
    }
    
    public void testResolveBogusIPAddress() throws Exception {
        log.info("Running testResolveBogusIPAddress...");
        HostnameResolver resolver = new CachingHostnameResolver();
        String hostname = resolver.resolve("1.1.1.1");
        log.debug(hostname);
        assertEquals("1.1.1.1", hostname);
    }
}