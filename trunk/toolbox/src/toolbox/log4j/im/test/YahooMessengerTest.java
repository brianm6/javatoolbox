package toolbox.log4j.im.test;

import java.util.Properties;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import toolbox.junit.StandaloneTestCase;
import toolbox.log4j.im.InstantMessenger;
import toolbox.log4j.im.YahooMessenger;
import toolbox.util.ResourceUtil;
import toolbox.util.ThreadUtil;
import toolbox.util.XMLUtil;

/**
 * Unit test for YahooMessenger.
 */
public class YahooMessengerTest extends TestCase implements StandaloneTestCase
{
    private static final Logger logger_ =
        Logger.getLogger(YahooMessengerTest.class);

    /**
     * Yahoo user that will receive all test messages.
     */
    private static final String TO_USER = "analogue";
    
    /**
     * Yahoo user that the messages will originate from.
     */
    private static final String FROM_USER = "supahfuzz";
    
    /**
     * Password of the FROM_USER.
     */
    private static final String FROM_PASSWORD = "techno"; 
        
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
        TestRunner.run(YahooMessengerTest.class);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------

    /**
     * Tests full lifecycle of typical usage.
     * 
     * @throws Exception on error
     */    
    public void testLifeCycle() throws Exception
    {
        logger_.info("Running testLifeCycle...");
        
        InstantMessenger messenger = new YahooMessenger();
        messenger.initialize(new Properties());
        
        logger_.debug("Before login...");
        messenger.login(FROM_USER, FROM_PASSWORD);
        
        logger_.debug("Before send...");
        messenger.send(TO_USER, "Hello from the testLifeCycle unit test.");
        
        ThreadUtil.sleep(5000);
        
        logger_.debug("Before logout...");
        messenger.logout();
        
        logger_.debug("Before shutdown...");
        messenger.shutdown();
        
        logger_.debug("All done!");
    }
    
    
    /**
     * Tests sending a whole slew of messages.
     * 
     * @throws Exception on error
     */    
    public void testSendMany() throws Exception
    {
        logger_.info("Running testSendMany...");
        
        int max = 20;
        
        InstantMessenger messenger = new YahooMessenger();
        messenger.initialize(new Properties());
        messenger.login(FROM_USER, FROM_PASSWORD);
        
        for (int i=0; i<max; i++)
        {
            messenger.send(
                TO_USER, "This is message number " + (i+1) + " of " + max);
        }
        
        messenger.logout();
        messenger.shutdown();
    }
    
    
    /**
     * Tests that the configuration in the xml file is read and executed
     * correctly.
     *  
     * @throws Exception on error
     */
    public void testConfigByXML() throws Exception
    {
        logger_.info("Running testConfigByXML...");

        String appenderName = "testXMLInit_appender";
        String loggerName   = "testXMLInit_logger";

        String xmlConfig = 
            ResourceUtil.getResourceAsString(
                "/toolbox/log4j/im/test/YahooMessengerTest.xml");

        // Load config from xml file        
        DOMConfigurator.configure(XMLUtil.loadElement(xmlConfig));
        Logger logger = Logger.getLogger(loggerName);
        
        logger.debug("debug");
        logger.info("info");
        logger.warn("warn");
        logger.error("error");
        
        ThreadUtil.sleep(2000);
        
        // Cleanup
        Appender appender = logger.getAppender(appenderName);
        logger_.info("Closing appender " + appender.getName());
        appender.close();
        logger.removeAppender(appenderName);
    }
}