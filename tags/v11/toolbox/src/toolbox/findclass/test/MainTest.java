package toolbox.findclass.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import org.apache.log4j.BasicConfigurator;
import toolbox.findclass.Main;

/**
 * Unit test for findclass
 */
public class MainTest extends TestCase
{
    /**
     * Test entry point
     * 
     * @param  args  Args
     */
    public static void main(String[] args)
    {
        BasicConfigurator.configure();
        TestRunner.run(MainTest.class);
    }

    /**
     * Arg constructor
     * 
     * @param arg  Args
     */
    public MainTest(String arg)
    {
        super(arg);
    }
    
    /**
     * Test finding a class in a jarfile
     * 
     * @throws Exception on error
     */
    public void testFindInJar() throws Exception
    {
        Main.main(new String[] {  "filter$" } );
    }

}