package toolbox.util.test;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import toolbox.util.CollectionUtil;

import junit.framework.TestCase;
import junit.textui.TestRunner;

/**
 * Unit test for CollectionUtil 
 */
public class CollectionUtilTest extends TestCase
{

    /**
     * Entrypoint
     *
     * @param  args  None recognized
     */
    public static void main(String[] args)
    {
        TestRunner.run(CollectionUtilTest.class);
    }


    /**
     * Constructor for CollectionUtilTest.
     * 
     * @param  arg0  Name
     */
    public CollectionUtilTest(String arg0)
    {
        super(arg0);
    }


    /**
     * Tests union()
     */
    public void testUnion()
    {
        Set s1 = new HashSet();
        s1.add("one");
        
        Set s2 = new HashSet();
        s2.add("two");

        Collection union = CollectionUtil.union(s1, s2);
        
        assertTrue(union.size() == 2);
        assertTrue(union.contains("one"));
        assertTrue(union.contains("two"));
    }
    
    
    /**
     * Tests intersection()
     */
    public void testIntersection()
    {
        Set s1 = new HashSet();
        s1.add("one");
        s1.add("two");
        
        Set s2 = new HashSet();
        s2.add("two");
        s2.add("three");

        Collection intersection = CollectionUtil.intersection(s1, s2);
        
        assertTrue(intersection.size() == 1);
        assertTrue(intersection.contains("two"));
    }
    
    
    /**
     * Tests difference()
     */
    public void testDifference()
    {
        Set s1 = new HashSet();
        s1.add("one");
        s1.add("two");
        
        Set s2 = new HashSet();
        s2.add("two");
        s2.add("three");
        
        Collection difference = CollectionUtil.difference(s1, s2);
        
        assertTrue(difference.size() == 1);
        assertTrue(difference.contains("one"));
    }
    

    /**
     * Tests isSubset()
     */
    public void testIsSubset()
    {
        Set s1 = new HashSet();
        s1.add("one");
        s1.add("two");
        
        Set s2 = new HashSet();
        s2.add("two");
        
        boolean subset = CollectionUtil.isSubset(s1, s2);
        assertTrue(subset);
        
        s2.add("one");
        subset = CollectionUtil.isSubset(s1, s2);
        assertTrue(subset);        
        
        s2.add("three");
        subset = CollectionUtil.isSubset(s1, s2);        
        assertTrue(!subset);
    }
}