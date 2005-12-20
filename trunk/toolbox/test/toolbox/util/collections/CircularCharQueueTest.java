package toolbox.util.collections;

import toolbox.util.collections.CircularCharQueue;
import junit.framework.TestCase;
import junit.textui.TestRunner;


public class CircularCharQueueTest extends TestCase {

    // -------------------------------------------------------------------------
    // Main 
    // -------------------------------------------------------------------------
    
    public static void main(String[] args) {
        TestRunner.run(CircularCharQueueTest.class);
    }
    
    // -------------------------------------------------------------------------
    // Unit Tests
    // -------------------------------------------------------------------------
    
    public void testSizeOne() {
        CircularCharQueue queue = new CircularCharQueue(1);
        queue.add('a');
        assertEquals("a", queue.toString());
        assertEquals(1, queue.size());
    }
    
    public void testSizeOneRollover() {
        CircularCharQueue queue = new CircularCharQueue(1);
        queue.add('a');
        queue.add('b');
        assertEquals("b", queue.toString());
        assertEquals(1, queue.size());
    }
    
    public void testSizeMany() {
        CircularCharQueue queue = new CircularCharQueue(3);
        queue.add('a');
        queue.add('b');
        queue.add('c');
        
        assertEquals("abc", queue.toString());
        assertEquals(3, queue.size());
    }
    
    public void testSizeManyRollover() {
        CircularCharQueue queue = new CircularCharQueue(3);
        queue.add('a');
        queue.add('b');
        queue.add('c');
        queue.add('d');
        assertEquals("bcd", queue.toString());
        assertEquals(3, queue.size());
    }
    
}
