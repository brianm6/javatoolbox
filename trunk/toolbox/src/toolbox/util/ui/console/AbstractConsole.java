package toolbox.util.ui.console;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * The purpose of the Console is to easily facilitate the execution of
 * commands in an interactive text based environment. The following commands 
 * are supported: 
 * <ul>
 *  <li>classpath - shows classpath information.
 *  <li>delprop   - removes a property from the system properties.
 *  <li>detach    - detaches the input stream from the console so Ctrl-C works 
 *                  to get a JVM dump.
 *  <li>help      - shows help on the available commands.
 *  <li>history   - shows a history of executed commands  
 *  <li>mem       - shows heap usage information.  
 *  <li>props     - shows the system properties.
 *  <li>quit/exit - exits the JVM.
 *  <li>setprop   - sets/adds a property to the system properties.
 *  <li>uptime    - shows the uptime of this process.
 * </ul>
 * <br>
 * To add a new command, just override handleCommand() in your subclass of 
 * Console and add interceptors for whatever commands you would like 
 * to support.
 */
public abstract class AbstractConsole implements Console
{
    private static final Logger logger_ = 
        Logger.getLogger(AbstractConsole.class);
    
    //--------------------------------------------------------------------------
    // Commands
    //--------------------------------------------------------------------------
    
    /**
     * Command to show help. 
     */
    public static final String CMD_HELP = "help";
    
    /** 
     * Command to exit the jvm. 
     */
    public static final String CMD_QUIT = "quit";
    
    /** 
     * Command to exit the jvm. 
     */
    public static final String CMD_EXIT = "exit";
    
    /** 
     * Command to show the classpath. 
     */
    public static final String CMD_CLASSPATH = "classpath";
    
    /** 
     * Command to show the system properties. 
     */
    public static final String CMD_PROPS = "props";
    
    /** 
     * Command to show memory consumption. 
     */
    public static final String CMD_MEM = "mem";
    
    /** 
     * Command to detach the console from the input stream. 
     */
    public static final String CMD_DETACH = "detach";
    
    /** 
     * Command to add a property to System.properties. 
     */
    public static final String CMD_SETPROP = "setprop";
    
    /** 
     * Command to remove a property from System.properties. 
     */
    public static final String CMD_DELPROP = "delprop";

    /** 
     * Command to show how long the console has been active.
     */
    public static final String CMD_UPTIME = "uptime";
    
    /** 
     * Command to show the history of previously executed commands.
     */
    public static final String CMD_HISTORY = "history";
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /** 
     * Source of commands. 
     */    
    private LineNumberReader reader_;
    
    /** 
     * Output of command results. 
     */
    private PrintStream ps_;

    /** 
     * Time console was created.
     */
    private long startTime_;
    
    /**
     * Command history.
     */
    private List history_;

    /**
     * Name of this console.
     */
    private String name_;
    
    private InputStream is_;
    private OutputStream os_;
    
    //--------------------------------------------------------------------------
    // Constructors 
    //--------------------------------------------------------------------------
    
    /**
     * Create an InteractiveConsole with System.in and System.out as the default
     * in/out streams.
     */
    public AbstractConsole()
    {
        this(System.in, System.out);
    }

    
    /**
     * Creates a Console with the given input and output streams.
     * 
     * @param is Input stream to read commands from.
     * @param os Output stream to write command results to.
     */
    public AbstractConsole(InputStream is, OutputStream os)
    {
        this("", is, os);
    }

    
    /**
     * Creates a Console with the given input and output streams.
     * 
     * @param name Name of this console.
     * @param is Input stream to read commands from.
     * @param os Output stream to write command results to.
     */
    public AbstractConsole(String name, InputStream is, OutputStream os)
    {
        setName(name);
        startTime_ = Calendar.getInstance().getTime().getTime();
        ps_        = new PrintStream(os);
        reader_    = new LineNumberReader(new InputStreamReader(is));
        history_   = new ArrayList();
        is_ = is;
        os_ = os;
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
 
    /**
     * This method must be called when ready to handle commands.
     */
    public void startConsole()
    {
        new Thread(new Runnable() {
            
            public void run()
            {
                while (true)
                {
                    try
                    {
                        String command = getNextCommand();
                        logger_.debug("Read command = " + command);
                        handleCommand(command);
                    }
                    catch (Exception ex)
                    {
                        logger_.error("run", ex);
                    }
                }
            }
        }).start();
    }
      
    
    /**
     * Accessor for the print stream that all output is sent to.
     * 
     * @return PrintStream.
     */
    public PrintStream getPrintStream()
    {
        return ps_;
    }


    /**
     * @see toolbox.util.ui.console.Console#getInputStream()
     */
    public InputStream getInputStream()
    {
        return is_;
    }
    
    /**
     * @see toolbox.util.ui.console.Console#getOutputStream()
     */
    public OutputStream getOutputStream()
    {
        return os_;
    }
    
    
    
    /**
     * Retrieves the next command from the input stream.
     * 
     * @return Next command.
     */
    public String getNextCommand()
    {
        String cmd = null;

        try
        {
            getPrintStream().print(getPrompt());
            cmd = reader_.readLine();
        }
        catch (IOException io)
        {
            logger_.error("getNextcommand", io);
        }

        history_.add(cmd);
        return cmd;
    }        
    
    
    /**
     * Handles the command. 
     * 
     * @param cmd Command to handle.
     */
    public void handleCommand(String cmd) 
    {
        if (cmd.equals(CMD_QUIT) || cmd.equals(CMD_EXIT))
            commandQuit();
        else if (cmd.equals(CMD_CLASSPATH))
            commandClasspath();
        else if (cmd.equals(CMD_HELP))
            commandHelp();
        else if (cmd.equals(CMD_PROPS))
            commandProps();
        else if (cmd.equals(CMD_MEM))
            commandMem();
        else if (cmd.equals(CMD_DETACH))
            commandDetach();
        else if (cmd.startsWith(CMD_SETPROP))
            commandSetProp(cmd);
        else if (cmd.startsWith(CMD_DELPROP))
            commandDelProp(cmd);
        else if (cmd.equals(CMD_UPTIME))
            commandUptime();
        else if (cmd.equals(CMD_HISTORY))
            commandHistory();            
        else
            getPrintStream().println("Unknown command: " + cmd);
        
        getPrintStream().flush();
    }


    /**
     * Returns the command history.
     * 
     * @return Buffer
     */
    public List getHistory()
    {
        return history_;
    }

    //--------------------------------------------------------------------------
    // Nameable Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.service.Nameable#getName()
     */
    public String getName()
    {
        return name_;
    }
    
    /**
     * @see toolbox.util.service.Nameable#setName(java.lang.String)
     */
    public void setName(String name)
    {
        name_ = name;
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /**
     * Adds/sets a property to system properties.
     * 
     * @param cmd Original command so we can extract prop name/value.
     */
    protected void commandSetProp(String cmd)
    {
        StringTokenizer st = new StringTokenizer(cmd, " ");

        if (st.countTokens() != 3)
            getPrintStream().println("setprop <property name> <value>");
        else
        {
            st.nextToken();
            System.getProperties().setProperty(st.nextToken(), st.nextToken());
        }
    }

    
    /**
     * Removes a system property.
     * 
     * @param cmd Original command so we can extract prop name.
     */
    protected void commandDelProp(String cmd)
    {
        StringTokenizer st = new StringTokenizer(cmd, " ");

        if (st.countTokens() != 2)
            ps_.println("delprop <property name>");
        else
        {
            st.nextToken();
            System.getProperties().remove(st.nextToken());
        }
    }

    
    /**
     * Exits the virtual machine.
     */
    protected void commandQuit()
    {
        getPrintStream().println("Goodbye!");
        System.exit(0);
    }

    
    /**
     * Detachs the console from the input/output streams.
     */
    protected void commandDetach()
    {
        /**
         * detach from the input stream for 10 secs so Ctrl-C can work so the VM
         * can be dumped without exiting or causing termination
         */
        int numSecs = 10000;

        getPrintStream().println(
            "Detaching from inputstream for " + numSecs / 1000 + " secs...");

        try
        {
            Thread.sleep(10000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        getPrintStream().println("Re-attached to inputstream.");
    }
    
    
    /**
     * Print help info.
     */
    protected void commandHelp()
    {
        PrintStream ps = getPrintStream();
        ps.println("classpath  => prints out classpath information");
        ps.println("delprop    => removes a properties from system properties");
        ps.println("detach     => detaches from System.in so Ctrl-C can"
                 + "trigger a VM dump");
        ps.println("mem        => prints out runtime memeory info");
        ps.println("help       => prints help info");
        ps.println("props      => prints out System properties");
        ps.println("quit       => quits the program");
        ps.println("setprop    => adds/changes the value of a system property");
        ps.println("uptime     => shows how long process has been running");
    }


    /**
     * Prints command history.
     */
    protected void commandHistory()
    {
        Object[] cmds = history_.toArray();
        
        for (int i = 0; i < cmds.length - 1; 
            getPrintStream().println(cmds[i++].toString()));
    }
    
    
    /**
     * Prints out all system properties in alphabetical order.
     */
    protected void commandProps()
    {
        Properties props = System.getProperties();

        List list = new ArrayList();

        int max = 0;

        for (Enumeration e = props.propertyNames(); e.hasMoreElements();)
        {
            String name = (String) e.nextElement();

            // keep track of max length to line columns up
            if (name.length() > max)
                max = name.length();
            list.add(name);
        }

        // looks nicer sorted by property name
        Object[] arr = list.toArray();
        Arrays.sort(arr);

        for (int i = 0; i < arr.length; i++)
        {
            String name = (String) arr[i];
            String value = props.getProperty(name);
            getPrintStream().println(StringUtils.left(name, max + 1) + value);
        }
    }

    
    /**
     * Prints out runtime memory allocation.
     */
    protected void commandMem()
    {
        getPrintStream().println(
            "Free memory  " + Runtime.getRuntime().freeMemory());

        getPrintStream().println(
            "Total memory " + Runtime.getRuntime().totalMemory());
    }

    
    /**
     * Print out classpath information.
     */
    protected void commandClasspath()
    {
        StringBuffer sb = new StringBuffer();
        String path = System.getProperty("java.class.path");

        for (StringTokenizer st = new StringTokenizer(path, 
            System.getProperty("path.separator")); st.hasMoreTokens();)
        {
            sb.append(" " + st.nextToken() + "\n");
        }

        getPrintStream().print(sb.toString());
    }
    
    
    /**
     * Prints out the uptime.
     */
    protected void commandUptime()
    {
        long currentTime = Calendar.getInstance().getTime().getTime();
        long delta = currentTime - startTime_;

        long milli = 1;
        long second = 1000 * milli;
        long minute = 60 * second;
        long hour = 60 * minute;
        long day = 24 * hour;

        long days = delta / day;
        delta -= days * day;
        long hours = delta / hour;
        delta -= hours * hour;
        long minutes = delta / minute;
        delta -= minute * minutes;
        long seconds = delta / second;
        delta -= second * seconds;
        long millis = delta;

        StringBuffer sb = new StringBuffer();

        if (days > 0)
            sb.append(days + "d ");
        if (hours > 0)
            sb.append(hours + "h ");
        if (minutes > 0)
            sb.append(minutes + "m ");
        if (seconds > 0)
            sb.append(seconds + "s ");
        if (millis > 0)
            sb.append(millis + "ms");

        getPrintStream().println(sb.toString());
    }
}