package toolbox.util.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.MathUtil;

/**
 * MathUtil test class
 */
public class MathUtilTest extends TestCase
{
    /** Logger */
    private static final Logger logger_ = 
        Logger.getLogger(MathUtilTest.class);
    
    /**
     * Starts the test case and runs the entire suite.
     *
     * @param  args  None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(MathUtilTest.class);
    }
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * MathUtilTest constructor comment.
     * 
     * @param aName String
     */
    public MathUtilTest(String aName)
    {
        super(aName);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
        
    /**
     * Tests isEven()
     * 
     * @throws  Exception on error
     */
    public void testIsEven() throws Exception
    {
        logger_.info("Running testIsEven...");
        
        assertTrue("0 is even!", MathUtil.isEven(0));
        assertTrue("1 is not even!", !MathUtil.isEven(1));
        assertTrue("-1 is not even!", !MathUtil.isEven(-1));
        assertTrue("2 is even!", MathUtil.isEven(2));
        assertTrue("999 is not even!", !MathUtil.isEven(999));
        assertTrue("-1000 is even!", MathUtil.isEven(-1000));
    }

    
    /**
     * Tests isOdd()
     * 
     * @throws  Exception on error
     */
    public void testIsOdd() throws Exception
    {
        logger_.info("Running testIsOdd...");
        
        assertTrue("0 is not odd!", !MathUtil.isOdd(0));
        assertTrue("1 is odd!", MathUtil.isOdd(1));
        assertTrue("-1 is odd!", MathUtil.isOdd(-1));
        assertTrue("2 is not odd!", !MathUtil.isOdd(2));
        assertTrue("999 is odd!", MathUtil.isOdd(999));
        assertTrue("-1000 is not odd!", !MathUtil.isOdd(-1000));
    }
    
    
    /**
     * Tests addToAll()
     */
    public void testAddToAll()
    {
        logger_.info("Running testAddToAll...");
        
        int[] n = new int[] { 1, 2, 3};
        int x = 10;
        MathUtil.addToAll(n, x);
        
        assertEquals(n[0], 1 + x);
        assertEquals(n[1], 2 + x);
        assertEquals(n[2], 3 + x);
    }
    
    
    /**
     * Tests sum()
     */
    public void testSum()
    {
        logger_.info("Running testSum...");
        
        int[] n = new int[] { 1, 2, 3};
        int sum = MathUtil.sum(n);
        assertEquals(1 + 2 + 3, sum);
    }
    
    
    /**
     * Tests invNormalCumDist() 
     */
    public void testInvNormalCumDist()
    {
        logger_.info("Running testInvNormalCumDist...");
        
        logger_.info("Inverse normal cumulative distribution(0.5): " + 
            MathUtil.invNormalCumDist(0.5));
            
        logger_.info("Inverse normal cumulative distribution(0.75 " + 
            MathUtil.invNormalCumDist(0.75));
    }
}