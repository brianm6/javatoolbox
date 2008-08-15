package toolbox.ip2hostname;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.regexp.RE;

/**
 * Reader that replaces all occurrences of an IPv4 ip address with its 
 * associated hostname (if it has one). 
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
    // Fields
    // -------------------------------------------------------------------------

    /** 
     * Regular expression that matches an IP address of the form 127.0.0.1. 
     */
    private RE matcher;             
   
    /**
     * Responsible for resolving an IP address to a hostname. 
     */
    private HostnameResolver resolver;
    
    /**
     * Replace the ipaddress with the hostname or insert the hostname?
     */
    private boolean insertHostname;
    
    // --------------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------------

    public IP2HostnameReader(Reader in, HostnameResolver resolver) {
        this(in, resolver, false);
    }
    
    public IP2HostnameReader(Reader in, HostnameResolver resolver, boolean insertHostname) {
        super(in);
        this.resolver = resolver;
        this.matcher = new RE(REGEXP_IP_ADDRESS);
        this.insertHostname = insertHostname;
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
            
            while (match = matcher.match(line, index)) {
                String ipAddress = matcher.getParen(0);
                String hostname = resolver.resolve(ipAddress);
                int ipStart = matcher.getParenStart(0);
                int ipEnd   = matcher.getParenEnd(0);
                
                // only replace if resolved to something other than IP
                if (!ipAddress.equals(hostname)) {
                    
                    if (insertHostname) {
                        // Append hostname to ip addr instead of replacing it
                        hostname = ipAddress + " [" + hostname + "] ";
                    }
                    
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
}