package toolbox.log4j.im.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import toolbox.log4j.im.InstantMessengerAppender;
import toolbox.util.ThreadUtil;

/**
 * Unit test for InstantMessengerAppender.
 */
public class InstantMessengerAppenderTest extends TestCase
{
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Entrypoint.
     * 
     * @param args None recognized
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
        Logger root = Logger.getRootLogger();
        
        InstantMessengerAppender imAppender = new InstantMessengerAppender();
        
        imAppender.setMessenger("null");
        imAppender.setThreshold(Priority.INFO);
        root.addAppender(imAppender);

        root.info("root info");
        root.debug("root debug");
        root.warn("root warning");
        root.error("root error");
        
        ThreadUtil.sleep(2000);
        LogManager.shutdown();
    }
}