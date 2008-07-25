package toolbox.ip2hostname;

public interface CachingHostnameResolver extends HostnameResolver {
    
    void clear();
    
    boolean hasResolved(String ipAddress);
}