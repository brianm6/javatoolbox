package toolbox.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Exception utilities
 */
public class ExceptionUtil
{
    /**
     * Prevent construction
     */
    private ExceptionUtil()
    {
    }

    /**
     * Converts a <code>Throwable</code>'s stack trace to a string
     *
     * @param  t   Throwable to extrace stack trace from
     * @return Stack trace of the throwable as a string
     */
    public static String getStackTrace(Throwable t) 
    {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
