package toolbox.util.ui.console;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import toolbox.util.ClassUtil;
import toolbox.util.ThreadUtil;

/**
 * Basic set of commands supported by a Console out of the box. The following 
 * commands are supported: 
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
 */
public class DefaultCommandHandler implements CommandHandler
{
    private static final Logger logger_ = 
        Logger.getLogger(DefaultCommandHandler.class);
    
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
     * Console that is delegating the execution of commands to us.
     */
    private Console console_;
    
    /**
     * Start time of the console..used for the uptime command.
     */
    private long startTime_;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a DefaultCommandHandler.
     */
    public DefaultCommandHandler()
    {
        startTime_ = System.currentTimeMillis();
    }

    //--------------------------------------------------------------------------
    // CommandHandler Interface
    //--------------------------------------------------------------------------
    
    /**
     * @see toolbox.util.ui.console.CommandHandler#handleCommand(
     *      toolbox.util.ui.console.Console, java.lang.String)
     */
    public void handleCommand(Console console, String cmd)
        throws Exception
    {
        setConsole(console);
        
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
            println("Unknown command: " + cmd);
    }

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Returns the console.
     * 
     * @return Console
     */
    public Console getConsole()
    {
        return console_;
    }
    
    
    /**
     * Sets the console.
     * 
     * @param console Console
     */
    public void setConsole(Console console)
    {
        console_ = console;
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Convenience method to print text to the console.
     * 
     * @param s Text to print.
     */
    protected void print(String s)
    {
        getConsole().write(s);
    }

    
    /**
     * Convenience method to print text to the console.
     * 
     * @param s Text to print.
     */
    protected void println(String s)
    {
        getConsole().write(s + "\n");
    }
    
    //--------------------------------------------------------------------------
    // Commands
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
            println("setprop <property name> <value>");
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
            println("delprop <property name>");
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
        println("Goodbye!");
        System.exit(0);
    }

    
    /**
     * Detachs the console from the input/output streams.
     */
    protected void commandDetach()
    {
        //detach from the input stream for 10 secs so Ctrl-C can work so the VM
        //can be dumped without exiting or causing termination
        
        int delay = 10000;
        println("Detaching from inputstream for " + delay / 1000 + " secs..");
        ThreadUtil.sleep(delay);
        println("Re-attached to inputstream.");
    }
    
    
    /**
     * Print help info.
     */
    protected void commandHelp()
    {
        println("classpath  => prints out classpath information");
        println("delprop    => removes a properties from system properties");
        println("detach     => detaches from System.in so Ctrl-C can"
                 + "trigger a VM dump");
        println("mem        => prints out runtime memeory info");
        println("help       => prints help info");
        println("props      => prints out System properties");
        println("quit       => quits the program");
        println("setprop    => adds/changes the value of a system property");
        println("uptime     => shows how long process has been running");
    }


    /**
     * Prints command history.
     */
    protected void commandHistory()
    {
        println("Work in progress...");
        
//        Object[] cmds = history_.toArray();
//        
//        for (int i = 0; i < cmds.length - 1; 
//            getPrintStream().println(cmds[i++].toString()));
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
            println(StringUtils.left(name, max + 1) + value);
        }
    }

    
    /**
     * Prints out runtime memory allocation.
     */
    protected void commandMem()
    {
        println("Free memory  " + Runtime.getRuntime().freeMemory());
        println("Total memory " + Runtime.getRuntime().totalMemory());
    }

    
    /**
     * Print out classpath information.
     */
    protected void commandClasspath()
    {
        String[] elements = ClassUtil.getClassPathElements();
        for (int i = 0; i < elements.length; println(elements[i++]));
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

        println(sb.toString());
    }
}