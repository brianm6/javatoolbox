package toolbox.log4j.test;

import org.apache.log4j.Logger;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import toolbox.log4j.SmartLogger;
import toolbox.util.StringUtil;

/**
 * Unit Test for SmartLogger
 */
public class SmartLoggerTest extends TestCase
{
    private static Logger logger_ = 
        Logger.getLogger(SmartLoggerTest.class);

    /**
     * Entrypoint
     * 
     * @param  args  Args
     */
    public static void main(String[] args)
    {
        TestRunner.run(SmartLoggerTest.class);
    }

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * SmartLoggerTest constructor
     * 
     * @param  name  Test name
     */
    public SmartLoggerTest(String name)
    {
        super(name);
    }

    //--------------------------------------------------------------------------
    //  Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests right() for proper truncation behavior
     */
    public void testLog()
    {
        logger_.info("Running testLog...");
        
        String br = StringUtil.repeat("=", 60); 
        
        String s = "one\ntwo\nthree\nfour\nfive";
        logger_.info("There should be 5 lines one...five");
        logger_.info(br);
        SmartLogger.info(logger_, s);        
        
        logger_.info(br);
        String b = "";
        logger_.info("There should be 1 empty line");
        logger_.info(br);
        SmartLogger.info(logger_, b);        

        logger_.info(br);
        String c = "\n";
        logger_.info("There should be 1 empty line");
        logger_.info(br);
        SmartLogger.info(logger_, c);        

        logger_.info(br);
        logger_.info("There should be 2 empty lines");
        logger_.info(br);
        SmartLogger.info(logger_, "\n\n");        
    }
}
