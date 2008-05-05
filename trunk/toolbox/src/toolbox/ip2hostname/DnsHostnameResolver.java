package toolbox.ip2hostname;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;

public class DnsHostnameResolver implements HostnameResolver {

    private static final Logger logger = 
        Logger.getLogger(DnsHostnameResolver.class);

    public String resolve(String ip) {
        
        String hostname = null;
        StopWatch timer = new StopWatch();
        timer.start();
        
        try {
            logger.debug("Resolving " + ip + " ...");
            InetAddress[] ips = InetAddress.getAllByName(ip);
            
            switch (ips.length) {
            
                case 0: 
                    hostname = ip;
                    logger.debug("No hostnames attached to " + ip);
                    break;
                    
                default: 
                    hostname = ips[0].getCanonicalHostName();
                    logger.debug(ips.length + " hostnames mapped to " + ip);
                    logger.debug(ip + " = " + hostname);
            }
        }
        catch (UnknownHostException uhe) {
            logger.debug("Not valid ip address: " + hostname);
            hostname = ip;
        }
        finally {
            timer.stop();
            logger.debug("DNS time: " + timer);
        }
        return hostname;
    }
}