package toolbox.rtelnet;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.SocketException;
import java.util.Iterator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Remote telnet driver enables you to establish a telnet session with a 
 * telnet server and submit a command for execution remotely. 
 */
public class RemoteTelnet
{
    private static final Logger logger_ = Logger.getLogger(RemoteTelnet.class);

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Separator for (submit command, wait for response) pairs submitted to
     * the program via the -c switch.
     */
    private static final char COMMAND_SEPARATOR = '|';
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /** 
     * Used to read missing telnet info from standard input. 
     */
    private LineNumberReader reader_;
    
    /** 
     * Telnet authentication, connection, and command information. 
     */
    private RemoteTelnetInfo options_;
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Launches RemoteTelnet.
     * 
     * @param args See following: 
     * <pre>
     * -h host:port Host and port to telnet to
     * -u username  Telnet username    
     * -p password  Telnet password
     * -c command   Telnet command/response pairs
     * </pre>
     * 
     * @throws IOException on I/O error.
     * @throws ParseException on command line parsing error.
     * @throws InterruptedException on reader thread interruption.
     */
    public static void main(String[] args) 
        throws IOException, ParseException, InterruptedException
    {
        // TODO: Add support for telnet port
        
        // Parse args
        Options options = new Options();
        Option  hostname = new Option("h", "hostname", true , "Hostname");
        Option  username = new Option("u", "username", true , "Username");
        Option  password = new Option("p", "password", true , "Password");
        Option  command  = new Option("c", "command" , true , "Command");
        Option  help     = new Option("?", "help"    , false, "Print Usage");
        
        options.addOption(hostname);
        options.addOption(username);
        options.addOption(password);
        options.addOption(command);
        options.addOption(help);        

        CommandLineParser parser = new PosixParser();
        CommandLine cmdLine = parser.parse(options, args, true);
        RemoteTelnetInfo info = new RemoteTelnetInfo();
        
        for (Iterator i = cmdLine.iterator(); i.hasNext();)
        {
            Option option = (Option) i.next();
            String opt    = option.getOpt();            
            String value  = option.getValue();
            
            logger_.debug("Processing opt " + opt + " with value " + value);
            
            if (opt.equals(hostname.getOpt()))
                info.setHostname(value);
            else if (opt.equals(username.getOpt()))
                info.setUsername(value);
            else if (opt.equals(password.getOpt()))
                info.setPassword(value);
            else if (opt.equals(command.getOpt()))
                info.setCommand(value);
            else if (opt.equals(help.getOpt()))
                printUsage();
        }
        
        RemoteTelnet rtelnet = new RemoteTelnet(info);
        rtelnet.verifyOptions();
        rtelnet.execute();        
    }

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a remote telnet with the given options.
     * 
     * @param hostname Host to telnet to.
     * @param port Telnet port.
     * @param username Login user.
     * @param password Clear text password.
     * @param command Command to execute on remote host.
     * @throws IOException on I/O error.
     */ 
    public RemoteTelnet(
            String hostname, 
            int port, 
            String username,
            String password, 
            String command) throws IOException
    {
        this(new RemoteTelnetInfo(hostname, port, username, password, command));
    }
    
    
    /**
     * Creates a remote telnet with the given options.
     * 
     * @param options Telnet info.
     * @throws IOException on I/O error.
     */
    public RemoteTelnet(RemoteTelnetInfo options) throws IOException
    {
        options_ = options;
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Telnets to the remote host and executes the command.
     * 
     * @throws SocketException on socket communication error.
     * @throws IOException on I/O error.
     * @throws InterruptedException on reader thread interruption.
     */    
    public void execute() 
        throws SocketException, IOException, InterruptedException
    {
        System.out.println("Options\n========");
        System.out.println(options_);
        
        RemoteTelnetClient telnet = new RemoteTelnetClient();
        
        logger_.debug("Connecting...");
        telnet.connect(options_.getHostname(), options_.getPort());
        
        logger_.debug("Sending AYT...");
        telnet.sendAYT(10000);
        
        //telnet.sendCommand("");
        telnet.waitFor("login");
        telnet.sendCommand(options_.getUsername());
        telnet.waitFor("Password");
        telnet.sendCommand(options_.getPassword());
        telnet.waitFor("Last");
        
        //telnet.sendCommand("export DISPLAY=" + 
        //    InetAddress.getLocalHost().getHostAddress() + ":0");
        
        String[] tokens = 
            StringUtils.split(options_.getCommand(), COMMAND_SEPARATOR);
        
        switch (tokens.length)
        {
            case 0: 
                System.out.println("No commands specified");
                printUsage();
                break;
                
            case 1:
                telnet.sendCommand(tokens[0]);
                break;
                
            default:
                
                for (int i = 0; i < tokens.length; i+=2)
                {
                    telnet.sendCommand(tokens[i]);
                    Thread.sleep(1000);
                    if (i + 1 < tokens.length)
                        telnet.waitFor(tokens[i+1]);
                }
            
                break;
        }
        
        logger_.debug("Sending AYT...");
        telnet.sendAYT(10000);
        telnet.disconnect();
        logger_.debug("Disconnected");
    }
    
    
    /**
     * Enables interactive telnet.
     * 
     * @param telnet Telnet client.
     * @throws IOException on I/O error.
     */
    public void commandLoop(RemoteTelnetClient telnet) throws IOException
    {
        String cmd = "";
        while (true)
        {
            cmd = queryUser("CMD> ");
            
            if (cmd.equals("exit"))
                return;
                
            telnet.getOutputStream().write((cmd + "\n").getBytes());
            telnet.getOutputStream().flush();
            
            //String response = StreamUtil.asString(telnet.getInputStream());
        }
        
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
        
    /**
     * Prints program usage.
     */
    protected static void printUsage()
    {
        System.out.println(
            "RemoteTelnetClient  : Runs a command remotely via telnet");
            
        System.out.println("Usage    : rtelnet -h hostname " +
                                              "-u username " + 
                                              "-p password " + 
                                              "-c command");
    }
    
    
    /**
     * Verifies that the telnet options are valid. Prompts the user via
     * the command line if an option is missing or invalid.
     * 
     * @throws IOException on I/O error.
     */
    protected void verifyOptions() throws IOException
    {
        if (StringUtils.isEmpty(options_.getHostname()))
            options_.setHostname(queryUser("Hostname: "));
            
        if (options_.getPort() == 0)
            options_.setPort(Integer.parseInt(queryUser("Port: ")));
            
        if (StringUtils.isEmpty(options_.getUsername()))
            options_.setUsername(queryUser("Username: "));
            
        if (StringUtils.isEmpty(options_.getPassword()))
            options_.setPassword(queryUser("Password: "));
            
        if (StringUtils.isEmpty(options_.getCommand()))
            options_.setCommand(queryUser("Command: "));
    }    


    /**
     * Promps the user to enter a value via the command line.
     * 
     * @param prompt Prompt that the user is presented with.
     * @return Value that the user typed in.
     * @throws IOException on I/O error.
     */
    private String queryUser(String prompt) throws IOException
    {
        System.out.print(prompt);

        if (reader_ == null)
            reader_ = new LineNumberReader(new InputStreamReader(System.in));
            
        return reader_.readLine();    
    }
}