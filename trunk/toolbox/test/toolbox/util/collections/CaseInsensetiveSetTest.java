package toolbox.util.collections;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

/**
 * Unit test for {@link toolbox.util.collections.CaseInsensetiveSet}. 
 */
public class CaseInsensetiveSetTest extends TestCase
{
    private static final Logger logger_ =
        Logger.getLogger(CaseInsensetiveSetTest.class);
        
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
        TestRunner.run(CaseInsensetiveSetTest.class);
    }

    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests add() 
     */
    public void testAdd()
    {
        logger_.info("Running testAdd...");

        Set s = new CaseInsensetiveSet(new HashSet());
        s.add("abc");
        
        assertTrue(s.contains("abc"));
        assertTrue(s.contains("ABC"));
        assertTrue(s.contains("aBc"));
    }

    
    /**
     * Tests add() for duplicates.
     */
    public void testAddDuplicates()
    {
        logger_.info("Running testAddDuplicates...");

        Set s = new CaseInsensetiveSet(new HashSet());
        s.add("abc");
        assertTrue(!s.add("ABC"));
        assertTrue(!s.add("aBc"));
        assertTrue(!s.add("Abc"));
        assertTrue(!s.add("aBC"));
        assertEquals(1, s.size());
    }

    
    
    /**
     * Tests remove() 
     */
    public void testRemove()
    {
        logger_.info("Running testRemove...");
        
        // Remove identical
        Set s = new CaseInsensetiveSet(new HashSet());
        s.add("abc");
        s.remove("abc");
        assertTrue(s.isEmpty());
        
        // Remove mixed case
        s = new CaseInsensetiveSet(new HashSet());
        s.add("abc");
        s.remove("ABC");
        assertTrue(s.isEmpty());
        
        // Remove no match
        s = new CaseInsensetiveSet(new HashSet());
        s.add("abc");
        s.remove("xyz");
        assertTrue(!s.isEmpty());
    }

    
    /**
     * Tests equals() 
     */
    public void testEquals()
    {
        logger_.info("Running testEquals...");
        
        // Self
        Set self = new CaseInsensetiveSet(new HashSet());
        assertEquals(self, self);
        
        // [x] = [x]
        Set a = new CaseInsensetiveSet(new HashSet());
        a.add("a");
        Set b = new CaseInsensetiveSet(new HashSet());
        b.add("a");
        assertEquals(a, b);
        
        // [y] = [Y]
        a = new CaseInsensetiveSet(new HashSet());
        a.add("y");
        b = new CaseInsensetiveSet(new HashSet());
        b.add("Y");
        assertEquals(a, b);
        
        // [a,b] = [B,A]
        a = new CaseInsensetiveSet(new HashSet());
        a.add("a");
        a.add("b");
        b = new CaseInsensetiveSet(new HashSet());
        b.add("A");
        b.add("B");
        assertEquals(a, b);
    }

    
    /**
     * Tests not equals() 
     */
    public void testEqualsNot()
    {
        logger_.info("Running testEqualsNot...");
        
        // [x] != [y]
        Set a = new CaseInsensetiveSet(new HashSet());
        a.add("x");
        Set b = new CaseInsensetiveSet(new HashSet());
        b.add("y");
        assertTrue(a != b);
        
        // [x,Y] != [x,Y,z]
        a = new CaseInsensetiveSet(new HashSet());
        a.add("x");
        a.add("Y");
        b = new CaseInsensetiveSet(new HashSet());
        b.add("x");
        b.add("Y");
        b.add("z");
        assertTrue(a != b);
    }
}