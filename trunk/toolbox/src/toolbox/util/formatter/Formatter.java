package toolbox.util.formatter;

import java.io.InputStream;
import java.io.OutputStream;

import toolbox.util.service.Nameable;
import toolbox.workspace.IPreferenced;

/**
 * A <code>Formatter</code> for any type of text. The text to be formatted can
 * be specified as a <code>String</code> or a <code>InputStream</code>.
 */
public interface Formatter extends Nameable, IPreferenced
{
    /**
     * Returns a formatted version of the input string.
     *
     * @param input String to format.
     * @return String
     * @throws Exception on formatting error.
     */
    String format(String input) throws Exception;


    /**
     * Formats the contents of the <code>InputStream<code> and writes the
     * results to the <code>OutputStream</code>.
     *
     * @param input Source of data to format.
     * @param output Destination of formatted data.
     * @throws Exception on formatting error.
     */
    void format(InputStream input, OutputStream output) throws Exception;
}