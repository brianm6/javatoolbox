package toolbox.util.ui.console;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.junit.testcase.UITestCase;


/**
 * Unit test for SwingConsole.
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
    
    public void testSwingShellConsole1()
    {
        logger_.info("Running testSwingShellConsole1...");

        SwingConsole cons = new SwingConsole("console", 25, 80);
        
        try
        {
            cons.start();
            launchInDialog(cons.getView());
        }
        finally
        {
            cons.stop();
        }
    }
    
    
    public static void shell1()
    {
        SwingConsole cons = new SwingConsole("console", 25, 80);
        cons.setPrompt("howdy>");
        JFrame f= new JFrame("console");
        f.getContentPane().setLayout(new BorderLayout());
        f.getContentPane().add(cons.getView(), BorderLayout.CENTER);
        f.pack();
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cons.start();
    }
}
