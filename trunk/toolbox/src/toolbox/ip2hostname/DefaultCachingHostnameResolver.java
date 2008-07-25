package toolbox.ip2hostname;

import java.util.HashMap;
import java.util.Map;

public class DefaultCachingHostnameResolver implements CachingHostnameResolver {

    private Map cache; 
    private HostnameResolver delegate;
        
    public DefaultCachingHostnameResolver(HostnameResolver delegate) {
        this.delegate = delegate;
        cache = new HashMap();
    }
    
    public String resolve(String ipAddress) {
        
        String cachedHostname = (String) cache.get(ipAddress);
        
        if (cachedHostname == null) {
            cachedHostname = delegate.resolve(ipAddress);
            cache.put(ipAddress, cachedHostname);
        }
        
        return cachedHostname;
    }
    
    public void clear() {
        cache.clear();
    }
    
    public boolean hasResolved(String ipAddress) {
        return cache.containsKey(ipAddress);
    }
}