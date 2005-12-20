package toolbox.log4j.im;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import toolbox.junit.testcase.StandaloneTestCase;
import toolbox.util.ThreadUtil;

/**
 * Unit test for {@link toolbox.log4j.im.InstantMessengerAppender}.
 */
public class InstantMessengerAppenderTest extends TestCase
    implements StandaloneTestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(InstantMessengerAppenderTest.class);
    
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
        TestRunner.run(InstantMessengerAppenderTest.class);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------

    /**
     * Tests append()
     * 
     * @throws Exception on error.
     */
    public void testAppend() throws Exception
    {
        logger_.info("Running testAppend...");
        
        Logger root = Logger.getRootLogger();
        
        InstantMessengerAppender imAppender = new InstantMessengerAppender();
        
        imAppender.setMessenger("null");
        imAppender.setThreshold(Level.INFO);
        root.addAppender(imAppender);
        root.info("root info");
        root.debug("root debug");
        root.warn("root warning");
        root.error("root error");
        
        ThreadUtil.sleep(2000);
        LogManager.shutdown();
    }
}