package toolbox.rtelnet;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Iterator;
import org.apache.log4j.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import toolbox.util.StringUtil;

/**
 * Remote telnet driver enables you to establish a telnet session with a 
 * telnet server and submit a command for execution remotely. 
 */
public class RemoteTelnet
{
	private static final Logger logger_ =
		Logger.getLogger(RemoteTelnet.class);
		
	/** Used to read missing telnet info from the standard in */
	private LineNumberReader lnr_;
	
	/** Remote telnet authentication, connection, command information */
	private RemoteTelnetInfo options_;
	
	/**
	 * Entrypoint
	 * 
	 * @param  args -h host:port	Host and to telnet to
	 *               -u username	Telnet username	
	 *               -p password	Telnet password
	 *               -c command		Telnet command
	 */
	public static void main(String[] args) throws IOException, ParseException
	{
		// Parse args
		CommandLineParser parser = new PosixParser();
		
		Options cliOptions = new Options();
		Option  hostOption = new Option("h", "hostname", true, "Hostname");
		Option  portOption = new Option("o", "port", false, "Port");
		Option  userOption = new Option("u", "username", true, "Username");
		Option  passwordOption = new Option("p", "password", true, "Password");
		Option  cmdOption = new Option("c", "command", true, "Command");		
		Option  helpOption = new Option("?", "help", false, "Print Usage");
        
		cliOptions.addOption(hostOption);
		//cliOptions.addOption(portOption);
		cliOptions.addOption(userOption);
		cliOptions.addOption(passwordOption);
		cliOptions.addOption(cmdOption);
		//cliOptions.addOption(helpOption);        

		CommandLine cmdLine = parser.parse(cliOptions, args, true);
    	RemoteTelnetInfo options = new RemoteTelnetInfo();
    	
		for (Iterator i = cmdLine.iterator(); i.hasNext(); )
		{
			Option option = (Option) i.next();
			String opt    = option.getOpt();			
			String value  = option.getValue();
            
            logger_.debug("Processing opt " + opt + " with value " + value);
            
			if (opt.equals(hostOption.getOpt()))
				options.setHostname(value);
			//else if (opt.equals(portOption.getOpt()))
			//	options.setPort(Integer.parseInt(value));
			else if (opt.equals(userOption.getOpt()))
				options.setUsername(value);
			else if (opt.equals(passwordOption.getOpt()))
				options.setPassword(value);
			else if (opt.equals(cmdOption.getOpt()))
				options.setCommand(value);
			else if (opt.equals(helpOption.getOpt()))
				printUsage();
		}
		
		RemoteTelnet rtelnet = new RemoteTelnet(options);
		rtelnet.verifyOptions();
		rtelnet.execute();		
		
		//System.exit(0);
	}

	public RemoteTelnet(String hostname, 
				int port, 
				String username, 
				String password, 
				String command) throws IOException
	{
		this(new RemoteTelnetInfo(hostname, port, username, password, command));
	}
	
	public RemoteTelnet(RemoteTelnetInfo options) throws IOException
	{
		options_ = options;
	}
    
    public void execute() throws SocketException, IOException
    {
    	System.out.println("Options\n========");
    	System.out.println(options_);
    	
    	RemoteTelnetClient telnet = new RemoteTelnetClient();
    	telnet.connect(options_.getHostname(), options_.getPort());
   	
//    	telnet.sendCommand("xx\n");
//    	telnet.waitFor("login");
//    	telnet.sendCommand("semir");
//    	telnet.waitFor("Password");
//    	telnet.sendCommand("semir");
//    	telnet.waitFor("Last");
//    	telnet.sendCommand("export DISPLAY=9.90.20.248:0");
//    	telnet.sendCommand("startkde&");

		telnet.sendCommand("");
		telnet.waitFor("login");
		telnet.sendCommand(options_.getUsername());
		telnet.waitFor("Password");
		telnet.sendCommand(options_.getPassword());
		telnet.waitFor("Last");
		telnet.sendCommand(
			"export DISPLAY=" + InetAddress.getLocalHost().getHostAddress() + ":0");
		telnet.sendCommand(options_.getCommand());
    	
    	//telnet.disconnect();
    	//commandLoop(telnet);
    }
    
    public void commandLoop(RemoteTelnetClient telnet) throws IOException
    {
    	String cmd = "";
    	while (true)
    	{
			cmd = queryUser("CMD> ");
			
			if (cmd.equals("exit"))
				return;
				
			telnet.getOutputStream().write((cmd+"\n").getBytes());
			telnet.getOutputStream().flush();
			
			//String response = StreamUtil.asString(telnet.getInputStream());
    	}
		
    }
    
	/**
	 * Prints program usage
	 */
	protected static void printUsage()
	{
		System.out.println("RemoteTelnetClient  : Runs a command remotely via telnet");
		System.out.println("Usage    : rtelnet -h hostname " +
											  "-o port " + 
		                                      "-u username " + 
                                              "-p password " + 
                                              "-c command");
	}
    
	private void verifyOptions() throws IOException
	{
		if (StringUtil.isNullOrEmpty(options_.getHostname()))
			options_.setHostname(queryUser("Hostname: "));
    		
		if (options_.getPort() == 0)
			options_.setPort(Integer.parseInt(queryUser("Port: ")));
    		
		if (StringUtil.isNullOrEmpty(options_.getUsername()))
			options_.setUsername(queryUser("Username: "));
    		
		if (StringUtil.isNullOrEmpty(options_.getPassword()))
			options_.setPassword(queryUser("Password: "));
    		
		if (StringUtil.isNullOrEmpty(options_.getCommand()))
			options_.setCommand(queryUser("Command: " ));
	}    

    private String queryUser(String prompt) throws IOException
    {
		System.out.print(prompt);

		if (lnr_ == null)
			lnr_ = new LineNumberReader(new InputStreamReader(System.in));
			
		return lnr_.readLine();    
    }
    
}