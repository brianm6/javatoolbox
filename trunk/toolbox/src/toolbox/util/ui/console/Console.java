package toolbox.util.ui.console;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import toolbox.util.service.Destroyable;
import toolbox.util.service.Nameable;

/**
 * Basic interface to an application console that supports intput/output via
 * streams.
 */
public interface Console extends Nameable, Destroyable
{
    /**
     * Stream used to read input from the console.
     * 
     * @return InputStream
     */
    InputStream getInputStream();


    /**
     * Stream used to write output to the console.
     * 
     * @return PrintStream
     */
    OutputStream getOutputStream();


    /**
     * Sends text to the console.
     * 
     * @param text Text to send to the console.
     */
    void send(String text) throws IOException;


    /**
     * Clears the contents of the console.
     */
    void clear();
    
    
    String getPrompt();
}