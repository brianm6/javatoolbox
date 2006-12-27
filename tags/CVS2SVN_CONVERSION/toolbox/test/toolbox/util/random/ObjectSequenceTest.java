package toolbox.util.random;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.CollectionUtil;
import toolbox.util.ElapsedTime;
import toolbox.util.RandomUtil;

/**
 * Unit test for {@link toolbox.util.random.ObjectSequence}.
 */
public class ObjectSequenceTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(ObjectSequenceTest.class);
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args)
    {
        TestRunner.run(ObjectSequenceTest.class);
    }

    //--------------------------------------------------------------------------
    // Non-Repeating Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests nextValue(1, 1, nonrepeating)
     */
    public void testNextValueNonRepeatingOne() throws Exception
    {
        logger_.info("Running testNextValueNonRepeatingOne...");
        
        String expected = "singleton";
        List data = Collections.singletonList(expected);
        ObjectSequence gen = new ObjectSequence(data, true);
        List results = new ArrayList();
        
        while (gen.hasMore())
        {
            Object next = gen.nextValue();
            logger_.debug("next = " + next);
            results.add(next);
        }
        
        logger_.debug("Results: " + results);
        
        assertEquals(1, results.size());
        assertEquals(expected, results.get(0));
    }

    
    /**
     * Tests nextValue(low, high, nonrepeating) where the range is a small
     * interval. 
     */
    public void testNextValueNonRepeatingSome() throws Exception
    {
        logger_.info("Running testNextValueNonRepeatingSome...");

        List data = new ArrayList();
        for (int i = 0; i < 20; i++)
            data.add(RandomUtil.nextString(10));
        
        ObjectSequence sequence = new ObjectSequence(data, true);
        List results = new ArrayList();
        
        while (sequence.hasMore())
        {
            Object next = sequence.nextValue();
            logger_.debug("next = " + next);
            results.add(next);
        }
        
        assertEquals(data.size(), results.size());
        assertTrue(CollectionUtil.difference(data, results).isEmpty());
    }

    
    /**
     * Tests nextValue(low, high, nonrepeating) where the range is a large
     * interval.
     * 
     * TODO: This method needs serious optimization!
     */
    public void testNextValueNonRepeatingMany() throws Exception
    {
        logger_.info("Running testNextValueNonRepeatingMany...");

        List data = new ArrayList();
        for (int i = 0; i < 999; i++)
            data.add(RandomUtil.nextString(10));

        ObjectSequence sequence = new ObjectSequence(data, true);
        List results = new ArrayList();
        
        ElapsedTime et = new ElapsedTime();
        
        while (sequence.hasMore())
        {
            Object next = sequence.nextValue();
            results.add(next);
        }
        
        et.setEndTime();
        logger_.debug("Elapsed time = " + et);
        
        assertEquals(data.size(), results.size());
        assertTrue(CollectionUtil.difference(data, results).isEmpty());        
    }    

    
    /**
     * Tests hasMore() for a non-repeating sequence.
     */
    public void testHasMoreNonRepeating() throws Exception
    {
        logger_.info("Running testHasMoreNonRepeating...");

        ObjectSequence sequence = 
            new ObjectSequence(new String[] {"one"}, true);

        assertTrue(sequence.hasMore());
        sequence.nextValue();
        assertFalse(sequence.hasMore());
        
        sequence = 
            new ObjectSequence(new String[] {"one", "two", "three"}, true);

        for(int i = 0; i < 3; i++)
        {
            assertTrue(sequence.hasMore());
            sequence.nextValue();
        }
        
        assertFalse(sequence.hasMore());
    }
    
    //--------------------------------------------------------------------------
    // Negative Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Test for empty list.
     */
    public void testConstructorEmptyList() throws Exception
    {
        logger_.debug("Running testConstructorEmptyList...");
        
        try
        {
            new ObjectSequence(new String[0], true);
        }
        catch (IllegalArgumentException iae)
        {
            // Success
            logger_.debug("SUCCESS: " + iae.getMessage());
        }
    }
}