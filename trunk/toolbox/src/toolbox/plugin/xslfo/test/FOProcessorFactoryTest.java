package toolbox.util.xslfo.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.xslfo.FOProcessor;
import toolbox.util.xslfo.FOProcessorFactory;

/**
 * Unit test for FOPProcessorFactory.
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
     * @param args None recognized
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
     * @throws Exception on error
     */
    public void testCreateProcessorFOP() throws Exception
    {
        logger_.info("Running testCreateProcessorFOP...");
        
        FOProcessor fop =
            FOProcessorFactory.createProcessor(
                FOProcessorFactory.FO_IMPL_APACHE);

        assertNotNull(fop);
    }

    
    /**
     * Simple case: create processor for RenderX XEP.
     * 
     * @throws Exception on error
     */
    public void testCreateProcessorXEP() throws Exception
    {
        logger_.info("Running testCreateProcessorXEP...");
                
        FOProcessor fop =
            FOProcessorFactory.createProcessor(
                FOProcessorFactory.FO_IMPL_RENDERX);

        assertNotNull(fop);
    }

    
    /**
     * Test fault generation on unknown FO implementation.
     * 
     * @throws Exception on error
     */
    public void testCreateProcessorBogus() throws Exception
    {
        logger_.info("Running testCreateProcessorBogus...");
        
        try
        {
            FOProcessorFactory.createProcessor("FO_IMPL_BOGUS");
            fail("Exception should have thrown for unknown FO impl");
        }
        catch (IllegalArgumentException iae)
        {
            ; // Passed.
        }
    }
}