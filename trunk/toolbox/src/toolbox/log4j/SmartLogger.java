package toolbox.log4j;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import toolbox.util.StringUtil;

/**
 * Log4J Utility Class.
 */
public final class SmartLogger
{
    private static final Logger logger_ = Logger.getLogger(SmartLogger.class);

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------

    /**
     * Prevent contruction.
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
     * @param logger Logger to use
     * @param msg Multiline object to log
     */
    public static void debug(Logger logger, Object msg)
    {
        log(logger, Priority.DEBUG, msg);
    }

    
    /**
     * Logs each individual line of a string (delimited by a newline character)
     * separately.
     * 
     * @param logger Logger to use
     * @param msg Multiline object to log
     */
    public static void info(Logger logger, Object msg)
    {
        log(logger, Priority.INFO, msg);
    }

    
    /**
     * Logs each individual line of a string (delimited by a newline character)
     * separately.
     * 
     * @param logger Logger to use.
     * @param priority Message priority.
     * @param msg Multiline object to log
     */
    public static void log(Logger logger, Priority priority, Object msg)
    {
        // TODO: fix this pile of mess!
        
        String[] lines = StringUtil.tokenize(msg.toString(), "\n", true);
        logger_.debug("NumLines: " + lines.length);

        if (msg.toString().trim().length() == 0)
        {
            logger.log(priority, "");
            return;
        }

        for (int i = 0; i < lines.length;)
        {
            if (i > 0 && lines[i].equals("\n") && lines[i - 1].equals("\n"))
            {
                logger.log(priority, "");
                i += 1;
            }
            else
            {
                logger.log(priority, lines[i]);
                i += 2;
            }
        }
    }
}