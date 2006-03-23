package toolbox.ngrep;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.regexp.RE;

/**
 * Reader that transforms IP addresses to hostnames. 
 */
public class IP2HostnameReader extends LineNumberReader {

    private static final Logger logger = 
        Logger.getLogger(IP2HostnameReader.class);

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------
    
    private static final String REGEXP_IP_ADDRESS = 
        "([:digit:]{1,3}"    // 1-3 digits
        + "\\."             // .
        + "[:digit:]{1,3}"  // 1-3 digits
        + "\\."             // .      
        + "[:digit:]{1,3}"  // 1-3 digits
        + "\\."             // .      
        + "[:digit:]{1,3})"; // 1-3 digits

    // -------------------------------------------------------------------------
    // Static Fields
    // -------------------------------------------------------------------------
    
    /** Caches hostname lookups so DNS is not flooded w/ requests. */
    private static Map hostnameCache = new HashMap();

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    /** Regular expression that matches an IP address of the form 127.0.0.1. */
    private RE ipAddressMatcher;             
    
    // --------------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------------

    public IP2HostnameReader(Reader in) {
        super(in);
        ipAddressMatcher = new RE(REGEXP_IP_ADDRESS);
    }

    // --------------------------------------------------------------------------
    // Overrides java.io.LineNumberReader
    // --------------------------------------------------------------------------

    /**
     * Reads the next line that matches the regular expression.
     * 
     * @return String that matches regular expression or null if the end of the
     *         stream has been reached.
     * @throws IOException on error.
     */
    public String readLine() throws IOException {
        String line = super.readLine();

        if (line != null) {
            
            boolean match = false;
            int index = 0;
            
            while (match = ipAddressMatcher.match(line, index)) {
                String ipAddress = ipAddressMatcher.getParen(0);
                String hostname = getCachedHostname(ipAddress);
                int ipStart = ipAddressMatcher.getParenStart(0);
                int ipEnd   = ipAddressMatcher.getParenEnd(0);
                
                // only replace if resolved to something other than IP
                if (!ipAddress.equals(hostname)) {
                    line = StringUtils.replace(line, ipAddress, hostname);
                    index = ipStart + hostname.length() + 1;
                }
                else {
                    index = ipStart + ipAddress.length() + 1;
                }
            }
        }
        
        return line;
    }
    
    // -------------------------------------------------------------------------
    // Private
    // -------------------------------------------------------------------------
    
    private String resolveHostname(String ip) {
        String hostname = null;
        
        try {
            logger.debug("Resolving hostname: '" + ip + "'");
            InetAddress[] ips = InetAddress.getAllByName(ip);
            
            switch (ips.length) {
                case 0: hostname = ip; break;
                default: hostname = ips[0].getCanonicalHostName();
                //default: hostname = ips[0].getHostName();
            }
        }
        catch (UnknownHostException uhe) {
            hostname = ip;
        }
        
        return hostname;
    }

    private String getCachedHostname(String ip) {
        
        String hostname = (String) hostnameCache.get(ip);
        
        if (hostname == null) {
            hostname = resolveHostname(ip);
            hostnameCache.put(ip, hostname);
            logger.debug("Host cache size = " + hostnameCache.size());
        }
        
        return hostname;
    }
}