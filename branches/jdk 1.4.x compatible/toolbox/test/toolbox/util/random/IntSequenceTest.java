package toolbox.util.random;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.ElapsedTime;

/**
 * Unit test for {@link toolbox.util.random.IntSequence}.
 */
public class IntSequenceTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(IntSequenceTest.class);
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args)
    {
        TestRunner.run(IntSequenceTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests the default constructor.
     */
    public void testDefaultConstructor()
    {
        logger_.info("Running testDefaultConstructor...");
        
        IntSequence i = new IntSequence();
        
        for (int j = 0; j < 1000; j++)
        {
            int k = i.nextInt();
            assertTrue(k >= 0);
            assertTrue(k <= Integer.MAX_VALUE - 1);
        }
    }
    
    //--------------------------------------------------------------------------
    // Non-Repeating Sequence Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests nextValue(1, 1, nonrepeating)
     */
    public void testNextValueNonRepeatingOne() throws Exception
    {
        logger_.info("Running testNextValueNonRepeatingOne...");
        
        IntSequence gen = new IntSequence(1, 1, true);
        List nums = new ArrayList();
        
        while (gen.hasMore())
        {
            Object next = gen.nextValue();
            logger_.debug("next = " + next);
            nums.add(next);
        }
        
        logger_.debug("Numbers: " + nums);
        
        assertEquals(1, nums.size());
        assertEquals(new Integer(1), nums.get(0));
    }

    
    /**
     * Tests nextValue(low, high, nonrepeating) where the range is a small
     * interval. 
     */
    public void testNextValueNonRepeatingSome() throws Exception
    {
        logger_.info("Running testNextValueNonRepeatingSome...");
        
        int low = 1;
        int high = 20;
        int size = high - low + 1;
        
        IntSequence gen = new IntSequence(low, high, true);
        List nums = new ArrayList();
        
        while (gen.hasMore())
        {
            Object next = gen.nextValue();
            logger_.debug("next = " + next);
            nums.add(next);
        }
        
        assertEquals(size, nums.size());
        
        for (int i = low; i <= high; i++)
            nums.remove(new Integer(i));
        
        assertTrue(nums.isEmpty());
    }

    
    /**
     * Tests nextValue(low, high, nonrepeating) where the range is a large
     * interval.
     */
    public void testNextValueNonRepeatingMany() throws Exception
    {
        logger_.info("Running testNextValueNonRepeatingMany...");
        
        int low = 1000;
        int high = 9999;
        int size = high - low + 1;
        
        IntSequence gen = new IntSequence(low, high, true);
        List nums = new ArrayList();
        
        ElapsedTime et = new ElapsedTime();
        
        while (gen.hasMore())
        {
            Object next = gen.nextValue();
            nums.add(next);
        }
        
        et.setEndTime();
        logger_.debug("Elapsed time = " + et);
        
        assertEquals(size, nums.size());
        
        for (int i = low; i <= high; i++)
            nums.remove(new Integer(i));
        
        assertTrue(nums.isEmpty());
    }    

    
    /**
     * Tests hasMore() for a non-repeating sequence.
     */
    public void testHasMoreNonRepeating() throws Exception
    {
        logger_.info("Running testHasMoreNonRepeating...");

        IntSequence sequence = new IntSequence(1, 1, true);

        assertTrue(sequence.hasMore());
        sequence.nextValue();
        assertFalse(sequence.hasMore());
        sequence = new IntSequence(1, 10, true);

        for(int i = 0; i < 10; i++)
        {
            assertTrue(sequence.hasMore());
            sequence.nextValue();
        }
        
        assertFalse(sequence.hasMore());
    }

    
    /**
     * Tests for throwing of SequenceEndedException.
     */
    public void testNextValueNonRepeatingEnded() throws Exception
    {
        logger_.info("Running testNextValueNonRepeatingEnded...");
        
        IntSequence gen = new IntSequence(1, 1, true);
        gen.nextValue();
        
        try
        {
            gen.nextValue();
        }
        catch (SequenceEndedException see)
        {
            logger_.debug("SUCCESS: " + see.getMessage());
        }
    }
    
    //--------------------------------------------------------------------------
    // Repeating Sequence Tests
    //--------------------------------------------------------------------------

    /**
     * Tests nextValue(1, 1, repeating)
     */
    public void testNextValueRepeatingOne() throws Exception
    {
        logger_.info("Running testNextValueRepeatingOne...");

        int numIterations = 100;
        IntSequence gen = new IntSequence(1, 1, false);
        
        for (int i = 0; i < numIterations; i++)
            assertEquals(new Integer(1), (Integer) gen.nextValue());
    }

    
    /**
     * Tests nextValue(low, high, repeating)
     */
    public void testNextValueRepeating() throws Exception
    {
        logger_.info("Running testNextValueRepeating...");
        
        int low = 0;
        int high = 30;
        int numIterations = 1000;
        
        IntSequence gen = new IntSequence(low, high, false);
        
        for (int j = 0; j <= numIterations; j++)
        {
            Object next = gen.nextValue();
            //logger_.debug("next = " + next);
            int i = ((Integer) next).intValue();
            assertTrue(i >= low && i <= high);
        }
    }

    
    /**
     * Tests hasMore(x, y, repeating) should always return true.
     */
    public void testHasMoreRepeating() throws Exception
    {
        logger_.info("Running testHasMoreRepeating...");

        int numIterations = 100;
        IntSequence sequence = new IntSequence(1, 100, false);
        
        for (int i = 0; i < numIterations; i++)
            assertTrue(sequence.hasMore());
    }
    
    //--------------------------------------------------------------------------
    // Bounds Tests
    //--------------------------------------------------------------------------
    
    /**
     * Test invalid bounds.
     */
    public void testConstructorInvalidBounds() throws Exception
    {
        logger_.debug("Running testConstructorInvalidBounds...");
        
        int low = 5;
        int high = 4;

        try
        {
            new IntSequence(low, high, true);
        }
        catch (IllegalArgumentException iae)
        {
            // Success
            logger_.debug("SUCCESS: " + iae.getMessage());
        }
    }
    
    
    /**
     * Tests a negative integer range. 
     */
    public void testNegativeRange()
    {
        logger_.info("Running testNegativeRange...");
        
        IntSequence neg = new IntSequence(-50, 50, true);
        
        while (neg.hasMore())
            logger_.debug("Neg: " + neg.nextInt());
    }
    
    //--------------------------------------------------------------------------
    // Misc Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests getSize(). 
     */
    public void testGetSize()
    {
        logger_.info("Running testGetSize...");
        
        assertEquals(1, (new IntSequence(1, 1, true)).getSize());
        assertEquals(101, (new IntSequence(100, 200, true)).getSize());
    }

    
    /**
     * Tests toString(). 
     */
    public void testToString()
    {
        logger_.info("Running testToString...");
        
        assertEquals("[1..1]", (new IntSequence(1, 1, true)).toString());
        assertEquals("[100..200]", (new IntSequence(100, 200, true)).toString());
    }
}