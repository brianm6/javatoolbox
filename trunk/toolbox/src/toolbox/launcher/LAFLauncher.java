package toolbox.launcher;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import toolbox.util.ArrayUtil;
import toolbox.util.ThreadUtil;
import toolbox.util.ui.plaf.LookAndFeelUtil;

/**
 * Launches a java program with a predetermined Look and Feel.
 */
public class LAFLauncher
{
    private static final Logger logger_ = Logger.getLogger(LAFLauncher.class);
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Entrypoint.
     * 
     * @param args [0] = Name of class to launch
     *            [1..n] = Command line arguments
     * @throws Exception on error
     */
    public static void main(String args[]) throws Exception
    {
        switch (args.length)
        {
            case 0: 
            
                printUsage(); 
                break;
                
            case 1: 
                
                launch(args[0], new String[0]); 
                break;
                
            default: 
            
                launch(
                    args[0], 
                    (String[]) ArrayUtil.subset(args, 1, args.length-1));
                    
                break;
        }
    }
    
    //--------------------------------------------------------------------------
    // Private
    //--------------------------------------------------------------------------
    
    /**
     * Prints program usage.
     */
    private static void printUsage()
    {
        System.out.println(
            "Usage  : java " + LAFLauncher.class.getName() + 
            " classname [args]");
            
        System.out.println(
            "Example: java " + LAFLauncher.class.getName() + 
            " org.myapp.Main -debug blah");
    }


    /**
     * Launches target class.
     * 
     * @param target Name of class with main() to launch 
     * @param args Array of arguments
     * @throws Exception on error
     */
    private static void launch(String target, String[] args) throws Exception
    {
        LookAndFeelUtil.setPreferredLAF();
    
        Thread t = new Thread(new LAFRunner());
        t.start();
                                 
        Class c = Class.forName(target);
        Method m = c.getMethod("main", new Class[] { args.getClass() });
        m.invoke(null, new Object[] {args});
    }
}

class LAFRunner implements Runnable
{
    public void run()
    {
        ThreadUtil.sleep(30000);
        System.out.println("Setting LAF delayed.");
            
        try
        {
            LookAndFeelUtil.setPreferredLAF();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
        }
    }
}