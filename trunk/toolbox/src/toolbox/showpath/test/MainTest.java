package toolbox.showpath.test;

import toolbox.showpath.Main;
import junit.framework.TestCase;
import junit.textui.TestRunner;

/**
 * Unit test for toolbox.showpath.Main
 */
public class MainTest extends TestCase
{
	/**
	 * Entrypoint
	 * 
	 * @param  args  None recognized
	 */	
	public static void main(String[] args)
	{
		TestRunner.run(MainTest.class);
	}

	//--------------------------------------------------------------------------
	// Constructors
	//--------------------------------------------------------------------------
	
    /**
     * Constructor for MainTest.
     * 
     * @param arg0
     */
    public MainTest(String arg0)
    {
        super(arg0);
    }

	//--------------------------------------------------------------------------
	// Unit Tests
	//--------------------------------------------------------------------------
	
	/**
	 * Tests the main entry point
	 */
    public void testMain()
    {
    	// Just run main..can't do much else
    	
    	Main.main(new String[0]);
    }
}