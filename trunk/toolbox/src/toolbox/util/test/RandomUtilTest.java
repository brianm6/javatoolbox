package toolbox.util.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.RandomUtil;
import toolbox.util.StringUtil;

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

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
        
    /**
     * Constructor for RandomUtilTest
     * 
     * @param  arg0  Name
     */
    public RandomUtilTest(String arg0)
    {
        super(arg0);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
        
    /**
     * Tests nextAlpha()
     */
    public void testNextAlpha() 
    {
        logger_.info("Running testNextAlpha...");
        
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
        logger_.info("Running testNextUpperAlpha...");
        
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
        logger_.info("Running testNextLowerAlpha...");
        
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
        logger_.info("Running testNextInt...");
        
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
        logger_.info("Running testNextIntCeiling...");
        
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
        logger_.info("Running testNextIntCeilingSmall...");
        
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
        logger_.info("Running testNextIntFloorCeiling...");
       
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
        logger_.info("Running testNextIntFloorCeilingOne...");
        
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
        logger_.info("Running testNextBoolean...");
        
        for (int i=0; i<10; i++)
            logger_.info(RandomUtil.nextBoolean()+"");
    }    
    
    
    /**
     * Tests nextElement() for an empty array
     */
    public void testNextElementArrayEmpty()
    {
        logger_.info("Running testNextElementArrayEmpty...");
        
        assertNull(RandomUtil.nextElement(new String[0]));
    }

    
    /**
     * Tests nextElement() for a single element array
     */
    public void testNextElementArrayOne()
    {
        logger_.info("Running testNextElementArrayOne...");
        
        assertEquals("one", (RandomUtil.nextElement(new String[] { "one" } )));
    }


    /**
     * Tests nextElement() for an array length > 1
     */
    public void testNextElementArrayMany()
    {
        logger_.info("Running testNextElementArrayMany...");
        
        String[] s = new String[] { "zero", "one", "two", "three", "four", "five" };
        
        StringBuffer  sb = new StringBuffer();
        
        for (int i=0; i<50; i++)
            sb.append(RandomUtil.nextElement(s) + " ");
        
        logger_.info("\n" + StringUtil.wrap(sb.toString(), true));
    }

    /**
     * Tests nextElement() for an empty list
     */
    public void testNextElementListEmpty()
    {
        logger_.info("Running testNextElementListEmpty...");
        
        assertNull(RandomUtil.nextElement(new ArrayList()));
    }

    
    /**
     * Tests nextElement() for a single element list
     */
    public void testNextElementListOne()
    {
        logger_.info("Running testNextElementListOne...");
        
        List one = new ArrayList();
        one.add("one");
        assertEquals("one", (RandomUtil.nextElement(one)));
    }


    /**
     * Tests nextElement() for an list length > 1
     */
    public void testNextElementListMany()
    {
        logger_.info("Running testNextElementListMany...");
        
        List many = new ArrayList();
        many.add("zero");
        many.add("one");
        many.add("two");
        many.add("three");
        many.add("four");
        many.add("five");
        
        StringBuffer  sb = new StringBuffer();
        
        for (int i=0; i<50; i++)
            sb.append(RandomUtil.nextElement(many) + " ");
        
        logger_.info("\n" + StringUtil.wrap(sb.toString(), true));
    }
}

