package toolbox.util.ui.console;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

/**
 * Unit test for {@link toolbox.util.ui.console.DefaultCommandHandler}.
 */
public class DefaultCommandHandlerTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(DefaultCommandHandlerTest.class);

    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /**
     * Mock console.
     */
    private Console console_;

    /**
     * Handler to test.
     */
    private CommandHandler handler_;
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Entrypoint
     *
     * @param args None recognized.
     */
    public static void main(String[] args)
    {
        TestRunner.run(DefaultCommandHandlerTest.class);
    }

    //--------------------------------------------------------------------------
    // Overrides TestCase
    //--------------------------------------------------------------------------
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        console_ = new MockConsole();
        handler_ = new DefaultCommandHandler();
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests handleCommand(classpath)
     */
    public void testHandleCommandClasspath() throws Exception
    {
        logger_.info("Running testHandleCommandClasspath...");
        handler_.handleCommand(console_, DefaultCommandHandler.CMD_CLASSPATH);
    }
    
    
    /**
     * Tests handleCommand(help)
     */
    public void testHandleCommandHelp() throws Exception 
    {
        logger_.info("Running testHandleCommandHelp...");
        handler_.handleCommand(console_, DefaultCommandHandler.CMD_HELP);
    }
    
    
    /**
     * Tests handleCommand(mem)
     */
    public void testHandleCommandMem() throws Exception 
    {
        logger_.info("Running testHandleCommandMem...");
        handler_.handleCommand(console_, DefaultCommandHandler.CMD_MEM);
    }
    
    
    /**
     * Tests handleCommand(props)
     */
    public void testHandleCommandProps() throws Exception 
    {
        logger_.info("Running testHandleCommandProps...");
        handler_.handleCommand(console_, DefaultCommandHandler.CMD_PROPS);        
    }
    
    
    /**
     * Tests handleCommand(uptime)
     */
    public void testHandleCommandUptime() throws Exception 
    {
        logger_.info("Running testHandleCommandUptime...");
        handler_.handleCommand(console_, DefaultCommandHandler.CMD_UPTIME);        
    }
    
    
    /**
     * Tests handleCommand(setprop)
     */
    public void testHandleCommandSetProp() throws Exception 
    {
        logger_.info("Running testHandleCommandSetProp...");
        
        String prop = "console.test";
        String value = "123";        
        
        try
        {
            handler_.handleCommand(console_, 
                DefaultCommandHandler.CMD_SETPROP + " " + prop + " " + value);
            
            assertEquals(
                "property should be set", 
                value, 
                System.getProperty(prop));
        }
        finally
        {
            // Cleanup...
            System.getProperties().remove(prop);
        }
    }
    
    
    /**
     * Tests handleCommand(delprop)
     */
    public void testHandleCommandDelProp() throws Exception 
    {
        logger_.info("Running testHandleCommandDelProp...");
        
        String prop = "console.delprop";
        String value = "123";
        
        try
        {
            System.setProperty(prop, value);
            
            handler_.handleCommand(console_, 
                DefaultCommandHandler.CMD_DELPROP + " " + prop);
            
            assertNull("property should be null", System.getProperty(prop));
        }
        finally
        {
            // Cleanup...
            System.getProperties().remove(prop);
        }
    }
}