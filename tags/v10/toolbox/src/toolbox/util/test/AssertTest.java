package toolbox.util.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Category;
import toolbox.util.Assert;
import toolbox.util.AssertionException;

/**
 * Unit test for Assert 
 */
public class AssertTest extends TestCase
{
    /** Logger **/
    private static final Category logger = 
        Category.getInstance(AssertTest.class);
    
    /**
     * Constructor for AssertTest.
     * @param arg0
     */
    public AssertTest(String arg0)
    {
        super(arg0);
    }

    /**
     * Runs testcase in text mode
     */
    public static void main(String[] args)
    {
        BasicConfigurator.configure();
        TestRunner.run(AssertTest.class);
    }
    
    /**
     * Tests the isTrue() method 
     */
    public void testIsTrue() throws Exception
    {
        Assert.isTrue(true);
        
        try
        {
            Assert.isTrue(false);
            fail();
        }
        catch(AssertionException ae)
        {
            logger.info("isTrue passed");
        }
    }
    
    /**
     * Tests the isTrue(String,...) method
     */
    public void testIsTrueString() throws Exception
    {
        String failureString = "passed in false";
        
        Assert.isTrue(true, "passed in true");
        
        try
        {
            Assert.isTrue(false, failureString);
            fail();
        }
        catch(AssertionException ae)
        {
            logger.info("Assert string: " + ae.getMessage());
            
            assertEquals("string passed to assert don't match", 
                failureString, ae.getMessage());
        }
    }
}
