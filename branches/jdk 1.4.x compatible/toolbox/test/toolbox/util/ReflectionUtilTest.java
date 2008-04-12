package toolbox.util;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

/**
 * Unit test for {@link toolbox.util.ReflectionUtil}.
 */
public class ReflectionUtilTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(ReflectionUtilTest.class);

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
        TestRunner.run(ReflectionUtilTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests invokeStatic(Class, Method)
     */
    public void testInvokeStaticClassMethod() throws Exception 
    {
        logger_.info("Running testInvokeStaticClassMethod...");
        
        Object result = 
            ReflectionUtil.invokeStatic(System.class, "currentTimeMillis");
        
        assertNotNull(result);
        assertTrue(result instanceof Long);
        assertTrue(((Long) result).longValue() > 0);
    }

    
    /**
     * Tests invokeStatic(Class, Method, Arg)
     */
    public void testInvokeStaticClassMethodArg() throws Exception 
    {
        logger_.info("Running testInvokeStaticClassMethodArg...");
        
        Object result = 
            ReflectionUtil.invokeStatic(
                System.class, "getProperty", "user.dir");
        
        assertNotNull(result);
        assertTrue(result instanceof String);
        String userDir = (String) result;
        assertTrue(userDir.length() > 0);
        assertEquals(System.getProperty("user.dir"), userDir);
    }


    /**
     * Tests invokeStatic(Class, Method, Args[])
     */
    public void testInvokeStaticClassMethodArgs() throws Exception 
    {
        logger_.info("Running testInvokeStaticClassMethodArgs...");
        
        String propName = RandomUtil.nextString(10);
        String propValue = RandomUtil.nextString(10);
        
        try 
        {
            Object result = 
                ReflectionUtil.invokeStatic(   
                    System.class, 
                    "setProperty", 
                    new String[] {propName, propValue});
            
            // Setting non-existant prop should return prev value which is null
            assertNull(result);
            
            // Verify prop got inserted ok...
            assertEquals(propValue, System.getProperty(propName));
        }
        finally
        {
            // Clear out the prop
            System.getProperties().remove(propName);
        }
    }
    
    
    /**
     * Tests invokeStatic(Class, non-existant Method)
     */
    public void testInvokeStaticBogusMethod() throws Exception 
    {
        logger_.info("Running testInvokeStaticBogusMethod...");
        
        Object result = null;
        
        try 
        {
            result = ReflectionUtil.invokeStatic(System.class, "idontexist");
            fail("An exception should have been thrown on a bogus method");
        }
        catch (IllegalArgumentException iae)
        {
            // Success
            assertNull(result);
        }
    }


    /**
     * Tests invokeStatic(Class, Method, non-matching Args[])
     */
    public void testInvokeStaticArgsDontMatch() throws Exception 
    {
        logger_.info("Running testInvokeStaticArgsDontMatch...");
        
        Object result = null;
        
        try 
        {
            result = ReflectionUtil.invokeStatic(
                System.class, "gc", new Object[] {"bogusArg"});
            
            fail("An exception should have been thrown on a bogus method");
        }
        catch (IllegalArgumentException iae)
        {
            // Success
            assertNull(result);
        }
    }
}