package toolbox.util.ui.console;

import java.io.InterruptedIOException;

/**
 * Console interface suitable for the entry and execution of commands.
 */
public interface Console
{
    /**
     * Returns the name of this console.
     * 
     * @return Console name.
     */
    String getName();


    /**
     * Reads a line of input from the console.
     * 
     * @return Line read from the console.
     * @throws InterruptedIOException when the reading thread is interrupted
     *         before a line has been read.
     */
    String read() throws InterruptedIOException;


    /**
     * Writes to the console.
     * 
     * @param msg Message to write.
     */
    void write(String msg);


    /**
     * Returns the handler responsible for executing commands entered into this
     * console.
     * 
     * @return CommandHandler
     */
    CommandHandler getCommandHandler();


    /**
     * Clear the contents of the console.
     */
    void clear();


    /**
     * Sets the prompt to be used when reading input.
     * 
     * @param prompt Command prompt.
     */
    void setPrompt(String prompt);
}