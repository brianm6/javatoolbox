package toolbox.util.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import org.apache.log4j.Logger;

import toolbox.util.RandomUtil;
import toolbox.util.StringUtil;
import toolbox.util.io.WrappingWriter;

/**
 * Unit test for RandomUtil
 */
public class RandomUtilTest extends TestCase
{
    /** Logger **/
    private static final Logger logger_ = 
        Logger.getLogger(RandomUtilTest.class);


    /**
     * Entry point
     * 
     * @param  args  Args
     */
    public static void main(String[] args)
    {
        TestRunner.run(RandomUtilTest.class);
    }

    
    /**
     * Constructor for RandomUtilTest
     * 
     * @param  arg0  Name
     */
    public RandomUtilTest(String arg0)
    {
        super(arg0);
    }

    
    /**
     * Tests nextAlpha()
     */
    public void testNextAlpha() 
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
            logger_.debug("Random alphas:\n" + 
                StringUtil.wrap(sb.toString(), true));
        }
    }

    
    /**
     * Tests nextUpperAlpha()
     */
    public void testNextUpperAlpha() 
    {
        StringBuffer sb = new StringBuffer();
        
        try
        {
            for(int i=0; i<100; i++)
            {
                char c = RandomUtil.nextUpperAlpha();
                sb.append(c);
                assertTrue(c + " should be an alpha", Character.isLetter(c));
                assertTrue(c + " should be uppercase", 
                    Character.isUpperCase(c));
            }
        }
        finally
        {
            logger_.debug("Random upper alphas:\n " + 
                StringUtil.wrap(sb.toString(),true));
        }
    }


    /**
     * Tests nextLowerAlpha()
     */
    public void testNextLowerAlpha() 
    {
        StringBuffer sb = new StringBuffer();
        
        try
        {
            for(int i=0; i<100; i++)
            {
                char c = RandomUtil.nextLowerAlpha();
                sb.append(c);
                assertTrue(c + " should be an alpha", Character.isLetter(c));
                assertTrue(c + " should be lowercase", 
                    Character.isLowerCase(c));
            }
        }
        finally
        {
            logger_.debug("Random lower alphas:\n " + 
                StringUtil.wrap(sb.toString(), true));
        }
    }       

    
    /**
     * Tests nextInt() with no upper boundary
     */
    public void testNextInt()
    {
        StringBuffer sb = new StringBuffer();
        
        for (int j=0; j<300; j++)
        {
            int i = RandomUtil.nextInt();    
            sb.append(i + " ");       
            assertTrue("int should be >= 0", i >= 0);            
        }
        
        logger_.info("\n" + StringUtil.wrap(sb.toString(), true));
    }
    
    
    /**
     * Tests nextInt() with a ceiling boundary
     */
    public void testNextIntCeiling()
    {
        StringBuffer sb = new StringBuffer();
        int ceiling = 500;
        boolean maxHit = false;
        boolean minHit = false;
        
        while (!maxHit || !minHit)
        {
            int i = RandomUtil.nextInt(ceiling); 
            
            if (i == ceiling)
            {
                sb.append("**");
                maxHit = true;
            }
    
            if (i == 0)
            {
                sb.append("**");
                minHit = true;                
            }
            
            sb.append(i + " ");       
            assertTrue("int should be 0..100", i <= ceiling);            
            
        }
        
        logger_.info("\n" + StringUtil.wrap(sb.toString(), true));
    }
    
    
    /**
     * Tests nextInt() with a small ceiling boundary
     */
    public void testNextIntCeilingSmall()
    {
        StringBuffer sb = new StringBuffer();
        int ceiling = 1;
        boolean maxHit = false;
        boolean minHit = false;
        
        while (!maxHit || !minHit)
        {
            int i = RandomUtil.nextInt(ceiling); 
            
            if (i == ceiling)
            {
                sb.append("**");
                maxHit = true;
            }
    
            if (i == 0)
            {
                sb.append("**");
                minHit = true;                
            }
            
            sb.append(i + " ");       
            assertTrue("int should be in range", i <= ceiling);            
            
        }
        
        logger_.info("\n" + StringUtil.wrap(sb.toString(), true));
    }


    /**
     * Tests nextInt() with a floor and ceiling boundary
     */
    public void testNextIntFloorCeiling()
    {
        StringBuffer sb = new StringBuffer();
        int floor   = 100;
        int ceiling = 200;
        boolean maxHit = false;
        boolean minHit = false;
        
        while (!maxHit || !minHit)
        {
            int i = RandomUtil.nextInt(floor, ceiling); 
            
            if (i == ceiling)
            {
                sb.append("**");
                maxHit = true;
            }
    
            if (i == floor)
            {
                sb.append("*");
                minHit = true;                
            }
            
            sb.append(i + " ");       
            assertTrue("int should be greater than floor", i>= floor);
            assertTrue("int should be less than ceiling", i<=ceiling);            
        }
        
        logger_.info("\n" + StringUtil.wrap(sb.toString(), true));
    }
   
    
    /**
     * Tests nextInt() with a floor and ceiling boundary span of only 1
     */
    public void testNextIntFloorCeilingOne()
    {
        StringBuffer sb = new StringBuffer();
        int floor   = 101;
        int ceiling = 101;
        boolean maxHit = false;
        boolean minHit = false;
        
        while (!maxHit || !minHit)
        {
            int i = RandomUtil.nextInt(floor, ceiling); 
            
            if (i == ceiling)
            {
                sb.append("*");
                maxHit = true;
            }
            
            sb.append(i);       
            
            if (i == floor)
            {
                sb.append("*");
                minHit = true;                
            }
            
            assertTrue("int should be greater than floor", i>= floor);
            assertTrue("int should be less than ceiling", i<=ceiling);            
        }
        
        logger_.info("\n" + StringUtil.wrap(sb.toString(), true));
    }

    
    /**
     * Tests nextBoolean()
     */
    public void testNextBoolean()
    {
        for (int i=0; i<10; i++)
            logger_.info(RandomUtil.nextBoolean()+"");
    }    
}

