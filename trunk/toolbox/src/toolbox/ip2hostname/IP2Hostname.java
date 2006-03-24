package toolbox.ip2hostname;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

/**
 * Reads text from stdin and transforms all IP addresses to hostnames and pipes
 * to stdout.
 */
public class IP2Hostname {

    private static final Logger logger = Logger.getLogger(IP2Hostname.class);
    
    public static void main(String[] args) {
        IP2Hostname ip2hostname = new IP2Hostname();
        ip2hostname.start(System.in, System.out);
    }
    
    public void start(InputStream is, OutputStream os) {
        IP2HostnameReader reader = new IP2HostnameReader(new InputStreamReader(is));
        PrintWriter writer = new PrintWriter(os);
        String line = null;
        
        try {
            while ((line = reader.readLine()) != null) {
                writer.println(line);
                writer.flush();
            }
        }
        catch (IOException e) {
            logger.error(e);
        }
        finally {
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(writer);
        }
    }
}