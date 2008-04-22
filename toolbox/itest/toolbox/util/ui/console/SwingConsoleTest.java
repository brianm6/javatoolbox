package toolbox.util.ui.console;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.testcase.UITestCase;

/**
 * Unit test for {@link toolbox.util.ui.console.SwingConsole}.
 */
public class SwingConsoleTest extends UITestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(SwingConsoleTest.class);
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args)
    {
        TestRunner.run(SwingConsoleTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Simple interactive test of the SwingConsole.
     */
    public void testSwingShellConsole1() throws Exception
    {
        logger_.info("Running testSwingShellConsole1...");

        SwingConsole cons = new SwingConsole("console", 25, 80);
        
        try
        {
            cons.start();
            cons.setPrompt("Hit me, baby! >");
            cons.getCommandHandler().handleCommand(cons, "help");
            launchInDialog(cons.getView());
        }
        finally
        {
            cons.stop();
            assertTrue(!cons.isRunning());
        }
    }
    
    // Disregard...
    
    public static void shell1()
    {
        SwingConsole cons = new SwingConsole("console", 25, 80);
        cons.setPrompt("Howdy! ");
        JFrame f= new JFrame("console");
        f.getContentPane().setLayout(new BorderLayout());
        f.getContentPane().add(cons.getView(), BorderLayout.CENTER);
        f.pack();
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cons.start();
    }
}