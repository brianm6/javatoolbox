package toolbox.util.random;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

/**
 * Unit test for IntSequence.
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
     * Tests nextValue(high, low, nonrepeating)
     */
    public void testNextValueNonRepeating() throws Exception
    {
        logger_.info("Running testNextValueNonRepeating...");
        
        int low = 1;
        int high = 20;
        int size = high - low + 1;
        
        IntSequence gen = new IntSequence(low, high, false);
        List nums = new ArrayList();
        
        while (gen.hasMore())
        {
            Object next = gen.nextValue();
            logger_.info("next = " + next);
            nums.add(next);
        }
        
        assertEquals(size, nums.size());
        
        for (int i = low; i <= high; i++)
            nums.remove(new Integer(i));
        
        assertTrue(nums.isEmpty());
    }


    /**
     * Tests nextValue(high, low, repeating)
     */
    public void testNextValueRepeating() throws Exception
    {
        logger_.info("Running testNextValueRepeating...");
        
        int low = 1;
        int high = 20;
        int size = high - low + 1;
        
        IntSequence gen = new IntSequence(low, high, true);
        
        for (int j = 0; j <= size; j++)
        {
            Object next = gen.nextValue();
            logger_.info("next = " + next);
            int i = ((Integer) next).intValue();
            assertTrue(i >= low && i <= high);
        }
    }
    
    
    /**
     * Test invalid bounds.
     */
    public void testConstructorInvalidBounds() throws Exception
    {
        logger_.info("Running testConstructorInvalidBounds...");
        
        int low = 5;
        int high = 4;

        try
        {
            IntSequence gen = new IntSequence(low, high, false);
        }
        catch (IllegalArgumentException iae)
        {
            // Success
            logger_.info("SUCCESS: " + iae.getMessage());
        }
    }

}
