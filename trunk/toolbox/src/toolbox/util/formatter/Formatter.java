package toolbox.util.formatter;

import toolbox.util.service.Initializable;
import toolbox.util.service.Nameable;

/**
 * General purpose formatter interface.
 */
public interface Formatter extends Initializable, Nameable
{
    /**
     * Returns a formatted version of the input string.
     * 
     * @param input String contained element to be formatted.
     * @throws Exception on error.
     */
    String format(String input) throws Exception;
}