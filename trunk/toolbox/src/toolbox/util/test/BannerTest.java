package toolbox.util.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.Banner;

/**
 * Unit test for Banner
 */
public class BannerTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(BannerTest.class);

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Entrypoint
     * 
     * @param args None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(BannerTest.class);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests 
    //--------------------------------------------------------------------------
        
    /**
     * Tests figlet
     * 
     * @throws Exception on error
     */
    public void testFiglet() throws Exception
    {
        logger_.info("Running testFiglet...");
        
        String s = Banner.getBanner("Howdy!");
        logger_.info("\n" + s);
    }
    
    /**
     * Tests main()
     */
    public void testMain()
    {
        logger_.info("Running testMain...");
        
        Banner.main(new String[0]);
        Banner.main(new String[] {"-h"});        
        Banner.main(new String[] {"Ummmm...!"});
        Banner.main(new String[] {"-l", "Donuts!"});
        Banner.main(new String[] {"-s", "One doh per line"});
        Banner.main(new String[] {"-s", "-l", "Byte code"});
        Banner.main(new String[] {"-w", "120", "[this a big line 120]"});
    }
}