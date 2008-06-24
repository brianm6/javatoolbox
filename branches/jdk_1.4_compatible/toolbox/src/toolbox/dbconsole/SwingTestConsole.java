package toolbox.dbconsole;

import javax.swing.JFrame;

import toolbox.util.SwingUtil;

/**
 * Same as TestConsole but wrapped in a very minimal Swing frame.
 *
 * @see toolbox.dbconsole.TestConsole
 */
public class SwingTestConsole extends toolbox.dbconsole.SwingConsole {
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------

    /**
     * Entrypoint.
     *
     * @param args None
     */
    public static void main(String args[]) {
        new SwingTestConsole();
    }

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Creates a SwingConsoleTest.
     */
    public SwingTestConsole() {
        super("Swing Test Console", 40, 90);
        pack();
        SwingUtil.centerWindow(this);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Tie GUI in/out streams to the text mode impl.
        TestConsole console =
            new TestConsole(
                getInputStream(),
                getOutputStream());

        setTextConsole(console);
        console.startConsole();
    }
}