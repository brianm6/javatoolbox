package toolbox.util.decompiler.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.decompiler.Decompiler;
import toolbox.util.decompiler.DecompilerFactory;

/**
 * Unit test for DecompilerFactory.
 */
public class DecompilerFactoryTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(DecompilerFactoryTest.class);
    
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
        TestRunner.run(DecompilerFactoryTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Test create() for all supported decompilers.
     * 
     * @throws Exception on error.
     */
    public void testCreate() throws Exception
    {
        logger_.info("Running testCreate...");
        
        assertNotNull(
            DecompilerFactory.create(
                DecompilerFactory.DECOMPILER_JAD));
        
        assertNotNull(
            DecompilerFactory.create(
                DecompilerFactory.DECOMPILER_JODE));
        
        assertNotNull(
            DecompilerFactory.create(
                DecompilerFactory.DECOMPILER_JREVERSEPRO));
    }


    /**
     * Test create() for an unsupported decompiler.
     * 
     * @throws Exception on error.
     */
    public void testCreateFailure() throws Exception
    {
        logger_.info("Running testCreateFailure...");
        
        try
        {
            DecompilerFactory.create("kaboom");
            fail("Should have failed on invalid compiler");
        }
        catch (IllegalArgumentException iae)
        {
            assertTrue(true);
        }
    }

    
    /**
     * Tests createPreferred().
     * 
     * @throws Exception on error.
     */
    public void testCreatePreferred() throws Exception
    {
        logger_.info("Running testCreatePreferred...");
        
        assertNotNull(DecompilerFactory.createPreferred());
    }
    
    
    /**
     * Tests createAll().
     * 
     * @throws Exception on error.
     */
    public void testCreateAll() throws Exception
    {
        logger_.info("Running testCreateAll...");
        
        Decompiler[] d = DecompilerFactory.createAll();
        for (int i = 0; i < d.length; assertNotNull(d[i++]));
    }
}