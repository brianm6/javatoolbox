package toolbox.util.random;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

/**
 * Unit test for {@link toolbox.util.random.RandomSequenceFactory}.
 */
public class RandomSequenceFactoryTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(RandomSequenceFactoryTest.class);
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args)
    {
        TestRunner.run(RandomSequenceFactoryTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Class under test for RandomSequence create(int, int, boolean)
     */
    public void testCreateIntSequence()
    {
        logger_.info("Running testCreateIntSequence...");
        
        RandomSequence sequence = RandomSequenceFactory.create(10, 20, true);
        assertTrue(sequence instanceof IntSequence);
    }


    /**
     * Class under test for RandomSequence create(List, boolean)
     */
    public void testCreateObjectSequenceList()
    {
        logger_.info("Running testCreateObjectSequenceList...");
        
        List objects = new ArrayList();
        objects.add("a");
        objects.add("b");
        objects.add("c");
        RandomSequence sequence = RandomSequenceFactory.create(objects, true);
        assertTrue(sequence instanceof ObjectSequence);
    }


    /**
     * Class under test for RandomSequence create(Object[], boolean)
     */
    public void testCreateObjectSequenceArray()
    {
        logger_.info("Running testCreateObjectSequenceArray...");
        
        Object[] objects = new Object[] {"a", "b", "c"};
        RandomSequence sequence = RandomSequenceFactory.create(objects, true);
        assertTrue(sequence instanceof ObjectSequence);
    }
}
