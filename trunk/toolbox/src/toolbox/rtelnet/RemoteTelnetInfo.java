package toolbox.rtelnet;

/**
 * RemoteTelnetInfo is a data object that stores information needed to
 * identify, authenticate, and drive a telnet session.
 */
public class RemoteTelnetInfo
{
    private String hostname_;
    private int    port_;
    private String username_;
    private String password_;
    private String command_;

    //--------------------------------------------------------------------------
    // Constructors 
    //--------------------------------------------------------------------------
    
    /**
     * Default constructor. Sets telnet port to 23
     */    
    public RemoteTelnetInfo()
    {
        setPort(23);
    }
    
    /**
     * Creates a RemoteTelnetInfo with the given parameters
     * 
     * @param hostname  Host to telnet to
     * @param port      Port to connect to on telnet host
     * @param username  Username 
     * @param password  Password
     * @param command   Command to execute
     */
    public RemoteTelnetInfo(String hostname, int port, String username, 
        String password, String command)
    {
        setHostname(hostname);
        setPort(port);
        setUsername(username);
        setPassword(password);
        setCommand(command);    
    }
    
    //--------------------------------------------------------------------------
    // Public 
    //--------------------------------------------------------------------------
    
    /**
     * @return String
     */
    public String getCommand()
    {
        return command_;
    }

    /**
     * @return String
     */
    public String getHostname()
    {
        return hostname_;
    }

    /**
     * @return String
     */
    public String getPassword()
    {
        return password_;
    }

    /**
     * @return int
     */
    public int getPort()
    {
        return port_;
    }

    /**
     * @return String
     */
    public String getUsername()
    {
        return username_;
    }

    /**
     * Sets the command.
     * 
     * @param command The command to set
     */
    public void setCommand(String command)
    {
        command_ = command;
    }

    /**
     * Sets the hostname.
     * 
     * @param hostname The hostname to set
     */
    public void setHostname(String hostname)
    {
        hostname_ = hostname;
    }

    /**
     * Sets the password.
     * 
     * @param password The password to set
     */
    public void setPassword(String password)
    {
        password_ = password;
    }

    /**
     * Sets the port.
     * 
     * @param port The port to set
     */
    public void setPort(int port)
    {
        port_ = port;
    }

    /**
     * Sets the username.
     * 
     * @param username The username to set
     */
    public void setUsername(String username)
    {
        username_ = username;
    }

    //--------------------------------------------------------------------------
    // Overridden from java.lang.Object  
    //--------------------------------------------------------------------------
    
    /**
     * @return TelnetInfo as string
     */
    public String toString()
    {
        return 
            "TelnetInfo\n" + 
            "==========\n" +
            "Hostname: " + getHostname() + "\n" +
            "Port    : " + getPort() + "\n" +
            "Username: " + getUsername() + "\n" +
            "Password: " + getPassword() + "\n" +
            "Command : " + getCommand() + "\n";
    }
}