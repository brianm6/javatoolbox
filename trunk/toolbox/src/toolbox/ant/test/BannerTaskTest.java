package toolbox.ant.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.tools.ant.Main;

import toolbox.junit.StandaloneTestCase;

/**
 * Unit test for BannerTask. This test is marked as standalone because the call
 * into Ant results in call to System.exit() on completion.
 */
public class BannerTaskTest extends TestCase implements StandaloneTestCase
{
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Entrypoint.
     * 
     * @param args None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(BannerTaskTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------

    /**
     * Tests the banner task.
     */        
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