package toolbox.util.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.Banner;

/**
 * Unit Test for Banner
 */
public class BannerTest extends TestCase
{
    /** Logger **/
    private static final Logger logger_ = Logger.getLogger(BannerTest.class);
    
    /**
     * Entrypoint
     * 
     * @param  args  None
     */
    public static void main(String[] args)
    {
        TestRunner.run(BannerTest.class);
    }


    /**
     * Constructor for FigletFontTest.
     * 
     * @param arg0  Name
     */
    public BannerTest(String arg0)
    {
        super(arg0);
    }

    
    /**
     * Tests figlet
     * 
     * @throws Exception on error
     */
    public void testFiglet() throws Exception
    {
        String s = Banner.convert("Howdy!");
        logger_.info("\n" + s);
    }
}