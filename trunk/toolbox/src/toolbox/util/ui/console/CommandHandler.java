package toolbox.util.ui.console;

/**
 * CommandHandler is responsible executing commands.
 */
public interface CommandHandler
{
    /**
     * Handles the execution of the given command.
     * 
     * @param console Console that the command was entered into.
     * @param command Command to execute (the full text of the line).
     * @throws Exception if an error occurs.
     */
    void handleCommand(Console console, String command) 
        throws Exception;
}
