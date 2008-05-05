package toolbox.ip2hostname;

import java.util.HashMap;
import java.util.Map;

public class CachingHostnameResolver implements HostnameResolver {

    private Map cache; 
    private DnsHostnameResolver dnsResolver;
        
    public CachingHostnameResolver() {
        dnsResolver = new DnsHostnameResolver();
        cache = new HashMap();
    }
    
    public String resolve(String ipAddress) {
        
        String cachedHostname = (String) cache.get(ipAddress);
        
        if (cachedHostname == null) {
            cachedHostname = dnsResolver.resolve(ipAddress);
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