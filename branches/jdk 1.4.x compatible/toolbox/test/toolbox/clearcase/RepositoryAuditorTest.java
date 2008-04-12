package toolbox.clearcase;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

/**
 * Unit test for {@link toolbox.clearcase.RepositoryAuditor}.
 */
public class RepositoryAuditorTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(RepositoryAuditor.class);
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args)
    {
        TestRunner.run(RepositoryAuditorTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests main entrypoint.
     */
    public void testMain() throws Exception
    {
        logger_.info("Running testMain...");
        
        RepositoryAuditor.main(new String[] {
            "/toolbox/clearcase/RepositoryAuditorTest.properties"});
    }
}
