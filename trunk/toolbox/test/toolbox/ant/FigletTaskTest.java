package toolbox.ant;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.tools.ant.Main;

import toolbox.junit.testcase.StandaloneTestCase;

/**
 * Unit test for FigletTask. This test is marked as standalone because the call
 * into Ant results in call to System.exit() on completion.
 */
public class FigletTaskTest extends TestCase implements StandaloneTestCase
{
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Entrypoint.
     * 
     * @param args None recognized.
     */
    public static void main(String[] args)
    {
        TestRunner.run(FigletTaskTest.class);
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
            "-f", "resources/toolbox/ant/test/FigletTaskTest.xml"
        };
            
        Main.main(args); 
        
        //[options] [target]
    }
}