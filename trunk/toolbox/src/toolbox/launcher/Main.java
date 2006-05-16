package toolbox.launcher;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import toolbox.util.ArrayUtil;
import toolbox.util.ResourceUtil;

/**
 * Entrypoint referenced by MANIFEST.MF in toolbox.jar which provides convenient
 * way to run toolbox executables contained in the jar file.
 * <p>
 * Usage:
 * <pre>
 * java -jar toolbox.jar <program name> <program args...> 
 * </pre>
 */
public class Main {
    
    //--------------------------------------------------------------------------
    // Static Fields
    //--------------------------------------------------------------------------
    
    /** 
     * Maps an executable's name to its fully qualified class name.
     */
    private static Map programMap_;
    
    //--------------------------------------------------------------------------
    // Static Blocks
    //--------------------------------------------------------------------------
    
    static
    {
        // Map program names to class names
        programMap_ = new HashMap(15);
        
        programMap_.put("dirmon",        "toolbox.dirmon.DirMon");
        programMap_.put("figlet",        "toolbox.util.Figlet");
        programMap_.put("findclass",     "toolbox.findclass.Main");
        programMap_.put("rtelnet",       "toolbox.rtelnet.RemoteTelnet");
        programMap_.put("showclasspath", "toolbox.showclasspath.Main");
        programMap_.put("showpath",      "toolbox.showpath.Main");
        programMap_.put("tail",          "toolbox.tail.Main");
        programMap_.put("tcptunnel",     "toolbox.tunnel.TcpTunnel");
        programMap_.put("tivo",          "toolbox.tivo.TivoConverter");
        programMap_.put("tree",          "toolbox.tree.Tree");
        programMap_.put("workspace",     "toolbox.workspace.PluginWorkspace");
        programMap_.put("laflauncher",   "toolbox.launcher.LAFLauncher");
        programMap_.put("ip2hostname",   "toolbox.ip2hostname.IP2Hostname");
    }
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Entrypoint.
     * 
     * @param args See below.
     *        args[0] Program name.
     *        args[1..n] Program arguments.
     */
    public static void main(String[] args)
    {
        new Main(args);
    }
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates the launcher with the given arguments.
     * 
     * @param args First index is program name, remaining args are passed on
     *        to the program that is launched.
     */
    public Main(String[] args)
    {
        switch (args.length)
        {
            case 0 :
            
                printUsage();    
                break;
            
                
            default :
            
                if (!programMap_.containsKey(args[0]))
                    printUsage();
                else
                {
                    // Get new arg list minus the first element
                    String[] newArgs = new String[0];
                    
                    if (args.length > 1)
                        newArgs = (String[]) 
                            ArrayUtil.subset(args, 1, args.length - 1);
                  
                    // Launch program      
                    launch((String) programMap_.get(args[0]), newArgs);
                }
                break; 
        }    
    }
    
    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------
    
    /**
     * Launches a toolbox executable with the given classname and arguments.
     * 
     * @param className Name of class to launch.
     * @param args Arguments.
     */
    protected void launch(String className, String[] args)
    {
        try 
        {
            Class c = Class.forName(className);
            
            Method m = c.getMethod("main", 
                new Class[] {(new String[0]).getClass()});
                
            m.invoke(null, new Object[] {args});
        } 
        catch (Throwable e) 
        {
            System.out.println("Error: " + e);
            e.printStackTrace();
        }        
    }
    
    
    /**
     * Prints launcher usage.
     */
    protected void printUsage()
    {
        Properties p = new Properties();
        
        StringBuffer sb = new StringBuffer();
        
        try {
            p.load(ResourceUtil.getResource("version.properties"));
            sb.append("Version " + p.getProperty("toolbox.version"));
            //sb.append(" Build " + p.getProperty("toolbox.build.number"));
            sb.append(" Date " + p.getProperty("toolbox.build.date"));
        }
        catch(IOException ioe) {
        }
        
        System.out.println(
            "Toolbox " + sb.toString() + "\n" +        		
            "Usage: java -jar toolbox.jar [program] [args]          \n" +
            "                                                       \n" +
            "       where program is:                               \n" +
            "                                                       \n" +
            "       dirmon        => directory monitor              \n" +
            "       figlet        => creates a text banner          \n" +
            "       findclass     => find a java class file         \n" +
            "       ip2hostname   => replace ips with hostnames     \n" +
            "       rtelnet       => executes a telnet command      \n" +
            "       showclasspath => show detailed classpath info   \n" +
            "       showpath      => show detailed path info        \n" +
            "       tail          => tails a file with follow       \n" +
            "       tcptunnel     => tcp tunnel                     \n" +
            "       tivo          => tivo movie transcoder          \n" +
            "       tree          => prints directory as tree       \n" +
            "                                                       \n" +
            "       laflauncher   => launches program with look and feel\n" +
            "       workspace     => plugin workspace               \n");
    }
}