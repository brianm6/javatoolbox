package toolbox.findclass.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import toolbox.findclass.FindClass;
import toolbox.findclass.FindClassResult;
import toolbox.util.ArrayUtil;

/**
 * Unit test for findclass
 */
public class FindClassTest extends TestCase
{
    /**
     * Test entry point
     * 
     * @param  args  Args
     */
    public static void main(String[] args)
    {
        TestRunner.run(FindClassTest.class);
    }

    /**
     * Arg constructor
     * 
     * @param arg  Name
     */
    public FindClassTest(String arg)
    {
        super(arg);
    }
    
    /**
     * Test finding a class in a jarfile
     * 
     * @throws  Exception on error
     */
    public void testFindInJar() throws Exception
    {
        FindClass finder = new FindClass();
        FindClassResult[] results = finder.findClass("Info$", false);
        
        System.out.println(ArrayUtil.toString(results, true));
    }

}