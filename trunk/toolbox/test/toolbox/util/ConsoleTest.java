package toolbox.util;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.ui.console.AbstractConsole;

/**
 * Unit test for {@link toolbox.util.ui.console.AbstractConsole}.
 */
public class ConsoleTest extends TestCase
{
    private static final Logger logger_ = Logger.getLogger(ConsoleTest.class);

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
        TestRunner.run(ConsoleTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests handleCommand(classpath)
     */
    public void testHandleCommandClasspath()
    {
        logger_.info("Running testHandleCommandClasspath...");
        
        TestConsole console = new TestConsole();
        console.handleCommand(AbstractConsole.CMD_CLASSPATH);
    }
    
    
    /**
     * Tests handleCommand(help)
     */
    public void testHandleCommandHelp() 
    {
        logger_.info("Running testHandleCommandHelp...");
        
        TestConsole console = new TestConsole();
        console.handleCommand(AbstractConsole.CMD_HELP);
    }
    
    
    /**
     * Tests handleCommand(mem)
     */
    public void testHandleCommandMem() 
    {
        logger_.info("Running testHandleCommandMem...");
        
        TestConsole console = new TestConsole();
        console.handleCommand(AbstractConsole.CMD_MEM);
    }
    
    
    /**
     * Tests handleCommand(props)
     */
    public void testHandleCommandProps() 
    {
        logger_.info("Running testHandleCommandProps...");
        
        TestConsole console = new TestConsole();
        console.handleCommand(AbstractConsole.CMD_PROPS);
    }
    
    
    /**
     * Tests handleCommand(uptime)
     */
    public void testHandleCommandUptime() 
    {
        logger_.info("Running testHandleCommandUptime...");
        
        TestConsole console = new TestConsole();
        console.handleCommand(AbstractConsole.CMD_UPTIME);
    }
    
    
    /**
     * Tests handleCommand(setprop)
     */
    public void testHandleCommandSetProp() 
    {
        logger_.info("Running testHandleCommandSetProp...");
        
        TestConsole console = new TestConsole();

        String prop = "console.test";
        String value = "123";        
        
        console.handleCommand(
            AbstractConsole.CMD_SETPROP + " " + prop + " " + value);
            
        assertEquals("property should be set", value, System.getProperty(prop));
    }
    
    
    /**
     * Tests handleCommand(delprop)
     */
    public void testHandleCommandDelProp() 
    {
        logger_.info("Running testHandleCommandDelProp...");
        
        TestConsole console = new TestConsole();

        String prop = "console.delprop";
        String value = "123";        
        System.setProperty(prop, value);
        
        console.handleCommand(
            AbstractConsole.CMD_DELPROP + " " + prop);
            
        assertNull("property should be null", System.getProperty(prop));    
    }
    
    //--------------------------------------------------------------------------
    //  Inner Classes
    //--------------------------------------------------------------------------
    
    /**
     * Test implementation of console.
     */
    class TestConsole extends AbstractConsole
    {
        /**
         * @see toolbox.util.ui.console.AbstractConsole#getPrompt()
         */
        public String getPrompt()
        {
            return "TestConsole>";
        }
    }
}