package toolbox.ant.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.tools.ant.Main;

/**
 * Unit test for BannerTask
 */
public class BannerTaskTest extends TestCase
{
    /**
     * Entrypoint
     * 
     * @param  args  None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(BannerTaskTest.class);
    }
    
    public BannerTaskTest(String name)
    {
        super(name);
    }
    
    public void testBannerTask()
    {
        //-Dant.home=c:\ant 
        
        System.out.println("Anthome=" + System.getProperty("ant.home"));
        System.out.println("userhome=" + System.getProperty("user.home"));
        System.out.println("userdir=" + System.getProperty("user.dir"));
        
        String args[] =
        {
            "-f", "resources/toolbox/ant/test/BannerTaskTest.xml"
        };
            
        Main.main(args); 
        
        //[options] [target]
    }

}
