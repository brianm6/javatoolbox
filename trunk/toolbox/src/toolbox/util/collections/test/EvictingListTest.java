package toolbox.util.collections.test;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.collections.EvictingList;

/**
 * Unit test for EvictingList. 
 */
public class EvictingListTest extends TestCase
{
    private static final Logger logger_ =
        Logger.getLogger(EvictingListTest.class);
        
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    /**
     * Entrypoint.
     * 
     * @param args None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(EvictingListTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    public void testAddSizeZero()
    {
        logger_.info("Running testAddSizeZero...");

        try
        {
            new EvictingList(0);
            fail("Size <=0 is invalid");
        }
        catch (IllegalArgumentException iae)
        {
            assertTrue("Passed", true);
        }
    }

    public void testAddSizeOne()
    {
        logger_.info("Running testAddSizeOne...");
        
        EvictingList list = new EvictingList(1);
        
        list.add("first");
        assertEquals(list.get(0), "first");
        list.add("second");
        assertEquals(list.get(0), "second");
        assertEquals(1, list.size());
    }

    public void testAddSizeMany()
    {
        logger_.info("Running testAddSizeMany...");
        
        int size = 5;
        EvictingList list = new EvictingList(5);
        
        for (int i=1; i<=size; i++)
            list.add(i+"");
        
        for (int i=size+1; i<=size+size; i++)
            list.add(i+"");

        for (int i=0, j=size+size; i<size; i++, j--)
            assertEquals(j+"", list.get(i));
    }
}