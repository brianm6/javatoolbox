package toolbox.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import toolbox.util.Banner;

/**
 * BannerTask is an Ant task thats converts text to an ASCII banner and writes 
 * the result to the standard output stream.
 * <p>
 * To use as an ant task, place the following in your ant script:
 * <pre class="snippet">
 * 
 *   &lt;taskdef 
 *     name=&quot;banner&quot; 
 *     classname=&quot;toolbox.ant.BannerTask
 *     classpath=&quot;/path/to/toolbox-ant.jar&quot; /&gt;
 *   
 *   &lt;target name=&quot;testBannerTask&quot;&gt;
 *     &lt;banner message=&quot;Yippee!&quot; /&gt;
 *   &lt;/target&gt;
 * 
 * </pre>
 * Sample Output:
 * <pre class="snippet">
 *    __   __  _                                 _ 
 *    \ \ / / (_)  _ __    _ __     ___    ___  | |
 *     \ V /  | | | '_ \  | '_ \   / _ \  / _ \ | |
 *      | |   | | | |_) | | |_) | |  __/ |  __/ |_|
 *      |_|   |_| | .__/  | .__/   \___|  \___| (_)
 *                |_|     |_|                      
 * </pre>
 * 
 * @see toolbox.util.Banner
 */
public class BannerTask extends Task
{
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /** 
     * The text of the banner.
     */
    private String msg_;

    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------
    
    /**
     * Sets the banner message.
     * 
     * @param msg Banner message.
     */
    public void setMessage(String msg)
    {
        msg_ = msg;
    }
    
    //--------------------------------------------------------------------------
    // Overrides org.apache.tools.ant.Task
    //--------------------------------------------------------------------------

    /**
     * Writes banner to System.out.
     * 
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute() throws BuildException
    {
        System.out.println(Banner.getBanner(msg_));
    }
}