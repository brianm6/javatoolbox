package toolbox.util.ui;

import java.io.IOException;

import org.apache.log4j.Logger;

import toolbox.util.ExceptionUtil;
import toolbox.util.Platform;

/**
 * Native web browser launcher
 */
public class NativeBrowser
{
    private static final Logger logger_ = Logger.getLogger(NativeBrowser.class);
    
    /** The default system browser under windows */
    private static final String WIN_PATH = "rundll32";
    
    /** The flag to display a url */
    private static final String WIN_FLAG = "url.dll,FileProtocolHandler";
    
    /** The default browser under unix */
    private static final String UNIX_PATH = "netscape";
    
    /** The flag to display a url */
    private static final String UNIX_FLAG = "-remote openURL";
    
    //--------------------------------------------------------------------------
    // Public 
    //--------------------------------------------------------------------------
    
    /**
     * Display a file in the system browser. If you want to display a
     * file, you must include the absolute path name.
     * 
     * @param  url  URL to launch
     */
    public static void displayURL(String url)
    {
        boolean windows = Platform.isWindows();
        
        String cmd = null;
        
        try
        {
            if (windows)
            {
                // cmd = 'rundll32 url.dll,FileProtocolHandler http://...'
                cmd = WIN_PATH + " " + WIN_FLAG + " " + url;
                Process p = Runtime.getRuntime().exec(cmd);
            }
            else
            {
                // Under Unix, Netscape has to be running for the "-remote"
                // command to work. So, we try sending the command and
                // check for an exit value. If the exit command is 0,
                // it worked, otherwise we need to start the browser.
                // cmd = 'netscape -remoteopenURL(http://www.javaworld.com)'
                cmd = UNIX_PATH + " " + UNIX_FLAG + "(" + url + ")";
                Process p = Runtime.getRuntime().exec(cmd);
                try
                {
                    // wait for exit code -- if it's 0, command worked,
                    // otherwise we need to start the browser up.
                    int exitCode = p.waitFor();
                    if (exitCode != 0)
                    {
                        // Command failed, start up the browser
                        // cmd = 'netscape http://www.javaworld.com'
                        cmd = UNIX_PATH + " " + url;
                        p = Runtime.getRuntime().exec(cmd);
                    }
                }
                catch (InterruptedException x)
                {
                    ExceptionUtil.handleUI(x, logger_);
                    
                    //System.err.println(
                    //    "Error bringing up browser, cmd='" + cmd + "'");
                }
            }
        }
        catch (IOException x)
        {
            // couldn't exec browser
            ExceptionUtil.handleUI(x, logger_);
            
            //System.err.println("Could not invoke browser, command=" + cmd);
            //System.err.println("Caught: " + x);
        }
    }
}