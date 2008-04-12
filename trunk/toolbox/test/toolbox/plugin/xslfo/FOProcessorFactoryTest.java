package toolbox.plugin.xslfo;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

/**
 * Unit test for {@link toolbox.plugin.xslfo.FOProcessorFactory}.
 */
public class FOProcessorFactoryTest extends TestCase
{
    private static final Logger logger_ =
        Logger.getLogger(FOProcessorFactoryTest.class);

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
        TestRunner.run(FOProcessorFactoryTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------

    /**
     * Simple case: create processor for Apache FOP.
     *
     * @throws Exception on error.
     */
    public void testCreateFOP() throws Exception
    {
        logger_.info("Running testCreateFOP...");

        FOProcessor fop =
            FOProcessorFactory.create(
                FOProcessorFactory.FO_IMPL_APACHE);

        assertNotNull(fop);
    }


    /**
     * Test fault generation on unknown FO implementation.
     *
     * @throws Exception on error.
     */
    public void testCreateBogus() throws Exception
    {
        logger_.info("Running testCreateBogus...");

        try
        {
            FOProcessorFactory.create("FO_IMPL_BOGUS");
            fail("Exception should have thrown for unknown FO impl");
        }
        catch (IllegalArgumentException iae)
        {
            ; // Passed.
        }
    }
}