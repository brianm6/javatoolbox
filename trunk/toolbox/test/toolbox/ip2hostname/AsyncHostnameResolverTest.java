package toolbox.ip2hostname;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import toolbox.util.ThreadUtil;

public class AsyncHostnameResolverTest extends TestCase {

    private static final Logger log = Logger.getLogger(AsyncHostnameResolverTest.class);

    int NOT_RESOLVED_DELAY = 1000;
    int GUARANTEED_RESOLVED_DELAY = NOT_RESOLVED_DELAY + 1000;

    CachingHostnameResolver cachingResolver;
    AsyncHostnameResolver asyncResolver;
    
    public void testIPAddressNotInCache() throws Exception {

        // Setup
        asyncResolver = new AsyncHostnameResolver(new DelayedCachingResolver(NOT_RESOLVED_DELAY));
        
        // Test
        String hostnameBeforeResolve = asyncResolver.resolve("9.9.9.9");
        ThreadUtil.sleep(GUARANTEED_RESOLVED_DELAY);
        String hostnameAfterResolve = asyncResolver.resolve("9.9.9.9");
        
        // Verify
        assertEquals("9.9.9.9", hostnameBeforeResolve);
        assertEquals("www.foobar.com", hostnameAfterResolve);
    }

    public void testIPAddressInCache() throws Exception {

        // Setup
        asyncResolver = new AsyncHostnameResolver(new DelayedCachingResolver(NOT_RESOLVED_DELAY));
        
        // Test
        for (int i = 0; i < 10; i++) {
            String hostnameBeforeResolve = asyncResolver.resolve("9.9.9.9");
            assertEquals("loop " + i, "9.9.9.9", hostnameBeforeResolve);
        }
        
        ThreadUtil.sleep(GUARANTEED_RESOLVED_DELAY);
        String hostnameAfterResolve = asyncResolver.resolve("9.9.9.9");
        
        // Verify
        assertEquals("www.foobar.com", hostnameAfterResolve);
    }
    
    public void testMultipleLookupThreads() throws Exception {
        asyncResolver = new AsyncHostnameResolver(new DelayedCachingResolver(1000), 10);
        
        for (int i = 0; i < 50; i++) {
            String hostname = asyncResolver.resolve("9.9.9." + (i+1));
            log.debug("hostname = " + hostname);
        }
        
        while (!asyncResolver.resolve("9.9.9.50").equals("www.foobar.com")) {
            log.debug("Waiting for resolve...");
            ThreadUtil.sleep(1000);
        }
    }
    
    class DelayedCachingResolver implements CachingHostnameResolver {
    
        boolean resolved = false;
        int delay;
        Map cache = new HashMap();
        
        public DelayedCachingResolver(int delay) {
            this.delay = delay;
        }
        
        public void clear() {
        }
        
        public boolean hasResolved(String ipAddress) {
            return cache.containsKey(ipAddress);
        }
        
        public String resolve(String ipAddress) {
            // Simulate resolve taking a long time so multiple async lookups will 
            // return ip address immediately instead of hostname
            ThreadUtil.sleep(delay);
            cache.put(ipAddress, "www.foobar.com");
            return "www.foobar.com";
        }
    }
}