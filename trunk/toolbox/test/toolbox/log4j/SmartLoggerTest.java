package toolbox.log4j;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Unit test for {@link toolbox.log4j.SmartLogger}.
 */
public class SmartLoggerTest extends TestCase
{
    private static Logger logger_ = Logger.getLogger(SmartLoggerTest.class);

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------

    /**
     * Entrypoint.
     * 
     * @param args None recognized.
     */
    public static void main(String[] args)
    {
        TestRunner.run(SmartLoggerTest.class);
    }

    //--------------------------------------------------------------------------
    //  Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests right() for proper truncation behavior.
     */
    public void testLog()
    {
        logger_.info("Running testLog...");
        
        String br = StringUtils.repeat("=", 60); 
        
        String s = "one\ntwo\nthree\nfour\nfive";
        logger_.debug("There should be 5 lines one...five");
        logger_.debug(br);
        SmartLogger.info(logger_, s);        
        
        logger_.debug(br);
        String b = "";
        logger_.debug("There should be 1 empty line");
        logger_.debug(br);
        SmartLogger.info(logger_, b);        

        logger_.debug(br);
        String c = "\n";
        logger_.debug("There should be 1 empty line");
        logger_.debug(br);
        SmartLogger.info(logger_, c);        

        logger_.debug(br);
        logger_.debug("There should be 2 empty lines");
        logger_.debug(br);
        SmartLogger.info(logger_, "\n\n");        
    }
}
