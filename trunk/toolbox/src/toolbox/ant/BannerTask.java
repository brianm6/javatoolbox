package toolbox.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import toolbox.util.Banner;

/**
 * Task thats converts text to an ASCII banner.
 */
public class BannerTask extends Task
{
    /** Banner message */
    private String msg_;
    
    /**
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute() throws BuildException
    {
        System.out.println(Banner.getBanner(msg_));
    }
    
    /**
     * Sets the banner message
     * 
     * @param  msg  Banner message
     */
    public void setMessage(String msg)
    {
        msg_ = msg;
    }
}