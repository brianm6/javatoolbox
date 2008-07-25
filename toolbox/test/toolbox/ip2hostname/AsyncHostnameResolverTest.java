package toolbox.ip2hostname;

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
        asyncResolver = new AsyncHostnameResolver(new DelayedCachingResolver());
        
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
        asyncResolver = new AsyncHostnameResolver(new DelayedCachingResolver());
        
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
    
    class DelayedCachingResolver implements CachingHostnameResolver {
    
        boolean resolved = false;
        
        public void clear() {
        }
        
        public boolean hasResolved(String ipAddress) {
            return resolved;
        }
        
        public String resolve(String ipAddress) {
            // Simulate resolve taking a long time so multiple async lookups will 
            // return ip address immediately instead of hostname
            ThreadUtil.sleep(NOT_RESOLVED_DELAY);
            resolved = true;
            return "www.foobar.com";
        }
    }
}