package toolbox.launcher;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import toolbox.util.ArrayUtil;

/**
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 */
public class Main
{
    /** program name to class file map **/
    private static HashMap programMap;
    
    static
    {
        /* map program names to class names */
        programMap = new HashMap(10);
        
        programMap.put("findclass",     "toolbox.findclass.Main");
        programMap.put("showclasspath", "toolbox.showclasspath.Main");
        programMap.put("showpath",      "toolbox.showpath.Main");
        programMap.put("jsourceview",   "toolbox.jsourceview.JSourceView");
        programMap.put("jtail",         "toolbox.jtail.Main");
        programMap.put("tail",          "toolbox.tail.Main");
        programMap.put("jtcptunnel",    "toolbox.tcptunnel.JTcpTunnel");
        programMap.put("tcptunnel",     "toolbox.tcptunnel.TcpTunnel");
    }
    
    /**
     * Entrypoint 
     * 
     * @param  args  Args
     */
    public static void main(String[] args)
    {
        Main launcher = new Main(args);
    }
    
    /**
     * Constructor for Main.
     */
    public Main()
    {
        super();
    }
    
    /**
     * Arg constructor 
     * 
     * @param  args  arguments
     */
    public Main(String[] args)
    {
        switch (args.length)
        {
            case 0 :
                printUsage();    
                break;
                
            default :
                
                if (!programMap.containsKey(args[0]))
                    printUsage();
                else
                {
                    String[] newArgs = new String[0];
                    
                    if(args.length > 1)
                        newArgs = (String[]) ArrayUtil.subset(
                            args, 1, args.length - 1);
                        
                    launch((String)programMap.get(args[0]), newArgs);
                } 
                break;
        }
        
    }
    
    /**
     * Launches the programs
     * 
     * @param  className   Name of class to launch
     * @param  args        Arguments
     */
    protected void launch(String className, String[] args)
    {
          
        try 
        {
            Class c = Class.forName(className);
            
            Method m = c.getMethod("main", 
                new Class[] { (new String[0]).getClass() });
                
            m.invoke(null, new Object[] { args } );
        } 
        catch(SecurityException e) 
        {
            System.out.println("Error: " + e);
            e.printStackTrace();
        } 
        catch(ClassNotFoundException e) 
        {
            System.out.println("Error: " + e);
            e.printStackTrace();
        } 
        catch(NoSuchMethodException e) 
        {
            System.out.println("Error: " + e);
            e.printStackTrace();
        }
        catch(IllegalAccessException e) 
        {
            System.out.println("Error: " + e);
            e.printStackTrace();
        } 
        catch(InvocationTargetException e) 
        {
            System.out.println("Error: " + e);
            e.printStackTrace();
        }        
    }
    
    
    /**
     * Prints launcher usage
     */
    public void printUsage()
    {
        System.out.println(
            "Usage: java -jar toolbox.jar [program] [args]          \n" +
            "                                                       \n" +
            "       where program is:                               \n" +
            "                                                       \n" +
            "       findclass     => find a java class file         \n" + 
            "       showclasspath => show detailed classpath info   \n" +
            "       showpath      => show detailed path info        \n" +
            "       jsourceview   => java code counter              \n" +
            "       jtail         => java tailer (GUI)              \n" +
            "       tail          => java tail   (console)          \n" +
            "       jtcptunnel    => tcp tunnel  (GUI)              \n" +
            "       tcptunnel     => tcp tunnel  (console)          \n" );
    }
    
}
