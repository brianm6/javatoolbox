package toolbox.util.test;

import java.net.URL;

import toolbox.util.Banner;

import junit.framework.TestCase;
import junit.textui.TestRunner;

/**
 * Unit Test for Banner
 */
public class BannerTest extends TestCase
{
    
    public static void main(String[] args)
    {
        TestRunner.run(BannerTest.class);
    }


    /**
     * Constructor for FigletFontTest.
     * @param arg0
     */
    public BannerTest(String arg0)
    {
        super(arg0);
    }

    
    /**
     * Tests figlet
     */
    public void testFiglet() throws Exception
    {
        String s = Banner.convert("Howdy!");
        System.out.println(s);
    }
}