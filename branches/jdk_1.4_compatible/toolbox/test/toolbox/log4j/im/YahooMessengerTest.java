package toolbox.log4j.im;

import junit.textui.TestRunner;

import toolbox.junit.testcase.StandaloneTestCase;

/**
 * Unit test for {@link toolbox.log4j.im.YahooMessenger}.
 */
public class YahooMessengerTest extends AbstractMessengerTest implements 
	StandaloneTestCase
{
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
        TestRunner.run(YahooMessengerTest.class);
    }
    
    //--------------------------------------------------------------------------
    // Abstract Implementation
    //--------------------------------------------------------------------------

    /**
     * @see toolbox.log4j.im.AbstractMessengerTest#getFromUser()
     */
    public String getFromUser()
    {
        return "supahfuzz";
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
    	return "/toolbox/log4j/im/YahooMessengerTest.xml";        
    }
    
    
    /**
     * @see toolbox.log4j.im.AbstractMessengerTest#getMessenger()
     */
    public InstantMessenger getMessenger()
    {
        return new YahooMessenger();
    }
}