package toolbox.showclasspath;

import junit.framework.TestCase;
import junit.textui.TestRunner;

/**
 * Unit test for {@link toolbox.showclasspath.Main}..
 */
public class MainTest extends TestCase
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
        TestRunner.run(MainTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests the main entry point.
     */
    public void testMain()
    {
        // Just run main..can't do much else
        Main.main(new String[0]);
    }
}