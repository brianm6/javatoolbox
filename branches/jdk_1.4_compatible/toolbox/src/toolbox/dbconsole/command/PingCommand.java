package toolbox.dbconsole.command;

import toolbox.dbconsole.Command;
import toolbox.dbconsole.TestConsole;

/**
 * Ping command. Just prints pong to the console to make sure a command is
 * registered and working correctly.
 */
public class PingCommand implements Command {

    // -------------------------------------------------------------------------
    // Command Interface
    // -------------------------------------------------------------------------
    
    /*
     * @see toolbox.dbconsole.Command#execute(toolbox.dbconsole.TestConsole, java.lang.Object[])
     */
    public void execute(TestConsole console, Object[] args) throws Exception {
        console.getPrintStream().println("Pong!");
    }

    /*
     * @see toolbox.dbconsole.Command#getName()
     */
    public String getName() {
        return "ping";
    }

    /*
     * @see toolbox.dbconsole.Command#getDescription()
     */
    public String getDescription() {
        return "Ping pong";
    }
}