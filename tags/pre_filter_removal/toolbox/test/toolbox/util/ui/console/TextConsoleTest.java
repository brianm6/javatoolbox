package toolbox.util.ui.console;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.testcase.StandaloneTestCase;

/**
 * Unit test for {@link toolbox.util.ui.console.TextConsole}.
 */
public class TextConsoleTest extends TestCase implements StandaloneTestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(TextConsoleTest.class);
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Entrypoint.
     * 
     * @param args None.
     */
    public static void main(String[] args)
    {
        TestRunner.run(TextConsoleTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Simple interactive test of the text console.
     */
    public void testTextConsole() throws Exception
    {
        logger_.info("Running testTextConsole...");

        TextConsole console = 
            new TextConsole(
                "TextConsole", 
                new InputStreamReader(System.in),
                new OutputStreamWriter(System.out));
        
        try
        {
            console.setPrompt("Hit me>");
            console.start();
            console.getCommandHandler().handleCommand(console, "help");

            while (console.isRunning())
                Thread.sleep(1000);
        }
        finally
        {
            console.stop();
            assertTrue(!console.isRunning());
        }
    }
}