package toolbox.ip2hostname;

import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.buffer.BlockingBuffer;
import org.apache.commons.collections.buffer.UnboundedFifoBuffer;
import org.apache.log4j.Logger;

public class AsyncHostnameResolver implements HostnameResolver, Runnable {

    private static final Logger log = Logger.getLogger(AsyncHostnameResolver.class);
    
    private CachingHostnameResolver delegate;
    
    private Buffer queue;

    private Thread asyncResolver;

    // =======================================================================
    // Constructors
    // =======================================================================

    public AsyncHostnameResolver(CachingHostnameResolver delegate) {
        this.delegate = delegate;
    }

    // =======================================================================
    // HostnameResolver Interface
    // =======================================================================

    public String resolve(String ipAddress) {
        String hostname = null;
        checkInitialized();
        
        if (delegate.hasResolved(ipAddress)) {
            hostname = delegate.resolve(ipAddress);
        }
        else if (!queue.contains(ipAddress)) {
            queue.add(ipAddress);
            hostname = ipAddress;
        }
        else {
            hostname = ipAddress;
        }
        return hostname;
    }
    
    // =======================================================================
    // Runnable Interface
    // =======================================================================
    
    public void run() {
        while(true) {
            String ipAddress = (String) queue.remove();
            String hostname = delegate.resolve(ipAddress);
            log.debug("Popped " + ipAddress + " resolved to " + hostname);
        }
    }
    
    // =======================================================================
    // Private
    // =======================================================================
    
    private void checkInitialized() {
        if (queue == null) {
            queue = BlockingBuffer.decorate(new UnboundedFifoBuffer());
            asyncResolver = new Thread(this);
            asyncResolver.start();
        }
    }
}