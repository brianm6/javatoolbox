package toolbox.dbconsole.command;

import toolbox.dbconsole.Command;
import toolbox.dbconsole.TestConsole;

/**
 * Prints out the current version of the test console. This file needs
 * to be updated whenever a new version of this test console is released.
 */
public class VersionCommand implements Command {

    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------

    /**
     * Current version of the test console.
     */
    public static final String VERSION = "0.0";
    
    /**
     * Last updated date.
     */
    public static final String LAST_UPDATED = "01-01-1980";

    //--------------------------------------------------------------------------
    // Command Interface
    //--------------------------------------------------------------------------

    /*
     * @see toolbox.dbconsole.Command#execute(toolbox.dbconsole.TestConsole, java.lang.Object[])
     */
    public void execute(TestConsole console, Object[] args) throws Exception {

        console.getPrintStream().println(
            "DbConsole" 
            + VERSION 
            + "\tUpdated " 
            + LAST_UPDATED);
    }


    /*
     * @see toolbox.dbconsole.Command#getName()
     */
    public String getName() {
        return "version";
    }


    /*
     * @see toolbox.dbconsole.Command#getDescription()
     */
    public String getDescription() {
        return "Shows the current version and change history.";
    }
}