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
    private static final Category logger_ = 
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
     * Tests equals(double)
     */
    public void testEqualsDouble() throws Exception
    {
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
    public void testEqualsFloat() throws Exception
    {
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
    public void testEqualsLong() throws Exception
    {
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
    public void testEqualsObject() throws Exception
    {
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
    public void testIsFalse() throws Exception
    {
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
    public void testIsTrue() throws Exception
    {
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
    public void testNotNull() throws Exception
    {
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