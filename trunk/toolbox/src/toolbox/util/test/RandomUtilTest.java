package toolbox.util.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import org.apache.log4j.Category;
import toolbox.util.RandomUtil;

/**
 * Unit test for RandomUtil
 */
public class RandomUtilTest extends TestCase
{
    /** Logger **/
    private static final Category logger_ = 
        Category.getInstance(RandomUtilTest.class);
    
    /**
     * Constructor for RandomUtilTest
     */
    public RandomUtilTest(String arg0)
    {
        super(arg0);
    }

    /**
     * Entry point
     */
    public static void main(String[] args)
    {
        TestRunner.run(RandomUtilTest.class);
    }
    
    /**
     * Tests nextAlpha()
     */
    public static void testNextAlpha() throws Exception
    {
        StringBuffer sb = new StringBuffer();
        
        try
        {
            for(int i=0; i<100; i++)
            {
                char c = RandomUtil.nextAlpha();
                sb.append(c);
                assertTrue(c + " is not an alpha", Character.isLetter(c));
            }
        }
        finally
        {
            logger_.debug("Random alphas: " + sb.toString());
        }
    }
    
    /**
     * Tests nextUpperAlpha()
     */
    public static void testNextUpperAlpha() throws Exception
    {
        StringBuffer sb = new StringBuffer();
        
        try
        {
            for(int i=0; i<100; i++)
            {
                char c = RandomUtil.nextUpperAlpha();
                sb.append(c);
                assertTrue(c + " should be an alpha", Character.isLetter(c));
                assertTrue(c + " should be uppercase", Character.isUpperCase(c));
            }
        }
        finally
        {
            logger_.debug("Random upper alphas: " + sb.toString());
        }
    }

    /**
     * Tests nextLowerAlpha()
     */
    public static void testNextLowerAlpha() throws Exception
    {
        StringBuffer sb = new StringBuffer();
        
        try
        {
            for(int i=0; i<100; i++)
            {
                char c = RandomUtil.nextLowerAlpha();
                sb.append(c);
                assertTrue(c + " should be an alpha", Character.isLetter(c));
                assertTrue(c + " should be lowercase", Character.isLowerCase(c));
            }
        }
        finally
        {
            logger_.debug("Random lower alphas: " + sb);
        }
    }       
}

