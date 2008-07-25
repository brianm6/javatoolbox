package toolbox.ip2hostname;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import junit.framework.TestCase;

import org.apache.log4j.Logger;

public class DefaultCachingHostnameResolverTest extends TestCase {

    private static final Logger log = Logger.getLogger(DefaultCachingHostnameResolverTest.class);

    HostnameResolver dnsResolver;
    CachingHostnameResolver cachingResolver;
    
    protected void setUp() throws Exception {
        dnsResolver = mock(HostnameResolver.class);
        cachingResolver = new DefaultCachingHostnameResolver(dnsResolver);
    }
    
    public void testIPAddressNotInCache() throws Exception {

        // Setup
        stub(dnsResolver.resolve(anyString())).toReturn("www.foobar.com");
        
        // Test
        String hostname = cachingResolver.resolve("9.9.9.9");
        
        // Verify
        verify(dnsResolver, times(1)).resolve(eq("9.9.9.9"));
        assertEquals("www.foobar.com", hostname);
    }

    public void testIPAddressInCache() throws Exception {

        // Setup
        stub(dnsResolver.resolve(anyString())).toReturn("www.foobar.com");
        
        // Test
        for (int i = 0; i < 100; i++)
            cachingResolver.resolve("9.9.9.9");
        
        // Verify only called DNS once
        verify(dnsResolver, times(1)).resolve(eq("9.9.9.9")); 
    }
    
    public void testIPAddressLookedUpAgainAfterCacheCleared() throws Exception {

        // Setup
        stub(dnsResolver.resolve(anyString())).toReturn("www.foobar.com");
        
        // Test
        cachingResolver.resolve("9.9.9.9");
        cachingResolver.resolve("9.9.9.9");
        cachingResolver.clear();
        cachingResolver.resolve("9.9.9.9");
        cachingResolver.resolve("9.9.9.9");
        
        // Verify - 1 call before clear() + 1 call after clear()
        verify(dnsResolver, times(2)).resolve(eq("9.9.9.9")); 
    }
    
    public void testHasResolved() {
        assertFalse(cachingResolver.hasResolved("9.9.9.9"));
        cachingResolver.resolve("9.9.9.9");
        assertTrue(cachingResolver.hasResolved("9.9.9.9"));
    }
    
    public void testHasResolvedAfterCacheCleared() {
        assertFalse(cachingResolver.hasResolved("9.9.9.9"));
        cachingResolver.resolve("9.9.9.9");
        assertTrue(cachingResolver.hasResolved("9.9.9.9"));
        cachingResolver.clear();
        assertFalse(cachingResolver.hasResolved("9.9.9.9"));
    }
}