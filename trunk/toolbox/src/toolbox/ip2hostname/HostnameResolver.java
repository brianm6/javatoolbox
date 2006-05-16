package toolbox.ip2hostname;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.buffer.BlockingBuffer;
import org.apache.commons.collections.buffer.UnboundedFifoBuffer;
import org.apache.log4j.Logger;

/**
 * Resolves an IP address to a hostname but also cached lookups and can be
 * configured to do the lookup asynchronously. 
 * <p>
 * If caching is turned off, all lookups are forwarded to DNS regardless of
 * having done the lookup at some point in the past.
 * <p>
 * If caching is turned on, lookups are cached indefinitely and no requests
 * are forwarded to DNS on subsequent requests to resolve an IP address.
 * <p>
 * If asynchronous lookups are turned off, the lookup my take a while
 * (especially when you feed it an IP address that doesn't exist or one that
 * does not have a hostname). 
 * <p>
 * If asynchronous lookups are turned on, the first request always queues the
 * lookup and returns the IP address immediately. On subsequent lookups, 
 * depending if the async lookup has completed, either the resolved hostname
 * or IP address will be returned.
 * <p>
 * It is invalid to configure the HostnameResolver to not cache and perform
 * asynchronous lookups.
 * <p>
 * Caching is turned on by default.
 * <p>
 * Asynchronous lookups are turned off by default. 
 */
public class HostnameResolver {
    
    private static final Logger logger = 
        Logger.getLogger(HostnameResolver.class);

    //--------------------------------------------------------------------------
    // Default Constants
    //--------------------------------------------------------------------------
    
    public static final boolean DEFAULT_CACHING = true;
    public static final boolean DEFAULT_ASYNC = false;
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Set to true to perform asynchronous lookups.
     */
    private boolean async;
    
    /**
     * Set to true to cache lookups.
     */
    private boolean caching;
    
    /**
     * Cache for storing lookups.
     */
    private Map cache;
    
    /**
     * Work queue when performing async lookups. All lookups a serialized on
     * a single queue and farmed off to a single thread.
     */
    private Buffer queue;
    
    /**
     * Thead that performs the async lookups.
     */
    private Thread asyncResolver;
    
    //--------------------------------------------------------------------------
    // Constuctors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a HostnameResolver that is caching and not asynchronous.
     */
    public HostnameResolver() {
        this(DEFAULT_CACHING, DEFAULT_ASYNC);
    }
    
    /**
     * Creates a HostnameResolver.
     * 
     * @param caching True to cache lookups, false otherwise
     * @param async True to perform async lookups, false otherwise.
     */
    public HostnameResolver(boolean caching, boolean async) {
        this.async = async;
        this.caching = caching;
        this.cache = new HashMap();
        this.queue = BlockingBuffer.decorate(new UnboundedFifoBuffer());
        
        if (!caching && async)
            throw new IllegalArgumentException(
                "Hostname resolver cannot be both non-caching and asynchronous");
        
        if (this.async) {
            this.asyncResolver = new Thread(new AsyncResolver());
            this.asyncResolver.start();
        }
    }

    //--------------------------------------------------------------------------
    // AsyncResolver
    //--------------------------------------------------------------------------
    
    class AsyncResolver implements Runnable {
        
        public void run() {
            while (true) {
                String ip = (String) queue.remove();
                resolve(ip);
            }
        }
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Resolves the given IP to a hostname based. If caching is turned on and 
     * the hostname is found in the cache, then that hostname is returned. If
     * async is turned on then the IP is returned for the hostname immediately
     * and a DNS lookup is queued up of which the result is placed in the 
     * cache.
     * 
     * @param ip IP address to resolve to a hostname.
     * @return String
     */
    public String resolve(String ip) {
        
        String hostname = null;
        
        if (caching) {
            hostname = (String) cache.get(ip);
            
            if (hostname == null) {
                if (async && Thread.currentThread() != asyncResolver) {
                    if (!queue.contains(ip.intern())) {
                        logger.debug("Adding " + ip + " to queue: size =" + (queue.size() + 1));
                        queue.add(ip);
                    }
                    hostname = ip;
                }
                else {
                    hostname = resolveInternal(ip);
                    cache.put(ip, hostname);
                }
            }
        }
        else {
            hostname = resolveInternal(ip);
        }
        
        return hostname;
    }

    /**
     * Clears the hostname cache.
     */
    public void clear() {
    	cache.clear();
    }
    
    //--------------------------------------------------------------------------
    // Private
    //--------------------------------------------------------------------------
    
    private String resolveInternal(String ip) {
        String hostname = null;
        
        try {
            logger.debug("Resolving " + ip + " ...");
            InetAddress[] ips = InetAddress.getAllByName(ip);
            
            switch (ips.length) {
            
                case 0: 
                	hostname = ip;
                	logger.debug("InetAddress.getAllByName(" + ip + ") returned ZERO names");
                	break;
                	
                default: 
                	hostname = ips[0].getCanonicalHostName();
                	logger.debug("Total names returned for " + ip + ":" + ips.length);
                	logger.debug(ip + " = " + hostname);
            }
        }
        catch (UnknownHostException uhe) {
        	logger.debug("Unknown host: " + hostname);
            hostname = ip;
        }
        
        return hostname;
    }
}