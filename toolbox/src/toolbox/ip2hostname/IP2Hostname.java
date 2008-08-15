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
import org.apache.commons.lang.StringUtils;
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
    
    private HostnameResolver resolver;
    private boolean insertHostname;
    
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
            Option maxThreadsOption = new Option("m", "maxthreads", true, "Max concurrent DNS lookups [1..20] (default = 5)");
            Option insertHostnameOption = new Option("i", "insert", false, "Insert hostname instead of replacing IP address");
            
            options.addOption(helpOption);
            options.addOption(asyncOption);        
            options.addOption(syncOption);
            options.addOption(cachingOption);
            options.addOption(nonCachingOption);
            options.addOption(verboseOption);
            options.addOption(maxThreadsOption);
            options.addOption(insertHostnameOption);
    
            // Parse options
            CommandLine cmdLine = parser.parse(options, args, true);

            boolean insertHostname = false;
            boolean async = true;
            boolean caching = true;
            int maxThreads = 5;
            
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
                else if (opt.equals(maxThreadsOption.getOpt())) {
                    String maxThreadsString = option.getValue();
                    
                    if (StringUtils.isNotBlank(maxThreadsString)) {
                        maxThreadsString = maxThreadsString.trim();
                        try {
                            maxThreads = Integer.parseInt(maxThreadsString);
                            if (maxThreads < 1 || maxThreads > 20) {
                                System.err.println("Max threads must be between 1 and 20");
                                return;
                            }
                        }
                        catch (NumberFormatException nfe) {
                            System.err.println("Invalid maxthreads argument. Try -m 5");
                            return;
                        }
                    }
                    else {
                        System.err.println("Invalid maxthreads argument. Try -m 5");
                        return;
                    }
                }
                else if (opt.equals(insertHostnameOption.getOpt())) {
                    insertHostname = true;
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

            // there is no support for arguments - flag as improper usage
            switch (cmdLine.getArgs().length) {
                
                case 0:
                    // OK
                    break;

                default:
                    // Invalid
                    printUsage(options);
                    return;
            }
            
            HostnameResolver resolver;
            
            if (caching && async)
                resolver = new AsyncHostnameResolver(new DefaultCachingHostnameResolver(new DnsHostnameResolver()), maxThreads);
            else if (caching && !async)
                resolver = new DefaultCachingHostnameResolver(new DnsHostnameResolver());
            else if (!caching)
                resolver = new DnsHostnameResolver();
            else
                throw new IllegalArgumentException("Could not determine with hostname resolver to use");
            
            IP2Hostname ip2hostname = new IP2Hostname(resolver, insertHostname);
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
        this(new AsyncHostnameResolver(new DefaultCachingHostnameResolver(new DnsHostnameResolver())), false);
    }
    
    public IP2Hostname(HostnameResolver resolver, boolean insertHostname) {
        this.resolver = resolver;
        this.insertHostname = insertHostname;
    }
    
    // -------------------------------------------------------------------------
    // Public
    // -------------------------------------------------------------------------
    
    public void start(InputStream is, OutputStream os) {
        
        IP2HostnameReader reader = new IP2HostnameReader(new InputStreamReader(is), resolver, insertHostname);
        
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