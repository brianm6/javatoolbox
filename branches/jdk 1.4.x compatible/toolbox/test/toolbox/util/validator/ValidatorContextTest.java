package toolbox.util.validator;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

/**
 * Unit test for {@link toolbox.util.validator.ValidatorContext}.
 */
public class ValidatorContextTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(ValidatorContextTest.class);
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args)
    {
        TestRunner.run(ValidatorContextTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    public void testReset()
    {
        logger_.info("Running testReset...");
        
        ValidatorContextIfc ctx = new ValidatorContext();
        ctx.reset();
        
        assertEquals(0, ctx.getFailures().size());
        assertEquals(0, ctx.getWarnings().size());
    }
    
    
    public void testAddFailure()
    {
        logger_.info("Running testAddFailure...");
        
        ValidatorContextIfc ctx = new ValidatorContext();
        assertTrue(ctx.isValid());
        ctx.addFailure("Failure1");
        
        assertFalse(ctx.isValid());
        assertEquals(1, ctx.getFailures().size());
        assertEquals(0, ctx.getWarnings().size());
    }
    
    
    public void testAddWarning()
    {
        logger_.info("Running testAddWarning...");
        
        ValidatorContextIfc ctx = new ValidatorContext();
        assertTrue(ctx.isValid());
        ctx.addWarning("Warning1");
        
        assertTrue(ctx.isValid());
        assertEquals(0, ctx.getFailures().size());
        assertEquals(1, ctx.getWarnings().size());
    }
}
