package toolbox.util;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.Figlet;

/**
 * Unit test for Figlet.
 */
public class FigletTest extends TestCase
{
    private static final Logger logger_ = Logger.getLogger(FigletTest.class);

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
        TestRunner.run(FigletTest.class);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests 
    //--------------------------------------------------------------------------
        
    /**
     * Tests figlet.
     * 
     * @throws Exception on error.
     */
    public void testFiglet() throws Exception
    {
        logger_.info("Running testFiglet...");
        
        String s = Figlet.getBanner("Howdy!");
        logger_.info("\n" + s);
    }
    
    
    /**
     * Tests main().
     */
    public void testMain()
    {
        logger_.info("Running testMain...");
        
        Figlet.main(new String[0]);
        Figlet.main(new String[] {"-h"});        
        Figlet.main(new String[] {"Ummmm...!"});
        Figlet.main(new String[] {"-l", "Donuts!"});
        Figlet.main(new String[] {"-s", "One doh per line"});
        Figlet.main(new String[] {"-s", "-l", "Byte code"});
        Figlet.main(new String[] {"-w", "120", "[this a big line 120]"});
    }
}