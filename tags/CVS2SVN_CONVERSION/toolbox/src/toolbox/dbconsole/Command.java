package toolbox.dbconsole;

/**
 * Command Pattern used to implement command line operations executed by the
 * test console.
 */
public interface Command {

    /**
     * Executes this command.
     *
     * @param console Console that invoked the command.
     * @param args Arguments.
     * @throws Exception on error.
     */
    void execute(TestConsole console, Object args[]) throws Exception;


    /**
     * Returns the name of this command.
     *
     * @return String
     */
    String getName();


    /**
     * Returns a description of this command suitable for display in the
     * test console help.
     *
     * @return String
     */
    String getDescription();
}