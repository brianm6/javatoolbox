package toolbox.util.formatter;

import java.io.InputStream;
import java.io.OutputStream;

import toolbox.util.service.Nameable;

/**
 * General purpose formatter interface.
 */
public interface Formatter extends Nameable
{
    /**
     * Returns a formatted version of the input string.
     * 
     * @param input String contained element to be formatted.
     * @throws Exception on formatting error.
     */
    String format(String input) throws Exception;
    
    
    /**
     * Formats the contents of the given input stream and writes the results to
     * the given output stream.
     * 
     * @param input Source of data to format.
     * @param output Destination of formatted data.
     * @throws Exception on formatting error.
     */
    void format(InputStream input, OutputStream output) throws Exception;
}