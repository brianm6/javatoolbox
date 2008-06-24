package toolbox.ip2hostname;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Reads text from stdin and transforms all IP addresses to hostnames and pipes
 * to stdout.
 */
public class IP2Hostname {

    private static final Logger logger = Logger.getLogger(IP2Hostname.class);
    
    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------
    
    private boolean async;
    private boolean caching;
    
    // -------------------------------------------------------------------------
    // Main
    // -------------------------------------------------------------------------
    
    public static void main(String[] args) {
        
        try {
            CommandLineParser parser = new PosixParser();
            Options options = new Options();

            // Create command line options            
            Option asyncOption = new Option("a", "async", false, "Asynchronous replacement of hostname (default)");
            Option syncOption = new Option("s", "sync", false, "Synchronous replacement of hostname");
            Option cachingOption = new Option("c", "caching", false, "Cache resolved hostnames (default)");
            Option nonCachingOption = new Option("n", "noncaching", false, "Do not cache resolved hostnames");
            Option verboseOption = new Option("v", "verbose", false, "Verbose logging");
            Option helpOption = new Option("h", "help", false, "Print usage");
            
            options.addOption(helpOption);
            options.addOption(asyncOption);        
            options.addOption(syncOption);
            options.addOption(cachingOption);
            options.addOption(nonCachingOption);
            options.addOption(verboseOption);
    
            // Parse options
            CommandLine cmdLine = parser.parse(options, args, true);

            boolean async = true;
            boolean caching = true;
            
            // Handle options
            for (Iterator i = cmdLine.iterator(); i.hasNext();) {
                Option option = (Option) i.next();
                String opt = option.getOpt();

                if (opt.equals(asyncOption.getOpt())) {
                    async = true;
                }
                else if (opt.equals(syncOption.getOpt())) {
                    async = false;
                }
                else if (opt.equals(cachingOption.getOpt())) {
                    caching = true;
                }
                else if (opt.equals(nonCachingOption.getOpt())) {
                    caching = false;
                }
                else if (opt.equals(verboseOption.getOpt())) {
                    Logger l = Logger.getLogger("toolbox.ip2hostname");
                    l.setAdditivity(true);
                    l.setLevel(Level.DEBUG);
                }
                else if (opt.equals(helpOption.getOpt())) {
                    printUsage(options);
                    return;
                }
                else {
                    logger.error("Option not handled: " + option);
                }
                
                logger.debug("handling option: " + option);
            }

            // there is not support for arguments - flag as improper usage
            switch (cmdLine.getArgs().length) {
                
                case 0:
                    // OK
                    break;

                default:
                    // Invalid
                    printUsage(options);
                    return;
            }
            
            IP2Hostname ip2hostname = new IP2Hostname();
            ip2hostname.setAsync(async);
            ip2hostname.setCaching(caching);
            ip2hostname.start(System.in, System.out);
            
        }
        catch (Exception e) {
            logger.error("main", e);
        }
        
    }
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    
    public IP2Hostname() {
    }
    
    // -------------------------------------------------------------------------
    // Public
    // -------------------------------------------------------------------------
    
    public void start(InputStream is, OutputStream os) {
        
        IP2HostnameReader reader = new IP2HostnameReader(
            new InputStreamReader(is), new HostnameResolver(caching, async));
        
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

    public boolean isAsync() {
        return async;
    }

    
    public void setAsync(boolean async) {
        this.async = async;
    }

    
    public boolean isCaching() {
        return caching;
    }

    
    public void setCaching(boolean caching) {
        this.caching = caching;
    }
    
    // -------------------------------------------------------------------------
    // Private 
    // -------------------------------------------------------------------------
    
    /**
     * Prints program usage. 
     */
    private static void printUsage(Options options) {
        HelpFormatter f = new HelpFormatter();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        
        f.printHelp(
            pw, 
            80, 
            "ip2hostname " + "[options]", 
            "", 
            options, 
            2, 
            4,
            "",
            false);
        
        System.out.println(sw.toString());
    }
}