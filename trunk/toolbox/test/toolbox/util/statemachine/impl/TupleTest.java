package toolbox.util.statemachine.impl;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

import toolbox.util.statemachine.State;
import toolbox.util.statemachine.StateMachineFactory;
import toolbox.util.statemachine.Transition;

/**
 * Unit test for {@link toolbox.util.statemachine.impl.Tuple}.
 */
public class TupleTest extends TestCase
{
    private static final Logger logger_ = Logger.getLogger(TupleTest.class);
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args)
    {
        TestRunner.run(TupleTest.class);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests equals()
     */
    public void testEquals()
    {
        logger_.info("Running testEquals...");
        
        State s1 = StateMachineFactory.createState("s1");
        State s2 = StateMachineFactory.createState("s2");
        
        State s1Dupe = StateMachineFactory.createState("s1");
        State s2Dupe = StateMachineFactory.createState("s2");
        
        Transition t1 = StateMachineFactory.createTransition("t1");
        Transition t2 = StateMachineFactory.createTransition("t2");
        Transition t1Dupe = StateMachineFactory.createTransition("t1");
        
        Tuple tup1 = new Tuple(s1, t1, s2);
        Tuple tup2 = new Tuple(s1, t1, s2);
        Tuple tup4 = tup1;
        
        // Self
        assertTrue(tup1.equals(tup1));
        
        // Equals self
        assertTrue(tup1.equals(tup2));
        assertTrue(tup2.equals(tup1));

        // Equals dupe
        assertTrue(tup1.equals(new Tuple(s1Dupe, t1Dupe, s2Dupe)));
        
        // Not equals
        assertFalse(tup1.equals(new Tuple(s2, t1, s1)));
        assertFalse(tup1.equals(new Tuple(s1, t2, s2)));
        assertFalse(tup1.equals(new Tuple(s2, t2, s1)));
        assertFalse(tup1.equals(new Tuple(s1, t1, s1)));
        assertFalse(tup1.equals(new Tuple(s2, t1, s2)));
        
        // Reference
        assertTrue(tup1.equals(tup4));
        assertTrue(tup4.equals(tup1));
    }
}