package toolbox.log4j;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import toolbox.util.StringUtil;

/**
 * Log4J Utility Class
 */
public class SmartLogger
{
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Prevent contruction
     */
    private SmartLogger()
    {
    }

    //--------------------------------------------------------------------------
    // Public Static
    //--------------------------------------------------------------------------
    
    /**
     * Logs each individual line of a string (delimited by a newline character)
     * separately.
     * 
     * @param  logger  Logger to use
     * @param  msg     Multiline object to log
     */
    public static void debug(Logger logger, Object msg)
    {
        log(logger, Priority.DEBUG, msg);
    }
    
    /**
     * Logs each individual line of a string (delimited by a newline character)
     * separately.
     * 
     * @param  logger  Logger to use
     * @param  msg     Multiline object to log
     */
    public static void info(Logger logger, Object msg)
    {
        log(logger, Priority.INFO, msg);
    }
    
    /**
     * Logs each individual line of a string (delimited by a newline character)
     * separately.
     * 
     * @param  logger  Logger to use
     * @param  msg     Multiline object to log
     * 
     */
    public static void log(Logger logger, Priority priority, Object msg)
    {
        String[] lines = StringUtil.tokenize(msg.toString(), "\n");
        
        for (int i=0; i<lines.length; i++)
            logger.log(priority, lines[i]);
    }
}
