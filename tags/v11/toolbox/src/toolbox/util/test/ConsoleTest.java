package toolbox.util.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import org.apache.log4j.Category;
import toolbox.util.Console;

/**
 * Unit test for Console
 */
public class ConsoleTest extends TestCase
{
    /** Logger **/
    private static final Category logger_ = 
        Category.getInstance(ConsoleTest.class);

    /**
     * Test implementation of console
     */
    class TestConsole extends Console
    {
        public String getPrompt()
        {
            return "TestConsole>";
        }
    }
    
    /**
     * Constructor for ConsoleTest.
     * 
     * @param name  Test name
     */
    public ConsoleTest(String name)
    {
        super(name);
    }
    
    /**
     * Entrypoint
     *
     * @param  args  Arguments
     */
    public static void main(String[] args)
    {
        TestRunner.run(ConsoleTest.class);
    }
    
    /**
     * Tests handleCommand(classpath)
     */
    public void testHandleCommandClasspath()
    {
        TestConsole console = new TestConsole();
        console.handleCommand(Console.CMD_CLASSPATH);
    }
    
    /**
     * Tests handleCommand(help)
     */
    public void testHandleCommandHelp() 
    {
        TestConsole console = new TestConsole();
        console.handleCommand(Console.CMD_HELP);
    }
    
    /**
     * Tests handleCommand(mem)
     */
    public void testHandleCommandMem() 
    {
        TestConsole console = new TestConsole();
        console.handleCommand(Console.CMD_MEM);
    }
    
    /**
     * Tests handleCommand(props)
     */
    public void testHandleCommandProps() 
    {
        TestConsole console = new TestConsole();
        console.handleCommand(Console.CMD_PROPS);
    }
    
    /**
     * Tests handleCommand(uptime)
     */
    public void testHandleCommandUptime() 
    {
        TestConsole console = new TestConsole();
        console.handleCommand(Console.CMD_UPTIME);
    }
    
    /**
     * Tests handleCommand(setprop)
     */
    public void testHandleCommandSetProp() 
    {
        TestConsole console = new TestConsole();

        String prop = "console.test";
        String value = "123";        
        
        console.handleCommand(
            Console.CMD_SETPROP + " " + prop + " " + value);
            
        assertEquals("property should be set", value, System.getProperty(prop));    
    }
    
    /**
     * Tests handleCommand(delprop)
     */
    public void testHandleCommandDelProp() 
    {
        TestConsole console = new TestConsole();

        String prop = "console.delprop";
        String value = "123";        
        System.setProperty(prop, value);
        
        console.handleCommand(
            Console.CMD_DELPROP + " " + prop);
            
        assertNull("property should be null", System.getProperty(prop));    
    }
}