package toolbox.launcher;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import toolbox.util.ArrayUtil;

/**
 * Main class referenced by MANIFEST.MF in toolbox.jar. Provides convenient
 * way to run toolbox executables via 
 * <pre>
 * java -jar toolbox.jar [program name] [program args]
 * </pre>
 */
public class Main
{
    /** program name to class file map **/
    private static HashMap programMap_;
    
    static
    {
        /* map program names to class names */
        programMap_ = new HashMap(10);
        
        programMap_.put("findclass",     "toolbox.findclass.Main");
        programMap_.put("jfindclass",    "toolbox.findclass.JFindClass");
        programMap_.put("showclasspath", "toolbox.showclasspath.Main");
        programMap_.put("showpath",      "toolbox.showpath.Main");
        programMap_.put("jsourceview",   "toolbox.jsourceview.JSourceView");
        programMap_.put("jtail",         "toolbox.jtail.JTail");
        programMap_.put("tail",          "toolbox.tail.Main");
        programMap_.put("jtcptunnel",    "toolbox.tunnel.JTcpTunnel");
        programMap_.put("tcptunnel",     "toolbox.tunnel.TcpTunnel");
        programMap_.put("tree",          "toolbox.tree.Tree");
        programMap_.put("sqlviewer",     "toolbox.sqlviewer.SQLViewer");
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
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates the launcher with the given arguments
     * 
     * @param  args  First index is program name, remaining args are passed on
     *               to the program that is launched
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
                {
                    // Program not recognized
                    printUsage();
                }
                else
                {
                    // Get new arg list minus the first element
                    String[] newArgs = new String[0];
                    
                    if (args.length > 1)
                        newArgs = (String[]) ArrayUtil.subset(
                            args, 1, args.length - 1);
                  
                    // Launch program      
                    launch((String)programMap_.get(args[0]), newArgs);
                } 
                break;
        }
        
    }
    
    //--------------------------------------------------------------------------
    // Private
    //--------------------------------------------------------------------------
    
    /**
     * Launches a toolbox executable with the given classname and arguments
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
    protected void printUsage()
    {
        System.out.println(
            "Usage: java -jar toolbox.jar [program] [args]          \n" +
            "                                                       \n" +
            "       where program is:                               \n" +
            "                                                       \n" +
            "       findclass     => find a java class file         \n" + 
            "       showclasspath => show detailed classpath info   \n" +            
            "       showpath      => show detailed path info        \n" +            
            "       tail          => tails a file with follow       \n" +
            "       tcptunnel     => tcp tunnel                     \n" +
            "       tree          => prints directory as tree       \n" +
            "                                                       \n" +                        
            "       jfindclass    => gui version of findclass       \n" +
            "       jsourceview   => java code counter              \n" +
            "       jtail         => java tailer (GUI)              \n" +
            "       jtcptunnel    => tcp tunnel  (GUI)              \n" +
            "       sqlviewer     => unmangles sql statements       \n" );
    }
    
}
