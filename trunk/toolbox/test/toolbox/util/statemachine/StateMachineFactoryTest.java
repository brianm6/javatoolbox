package toolbox.util.statemachine;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;

/**
 * Unit test for StateMachineFactory.
 * 
 * @see toolbox.util.statemachine.StateMachineFactory
 */
public class StateMachineFactoryTest extends TestCase
{
    private static final Logger logger_ = 
        Logger.getLogger(StateMachineFactoryTest.class);
    
    //--------------------------------------------------------------------------
    // Main
    //--------------------------------------------------------------------------
    
    public static void main(String[] args)
    {
        TestRunner.run(StateMachineFactoryTest.class);
    }
    
    //--------------------------------------------------------------------------
    // Unit Tests
    //--------------------------------------------------------------------------
    
    /**
     * Tests creation of state.
     */
    public void testCreateState()
    {
        logger_.info("Running testCreateState...");
        
        State s = StateMachineFactory.createState("");
        assertEquals("", s.getName());
        
        State s2 = StateMachineFactory.createState("someState");
        assertEquals("someState", s2.getName());
    }

    
    /**
     * Tests creation of statemachines.
     */
    public void testCreateStateMachine()
    {
        logger_.info("Running testCreateStateMachine...");
        
        StateMachine s = StateMachineFactory.createStateMachine("");
        assertEquals("", s.getName());
        
        StateMachine s2 = StateMachineFactory.createStateMachine("machine");
        assertEquals("machine", s2.getName());
    }

    
    /**
     * Tests creation of transitions.
     */
    public void testCreateTransition()
    {
        logger_.info("Running testCreateTransition...");
        
        Transition s = StateMachineFactory.createTransition("");
        assertEquals("", s.getName());
        
        Transition s2 = StateMachineFactory.createTransition("someTransition");
        assertEquals("someTransition", s2.getName());
    }
}
