package toolbox.dbconsole;

import java.io.InputStream;
import java.io.PrintStream;

/**
 * Basic interface to an application console that supports intput/output via
 * streams.
 */
public interface ConsoleIfc {

    /**
     * Returns the stream used for reading input from the console.
     * 
     * @return InputStream
     */
    InputStream getInputStream();


    /**
     * Returns the stream used to write output to the console.
     * 
     * @return PrintStream
     */
    PrintStream getOutputStream();


    /**
     * Sends text.
     * 
     * @param text
     */
    void send(String text);
    
    
    /**
     * Clears the contents of the console.
     */
    void clear();


    /**
     * Release any resources held by the console.
     */
    void dispose();
}