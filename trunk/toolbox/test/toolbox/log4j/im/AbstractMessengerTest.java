package toolbox.log4j.im;

import java.util.Properties;

import junit.framework.TestCase;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import toolbox.junit.testcase.StandaloneTestCase;
import toolbox.util.ResourceUtil;
import toolbox.util.ThreadUtil;
import toolbox.util.XMLUtil;

/**
 * Base class for Messenger tests.
 */
public abstract class AbstractMessengerTest extends TestCase implements 
	StandaloneTestCase
{
    private static final Logger logger_ =
        Logger.getLogger(AbstractMessengerTest.class);

    //--------------------------------------------------------------------------
    // Overrides TestCase
    //--------------------------------------------------------------------------
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        ThreadUtil.sleep(5000);
    }
    
    //--------------------------------------------------------------------------
    // Abstract
    //--------------------------------------------------------------------------
    
    public abstract String getFromUser();
    public abstract String getFromPassword();
    public abstract String getToUser();
    public abstract InstantMessenger getMessenger();
    public abstract String getLog4JXML();
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------

    /**
     * Tests full lifecycle of typical usage.
     * 
     * @throws Exception on error.
     */    
    public void testLifeCycle() throws Exception
    {
        logger_.info("Running testLifeCycle...");
        
        InstantMessenger messenger = getMessenger();
        messenger.initialize(new Properties());
        
        logger_.debug("Before login...");
        messenger.login(getFromUser(), getFromPassword());
        
        logger_.debug("Before send...");
        messenger.send(getToUser(), "Hello from the testLifeCycle unit test.");
        
        ThreadUtil.sleep(5000);
        
        logger_.debug("Before logout...");
        messenger.logout();
        
        logger_.debug("Before shutdown...");
        messenger.destroy();
        
        logger_.debug("All done!");
    }
    
    
    /**
     * Tests sending a whole slew of messages.
     * 
     * @throws Exception on error.
     */    
    public void testSendMany() throws Exception
    {
        logger_.info("Running testSendMany...");
        
        int max = 10;
        
        InstantMessenger messenger = getMessenger();
        messenger.initialize(new Properties());
        messenger.login(getFromUser(), getFromPassword());
        
        for (int i = 0; i < max; i++)
        {
            messenger.send(
                getToUser(),
                "This is message number " + (i + 1) + " of " + max);
        }
        
        messenger.logout();
        messenger.destroy();
    }
    
    
    /**
     * Tests that the configuration in the xml file is read and executed
     * correctly.
     *  
     * @throws Exception on error.
     */
    public void testConfigByXML() throws Exception
    {
        logger_.info("Running testConfigByXML...");

        String appenderName = "testXMLInit_appender";
        String loggerName   = "testXMLInit_logger";

        String xmlConfig = ResourceUtil.getResourceAsString(getLog4JXML());
                

        // Load config from xml file        
        DOMConfigurator.configure(XMLUtil.toElement(xmlConfig));
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