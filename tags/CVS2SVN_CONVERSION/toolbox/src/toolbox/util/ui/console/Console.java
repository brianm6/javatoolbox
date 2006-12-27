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
     * Clears the contents of the console.
     */
    void clear();


    /**
     * Returns the text of the command prompt.
     * 
     * @return String
     */
    String getPrompt();

    
    /**
     * Sets the text of the command prompt.
     * 
     * @param prompt Text of the command prompt.
     */
    void setPrompt(String prompt);
    
    
    /**
     * Renders the command prompt to the console.
     */
    void renderPrompt();
}