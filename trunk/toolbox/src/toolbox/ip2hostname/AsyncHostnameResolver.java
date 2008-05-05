package toolbox.ip2hostname;

import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.buffer.BlockingBuffer;
import org.apache.commons.collections.buffer.UnboundedFifoBuffer;

public class AsyncHostnameResolver extends CachingHostnameResolver implements Runnable {

    private Buffer queue;

    private Thread asyncResolver;
    
    public AsyncHostnameResolver() {
        queue = BlockingBuffer.decorate(new UnboundedFifoBuffer());
        asyncResolver = new Thread(this);
        asyncResolver.start();
    }
    
    public String resolve(String ipAddress) {
        
        // already resolved..just return in
        if (hasResolved(ipAddress))
            return super.resolve(ipAddress);
        
        // in-flight resolve..just return ip
        if (queue.contains(ipAddress))
            return ipAddress;
       
        queue.add(ipAddress);
        return ipAddress;
    }
        
    public void run() {
        while(true) {
            String ipAddress = (String) queue.remove();
            super.resolve(ipAddress);
        }
    }
}