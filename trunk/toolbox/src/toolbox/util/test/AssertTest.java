package toolbox.util.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.Assert;
import toolbox.util.AssertionException;

/**
 * Unit test for Assert 
 */
public class AssertTest extends TestCase
{
    /** Logger **/
    private static final Logger logger_ = 
        Logger.getLogger(AssertTest.class);

    /**
     * Runs testcase in text mode
     * 
     * @param args Args
     */
    public static void main(String[] args)
    {
        TestRunner.run(AssertTest.class);
    }

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
            
    /**
     * Constructor for AssertTest.
     * 
     * @param arg0  Name
     */
    public AssertTest(String arg0)
    {
        super(arg0);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
        
    /**
     * Tests equals(double)
     */
    public void testEqualsDouble() 
    {
        logger_.info("Running testEqualsDouble...");
        
        Assert.equals(1.0d, 1.0d, 0.0, "equal");
        
        try
        {
            Assert.equals(1.0d, 2.0d, 0.0, "not equal");
            fail("testEqualsDouble");
        }
        catch (AssertionException e)
        {
            logger_.info("Passed: " + e.getMessage());
        }
    }    
    
    /**
     * Tests equals(float)
     */
    public void testEqualsFloat()
    {
        logger_.info("Running testEqualsFloat...");
        
        Assert.equals(1.0f, 1.0f, 0.0, "equal");
        
        try
        {
            Assert.equals(1.0f, 2.0f, 0.0, "not equal");
            fail("testEqualsFloat");
        }
        catch (AssertionException e)
        {
            logger_.info("Passed: " + e.getMessage());
        }
    }    
    
    /**
     * Tests equals(long)
     */
    public void testEqualsLong() 
    {
        logger_.info("Running testEqualsLong...");
        
        Assert.equals(111L, 111L, "equal");
        
        try
        {
            Assert.equals(111L, 222L,"not equal");
            fail("testEqualsLong");
        }
        catch (AssertionException e)
        {
            logger_.info("Passed: " + e.getMessage());
        }
    }    
   
    /**
     * Tests equals(Object)
     */
    public void testEqualsObject() 
    {
        logger_.info("Running testEqualsObject...");
        
        Assert.equals("one", "one", "equal");
        
        try
        {
            Assert.equals("one", "two", "not equal");
            fail("testEqualsObject");
        }
        catch (AssertionException e)
        {
            logger_.info("Passed: " + e.getMessage());
        }
    }    
    
    /**
     * Tests isFalse()
     */
    public void testIsFalse() 
    {
        logger_.info("Running testIsFalse...");
        
        Assert.isFalse(false, "isFalse");
        
        try
        {
            Assert.isFalse(true, "isFalse");
            fail("testIsFalse");
        }
        catch (AssertionException e)
        {
            logger_.info("Passed: " + e.getMessage());            
        }
    }
    
    /**
     * Tests isTrue()
     */
    public void testIsTrue() 
    {
        logger_.info("Running testIsTrue...");
        
        Assert.isTrue(true, "isTrue");
        
        try
        {
            Assert.isTrue(false, "isTrue");
            fail("testIsTrue");
        }
        catch (AssertionException e)
        {
            logger_.info("Passed: " + e.getMessage());            
        }
    }
    
    /**
     * Tests notNull()
     */
    public void testNotNull() 
    {
        logger_.info("Running testNotNull...");
        
        Assert.notNull("i am not null", "Object is not null");
        
        try
        {
            Assert.notNull(null, "Obiect is null");
            fail("testNotNull");       
        }
        catch (AssertionException e)
        {
            logger_.info("Passed: " + e.getMessage());
        }
    }
}