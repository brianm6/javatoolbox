package toolbox.util.test;

import org.apache.log4j.Logger;

import junit.framework.TestCase;
import junit.textui.TestRunner;

/**
 * Unit test for ResourceCloser
 */
public class ResourceCloserTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(ResourceCloserTest.class);
    
    /**
     * Entrypoint
     * 
     * @param  args  None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(ResourceCloserTest.class);    
    }
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Constructor for ResourceCloserTest
     * 
     * @param  arg0  Name
     */
    public ResourceCloserTest(String arg0)
    {
        super(arg0);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
  
}