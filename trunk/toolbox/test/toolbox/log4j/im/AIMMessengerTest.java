package toolbox.log4j.im;

import junit.textui.TestRunner;

import org.apache.log4j.Logger;
import org.apache.log4j.helpers.LogLog;

import toolbox.junit.StandaloneTestCase;

/**
 * Unit tests for AIMMessenger.
 */
public class AIMMessengerTest extends AbstractMessengerTest implements 
	StandaloneTestCase
{
    // TODO: Still broken...

    private static final Logger logger_ =
        Logger.getLogger(AIMMessengerTest.class);

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
        LogLog.setInternalDebugging(true);
        
        TestRunner.run(AIMMessengerTest.class);
    }
    
    //--------------------------------------------------------------------------
    // Abstract Implementation
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.log4j.im.AbstractMessengerTest#getFromUser()
     */
    public String getFromUser()
    {
        return "supahfuzzy";
    }
    
    
    /**
     * @see toolbox.log4j.im.AbstractMessengerTest#getFromPassword()
     */
    public String getFromPassword()
    {
        return "techno";
    }
    
    
    /**
     * @see toolbox.log4j.im.AbstractMessengerTest#getToUser()
     */
    public String getToUser()
    {
        return "analogue";
    }
    
    
    /**
     * @see toolbox.log4j.im.AbstractMessengerTest#getLog4JXML()
     */
    public String getLog4JXML()
    {
    	return "/toolbox/log4j/im/AOLMessengerTest.xml";        
    }
    
    
    /**
     * @see toolbox.log4j.im.AbstractMessengerTest#getMessenger()
     */
    public InstantMessenger getMessenger()
    {
        return new AIMMessenger();
    }
    
    /* (non-Javadoc)
     * @see toolbox.log4j.im.AbstractMessengerTest#testLifeCycle()
     */
    public void testLifeCycle() throws Exception
    {
        super.testLifeCycle();
    }
}