package toolbox.log4j.im;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

/**
 * Unit test for {@link toolbox.log4j.im.InstantMessengerFactory}.
 */
public class InstantMessengerFactoryTest extends TestCase
{
    private static final Logger logger_ =
        Logger.getLogger(InstantMessengerFactoryTest.class);

    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------

    /**
     * Entrypoint
     *
     * @param args None recognized.
     */
    public static void main(String[] args)
    {
        TestRunner.run(InstantMessengerFactoryTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------

    /**
     * Tests creation of the Yahoo messenger.
     */
    public void testCreateYahoo()
    {
        logger_.info("Running testCreateYahoo...");

        InstantMessenger im = InstantMessengerFactory.create("yahoo");
        assertNotNull(im);
        assertFalse(im.isConnected());
        assertTrue(im instanceof YahooMessenger);
    }


    /**
     * Tests creation of the Msn messenger.
     */
    public void testCreateMSN()
    {
        logger_.info("Running testCreateMSN...");

        InstantMessenger im = InstantMessengerFactory.create("msn");
        assertNotNull(im);
        assertFalse(im.isConnected());
        assertTrue(im instanceof MSNMessenger);
    }


    /**
     * Tests creation of the AOL messenger.
     */
    public void testCreateAOL()
    {
        logger_.info("Running testCreateAOL...");

        InstantMessenger im = InstantMessengerFactory.create("aol");
        assertNotNull(im);
        assertFalse(im.isConnected());
        assertTrue(im instanceof AIMMessenger);
    }


    /**
     * Tests creation of an invalid messenger.
     */
    public void testCreateFailure()
    {
        logger_.info("Running testCreateFailure...");

        try
        {
            InstantMessengerFactory.create("xyz");
            fail("Should have failed for bogus messenger xyz");
        }
        catch (IllegalArgumentException iae)
        {
            assertTrue(true);
        }
    }
}