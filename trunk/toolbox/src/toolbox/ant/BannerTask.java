package toolbox.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import toolbox.util.Banner;

/**
 * ANT Task thats converts text to an ASCII banner.
 */
public class BannerTask extends Task
{
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
     * @param msg Banner message
     */
    public void setMessage(String msg)
    {
        msg_ = msg;
    }
    
    //--------------------------------------------------------------------------
    // Overrides org.apache.tools.ant.Task
    //--------------------------------------------------------------------------

    /**
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute() throws BuildException
    {
        System.out.println(Banner.getBanner(msg_));
    }
}